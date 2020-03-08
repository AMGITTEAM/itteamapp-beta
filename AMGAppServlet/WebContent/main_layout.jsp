<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="rechtHoehe" scope="request" type="java.lang.Integer" />
<jsp:useBean id="imageForHeader" scope="request" type="java.lang.String" />
<jsp:useBean id="title" scope="request" type="java.lang.String" />
<jsp:useBean id="benutzername" scope="request" type="java.lang.String" />
<jsp:useBean id="passwort" scope="request" type="java.lang.String" />
<jsp:useBean id="content_file" scope="request" type="java.lang.String" />
<jsp:useBean id="vplan" scope="request" type="java.lang.Boolean" />

<!doctype html>
<html lang="de">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="robots" content="INDEX,FOLLOW">
<meta name="page-type" content=" homepage">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="keywords"
	content="homepage,dokument,webpage,page,web,netz,homepage dokument webpage page web netz">
<meta name="description"
	content="homepage, dokument, webpage, page, web, netz">
<title>AMG Appsite</title>

<!-- /////////////////// Copyright-Vermerk /////////////////// -->
<!-- Der nachfolgende einzeilige Copyright-Vermerk (c) ist nicht zu loeschen.-->
<!-- (c)Copyright by S.I.S.Papenburg / www.on-mouseover.de/templates/ -->
<!--Ein Entfernen dieses Copyright/Urheberrecht-Vermerks kann rechtliche Schritte nach sich ziehen -->
<!-- ////////////////////////////////////// -->



<!-- /////////////////// Der nachfolgende Hinweis-Vermerk darf geloescht werden /////////////////// -->
<!-- Hinweis:
Das Anbieten dieser Vorlage auf einer Webseite, CD, DVD oder anderen Bild/Tontraegern ist untersagt.
Nutzen duerfen Sie diese Vorlage aber auf einer Webseite wie folgt:
Die Vorlage kann privat (kostenlos) und kommerziell/gewerblich (gegen Bezahlung) fuer Sie selbst oder eine dritte Person (andere Person oder Firma) genutzt werden.
Je nach dem Inhalt, welcher eingefuegt wird, kann auch fuer Privatpersonen eine kommerzielle Nutzung vorliegen.
Lesen Sie auf der Webseite www.on-mouseover.de/templates/
bitte die Nutzungsbedingungen nach.
-->
<!-- /////////////////// Ende /////////////////// -->


<link rel="stylesheet" href="css/format.css" type="text/css">
<link rel="stylesheet" href="css/menue.css" type="text/css">
<link rel="stylesheet" href="css/animation.css" type="text/css">
<link rel="stylesheet" href="css/font-roboto-light.css" type="text/css">
<link rel="stylesheet" href="css/font-open-sans-light.css"
	type="text/css">
<!-- all.min.css = fontawesome-icon-font -->
<link rel="stylesheet" href="css/all.min.css" type="text/css">

<link rel="apple-touch-icon" sizes="180x180"
	href="images/apple-touch-icon.png">
<link rel="icon" type="image/png" sizes="32x32"
	href="images/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16"
	href="images/favicon-16x16.png">
<link rel="manifest" href="images/site.webmanifest">
<link rel="mask-icon" href="images/safari-pinned-tab.svg"
	color="#ffffff">
<meta name="msapplication-TileColor" content="#ffffff">
<meta name="theme-color" content="#ffffff">

<style>
.vplan {
	background: #ccc;
}

@media ( max-width : 1024px) {
	.vplan {
		padding-left: 0;
		padding-right: 0;
		margin-left: 0;
		margin-right: 0;
		width: 100vw;
	}
}
</style>

<link rel="stylesheet" type="text/css"
	href="//wpcc.io/lib/1.0.2/cookieconsent.min.css" />
<script src="//wpcc.io/lib/1.0.2/cookieconsent.min.js"></script>
<script>
	window
			.addEventListener(
					"load",
					function() {
						window.wpcc
								.init({
									"colors" : {
										"popup" : {
											"background" : "#f6f6f6",
											"text" : "#000000",
											"border" : "#555555"
										},
										"button" : {
											"background" : "#555555",
											"text" : "#ffffff"
										}
									},
									"position" : "bottom",
									"padding" : "small",
									"margin" : "none",
									"content" : {
										"message" : "Diese Website nutzt Cookies, um die Nutzungsstatistiken noch detaillierter zu gestalten.",
										"link" : "Mehr erfahren",
										"button" : "Meinetwegen",
										"href" : "https://amgitt.de/AMGAppServlet/visual?username=&amp;password=&amp;requestType=datenschutz#Cookies"
									}
								})
					});
</script>


</head>


<body>

	<div class="container_haupt" id="top">
		<div class="wrapper">


			<!-- ============================== HAUPTBEREICH ============================== -->

			<div class="section-logo"
				style="background-image: url(images/<%=imageForHeader%>)">

				<!-- name mit position relative/absolute angelegt  -->
				<div class="wrapper-name filter">
					<span class="name">
						AMG Witten
						<br>
						<b>Appsite</b>
						<br>
						- für alle ohne Android -
					</span>
				</div>
				<!-- ENDE name mit position relative/absolute angelegt  -->


				<input type="checkbox" id="menuschalter">
				<label class="button-close" for="menuschalter">
					<i class="far fa-times-circle"></i>
				</label>
				<label class="button-open" for="menuschalter">
					<i class="fas fa-bars"></i>
				</label>



				<div id="nav">
					<div class="akkordeon">

						<div class="bereich">
							<input id="ac-1" name="akkordeon-1" type="radio">
							<label for="ac-1">
								<a
									href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort%>>Startseite</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>

						<%
							if (rechtHoehe >= 1) {
						%>
						<div class="bereich">
							<input id="ac-2" name="akkordeon-1" type="radio">
							<label for="ac-2">
								<b>Vertretungsplan</b>
							</label>
							<span>
								<ul>
									<li>
										<a
											href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
						+ "&requestType=vplan-heute"%>>Heute</a>
									</li>
									<li>
										<a
											href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
						+ "&requestType=vplan-folgetag"%>>Folgetag</a>
									</li>
								</ul>

							</span>
						</div>

						<div class="bereich">
							<input id="ac-3" name="akkordeon-1" type="radio">
							<label for="ac-3">
								<a
									href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
						+ "&requestType=schwarzes-brett"%>>Schwarzes
									Brett</a>
							</label>
							<span class="no-drop" />
						</div>
						<%
							}
						%>

						<%
							if (rechtHoehe == 2) {
						%>

						<div class="bereich">
							<input id="ac-4" name="akkordeon-1" type="radio">
							<label for="ac-4">
								<a
									href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
						+ "&requestType=it-team-melden"%>>IT-Team
									Melden</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>

						<%
							}
						%>
						<%
							if (rechtHoehe >= 3) {
						%>
						<div class="bereich">
							<input id="ac-4" name="akkordeon-1" type="radio">
							<label for="ac-4">
								<b>IT-Team</b>
							</label>
							<span>
								<ul>
									<li>
										<a
											href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
						+ "&requestType=it-team-melden"%>>Melden</a>
									</li>
									<li>
										<a href="">Abrufen</a>
									</li>
									<!--Als Lehrer nur als Einzelpunkt (nicht aufklappbar) anzeigen-->
								</ul>

							</span>
						</div>
						<div class="bereich">
							<input id="ac-5" name="akkordeon-1" type="radio">
							<label for="ac-5">
								<a href="">Feedback abrufen</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						<%
							}
						%>

						<%
							if (rechtHoehe >= 1) {
						%>
						<%
							if (!(benutzername.equals("Schueler") || benutzername.equals("Lehrer")
										|| benutzername.equals("ITTeam"))) {
						%>
						<div class="bereich">
							<input id="ac-6" name="akkordeon-1" type="radio">
							<label for="ac-6">
								<b>Administration</b>
							</label>
							<span>
								<ul>
									<li>
										<a
											href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
							+ "&requestType=einstellungen"%>>Einstellungen</a>
									</li>
									<li>
										<a
											href="/AMGAppServlet/visual?username=&password=&requestType=logout">Abmelden</a>
									</li>
								</ul>
							</span>
						</div>
						<%
							} else {
						%>
						<div class="bereich">
							<input id="ac-6" name="akkordeon-1" type="radio">
							<label for="ac-6">
								<a
									href="/AMGAppServlet/visual?username=&password=&requestType=logout">Abmelden</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						<%
							}
						%>
						<%
							}
						%>
						<%
							if (rechtHoehe == 0) {
						%>

						<div class="bereich">
							<input id="ac-6" name="akkordeon-1" type="radio">
							<label for="ac-6">
								<a
									href="/AMGAppServlet/visual?username=&password=&requestType=login">Login</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						<%
							}
						%>
					</div>
				</div>

			</div>




			<!-- ############################################################ -->
			<!-- content bereich -->
			<!-- ############################################################ -->


			<div class="section-content">
				<%
					if (vplan) {
				%>
				<div class="content vplan">
					<%
						} else {
					%>
					<div class="content">
						<%
							}
						%>
						<h2><%=title%></h2>
						<br />
						<jsp:include page="<%=content_file%>" />
					</div>
				</div>
				<!-- ############################## content ENDE ############################## -->



				<!-- ############################################################ -->
				<!-- fusstop bereich -->
				<!-- ############################################################ -->

				<div class="section-fusstop">
					<div class="fusstop">

						<div class="fusstopbox">

							<div class="inhalt">
								<h3>Kontakt</h3>
								<ul>
									<li>Albert Martmöller-Gymnasium</li>
									<li>Oberdorf 9</li>
									<li>58452 Witten</li>
									<br>

									<li>Telefon: +49 02302 189172</li>
									<li>Fax : +49 02302 189059</li>
									<li>
										<a href="mailto:amg@schule-witten.de">amg@schule-witten.de</a>
									</li>
								</ul>
							</div>

						</div>

						<div class="fusstopbox">

							<div class="fusstop-menu">
								<h3>Weitere Links</h3>
								<ul>
									<li>
										<a href="https://amg-witten.de/">Website der Schule</a>
									</li>
									<li>
										<a
											href="https://play.google.com/store/apps/details?id=www.amg_witten.de.apptest">AMG-App</a>
									</li>
								</ul>
							</div>

							<div class="fusstop-menu">
								<h3>Webmaster</h3>
								<ul>
									<li>Verantwortlich für diese Seite und ihre Inhalte ist</li>
									<li>
										<a href="mailto:amgwitten@gmail.com">Adrian Kathagen</a>
									</li>
								</ul>
							</div>

						</div>

						<div class="fusstopbox">
							<div class="fusstop-menu">
								<h3>Rechtliches</h3>
								<ul>
									<li>
										<a
											href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
					+ "&requestType=impressum"%>>Impressum</a>
									</li>
									<li>
										<a
											href=<%="/AMGAppServlet/visual?username=" + benutzername + "&password=" + passwort
					+ "&requestType=datenschutz"%>>Datenschutz</a>
									</li>
									<br />
									<li>Fotos &copy;Dr. Martin Krüger</li>
								</ul>
							</div>

						</div>

					</div>
				</div>

				<!-- ############################## fusstop ENDE ############################## -->


				<!-- ############################################################ -->
				<!-- fuss bereich -->
				<!-- ############################################################ -->

				<div class="section-fuss">
					<div class="fuss">

						<div class="fussbox-1a">
							<span class="fussname">&copy;2019 | AMG-Appsite</span>
						</div>

					</div>
				</div>
				<!-- ende fuss -->


				<!-- ############################## fuss ENDE ############################## -->



				<!-- ============================== ENDE HAUPTBEREICH  ============================== -->

			</div>
			<!-- ende wrapper-->
		</div>
		<!-- ende container-haupt-->

		<div id="preloader">
			<img src="images/1.jpg" width="1" height="1">
			<img src="images/2.jpg" width="1" height="1">
			<img src="images/3.jpg" width="1" height="1">
			<img src="images/4.jpg" width="1" height="1">
			<img src="images/5.jpg" width="1" height="1">
			<img src="images/6.jpg" width="1" height="1">
		</div>
</body>
</html>