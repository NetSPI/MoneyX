## Command Injection

#### Description

Command and Expression Language Injection vulnerabilities are a class of vulnerability similar to SQL Injection where attackers can execute malicious on the server. Often times, untrusted data that is unproperly validated can end up in strings that are directly executed as code, and become indistinguishable from the code created by the original developer. Since the malicious commands will appear in-stream with the intended code, it will also execute with the same privileges as the original operation, creating a security risk. Command and Expression Language Injection attacks are often used to establish a foothold on the affected server that is then leveraged by an attacker to further attack an organization's network.

The most common avenue for any injection attack is when unverified input is directly concatenated to a command string.

#### Problem
URL: http://localhost:8080/test.jsp

MoneyX contains a test JSP file within the views directory that was ostensibly used during initial setup as a temporary script or development aid to evaluate spring parameters. Since this parameter is user-controllable, it is possible to pass in various commands and expressions to be executed by the application.


#### Code Snippet
src/main/java/webapp/WEB-INF/views/test.jsp

```
<spring:eval expression="${param.test }" var="test" />
<c:out value="${test}" />

```

src/main/java/com/nvisium/androidnv/api/controller/DashboardController.java
```
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(
			@RequestParam(required = false, value = "@environment.getProperty('user')") String test) {
		return "test";
	}
```

#### Solution

The main solution for dealing with command injection attacks is to remove any evaluation or passing of user-controlled parameters back to command interpreters. This functionality very rarely needs user-input, but if it does, input validation must be performed to eliminate control characters that give an attacker freeform access to commands and expressions. The most common way to do this is by building a regular expression that validates the user-provided string before being concatanated to the command expression.

In the case of MoneyX, removal of the test.jsp page would be top priority. If user input is required for the command parameters, implement a regular expression to limit what data can be passed into the expression by a user.
