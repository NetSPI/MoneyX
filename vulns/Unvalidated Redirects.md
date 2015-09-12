## Unvalidated Redirects

#### Description

Unvalidated redirects occur when a web application accepts malicious user input and allows redirects to occur based on it. The malicious input is usually in the form of a URL to an attacker-controlled server. When the unvalidated redirect occurs, the base URL of the site the user is on appears to be yours and trusted, but their browser will then be redirected to the attacker's domain.

Unvalidated redirects are common after users authenticate themselves on a website. When a user first attempts to ask a protected page, they will be redirected to ```/login?next=/protected_page```. Using the ```next``` parameter, the site will take the user back to the protected page after they authenticate, creating a smooth user experience. However, if the value of the ```next``` value is not checked, then ```/login?next=http://evil.com``` will work just as well.

Unvalidated Redirects are sometimes not considered a direct vulnerability since they do not affect users of your website. However, they have the potential to affect users of any website and harm the reputation of your site. It is best to avoid allowing such redirects so malicious attackers cannot damage brand reputation by piggybacking off your users' trust.

#### Problem
URL: http://localhost:8080/register?next=/payment/make-payment

MoneyX's registration controller action takes an optional ```next``` parameter that determines where the user browser is redirected to after registration. This feature was added so the user could be redirect to different versions of the login page, or even the main page of the application, without even modifying the main registration Java code. However, there is no validation that the path passed in is part of the application. In fact, passing in ```next=http%3A%2F%2Fevil.com``` results in a malicious browser redirect!

#### Walkthrough
1. Click on the Register button.
2. Add a next parameter to the URL by copy and pasting ```?next=http%3A%2F%2Fgoogle.com``` at the end.
3. Hit return/enter in the URL bar to reload the page with the value loaded in the next parameter.
4. Enter values in each of the form fields as appropriate (make something up).
5. Click the Register button.

Note that the application successfully registers and then redirects your browser to google.com.

#### Code Snippet
src/main/java/com/nVisium/androidnv/api/controller/UserController.java
Lines 65-74
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
		}
```

#### Solution

The optimal solution for preventing unvalidated redirect attacks is to require URLs to be passed in as controller and action pairs (ex: ```(user, login)```) rather than a URL. That way, the value cannot be manipulated to point to another website. You could also do a simple regex or string search for "http://", but a dedicated attacker may find ways around this. A final absolute option is to validate that the URL is a relative URL that contains one of your website domains. The general Java [URL](https://docs.oracle.com/javase/8/docs/api/java/net/URL.html) class can be used to validate different parts of the value, whether relative or absolute.

It may be worth allowing remote websites to be accessed, but putting up a redirect landing page informing users they will be accessing untrusted resources. This will inform the user that they may possibly be exposing themselves to attack.

#### Solution Code - Implement a simple http:// regex
src/main/java/com/nVisium/androidnv/api/controller/UserController.java
Lines 65-74
```
// ---

userService.addRegularUser(username, password, email, answer,
		firstname, lastname);
redirectAttrs.addFlashAttribute("success",
		"Successfully registered!");
if (next != null && !next.startsWith("http://")) {
	return new ModelAndView("redirect:" + next);
} else {
	return new ModelAndView("redirect:/login");
}

// ---
