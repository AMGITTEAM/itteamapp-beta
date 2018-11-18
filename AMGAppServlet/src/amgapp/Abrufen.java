package amgapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/abrufen")
public class Abrufen extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Abrufen() {
        super();
    }

	protected void doGet(HttpServletRequest HttpRequest, HttpServletResponse HttpResponse) throws ServletException, IOException {
		String benutzername = "";
		String passwort = "";
		String request = "SELECT * FROM fehlermeldungen;";
		boolean ausfuehrFehler = false;
		try {
			benutzername = HttpRequest.getParameter("username").replaceAll("%20", " ");
			passwort = HttpRequest.getParameter("password").replaceAll("%20", " ").hashCode()+"";
			try {
				request = HttpRequest.getParameter("request").replaceAll("%20", " ");
			}
			catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
		catch (NumberFormatException ex) {
			ausfuehrFehler = true;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String returnString = Lite.transact("ITTeamHolen", request, benutzername, passwort);
			returnString = returnString.replaceAll("/newthing/", "<br/><br/><br/>");
			returnString = returnString.replaceAll("//", "<br/>");
			System.out.println(returnString);
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
