package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private final String secret;
    private static final String AUTHORITIES_KEY = "auth";
    private long tokenValidateMillisecondRemember;

    private long tokenValidateMilliseconds;

    private final Key key;

    private final JwtParser jwtParser;

    public TokenProvider(@Value("${jwt.token.secret}") String secret) {
        this.secret = secret;
        byte[] keyByte;
        keyByte = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyByte);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidateMillisecondRemember = 1000 * 86400000;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validate = new Date(now + tokenValidateMillisecondRemember);

        return Jwts
                .builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("userId" , Long.class)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validate)
                .compact();
    }

    public Authentication getAuthentication(String jwt) {
        Claims claims = jwtParser.parseClaimsJws(jwt).getBody();
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return  new UsernamePasswordAuthenticationToken(principal, jwt, authorities);

    }

    public String getUserNameFromToken(String jwt) {
        Claims claims  = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.get("userId", Long.class);
    }


    public boolean validateToken(String jwt) {
        try {
            jwtParser.parseClaimsJws(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("ExpiredJwtException");
        } catch (UnsupportedJwtException u) {
            logger.error("UnsupportedJwtException");
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwtException");
        } catch (SignatureException e) {
            logger.error("SignatureException");
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException");
        }
        return false;
    }
}
