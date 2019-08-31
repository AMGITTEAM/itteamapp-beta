package amgapp;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/amgapp")
public class ErstesServletKlasse extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ErstesServletKlasse() {
        super();
    }

	protected void doGet(HttpServletRequest HttpRequest, HttpServletResponse HttpResponse) throws ServletException, IOException {
		String requestType = "";
		String request = "";
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
			requestType = URLDecoder.decode(HttpRequest.getParameter("requestType").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(requestType);
			request = URLDecoder.decode(HttpRequest.getParameter("request").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(request);
			benutzername = URLDecoder.decode(HttpRequest.getParameter("username").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(request);
			passwort = URLDecoder.decode(HttpRequest.getParameter("password").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(request);
			datum = URLDecoder.decode(HttpRequest.getParameter("datum").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(datum);
			gebaeude = URLDecoder.decode(HttpRequest.getParameter("gebaeude").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(gebaeude);
			etage = URLDecoder.decode(HttpRequest.getParameter("etage").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(etage);
			raum = URLDecoder.decode(HttpRequest.getParameter("raum").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(raum);
			wichtigkeit = URLDecoder.decode(HttpRequest.getParameter("wichtigkeit").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(wichtigkeit);
			fehler = URLDecoder.decode(HttpRequest.getParameter("fehler").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(fehler);
			beschreibung = URLDecoder.decode(HttpRequest.getParameter("beschreibung").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(beschreibung);
			status = URLDecoder.decode(HttpRequest.getParameter("status").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
			System.out.println(status);
			bearbeitetVon = URLDecoder.decode(HttpRequest.getParameter("bearbeitetVon").replaceAll("%20", " ").replaceAll("%30", "\n"),"utf-8");
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
			String returnString = Lite.transact(requestType, request, benutzername, passwort, datum, gebaeude, etage, raum, wichtigkeit, fehler, beschreibung, status, bearbeitetVon);
			if(!requestType.equals("vplan")) {
				System.out.println(requestType+" not "+"vplan");
				returnString = URLEncoder.encode(returnString.replaceAll("\n","//"), "utf-8");
			}
			System.out.println(returnString);
			HttpRequest.setAttribute("responsed", returnString);
			HttpRequest.setAttribute("fehler", ausfuehrFehler);
			if(requestType.equals("HTMLRequest")) {
				getServletContext().getRequestDispatcher("/htmlrequest.jsp").forward(HttpRequest, HttpResponse);
			}
			else {
				getServletContext().getRequestDispatcher("/ergebnis.jsp").forward(HttpRequest, HttpResponse);
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
