<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page session="true"%>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script>
$.ajax({url: "/springsec/test", 
	success: function(result){
        $("#div1").html(result);
    }});
</script>
</head>
<body>
	<h1>Title : ${title}</h1>
	<h1>Message : ${message}</h1>
	
	<div id="div1">
	</div>

	<c:if test="${pageContext.request.userPrincipal.name != null}">
	   <h2>Welcome : ${pageContext.request.userPrincipal.name}| <a href="<c:url value="/logout" />" > Logout</a></h2>
	</c:if>
	
	   <h2>Welcome : <sec:authentication property = "name"/></h2>
	   <sec:authorize ifAnyGranted="ROLE_USER">
	   	<a>Check if Authorized!!</a>
	   </sec:authorize>
</body>

</html>