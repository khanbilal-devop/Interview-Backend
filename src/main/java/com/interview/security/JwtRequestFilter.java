package com.interview.security;


import com.interview.model.GenericServiceResponse;
import com.interview.model.User;
import com.interview.model.UserPrincipal;
import com.interview.service.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserServiceImpl jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest multiReadRequest, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (multiReadRequest.getParameter("filePath") == null || !(multiReadRequest.getParameter("filePath").equalsIgnoreCase("adipro/"))) {
            /**
             * *********************************************************************
             * For Multi Read Request
             * *********************************************************************
             */

//            var multiReadRequest = new CachedBodyHttpServletRequest(request);
            final String requestTokenHeader = multiReadRequest.getHeader(AUTHORIZATION);

            String emailId = "";
            String jwtToken = "";

            /**
             * *********************************************************************
             * JWT Token is in the form "Bearer token". Remove Bearer word and get
             * only the Token
             * *********************************************************************
             */
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    User user = jwtTokenUtil.getUserFromToken(jwtToken);
                    emailId = user.getEmail();
                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    log.error("JWT Token has expired");
                }
            } else {
                log.warn("JWT Token does not begin with Bearer String");
            }

            /**
             * *********************************************************************
             * Once we get the token validate it.
             * *********************************************************************
             */
            if (!emailId.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                /**
                 * *****************************************************************
                 * Checking application to get Authenticate
                 * *****************************************************************
                 */
                boolean tokenValidation;
                User user ;
                GenericServiceResponse genericServiceResponse
                        = jwtUserDetailsService.loadUserByUsername(emailId);

                if (genericServiceResponse.getHttpStatus().equals(HttpStatus.OK)) {
                    user = (User) genericServiceResponse.getTempData();
                    /**
                     * *****************************************************************
                     * if token is valid configure Spring Security to manually set
                     * authentication
                     * *****************************************************************
                     */
                    tokenValidation = jwtTokenUtil.validateToken(jwtToken, user);
                    if (tokenValidation) {
                        UserPrincipal userDetails = new UserPrincipal(user);
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(multiReadRequest));

                        /**
                         * *************************************************************
                         * After setting the Authentication in the context, we specify
                         * that the current user is authenticated. So it passes the
                         * Spring Security Configurations successfully.
                         * *************************************************************
                         */
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }

                /**
                 * *************************************************************
                 * Added For Audit Trail
                 * *************************************************************
                 */
            }
            chain.doFilter(multiReadRequest, response);
        } else {
            chain.doFilter(multiReadRequest, response);
        }
    }

}

