package com.github.mateuszmazewski.passwordmanager.views;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.Role;
import com.github.mateuszmazewski.passwordmanager.security.Util;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@PageTitle("Rejestracja")
@Route(value = "register", layout = MainLayout.class)
@AnonymousAllowed
public class RegisterView extends HorizontalLayout {
    FormLayout form = new FormLayout();
    Binder<User> binder = new BeanValidationBinder<>(User.class);
    TextField username = new TextField("Nazwa użytkownika");
    EmailField email = new EmailField("E-mail");
    PasswordField password = new PasswordField("Hasło do logowania");
    PasswordField masterPassword = new PasswordField("Hasło główne");
    Button registerButton = new Button("Zarejestruj się");
    Button loginButton = new Button("Zaloguj się");
    Span variousPasswordsInfo = new Span("Dla większego bezpieczeństwa użyj dwóch różnych haseł");
    User user = new User();
    PasswordEncoder passwordEncoder;
    private final UserService service;
    Dialog successDialog = new Dialog();
    ProgressBar passwordStrength = new ProgressBar();
    ProgressBar masterPasswordStrength = new ProgressBar();

    public RegisterView(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;

        binder.bindInstanceFields(this);

        binder.forField(username)
                .withValidator(
                        username -> username != null && !username.isEmpty(),
                        Messages.EMPTY)
                .withValidator(
                        username -> username != null && username.length() <= 100,
                        Messages.LENGTH_100)
                .withValidator(
                        username -> username != null && !service.userExists(username),
                        Messages.USERNAME_NOT_UNIQUE)
                .bind(User::getUsername, User::setUsername);

        variousPasswordsInfo.getElement().getThemeList().add("badge");

        password.addValueChangeListener(e -> Util.validatePassword(password, passwordStrength, true));
        masterPassword.addValueChangeListener(e -> Util.validatePassword(masterPassword, masterPasswordStrength, true));

        passwordStrength.setMin(Util.PASSWORD_STRENGTH_MIN);
        masterPasswordStrength.setMin(Util.PASSWORD_STRENGTH_MIN);
        passwordStrength.setMax(Util.PASSWORD_STRENGTH_MAX);
        masterPasswordStrength.setMax(Util.PASSWORD_STRENGTH_MAX);

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClickListener(e -> validateAndSave());
        loginButton.addClickListener(e -> {
            successDialog.close();
            UI.getCurrent().navigate(LoginView.class);
        });

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultVerticalComponentAlignment(Alignment.CENTER);

        configureForm();
        add(form);
    }

    private void configureForm() {
        form.setWidth("25em");

        form.add(
                new H2("Rejestracja nowego użytkownika"),
                username, email,
                new H3(),
                variousPasswordsInfo,
                password, passwordStrength, masterPassword, masterPasswordStrength,
                registerButton
        );

        VerticalLayout dialogLayout = new VerticalLayout(
                new H2("Pomyślnie utworzono nowego użytkownika"),
                loginButton
        );
        dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        successDialog.add(dialogLayout);
        successDialog.setCloseOnEsc(false);
        successDialog.setCloseOnOutsideClick(false);

        form.getStyle().set("text-align", "center");
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            boolean passwordValid = Util.validatePassword(password, passwordStrength, true);
            boolean masterPasswordValid = Util.validatePassword(masterPassword, masterPasswordStrength, true);
            if (passwordValid && masterPasswordValid) {
                user.setHashedPassword(passwordEncoder.encode(password.getValue()));
                user.setHashedMasterPassword(passwordEncoder.encode(masterPassword.getValue()));
                user.setRoles(Collections.singleton(Role.USER));
                service.update(user);
                successDialog.open();
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

}