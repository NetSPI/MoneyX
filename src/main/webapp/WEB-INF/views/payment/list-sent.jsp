<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>Sent Payments</h1>
    <table class="table">
        <tr>
            <th>ID</th>
            <th>Amount</th>
            <th>To</th>
            <th>Event</th>
            <th>Timestamp</th>
        </tr>
        <c:forEach var="payment" items="${payments}">
        <tr>
        	<td>${payment.id}</td>
        	<td>$${payment.amount}</td>
			<td>${payment.receiver.firstname} ${payment.receiver.lastname}</td>
			<td>${payment.event.name}</td>
			<td>${payment.timestamp}</td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>