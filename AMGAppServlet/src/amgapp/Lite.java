package amgapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lite {
	static String path = "jdbc:sqlite:/home/ak/AMGApp.db";

	public static String transact(String request, String req, String benutzername, String passwort, String... args) throws Exception{
		String returnString = "";
		try {
			System.out.println(request);
			if(request.equals("Login")){
				returnString = ""+getRechthoehe(benutzername,passwort);
			}
			else if(request.equals("ITTeamMelden")){
				if(getRechthoehe(benutzername,passwort)>=2) {
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
				    returnString=success+"";
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("ITTeamHolen")){
				if(getRechthoehe(benutzername,passwort)>=3) {
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
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
			}
			else if(request.equals("ITTeamLoeschen")){
				if(getRechthoehe(benutzername,passwort)>=3) {
					Class.forName("org.sqlite.JDBC");
					Connection c = DriverManager.getConnection(path);
					c.setAutoCommit(false);
					Statement stmt = c.createStatement();
					stmt.executeUpdate(req);
					c.commit();
					returnString = "true";
					stmt.close();
					c.close();
				}
				else {
					returnString="Du hast nicht genügend Rechte!";
				}
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

}
