## Cross-site Scripting (XSS)

#### Description

Cross-Site Scripting is a vulnerability that occurs when user-supplied data is unsafely rendered back in an application. When malicious data is rendered to the front end views without any escaping, the user's browser may be hijacked or commanded for malicious purposes.

XSS flaws usually rely on executing JavaScript in the user's browser, but can occur in a wide variety of settings beyond the common ```<script>``` tag. HTML is a versatile language, and JavaScript can be executed from within HTML attributes, CSS, or even from within JSON payloads.

Most common XSS attacks attempt to either steal user cookie information (containing their site session), browser information, account contents, or other sensitive information. It's also possible to force the user's browser to navigate to an attacker controlled server for further exploitation.

There are usually two broad forms of XSS: stored XSS, and reflected XSS. Reflected XSS is usually parsed from a URL or some other client-only input, and affects the user without any interaction from the server. Reflected XSS is much harder to detect, since there is no interaction with the server as the user is hijacked. On the other hand, stored XSS is usually sent as input to the server and saved in a database. Any time the page with the payload is loaded, it will replay the exploit. While easier to detect, stored XSS can have a wider reaching impact on the end user.


#### Problem
URL: http://localhost:8080/dashboard

MoneyX uses the JSP templating language to render its frontend views. However, throughout almost the entire application it does not adequately protect against XSS! Although there is no indication in the templates that the outputs are "insecure", simplying using the ```${variable}``` syntax does not perform any encoding by default. In the "wrapper.tag" template above, the value of the ```info``` parameter is printed out in raw format; if an attacker passes in a value that contains a script tag, it would be output to the page directly and treated as HTML. Similarly, the "profile" JSP shows the information being pulled from the URL and reflected back out to the user in a raw format.

#### Walkthrough
1. Open up MoneyX using the SauceLabs Firefox Browser
2. Sign in as 'user' with password 'user123'
3. Change the URL on the dashboard page and append ```?info=test<script>alert('XSS')</script>``` to the end 
4. Hit enter and an alert box pops up with 'XSS' in it.
5. Close the alert box.

#### Code Snippet
src/main/webapp/WEB-INF/tags/wrapper.tag

```
<c:if test="${not empty param.info}">
<div class="alert alert-info alert-dismissible" role="danger">
	${param.info}
</div>
</c:if>
```
src/main/webapp/WEB-INF/view/user/profile.jsp

```
<p>Link to profile: <a href="/profile/${id}">/profile/${id}</a>
```
#### Solution

XSS requires a more proactive approach to mitigate in JSP templates than in other templating languages. While some other languages encode automatically, and require you to specifically mark variables you do not wish to encode, JSP templates do not always help. Therefore, it is extremely important to be careful when outputting variables in JSP templates.

When outputting variables to a page, ensure you use the an encoder library to properly escape for XML/HTML. We can rewrite the event attributes table from above using the OWASP Java Encoder library to protect ourselves as shown below.

wrapper.tag
Line 4 - Add the Java Encoder Library
```
<%@ taglib prefix="encoder" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>
```

Lines 106-110 - Use it to encode the ```info``` parameter
```
<c:if test="${not empty param.info}">
<div class="alert alert-info alert-dismissible" role="danger">
	${encoder:forHtml(param.info)}
</div>
</c:if>
```

However, we must take care to protect against different *contexts* of XSS. Values that are inserted into a URL must be encoded differently from values placed directly on the page in HTML, or values placed in CSS. To protect against this, we can use the JSTL ```<c:url>``` tag, which forces relative URLs to be converted to absolute URLs.

One potential problem with XSS is when a value starts in one context, and shifts to another. *Multiple context XSS* attacks, may start in the URL but then be output onto the page. In general, it is best to allow only the smallest subset of characters that are needed for the application to function.

There are also several useful HTTP headers your application can set to prevent XSS attacks. For example, setting ```X-XSS-Protection``` to 1 will ensure that the browser uses its built-in XSS protections. While this is usually enabled by default, it may not be on some older browsers. It's also worth setting a strong ```Content-Security-Policy``` header. While out of the scope of this document, CSP will help protect your site in a broader way than just against XSS.

