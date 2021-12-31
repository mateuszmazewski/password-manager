package com.github.mateuszmazewski.passwordmanager.security;

import com.github.mateuszmazewski.passwordmanager.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    public static final String LOGOUT_URL = "/";

    // strength == 10 -> 2^10 rounds
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    /*
    All requests between the client and the server are included with a user session specific CSRF token.
    All communication between the server and the client is handled by Vaadin,
    so you do not need to remember to include and verify the CSRF tokens manually.

    Vaadin has built-in protection against cross-site scripting (xss) attacks.
    Vaadin uses Browser APIs that make the browser render content as text instead of HTML,
    such as using innerText instead of innerHTML.
    This negates the chance to accidentally inserting e.g. <script> tags into the DOM by binding unsecure string values.
    Allowing insecure HTML content is never the default.
     */

        super.configure(http);
        http.requiresChannel().antMatchers("/**").requiresSecure();
        http.headers().addHeaderWriter(new StaticHeadersWriter("Server", "I am here to serve You"));
        http.headers().contentSecurityPolicy("img-src 'self' https://website.vaadin.com");
        setLoginView(http, LoginView.class, LOGOUT_URL);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/images/*.png");
    }
}
