<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="benutzername" scope="request" type="java.lang.String" />
<jsp:useBean id="passwort" scope="request" type="java.lang.String" />
<jsp:useBean id="rechtHoehe" scope="request" type="java.lang.Integer" />
<jsp:useBean id="aktuelleKlasse" scope="request" type="java.lang.String" />
<jsp:useBean id="farbeEigeneKlasse" scope="request"
	type="java.lang.String" />
<jsp:useBean id="farbeUnterstufe" scope="request"
	type="java.lang.String" />
<jsp:useBean id="farbeMittelstufe" scope="request"
	type="java.lang.String" />
<jsp:useBean id="farbeOberstufe" scope="request" type="java.lang.String" />
<jsp:useBean id="iconsImVertretungsplan" scope="request"
	type="java.lang.Boolean" />
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
	<form method="get" action="/AMGAppServlet/visual">
		<input type="hidden" name="username" value=<%=benutzername%>>
		<input type="hidden" name="password" value=<%=passwort%>>
		<input type="hidden" name="requestType" value="saveEinstellungen">
		<div>
			<label for="aktuelleKlasse">Aktuelle Klasse:</label>
			<select id="aktuelleKlasse" name="aktuelleKlasse">
				<option value=<%=aktuelleKlasse%> selected hidden><%=aktuelleKlasse%></option>
				<option value="5a">05a</option>
				<option value="5b">05b</option>
				<option value="5c">05c</option>
				<option value="5d">05d</option>
				<option value="6a">06a</option>
				<option value="6b">06b</option>
				<option value="6c">06c</option>
				<option value="6d">06d</option>
				<option value="7a">07a</option>
				<option value="7b">07b</option>
				<option value="7c">07c</option>
				<option value="7d">07d</option>
				<option value="8a">08a</option>
				<option value="8b">08b</option>
				<option value="8c">08c</option>
				<option value="8d">08d</option>
				<option value="9a">09a</option>
				<option value="9b">09b</option>
				<option value="9c">09c</option>
				<option value="9d">09d</option>
				<option value="EF">EF</option>
				<option value="Q1">Q1</option>
				<option value="Q2">Q2</option>
			</select>
		</div>
		<div>
			<label for="iconsImVertretungsplan">Icons im Vertretungsplan:
			</label>
			<input type="checkbox" id="iconsImVertretungsplan"
				name="iconsImVertretungsplan" style="display: inline;"
				<%=iconsImVertretungsplan ? "checked=\"checked\"" : ""%>>
		</div>
		</p>
		<div>
			<label for="farbeEigeneKlasse">Farbe eigene Klasse: </label>
			<input type="color" id="farbeEigeneKlasse" name="farbeEigeneKlasse"
				value=<%=farbeEigeneKlasse%>>
		</div>
		<div>
			<label for="farbeUnterstufe">Farbe Unterstufe: </label>
			<input type="color" id="farbeUnterstufe" name="farbeUnterstufe"
				value=<%=farbeUnterstufe%>>
		</div>
		<div>
			<label for="farbeMittelstufe">Farbe Mittelstufe: </label>
			<input type="color" id="farbeMittelstufe" name="farbeMittelstufe"
				value=<%=farbeMittelstufe%>>
		</div>
		<div>
			<label for="farbeOberstufe">Farbe Oberstufe: </label>
			<input type="color" id="farbeOberstufe" name="farbeOberstufe"
				value=<%=farbeOberstufe%>>
		</div>
		<input type="submit" value="Speichern">
	</form>
</body>
</html>