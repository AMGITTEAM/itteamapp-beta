package amgapp;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class SUSAuthenticator extends Authenticator {
	
	final private String password;
	
	SUSAuthenticator(String password){
		this.password = password;
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		String username = "Schueler";
		return new PasswordAuthentication(username,password.toCharArray());
	}
	
}
