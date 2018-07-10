package sq;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Lite {
	Socket s;

	public Lite(Socket s) {
		this.s=s;
	}

	public void transact() throws Exception{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			String request = br.readLine();
			System.out.println(request);
			if(request.equals("Login")){
				Connection conn = DriverManager.getConnection("jdbc:sqlite:AMGApp.db");
			    Statement stat = conn.createStatement();
			    String req = br.readLine();
			    System.out.println(req);
				ResultSet rs2 = stat.executeQuery(req);
				System.out.println(rs2.getString("rechthoehe"));
				pw.println(rs2.getString("rechthoehe"));
				pw.flush();
			}
			else if(request.equals("ITTeamMelden")){
				String datum="";
				String zulBearb="";
				String gebaeude="";
				String etage="";
				String raum="";
				String wichtigk="";
				String fehler="";
				String beschr="";
				String status="";
				String bearbVon="";
				datum=br.readLine();
				zulBearb=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
				gebaeude=br.readLine();
				etage=br.readLine();
				raum=br.readLine();
				wichtigk=br.readLine();
				fehler=br.readLine();
				beschr=br.readLine();
				status=br.readLine();
				bearbVon=br.readLine();
				
				beschr=beschr.replaceAll("//", "\n");
				
				Class.forName("org.sqlite.JDBC");
			    Connection conn = DriverManager.getConnection("jdbc:sqlite:AMGApp.db");
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
			    prep.addBatch();
			    
			    conn.setAutoCommit(false);
			    prep.executeBatch();
			    conn.setAutoCommit(true);
			    conn.close();
			}
			else if(request.equals("ITTeamHolen")){
				Connection conn = DriverManager.getConnection("jdbc:sqlite:AMGApp.db");
			    Statement stat = conn.createStatement();
			    String req = br.readLine();
			    System.out.println(req);
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
				pw.println(""+rowcount);
				pw.flush();
				ResultSet rs = stat.executeQuery(req);
			    while (rs.next()){
			      pw.println("Datum: "+rs.getString("datum")+"//Zuletzt bearbeitet: "+rs.getString("zuletztBearbeitet")+"//Gebaeude: "+rs.getString("gebaeude")+"//Etage: "+rs.getString("etage")+"//Raum: "+rs.getString("raum")+"//Wichtigkeit: "+rs.getString("wichtigkeit")+"//Fehler: "+rs.getString("fehler")+"//Beschreibung: "+rs.getString("beschr")+"//Status: "+rs.getString("status")+"//Zuletzt bearbeitet von: "+rs.getString("bearbVon"));
			      pw.flush();
			    }
			    rs.close();
			    stat.close();
			    conn.close();
			}
			else if(request.equals("ITTeamLoeschen")){
				Connection c = DriverManager.getConnection("jdbc:sqlite:AMGApp.db");
				c.setAutoCommit(false);
				Statement stmt = c.createStatement();
				String sql = br.readLine();
				stmt.executeUpdate(sql);
				c.commit();
			}
			
			br.close();
			pw.close();
			s.close();
		}
		catch (NullPointerException e){
			System.out.println("Internet überprüfen");
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		System.out.println("---------------------");

	}

}
