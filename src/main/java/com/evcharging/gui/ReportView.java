package com.evcharging.gui;

import com.evcharging.model.User;
import com.evcharging.util.DBConnection;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportView {

    private final User loggedInUser;

    private Label totalBookingsLabel;
    private Label confirmedLabel;
    private Label cancelledLabel;
    private Label revenueLabel;

    private Label usersLabel;
    private Label stationsLabel;
    private Label chargersLabel;
    private Label activeChargersLabel;

    private VBox activityBox;

    private Pane backgroundPane;

    private final List<Circle> particles =
            new ArrayList<>();

    private final List<Timeline> particleAnimations =
            new ArrayList<>();

    public ReportView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // =============================================================
    // SHOW
    // =============================================================

    public void show(Stage stage) {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: #050b12;"
        );

        backgroundPane =
                createBackground();

        StackPane center =
                new StackPane();

        center.getChildren().add(
                backgroundPane
        );

        VBox content =
                createContent(stage);

        center.getChildren().add(
                content
        );

        root.setLeft(
                createSidebar(stage)
        );

        root.setCenter(
                center
        );

        Scene scene =
                new Scene(
                        root,
                        1440,
                        900
                );

        scene.setFill(
                Color.rgb(
                        5,
                        11,
                        18
                )
        );

        stage.setTitle(
                "Smart EV Charging • Reports"
        );

        stage.setScene(
                scene
        );

        stage.setMaximized(
                true
        );

        stage.show();

        refreshReports();

        startParticleAnimation();
    }

    // =============================================================
    // SIDEBAR
    // =============================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar =
                new VBox();

        sidebar.setPrefWidth(
                245
        );

        sidebar.setPadding(
                new Insets(
                        28,
                        18,
                        25,
                        18
                )
        );

        sidebar.setSpacing(
                12
        );

        sidebar.setStyle(
                "-fx-background-color: rgba(4,10,17,0.96);" +
                "-fx-border-color: rgba(100,240,215,0.15);" +
                "-fx-border-width: 0 1 0 0;"
        );

        // =========================================================
        // LOGO
        // =========================================================

        VBox logoBox =
                new VBox(4);

        logoBox.setPadding(
                new Insets(
                        0,
                        10,
                        25,
                        10
                )
        );

        Label logo =
                new Label(
                        "⚡ SMART EV"
                );

        logo.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        22
                )
        );

        logo.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        Label logoSub =
                new Label(
                        "SMART GRID CONTROL"
                );

        logoSub.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        logoSub.setTextFill(
                Color.rgb(
                        125,
                        145,
                        160
                )
        );

        logoBox.getChildren().addAll(
                logo,
                logoSub
        );

        sidebar.getChildren().add(
                logoBox
        );

        // =========================================================
        // NAV TITLE
        // =========================================================

        Label navTitle =
                new Label(
                        "CONTROL CENTER"
                );

        navTitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        navTitle.setTextFill(
                Color.rgb(
                        90,
                        110,
                        125
                )
        );

        navTitle.setPadding(
                new Insets(
                        5,
                        10,
                        5,
                        10
                )
        );

        sidebar.getChildren().add(
                navTitle
        );

        // =========================================================
        // BUTTONS
        // =========================================================

        Button dashboard =
                sideButton(
                        "⌂",
                        "Dashboard"
                );

        Button stations =
                sideButton(
                        "▦",
                        "Stations"
                );

        Button chargers =
                sideButton(
                        "⚡",
                        "Chargers"
                );

        Button bookings =
                sideButton(
                        "◫",
                        "Bookings"
                );

        Button users =
                sideButton(
                        "◉",
                        "Users"
                );

        Button reports =
                sideButton(
                        "▤",
                        "Reports"
                );

        Button settings =
                sideButton(
                        "⚙",
                        "Settings"
                );

        reports.setStyle(
                activeSideStyle()
        );

        sidebar.getChildren().addAll(
                dashboard,
                stations,
                chargers,
                bookings,
                users,
                reports,
                settings
        );

        // =========================================================
        // SPACER
        // =========================================================

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                spacer
        );

        // =========================================================
        // SYSTEM STATUS
        // =========================================================

        VBox statusBox =
                new VBox(8);

        statusBox.setPadding(
                new Insets(
                        14
                )
        );

        statusBox.setStyle(
                "-fx-background-color: rgba(100,240,215,0.05);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(100,240,215,0.12);" +
                "-fx-border-radius: 12;"
        );

        HBox statusRow =
                new HBox(8);

        statusRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle statusDot =
                new Circle(
                        4,
                        Color.rgb(
                                100,
                                240,
                                215
                        )
                );

        Label status =
                new Label(
                        "REPORT ENGINE ONLINE"
                );

        status.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        status.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        statusRow.getChildren().addAll(
                statusDot,
                status
        );

        Label systemInfo =
                new Label(
                        "Live database analytics\n"
                                + "Real-time statistics"
                );

        systemInfo.setFont(
                Font.font(
                        "System",
                        9
                )
        );

        systemInfo.setTextFill(
                Color.rgb(
                        125,
                        145,
                        160
                )
        );

        statusBox.getChildren().addAll(
                statusRow,
                systemInfo
        );

        sidebar.getChildren().add(
                statusBox
        );

        // =========================================================
        // LOGOUT
        // =========================================================

        Button logout =
                new Button(
                        "↪   Logout"
                );

        logout.setMaxWidth(
                Double.MAX_VALUE
        );

        logout.setAlignment(
                Pos.CENTER_LEFT
        );

        logout.setPadding(
                new Insets(
                        12,
                        15,
                        12,
                        15
                )
        );

        logout.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        logout.setTextFill(
                Color.rgb(
                        255,
                        125,
                        125
                )
        );

        logout.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
        );

        logout.setOnMouseEntered(
                e -> logout.setStyle(
                        "-fx-background-color: rgba(255,80,80,0.10);" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;"
                )
        );

        logout.setOnMouseExited(
                e -> logout.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;"
                )
        );

        logout.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new LoginView().show(
                            stage
                    );
                }
        );

        sidebar.getChildren().add(
                logout
        );

        // =========================================================
        // NAVIGATION
        // =========================================================

        dashboard.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new AdminDashboardView(
                            loggedInUser
                    ).show(stage);
                }
        );

        stations.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new AdminStationView(
                            loggedInUser
                    ).show(stage);
                }
        );

        chargers.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new ChargerManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );

        bookings.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new BookingManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );

        users.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new UserManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );

        settings.setOnAction(
                e -> {

                    stopParticleAnimation();

                    new SettingsView(
                            loggedInUser
                    ).show(stage);
                }
        );

        return sidebar;
    }

    // =============================================================
    // MAIN CONTENT
    // =============================================================

    private VBox createContent(Stage stage) {

        VBox main =
                new VBox(20);

        main.setPadding(
                new Insets(
                        35,
                        42,
                        35,
                        42
                )
        );

        // =========================================================
        // HEADER
        // =========================================================

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(5);

        Label title =
                new Label(
                        "SMART GRID REPORTS"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        28
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        Label subtitle =
                new Label(
                        "ENERGY ANALYTICS • BOOKING INTELLIGENCE • SYSTEM PERFORMANCE"
                );

        subtitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        subtitle.setTextFill(
                Color.rgb(
                        110,
                        135,
                        150
                )
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox live =
                new HBox(8);

        live.setAlignment(
                Pos.CENTER
        );

        live.setPadding(
                new Insets(
                        9,
                        15,
                        9,
                        15
                )
        );

        live.setStyle(
                "-fx-background-color: rgba(100,240,215,0.07);" +
                "-fx-border-color: rgba(100,240,215,0.18);" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );

        Circle dot =
                new Circle(
                        4,
                        Color.rgb(
                                100,
                                240,
                                215
                        )
                );

        Label liveText =
                new Label(
                        "LIVE ANALYTICS"
                );

        liveText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        liveText.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        live.getChildren().addAll(
                dot,
                liveText
        );

        Button refresh =
                new Button(
                        "⟳  REFRESH DATA"
                );

        refresh.setPadding(
                new Insets(
                        10,
                        16,
                        10,
                        16
                )
        );

        refresh.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        refresh.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        refresh.setStyle(
                "-fx-background-color: rgba(100,240,215,0.08);" +
                "-fx-border-color: rgba(100,240,215,0.20);" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        refresh.setOnAction(
                e -> refreshReports()
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                live,
                refresh
        );

        // =========================================================
        // BOOKING KPI
        // =========================================================

        HBox bookingStats =
                new HBox(15);

        VBox total =
                createStatCard(
                        "TOTAL BOOKINGS",
                        "0",
                        "◫",
                        Color.rgb(
                                100,
                                240,
                                215
                        )
                );

        VBox confirmed =
                createStatCard(
                        "CONFIRMED",
                        "0",
                        "✓",
                        Color.rgb(
                                90,
                                210,
                                150
                        )
                );

        VBox cancelled =
                createStatCard(
                        "CANCELLED",
                        "0",
                        "×",
                        Color.rgb(
                                255,
                                120,
                                120
                        )
                );

        VBox revenue =
                createStatCard(
                        "TOTAL REVENUE",
                        "Rs. 0",
                        "₹",
                        Color.rgb(
                                180,
                                140,
                                255
                        )
                );

        totalBookingsLabel =
                getValueLabel(total);

        confirmedLabel =
                getValueLabel(confirmed);

        cancelledLabel =
                getValueLabel(cancelled);

        revenueLabel =
                getValueLabel(revenue);

        HBox.setHgrow(
                total,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                confirmed,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                cancelled,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                revenue,
                Priority.ALWAYS
        );

        bookingStats.getChildren().addAll(
                total,
                confirmed,
                cancelled,
                revenue
        );

        // =========================================================
        // SYSTEM OVERVIEW
        // =========================================================

        HBox systemStats =
                new HBox(15);

        VBox usersCard =
                createSmallStat(
                        "REGISTERED USERS",
                        "0",
                        "◉"
                );

        VBox stationsCard =
                createSmallStat(
                        "CHARGING STATIONS",
                        "0",
                        "▦"
                );

        VBox chargersCard =
                createSmallStat(
                        "TOTAL CHARGERS",
                        "0",
                        "⚡"
                );

        VBox activeCard =
                createSmallStat(
                        "ACTIVE CHARGERS",
                        "0",
                        "●"
                );

        usersLabel =
                getValueLabel(usersCard);

        stationsLabel =
                getValueLabel(stationsCard);

        chargersLabel =
                getValueLabel(chargersCard);

        activeChargersLabel =
                getValueLabel(activeCard);

        HBox.setHgrow(
                usersCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                stationsCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                chargersCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activeCard,
                Priority.ALWAYS
        );

        systemStats.getChildren().addAll(
                usersCard,
                stationsCard,
                chargersCard,
                activeCard
        );

        // =========================================================
        // LOWER SECTION
        // =========================================================

        HBox lower =
                new HBox(18);

        VBox analytics =
                createAnalyticsPanel();

        activityBox =
                createActivityPanel();

        HBox.setHgrow(
                analytics,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activityBox,
                Priority.ALWAYS
        );

        lower.getChildren().addAll(
                analytics,
                activityBox
        );

        VBox.setVgrow(
                lower,
                Priority.ALWAYS
        );

        main.getChildren().addAll(
                header,
                bookingStats,
                systemStats,
                lower
        );

        return main;
    }

    // =============================================================
    // STAT CARD
    // =============================================================

    private VBox createStatCard(
            String title,
            String value,
            String icon,
            Color accent) {

        VBox card =
                new VBox(6);

        card.setPadding(
                new Insets(
                        17
                )
        );

        card.setMinHeight(
                105
        );

        card.setStyle(
                "-fx-background-color: rgba(10,22,32,0.90);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.12);" +
                "-fx-border-radius: 15;"
        );

        HBox top =
                new HBox();

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        18
                )
        );

        iconLabel.setTextFill(
                accent
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label live =
                new Label(
                        "LIVE"
                );

        live.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        live.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        top.getChildren().addAll(
                iconLabel,
                spacer,
                live
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        25
                )
        );

        valueLabel.setTextFill(
                Color.WHITE
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        titleLabel.setTextFill(
                Color.rgb(
                        120,
                        145,
                        160
                )
        );

        card.getProperties().put(
                "valueLabel",
                valueLabel
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                titleLabel
        );

        return card;
    }

    // =============================================================
    // SMALL STAT
    // =============================================================

    private VBox createSmallStat(
            String title,
            String value,
            String icon) {

        VBox card =
                new VBox(5);

        card.setPadding(
                new Insets(
                        14
                )
        );

        card.setStyle(
                "-fx-background-color: rgba(8,18,27,0.88);" +
                "-fx-background-radius: 13;" +
                "-fx-border-color: rgba(100,240,215,0.08);" +
                "-fx-border-radius: 13;"
        );

        HBox row =
                new HBox(8);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        15
                )
        );

        iconLabel.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        titleLabel.setTextFill(
                Color.rgb(
                        120,
                        145,
                        160
                )
        );

        row.getChildren().addAll(
                iconLabel,
                titleLabel
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        22
                )
        );

        valueLabel.setTextFill(
                Color.WHITE
        );

        card.getProperties().put(
                "valueLabel",
                valueLabel
        );

        card.getChildren().addAll(
                row,
                valueLabel
        );

        return card;
    }

    private Label getValueLabel(
            VBox card) {

        return (Label)
                card.getProperties()
                        .get("valueLabel");
    }

    // =============================================================
    // ANALYTICS PANEL
    // =============================================================

    private VBox createAnalyticsPanel() {

        VBox panel =
                new VBox(15);

        panel.setPadding(
                new Insets(
                        20
                )
        );

        panel.setPrefWidth(
                620
        );

        panel.setStyle(
                "-fx-background-color: rgba(8,18,27,0.92);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.10);" +
                "-fx-border-radius: 15;"
        );

        Label title =
                new Label(
                        "ENERGY & BOOKING ANALYTICS"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        Label subtitle =
                new Label(
                        "SYSTEM PERFORMANCE OVERVIEW"
                );

        subtitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        subtitle.setTextFill(
                Color.rgb(
                        100,
                        125,
                        140
                )
        );

        VBox graph =
                new VBox(12);

        graph.setPadding(
                new Insets(
                        15
                )
        );

        graph.setStyle(
                "-fx-background-color: rgba(100,240,215,0.025);" +
                "-fx-background-radius: 12;"
        );

        addMetricBar(
                graph,
                "BOOKING UTILIZATION",
                0.78,
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        addMetricBar(
                graph,
                "CHARGER UTILIZATION",
                0.64,
                Color.rgb(
                        90,
                        190,
                        255
                )
        );

        addMetricBar(
                graph,
                "GRID AVAILABILITY",
                0.91,
                Color.rgb(
                        180,
                        140,
                        255
                )
        );

        addMetricBar(
                graph,
                "SYSTEM HEALTH",
                0.96,
                Color.rgb(
                        90,
                        210,
                        150
                )
        );

        panel.getChildren().addAll(
                title,
                subtitle,
                graph
        );

        return panel;
    }

    private void addMetricBar(
            VBox parent,
            String title,
            double progress,
            Color color) {

        HBox header =
                new HBox();

        Label label =
                new Label(
                        title
                );

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        label.setTextFill(
                Color.rgb(
                        150,
                        170,
                        180
                )
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label percentage =
                new Label(
                        (int)
                                (progress * 100)
                                + "%"
                );

        percentage.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        percentage.setTextFill(
                color
        );

        header.getChildren().addAll(
                label,
                spacer,
                percentage
        );

        ProgressBar bar =
                new ProgressBar(
                        progress
                );

        bar.setMaxWidth(
                Double.MAX_VALUE
        );

        bar.setPrefHeight(
                8
        );

        bar.setStyle(
                "-fx-accent: rgb("
                        + (int) (color.getRed() * 255)
                        + ","
                        + (int) (color.getGreen() * 255)
                        + ","
                        + (int) (color.getBlue() * 255)
                        + ");"
        );

        parent.getChildren().addAll(
                header,
                bar
        );
    }

    // =============================================================
    // ACTIVITY PANEL
    // =============================================================

    private VBox createActivityPanel() {

        VBox panel =
                new VBox(12);

        panel.setPadding(
                new Insets(
                        20
                )
        );

        panel.setPrefWidth(
                500
        );

        panel.setStyle(
                "-fx-background-color: rgba(8,18,27,0.92);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.10);" +
                "-fx-border-radius: 15;"
        );

        Label title =
                new Label(
                        "SYSTEM ACTIVITY"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        Label subtitle =
                new Label(
                        "LATEST DATABASE EVENTS"
                );

        subtitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        subtitle.setTextFill(
                Color.rgb(
                        100,
                        125,
                        140
                )
        );

        panel.getChildren().addAll(
                title,
                subtitle
        );

        addActivity(
                panel,
                "DATABASE",
                "Report data synchronized",
                "ONLINE",
                Color.rgb(
                        100,
                        240,
                        215
                )
        );

        addActivity(
                panel,
                "BOOKINGS",
                "Booking records monitored",
                "ACTIVE",
                Color.rgb(
                        90,
                        190,
                        255
                )
        );

        addActivity(
                panel,
                "CHARGERS",
                "Charging network monitored",
                "LIVE",
                Color.rgb(
                        180,
                        140,
                        255
                )
        );

        addActivity(
                panel,
                "GRID",
                "Smart grid connection stable",
                "STABLE",
                Color.rgb(
                        90,
                        210,
                        150
                )
        );

        return panel;
    }

    private void addActivity(
            VBox parent,
            String category,
            String message,
            String status,
            Color color) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        4,
                        color
                );

        dot.setEffect(
                new DropShadow(
                        8,
                        color
                )
        );

        VBox text =
                new VBox(2);

        Label categoryLabel =
                new Label(
                        category
                );

        categoryLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        categoryLabel.setTextFill(
                color
        );

        Label messageLabel =
                new Label(
                        message
                );

        messageLabel.setFont(
                Font.font(
                        "System",
                        9
                )
        );

        messageLabel.setTextFill(
                Color.rgb(
                        150,
                        170,
                        180
                )
        );

        text.getChildren().addAll(
                categoryLabel,
                messageLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label statusLabel =
                new Label(
                        status
                );

        statusLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        statusLabel.setTextFill(
                color
        );

        row.getChildren().addAll(
                dot,
                text,
                spacer,
                statusLabel
        );

        parent.getChildren().add(
                row
        );
    }

    // =============================================================
    // DATABASE REPORTS
    // =============================================================

    private void refreshReports() {

        loadBookingStats();

        loadSystemStats();
    }

    // =============================================================
    // BOOKING STATS
    // =============================================================

    private void loadBookingStats() {

        String sql =
                "SELECT " +
                "COUNT(*) AS total, " +
                "COALESCE(SUM(CASE " +
                "WHEN UPPER(status) = 'CONFIRMED' " +
                "THEN 1 ELSE 0 END), 0) AS confirmed, " +
                "COALESCE(SUM(CASE " +
                "WHEN UPPER(status) = 'CANCELLED' " +
                "THEN 1 ELSE 0 END), 0) AS cancelled, " +
                "COALESCE(SUM(cost), 0) AS revenue " +
                "FROM bookings";

        try (Connection con =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {

                int total =
                        rs.getInt("total");

                int confirmed =
                        rs.getInt("confirmed");

                int cancelled =
                        rs.getInt("cancelled");

                double revenue =
                        rs.getDouble("revenue");

                totalBookingsLabel.setText(
                        String.valueOf(
                                total
                        )
                );

                confirmedLabel.setText(
                        String.valueOf(
                                confirmed
                        )
                );

                cancelledLabel.setText(
                        String.valueOf(
                                cancelled
                        )
                );

                revenueLabel.setText(
                        String.format(
                                "Rs. %.2f",
                                revenue
                        )
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to load booking reports: "
                            + e.getMessage()
            );
        }
    }

    // =============================================================
    // SYSTEM STATS
    // =============================================================

    private void loadSystemStats() {

        int users =
                getCount(
                        "SELECT COUNT(*) FROM users"
                );

        int stations =
                getCount(
                        "SELECT COUNT(*) FROM stations"
                );

        int chargers =
                getCount(
                        "SELECT COUNT(*) FROM chargers"
                );

        int active =
                getCount(
                        "SELECT COUNT(*) FROM chargers " +
                        "WHERE available = TRUE"
                );

        usersLabel.setText(
                String.valueOf(
                        users
                )
        );

        stationsLabel.setText(
                String.valueOf(
                        stations
                )
        );

        chargersLabel.setText(
                String.valueOf(
                        chargers
                )
        );

        activeChargersLabel.setText(
                String.valueOf(
                        active
                )
        );
    }

    private int getCount(
            String sql) {

        try (Connection con =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Report query failed: "
                            + e.getMessage()
            );
        }

        return 0;
    }

    // =============================================================
    // BACKGROUND
    // =============================================================

    private Pane createBackground() {

        Pane pane =
                new Pane();

        pane.setMouseTransparent(
                true
        );

        pane.setStyle(
                "-fx-background-color: #050b12;"
        );

        // Grid

        for (int x = 0; x < 1600; x += 55) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            1000
                    );

            line.setStroke(
                    Color.rgb(
                            40,
                            90,
                            105,
                            0.10
                    )
            );

            pane.getChildren().add(
                    line
            );
        }

        for (int y = 0; y < 1000; y += 55) {

            Line line =
                    new Line(
                            0,
                            y,
                            1600,
                            y
                    );

            line.setStroke(
                    Color.rgb(
                            40,
                            90,
                            105,
                            0.10
                    )
            );

            pane.getChildren().add(
                    line
            );
        }

        // Glow

        Circle glow1 =
                new Circle(
                        450,
                        280,
                        240
                );

        glow1.setFill(
                Color.rgb(
                        0,
                        220,
                        200,
                        0.025
                )
        );

        Circle glow2 =
                new Circle(
                        1200,
                        700,
                        260
                );

        glow2.setFill(
                Color.rgb(
                        70,
                        130,
                        255,
                        0.025
                )
        );

        pane.getChildren().addAll(
                glow1,
                glow2
        );

        // Particles

        for (int i = 0; i < 45; i++) {

            Circle particle =
                    new Circle(
                            1.5
                    );

            particle.setFill(
                    Color.rgb(
                            100,
                            240,
                            215,
                            0.30
                    )
            );

            particle.setLayoutX(
                    250 +
                            Math.random() * 1200
            );

            particle.setLayoutY(
                    Math.random() * 900
            );

            particles.add(
                    particle
            );

            pane.getChildren().add(
                    particle
            );
        }

        return pane;
    }

    // =============================================================
    // PARTICLE ANIMATION
    // =============================================================

    private void startParticleAnimation() {

        stopParticleAnimation();

        for (Circle particle :
                particles) {

            double start =
                    particle.getLayoutY();

            double movement =
                    60 +
                            Math.random() * 120;

            Timeline animation =
                    new Timeline(
                            new KeyFrame(
                                    Duration.ZERO,
                                    new KeyValue(
                                            particle.translateYProperty(),
                                            0
                                    )
                            ),
                            new KeyFrame(
                                    Duration.seconds(
                                            2.5 +
                                                    Math.random() * 3
                                    ),
                                    new KeyValue(
                                            particle.translateYProperty(),
                                            -movement
                                    )
                            )
                    );

            animation.setAutoReverse(
                    true
            );

            animation.setCycleCount(
                    Animation.INDEFINITE
            );

            animation.play();

            particleAnimations.add(
                    animation
            );
        }
    }

    private void stopParticleAnimation() {

        for (Timeline animation :
                particleAnimations) {

            animation.stop();
        }

        particleAnimations.clear();
    }

    // =============================================================
    // SIDEBAR BUTTON
    // =============================================================

    private Button sideButton(
            String icon,
            String text) {

        Button button =
                new Button(
                        icon + "   " + text
                );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        12,
                        15,
                        12,
                        15
                )
        );

        button.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        button.setTextFill(
                Color.rgb(
                        145,
                        165,
                        175
                )
        );

        button.setStyle(
                normalSideStyle()
        );

        button.setOnMouseEntered(
                e -> {

                    if (!button.getStyle().equals(
                            activeSideStyle()
                    )) {

                        button.setStyle(
                                "-fx-background-color: rgba(100,240,215,0.07);" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;"
                        );
                    }
                }
        );

        button.setOnMouseExited(
                e -> {

                    if (!button.getStyle().equals(
                            activeSideStyle()
                    )) {

                        button.setStyle(
                                normalSideStyle()
                        );
                    }
                }
        );

        return button;
    }

    private String normalSideStyle() {

        return
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;";
    }

    private String activeSideStyle() {

        return
                "-fx-background-color: rgba(100,240,215,0.12);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: rgba(100,240,215,0.25);" +
                "-fx-border-width: 0 0 0 3;" +
                "-fx-cursor: hand;";
    }
}