package com.knowledgeVista.User.SecurityConfiguration;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	// 24hrs
	public static final long JWT_EXPIRATION_MS = 86400000;
	// public static final long JWT_EXPIRATION_MS = 60000; // 1 minute

	public String generateToken(String username, String userRole, String institutionName, Long userId, String email) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

		return Jwts.builder().setSubject(username).setIssuedAt(now).claim("username", username).claim("email", email)
				.claim("role", userRole).claim("institution", institutionName).claim("userId", userId)
				.setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey()).compact();
	}

	public String getInstitutionFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
			return claims.get("institution", String.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error extracting institution", e);
			return null;
		}
	}

	public Long getUserIdFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
			return claims.get("userId", Long.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error extracting userId", e);
			return null;
		}
	}

	public boolean validateToken(String token) {
		try {
			// Check if the token is blacklisted
			if (tokenBlacklist.isTokenBlacklisted(token)) {
				return false; // Token is blacklisted, so consider it invalid
			}

			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();

			// Check if the token has expired
			Date expiration = claims.getExpiration();
			Date now = new Date();
			return !expiration.before(now); // Return true if not expired
		} catch (Exception e) {
			// Token parsing failed or expired
			e.printStackTrace();
			logger.error("", e);
			return false;
		}
	}

	public String getRoleFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
			return claims.get("role", String.class);
		} catch (Exception e) {
			// Print or log the exception for debugging
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public String getUsernameFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
			return claims.get("username", String.class);
		} catch (Exception e) {
			// Print or log the exception for debugging
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public String getEmailFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
			return claims.get("email", String.class);
		} catch (Exception e) {
			// Print or log the exception for debugging
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public String refreshToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token).getBody();

			String username = claims.get("username", String.class);
			String role = claims.get("role", String.class);
			String institution = claims.get("institution", String.class);
			String email = claims.get("email", String.class);
			Long userId = claims.get("userId", Long.class);

			Date now = new Date();
			Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

			return Jwts.builder().setSubject(username).claim("username", username).claim("role", role)
					.claim("email", email).claim("institution", institution).claim("userId", userId)
					.setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey()).compact();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error refreshing token", e);
			return null;
		}
	}

}
