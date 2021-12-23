package com.github.mateuszmazewski.passwordmanager.views;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import static com.github.mateuszmazewski.passwordmanager.data.entity.User.MIN_PASSWORD_LENGTH;

@PageTitle("Rejestracja")
@Route(value = "register", layout = MainLayout.class)
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    Binder<User> binder = new BeanValidationBinder<>(User.class);
    TextField username = new TextField("Nazwa użytkownika");
    EmailField email = new EmailField("E-mail");
    PasswordField password = new PasswordField("Hasło do logowania");
    PasswordField masterPassword = new PasswordField("Hasło główne");
    Button registerButton = new Button("Zarejestruj się");
    Button loginButton = new Button("Zaloguj się");
    User user = new User();
    PasswordEncoder passwordEncoder;
    private final UserService service;
    Dialog successDialog = new Dialog();

    public RegisterView(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;

        setSpacing(false);
        binder.bindInstanceFields(this);

        binder.forField(username)
                .withValidator(
                        username -> username != null && !username.isEmpty(),
                        Messages.EMPTY)
                .withValidator(
                        username -> username != null && !service.userExists(username),
                        Messages.USERNAME_NOT_UNIQUE)
                .bind(User::getUsername, User::setUsername);

        password.addValueChangeListener(e -> validatePassword());
        masterPassword.addValueChangeListener(e -> validateMasterPassword());

        registerButton.addClickListener(e -> validateAndSave());

        add(
                new H2("Rejestracja nowego użytkownika"),
                username, email, password, masterPassword,
                registerButton
        );

        loginButton.addClickListener(e -> UI.getCurrent().getPage().setLocation("login"));
        VerticalLayout dialogLayout = new VerticalLayout(
                new H2("Pomyślnie utworzono nowego użytkownika"),
                loginButton
        );
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        successDialog.add(dialogLayout);
        successDialog.setCloseOnEsc(false);
        successDialog.setCloseOnOutsideClick(false);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            user.setHashedPassword(passwordEncoder.encode(password.getValue()));
            user.setHashedMasterPassword(passwordEncoder.encode(masterPassword.getValue()));
            boolean passwordValid = validatePassword();
            boolean masterPasswordValid = validateMasterPassword();
            if (passwordValid && masterPasswordValid) {
                service.update(user);
                successDialog.open();
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private boolean validatePassword() {
        if (passwordIsStrong(password.getValue())) {
            password.setInvalid(false);
            password.setErrorMessage(null);
            return true;
        } else {
            password.setInvalid(true);
            password.setErrorMessage(Messages.PASSWORD_NOT_STRONG);
            return false;
        }
    }

    private boolean validateMasterPassword() {
        if (passwordIsStrong(masterPassword.getValue())) {
            masterPassword.setInvalid(false);
            masterPassword.setErrorMessage(null);
            return true;
        } else {
            masterPassword.setInvalid(true);
            masterPassword.setErrorMessage(Messages.PASSWORD_NOT_STRONG);
            return false;
        }
    }

    private boolean passwordIsStrong(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean containsDigit = false;
        boolean containsUppercase = false;
        boolean containsLowercase = false;
        boolean containsSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                containsDigit = true;
                continue;
            }
            if (Character.isUpperCase(c)) {
                containsUppercase = true;
                continue;
            }
            if (Character.isLowerCase(c)) {
                containsLowercase = true;
                continue;
            }
            if (!Character.isLetterOrDigit(c)) {
                containsSpecial = true;
            }
        }

        return containsDigit && containsUppercase && containsLowercase && containsSpecial;
    }

}