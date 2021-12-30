package com.github.mateuszmazewski.passwordmanager.security;

import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.repository.UserRepository;
import com.github.mateuszmazewski.passwordmanager.data.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationSuccessListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        final String userAgent = request.getHeader("User-Agent");

        Authentication authentication = e.getAuthentication();
        User authenticatedUser = null;
        if (authentication != null && authentication.getName() != null && !authentication.getName().isEmpty()) {
            authenticatedUser = userRepository.findByUsername(authentication.getName());
        }

        if (xfHeader == null) {
            loginAttemptService.loginSucceeded(request.getRemoteAddr(), authenticatedUser, userAgent);
        } else {
            loginAttemptService.loginSucceeded(xfHeader.split(",")[0], authenticatedUser, userAgent);
        }
    }
}