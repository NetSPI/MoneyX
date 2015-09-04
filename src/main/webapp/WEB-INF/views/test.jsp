<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!--VULN - Expression Language Injection -->
<!-- Example request - http://localhost:8080/test?test=@environment.getProperty(%27user%27) -->
<spring:eval expression="${param.test }" var="test" />
<c:out value="${test}" />
