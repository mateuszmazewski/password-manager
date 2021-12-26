package com.github.mateuszmazewski.passwordmanager.views.forms;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.entity.VaultEntity;
import com.github.mateuszmazewski.passwordmanager.security.AESUtil;
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

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class VaultEntityForm extends EntityForm {
    Binder<VaultEntity> binder = new BeanValidationBinder<>(VaultEntity.class);

    TextField name = new TextField("Nazwa");
    TextField url = new TextField("Url");
    TextField username = new TextField("Nazwa użytkownika");
    PasswordField password = new PasswordField("Hasło");
    private VaultEntity vaultEntity;
    private final PasswordEncoder passwordEncoder;
    private final User authenticatedUser;

    public enum Action {
        SAVE,
        DELETE,
        DECRYPT
    }

    public VaultEntityForm(User authenticatedUser, PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUser = authenticatedUser;
        binder.bindInstanceFields(this);

        password.addValueChangeListener(e -> validatePassword());

        add(name, url, username, password, createButtonLayout());
        saveButton.addClickListener(e -> validateAndSave());
        deleteButton.addClickListener(e -> validateMasterPasswordDialog(Action.DELETE));
        cancelButton.addClickListener(e -> {
            clearForm();
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
                String givenMasterPasword = masterPasswordField.getValue();
                if (passwordEncoder.matches(givenMasterPasword, hashedMasterPassword)) {
                    performAction(action, givenMasterPasword);
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

    private void performAction(Action action, String masterPassword) {
        if (action == Action.SAVE) {
            save(masterPassword);
        } else if (action == Action.DELETE) {
            if (vaultEntity != null
                    && vaultEntity.getEncryptedPassword() != null
                    && !vaultEntity.getEncryptedPassword().isEmpty()) {
                clearForm();
                fireEvent(new DeleteEvent(this, vaultEntity));
            }
        } else if (action == Action.DECRYPT) {
            decrypt(masterPassword);
        }
    }

    private void save(String masterPassword) {
        String plainPassword = password.getValue();
        try {
            byte[] salt = AESUtil.generateSalt(8);
            SecretKey key = AESUtil.getKeyFromPassword(masterPassword, new String(salt));
            IvParameterSpec ivParameterSpec = AESUtil.generateIv();
            String algorithm = "AES/CBC/PKCS5Padding";
            String encryptedPassword = AESUtil.encrypt(algorithm, plainPassword, key, ivParameterSpec);

            vaultEntity.setEncryptedPassword(encryptedPassword);
            vaultEntity.setSalt(Base64.getEncoder().encodeToString(salt));
            vaultEntity.setIv(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()));
            vaultEntity.setUserId(authenticatedUser.getId());
            fireEvent(new SaveEvent(this, vaultEntity));
            clearForm();
        } catch (Exception e) {
            Notification.show(Messages.SAVE_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void decrypt(String masterPassword) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(vaultEntity.getIv()));
            byte[] salt = Base64.getDecoder().decode(vaultEntity.getSalt());

            SecretKey key = AESUtil.getKeyFromPassword(masterPassword, new String(salt));
            String algorithm = "AES/CBC/PKCS5Padding";
            String decryptedPassword = AESUtil.decrypt(
                    algorithm, vaultEntity.getEncryptedPassword(), key, ivParameterSpec);
            password.setValue(decryptedPassword);
            setVisible(true);
        } catch (Exception e) {
            Notification.show(Messages.DECRYPTION_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void clearForm() {
        name.clear();
        url.clear();
        username.clear();
        password.clear();

        name.setInvalid(false);
        url.setInvalid(false);
        username.setInvalid(false);
        password.setInvalid(false);
    }

    public void setDeleteButtonVisible(boolean visible) {
        deleteButton.setVisible(visible);
    }

}
