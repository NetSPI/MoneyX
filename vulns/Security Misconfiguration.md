## Security Misconfiguration

#### Description

Security Misconfiguration refers to insecure settings chosen for any portion of the web application stack. When the application is configured in an insecure way, it can be vulnerable to any number of other attacks, since the regular defences are disabled. Such issues can involve default pages, system level accounts that are unprotected, access to private database pages, or insecure files or directories.

Security Misconfiguration issues can occur in the web application itself, the application server, the web server, the host operating system. For this reason, this class of vulnerability can be easy to exploit. Misconfiguration often involves searching for what is missing, rather than what is purposely configured insecurely. It can be also difficult to ensure that all layers of your stack are configured securely, especially across system updates.

#### Code Snippet

com.nVisium.androidnv.api.security.SecurityConfiguration

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
application.properties

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

#### Problem

In ```SecurityConfiguration```, SpringSecurity has been configured in an extremely insecure manner. CSRF protection, which is covered in another vulnerability, has been completely disabled. The clickjacking protection has also been disabled by ```http.headers().frameOptions().disable()```.

Furthermore, the built-in H2 database console has been left unprotected and does not require system login to access. Since the database console has full access to the database, this is a significant security vulnerability.

In the ```application.properties``` file, the server has been set to bind on any IP address on the system, which could potentially expose it on an unfirewalled network interface. It also uses the default settings for SSL, which leave it completely disabled. Finally, any security headers,  which improve the security of the client browser, have been disabled.

#### Solution

In ```SecurityConfiguration```, avoid disabling features such as CSRF, even if they require you to pass tokens around. Avoid disabling security headers, such as the clickjacking protection, and instead design your application to take into account the presence of such headers.

It is best to avoid using system administration code built into the application (such as the H2 database) and instead use offline administration. However, if you are unable to remove such tools from production, instead focus on making them extremely secure. Consider requiring 2-factor authentication to access them, as well as confirmation from the user's email address.

Ensure that your application properly uses encryption technology like SSL for any sensitive pages. At a _bare_ minimum, pages that involve user authentication  or authorization, such as login or registration pages, should be protected with SSL. However, there are no downsides to using SSL for your entire application!

It's important to note that Security Misconfiguration also occurs with the use of improperly configured 3rd party application code. Make sure you carefully read all documentation for libraries used in your applications, taking special note of any comments referencing security or configuration.