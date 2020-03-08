<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="rechtHoehe" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="benutzername" scope="request" type="java.lang.String"/>
<jsp:useBean id="passwort" scope="request" type="java.lang.String"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Ergebnis</title>
</head>
<body>
<%if(rechtHoehe >= 1){%>
<a href=<%="/AMGAppServlet/amgapp?requestType=vplan&request=Heute&username="+benutzername+"&password="+passwort.hashCode()%>>VPlan Heute</a>
<br/>
<a href=<%="/AMGAppServlet/amgapp?requestType=vplan&request=Folgetag&username="+benutzername+"&password="+passwort.hashCode()%>>VPlan Folgetag</a>
<br/>
<a href="about:clear">Stundenplan (in Arbeit)</a>
<br/>
<a href="about:clear">Schwarzes Brett (in Arbeit)</a>
<br/>
<%} else{%>
Ungenügende Rechtstufe!
<%}%>
<%if(rechtHoehe >= 2){%>
<a href="about:clear">ITTeam-Melden (in Arbeit)</a>
<br/>
<%}%>
<%if(rechtHoehe >= 3){%>
<a href="about:clear">ITTeam-Meldungen abrufen (in Arbeit)</a>
<br/>
<a href="about:clear">Feedback abrufen (in Arbeit)</a>
<br/>
<%}%>
<%if(rechtHoehe >= 1){%>
<br/>
<br/>
<a href="/AMGAppServlet/visual?username=&password=&requestType=logout">Logout</a>
<br/>
<a href=<%="/AMGAppServlet/visual?username="+benutzername+"&password="+passwort+"&requestType=einstellungen"%>>Einstellungen (in Arbeit)</a>
<br/>
<%}%>
</body>
</html>