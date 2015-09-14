## Security Misconfiguration

#### Description

Security Misconfiguration refers to insecure settings chosen for any portion of the web application stack. When the application is configured in an insecure way, it can be vulnerable to any number of other attacks, since the regular defences are disabled. Such issues can involve default pages, system level accounts that are unprotected, access to private database pages, or insecure files or directories.

Security Misconfiguration issues can occur in the web application itself, the application server, the web server, the host operating system. For this reason, this class of vulnerability can be easy to exploit. Misconfiguration often involves searching for what is missing, rather than what is purposely configured insecurely. It can be also difficult to ensure that all layers of your stack are configured securely, especially across system updates.

#### Problem
URL: http://localhost:8080/dashboard, http://locahost:8080/console

In ```SecurityConfiguration```, SpringSecurity has been configured in an extremely insecure manner. CSRF protection, which is covered in another vulnerability, has been completely disabled. The clickjacking protection has also been disabled by ```http.headers().frameOptions().disable()```.

Furthermore, the built-in H2 database console has been left unprotected and does not require system login to access. Since the database console has full access to the database, this is a significant security vulnerability.

In the ```application.properties``` file, the server has been set to bind on any IP address on the system, which could potentially expose it on an unfirewalled network interface. It also uses the default settings for SSL, which leave it completely disabled. Finally, any security headers,  which improve the security of the client browser, have been disabled.

#### Walkthrough

###### H2 Console

1. With your web browser, navigate to the ```/console``` path in the MoneyX application.
2. You are able to log into the H2 database, using the default credentials that are populated into the fields. From this console, you can view all data in the database, and make any changes you wish

###### SSL Configuration

1. Notice that in ```application.properties```, the application has been configured to not use SSL.
2. Using Wireshark, a web proxy, or another tool to intercept network traffic, intercept the traffic from your browser to the MoneyX application
3. Attempt to log in to the application using the default credentials
4. Notice that all traffic is sent over plaintext

###### Security Headers

1. Open the developer console in your browser, and navigate to the network tab (Menu -> More Tools -> Developer Console -> Network in Chrome, Tools -> Web Developer -> Network in Firefox)
2. Navigate to the MoneyX application
3. Notice that common web security headers, including ```X-Frame-Options```, ```X-XSS-Protection```, or ```X-Content-Type-Options``` are missing from the response headers. Without these protections, the user's browser is left in a more vulnerable state

CSRF Exploitation is covered more in depth in another writeup. Please refer to the ```Cross-Site Request Forgery``` section for more information about preventing CSRF attacks.

#### Code Snippet
src/main/java/com/nVisium/androidnv/api/security/SecurityConfiguration.java

```
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/register/**", "/forgot-password/**", "/dist/**",
						"/console/**").permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage("/login")
				.defaultSuccessUrl("/dashboard").permitAll().and().logout()
				.permitAll().and().requestCache()
				.requestCache(new NullRequestCache()).and().csrf().disable();

		http.headers().frameOptions().disable();
	}
```
src/main/resources/application.properties

```
# ...
server.port: 8080
server.address: 0.0.0.0
spring.redis.host=localhost
spring.redis.port=6379
security.require-ssl=false

security.headers.xss=false
security.headers.cache=false
security.headers.frame=false
security.headers.content-type=false
# ...
```

#### Solution

In ```SecurityConfiguration```, avoid disabling features such as CSRF, even if they require you to pass tokens around. Avoid disabling security headers, such as the clickjacking protection, and instead design your application to take into account the presence of such headers.

It is best to avoid using system administration code built into the application (such as the H2 database) and instead use offline administration. However, if you are unable to remove such tools from production, instead focus on making them extremely secure. Consider requiring 2-factor authentication to access them, as well as confirmation from the user's email address.

Ensure that your application properly uses encryption technology like SSL for any sensitive pages. At a _bare_ minimum, pages that involve user authentication  or authorization, such as login or registration pages, should be protected with SSL. However, there are no downsides to using SSL for your entire application!

It's important to note that Security Misconfiguration also occurs with the use of improperly configured 3rd party application code. Make sure you carefully read all documentation for libraries used in your applications, taking special note of any comments referencing security or configuration.

#### Solution Code

src/main/java/com/nVisium/androidnv/api/security/SecurityConfiguration.java

**Lines 32-43**: Remove ```http.headers().frameOptions().disable();``` from the HttpSecurity configuration. Also, remove ```/console/**``` from the list of allowed antMatchers. Finally, remove the code disabling CSRF protection.

You should be left with the below function after your changes.

```
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/register/**", "/forgot-password/**", "/dist/**")
				.permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage("/login")
				.defaultSuccessUrl("/dashboard").permitAll().and().logout()
				.permitAll().and().requestCache()
				.requestCache(new NullRequestCache());

	}
```

src/main/resources/application.properties

**Lines 11-14**: Uncomment the SSL configuration values, and replace them with the proper information for your own SSL configuration. You can find more information on importing your SSL certificates into Java Keystores [here](http://javarevisited.blogspot.com/2012/03/add-list-certficates-java-keystore.html)

**Line 17**: Set ```security.require-ssl``` to true after adding your SSL configuration

**Lines 19-22**: Set all security header values to true, in order to add the following headers to your HTTP configuration.

```X-XSS-Protection: 1; mode=block``` - Forces the browser to enable its built in XSS protection

```X-Frame-Options: DENY``` - Prevents clickjacking attacks by disallowing your site from being rendered in a frame. This means attackers cannot use it in a frame on their websites to fool your users

```X-Content-Type-Options: nosniff``` - Prevents certain legacy browsers from attempting to guess the page Content-Type, instead of using the given value. Attackers could leverage that sniffing to launch XSS attacks

Finally, the following would prevent the user's browser from caching your site, in case the pages contain sensitive information

```
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
```

When you've set all the values correctly, the configuration should look like this

```
... (more up here) ...
server.port: 8080
server.address: 0.0.0.0
server.session-timeout=10000
server.ssl.key-store: mykeystore.p12
server.ssl.key-store-password: mypassword
server.ssl.keyStoreType: PKCS12
server.ssl.keyAlias: tomcat
spring.redis.host=localhost
spring.redis.port=6379
security.require-ssl=true

security.headers.xss=true
security.headers.cache=true
security.headers.frame=true
security.headers.content-type=true
```
