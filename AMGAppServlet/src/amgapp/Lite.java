package amgapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Lite {
	static String path = "jdbc:sqlite:/home/ak/AMGApp.db";

	public static String transact(String request, String req, String benutzername, String passwort, String... args) throws Exception{
		String returnString = "";
		try {
			System.out.println(request);
			if(request.equals("Login")){
				int rechthoehe = getRechthoehe(benutzername,passwort);
				returnString = ""+rechthoehe;
				if(rechthoehe>=1) {
					String passwordVertretungsplanSchueler="";
					try {
						passwordVertretungsplanSchueler=doLogin(args);
					}
					catch(NumberFormatException | SQLException | ClassNotFoundException e) {
						e.printStackTrace();
					}
					returnString+="\n"+passwordVertretungsplanSchueler;
				}
			}
			else if(request.equals("ITTeamMelden")){
				if(getRechthoehe(benutzername,passwort)>=2) {
					if(getRechthoehe(benutzername,passwort)==100) {
						returnString=true+"";
					}
					else {
						returnString+=doITTeamMelden(args);
					}
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("ITTeamHolen")){
				if(getRechthoehe(benutzername,passwort)>=3) {
					returnString+=doITTeamHolen(req);
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("FeedbackHolen")){
				if(getRechthoehe(benutzername,passwort)>=3) {
					returnString+=doFeedbackHolen(req);
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("ITTeamLoeschen")){
				if(getRechthoehe(benutzername,passwort)>=3) {
					if(getRechthoehe(benutzername,passwort)==100) {
						returnString=true+"";
					}
					else {
						returnString+=doITTeamLoeschen(req);
					}
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("Feedback")) {
				if(getRechthoehe(benutzername,passwort)>=1) {
					if(getRechthoehe(benutzername,passwort)==100) {
						returnString=true+"";
					}
					else {
						returnString+=doFeedback(args);
					}
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("HTMLRequest")) {
				if(getRechthoehe(benutzername,passwort)>=1) {
					returnString+=doHTMLRequest(req);
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("KurssprecherRequest")) {
				if(getRechthoehe(benutzername,passwort)>=1) {
					returnString+=doKurssprecherRequest(args);
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("GebaeudefehlerMelden")) {
				if(getRechthoehe(benutzername,passwort)>=2) {
					doGebaeudefehlerMelden(args);
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("vplan")) {
				if(getRechthoehe(benutzername,passwort)>=1) {
					String pwd = doLogin(args);
					returnString = VPlan.getVPlan(pwd,req);
				}
			}
			else {
				System.out.println("Request nicht gefunden: "+request);
			}
		}
		catch (NullPointerException e){
			System.out.println("Internet überprüfen");
		}
		catch (Exception e){
			returnString = "Error: "+e;
			e.printStackTrace();
		}
		System.out.println("---------------------");
		
		return returnString;

	}
	
	public static int getRechthoehe(String benutzername, String passwort) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(path);
		    Statement stat = conn.createStatement();
		    String req = "select * from login where benutzername=\""+benutzername+"\" and passwort=\""+passwort+"\"";
		    System.out.println(req);
			ResultSet rs2 = stat.executeQuery(req);
			System.out.println(rs2.getString("rechthoehe"));
			int returns = Integer.parseInt(rs2.getString("rechthoehe"));
			rs2.close();
			stat.close();
			conn.close();
			return returns;
		}
		catch(NumberFormatException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void sendEMail(String msg) {
		// Recipient's email ID needs to be mentioned.
	      String to = "5958456@gmail.com";
	 
	      // Sender's email ID needs to be mentioned
	      String from = "amgwitten@gmail.com";
	 
	      // Assuming you are sending email from localhost
	      String host = "localhost";
	 
	      // Get system properties
	      Properties properties = System.getProperties();
	 
	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	 
	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);
	         
	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));
	         
	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	         
	         // Set Subject: header field
	         message.setSubject("AMG-App: Schadensmeldung","UTF-8");
	         
	         // Now set the actual message
	         message.setText(msg,"UTF-8");
	         
	         Transport.send(message);
	         
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
		
	}

	public static String doLogin(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(path);
	    Statement stat = conn.createStatement();
	    String req2 = "select * from passwords where benutzername=\"Schueler\"";
	    System.out.println(req2);
		ResultSet rs2 = stat.executeQuery(req2);
		System.out.println(rs2.getString("password"));
		String passwordVertretungsplanSchueler = rs2.getString("password");
		rs2.close();
		stat.close();
		conn.close();
		
		return passwordVertretungsplanSchueler;
	}
	
	public static String doITTeamMelden(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String zulBearb=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
		
		String datum = args[0];
		String gebaeude=args[1];
		String etage=args[2];
		String raum=args[3];
		String wichtigk=args[4];
		String fehler=args[5];
		String beschr=args[6].replaceAll("//", "\n");
		String status=args[7];
		String bearbVon=args[8];
		
		for (String arg:args) {
			System.out.println(arg);
		}
		
		Class.forName("org.sqlite.JDBC");
	    Connection conn = DriverManager.getConnection(path);
	    Statement stat = conn.createStatement();
	    stat.executeUpdate("CREATE TABLE IF NOT EXISTS fehlermeldungen (datum, zuletztBearbeitet, gebaeude, etage, raum, wichtigkeit, fehler, beschr, status, bearbVon);");
	    PreparedStatement prep = conn.prepareStatement("insert into fehlermeldungen values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
	    
	    prep.setString(1, datum);
	    prep.setString(2, zulBearb);
	    prep.setString(3, gebaeude);
	    prep.setString(4, etage);
	    prep.setString(5, raum);
	    prep.setString(6, wichtigk);
	    prep.setString(7, fehler);
	    prep.setString(8, beschr);
	    prep.setString(9, status);
	    prep.setString(10, bearbVon);
	    
	    int result = prep.executeUpdate();
	    prep.close();
	    stat.close();
	    conn.close();
	    boolean success = true;
	    if(result<=0||result==PreparedStatement.EXECUTE_FAILED) {
	    	success=false;
	    }
	    System.out.println(result);
	    return success+"";
	}
	
	public static String doITTeamHolen(String req) throws SQLException, ClassNotFoundException {
		String returnString = "";
		
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(path);
	    Statement stat = conn.createStatement();
	    System.out.println("SELECT * FROM fehlermeldungen");
		ResultSet rs2 = stat.executeQuery(req);
		boolean ready=false;
		int rowcount = 0;
		while (!ready){
			if (!rs2.next()) {
				ready=true;
			}
			else {
				rowcount++;
			}
		}
		System.out.println(rowcount);
		returnString += rowcount+"/newthing/";
		ResultSet rs = stat.executeQuery(req);
	    while (rs.next()){
	      returnString += "Datum: "+rs.getString("datum")+"//Zuletzt bearbeitet: "+rs.getString("zuletztBearbeitet")+"//Gebaeude: "+rs.getString("gebaeude")+"//Etage: "+rs.getString("etage")+"//Raum: "+rs.getString("raum")+"//Wichtigkeit: "+rs.getString("wichtigkeit")+"//Fehler: "+rs.getString("fehler")+"//Beschreibung: "+rs.getString("beschr")+"//Status: "+rs.getString("status")+"//Zuletzt bearbeitet von: "+rs.getString("bearbVon")+"/newthing/";
	    }
	    rs.close();
	    rs2.close();
	    stat.close();
	    conn.close();
	    
	    return returnString;
	}
	
	public static String doFeedbackHolen(String req) throws SQLException, ClassNotFoundException {
		String returnString = "";
		
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(path);
	    Statement stat = conn.createStatement();
	    System.out.println("SELECT * FROM feedback");
		int rowcount = 0;
		String returnStrin = "";
		ResultSet rs = stat.executeQuery(req);
	    while (rs.next()){
	    	rowcount++;
	    	returnStrin += "Typ: "+rs.getString("type")+"//Beschreibung: "+rs.getString("description")+"/newthing/";
	    	System.out.println(rowcount+"Typ: "+rs.getString("type")+"//Beschreibung: "+rs.getString("description")+"/newthing/");
	    }
	    rs.close();
		returnString += rowcount+"/newthing/";
		System.out.println(rowcount);
		returnString += returnStrin;
	    stat.close();
	    conn.close();
	    
	    return returnString;
	}
	
	public static String doITTeamLoeschen(String req) throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Connection c = DriverManager.getConnection(path);
		c.setAutoCommit(false);
		Statement stmt = c.createStatement();
		String decoded = (URLDecoder.decode(req,"utf-8"));
		System.out.println(decoded);
		stmt.executeUpdate(decoded);
		c.commit();
		stmt.close();
		c.close();
		return "true";
	}
	
	public static String doFeedback(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		
		String type = args[0];
		String description = args[1];
		
		for (String arg:args) {
			System.out.println(arg);
		}
		
		Class.forName("org.sqlite.JDBC");
	    Connection conn = DriverManager.getConnection(path);
	    Statement stat = conn.createStatement();
	    stat.executeUpdate("CREATE TABLE IF NOT EXISTS feedback (type, description);");
	    PreparedStatement prep = conn.prepareStatement("insert into feedback values (?, ?);");
	    
	    prep.setString(1, type);
	    prep.setString(2, description);
	    
	    int result = prep.executeUpdate();
	    prep.close();
	    stat.close();
	    conn.close();
	    boolean success = true;
	    if(result<=0||result==PreparedStatement.EXECUTE_FAILED) {
	    	success=false;
	    }
	    System.out.println(result);
	    return success+"";
	}
	
	public static String doHTMLRequest(String req) throws ClassNotFoundException, SQLException, IOException {
		String returnString = "";

		if(req.contains("http://amg-witten.de/fileadmin/VertretungsplanSUS")) {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(path);
		    Statement stat = conn.createStatement();
		    String req2 = "select * from passwords where benutzername=\"Schueler\"";
		    System.out.println(req2);
			ResultSet rs2 = stat.executeQuery(req2);
			System.out.println(rs2.getString("password"));
			String pwd = rs2.getString("password");
			Authenticator.setDefault(new SUSAuthenticator(pwd));
		}
		URL mainUrl = new URL(req);
		System.out.println("HTMLRequest auf "+req);

        BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
        StringBuilder full = new StringBuilder();
        String str;
        while ((str = in.readLine()) != null) {
            full.append(str);
        }
        in.close();
        
        returnString=full.toString();
        return returnString;
	}
	
	public static String doKurssprecherRequest(String[] args) throws SQLException, ClassNotFoundException {
		String returnString = "";

		String kursid = args[0];
		String klasse = args[1];
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(path);
	    Statement stat = conn.createStatement();
	    System.out.println("SELECT * FROM kurssprecher");
		ResultSet rs = stat.executeQuery("SELECT * FROM kurssprecher;");
		int rowcount = 0;
		String results = "";
	    while (rs.next()){
	    	System.out.println(kursid+" equals "+rs.getString("kursid")+"? "+klasse+" equals "+rs.getString("klasse"));
	    	if(kursid.equals(rs.getString("kursid")) && klasse.equals(rs.getString("klasse"))) {
			      results += "/newthing/Kurs-ID: "+rs.getString("kursid")+"//Kurssprecher: "+
	    	rs.getString("sprecher")+"//Vertretung: "+rs.getString("vertretung");
			      rowcount++;
	    	}
	    }
		returnString += rowcount+results;
	    rs.close();
	    stat.close();
	    conn.close();
	    
	    return returnString;
	}
	
	public static void doGebaeudefehlerMelden(String[] args) {
		String msg = "Hallo,\n" + 
				"folgende Meldung wurde über die AMG-App übermittelt:\n\n";
		
		String datum = args[0];
		String gebaeude=args[1];
		String etage=args[2];
		String raum=args[3];
		String wichtigk=args[4];
		String fehler=args[5];
		String beschr=args[6].replaceAll("//", "\n");
		
		msg += "Datum: "+datum+",\n";
		msg += "Raum: "+gebaeude+etage+raum+",\n";
		msg += "Wichtigkeit: "+wichtigk+",\n";
		msg += "Problem: "+fehler+",\n";
		msg += "Beschreibung: "+beschr+"\n\n";
		
		msg += "Bitte um weitere Bearbeitung.\n\n\n";
		
		msg += " - Dies ist eine automatisch erstellte Nachricht im Auftrag von J. Rienäcker - ";
		sendEMail(msg);
	}
	
}
