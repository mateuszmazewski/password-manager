package com.github.mateuszmazewski.passwordmanager.views;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.service.UserService;
import com.github.mateuszmazewski.passwordmanager.security.Util;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@PageTitle("Odzyskiwanie hasła")
@Route(value = "password-recovery", layout = MainLayout.class)
@AnonymousAllowed
public class PasswordRecoveryView extends HorizontalLayout {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    Binder<User> binder = new BeanValidationBinder<>(User.class);
    FormLayout form = new FormLayout();
    EmailField email = new EmailField("E-mail");
    Button recoverPasswordButton = new Button("Odzyskaj hasło");
    PasswordField passwordField = new PasswordField("Hasło do logowania");
    ProgressBar passwordStrength = new ProgressBar();
    Label info = new Label();
    Dialog codeDialog;
    Dialog successDialog = new Dialog();

    public PasswordRecoveryView(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        binder.bindInstanceFields(this);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        Button loginButton = new Button("Zaloguj się");

        loginButton.addClickListener(e -> {
            successDialog.close();
            UI.getCurrent().navigate(LoginView.class);
        });

        VerticalLayout successDialogLayout = new VerticalLayout(
                new H2("Pomyślnie zmieniono hasło"),
                loginButton
        );
        successDialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        successDialog.add(successDialogLayout);
        successDialog.setCloseOnEsc(false);
        successDialog.setCloseOnOutsideClick(false);

        configureForm();
        add(form);
    }

    private void configureForm() {
        form.setWidth("25em");
        form.add(
                new H2("Odzyskiwanie hasła"),
                email, recoverPasswordButton, info
        );

        email.setClearButtonVisible(true);
        recoverPasswordButton.addClickListener(e -> recoverPassword());
        form.getStyle().set("text-align", "center");
    }

    private void recoverPassword() {
        if (binder.validate().hasErrors()) {
            return;
        }

        User user = service.findByEmail(email.getValue());
        String code = null;
        LocalDateTime codeValidUntil = null;
        codeDialog = new Dialog();

        if (user != null) {
            code = generateCode();
            codeValidUntil = LocalDateTime.now().plusMinutes(10);
            System.out.println("Normalnie wysłałbym wiadomość e-mail na adres " + email.getValue() + " z treścią:");
            System.out.println("Wystąpiła próba zresetowania hasła do konta powiązanego z tym adresem e-mail w menedżerze haseł.");
            System.out.println("Jeśli chcesz zresetować hasło, wprowadź swój tajny kod: " + code);
            System.out.println("Kod jest ważny do: " + codeValidUntil.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Jeśli nie rozpoznajesz tej aktywności, zignoruj tę wiadomość.");

            // Normally, I would send e-mail here
        }

        String info = "Jeśli istnieje użytkownik o podanym adresie e-mail, na ten adres otrzymasz tajny kod (ważny 10 minut)." +
                " Wprowadź go poniżej, aby ustawić nowe hasło.";

        TextField codeField = new TextField("Tajny kod");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Anuluj");
        String finalCode = code;
        LocalDateTime finalCodeValidUntil = codeValidUntil;

        okButton.addClickListener(e -> verifyCode(codeField, finalCode, finalCodeValidUntil, user));
        cancelButton.addClickListener(e -> codeDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        FormLayout formLayout = new FormLayout(
                new Label(info),
                codeField,
                new HorizontalLayout(okButton, cancelButton)
        );
        formLayout.setWidth("25em");
        formLayout.getStyle().set("text-align", "center");
        codeDialog.add(formLayout);
        codeDialog.setCloseOnEsc(false);
        codeDialog.setCloseOnOutsideClick(false);

        codeDialog.open();
    }

    private String generateCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void verifyCode(TextField codeField, String validCode, LocalDateTime codeValidUntil, User user) {
        if (codeField.getValue() == null || codeField.getValue().isEmpty()) {
            codeField.setInvalid(true);
            codeField.setErrorMessage(Messages.EMPTY);
            return;
        } else if (LocalDateTime.now().isAfter(codeValidUntil)) {
            codeField.setInvalid(true);
            codeField.setErrorMessage(Messages.CODE_NO_LONGER_VALID);
            return;
        } else {
            codeField.setInvalid(false);
            codeField.setErrorMessage(null);
        }

        if (validCode == null) {
            return; // User with given e-mail does not exist
        }

        if (codeField.getValue().equals(validCode)) {
            codeDialog.close();
            setPasswordDialog(user);
        } else {
            codeField.setInvalid(true);
            codeField.setErrorMessage("Nieprawidłowy kod.");
        }
    }

    private void setPasswordDialog(User user) {
        Dialog setPasswordDialog = new Dialog();
        TextField username = new TextField("Nazwa użytkownika");
        EmailField email = new EmailField("E-mail");
        Button setPasswordButton = new Button("Ustaw hasło");
        Button cancelButton = new Button("Anuluj");

        username.setValue(user.getUsername());
        username.setEnabled(false);
        email.setValue(user.getEmail());
        email.setEnabled(false);
        FormLayout formLayout = new FormLayout();

        passwordField.addValueChangeListener(e -> Util.validatePassword(passwordField, passwordStrength, true));
        passwordStrength.setMin(Util.PASSWORD_STRENGTH_MIN);
        passwordStrength.setMax(Util.PASSWORD_STRENGTH_MAX);

        setPasswordButton.addClickListener(e -> validateAndSave(user, setPasswordDialog));
        cancelButton.addClickListener(e -> setPasswordDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        formLayout.add(
                new H2("Kod poprawny. Teraz ustaw swoje nowe hasło."),
                username, email, passwordField, passwordStrength,
                new HorizontalLayout(setPasswordButton, cancelButton)
        );
        formLayout.setWidth("25em");
        formLayout.getStyle().set("text-align", "center");
        setPasswordDialog.add(formLayout);
        setPasswordDialog.setCloseOnOutsideClick(false);
        setPasswordDialog.setCloseOnEsc(false);

        setPasswordDialog.open();
    }

    private void validateAndSave(User user, Dialog setPasswordDialog) {
        try {
            boolean passwordValid = Util.validatePassword(passwordField, passwordStrength, true);
            if (passwordValid) {
                user.setHashedPassword(passwordEncoder.encode(passwordField.getValue()));
                service.update(user);
                setPasswordDialog.close();
                successDialog.open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}