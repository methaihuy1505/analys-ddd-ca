package de.jenshardt.multimavendemo.authentication;

import java.io.Serializable;

public class JwtRequest implements Serializable {

	private static final long serialVersionUID = -8463313041934736867L;
	
	private String username;
	private String password;

	// Need default constructor for JSON Parsing
	public JwtRequest() { }

	public JwtRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
