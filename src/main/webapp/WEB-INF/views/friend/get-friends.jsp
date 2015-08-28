<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>MoneyX Friends</h1>
    <table class="table">
        <tr>
            <th>Friend ID</th>
            <th>Friend Name</th>
            <th><span class="glyphicon glyphicon-trash" aria-hidden="true"></th>
        </tr>
        <c:forEach var="friend" items="${friends}">
        <tr>
        <c:choose>
        	<c:when test="friend.user1 == principal.user">
        		<td>${friend.id}</td>
        		<td>${friend.user2}</td>
        		<td><a href="/friend/delete-friend?friend=${friend.user2}"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a></td>
        	</c:when>
        	<c:otherwise>
        		<td>${friend.id}</td>
        		<td>${friend.user1}</td>
        		<td><a href="/friend/delete-friend?friend=${friend.user1}"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a></td>
        	</c:otherwise>
        </c:choose>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>