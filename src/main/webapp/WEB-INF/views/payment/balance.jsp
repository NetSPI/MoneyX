<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:wrapper>
    <div class="row">
        <div class="col-md-6">
            <p class="lead">Your current balance is $${user.balance}</p>
        </div>
    </div>

    <div class="row">
        <form:form class="form-signin form-horizontal payment-cc-form" action="/payment/balance" modelAttribute="payment" method="post">
        <div class="col-md-6">
            <h3 class="text-center form-signin-heading">Add money to your balance</h3>
            <div class="card-wrapper"></div>
        </div>
        <div class="col-md-6">
            <div class="form-group">
                <label for="number" class="col-md-2 control-label">CC #</label>
                <div class="col-md-10">
                    <input class="form-control" placeholder="**** **** **** ****" type="text" id="creditcard" name="creditcard">
                </div>
            </div>
            <div class="form-group">
                <label for="number" class="col-md-2 control-label">Full Name</label>
                <div class="col-md-10">
                    <input class="form-control" placeholder="Full name" type="text" id="fullname" name="fullname">
                </div>
            </div>
            <div class="form-group">
                <label for="number" class="col-md-2 control-label">Exp. Date</label>
                <div class="col-md-10">
                    <input class="form-control" placeholder="MM/YY" type="text" id="expirationdate" name="expirationdate">
                </div>
            </div>
            <div class="form-group">
                <label for="number" class="col-md-2 control-label">CVC</label>
                <div class="col-md-10">
                    <input class="form-control" placeholder="CVC" type="text" id="cvccode" name="cvccode">
                </div>
            </div>
            <div class="form-group">
                <label for="number" class="col-md-2 control-label">Amount</label>
                <div class="col-md-10">
                    <div class="input-group">
                        <div class="input-group-addon">$</div>
                        <input class="form-control" placeholder="5.00" step="0.01" type="number" name="amount">
                    </div>
                </div>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Add</button>
        </div>
        </form:form>
    </div>

    <p class="small">Note: this is a training application, please don't use a real credit card!</p>

</t:wrapper>