<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="fehler" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="responsed" scope="request" type="java.lang.String"/>

<%if(fehler){%>
Eingaben konnten nicht in Zahlen umgewandelt werden!
<%} else{%>
<%=responsed%>
<%}%>