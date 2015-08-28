<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>MoneyX Sent Friend Requests</h1>
    <table class="table">
        <tr>
            <th>ID</th>
            <th>Receiver</th>
            <th><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></th>
        </tr>
        <c:forEach var="req" items="${friendrequests}">
        <tr>
        <td>${req.id}</td>
        <td>${req.receiver}</td>
        <td><a href="/friend/delete-friend-request/${req.id}"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a></td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>