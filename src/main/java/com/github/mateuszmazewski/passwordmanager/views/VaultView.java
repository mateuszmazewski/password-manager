package com.github.mateuszmazewski.passwordmanager.views;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.entity.VaultEntity;
import com.github.mateuszmazewski.passwordmanager.data.service.VaultEntityService;
import com.github.mateuszmazewski.passwordmanager.security.AuthenticatedUser;
import com.github.mateuszmazewski.passwordmanager.views.forms.VaultEntityForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;

@PageTitle("Sejf")
@Route(value = "/", layout = MainLayout.class)
@PermitAll
public class VaultView extends VerticalLayout {
    Grid<VaultEntity> grid = new Grid<>(VaultEntity.class);
    TextField filterName = new TextField("Nazwa");
    VaultEntityForm form;
    private final VaultEntityService service;
    private final User authenticatedUser;
    private final PasswordEncoder passwordEncoder;

    public VaultView(VaultEntityService service, AuthenticatedUser authenticatedUser, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        setSizeFull();

        if (authenticatedUser.get().isPresent()) {
            this.authenticatedUser = authenticatedUser.get().get();
        } else {
            this.authenticatedUser = null;
            Notification.show(Messages.AUTHENTICATED_USER_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setVaultEntity(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        if (authenticatedUser != null) {
            grid.setItems(service.find(authenticatedUser.getId(), filterName.getValue()));
        } else {
            Notification.show(Messages.AUTHENTICATED_USER_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureForm() {
        form = new VaultEntityForm(authenticatedUser, passwordEncoder);
        form.setWidth("25em");

        form.addListener(VaultEntityForm.SaveEvent.class, this::saveVaultEntity);
        form.addListener(VaultEntityForm.DeleteEvent.class, this::deleteVaultEntity);
        form.addListener(VaultEntityForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveVaultEntity(VaultEntityForm.SaveEvent event) {
        service.update((VaultEntity) event.getEntity());
        updateList();
        closeEditor();
    }

    private void deleteVaultEntity(VaultEntityForm.DeleteEvent event) {
        try {
            service.delete(event.getEntity().getId());
            updateList();
            closeEditor();
        } catch (DataIntegrityViolationException e) {
            Notification.show(Messages.INTEGRITY).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Component getToolbar() {
        filterName.setClearButtonVisible(true);
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterName.addValueChangeListener(e -> updateList());

        Button addButton = new Button("Dodaj nowe dane logowania");
        addButton.addClickListener(e -> addVaultEntity());

        HorizontalLayout toolbar = new HorizontalLayout(filterName, addButton);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        return toolbar;
    }

    private void addVaultEntity() {
        grid.asSingleSelect().clear();
        form.clearForm();
        editVaultEntity(new VaultEntity());
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(VaultEntity::getName).setHeader("Nazwa").setSortable(true);
        grid.addColumn(
                new ComponentRenderer<>(
                        vaultEntity -> {
                            Anchor anchor = new Anchor(vaultEntity.getUrl(), vaultEntity.getUrl());
                            anchor.setTarget("_blank"); // Open in new tab
                            return anchor;
                        }
                )
        ).setHeader("Url").setSortable(false);
        grid.addColumn(
                new ComponentRenderer<>(
                        vaultEntity -> {
                            Button button = new Button("Deszyfruj");
                            button.addClickListener(e -> {
                                form.setVisible(false);
                                form.clearForm();
                                editVaultEntity(vaultEntity);
                            });
                            return button;
                        }
                )
        ).setHeader("Wyświetlenie/Edycja");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void editVaultEntity(VaultEntity vaultEntity) {
        if (vaultEntity == null) {
            closeEditor();
        } else {
            form.setVaultEntity(vaultEntity);
            if (vaultEntity.getEncryptedPassword() == null || vaultEntity.getEncryptedPassword().isEmpty()) { // Adding new entry
                form.setVisible(true);
                form.setDeleteButtonVisible(false);
                form.setCopyButtonVisible(false);
            } else { // Editing existing entry
                form.validateMasterPasswordDialog(VaultEntityForm.Action.DECRYPT);
                form.setDeleteButtonVisible(true);
                form.setCopyButtonVisible(true);
            }
            addClassName("editing");
        }
    }
}
