package com.interview.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        /**
         * *********************************************************************
         * We don't need CSRF for this example
         * *********************************************************************
         */
        httpSecurity.csrf().disable()
                /**
                 * *************************************************************
                 * Don`t authenticate this particular request
                 * *************************************************************
                 */
                .authorizeRequests()
                /**
                 * *************************************************************
                 * This added because off react cross origin issue
                 * *************************************************************
                 */
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/app/**").permitAll()
                /**
                 * *************************************************************
                 * all other requests need to be authenticated
                 * *************************************************************
                 */
                .anyRequest().authenticated().and().
                /**
                 * *************************************************************
                 * make sure we use stateless session; session won't be used to
                 * store user's state.
                 * *************************************************************
                 */
                        exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /**
         * *************************************************************
         * Add a filter to validate the tokens with every request
         * *************************************************************
         */
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }



}
