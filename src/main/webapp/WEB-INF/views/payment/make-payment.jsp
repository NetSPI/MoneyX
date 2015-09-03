<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <div class="row">
        <form:form class="form-signin" action="/payment/make-payment" modelAttribute="payment" method="post">
        <div class="col-md-6">
            <h2 class="form-signin-heading">Make a Payment</h2>
            
            <div class="form-group">
            <label for="event">Choose a Shared Payment</label>
            <select id="event" name="event" class="form-control">
            <c:forEach var="entry" items="${events}">
                <option value="${entry.value.id}"> ${entry.value.name} ($${memberships.get(entry.value.id).amount}) - ${users.get(entry.value.id).firstname} ${users.get(entry.value.id).lastname}</option>
            </c:forEach>
            </select>
            </div>

            
            <div class="form-group">
            	<label for="amount">Enter an amount</label>
                <input type="number" id="amount" name="amount" class="form-control" placeholder="Amount" required>
            </div>

            <hr />

            <button class="btn btn-lg btn-primary btn-block" type="submit">Pay</button>
        </div>

        </form:form>
    </div>

</t:wrapper>