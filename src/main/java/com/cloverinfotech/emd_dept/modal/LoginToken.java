package com.cloverinfotech.emd_dept.modal;

import jakarta.persistence.*;
import jakarta.persistence.Id;

@Entity
@Table(name="login_tokens")
public class LoginToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String token;
	
	private String username;
	
    private boolean active = true;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LoginToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginToken(long id, String token, String username, boolean active) {
		super();
		this.id = id;
		this.token = token;
		this.username = username;
		this.active = active;
	}

	@Override
	public String toString() {
		return "LoginToken [id=" + id + ", token=" + token + ", username=" + username + ", active=" + active + "]";
	}
    
    
}
