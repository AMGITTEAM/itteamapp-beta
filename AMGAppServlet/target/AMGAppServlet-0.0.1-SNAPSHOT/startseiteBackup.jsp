<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="rechtHoehe" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="imageForHeader" scope="request" type="java.lang.String"/>
<jsp:useBean id="benutzername" scope="request" type="java.lang.String"/>
<jsp:useBean id="passwort" scope="request" type="java.lang.String"/>

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
<title>homepage, dokument, webpage, page, web, netz, homepage
	dokument webpage page web netz</title>

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



</head>


<body>

	<div class="container_haupt" id="top">
		<div class="wrapper">


			<!-- ============================== HAUPTBEREICH ============================== -->

			<div class="section-logo" style="background-image: url(images/<%=imageForHeader%>)">

				<!-- name mit position relative/absolute angelegt  -->
				<div class="wrapper-name filter">
					<span class="name">
						How To Start A New
						<br>
						<b>Creative Project</b>
						<br>
						- Home -
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
								<a href=<%="/AMGAppServlet/visual?username="+benutzername+"&password="+passwort%>>Startseite</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						
						<% if(rechtHoehe>=1) {%>
						<div class="bereich">
							<input id="ac-2" name="akkordeon-1" type="radio">
							<label for="ac-2">
								<b>Vertretungsplan</b>
							</label>
							<span>
								<ul>
									<li>
										<a href=<%="/AMGAppServlet/amgapp?requestType=vplan&request=Heute&username="+benutzername+"&password="+passwort.hashCode()%>>Heute</a>
									</li>
									<li>
										<a href=<%="/AMGAppServlet/amgapp?requestType=vplan&request=Folgetag&username="+benutzername+"&password="+passwort.hashCode()%>>Folgetag</a>
									</li>
								</ul>

							</span>
						</div>

						<div class="bereich">
							<input id="ac-3" name="akkordeon-1" type="radio">
							<label for="ac-3">
								<a href="">Schwarzes Brett</a>
							</label>
							<span class="no-drop" />
						</div>
						<%}%>
						
						<%if(rechtHoehe==2){ %>
						
						<div class="bereich">
							<input id="ac-4" name="akkordeon-1" type="radio">
							<label for="ac-4">
								<a href="index1.html">IT-Team Melden</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						
						<%} %>
						<%if(rechtHoehe>=3){ %>
						<div class="bereich">
							<input id="ac-4" name="akkordeon-1" type="radio">
							<label for="ac-4">
								<b>IT-Team</b>
							</label>
							<span>
								<ul>
									<li>
										<a href="index1.html">Melden</a>
									</li>
									<li>
										<a href="index2.html">Abrufen</a>
									</li>
									<!--Als Lehrer nur als Einzelpunkt (nicht aufklappbar) anzeigen-->
								</ul>

							</span>
						</div>
						<div class="bereich">
							<input id="ac-5" name="akkordeon-1" type="radio">
							<label for="ac-5">
								<a href="index1.html">Feedback abrufen</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						<%} %>
						
						<%if(rechtHoehe>=1){ %>
						<%if(!(benutzername.equals("Schueler") || benutzername.equals("Lehrer") || benutzername.equals("ITTeam"))){ %>
						<div class="bereich">
							<input id="ac-6" name="akkordeon-1" type="radio">
							<label for="ac-6">
								<b>Administration</b>
							</label>
							<span>
								<ul>
									<li>
										<a href=<%="/AMGAppServlet/visual?username="+benutzername+"&password="+passwort+"&requestType=einstellungen"%>>Einstellungen</a>
									</li>
									<li>
										<a href="/AMGAppServlet/visual?username=&password=&requestType=logout">Abmelden</a>
									</li>
								</ul>
							</span>
						</div>
						<%} else {%>
						<div class="bereich">
							<input id="ac-6" name="akkordeon-1" type="radio">
							<label for="ac-6">
								<a href="/AMGAppServlet/visual?username=&password=&requestType=logout">Abmelden</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						<%}%>
						<%}%>
						<%if(rechtHoehe==0) {%>

						<div class="bereich">
							<input id="ac-6" name="akkordeon-1" type="radio">
							<label for="ac-6">
								<a href="/">Login</a>
							</label>
							<span class="no-drop">&nbsp;</span>
						</div>
						<%} %>
					</div>
				</div>

			</div>




			<!-- ############################################################ -->
			<!-- content bereich -->
			<!-- ############################################################ -->



			<div class="section-content">
				<div class="content">

					<h2>Informationen</h2>
					<h3>Template</h3>
					Dieses Template hei&szlig;t '
					<i>Start A New Creative Project</i>
					'. Die Seiten 'Home' (diese Seite) sowie die Seiten beim
					Aufklapp-Men&uuml;, welche Sie bei 'Untermen&uuml;-A' und
					'Untermen&uuml;-B' finden, sind bereits als Beispiel verlinkt.
					Bitte klicken Sie diese .
					<br>
					<br>
					Oberhalb und unterhalb vom Homepagenamen sehen sie eine Linie mit
					Font-Icons, hier kleine Sternchen. Dieses Font-Icon k&ouml;nnen Sie
					auch durch ein beliebig anderes Font-Icon ersetzen, denn die
					Font-Icon-Schrift 'Font Awesome' ist ja bereits installiert. Dies
					geschieht alleine &uuml;ber die CSS-Datei format.css und die
					&Auml;nderungen dort gelten dann automatisch f&uuml;r alle
					HTML-Seiten.
					<br>
					<br>

					<h3>Das Men&uuml;</h3>
					Das Aufklapp-Men&uuml; ist responsive und nur per CSS erstellt. Es
					funktioniert ganz ohne Javascript und ist au&szlig;erdem
					'touch-friendly', d.h. es l&auml;uft auch auf mobilen Ger&auml;ten.
					Hinweis dazu: Normalerweise wird die Aufklapp-Funktion ja per
					Mausber&uuml;hrung (hover) get&auml;tigt. Aber mobile Ger&auml;te
					kennen eben keine Maus sondern nur Touch.
					<div class="box-text-re">
						<img src="images/picture01.jpg" width="800" height="530" alt="">
					</div>
					Unsere L&ouml;sung beruht auf der sog. 'Toggle-Funktion per
					Checkbox-Hack'.
					<br>
					<br>

					Bei gr&ouml;&szlig;eren Aufl&ouml;sungen wird das
					Aufklapp-Men&uuml; mit 6 Buttons, welche nebeneinander stehen und
					einen leichten Farbverlauf haben, dargestellt. Bei mobilen
					Aufl&ouml;sungen erscheint nur der sog. Hamburger Button (3
					Striche, quer als Men&uuml;-Symbol). Um das Men&uuml; dort zu
					&ouml;ffen, klicken Sie bitte auf diesen Button Aus den 6 Buttons
					wird bei mobilen Aufl&ouml;sungen ein sog. Akkordeon-Men&uuml;,
					welches sehr splatzsparend ist.
					<br>
					<br>
					<br>
					<br>
					Vide quidam cotidieque ut vel. An illud iriure tamquam mea. Porro
					doming ex duo, mucius invenire eu his. Ne aliquando conclusionemque
					has. Quo te zril labore delicata, eos te debet elaboraret. Sit
					nonumy ornatus ut. Sea ea vero liber maluisset, pro deserunt
					pertinax ex. Vis noster liberavisse ad, et mei reque constituam.
					His melius commune an, in dolore persius sed, ferri apeirian eu
					est. Solet scripta aliquam te est, debet prompta ea has, facilis
					sapientem cu duo. Vel doming audiam accommodare ei, has agam
					tamquam ne, quem harum melius vel eu.


					<p class="break">&nbsp;</p>




					<h2>Responsive Webdesign</h2>

					<h3>Responsive Webdesign</h3>
					Responsive Webdesign (RWD) erm&ouml;glicht eine Anpassung bzw.
					Optimierung der unterschiedlichen Bildschirmgr&ouml;&szlig;en von
					Smartphone, Tablet, Notebook, Laptop und Desktop-PC innerhalb einer
					einzigen Webseite. Dazu werden per CSS-Media Queries die
					verschiedenen Bildschirmgr&ouml;ssen abgefragt.
					<div class="box-text-li">
						<img src="images/picture05.jpg" width="800" height="530" alt="">
					</div>
					Somit kann man auch auf das Hoch- oder Querformat eines mobilen
					Ger&auml;tes reagieren. Au&szlig;erdem wird beim Layout
					verst&auml;rkt mit Angaben in Prozent gearbeitet.
					<br>
					<br>
					<h3>So testen Sie</h3>
					Hier auf der Seite
					<a href="index1.html">index1.html</a>
					(Primus) nennen wir Ihnen gute M&ouml;glichkeiten ein
					Responsive-Layout in verschiedenen Aufl&ouml;sungen zu testen, denn
					nicht jeder hat ja diverse Smartphones und Tablets zu Hause. Diese
					Responsive-Vorlage verwendet keinerlei Javascript.
					<br>
					<br>
					Vide quidam cotidieque ut vel. An illud iriure tamquam mea. Porro
					doming ex duo, mucius invenire eu his. Ne aliquando conclusionemque
					has. Quo te zril labore delicata, eos te debet elaboraret. Sit
					nonumy ornatus ut. Sea ea vero liber maluisset, pro deserunt
					pertinax ex. Vis noster liberavisse ad, et mei reque constituam.
					His melius commune an, in dolore persius sed, ferri apeirian eu
					est. Solet scripta aliquam te est, debet prompta ea has, facilis
					sapientem cu duo. Vel doming audiam accommodare ei, has agam
					tamquam ne, quem harum melius vel eu.


					<p class="break">&nbsp;</p>

					<h2>Die Technik</h2>
					<h3>Einbau der Bilder</h3>
					Bei den
					<i>Fotos im Inhaltsbereich</i>
					haben wir uns f&uuml;r die CSS-Technik 'float' entschieden.
					<br>
					<br>
					Hierf&uuml;r haben wir drei Beispiele angelegt. Einmal ist das Bild
					links platziert und der Text flie&szlig;t rechts herum, ein anderes
					Mal ist das Bild rechts platziert und der Text flie&szlig;t links
					herum. Als drittes steht dann ein Bild mittig ohne Textflu&szlig;.
					Wir beschreiben es Ihnen hier in einem Tutorial auf der Seite '
					<a href="index4.html">Quartus</a>
					' (index4.html)
					<br>
					<br>
					<h3>Die Transparenz</h3>
					Genau genommen muss man hier von einer Teil-Transparenz sprechen.
					Damit Text im Kopfbereich (hier der Homepagename) auch mit
					dahinterliegendem Foto noch gut zu lesen ist, liegt auf dem
					Homepagenamen-Feld noch ein Transparenz-Filter. Per CSS-rgba. D.h.
					es k&ouml;nnen auch Farbe der Transparenz sowie auch der
					Deckungsgrad ver&auml;ndert werden. Dies nur zur Info, falls Sie
					Ihre eigenen Fotos einsetzen wollen.
					<br>
					<br>
					Beim 'rgba-Befehl' k&ouml;nnen Sie Farbe und Deckungsgrad auch
					anders einstellen (mit Werten von 0 bis 1, wobei gilt:
					0=volltransparent und 1= gar keine Transparenz). Dazwischen gelten
					die eigentlich interessanten Werte von 0.1 bis 0.9. Ein Beispiel
					f&uuml;r die Deckungskraft von 70% ist der Wert 0.7, d.h. das ein
					Objekt zu 30% transparent ist. Ein Beispiel f&uuml;r die
					Deckungskraft von 20% ist der Wert 0.2, d.h. das ein Objekt zu 80%
					transparent ist.


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
								<li>Fa. Michael, Mustermann & Sohn</li>
								<li>12345 Frankenburg</li>
								<li>Am sch&ouml;nen Gro&szlig;markt 18</li>
								<br>

								<li>Phone: (0000) 2468246</li>
								<li>Mobile : (+49) 0000-1234567</li>
								<li>
									<a href="#">infokontaktmustersohn@mail.de</a>
								</li>
							</ul>
						</div>

					</div>

					<div class="fusstopbox">

						<div class="fusstop-menu">
							<h3>More Links</h3>
							<ul>
								<li>
									<a href="index.html">Lorem Ipsum</a>
								</li>
								<li>
									<a href="index.html">Nomen est omen</a>
								</li>
								<li>
									<a href="index.html">Opes Regum corda subdit</a>
								</li>
								<li>
									<a href="index.html">Sapienti Sat Est</a>
								</li>
								<li>
									<a href="index.html">Omnia Vincit Amor</a>
								</li>
								<li>
									<a href="index.html">Bella Mia Terra Luna Sol</a>
								</li>
								<li>
									<a href="index.html">Sapienti Sat Est</a>
								</li>
								<li>
									<a href="index.html">Omnia Vincit Amor</a>
								</li>
								<li>
									<a href="index.html">Bella Mia Terra Luna Sol</a>
								</li>
							</ul>
						</div>

					</div>

					<div class="fusstopbox">
						<div class="fusstop-menu">
							<h3>Legal Conditions</h3>
							<ul>
								<li>
									<a href="index.html">Impressum</a>
								</li>
								<li>
									<a href="index.html">Datenschutz</a>
								</li>
								<li>
									<a href="index.html">Disclaimer</a>
								</li>
								<li>
									<a href="index.html">Lizenzbedingungen</a>
								</li>
								<li>
									<a href="index.html">AGB</a>
								</li>
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
						<span class="fussname">&copy;2040 | Start A New Project</span>
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



</body>
</html>