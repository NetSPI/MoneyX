## Cross Site Request Forgery (CSRF)

#### Description

A Cross-site Request Forgery (CSRF) attack is one in which the user's browser is hijacking in order to submit a request to another website they are authenticated on. They occur as a result of unprotected requests that can change the state without requiring direct user input. For example, if a form/request on a bank website to add money to their bank account is not protected by CSRF, an attacker could create an unrelated page, and submit a request to that endpoint when the user loads the attacker's page. Since the user's browser has a valid session cookie, the request would occur without the user even being aware anything had happened!

CSRF attacks are sometimes thought of as being only applicable to GET requests, but POST requests work just as well. A user simply needs to be manipulated into visiting a malicious website or clicking a malicious link to fall victim to the attack.

#### Code Snippet

com.nVisium.androidnv.api.controller.PaymentController

```
	@RequestMapping(value = "/make-payment", method = {RequestMethod.GET, RequestMethod.POST})
	public String makePayment(
			@RequestParam(value = "event", required = false) Long eventId,
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			RedirectAttributes redirectAttrs,
			Model model) {
				
		 /* --- */
				
		if (!paymentService.makePayment(eventId, amount)) {
			model.addAttribute("danger", "Insufficient funds in your account!");
			List<EventMembership> memberships = eventService.getEventsByMembership(security.getCurrentUserId());
			List<Event> events = eventService.getEventsByOwner(security.getCurrentUserId());
			for (EventMembership m: memberships) {
				events.add(eventService.getEventById(m.getEventId()));
			}
			model.addAttribute("events", events);
			return "payment/make-payment";
		}
		
		/* --- */
	}

```
com.nVisium.androidnv.api.security.SecurityConfiguration

```
http.authorizeRequests()
				.antMatchers("/register/**", "/forgot-password/**", "/dist/**",
						"/console/**").permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage("/login")
				.defaultSuccessUrl("/dashboard").permitAll().and().logout()
				.permitAll().and().requestCache()
				.requestCache(new NullRequestCache()).and().csrf().disable();
```

#### Problem

In MoneyX, the ```/make-payment``` controller action accepts either a GET or a POST request. It makes no attempt to validate that the user has the proper authentication to make the request. Since the application was also configured in ```SecurityConfiguration``` to disable Spring Security's default CSRF protection, any attacker who forces an unsuspecting user's browser to navigate to ```/make-payment``` can initiate a transfer for an existing event!

#### Solution

There are several ways to prevent CSRF attacks on your application. The most straightforward method is to use a "synchronizer token" with any request that change state in your application. This involves creating a unique token and attaching it to every HTML form or AJAX call sent to the user. If the user wishes to then make a state-modifying request, they will have to attach the token they were just sent. The server verifies that the token was sent to the user, and only then accepts the request.

Spring Security is the optimal solution for preventing CSRF attacks in Spring applications. Using its built in CSRF protection, you must simply make sure that any state-modifying controller actions are limited to non-```GET``` requests, and that a CSRF token is included in any forms.

```
<form action="/make-payment" method="post">
	<!-- Rest of form elements -->
  <input type="submit" value="Make Payment" />
  
  <!-- Include Spring CSRF token -->
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
```

However, if you are not using Spring Security, it is possible to generate secure tokens yourself and store them in a server-side cache for lookup. Ensure that tokens are generated randomly, using [SecureRandom](https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html). Note that a user may have multiple tokens attached to them simultaneously. Also, avoid storing CSRF tokens in the database, as they should expire within a short amount of time. If they persist, there is a risk that an attacker who compromises a user's system may be able to make a spoofed request at a later time.

Another option to prevent CSRF attacks utilizes *double submit cookies*. Under this system, when a user authenticates to the website, they receive a second "csrf token" cookie along with their session identifier. Any time the user's browser makes a state-modifying request, it will also submit the cookie value in a request parameter, and the request will fail unless the two are equal. Since attackers are unable to modify cookies using the same origin policy, they will not be able to spoof malicious requests with this system.