package com.cloverinfotech.emd_dept.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtSecurity {
	
	 private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

	    public String generateToken(String username) {
	        return Jwts.builder()
	                .subject(username)
	                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
	                .signWith(secretKey)
	                .compact();
	    }

}
