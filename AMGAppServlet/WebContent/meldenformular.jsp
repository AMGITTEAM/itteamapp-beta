<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="benutzername" scope="request" type="java.lang.String" />
<jsp:useBean id="passwort" scope="request" type="java.lang.String" />
<!DOCTYPE html>
<html>
<head>
<title>IT-Team</title>
</head>
<body>
	<form method="get" action="/AMGAppServlet/visual">
		<table>
			<tr>
				<input type="hidden" name="username" id="username"
					value="<%=benutzername%>" />
				<input type="hidden" name="password" id="password"
					value="<%=passwort%>" />
				<input type="hidden" name="requestType" id="requestType"
					value="it-team-melden-senden" />
				<td>
					<label for="gebaeude">Gebäude: </label>
				</td>
				<td>
					<select name="gebaeude" id="gebaeude" required>
						<option value="" selected hidden disabled>Bitte wählen...</option>
						<option value="A">Altbau</option>
						<option value="N">Neubau</option>
						<option value="H">Hauptbau</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="etage">Etage: </label>
				</td>
				<td>
					<select name="etage" id="etage" required>
						<option value="" selected hidden disabled>Bitte wählen...</option>
						<option value="U">U</option>
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="raum">Raum: </label>
				</td>
				<td>
					<select name="raum" id="raum" required>
						<option value="" selected hidden disabled>Bitte wählen...</option>
						<option value="00">00</option>
						<option value="01">01</option>
						<option value="02">02</option>
						<option value="03">03</option>
						<option value="04">04</option>
						<option value="05">05</option>
						<option value="06">06</option>
						<option value="07">07</option>
						<option value="08">08</option>
						<option value="09">09</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="13">13</option>
						<option value="14">14</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="wichtigkeit">Wichtigkeit: </label>
				</td>
				<td>
					<select name="etage" id="etage" required>
						<option value="" selected hidden disabled>Bitte wählen...</option>
						<option value="3">Wichtig</option>
						<option value="2">Normal</option>
						<option value="1">Unwichtig</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="fehler" style="vertical-align: top">Fehler: </label>
				</td>
				<td>
					<textarea name="fehler" id="fehler" required></textarea>
				</td>
			</tr>
			<tr>
				<td>
					<label for="beschreibung" style="vertical-align: top">Beschreibung:
					</label>
				</td>
				<td>
					<textarea name="beschreibung" id="beschreibung" required></textarea>
				</td>
			</tr>
		</table>
		<br />
		<button type="submit">Senden</button>
	</form>
</body>
</html>
