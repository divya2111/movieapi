package com.Movieapi.auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

@Service
public class JwtService {
	
	 private  String secretkey = "";
	 
	 
	 public JwtService() {

	        try {
	            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
	            SecretKey sk = keyGen.generateKey();
	            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    // extract username from JWT
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }

	    // extract information from JWT
	    private Claims extractAllClaims(String token) {
	        return Jwts.parser()
	                .verifyWith(getKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }

	    // decode and get the key
	    private SecretKey getKey() {
	        // decode SECRET_KEY
	        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }


	    public String generateToken(UserDetails userDetails) {
	        return generateToken(new HashMap<>(), userDetails);
	    }

	    // generate token using Jwt utility class and return token as String
	    public String generateToken(
	            Map<String, Object> extraClaims,
	            UserDetails userDetails
	    ) {
	        extraClaims = new HashMap<>(extraClaims);
	        extraClaims.put("role", userDetails.getAuthorities());
	        return Jwts
	                .builder()
	                .claims(extraClaims)
	                .subject(userDetails.getUsername())
	                .issuedAt(new Date(System.currentTimeMillis()))
	                .expiration(new Date(System.currentTimeMillis() + 25 * 100000))
	                .signWith(getKey())
	                .compact();
	    }

	    // if token is valid by checking if token is expired for current user
	    public boolean isTokenValid(String token, UserDetails userDetails) {
	        final String username = extractUsername(token);
	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }

	    // if token is expired
	    private boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }

	    // get expiration date from token
	    private Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }
	

}
