package com.github.mateuszmazewski.passwordmanager.views.forms;

import com.github.mateuszmazewski.passwordmanager.data.entity.VaultEntity;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class VaultEntityForm extends EntityForm {
    Binder<VaultEntity> binder = new BeanValidationBinder<>(VaultEntity.class);

    TextField name = new TextField("Nazwa");
    TextField url = new TextField("Url");
    PasswordField password = new PasswordField("Hasło");
    private VaultEntity vaultEntity;

    public VaultEntityForm() {
        super();
        binder.bindInstanceFields(this);

        add(name, url, password, createButtonLayout());
        saveButton.addClickListener(e -> validateAndSave());
        deleteButton.addClickListener(e -> fireEvent(new DeleteEvent(this, vaultEntity)));
        cancelButton.addClickListener(e -> fireEvent(new CloseEvent(this)));
    }

    public void setVaultEntity(VaultEntity vaultEntity) {
        this.vaultEntity = vaultEntity;
        binder.readBean(vaultEntity);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(vaultEntity);
            fireEvent(new SaveEvent(this, vaultEntity));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
