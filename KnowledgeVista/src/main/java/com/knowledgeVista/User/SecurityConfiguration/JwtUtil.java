package com.knowledgeVista.User.SecurityConfiguration;

import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public class JwtUtil {
	 @Autowired
	    private JwtConfig jwtConfig;

	    @Autowired
	    private TokenBlacklist tokenBlacklist;
	 
	 
	 public static final long JWT_EXPIRATION_MS = 86400000;

	    public String generateToken(String username,String userRole) {
	    	Date now = new Date();
	        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
	        
	        return Jwts.builder()
	                .setSubject(username)
	                .claim("username",username)
	                .claim("role", userRole) // Add role as a claim
	                .setExpiration(expiryDate)
	                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey())
	                .compact();
	    }
	    public boolean validateToken(String token) {
	        try {
	            // Check if the token is blacklisted
	            if (tokenBlacklist.isTokenBlacklisted(token)) {
	                return false; // Token is blacklisted, so consider it invalid
	            }
	            
	            Claims claims = Jwts.parser()
	                .setSigningKey(jwtConfig.getSecretKey())
	                .parseClaimsJws(token)
	                .getBody();

	            // Check if the token has expired
	            Date expiration = claims.getExpiration();
	            Date now = new Date();
	            return !expiration.before(now); // Return true if not expired
	        } catch (Exception e) {
	            // Token parsing failed or expired
	            return false;
	        }
	    }

	    public String getRoleFromToken(String token) {
	        Claims claims = Jwts.parser()
	            .setSigningKey(jwtConfig.getSecretKey())
	            .parseClaimsJws(token)
	            .getBody();
	        return claims.get("role", String.class);
	    }
	    public String getUsernameFromToken(String token) {
	        try {
	            Claims claims = Jwts.parser()
	                .setSigningKey(jwtConfig.getSecretKey())
	                .parseClaimsJws(token)
	                .getBody();
	            return claims.get("username", String.class);
	        } catch (Exception e) {
	            // Print or log the exception for debugging
	            e.printStackTrace();
	            return null;
	        }
	    }


}
