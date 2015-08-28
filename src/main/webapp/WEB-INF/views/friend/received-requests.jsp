<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>MoneyX Received Friend Requests</h1>
    <table class="table">
        <tr>
            <th>ID</th>
            <th>Sender</th>
            <th><span class="glyphicon glyphicon-trash" aria-hidden="true"></th>
            <th><span class="glyphicon glyphicon-ok" aria-hidden="true"></th>
        </tr>
        <c:forEach var="req" items="${friendrequests}">
        <tr>
        <td>${req.id}</td>
        <td>${req.sender.firstname} ${req.sender.lastname}</td>
        <td><a href="/friend/delete-friend-request/${req.id}"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a></td>
        <td><a href="/friend/accept-friend-request/${req.id}"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></a></td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>