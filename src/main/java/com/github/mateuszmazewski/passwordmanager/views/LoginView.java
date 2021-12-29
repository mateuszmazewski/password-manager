package com.github.mateuszmazewski.passwordmanager.views;

import com.github.mateuszmazewski.passwordmanager.data.entity.LoginAttempt;
import com.github.mateuszmazewski.passwordmanager.data.service.LoginAttemptService;
import com.github.mateuszmazewski.passwordmanager.security.Util;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {
    private final HttpServletRequest request;
    private final LoginAttemptService service;
    private final LoginI18n i18n = LoginI18n.createDefault();

    public LoginView(@Autowired HttpServletRequest request, @Autowired LoginAttemptService service) {
        this.request = request;
        this.service = service;
        setAction("login");

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Menadżer haseł");
        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);
        i18n.getForm().setTitle("Logowanie");
        i18n.getForm().setUsername("Nazwa użytkownika");
        i18n.getForm().setPassword("Hasło");
        i18n.getForm().setSubmit("Zaloguj się");
        i18n.getForm().setForgotPassword("Zarejestruj się");
        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(e -> UI.getCurrent().navigate(RegisterView.class));
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {

            String ip = Util.getClientIP(request);
            LoginAttempt attempt = service.findByIp(ip);
            if (attempt != null && attempt.getBlockedUntil() != null) {
                i18n.getErrorMessage().setTitle("Przekroczono limit prób logowania.");
                i18n.getErrorMessage().setMessage("Logowanie z twojego adresu IP zostało zablokowane do "
                        + attempt.getBlockedUntil().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                int attemptsRemaining;
                if (attempt != null) {
                    attemptsRemaining = LoginAttemptService.MAX_ATTEMPTS - attempt.getFailedAttempts();
                } else {
                    attemptsRemaining = LoginAttemptService.MAX_ATTEMPTS;
                }
                i18n.getErrorMessage().setTitle("Nieprawidłowy login lub hasło");
                i18n.getErrorMessage().setMessage("Upewnij się, że podane dane są prawidłowe i spróbuj ponownie." +
                        " Pozostałe próby: " + attemptsRemaining + ".");
            }
            setI18n(i18n);
            setError(true);
        }
    }

}
