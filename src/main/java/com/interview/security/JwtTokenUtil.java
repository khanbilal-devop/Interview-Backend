/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interview.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.UserDto;
import com.interview.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author +IT KHAMBE working in
 * <a href="https://www.openspaceservices.com">Openspace Services Pvt. Ltd.</a>
 */
@Component
@CrossOrigin("*")
@Log4j2
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    @Value("${jwt.token.validity.days}")
    public int JWT_TOKEN_VALIDITY_IN_DAYS;
    @Value("${jwt.secret}")
    private String secret;

    /**
     * *************************************************************************
     * Retrieve Email from JWT token
     * *************************************************************************
     *
     * @param token
     * @return Email
     */
    public String getEmailFromToken(String token) {
        try {
            User user = this.getUserFromToken(token);
            return user.getEmail();
        } catch (Exception e) {

            return null;
        }

    }

    /**
     * *************************************************************************
     * Retrieve Email from JWT token
     * *************************************************************************
     *
     * @param token
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        try {
            User user = this.getUserFromToken(token);
            return user.getEmail();
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * *************************************************************************
     * Retrieve User from JWT token
     * *************************************************************************
     *
     * @param token
     * @return user
     */
    public User getUserFromToken(String token) {
        User user = new User();
        try {
            String subject = this.getClaimFromToken(token, Claims::getSubject);
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(subject, User.class);
        } catch (JsonProcessingException e) {
            log.info("Error in getUserFromToken " + e.getMessage());
        }
        return user;
    }

    /**
     * *************************************************************************
     * Retrieve expiration date from JWT Token
     * *************************************************************************
     *
     * @param token
     * @return ExpirationDate
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * *************************************************************************
     * for Retrieving any information from token we will need the secret key
     * *************************************************************************
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * *************************************************************************
     * Check if the token has expired
     * *************************************************************************
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * *************************************************************************
     * Generate token for user
     * *************************************************************************
     *
     * @param user
     * @return token
     * @throws JsonProcessingException
     */
    public String generateToken(UserDto user) throws JsonProcessingException {

        Map<String, Object> claims = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(user);
        return doGenerateToken(claims, jsonInString);
    }

    /**
     * *************************************************************************
     * while creating the token ------------------------------------------------
     * 1.Define claims of the token, like Issuer, Expiration, Subject, and the
     * ID ----------------------------------------------------------------------
     * 2. Sign the JWT using the HS512 algorithm and secret key.----------------
     * 3. According to JWS Compact
     * Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     * compaction of the JWT to a URL-safe string
     * *************************************************************************
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        long days = JWT_TOKEN_VALIDITY_IN_DAYS * 24 * 60 * 60 * 1000L;
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + days))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * *************************************************************************
     * validate Token
     * *************************************************************************
     *
     * @param token
     * @param user
     * @return Boolean
     */
    public Boolean validateToken(String token, User user) {
        try {
            String email = this.getEmailFromToken(token);
            return (email.equals(user.getEmail()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.info(" Exception in validateToken" + e.getMessage());
            return false;
        }

    }
}
