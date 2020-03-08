package amgapp;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/visual")
public class Visual extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Visual() {
        super();
    }

	protected void doGet(HttpServletRequest HttpRequest, HttpServletResponse HttpResponse) throws ServletException, IOException {
		String requestType = "";
		String benutzername = "";
		String passwort = "";
		try {
			benutzername = URLDecoder.decode(HttpRequest.getParameter("username").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			passwort = URLDecoder.decode(HttpRequest.getParameter("password").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			requestType = URLDecoder.decode(HttpRequest.getParameter("requestType").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
		}
		catch (NullPointerException ignored) {}
		
		System.out.println(benutzername+" requests "+requestType);
		
		try {
			HttpRequest.setAttribute("benutzername", benutzername);
			HttpRequest.setAttribute("passwort", passwort);
			HttpRequest.setAttribute("rechtHoehe", Lite.getRechthoehe(benutzername, passwort.hashCode()+""));
			HttpRequest.setAttribute("imageForHeader", Math.round((Math.random()*5)+1)+".jpg");
			HttpRequest.setAttribute("vplan", false);
			if(benutzername.equals("")) {
				if(requestType.equals("logout")) {
					HttpRequest.setAttribute("content_file", "/logout.html");
					HttpRequest.setAttribute("title", "Logout");
					getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
					return;
				}
				else if(requestType.equals("login")) {
					HttpRequest.setAttribute("title", "Login");
					HttpRequest.setAttribute("content_file", "/login.html");
					getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
					return;
				}
				else if(requestType.equals("register")) {
					HttpRequest.setAttribute("title", "Registrieren");
					HttpRequest.setAttribute("content_file", "/register.html");
					getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
					return;
				}
				else if(requestType.equals("datenschutz")) {
					HttpRequest.setAttribute("title", "Datenschutz");
					HttpRequest.setAttribute("content_file", "/datenschutz.jsp");
					getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
					return;
				}
				else if(requestType.equals("impressum")) {
					HttpRequest.setAttribute("title", "Impressum");
					HttpRequest.setAttribute("content_file", "/impressum.jsp");
					getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
					return;
				}
				else {
					HttpRequest.setAttribute("title", "Startseite");
					HttpRequest.setAttribute("content_file", "startseite_content.jsp");
					getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
					return;
				}
			}
			else if(requestType.equals("datenschutz")) {
				HttpRequest.setAttribute("title", "Datenschutz");
				HttpRequest.setAttribute("content_file", "/datenschutz.jsp");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("impressum")) {
				HttpRequest.setAttribute("title", "Impressum");
				HttpRequest.setAttribute("content_file", "/impressum.jsp");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("")) {
				HttpRequest.setAttribute("title", "Startseite");
				HttpRequest.setAttribute("content_file", "startseite_content.jsp");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("register")) {
				HttpRequest.setAttribute("title", "Registrieren");
				HttpRequest.setAttribute("content_file", "/register.html");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("vplan-heute")) {
				VPlan vPlan = new VPlan();
				String[] settings = Lite.transact("einstellungen", "select * from einstellungen where benutzername = \""+benutzername+"\";", benutzername, passwort.hashCode()+"").split("//");
				vPlan.setKlasse(settings[0]);
				vPlan.setSettings(settings[1],settings[2],settings[3],settings[4],settings[5]);
				String vplan = vPlan.getVPlan(Lite.doLogin(), "Heute");
				HttpRequest.setAttribute("responsed", vplan);
				HttpRequest.setAttribute("content_file", "/ergebnis.jsp");
				HttpRequest.setAttribute("fehler", false);
				HttpRequest.setAttribute("vplan", true);
				HttpRequest.setAttribute("title", "Vertretungsplan Heute");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("vplan-folgetag")) {
				VPlan vPlan = new VPlan();
				String[] settings = Lite.transact("einstellungen", "select * from einstellungen where benutzername = \""+benutzername+"\";", benutzername, passwort.hashCode()+"").split("//");
				vPlan.setKlasse(settings[0]);
				vPlan.setSettings(settings[1],settings[2],settings[3],settings[4],settings[5]);
				String vplanString = vPlan.getVPlan(Lite.doLogin(), "Folgetag");
				HttpRequest.setAttribute("responsed", vplanString);
				HttpRequest.setAttribute("content_file", "/ergebnis.jsp");
				HttpRequest.setAttribute("fehler", false);
				HttpRequest.setAttribute("vplan", true);
				HttpRequest.setAttribute("title", "Vertretungsplan Folgetag");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("schwarzes-brett")) {
				String settings = Lite.transact("einstellungen", "select * from einstellungen where benutzername = \""+benutzername+"\";", benutzername, passwort.hashCode()+"");
				String klasse = settings.split("//")[0];
				String[] array = Lite.transact("SchwarzesBrett", "select * from schwarzesBrett where eingeblendet=\"true\" AND stufe=\""+klasse+"\";", benutzername, passwort.hashCode()+"", "").split("/newthing/");
				ArrayList<String> list = new ArrayList<String>(Arrays.asList(array));
				list.remove(0);
				for(int i=0;i<list.size();i++) {
					String entry = list.get(i);
					String endString = entry.split("//Enddatum: ")[1].split("//")[0];
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endString);
                    Date currentDate = new Date();

                    if(endDate.before(currentDate)){
                        list.remove(i);
                        i--;
                    }
				}
				HttpRequest.setAttribute("eintraege", list);
				HttpRequest.setAttribute("content_file", "/schwarzes-brett.jsp");
				HttpRequest.setAttribute("title", "Schwarzes Brett");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("it-team-melden")) {
				HttpRequest.setAttribute("content_file", "/meldenformular.jsp");
				HttpRequest.setAttribute("title", "IT-Team Melden");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("it-team-melden-senden")) {
				String datum = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
				String gebaeude = HttpRequest.getParameter("gebaeude");
				String etage = HttpRequest.getParameter("etage");
				String raum = HttpRequest.getParameter("raum");
				String wichtigkeit = HttpRequest.getParameter("wichtigkeit");
				String fehler = HttpRequest.getParameter("fehler");
				String beschreibung = HttpRequest.getParameter("beschreibung");
				
				Lite.transact("ITTeamMelden", "", benutzername, passwort.hashCode()+"", datum, gebaeude, etage, raum, wichtigkeit, fehler, beschreibung, "Offen", "Keiner");
				getServletContext().getRequestDispatcher("/gemeldet.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("einstellungen")) {
				String settings = Lite.transact("einstellungen", "select * from einstellungen where benutzername = \""+benutzername+"\";", benutzername, passwort.hashCode()+"");
				HttpRequest.setAttribute("aktuelleKlasse", settings.split("//")[0]);
				HttpRequest.setAttribute("farbeEigeneKlasse", settings.split("//")[1]);
				HttpRequest.setAttribute("farbeUnterstufe", settings.split("//")[2]);
				HttpRequest.setAttribute("farbeMittelstufe", settings.split("//")[3]);
				HttpRequest.setAttribute("farbeOberstufe", settings.split("//")[4]);
				HttpRequest.setAttribute("iconsImVertretungsplan", settings.split("//")[5].equals("true"));
				HttpRequest.setAttribute("content_file", "einstellungen.jsp");
				HttpRequest.setAttribute("title", "Einstellungen");
				getServletContext().getRequestDispatcher("/main_layout.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("saveEinstellungen")) {
				String aktuelleKlasse = URLDecoder.decode(HttpRequest.getParameter("aktuelleKlasse"),"utf-8");
				boolean iconsImVertretungsplan;
				try {
					iconsImVertretungsplan = URLDecoder.decode(HttpRequest.getParameter("iconsImVertretungsplan"),"utf-8").equals("on");
				}
				catch (NullPointerException e) {
					iconsImVertretungsplan = false;
				}
				String farbeEigeneKlasse = URLDecoder.decode(HttpRequest.getParameter("farbeEigeneKlasse"),"utf-8");
				String farbeUnterstufe = URLDecoder.decode(HttpRequest.getParameter("farbeUnterstufe"),"utf-8");
				String farbeMittelstufe = URLDecoder.decode(HttpRequest.getParameter("farbeMittelstufe"),"utf-8");
				String farbeOberstufe = URLDecoder.decode(HttpRequest.getParameter("farbeOberstufe"),"utf-8");
				
				Lite.doSaveEinstellungen(benutzername, aktuelleKlasse,iconsImVertretungsplan,farbeEigeneKlasse,farbeUnterstufe,farbeMittelstufe, farbeOberstufe);
				
				getServletContext().getRequestDispatcher("/einstellungen_gespeichert.jsp").forward(HttpRequest, HttpResponse);
				return;
			}
			else if(requestType.equals("register-send")) {
				String name = URLDecoder.decode(HttpRequest.getParameter("name"),"utf-8");
				String email = URLDecoder.decode(HttpRequest.getParameter("email"),"utf-8");
				String wished_username = URLDecoder.decode(HttpRequest.getParameter("wished_username"),"utf-8");
				String wished_password = URLDecoder.decode(HttpRequest.getParameter("wished_password"),"utf-8");
				String wished_password_repeat = URLDecoder.decode(HttpRequest.getParameter("wished_password_repeat"),"utf-8");
				
				Lite.sendRegisterMail(benutzername, passwort, Lite.getRechthoehe(benutzername, passwort), name, email, wished_username, wished_password, wished_password_repeat);
				
				getServletContext().getRequestDispatcher("/registration-sent.html").forward(HttpRequest, HttpResponse);
				
				return;
			}
			else {
				System.out.println("REQUEST TYPE NOT FOUND: "+requestType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public String getServletInfo() {
		return "Short description";
	}

}
