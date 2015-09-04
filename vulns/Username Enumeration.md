## Username Enumeration

#### Description

Username Enumeration refers to the ability of an attacker to determine which usernames are registered within the system, without having information about user passwords. Username Enumeration usually occurs when an action, such as attempting to log in or resetting your password, returns different information when an account exists with the given username and when an account does not exist with the given username. This allows an attacker to focus on only the passwords of the account during their attack.

It is debateable whether username enumeration actually presents a serious security risk. Some websites choose to make clear to the user if their username was typed in incorrectly as a matter of username experience. However, it is simple to ensure that error messages are not specific about whether your username or password was incorrect.

#### Code Snippet

src/main/java/com/nVisium/androidnv/api/MvcConfig.java

```
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
        impl.setUserDetailsService(new UserServiceImpl());
        impl.setHideUserNotFoundExceptions(false) ;
        return impl ;
    }
	}

```
src/main/webapp/WEB-INF/views/user/login.jsp

```
	<c:if test="${param.error != null}">
		<div class="alert alert-danger alert-dismissible" role="danger">
			<button type="button" class="close" data-dismiss="danger"
				aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			<strong>Error:</strong> Invalid username and/or password (${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message})
			<br />
		</div>
	</c:if>
```

#### Problem

The MoneyX developers attempted to make their login page more user friendly by specifying exactly what occurs when the user enters the incorrect information. If the user's password is incorrect, the application will display the string "Bad Credentials" on the page. However, if the username is incorrect, the application will not display the string.

#### Solution

Username Enumeration is relatively simple to protect against; simply ensure that pages do not leak information about which users are registered in the system. The most common venues of attack are the login and registration pages, or the "Forgot Password" page. Ensure that your login page makes no differentiation between an incorrect user or password. Registration pages should only display a generic fail message with information relating to user authentication. Finally, the "Forgot Password" should always specify that the email reset message was sent "if the user exists", preventing information being leaked about whether the user really existed.

![Invalid email](https://i.imgur.com/3zoKPEE.png)

![Response message does not leak information](https://i.imgur.com/NwO4dOg.png)

If you use a password hash that is purposely slow, such as bcrypt/scrypt/pbkdf2, it is important to ensure that the algorithm is still run even when a user is not found. When a login is checked and the username does not exist, you should hashing a random string so the difference in response time will not leak information that could be used to enumerate users.