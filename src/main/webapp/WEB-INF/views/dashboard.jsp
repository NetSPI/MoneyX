<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="encoder" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<t:wrapper>
	<div class="col-md-12">
	<div class="wrapper text-center">
		<div class="btn-group text-center">
			<a href="/payment/make-payment" class="btn btn-sm btn-primary">Make Payment</a>
			<a href="/get-public-users" class="btn btn-sm btn-primary">Find Friends</a>
			<a href="/event/add" class="btn btn-sm btn-primary">Create Event</a>
			<a href="/payment/balance" class="btn btn-sm btn-primary">Fund Account</a>
		</div>
	</div>
	</div>
	
	<div class="row">
	<div class="col-md-6">
    <h3>Shared With Me</h3>
    <table class="table table-condensed table-striped">
        <tr>
            <th>Event</th>
            <th>Pay</th>
            <th>From</th>
        </tr>
        <c:forEach var="event" items="${events}">
        <tr>
	        <td>${event.value.name}</td>
	        <td><a href="/payment/make-payment?event=${event.value.id}">$<c:out value="${event.key.amount}"/></a></td>
	        <td>${users[event.value][0].username}</td>
        </tr>
        </c:forEach>
    </table>
    </div>

	<div class="col-md-6">
	<h3>My Shared Events</h3>

    <table class="table table-condensed table-striped">
        <tr>
            <th>Event</th>
            <th>Total</th>
            <th>To</th>
        </tr>
        <c:forEach var="o" items="${owned}">
        <tr>
        <td>${o.name}</td>
        <td>$${o.amount}</td>
        <td>
        	<c:forEach var="user" items="${users[o]}">
	        	<c:out value="${user.username}"/>
	        </c:forEach>
        </td>
        </tr>
        </c:forEach>
        <tr>
        <td colspan="3" align="right">
        	<a href="/event/add"><span class="glyphicon glyphicon-plus" aria-hidden="true" title="Add Event"></span></a>
        </td>
        </tr>
    </table>
    </div>
    </div>
    
    <hr />
    <div class="row">
    <div class="col-md-6">
    <h3>Sent</h3>
    <table class="table table-condensed table-striped">
        <tr>
            <th>Event</th>
            <th>Amount</th>
            <th>To</th>
        </tr>
        <c:forEach var="s" items="${sent}">
        <tr>
	        <td>${s.event.name}</td>
	        <td>$${s.amount}</td>
	        <td>${s.receiver.username}</td>
        </tr>
        </c:forEach>
    </table>
    </div>
    
    <div class="col-md-6">
    <h3>Received</h3>
    <table class="table table-condensed table-striped">
        <tr>
            <th>Event</th>
            <th>Amount</th>
            <th>To</th>
        </tr>
        <c:forEach var="r" items="${received}">
        <tr>
	        <td>${r.event.name}</td>
	        <td>$${r.amount}</td>
	        <td>${r.sender.username}"</td>
        </tr>
        </c:forEach>
    </table>
    </div>
    </div>
    
    <hr />
    
</t:wrapper>