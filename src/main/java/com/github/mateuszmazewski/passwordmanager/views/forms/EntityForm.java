package com.github.mateuszmazewski.passwordmanager.views.forms;

import com.github.mateuszmazewski.passwordmanager.data.AbstractEntity;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

public class EntityForm extends FormLayout {
    Button saveButton = new Button("Zapisz");
    Button deleteButton = new Button("Usuń");
    Button cancelButton = new Button("Anuluj");

    protected Component createButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        //saveButton.addClickShortcut(Key.ENTER);
        //cancelButton.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(saveButton, deleteButton, cancelButton);
    }

    public static abstract class EntityFormEvent extends ComponentEvent<EntityForm> {
        private final AbstractEntity entity;

        protected EntityFormEvent(EntityForm source, AbstractEntity entity) {
            super(source, false);
            this.entity = entity;
        }

        public AbstractEntity getEntity() {
            return entity;
        }
    }

    public static class SaveEvent extends EntityFormEvent {
        SaveEvent(EntityForm source, AbstractEntity entity) {
            super(source, entity);
        }
    }

    public static class DeleteEvent extends EntityFormEvent {
        DeleteEvent(EntityForm source, AbstractEntity entity) {
            super(source, entity);
        }

    }

    public static class CloseEvent extends EntityFormEvent {
        CloseEvent(EntityForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
