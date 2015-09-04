<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>Find Friends</h1>
    <table class="table">
        <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th></th>
        </tr>
        <c:forEach var="user" items="${users}">
        <tr>
        <td>${user.id}</td>
        <td>${user.firstname}</td>
        <td>${user.lastname}</td>
        <td>${user.email}</td>
        <td><a href="/friend/send-friend-request/${user.id}"><span class="glyphicon glyphicon-plus" aria-hidden="true" title="Add Friend"></span></a></td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>