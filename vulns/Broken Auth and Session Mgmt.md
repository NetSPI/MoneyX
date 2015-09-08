## Broken Authentication and Session Management

#### Description

Broken Session and Authentication Management refers to several different problems with website authentication and authorization. Session fixation, poor storage of passwords, improper session storage and transmission, or improperly implemented "forgot password" functionality all fall under this broad class of vulnerability.

Session Fixation refers to the user session token not being refreshed when they reauthenticate themselves. The most common scenario this occurs in is when session tokens are assigned to unauthenticated users, who then authenticate themselves by logging in. At that point, a new session token must be generated and given to the user. Otherwise, if an attacker is able to malicious set the user session to a known value, they will be able to predict what session token they need to use for escalated privileges.

As an aside, user sessions must also be handled correctly on logout (deauthorization). Rather than simply resetting the cookie, make sure that someone who holds onto the sesion identifier does not retain any privileges for later.

This issue can also be used to refer to poor password storage in the database. Passwords should be stored with a strong, computationally intensive hash function such as **bcrypt**, **scrypt**, or **PBKDF2**. Avoid using MD5 or SHA for password storage, and never implement a hashing function on your own or store passwords in plaintext!

Ensure that sessions are properly stored in cookies on the client, and nowhere else. Some web frameworks will allow code to fall-back on storing the session as a GET parameter to every request (usually as JSESSIONID or similar). This usually results in the session being stored in user history, and making it easier to expose the user to CSRF or other attacks. In addition, ensure that authenticate and session identifier transmission is securely handled over SSL. In actuality, there isn't much of a downside to running your entire site over SSL!

Finally, some session implementations make it easy to hijack user sessions through smaller features; "forgot password" or "change password" is a common avenue of attack. These features should be complex enough to prevent attackers from malicious resetting credentials.

#### Code Snippet
#### Code Snippet
src/main/java/com/nVisium/androidnv/api/controller/UserController.java

```
                if (!userService.doesUserExist(username)) {
                        userService.addRegularUser(username, password, email, answer,
                                        firstname, lastname);
                        redirectAttrs.addFlashAttribute("success",
                                        "Successfully registered!");
                        if (next != null) {
                                return new ModelAndView("redirect:" + next);
                        } else {
                                return new ModelAndView("redirect:/login");
                        }
                } else
```
src/main/java/com/nVisium/androidnv/api/service/impl/UserServiceImpl.java

```
        public void addRegularUser(String username, String password, String email,
                        String answer, String firstname, String lastname) {
                com.nvisium.androidnv.api.model.User user = new com.nvisium.androidnv.api.model.User();
                user.addAccountInfo(username, password, email, answer, firstname,
                                lastname);
                accountRepository.save(user);
        }
```
src/main/java/com/nVisium/androidnv/api/model/User.java

```
        public void setPassword(String password) {
                this.password = password;
        }

        [...]

        public void addAccountInfo(String username, String password, String answer,
                        String email, String firstname, String lastname) {
                this.setUsername(username);
                this.setPassword(password);
                this.setAnswer(answer);
                this.setEmail(email);
                this.setFirstname(firstname);
                this.setLastname(lastname);
                this.setAccountNonLocked(true);
                this.roles = new HashSet<Role>();
                // create a new user with basic user privileges
                roles.add(new Role("USER", this));
                this.setRoles(roles);
                this.setPrivacyEnabled(false);
                this.balance = new BigDecimal(0.0);
        }
```

```
[example of forgot password involving you saying your favorite color]

```
view/user/profile.jsp

```
<p>Link to profile: <a href="/profile/${id}">/profile/${id}</a>
```

#### Problem

[talk about above stuff]

[lack of session timeout]

[lack of ssl]

[session fixation]

#### Solution
