<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <h1>The Latest</h1>
    <table class="table table-condensed table-striped">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Total Amount</th>
            <th>Completed?</th>
        </tr>
        <c:forEach var="event" items="${events}">
        <tr>
        <td><c:out value="${event.id}"/></td>
        <td><c:out value="${event.name}"/></td>
        <td><c:out value="${event.amount}"/></td>
        <td><c:out value="${event.completed}"/></td>
        </tr>
        </c:forEach>
    </table>

    <c:forEach var="event" items="${events}">
    <div class="row dashboard-row">
        <div class="col-md-2">
            <p><h1>$<c:out value="${event.amount}"/></h1></p>
        </div>
        <div class="col-md-7">
            <h3><c:out value="${event.name}"/></h3>
            <c:forEach var="user" items="${users[event]}">
                <c:out value="${user.firstname}"/> <c:out value="${user.lastname}"/>
            </c:forEach>
        </div>
        <div class="col-md-1 text-center">
            <c:choose>
            <c:when test="${event.completed}">
            <p>Fulfilled <h1><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></h1></p>
            </c:when>
            <c:otherwise>
            Unfulfilled <h1><span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span></h1>
            </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr />
    </c:forEach>
</t:wrapper>