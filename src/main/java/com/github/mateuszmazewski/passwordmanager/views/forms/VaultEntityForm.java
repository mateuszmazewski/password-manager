package com.github.mateuszmazewski.passwordmanager.views.forms;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.entity.VaultEntity;
import com.github.mateuszmazewski.passwordmanager.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class VaultEntityForm extends EntityForm {
    Binder<VaultEntity> binder = new BeanValidationBinder<>(VaultEntity.class);

    TextField name = new TextField("Nazwa");
    TextField url = new TextField("Url");
    TextField username = new TextField("Nazwa użytkownika");
    PasswordField password = new PasswordField("Hasło"); //TODO - change to password and use real encryption
    private VaultEntity vaultEntity;
    private final PasswordEncoder passwordEncoder;
    private final User authenticatedUser;

    public enum Action {
        SAVE,
        DELETE,
        DECRYPT
    }

    public VaultEntityForm(AuthenticatedUser authenticatedUser, PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
        binder.bindInstanceFields(this);

        if (authenticatedUser.get().isPresent()) {
            this.authenticatedUser = authenticatedUser.get().get();
        } else {
            this.authenticatedUser = null;
            Notification.show(Messages.AUTHENTICATED_USER_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        password.addValueChangeListener(e -> validatePassword());

        add(name, url, username, password, createButtonLayout());
        saveButton.addClickListener(e -> validateAndSave());
        deleteButton.addClickListener(e -> validateMasterPasswordDialog(Action.DELETE));
        cancelButton.addClickListener(e -> {
            name.clear();
            url.clear();
            username.clear();
            password.clear();
            fireEvent(new CloseEvent(this));
        });
    }

    public void setVaultEntity(VaultEntity vaultEntity) {
        this.vaultEntity = vaultEntity;
        binder.readBean(vaultEntity);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(vaultEntity);
            if (validatePassword()) {
                validateMasterPasswordDialog(Action.SAVE);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private boolean validatePassword() {
        if (password.getValue() != null && !password.getValue().isEmpty()) {
            password.setInvalid(false);
            password.setErrorMessage(null);
            return true;
        } else {
            password.setInvalid(true);
            password.setErrorMessage(Messages.EMPTY);
            return false;
        }
    }

    public void validateMasterPasswordDialog(Action action) {
        Dialog dialog = new Dialog();
        H3 title = new H3(Messages.MASTER_PASSWORD_REQUIRED);
        PasswordField masterPasswordField = new PasswordField("Hasło główne");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Anuluj");

        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        cancelButton.addClickListener(e -> {
            masterPasswordField.clear();
            dialog.close();
        });

        okButton.addClickListener(e -> {
            if (masterPasswordField.getValue() == null || masterPasswordField.getValue().isEmpty()) {
                masterPasswordField.setInvalid(true);
                masterPasswordField.setErrorMessage(Messages.EMPTY);
                return;
            } else {
                masterPasswordField.setInvalid(false);
                masterPasswordField.setErrorMessage(null);
            }

            if (authenticatedUser != null) {
                String hashedMasterPassword = authenticatedUser.getHashedMasterPassword();
                if (passwordEncoder.matches(masterPasswordField.getValue(), hashedMasterPassword)) {
                    performAction(action);
                    dialog.close();
                } else {
                    Notification.show(Messages.INVALID_MASTER_PASSWORD).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show(Messages.AUTHENTICATED_USER_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            masterPasswordField.clear();
        });

        HorizontalLayout buttons = new HorizontalLayout(okButton, cancelButton);
        VerticalLayout dialogLayout = new VerticalLayout(title, masterPasswordField, buttons);
        dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        dialog.open();
    }

    private void performAction(Action action) {
        if (action == Action.SAVE) {
            //TODO - encrypt and save
            fireEvent(new SaveEvent(this, vaultEntity));
        } else if (action == Action.DELETE) {
            fireEvent(new DeleteEvent(this, vaultEntity));
        } else if (action == Action.DECRYPT) {
            //TODO - decrypt and set visible
        }
    }

}
