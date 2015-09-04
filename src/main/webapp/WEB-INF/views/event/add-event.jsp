<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <div class="row">
        <form:form class="form-signin" action="/event/add" modelAttribute="event" method="post">
        <div class="col-md-6">
            <h2 class="form-signin-heading">Create a Shared Payment Event</h2>

            <label for="name" class="sr-only">Name</label>
            <input type="text" id="name" name="name" class="form-control" placeholder="Name" required autofocus>

            <label for="amount" class="sr-only">Amount</label>
            <div class="input-group">
                <div class="input-group-addon">$</div>
                <input type="number" id="amount" name="amount" class="form-control" step="0.01" placeholder="Amount" required>
            </div>

            <hr />

            <button class="btn btn-lg btn-primary btn-block" type="submit">Add</button>
        </div>
        <div class="col-md-6">
            <h3>To</h3>
            <select id="users" name="users" multiple class="form-control" size=5>
            <c:forEach var="user" items="${users}">
                <option value="${user.id}">${user.username} (${user.firstname} ${user.lastname})</option>
            </c:forEach>
            </select>
            <table class="table">
	            <tr>
		            <td align="right">
		            	<a href="/get-public-users"><span class="glyphicon glyphicon-plus" aria-hidden="true" title="Find Friends"></span></a>
		            </td>
	            </tr>
            </table>
        </div>
        </form:form>
    </div>

</t:wrapper>