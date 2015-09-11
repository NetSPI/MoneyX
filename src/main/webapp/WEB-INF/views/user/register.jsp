<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:auth>
    <c:if test="${param.error != null}">
    <div class="alert alert-danger alert-dismissible" role="danger">
        <button type="button" class="close" data-dismiss="danger" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <strong>Error:</strong> Invalid username and/or password<br />
    </div>
    </c:if>

    <c:if test="${param.logout != null}">
    <div class="alert alert-info alert-dismissible" role="logout">
        <button type="button" class="close" data-dismiss="logout" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        You have been logged out.
    </div>
    </c:if>

    <form:form class="form-signin" action="/register" modelAttribute="user" method="post">
        <h2 class="form-signin-heading">Register an Account</h2>

        <label for="username" class="sr-only">Username</label>
        <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>

        <label for="password" class="sr-only">Password</label>
        <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>

        <label for="email" class="sr-only">Email</label>
        <input type="email" id="email" name="email" class="form-control" placeholder="Email" required>
        
        <label for="answer" class="sr-only">Secret Question - What is your favorite color?</label>
        <input type="answer" id="email" name="answer" class="form-control" placeholder="Answer" required>

        <label for="firstname" class="sr-only">First Name</label>
        <input type="text" id="firstname" name="firstname" class="form-control" placeholder="First Name" required>

        <label for="lastname" class="sr-only">Last Name</label>
        <input type="text" id="lastname" name="lastname" class="form-control form-bottom" placeholder="Last Name" required>
        
        <input type="hidden" id="next" name="next" value="${param.next}">

        <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
    </form:form>

</t:auth>