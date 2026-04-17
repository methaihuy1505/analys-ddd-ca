package de.jenshardt.multimavendemo.authentication;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	static final long serialVersionUID = -7595988238826648203L;
	
	private final String jwttoken;

	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}
}