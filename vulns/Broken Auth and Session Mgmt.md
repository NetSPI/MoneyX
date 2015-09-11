## Broken Authentication and Session Management

#### Description

Broken Session and Authentication Management refers to several different problems with website authentication and authorization. Session fixation, poor storage of passwords, improper session storage and transmission, or improperly implemented "forgot password" functionality all fall under this broad class of vulnerability.

Session Fixation refers to the user session token not being refreshed when they reauthenticate themselves. The most common scenario this occurs in is when session tokens are assigned to unauthenticated users, who then authenticate themselves by logging in. At that point, a new session token must be generated and given to the user. Otherwise, if an attacker is able to malicious set the user session to a known value, they will be able to predict what session token they need to use for escalated privileges.

As an aside, user sessions must also be handled correctly on logout (deauthorization). Rather than simply resetting the cookie, make sure that someone who holds onto the sesion identifier does not retain any privileges for later.

This issue can also be used to refer to poor password storage in the database. Passwords should be stored with a strong, computationally intensive hash function such as **bcrypt**, **scrypt**, or **PBKDF2**. Avoid using MD5 or SHA for password storage, and never implement a hashing function on your own or store passwords in plaintext!

Ensure that sessions are properly stored in cookies on the client, and nowhere else. Some web frameworks will allow code to fall-back on storing the session as a GET parameter to every request (usually as JSESSIONID or similar). This usually results in the session being stored in user history, and making it easier to expose the user to CSRF or other attacks. In addition, ensure that authenticate and session identifier transmission is securely handled over SSL. In actuality, there isn't much of a downside to running your entire site over SSL!

Finally, some session implementations make it easy to hijack user sessions through smaller features; "forgot password" or "change password" is a common avenue of attack. These features should be complex enough to prevent attackers from malicious resetting credentials.

#### Problem

MoneyX has several issues with its authentication and session management. Firstly, the platform is vulnerable to session fixation. As discussed above, this allows an attacker to hijack a user session without knowing their credentials. The attacker first reads the user session cookie value, which is stored on their local computer, or sets their own cookie value on the user with some known session value (ex: JSESSIONID = "ABCDEF"). Once the user authenticates, the server simply authorizes "ABCDEF" as the user token instead of assigning a new one. Afterwards, the attacker is simply able to use the value they now known as privileged.

MoneyX also stores passwords insecurely; they are stored by default in plaintext in the database. Anyone who compromises the database server will have full access to all user accounts. Furthermore, even an attacker who manages to exploit SQL Injection or a similar attack will be able to read off user credentials. The system also lacks SSL overall, so authentication is not protected when it passes over the network. Even if the passwords were encrypted, they could still be read as the user logs in.

Sessions in MoneyX are valid for a long period of time, and stay valid even when the server is restarted. This lack of a proper session timeout means that attackers who hijack user sessions have large windows to perform malicious activity.

Finally, the developers of MoneyX have implemented a custom, insecure "forgot password" feature for users who cannot remember their passwords. Rather than requiring a user to verify their email address, they simply have to enter their favorite color that they chose on registration. Unfortunately, a large number of users will answer this question with a small subset of colors! An attacker could easily bruteforce common colors (red, blue, green, yellow, black, white, etc) and log into a large percentage of user accounts.

#### Walkthrough

###### Forgot Password

1. Make sure you're logged out of the system, and then click on the "Forgot Password" link on the login page (path: ```/forgot-password```).
2. Choose the user account you want to target (could use username enumeration to determine which accounts exist!)
3. The page asks for the favorite color selected by the user. Most users will use a color from a small subset of colors (red, green, blue, yellow, pink, brown, orange, white, black, etc). Try all the colors until one matches.
4. Once you have the user's favorite color, the password can be set to any value, and you will have full access to the user account.

###### Session Fixation

1. Make sure you're logged out of the system, and then navigate to the login page (```/login```)
2. Note that MoneyX has already assigned a tracking cookie to your computer, even before you have logged in. You can view this cookie in your browser's developer console (Chrome: ```Options -> More Tools -> Developer Tools```, Firefox: ```Tools -> Web Developer -> Network```). Select the request corresponding to the login page, and you can see the value of the cookie as one of the HTTP headers.
3. Log into the application, using an existing user account.
4. After you have logged in, check the value of the cookie. The value has not changed. This means if an attacker were to steal your session cookie, even if you were not logged into the website, they would have full access to your account whenever you DID log in. Therefore, MoneyX is vulnerable to session fixation!

#### Code Snippet
(SQL Query returning raw passwords from database)

```
SELECT password FROM USERS;

PASSWORD  
seth123
cyrus123
kyle123
jamesbond33
springboot123
dave123
test
(7 rows, 4 ms)
```
src/main/resources/application.properties

```
server.session-timeout=10000
#server.ssl.key-store: keystore.p12
#server.ssl.key-store-password: mypassword
#server.ssl.keyStoreType: PKCS12
#server.ssl.keyAlias: tomcat
```

src/main/java/com/nvisium/androidnv/api/controller/UserController.java

```
/* Validate the username and answer */
if (userService.doesUserExist(username)
		&& userService.isAnswerValid(username, answer)) {
	userService.updatePasswordByUsername(username, password);
	redirectAttrs.addFlashAttribute("success",
			"Password successfully updated!");
	return "redirect:/login";
} else
	model.addAttribute("error", "true");
return "user/forgot-password";
```

#### Solution

Protecting against session fixation is easy; only trust session identifiers that have been assigned by the server, and re-issue a new session identifer once the user changes their authorization. On de-auth/logout, be sure to again re-issue a new session identifier.

By default, Spring Security provides strong protection against session fixation. However, the MoneyX developers have removed it by adding ```[http security object].sessionManagement().sessionFixation().none()``` to their Spring HTTP Security configuration. Avoid disabling Spring's protection as it will weaken your app security.

Passwords should be encrypted using **bcrypt**, a password hashing function designed to be CPU-intensive. CPU-intensive hashes are designed to prevent attackers from decrypting them even with access to the hashes; since they take so long to calculate, attacks against them quickly become infeasible. In Spring Security, ensure you are using the ```BcryptPasswordEncoder()``` by adding the following to your ```src/main/java/com/nvisium/androidnv/api/security/SecurityConfig.java```

```
@Bean
public PasswordEncoder passwordEncoder(){
	PasswordEncoder encoder = new BCryptPasswordEncoder(12);
	return encoder;
}
```

The "13" passed to the constructor indicates the number of rounds. In general, this dictates how long it takes to calculate bcrypt. Increasing this number by one causes a large increase in time needed to calculate the hash.

SSL should be used, at a minimum, for all pages that involve changing user authorization. SSL can be enabled by uncommenting the values in ```src/main/resources/application.properties``` and configuring them for your own Java Keystore. More information about Spring SSL can be found [here](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#howto-configure-ssl) and information about creating and using Java Keystores can be found [in this guide](https://www.digitalocean.com/community/tutorials/java-keytool-essentials-working-with-java-keystores).

Spring stores user sessions with whichever Java application server you use. For the embedded Tomcat instance, you can configure session timeout to be shorter than the default by updating ```server.session-timeout``` in ```src/main/resources/application.properties```, which is in minutes. This will work for an embedded Tomcat server. For deploying your application to a production server, you will need to modify your Java app server configuration (for Tomcat, check ```[tomcat install location]/conf/web.xml```) and set ```session-timeout```, in minutes, to a lower value.

Finally, you should avoid using any "forgot password" that does not rely on users validating their emails. The average user will often set simple answers to recovery questions that can easily be found online, or with more information about the target.

Even worse are questions that have only a small set of legitimate answers (including the one in MoneyX). Most users will answer the questions truthfully, since they will usually forget their answer when they need it otherwise.

Unfortunately, Spring Security does not come with built-in functionality to reset passwords via email. Instead, modify MoneyX to store user email instead of favorite color. When users pass their email in on the forgot email form, generate a long (30+ character) reset token with [SecureRandom](https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html) and email it to them (Spring has a [built in email library](http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/mail.html)). Once they click on that link, take them to a page where they are able to reset their password. Ensure that the link stops functioning after their password is reset by clearing it from the user table in the database!