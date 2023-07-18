package com.honeybee.todospringboot.config;

import com.honeybee.todospringboot.security.JwtAuthenticationFilter;
import com.honeybee.todospringboot.security.OAuthSuccessHandler;
import com.honeybee.todospringboot.security.OAuthUserServiceImpl;
import com.honeybee.todospringboot.security.RedirectUrlCookieFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuthUserServiceImpl oAuthUserService;

    @Autowired
    private OAuthSuccessHandler oAuthSuccessHandler;

    @Autowired
    private RedirectUrlCookieFilter redirectUrlCookieFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.cors()
                .and()
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/","/auth/**","/oauth2/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .authorizationEndpoint()
                .baseUri("/auth/authorize")
                .and()
                .userInfoEndpoint()
                .userService(oAuthUserService)
                .and()
                .successHandler(oAuthSuccessHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        http.addFilterAfter(
                jwtAuthenticationFilter,
                CorsFilter.class
        );
        http.addFilterBefore(
                redirectUrlCookieFilter,
                OAuth2AuthorizationRequestRedirectFilter.class
        );
    }
}
