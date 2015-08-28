<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <div class="row">
        <form:form class="form-signin" action="/event/add" modelAttribute="event" method="post">
        <div class="col-md-6">
            <h2 class="form-signin-heading">Make a Payment</h2>

            <label for="amount" class="sr-only">Amount</label>
            <div class="input-group">
                <div class="input-group-addon">$</div>
                <input type="number" id="amount" name="amount" class="form-control" step="0.01" placeholder="Amount" required>
            </div>

            <hr />

            <button class="btn btn-lg btn-primary btn-block" type="submit">Pay</button>
        </div>
        <div class="col-md-6">
            <h3>Choose an event</h3>
            <select id="event" name="event" multiple class="form-control">
            <c:forEach var="event-membership" items="${event-memberships}">
                <option value="${event-membership.id}">$${event-membership.amount}</option>
            </c:forEach>
            </select>
        </div>
        </form:form>
    </div>

</t:wrapper>