package com.github.mateuszmazewski.passwordmanager.views;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.github.mateuszmazewski.passwordmanager.data.entity.Connection;
import com.github.mateuszmazewski.passwordmanager.data.entity.User;
import com.github.mateuszmazewski.passwordmanager.data.service.ConnectionService;
import com.github.mateuszmazewski.passwordmanager.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Historia logowań")
@Route(value = "history", layout = MainLayout.class)
@PermitAll
public class HistoryView extends VerticalLayout {
    Grid<Connection> grid = new Grid<>(Connection.class);
    TextField filterIp = new TextField("IP");
    private final ConnectionService service;
    private final User authenticatedUser;

    public HistoryView(ConnectionService service, AuthenticatedUser authenticatedUser) {
        this.service = service;
        setSizeFull();

        if (authenticatedUser.get().isPresent()) {
            this.authenticatedUser = authenticatedUser.get().get();
        } else {
            this.authenticatedUser = null;
            Notification.show(Messages.AUTHENTICATED_USER_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        configureGrid();

        add(getToolbar(), getContent());
        updateList();
    }

    private void updateList() {
        if (authenticatedUser != null) {
            if (filterIp.getValue() == null || filterIp.getValue().isEmpty()) {
                grid.setItems(service.findByUserId(authenticatedUser.getId()));
            } else {
                List<Connection> list = new ArrayList<>();
                list.add(service.findByUserIdAndIp(authenticatedUser.getId(), filterIp.getValue()));
                grid.setItems(list);
            }
        } else {
            Notification.show(Messages.AUTHENTICATED_USER_ERROR).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private Component getToolbar() {
        filterIp.setClearButtonVisible(true);
        filterIp.setValueChangeMode(ValueChangeMode.LAZY);
        filterIp.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(filterIp);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(Connection::getIp).setHeader("IP").setSortable(true);
        grid.addColumn(Connection::getUserAgent).setHeader("User-Agent").setSortable(true);
        grid.addColumn(c -> c.getLastConnectionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setHeader("Data ostatniego logowania").setSortable(true);
        grid.addColumn(
                new ComponentRenderer<>(
                        connection -> {
                            Button detailsButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());
                            detailsButton.addClickListener(e -> detailsDialog(connection));
                            return detailsButton;
                        }
                )
        ).setHeader("Wyświetl").setSortable(false);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void detailsDialog(Connection connection) {
        Dialog dialog = new Dialog();
        Label ip = new Label("IP: " + connection.getIp());
        Label userAgent = new Label("User-Agent: " + connection.getUserAgent());
        Label lastConnectionDate = new Label("Data ostatniego logowania: " + connection.getLastConnectionDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Button closeButton = new Button("Zamknij");
        closeButton.addClickListener(e -> dialog.close());

        dialog.add(
                new VerticalLayout(ip, userAgent, lastConnectionDate, closeButton)
        );
        dialog.open();
    }
}
