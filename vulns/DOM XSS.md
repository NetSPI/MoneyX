## Document Object Model (DOM) Cross-site Scripting (XSS)

#### Description

Cross-Site Scripting is a vulnerability that occurs when user-supplied data is unsafely rendered back in an application. When malicious data is rendered to the front end views without any escaping, the user's browser may be hijacked or commanded for malicious purposes. DOM-XSS is a unique version of XSS where the user-supplied data is rendered back using the browser's rendering engine (for example, JavaScript) instead of by the application server. In other words, the page itself (the HTTP response) does not change, but the client side code contained in the page executes differently due to the malicious modifications that have occurred in the DOM environment.

XSS flaws usually rely on executing JavaScript in the user's browser, but can occur in a wide variety of settings beyond the common ```<script>``` tag. HTML is a versatile language, and JavaScript can be executed from within HTML attributes, CSS, or even from within JSON payloads.

There are usually two broad forms of XSS: stored XSS, and reflected XSS. DOM-XSS is a subset of both forms, but is most commonly seen as an addition to reflected XSS. DOM XSS is usually parsed from a URL or some other client-only input, and affects the user without any interaction from the server. Reflected XSS is much harder to detect, since there is no interaction with the server as the user is hijacked. On the other hand, stored XSS is usually sent as input to the server and saved in a database. Any time the page with the payload is loaded, it will replay the exploit. While easier to detect, stored XSS can have a wider reaching impact on the end user.

#### Problem
URL: http://localhost:8080/dashboard#test

MoneyX intends to use JavaScript to search for specific URL hash terms within each page and adds an error message to DOM when it cannot be found. However, as seen with reflected and stored XSS, the application is not adequately protected! Although there is no indication in each rendered page that the outputs are "insecure", use of the ```document.write``` JavaScript function in combination with the ```location.hash``` user-controlled variable does not perform any encoding by default. Pop-ups and other JavaScript functions may be called using the same techniques as within the other XSS forms.

#### Walkthrough
1. Open up MoneyX using the SauceLabs Firefox Browser
2. Sign in as 'user' with password 'user123'
3. Click on Events -> My Events
4. Append ```#test<script>alert('XSS')``` to the end of the URL.
4. Hit enter, click the reload button for the browser and an alert box pops up with 'XSS' in it.
5. Close the alert box.

#### Code Snippet
src/main/webapp/WEB-INF/tags/wrapper.tag
Lines 99-104
```
   <div class="container" role="main" id="main">
    <script>
    if(location.hash){
        document.write('<div class="alert alert-info alert-dismissible" role="info">'+location.hash+' is not a valid hash tag</div>');
    }
    </script>

    <c:if test="${not empty param.info}">
```

#### Solution

DOM-XSS requires specific knowledge of the user-controlled JavaScript parameters, in addition to those functions that write directly to the DOM without any protections against unsafe content. First of all, always treat untrusted data as displayable text. Use ```element.textContent``` to instruct the browser how the data should be treated. Further, use ```document.createElement``` instead of ```document.write``` when manipulating the DOM directly. Finally, when outputting JavaScript variables to a dynamically-generated JSP page, ensure you use the [JSTL](https://jstl.java.net/) library's ```<c:out>``` tag or OWASP's Java Encoder ```${encoder.forHtml(param.test)}``` to properly escape for XML/HTML.

### Solution Code
wrapper.tag
Lines 99-107
```
    <div class="container" role="main" id="main">
    <script>
    if(location.hash){
    	var d = document.createElement('div');
    	d.className="alert alert-info alert-dismissable";
    	d.textContent=location.hash+' is not a valid hash tag';
    	document.getElementById('main').appendChild(d);
    }
    </script>
```
