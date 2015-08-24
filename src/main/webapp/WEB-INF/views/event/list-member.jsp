<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>Events I'm Part Of</h1>
    <table class="table">
        <tr>
            <th>#</th>
            <th>Total Amount</th>
            <th>Date Created</th>
        </tr>
        <c:forEach var="event" items="${events}">
        <tr>
        <td>${event.id}</td>
        <td>$${event.amount}</td>
        <td>${event.created}</td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>