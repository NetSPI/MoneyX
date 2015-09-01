<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:auth>
    <c:if test="${error != null}">
    <div class="alert alert-danger alert-dismissible" role="danger">
        <button type="button" class="close" data-dismiss="danger" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <strong>Error:</strong> Invalid username and/or answer<br />
    </div>
    </c:if>

    <form:form class="form-signin" action="/forgot-password" modelAttribute="user" method="post">
        <h2 class="form-signin-heading">Set A New Password</h2>

 		<div class="form-group">
        	<input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
        	<input type="text" id="answer" name="answer" class="form-control" placeholder="Favorite Color" required autofocus>
        	<input type="password" id="password" name="password" class="form-control" placeholder="New Password" required>
		</div>
		
        <button class="btn btn-lg btn-primary btn-block" type="submit">Set Password</button>
    </form:form>

</t:auth>