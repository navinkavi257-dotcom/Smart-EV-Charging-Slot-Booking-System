package com.evcharging.gui;

import com.evcharging.model.User;
import com.evcharging.service.UserService;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
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

import java.util.ArrayList;
import java.util.List;

public class UserManagementView {

    private final User loggedInUser;
    private final UserService userService = new UserService();

    private TableView<User> userTable;

    private Label totalUsersLabel;
    private Label adminUsersLabel;
    private Label regularUsersLabel;

    private Pane backgroundPane;

    private Timeline backgroundAnimation;

    private final List<Circle> particles =
            new ArrayList<>();


    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    public UserManagementView(User loggedInUser) {

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


        // =========================================================
        // BACKGROUND
        // =========================================================

        backgroundPane =
                createBackground();


        StackPane centerLayer =
                new StackPane();

        centerLayer.getChildren().add(
                backgroundPane
        );


        VBox content =
                createMainContent(stage);

        StackPane.setAlignment(
                content,
                Pos.TOP_LEFT
        );

        centerLayer.getChildren().add(
                content
        );


        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar =
                createSidebar(stage);

        root.setLeft(sidebar);

        root.setCenter(
                centerLayer
        );


        // =========================================================
        // SCENE
        // =========================================================

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
                "Smart EV Charging • User Management"
        );

        stage.setMinWidth(1100);
        stage.setMinHeight(700);


        /*
         * IMPORTANT:
         *
         * Do NOT use:
         *
         * stage.setMaximized(true);
         *
         * This keeps the same normal window sizing
         * as the corrected Booking Management page.
         */

        stage.setScene(scene);

        stage.show();


        // =========================================================
        // LOAD DATA
        // =========================================================

        refreshUsers();


        // =========================================================
        // START BACKGROUND
        // =========================================================

        startBackgroundAnimation();


        // =========================================================
        // ENTRANCE ANIMATION
        // =========================================================

        playEntrance(
                sidebar,
                content
        );
    }


    // =============================================================
    // SIDEBAR
    // =============================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar =
                new VBox();

        sidebar.setPrefWidth(245);

        sidebar.setMinWidth(245);

        sidebar.setMaxWidth(245);

        sidebar.setPadding(
                new Insets(
                        28,
                        18,
                        25,
                        18
                )
        );

        sidebar.setSpacing(12);

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
        // NAVIGATION TITLE
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
        // NAVIGATION BUTTONS
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


        // Current page

        users.setStyle(
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
                        "SYSTEM ONLINE"
                );

        status.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
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
                        "Database connected\n" +
                        "Grid communication stable"
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

                    stopBackgroundAnimation();

                    new LoginView()
                            .show(stage);
                }
        );


        sidebar.getChildren().add(
                logout
        );


        // =========================================================
        // NAVIGATION ACTIONS
        // =========================================================

        dashboard.setOnAction(
                e -> {

                    stopBackgroundAnimation();

                    new AdminDashboardView(
                            loggedInUser
                    ).show(stage);
                }
        );


        stations.setOnAction(
                e -> {

                    stopBackgroundAnimation();

                    new AdminStationView(
                            loggedInUser
                    ).show(stage);
                }
        );


        chargers.setOnAction(
                e -> {

                    stopBackgroundAnimation();

                    new ChargerManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );


        bookings.setOnAction(
                e -> {

                    stopBackgroundAnimation();

                    new BookingManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );


        reports.setOnAction(
                e -> {

                    stopBackgroundAnimation();

                    new ReportView(
                            loggedInUser
                    ).show(stage);
                }
        );


        settings.setOnAction(
                e -> {

                    stopBackgroundAnimation();

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

    private VBox createMainContent(
            Stage stage
    ) {

        VBox main =
                new VBox(22);

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
                        "USER MANAGEMENT"
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
                        "SMART MOBILITY • USER NETWORK • ACCESS CONTROL"
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


        Region headerSpacer =
                new Region();

        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );


        // =========================================================
        // ONLINE INDICATOR
        // =========================================================

        HBox online =
                new HBox(8);

        online.setAlignment(
                Pos.CENTER
        );

        online.setPadding(
                new Insets(
                        9,
                        15,
                        9,
                        15
                )
        );

        online.setStyle(
                "-fx-background-color: rgba(100,240,215,0.07);" +
                "-fx-border-color: rgba(100,240,215,0.18);" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );


        Circle onlineDot =
                new Circle(
                        4,
                        Color.rgb(
                                100,
                                240,
                                215
                        )
                );


        Label onlineText =
                new Label(
                        "USER DATABASE ONLINE"
                );

        onlineText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        onlineText.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );


        online.getChildren().addAll(
                onlineDot,
                onlineText
        );


        // =========================================================
        // REFRESH
        // =========================================================

        Button refresh =
                new Button(
                        "⟳  REFRESH"
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


        refresh.setOnMouseEntered(
                e -> refresh.setStyle(
                        "-fx-background-color: rgba(100,240,215,0.15);" +
                        "-fx-border-color: rgba(100,240,215,0.40);" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                )
        );


        refresh.setOnMouseExited(
                e -> refresh.setStyle(
                        "-fx-background-color: rgba(100,240,215,0.08);" +
                        "-fx-border-color: rgba(100,240,215,0.20);" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                )
        );


        refresh.setOnAction(
                e -> refreshUsers()
        );


        // =========================================================
        // ADD ADMIN
        // =========================================================

        Button addAdmin =
                new Button(
                        "+  ADD ADMIN"
                );

        addAdmin.setPadding(
                new Insets(
                        10,
                        16,
                        10,
                        16
                )
        );

        addAdmin.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        addAdmin.setTextFill(
                Color.rgb(
                        180,
                        140,
                        255
                )
        );

        addAdmin.setStyle(
                "-fx-background-color: rgba(180,140,255,0.08);" +
                "-fx-border-color: rgba(180,140,255,0.25);" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        addAdmin.setOnMouseEntered(
                e -> addAdmin.setStyle(
                        "-fx-background-color: rgba(180,140,255,0.16);" +
                        "-fx-border-color: rgba(180,140,255,0.45);" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                )
        );

        addAdmin.setOnMouseExited(
                e -> addAdmin.setStyle(
                        "-fx-background-color: rgba(180,140,255,0.08);" +
                        "-fx-border-color: rgba(180,140,255,0.25);" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                )
        );

        addAdmin.setOnAction(
                e -> showCreateAdminDialog()
        );
        // =========================================================
        // ADD HEADER ITEMS
        // =========================================================

        header.getChildren().addAll(
                titleBox,
                headerSpacer,
                online,
                refresh,
                addAdmin
        );

        // =========================================================
        // KPI CARDS
        // =========================================================

        HBox stats =
                new HBox(15);


        VBox totalCard =
                createStatCard(
                        "TOTAL USERS",
                        "0",
                        "◉",
                        Color.rgb(
                                100,
                                240,
                                215
                        )
                );


        VBox adminCard =
                createStatCard(
                        "ADMIN ACCOUNTS",
                        "0",
                        "◆",
                        Color.rgb(
                                170,
                                130,
                                255
                        )
                );


        VBox regularCard =
                createStatCard(
                        "REGULAR USERS",
                        "0",
                        "●",
                        Color.rgb(
                                90,
                                190,
                                255
                        )
                );


        totalUsersLabel =
                (Label) totalCard
                        .getProperties()
                        .get("valueLabel");


        adminUsersLabel =
                (Label) adminCard
                        .getProperties()
                        .get("valueLabel");


        regularUsersLabel =
                (Label) regularCard
                        .getProperties()
                        .get("valueLabel");


        HBox.setHgrow(
                totalCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                adminCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                regularCard,
                Priority.ALWAYS
        );


        stats.getChildren().addAll(
                totalCard,
                adminCard,
                regularCard
        );


        // =========================================================
        // INFORMATION
        // =========================================================

        HBox information =
                new HBox(15);


        VBox networkCard =
                createNetworkCard();


        VBox accessCard =
                createAccessCard();


        HBox.setHgrow(
                networkCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                accessCard,
                Priority.ALWAYS
        );


        information.getChildren().addAll(
                networkCard,
                accessCard
        );


        // =========================================================
        // TABLE
        // =========================================================

        VBox tableCard =
                createUserTable();


        VBox.setVgrow(
                tableCard,
                Priority.ALWAYS
        );


        main.getChildren().addAll(
                header,
                stats,
                information,
                tableCard
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
            Color accent
    ) {

        VBox card =
                new VBox(7);

        card.setPadding(
                new Insets(20)
        );

        card.setMinHeight(
                115
        );

        card.setStyle(
                "-fx-background-color: rgba(10,22,32,0.88);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.12);" +
                "-fx-border-radius: 15;"
        );


        HBox top =
                new HBox();

        top.setAlignment(
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
                        27
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
    // NETWORK CARD
    // =============================================================

    private VBox createNetworkCard() {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        card.setPrefHeight(
                125
        );

        card.setStyle(
                "-fx-background-color: rgba(10,22,32,0.88);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.10);" +
                "-fx-border-radius: 15;"
        );


        Label heading =
                new Label(
                        "USER NETWORK"
                );

        heading.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        heading.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );


        HBox network =
                new HBox(25);

        network.setAlignment(
                Pos.CENTER_LEFT
        );


        network.getChildren().addAll(

                networkNode(
                        "USERS",
                        "DATABASE",
                        Color.rgb(
                                100,
                                240,
                                215
                        )
                ),

                networkLine(),

                networkNode(
                        "ACCESS",
                        "CONTROL",
                        Color.rgb(
                                100,
                                180,
                                255
                        )
                ),

                networkLine(),

                networkNode(
                        "EV GRID",
                        "SERVICES",
                        Color.rgb(
                                180,
                                130,
                                255
                        )
                )
        );


        card.getChildren().addAll(
                heading,
                network
        );


        return card;
    }


    private VBox networkNode(
            String top,
            String bottom,
            Color color
    ) {

        VBox box =
                new VBox(3);

        box.setAlignment(
                Pos.CENTER
        );


        Circle node =
                new Circle(
                        6,
                        color
                );

        node.setEffect(
                new DropShadow(
                        12,
                        color
                )
        );


        Label topLabel =
                new Label(
                        top
                );

        topLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        topLabel.setTextFill(
                Color.WHITE
        );


        Label bottomLabel =
                new Label(
                        bottom
                );

        bottomLabel.setFont(
                Font.font(
                        "System",
                        7
                )
        );

        bottomLabel.setTextFill(
                Color.rgb(
                        100,
                        125,
                        140
                )
        );


        box.getChildren().addAll(
                node,
                topLabel,
                bottomLabel
        );


        return box;
    }


    private Region networkLine() {

        Region line =
                new Region();

        line.setPrefWidth(
                50
        );

        line.setPrefHeight(
                1
        );

        line.setStyle(
                "-fx-background-color: rgba(100,240,215,0.25);"
        );


        return line;
    }


    // =============================================================
    // ACCESS CARD
    // =============================================================

    private VBox createAccessCard() {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(18)
        );

        card.setPrefHeight(
                125
        );

        card.setStyle(
                "-fx-background-color: rgba(10,22,32,0.88);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.10);" +
                "-fx-border-radius: 15;"
        );


        Label heading =
                new Label(
                        "ACCESS SECURITY"
                );

        heading.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        heading.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                )
        );


        HBox row1 =
                securityRow(
                        "DATABASE AUTHENTICATION",
                        "ACTIVE"
                );


        HBox row2 =
                securityRow(
                        "USER ACCESS CONTROL",
                        "ACTIVE"
                );


        HBox row3 =
                securityRow(
                        "ADMIN PRIVILEGES",
                        "PROTECTED"
                );


        card.getChildren().addAll(
                heading,
                row1,
                row2,
                row3
        );


        return card;
    }


    private HBox securityRow(
            String title,
            String status
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );


        Label titleLabel =
                new Label(
                        "●  " + title
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
                Color.rgb(
                        100,
                        240,
                        215
                )
        );


        row.getChildren().addAll(
                titleLabel,
                spacer,
                statusLabel
        );


        return row;
    }


    // =============================================================
    // USER TABLE
    // =============================================================

    private VBox createUserTable() {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: rgba(8,18,27,0.94);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(100,240,215,0.10);" +
                "-fx-border-radius: 15;"
        );


        // =========================================================
        // TABLE HEADER
        // =========================================================

        HBox tableHeader =
                new HBox();

        tableHeader.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox tableTitle =
                new VBox(3);


        Label heading =
                new Label(
                        "REGISTERED USERS"
                );

        heading.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        heading.setTextFill(
                Color.WHITE
        );


        Label subheading =
                new Label(
                        "LIVE USER DATABASE • ACCESS OVERVIEW"
                );

        subheading.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        subheading.setTextFill(
                Color.rgb(
                        100,
                        125,
                        140
                ));


        tableTitle.getChildren().addAll(
                heading,
                subheading
        );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Label recordStatus =
                new Label(
                        "● DATABASE SYNCED"
                );

        recordStatus.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        recordStatus.setTextFill(
                Color.rgb(
                        100,
                        240,
                        215
                ));


        tableHeader.getChildren().addAll(
                tableTitle,
                spacer,
                recordStatus
        );


        // =========================================================
        // TABLE
        // =========================================================

        userTable =
                new TableView<>();

        userTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        Label emptyLabel =
                new Label(
                        "No users found in database."
                );

        emptyLabel.setTextFill(
                Color.rgb(
                        120,
                        145,
                        160
                ));


        userTable.setPlaceholder(
                emptyLabel
        );


        userTable.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-control-inner-background: #08121b;" +
                "-fx-table-cell-border-color: rgba(100,240,215,0.05);" +
                "-fx-selection-bar: rgba(100,240,215,0.18);" +
                "-fx-selection-bar-non-focused: rgba(100,240,215,0.10);"
        );


        // =========================================================
        // ID COLUMN
        // =========================================================

        TableColumn<User, String> idColumn =
                new TableColumn<>(
                        "USER ID"
                );


        idColumn.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                String.valueOf(
                                        data.getValue()
                                                .getUserId()
                                )
                        )
        );


        // =========================================================
        // NAME COLUMN
        // =========================================================

        TableColumn<User, String> nameColumn =
                new TableColumn<>(
                        "NAME"
                );


        nameColumn.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getName()
                                )
                        )
        );


        // =========================================================
        // EMAIL COLUMN
        // =========================================================

        TableColumn<User, String> emailColumn =
                new TableColumn<>(
                        "EMAIL"
                );


        emailColumn.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getEmail()
                                )
                        )
        );


        // =========================================================
        // PHONE COLUMN
        // =========================================================

        TableColumn<User, String> phoneColumn =
                new TableColumn<>(
                        "PHONE"
                );


        phoneColumn.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getPhone()
                                )
                        )
        );


        // =========================================================
        // ACCESS COLUMN
        // =========================================================

        TableColumn<User, String> typeColumn =
                new TableColumn<>(
                        "ACCESS"
                );


        typeColumn.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                determineRole(
                                        data.getValue()
                                )
                        )
        );


        typeColumn.setCellFactory(
                column ->
                        new TableCell<User, String>() {

                            @Override
                            protected void updateItem(
                                    String item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );


                                if (
                                        empty ||
                                        item == null
                                ) {

                                    setText(null);

                                    setStyle(
                                            "-fx-background-color: transparent;"
                                    );

                                } else {

                                    setText(
                                            item
                                    );


                                    if (
                                            "ADMIN".equalsIgnoreCase(
                                                    item
                                            )
                                    ) {

                                        setTextFill(
                                                Color.rgb(
                                                        180,
                                                        140,
                                                        255
                                                )
                                        );

                                    } else {

                                        setTextFill(
                                                Color.rgb(
                                                        100,
                                                        240,
                                                        215
                                                )
                                        );
                                    }


                                    setFont(
                                            Font.font(
                                                    "System",
                                                    FontWeight.BOLD,
                                                    10
                                            )
                                    );
                                }
                            }
                        }
        );


        // =========================================================
        // STATUS COLUMN
        // =========================================================

        TableColumn<User, String> statusColumn =
                new TableColumn<>(
                        "STATUS"
                );


        statusColumn.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                "● ACTIVE"
                        )
        );


        statusColumn.setCellFactory(
                column ->
                        new TableCell<User, String>() {

                            @Override
                            protected void updateItem(
                                    String item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );


                                if (
                                        empty ||
                                        item == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            item
                                    );

                                    setTextFill(
                                            Color.rgb(
                                                    100,
                                                    240,
                                                    215
                                            )
                                    );

                                    setFont(
                                            Font.font(
                                                    "System",
                                                    FontWeight.BOLD,
                                                    9
                                            )
                                    );
                                }
                            }
                        }
        );


        userTable.getColumns().addAll(
                idColumn,
                nameColumn,
                emailColumn,
                phoneColumn,
                typeColumn,
                statusColumn
        );


        VBox.setVgrow(
                userTable,
                Priority.ALWAYS
        );


        card.getChildren().addAll(
                tableHeader,
                userTable
        );


        return card;
    }


    // =============================================================
    // REFRESH USERS
    // =============================================================

    private void refreshUsers() {

        if (userTable == null) {
            return;
        }


        try {

            List<User> users =
                    userService.getAllUsers();


            userTable.getItems().setAll(
                    users
            );


            int total =
                    users.size();


            int admins = 0;


            for (User user : users) {

                if (
                        "ADMIN".equalsIgnoreCase(
                                determineRole(user)
                        )
                ) {

                    admins++;
                }
            }


            int regular =
                    total - admins;


            totalUsersLabel.setText(
                    String.valueOf(
                            total
                    )
            );


            adminUsersLabel.setText(
                    String.valueOf(
                            admins
                    )
            );


            regularUsersLabel.setText(
                    String.valueOf(
                            regular
                    )
            );


        } catch (Exception e) {

            showAlert(
                    "DATABASE ERROR",
                    "Unable to load users.\n\n"
                            + e.getMessage()
            );
        }
    }


    // =============================================================
    // ROLE
    // =============================================================

        private String determineRole(User user) {

        if (user == null) {
                return "USER";
        }

        String role = user.getRole();

        if (role == null || role.trim().isEmpty()) {
                return "USER";
        }

        return role.trim().toUpperCase();
        }

    // =============================================================
    // SAFE STRING
    // =============================================================

    private String safe(
            String value
    ) {

        if (
                value == null ||
                value.trim().isEmpty()
        ) {

            return "—";
        }


        return value;
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


        // =========================================================
        // GRID
        // =========================================================

        for (
                int x = 0;
                x < 1600;
                x += 55
        ) {

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


        for (
                int y = 0;
                y < 1000;
                y += 55
        ) {

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


        // =========================================================
        // GLOW ZONES
        // =========================================================

        Circle glow1 =
                new Circle(
                        500,
                        300,
                        220
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
                        250
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


        // =========================================================
        // PARTICLES
        // =========================================================

        for (
                int i = 0;
                i < 40;
                i++
        ) {

            Circle particle =
                    new Circle(
                            1.5
                    );


            particle.setFill(
                    Color.rgb(
                            100,
                            240,
                            215,
                            0.35
                    )
            );


            particle.setLayoutX(
                    260 +
                            Math.random() *
                                    1180
            );


            particle.setLayoutY(
                    Math.random() *
                            900
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
    // BACKGROUND ANIMATION
    // =============================================================

    private void startBackgroundAnimation() {

        stopBackgroundAnimation();


        backgroundAnimation =
                new Timeline();


        /*
         * Each particle receives a small vertical
         * floating animation.
         */

        for (
                Circle particle :
                particles
        ) {

            double startY =
                    particle.getLayoutY();


            double movement =
                    60 +
                            Math.random() *
                                    100;


            KeyValue startPosition =
                    new KeyValue(
                            particle.translateYProperty(),
                            0
                    );


            KeyValue endPosition =
                    new KeyValue(
                            particle.translateYProperty(),
                            -movement
                    );


            KeyFrame startFrame =
                    new KeyFrame(
                            Duration.ZERO,
                            startPosition
                    );


            KeyFrame endFrame =
                    new KeyFrame(
                            Duration.seconds(
                                    2.5 +
                                            Math.random() *
                                                    3
                            ),
                            endPosition
                    );


            Timeline particleAnimation =
                    new Timeline(
                            startFrame,
                            endFrame
                    );


            particleAnimation.setAutoReverse(
                    true
            );


            particleAnimation.setCycleCount(
                    Animation.INDEFINITE
            );


            particleAnimation.play();
        }
    }


    // =============================================================
    // STOP BACKGROUND ANIMATION
    // =============================================================

    private void stopBackgroundAnimation() {

        if (
                backgroundAnimation != null
        ) {

            backgroundAnimation.stop();

            backgroundAnimation = null;
        }


        /*
         * Reset particles so that when the page
         * is opened again, they start normally.
         */

        for (
                Circle particle :
                particles
        ) {

            particle.setTranslateY(0);
        }
    }


    // =============================================================
    // ENTRANCE ANIMATION
    // =============================================================

    private void playEntrance(
            VBox sidebar,
            VBox mainContent
    ) {

        sidebar.setOpacity(0);

        mainContent.setOpacity(0);


        FadeTransition sidebarFade =
                new FadeTransition(
                        Duration.millis(
                                450
                        ),
                        sidebar
                );

        sidebarFade.setFromValue(0);

        sidebarFade.setToValue(1);


        FadeTransition contentFade =
                new FadeTransition(
                        Duration.millis(
                                600
                        ),
                        mainContent
                );

        contentFade.setFromValue(0);

        contentFade.setToValue(1);


        sidebarFade.play();

        contentFade.play();
    }


    // =============================================================
    // SIDEBAR BUTTON
    // =============================================================

    private Button sideButton(
            String icon,
            String text
    ) {

        Button button =
                new Button(
                        icon +
                                "   " +
                                text
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

                    if (
                            !button.getStyle().equals(
                                    activeSideStyle()
                            )
                    ) {

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

                    if (
                            !button.getStyle().equals(
                                    activeSideStyle()
                            )
                    ) {

                        button.setStyle(
                                normalSideStyle()
                        );
                    }
                }
        );


        return button;
    }


    // =============================================================
    // NORMAL SIDEBAR STYLE
    // =============================================================

    private String normalSideStyle() {

        return
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;";
    }


    // =============================================================
    // ACTIVE SIDEBAR STYLE
    // =============================================================

    private String activeSideStyle() {

        return
                "-fx-background-color: rgba(100,240,215,0.12);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: rgba(100,240,215,0.25);" +
                "-fx-border-width: 0 0 0 3;" +
                "-fx-cursor: hand;";
    }
        
        // =============================================================
// CREATE ADMIN DIALOG
// =============================================================

private void showCreateAdminDialog() {

    // ---------------------------------------------------------
    // SECURITY CHECK
    // ---------------------------------------------------------

    if (
            loggedInUser == null ||
            loggedInUser.getRole() == null ||
            !"ADMIN".equalsIgnoreCase(
                    loggedInUser.getRole()
            )
    ) {

        showAlert(
                "ACCESS DENIED",
                "Only an existing administrator can create another administrator."
        );

        return;
    }


    // ---------------------------------------------------------
    // DIALOG
    // ---------------------------------------------------------

    Dialog<ButtonType> dialog =
            new Dialog<>();

    dialog.setTitle(
            "Smart EV • Create Admin"
    );


    DialogPane pane =
            dialog.getDialogPane();

    pane.setStyle(
            "-fx-background-color: #08121b;"
    );


    // ---------------------------------------------------------
    // TITLE
    // ---------------------------------------------------------

    Label title =
            new Label(
                    "CREATE ADMIN ACCOUNT"
            );

    title.setFont(
            Font.font(
                    "System",
                    FontWeight.BOLD,
                    18
            )
    );

    title.setTextFill(
            Color.rgb(
                    180,
                    140,
                    255
            )
    );


    Label subtitle =
            new Label(
                    "AUTHORIZED ADMINISTRATOR • PRIVILEGED ACCESS"
            );

    subtitle.setFont(
            Font.font(
                    "System",
                    FontWeight.BOLD,
                    9
            )
    );

    subtitle.setTextFill(
            Color.rgb(
                    110,
                    135,
                    150
            ));


    // ---------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------

    TextField nameField =
            new TextField();

    nameField.setPromptText(
            "Enter administrator name"
    );


    TextField emailField =
            new TextField();

    emailField.setPromptText(
            "Enter administrator email"
    );


    PasswordField passwordField =
            new PasswordField();

    passwordField.setPromptText(
            "Enter password"
    );


    TextField phoneField =
            new TextField();

    phoneField.setPromptText(
            "Enter phone number"
    );


    String fieldStyle =
            "-fx-background-color: #0d1b26;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #607585;" +
            "-fx-border-color: rgba(100,240,215,0.18);" +
            "-fx-border-radius: 7;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 10;";


    nameField.setStyle(fieldStyle);
    emailField.setStyle(fieldStyle);
    passwordField.setStyle(fieldStyle);
    phoneField.setStyle(fieldStyle);


    // ---------------------------------------------------------
    // LABELS
    // ---------------------------------------------------------

    Label nameLabel =
            createDialogLabel("NAME");

    Label emailLabel =
            createDialogLabel("EMAIL");

    Label passwordLabel =
            createDialogLabel("PASSWORD");

    Label phoneLabel =
            createDialogLabel("PHONE");


    // ---------------------------------------------------------
    // FORM
    // ---------------------------------------------------------

    VBox form =
            new VBox(7);

    form.setPadding(
            new Insets(
                    15,
                    0,
                    5,
                    0
            )
    );


    form.getChildren().addAll(
            nameLabel,
            nameField,

            emailLabel,
            emailField,

            passwordLabel,
            passwordField,

            phoneLabel,
            phoneField
    );


    VBox content =
            new VBox(10);

    content.setPadding(
            new Insets(10)
    );

    content.setPrefWidth(
            390
    );


    content.getChildren().addAll(
            title,
            subtitle,
            form
    );


    pane.setContent(
            content
    );


    // ---------------------------------------------------------
    // BUTTONS
    // ---------------------------------------------------------

    ButtonType createButtonType =
            new ButtonType(
                    "CREATE ADMIN",
                    ButtonBar.ButtonData.OK_DONE
            );

    ButtonType cancelButtonType =
            new ButtonType(
                    "CANCEL",
                    ButtonBar.ButtonData.CANCEL_CLOSE
            );


    pane.getButtonTypes().addAll(
            cancelButtonType,
            createButtonType
    );


    Button createButton =
            (Button) pane.lookupButton(
                    createButtonType
            );


    createButton.setStyle(
            "-fx-background-color: rgba(180,140,255,0.15);" +
            "-fx-text-fill: #b48cff;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 7;" +
            "-fx-border-color: rgba(180,140,255,0.35);" +
            "-fx-border-radius: 7;"
    );


    Button cancelButton =
            (Button) pane.lookupButton(
                    cancelButtonType
            );


    cancelButton.setStyle(
            "-fx-background-color: rgba(100,240,215,0.06);" +
            "-fx-text-fill: #64f0d7;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 7;" +
            "-fx-border-color: rgba(100,240,215,0.20);" +
            "-fx-border-radius: 7;"
    );


        // ---------------------------------------------------------
        // VALIDATION
        // ---------------------------------------------------------

        createButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                        String name =
                                nameField.getText().trim();

                        String email =
                                emailField.getText().trim();

                        String password =
                                passwordField.getText();

                        String phone =
                                phoneField.getText().trim();


                        if (
                                name.isEmpty() ||
                                email.isEmpty() ||
                                password.isEmpty()
                        ) {

                        event.consume();

                        showAlert(
                                "MISSING INFORMATION",
                                "Name, email and password are required."
                        );

                        return;
                        }


                        if (!email.contains("@")) {

                        event.consume();

                        showAlert(
                                "INVALID EMAIL",
                                "Please enter a valid email address."
                        );

                        return;
                        }


                        if (password.length() < 4) {

                        event.consume();

                        showAlert(
                                "INVALID PASSWORD",
                                "Password must contain at least 4 characters."
                        );

                        return;
                        }


                        User admin =
                                new User(
                                        name,
                                        email,
                                        password,
                                        phone
                                );


                        boolean created =
                                userService.createAdmin(
                                        admin
                                );


                        if (!created) {

                        event.consume();

                        showAlert(
                                "ADMIN CREATION FAILED",
                                "Unable to create administrator.\n\n" +
                                "The email may already exist or the database rejected the request."
                        );

                        return;
                        }


                        showAlert(
                                "ADMIN CREATED",
                                "Administrator account created successfully.\n\n" +
                                "Name: " + name + "\n" +
                                "Email: " + email
                        );


                        refreshUsers();
                }
                );


                dialog.showAndWait();
                }       
        // =============================================================
        // DIALOG LABEL
        // =============================================================

        private Label createDialogLabel(
                String text
        ) {

        Label label =
                new Label(
                        text
                );

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        label.setTextFill(
                Color.rgb(
                        120,
                        145,
                        160
                ));

        return label;
        }


    // =============================================================
    // ALERT
    // =============================================================

    private void showAlert(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                title
        );


        alert.setHeaderText(
                null
        );


        alert.setContentText(
                message
        );


        DialogPane pane =
                alert.getDialogPane();


        pane.setStyle(
                "-fx-background-color: #0a1620;"
        );


        alert.showAndWait();
    }
}