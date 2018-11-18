package amgapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/melden")
public class Melden extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Melden() {
        super();
    }

	protected void doGet(HttpServletRequest HttpRequest, HttpServletResponse HttpResponse) throws ServletException, IOException {
		String benutzername = "";
		String passwort = "";
		String datum = "";
		String gebaeude = "";
		String etage = "";
		String raum = "";
		String wichtigkeit = "";
		String fehler = "";
		String beschreibung = "";
		String status = "";
		String bearbeitetVon = "";
		boolean ausfuehrFehler = false;
		try {
			benutzername = HttpRequest.getParameter("username").replaceAll("%20", " ");
			passwort = HttpRequest.getParameter("password").replaceAll("%20", " ").hashCode()+"";
			datum = HttpRequest.getParameter("datum").replaceAll("%20", " ");
			System.out.println(datum);
			gebaeude = HttpRequest.getParameter("gebaeude").replaceAll("%20", " ");
			System.out.println(gebaeude);
			etage = HttpRequest.getParameter("etage").replaceAll("%20", " ");
			System.out.println(etage);
			raum = HttpRequest.getParameter("raum").replaceAll("%20", " ");
			System.out.println(raum);
			wichtigkeit = HttpRequest.getParameter("wichtigkeit").replaceAll("%20", " ");
			System.out.println(wichtigkeit);
			fehler = HttpRequest.getParameter("fehler").replaceAll("%20", " ");
			System.out.println(fehler);
			beschreibung = HttpRequest.getParameter("beschreibung").replaceAll("%20", " ");
			System.out.println(beschreibung);
			status = HttpRequest.getParameter("status").replaceAll("%20", " ");
			System.out.println(status);
			bearbeitetVon = HttpRequest.getParameter("bearbeitetVon").replaceAll("%20", " ");
			System.out.println(bearbeitetVon);
		}
		catch (NumberFormatException ex) {
			ausfuehrFehler = true;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String returnString = Lite.transact("ITTeamMelden", "", benutzername, passwort, datum, gebaeude, etage, raum, wichtigkeit, fehler, beschreibung, status, bearbeitetVon);
			HttpRequest.setAttribute("responsed", returnString);
			HttpRequest.setAttribute("fehler", ausfuehrFehler);
			getServletContext().getRequestDispatcher("/ergebnis.jsp").forward(HttpRequest, HttpResponse);
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
