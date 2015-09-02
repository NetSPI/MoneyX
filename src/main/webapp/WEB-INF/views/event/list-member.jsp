<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>Shared Events</h1>
    <h6><i>Who do I owe money to?</i></h6>
    <table class="table">
        <tr>
            <th>#</th>
            <th>Owner</th>
            <th>My Share</th>
			<th>Total</th>
            <th>Event</th>
            <th>Date</th>
            <th>Pay Now</th>
        </tr>
        <c:forEach var="event" items="${events}">
        <tr>
        <td>${event.id}</td>
        <td>${users.get(event.id).firstname} ${users.get(event.id).lastname}</td>
        <td>$${memberships.get(event.id).amount}
        <td>$${event.amount}</td>
        <td>${event.name}</td>
        <td>${event.created}</td>
        <td><a href="/payment/make-payment?event=${event.id}"><span class="glyphicon glyphicon-usd" aria-hidden="true"></span></a></td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>