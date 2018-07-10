<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="fehler" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="responsed" scope="request" type="java.lang.String"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Ergebnis</title>
</head>
<body>
<%if(fehler){%>
Eingaben konnten nicht in Zahlen umgewandelt werden!
<%} else{%>
<%=responsed%>
<%}%>
</body>
</html>