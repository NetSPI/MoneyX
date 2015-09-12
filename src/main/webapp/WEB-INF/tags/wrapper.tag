<%@tag description="template" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="/dist/favicon.ico">

    <title>MoneyX</title>

    <!-- Bootstrap core CSS -->
    <link href="/dist/css/bootstrap.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/dist/css/bootstrap-theme.css" rel="stylesheet">

    <link href="/dist/css/custom.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body role="document">

    <!-- Fixed navbar -->
    <nav class="navbar navbar-default navbar-static-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/">MoneyX</a>
        </div>
        <sec:authorize access="isAuthenticated()">
        <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="/dashboard">Dashboard</a></li>
            <li><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Events<span class="caret"></span></a>
            	<ul class="dropdown-menu">
            		<li><a href="/event/list-owner/<sec:authentication property="principal.user.id" />">My Events</a></li>
            		<li><a href="/event/list-member/<sec:authentication property="principal.user.id" />">Member Events</a></li>
            		<li><a href="/event/add">Create Event</a></li>
            	</ul>
            </li>
            <li><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Payments<span class="caret"></span></a>
            	<ul class="dropdown-menu">
            		<li><a href="/payment/make-payment">Make a Payment</a></li>
            		<li><a href="/payment/list-received/<sec:authentication property="principal.user.id" />">Received Payments</a></li>
            		<li><a href="/payment/list-sent/<sec:authentication property="principal.user.id" />">Sent Payments</a></li>
            	</ul>
            </li>
            <li><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Friends<span class="caret"></span></a>
            	<ul class="dropdown-menu">
            		<li><a href="/get-public-users">Find Friends</a></li>
            		<li><a href="/friend/get-friends">My Friends</a></li>
            		<li><a href="/friend/list-received-friend-requests">Received Friend Requests</a></li>
            		<li><a href="/friend/list-sent-friend-requests">Sent Friend Requests</a></li>
            	</ul>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <p class="navbar-text">Account Balance: $<sec:authentication property="principal.user.balance" /></p>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Hello, <sec:authentication property="principal.username" /> <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li><a href="/profile/<sec:authentication property="principal.user.id" />">Profile</a></li>
                <li><a href="/get-settings">Settings</a></li>
                <li><a href="/payment/balance">Fund Account</a></li>
                <li role="separator" class="divider"></li>
                <li class="dropdown-header">Account</li>
                <li><a href="/logout">Sign Out</a></li>
              </ul>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
        <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-right">
            <a href="/login"><button type="button" class="btn btn-default navbar-btn">Sign in</button></a>
            <a href="/register"><button type="button" class="btn btn-default navbar-btn">Register</button></a>
          </ul>
        </div><!--/.nav-collapse -->
        </sec:authorize>
      </div>
    </nav>

    <div class="container" role="main" id="main">
    <script>
    if(location.hash){
    	document.write('<div class="alert alert-info alert-dismissible" role="info">'+location.hash+' is not a valid hash tag</div>');
    }
    </script>

    <c:if test="${not empty param.info}">
    <div class="alert alert-info alert-dismissible" role="danger">
      ${param.info}
    </div>
    </c:if>

    <c:if test="${not empty danger}">
    <div class="alert alert-danger alert-dismissible" role="danger">
        ${danger}
    </div>
    </c:if>
    <c:if test="${not empty success}">
    <div class="alert alert-success alert-dismissible" role="success">
        ${success}
    </div>
    </c:if>
    <c:if test="${not empty info}">
    <div class="alert alert-info alert-dismissible" role="info">
        ${info}
    </div>
    </c:if>
      <jsp:doBody/>
    </div> <!-- /container -->

    <!-- footer-container is for styling the footer -->
    <div class="container-fluid footer-container">
      <div class="row footer-row">
        <div class="col-md-4 col-md-offset-4">
          <!-- Footer content goes here -->
          <p><img src="/dist/img/trustacademy-gray.png" class="footer-image"></p>
        </div> <!-- /.col-md-4 -->
      </div> <!-- /.row -->
    </div> <!-- /container-fluid -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="/dist/js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="/dist/assets/js/ie10-viewport-bug-workaround.js"></script>

    <script src="/dist/js/jquery.card.js"></script>
    <script>
    $('.payment-cc-form').card({
        // a selector or DOM element for the container
        // where you want the card to appear
        container: '.card-wrapper', // *required*
        formSelectors: {
          numberInput: 'input#creditcard', // optional — default input[name="number"]
          expiryInput: 'input#expirationdate', // optional — default input[name="expiry"]
          cvcInput: 'input#cvccode', // optional — default input[name="cvc"]
          nameInput: 'input#fullname' // optional - defaults input[name="name"]
        },

        // all of the other options from above
    });
    </script>
  </body>
</html>

