package sq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {

	public static void main(String[] args) {

		try {
			ServerSocket server = new ServerSocket(18732);
			System.out.println("Server gestartet");
			while(true){
				Socket s = server.accept();
				System.out.println("Angenommen");
				try {
					new Lite(s).transact();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
