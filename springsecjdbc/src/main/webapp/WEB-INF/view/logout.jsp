<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>logout page</title>
</head>
<body>
 <h1>${message}</h1>
 <a href = "<c:url value='/login' />">Log In Again!!</a>
</body>
</html>
