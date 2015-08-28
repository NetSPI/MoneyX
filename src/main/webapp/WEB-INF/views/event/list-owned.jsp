<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>My Events</h1>
    <table class="table">
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Total Amount</th>
            <th>Completed?</th>
            <th></th>
        </tr>
        <c:forEach var="event" items="${events}">
        <tr>
        <td>${event.id}</td>
        <td>${event.name}</td>
        <td>$${event.amount}</td>
        <td>${event.completed}</td>
        <td><a href="/event/delete/${event.id}"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a></td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>