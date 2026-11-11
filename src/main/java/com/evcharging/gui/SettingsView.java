package com.evcharging.gui;

import com.evcharging.model.User;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SettingsView {

    private final User loggedInUser;

    private Pane backgroundPane;
    private Timeline animation;

    private final List<Circle> particles =
            new ArrayList<>();

    public SettingsView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #050A0F;"
        );

        // =========================================================
        // BACKGROUND
        // =========================================================

        backgroundPane = new Pane();

        createBackground();

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = createSidebar(stage);

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = createContent(stage);

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                "-fx-background-color: transparent;"
        );

        root.setLeft(sidebar);
        root.setCenter(scrollPane);

        // Background behind everything
        StackPaneWithBackground wrapper =
                new StackPaneWithBackground(
                        backgroundPane,
                        root
                );

        Scene scene =
                new Scene(
                        wrapper,
                        1440,
                        900
                );

        stage.setTitle(
                "Smart EV Charging - Settings"
        );

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        startAnimation();
    }


    // =============================================================
    // SIDEBAR
    // =============================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar =
                new VBox(12);

        sidebar.setPadding(
                new Insets(30, 18, 25, 18)
        );

        sidebar.setPrefWidth(245);

        sidebar.setStyle(
                "-fx-background-color: rgba(5,15,22,0.96);" +
                "-fx-border-color: rgba(70,220,210,0.18);" +
                "-fx-border-width: 0 1 0 0;"
        );

        // Logo
        Label logo =
                new Label("⚡");

        logo.setStyle(
                "-fx-font-size: 34px;" +
                "-fx-text-fill: #55F5D7;"
        );

        Label title =
                new Label("SMART EV");

        title.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitle =
                new Label(
                        "CHARGING INTELLIGENCE"
                );

        subtitle.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: #61DCC8;" +
                "-fx-letter-spacing: 1px;"
        );

        VBox branding =
                new VBox(2, logo, title, subtitle);

        branding.setPadding(
                new Insets(0, 0, 25, 8)
        );

        sidebar.getChildren().add(branding);

        // Navigation
        Button dashboard =
                createNavButton(
                        "▣   Dashboard"
                );

        Button stations =
                createNavButton(
                        "◉   Stations"
                );

        Button chargers =
                createNavButton(
                        "⚡   Chargers"
                );

        Button bookings =
                createNavButton(
                        "▤   Bookings"
                );

        Button users =
                createNavButton(
                        "♙   Users"
                );

        Button reports =
                createNavButton(
                        "◈   Reports"
                );

        Button settings =
                createNavButton(
                        "⚙   Settings"
                );

        settings.setStyle(
                "-fx-background-color: rgba(70,220,200,0.15);" +
                "-fx-text-fill: #65F5DA;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(80,240,220,0.35);" +
                "-fx-border-radius: 10;"
        );

        dashboard.setOnAction(e ->
                new AdminDashboardView(
                        loggedInUser
                ).show(stage)
        );

        stations.setOnAction(e ->
                new AdminStationView(
                        loggedInUser
                ).show(stage)
        );

        chargers.setOnAction(e ->
                new ChargerManagementView(
                        loggedInUser
                ).show(stage)
        );

        bookings.setOnAction(e ->
                new BookingManagementView(
                        loggedInUser
                ).show(stage)
        );

        users.setOnAction(e ->
                new UserManagementView(
                        loggedInUser
                ).show(stage)
        );

        reports.setOnAction(e ->
                new ReportView(
                        loggedInUser
                ).show(stage)
        );

        settings.setOnAction(e -> {
            // Already on settings page
        });

        sidebar.getChildren().addAll(
                dashboard,
                stations,
                chargers,
                bookings,
                users,
                reports,
                settings
        );

        VBox spacer =
                new VBox();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(spacer);

        Label system =
                new Label(
                        "● SYSTEM ONLINE"
                );

        system.setStyle(
                "-fx-text-fill: #62E9C9;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Button logout =
                createNavButton(
                        "↪   Logout"
                );

        logout.setStyle(
                "-fx-background-color: rgba(255,80,90,0.08);" +
                "-fx-text-fill: #FF7C85;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10;"
        );

        logout.setOnAction(e -> {

            stopAnimation();

            new LoginView().show(stage);
        });

        sidebar.getChildren().addAll(
                system,
                logout
        );

        return sidebar;
    }


    private Button createNavButton(
            String text) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(13, 15, 13, 15)
        );

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #8FA9B5;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: rgba(70,220,200,0.10);" +
                        "-fx-text-fill: #65F5DA;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 10;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #8FA9B5;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 10;"
                )
        );

        return button;
    }


    // =============================================================
    // MAIN CONTENT
    // =============================================================

    private VBox createContent(Stage stage) {

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(38, 45, 50, 45)
        );

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        // Header
        Label heading =
                new Label("SYSTEM SETTINGS");

        heading.setStyle(
                "-fx-font-size: 31px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label description =
                new Label(
                        "Configure and monitor your Smart EV Charging environment"
                );

        description.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #76929E;"
        );

        VBox header =
                new VBox(5,
                        heading,
                        description
                );

        content.getChildren().add(header);

        // =========================================================
        // SYSTEM STATUS
        // =========================================================

        HBox statusRow =
                new HBox(18);

        statusRow.getChildren().addAll(
                createStatusCard(
                        "●",
                        "SYSTEM STATUS",
                        "ONLINE",
                        "#65F5DA"
                ),
                createStatusCard(
                        "◉",
                        "DATABASE",
                        "CONNECTED",
                        "#65F5DA"
                ),
                createStatusCard(
                        "⚡",
                        "ENERGY GRID",
                        "STABLE",
                        "#65F5DA"
                )
        );

        content.getChildren().add(statusRow);

        // =========================================================
        // SETTINGS CARDS
        // =========================================================

        HBox settingsRow =
                new HBox(20);

        VBox application =
                createSettingsSection(
                        "APPLICATION",
                        "Core application preferences"
                );

        application.getChildren().addAll(
                createSettingItem(
                        "Application Name",
                        "Smart EV Charging Slot Booking System"
                ),
                createSettingItem(
                        "Interface",
                        "JavaFX Premium Dashboard"
                ),
                createSettingItem(
                        "Theme",
                        "Dark Smart Grid"
                )
        );

        VBox database =
                createSettingsSection(
                        "DATABASE",
                        "MySQL connection configuration"
                );

        database.getChildren().addAll(
                createSettingItem(
                        "Database",
                        "ev_charging_db"
                ),
                createSettingItem(
                        "Connection",
                        "JDBC / MySQL"
                ),
                createSettingItem(
                        "Status",
                        "Connected"
                )
        );

        HBox.setHgrow(
                application,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                database,
                Priority.ALWAYS
        );

        settingsRow.getChildren().addAll(
                application,
                database
        );

        content.getChildren().add(settingsRow);

        // =========================================================
        // SECURITY + BOOKING SETTINGS
        // =========================================================

        HBox secondRow =
                new HBox(20);

        VBox security =
                createSettingsSection(
                        "SECURITY",
                        "User access and authentication"
                );

        security.getChildren().addAll(
                createSettingItem(
                        "Authentication",
                        "Email + Password"
                ),
                createSettingItem(
                        "Access Control",
                        "Role Based"
                ),
                createSettingItem(
                        "Login Protection",
                        "Enabled"
                )
        );

        VBox booking =
                createSettingsSection(
                        "BOOKING ENGINE",
                        "Charging slot configuration"
                );

        booking.getChildren().addAll(
                createSettingItem(
                        "Slot Booking",
                        "Enabled"
                ),
                createSettingItem(
                        "Payment Processing",
                        "Enabled"
                ),
                createSettingItem(
                        "Availability Check",
                        "Real-time"
                )
        );

        HBox.setHgrow(
                security,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                booking,
                Priority.ALWAYS
        );

        secondRow.getChildren().addAll(
                security,
                booking
        );

        content.getChildren().add(secondRow);

        // =========================================================
        // PROJECT INFORMATION
        // =========================================================

        VBox projectCard =
                new VBox(12);

        projectCard.setPadding(
                new Insets(24)
        );

        projectCard.setStyle(
                "-fx-background-color: rgba(10,22,30,0.88);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(80,220,205,0.20);" +
                "-fx-border-radius: 18;"
        );

        Label projectTitle =
                new Label(
                        "PROJECT INFORMATION"
                );

        projectTitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #65F5DA;"
        );

        Label project =
                new Label(
                        "Smart EV Charging Slot Booking System"
                );

        project.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label technology =
                new Label(
                        "Java • JavaFX • JDBC • MySQL • OOP • Exception Handling • Multithreading"
                );

        technology.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #839BA5;"
        );

        Label user =
                new Label(
                        "Current session: " +
                        (loggedInUser != null
                                ? loggedInUser.getName()
                                : "Administrator")
                );

        user.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #61DCC8;"
        );

        projectCard.getChildren().addAll(
                projectTitle,
                project,
                technology,
                user
        );

        content.getChildren().add(
                projectCard
        );

        // =========================================================
        // FOOTER
        // =========================================================

        Label footer =
                new Label(
                        "SMART EV • CHARGING INTELLIGENCE • SMART MOBILITY"
                );

        footer.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: #49616B;"
        );

        footer.setPadding(
                new Insets(8, 0, 20, 0)
        );

        content.getChildren().add(
                footer
        );

        return content;
    }


    // =============================================================
    // STATUS CARD
    // =============================================================

    private VBox createStatusCard(
            String icon,
            String title,
            String value,
            String color) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefHeight(105);

        card.setStyle(
                "-fx-background-color: rgba(10,24,32,0.90);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: rgba(80,220,205,0.18);" +
                "-fx-border-radius: 16;"
        );

        HBox top =
                new HBox(10);

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-text-fill: " + color + ";"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #718994;"
        );

        top.getChildren().addAll(
                iconLabel,
                titleLabel
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + color + ";"
        );

        card.getChildren().addAll(
                top,
                valueLabel
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        return card;
    }


    // =============================================================
    // SETTINGS SECTION
    // =============================================================

    private VBox createSettingsSection(
            String title,
            String subtitle) {

        VBox box =
                new VBox(15);

        box.setPadding(
                new Insets(23)
        );

        box.setMinHeight(250);

        box.setStyle(
                "-fx-background-color: rgba(9,21,29,0.92);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(80,220,205,0.18);" +
                "-fx-border-radius: 18;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #65F5DA;"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #6C858F;"
        );

        box.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        return box;
    }


    // =============================================================
    // SETTING ITEM
    // =============================================================

    private HBox createSettingItem(
            String name,
            String value) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(12, 14, 12, 14)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.025);" +
                "-fx-background-radius: 10;"
        );

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #9BB0B8;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #E5F4F5;"
        );

        HBox spacer =
                new HBox();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                nameLabel,
                spacer,
                valueLabel
        );

        return row;
    }


    // =============================================================
    // BACKGROUND
    // =============================================================

    private void createBackground() {

        backgroundPane.setMouseTransparent(true);

        // Grid
        for (int x = 0; x <= 1600; x += 70) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            1000
                    );

            line.setStroke(
                    Color.rgb(
                            70,
                            180,
                            180,
                            0.07
                    )
            );

            backgroundPane
                    .getChildren()
                    .add(line);
        }

        for (int y = 0; y <= 1000; y += 70) {

            Line line =
                    new Line(
                            0,
                            y,
                            1600,
                            y
                    );

            line.setStroke(
                    Color.rgb(
                            70,
                            180,
                            180,
                            0.07
                    )
            );

            backgroundPane
                    .getChildren()
                    .add(line);
        }

        // Particles
        for (int i = 0; i < 45; i++) {

            Circle particle =
                    new Circle(
                            1 + Math.random() * 2
                    );

            particle.setFill(
                    Color.rgb(
                            80,
                            230,
                            210,
                            0.35
                    )
            );

            particle.setLayoutX(
                    Math.random() * 1500
            );

            particle.setLayoutY(
                    Math.random() * 900
            );

            particles.add(particle);

            backgroundPane
                    .getChildren()
                    .add(particle);
        }
    }


    // =============================================================
    // ANIMATION
    // =============================================================

    private void startAnimation() {

        animation =
                new Timeline(
                        new KeyFrame(
                                Duration.millis(50),
                                e -> animateParticles()
                        )
                );

        animation.setCycleCount(
                Animation.INDEFINITE
        );

        animation.play();
    }


    private void animateParticles() {

        for (Circle particle : particles) {

            particle.setTranslateY(
                    particle.getTranslateY()
                            - 0.15
            );

            if (particle.getTranslateY() < -20) {

                particle.setTranslateY(20);
            }
        }
    }


    private void stopAnimation() {

        if (animation != null) {
            animation.stop();
        }
    }


    // =============================================================
    // SIMPLE BACKGROUND STACK
    // =============================================================

    private static class StackPaneWithBackground
            extends javafx.scene.layout.StackPane {

        StackPaneWithBackground(
                Pane background,
                BorderPane content) {

            getChildren().addAll(
                    background,
                    content
            );
        }
    }
}
