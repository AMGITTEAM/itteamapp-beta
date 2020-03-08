<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="benutzername" scope="request" type="java.lang.String" />
<jsp:useBean id="passwort" scope="request" type="java.lang.String" />
<jsp:useBean id="rechtHoehe" scope="request" type="java.lang.Integer" />
<jsp:useBean id="eintraege" scope="request" type="java.util.List<String>" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<link rel="stylesheet" href="css/format.css" type="text/css">
<link rel="stylesheet" href="css/menue.css" type="text/css">
<link rel="stylesheet" href="css/animation.css" type="text/css">
<link rel="stylesheet" href="css/font-roboto-light.css" type="text/css">
<link rel="stylesheet" href="css/font-open-sans-light.css"
	type="text/css">
<!-- all.min.css = fontawesome-icon-font -->
<link rel="stylesheet" href="css/all.min.css" type="text/css">
</head>
<body>
<%for(String eintrag : eintraege){%>
<h3><b><%=eintrag.split("Titel: \"")[1].split("\"//Inhalt: \"")[0].replaceAll("\n","<br/>") %></b></h3>
<p><%=eintrag.split("Inhalt: \"")[1].split("\"//Datum: ")[0].replaceAll("\n","<br/>") %><br/></p>
<p style="text-align:right;">Datum: <%=eintrag.split("Datum: ")[1].split("//Enddatum: ")[0] %></p>
<hr/>
<%}%>
</body>
</html>