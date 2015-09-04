<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>MoneyX Friends</h1>
    <table class="table">
        <tr>
        	<th>Username</th>
            <th>Full Name</th>
            <th><span class="glyphicon glyphicon-trash" aria-hidden="true"></th>
        </tr>
        <c:forEach var="friend" items="${friends}">
        <tr>
			<td>${friend.username}</td>
        	<td>${friend.firstname} ${friend.lastname}</td>
        	<td><a href="/friend/delete-friend?friend=${friend.id}"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a></td>
        </tr>
        </c:forEach>
    </table>
</t:wrapper>