<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="benutzername" scope="request" type="java.lang.String"/>
<jsp:useBean id="passwort" scope="request" type="java.lang.String"/>
<!DOCTYPE html>
<html>
<head>
<title>Login</title>
<meta http-equiv='refresh' content=<%="0;URL=/AMGAppServlet/visual?username="+benutzername+"&password="+passwort%>>
</head>
<body>
<p>Die Meldung wurde aufgenommen. Sie werden automatisch auf die Startseite weitergeleitet. Falls dies nicht passieren sollte, klicken Sie bitte <a href=<%="/AMGAppServlet/visual?username="+benutzername+"&password="+passwort%>>hier</a>.</p>
</body>
</html>