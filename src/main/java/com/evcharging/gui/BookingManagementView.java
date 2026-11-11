package com.evcharging.gui;

import com.evcharging.exception.BookingException;
import com.evcharging.model.User;
import com.evcharging.service.BookingService;
import com.evcharging.util.DBConnection;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BookingManagementView {

    private final User loggedInUser;
    private final BookingService bookingService;

    private Stage stage;

    private VBox bookingList;

    private Label totalValue;
    private Label confirmedValue;
    private Label cancelledValue;
    private Label revenueValue;

    private Label messageLabel;

    private AnimationTimer backgroundAnimation;
    private AnimationTimer bookingAnimation;

    private final List<BookingNode> nodes = new ArrayList<>();


    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BG =
            Color.rgb(2, 8, 13);

    private static final Color PANEL =
            Color.rgb(5, 18, 28, 0.96);

    private static final Color CYAN =
            Color.rgb(0, 229, 255);

    private static final Color GREEN =
            Color.rgb(40, 255, 145);

    private static final Color PURPLE =
            Color.rgb(145, 100, 255);

    private static final Color AMBER =
            Color.rgb(255, 190, 70);

    private static final Color RED =
            Color.rgb(255, 90, 100);

    private static final Color WHITE =
            Color.rgb(235, 250, 255);

    private static final Color MUTED =
            Color.rgb(125, 160, 175);

    private static final Color GRID =
            Color.rgb(18, 53, 67, 0.45);


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BookingManagementView(User user) {

        this.loggedInUser = user;
        this.bookingService = new BookingService();
    }


    // =========================================================
    // SHOW PAGE
    // =========================================================

    public void show(Stage stage) {

        this.stage = stage;

        BorderPane foreground = new BorderPane();

        VBox sidebar = createSidebar();
        VBox mainContent = createMainContent();

        BorderPane.setMargin(
                mainContent,
                new Insets(22, 28, 22, 28)
        );

        foreground.setLeft(sidebar);
        foreground.setCenter(mainContent);

        StackPane root = new StackPane();

        root.getChildren().addAll(
                createAnimatedBackground(),
                foreground
        );

        Scene scene = new Scene(
                root,
                1440,
                900
        );

        stage.setTitle(
                "Smart EV - Booking Management"
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
         * Do NOT manually set the width to the
         * entire screen.
         *
         * This keeps the page from stretching
         * across both sides.
         */

        stage.setScene(scene);

        stage.show();

        /*
         * Start animations only after the scene
         * has been displayed.
         */
        if (backgroundAnimation != null) {
            backgroundAnimation.start();
        }

        if (bookingAnimation != null) {
            bookingAnimation.start();
        }

        playEntrance(
                sidebar,
                mainContent
        );
    }


    // =========================================================
    // ANIMATED BACKGROUND
    // =========================================================

    private StackPane createAnimatedBackground() {

        StackPane background =
                new StackPane();

        Canvas canvas =
                new Canvas(1600, 1000);

        background.getChildren().add(canvas);

        GraphicsContext gc =
                canvas.getGraphicsContext2D();

        backgroundAnimation =
                new AnimationTimer() {

                    private double time = 0;

                    @Override
                    public void handle(long now) {

                        time += 0.012;

                        drawBackground(
                                gc,
                                time
                        );
                    }
                };

        return background;
    }


    private void drawBackground(
            GraphicsContext gc,
            double time
    ) {

        double width = 1600;
        double height = 1000;

        // Background
        gc.setFill(BG);

        gc.fillRect(
                0,
                0,
                width,
                height
        );


        // ---------------------------------------------
        // GRID
        // ---------------------------------------------

        gc.setStroke(GRID);
        gc.setLineWidth(1);

        double gridOffset =
                (time * 25) % 50;

        for (
                double x = -50 + gridOffset;
                x < width;
                x += 50
        ) {

            gc.strokeLine(
                    x,
                    0,
                    x,
                    height
            );
        }

        for (
                double y = -50 + gridOffset;
                y < height;
                y += 50
        ) {

            gc.strokeLine(
                    0,
                    y,
                    width,
                    y
            );
        }


        // ---------------------------------------------
        // DIAGONAL ENERGY LINES
        // ---------------------------------------------

        gc.setStroke(
                Color.rgb(
                        0,
                        229,
                        255,
                        0.06
                )
        );

        gc.setLineWidth(1);

        for (
                int i = -1000;
                i < width + 1000;
                i += 160
        ) {

            double offset =
                    (time * 60) % 160;

            gc.strokeLine(
                    i + offset,
                    0,
                    i - 350 + offset,
                    height
            );
        }


        // ---------------------------------------------
        // PARTICLES
        // ---------------------------------------------

        for (int i = 0; i < 75; i++) {

            double x =
                    (i * 137.31
                            + time * (12 + (i % 5) * 3))
                            % width;

            double y =
                    (i * 71.17
                            + Math.sin(
                                    time * 0.7 + i
                            ) * 35)
                            % height;

            double radius =
                    1.0 + (i % 3) * 0.7;

            double opacity =
                    0.12 + 0.08 *
                            Math.sin(
                                    time * 2 + i
                            );

            if (opacity < 0.03) {
                opacity = 0.03;
            }

            gc.setFill(
                    Color.rgb(
                            0,
                            229,
                            255,
                            opacity
                    )
            );

            gc.fillOval(
                    x,
                    y,
                    radius,
                    radius
            );
        }


        // ---------------------------------------------
        // SCAN LINE
        // ---------------------------------------------

        double scanY =
                (time * 80) % height;

        gc.setStroke(
                Color.rgb(
                        0,
                        229,
                        255,
                        0.08
                )
        );

        gc.setLineWidth(2);

        gc.strokeLine(
                0,
                scanY,
                width,
                scanY
        );
    }


    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar() {

        VBox sidebar =
                new VBox();

        sidebar.setPrefWidth(235);
        sidebar.setMinWidth(235);
        sidebar.setMaxWidth(235);

        sidebar.setPadding(
                new Insets(
                        28,
                        16,
                        20,
                        16
                )
        );

        sidebar.setSpacing(18);

        sidebar.setStyle(
                "-fx-background-color: rgba(3,13,20,0.97);" +
                "-fx-border-color: rgba(0,229,255,0.18);" +
                "-fx-border-width: 0 1 0 0;"
        );


        // ---------------------------------------------
        // BRAND
        // ---------------------------------------------

        VBox brand =
                new VBox(4);

        Label logo =
                new Label("⚡ SMART EV");

        logo.setStyle(
                "-fx-text-fill: #00E5FF;" +
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "BOOKING CONTROL"
                );

        subtitle.setStyle(
                "-fx-text-fill: #7DA0AF;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 2px;"
        );

        brand.getChildren().addAll(
                logo,
                subtitle
        );


        // ---------------------------------------------
        // ADMIN CARD
        // ---------------------------------------------

        VBox adminCard =
                new VBox(7);

        adminCard.setPadding(
                new Insets(14)
        );

        adminCard.setStyle(
                "-fx-background-color: rgba(0,229,255,0.055);" +
                "-fx-border-color: rgba(0,229,255,0.20);" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        Label adminName =
                new Label(
                        loggedInUser != null
                                ? loggedInUser.getName()
                                : "Administrator"
                );

        adminName.setStyle(
                "-fx-text-fill: #EBFAFF;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        Label online =
                new Label(
                        "● SYSTEM ONLINE"
                );

        online.setStyle(
                "-fx-text-fill: #28FF91;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;"
        );

        adminCard.getChildren().addAll(
                adminName,
                online
        );


        // ---------------------------------------------
        // MODULE
        // ---------------------------------------------

        Label module =
                new Label(
                        "BOOKING MANAGEMENT"
                );

        module.setMaxWidth(
                Double.MAX_VALUE
        );

        module.setPadding(
                new Insets(10, 12, 10, 12)
        );

        module.setStyle(
                "-fx-background-color: rgba(0,229,255,0.08);" +
                "-fx-border-color: rgba(0,229,255,0.22);" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-text-fill: #00E5FF;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;"
        );


        // ---------------------------------------------
        // NAVIGATION
        // ---------------------------------------------

        VBox navigation =
                new VBox(7);

        Button dashboard =
                navButton(
                        "▣",
                        "Dashboard",
                        false
                );

        Button logout =
                navButton(
                        "↪",
                        "Logout",
                        false
                );


        dashboard.setOnAction(e -> {

            stopAnimation();

            new AdminDashboardView(
                    loggedInUser
            ).show(stage);
        });


        logout.setOnAction(e -> {

            stopAnimation();

            new LoginView().show(stage);
        });


        navigation.getChildren().addAll(
                dashboard
        );


        RegionSpacer spacer =
                new RegionSpacer();

        VBox bottom =
                new VBox(10);

        bottom.getChildren().add(
                logout
        );


        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                brand,
                adminCard,
                module,
                navigation,
                spacer,
                bottom
        );

        return sidebar;
    }


    // =========================================================
    // NAV BUTTON
    // =========================================================

    private Button navButton(
            String icon,
            String text,
            boolean selected
    ) {

        Button button =
                new Button();

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: " +
                        toRgb(
                                selected
                                        ? CYAN
                                        : MUTED
                        ) +
                        ";" +
                        "-fx-font-size: 15px;"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: " +
                        toRgb(
                                selected
                                        ? WHITE
                                        : MUTED
                        ) +
                        ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        HBox content =
                new HBox(
                        11,
                        iconLabel,
                        textLabel
                );

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setGraphic(content);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setMinHeight(42);

        String normalStyle =
                "-fx-background-color: " +
                        (
                                selected
                                        ? "rgba(0,229,255,0.10)"
                                        : "transparent"
                        ) +
                        ";" +
                        "-fx-border-color: " +
                        (
                                selected
                                        ? "rgba(0,229,255,0.25)"
                                        : "transparent"
                        ) +
                        ";" +
                        "-fx-border-radius: 7;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: rgba(0,229,255,0.08);" +
                "-fx-border-color: rgba(0,229,255,0.20);" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 12 8 12;" +
                "-fx-cursor: hand;";

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(
                e -> button.setStyle(
                        hoverStyle
                )
        );

        button.setOnMouseExited(
                e -> button.setStyle(
                        normalStyle
                )
        );

        return button;
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent() {

        VBox main =
                new VBox(18);

        main.setFillWidth(true);


        // ---------------------------------------------
        // HEADER
        // ---------------------------------------------

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "BOOKING MANAGEMENT"
                );

        title.setStyle(
                "-fx-text-fill: #EBFAFF;" +
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );

        Label description =
                new Label(
                        "Monitor and manage EV charging reservations"
                );

        description.setStyle(
                "-fx-text-fill: #7DA0AF;" +
                "-fx-font-size: 12px;"
        );

        titleBox.getChildren().addAll(
                title,
                description
        );


        Button refresh =
                smallButton(
                        "↻ REFRESH"
                );

        refresh.setOnAction(e -> {

            loadBookings();

            showMessage(
                    "Booking data refreshed from MySQL."
            );
        });


        HBox refreshBox =
                new HBox(refresh);

        refreshBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        HBox.setHgrow(
                titleBox,
                Priority.ALWAYS
        );

        header.getChildren().addAll(
                titleBox,
                refreshBox
        );


        // ---------------------------------------------
        // KPI
        // ---------------------------------------------

        HBox kpi =
                createKpiSection();


        // ---------------------------------------------
        // NETWORK
        // ---------------------------------------------

        VBox network =
                createBookingNetwork();


        // ---------------------------------------------
        // BOOKINGS
        // ---------------------------------------------

        VBox table =
                createBookingTable();


        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        main.getChildren().addAll(
                header,
                kpi,
                network,
                table
        );

        return main;
    }


    // =========================================================
    // KPI SECTION
    // =========================================================

    private HBox createKpiSection() {

        HBox container =
                new HBox(12);

        totalValue =
                new Label("0");

        confirmedValue =
                new Label("0");

        cancelledValue =
                new Label("0");

        revenueValue =
                new Label("₹0.00");


        VBox total =
                kpiCard(
                        "TOTAL BOOKINGS",
                        totalValue,
                        CYAN
                );

        VBox confirmed =
                kpiCard(
                        "CONFIRMED",
                        confirmedValue,
                        GREEN
                );

        VBox cancelled =
                kpiCard(
                        "CANCELLED",
                        cancelledValue,
                        RED
                );

        VBox revenue =
                kpiCard(
                        "TOTAL REVENUE",
                        revenueValue,
                        PURPLE
                );


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


        container.getChildren().addAll(
                total,
                confirmed,
                cancelled,
                revenue
        );

        return container;
    }


    private VBox kpiCard(
            String title,
            Label value,
            Color accent
    ) {

        VBox card =
                new VBox(6);

        card.setPadding(
                new Insets(14, 16, 14, 16)
        );

        card.setStyle(
                "-fx-background-color: rgba(5,18,28,0.94);" +
                "-fx-border-color: " +
                toRgb(accent) +
                "44;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill: #7DA0AF;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1.4px;"
        );

        value.setStyle(
                "-fx-text-fill: " +
                        toRgb(accent) +
                        ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                titleLabel,
                value
        );

        return card;
    }


    // =========================================================
    // BOOKING NETWORK
    // =========================================================

    private VBox createBookingNetwork() {

        VBox panel =
                new VBox(10);

        panel.setPadding(
                new Insets(15)
        );

        panel.setStyle(
                "-fx-background-color: rgba(5,18,28,0.94);" +
                "-fx-border-color: rgba(0,229,255,0.15);" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );


        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        "BOOKING NETWORK"
                );

        title.setStyle(
                "-fx-text-fill: #EBFAFF;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1.2px;"
        );

        Label live =
                new Label(
                        "● LIVE RESERVATION NETWORK"
                );

        live.setStyle(
                "-fx-text-fill: #28FF91;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;"
        );

        HBox.setHgrow(
                title,
                Priority.ALWAYS
        );

        header.getChildren().addAll(
                title,
                live
        );


        Canvas canvas =
                new Canvas(
                        1100,
                        220
                );

        setupBookingAnimation(
                canvas
        );


        StackPane canvasHolder =
                new StackPane(canvas);

        canvasHolder.setAlignment(
                Pos.CENTER
        );

        panel.getChildren().addAll(
                header,
                canvasHolder
        );

        return panel;
    }


    private void setupBookingAnimation(
            Canvas canvas
    ) {

        nodes.clear();

        nodes.add(
                new BookingNode(
                        260,
                        55,
                        "STATION 01"
                )
        );

        nodes.add(
                new BookingNode(
                        260,
                        165,
                        "STATION 02"
                )
        );

        nodes.add(
                new BookingNode(
                        840,
                        55,
                        "STATION 03"
                )
        );

        nodes.add(
                new BookingNode(
                        840,
                        165,
                        "STATION 04"
                )
        );


        GraphicsContext gc =
                canvas.getGraphicsContext2D();


        bookingAnimation =
                new AnimationTimer() {

                    private double time = 0;

                    @Override
                    public void handle(long now) {

                        time += 0.018;

                        drawBookingNetwork(
                                gc,
                                time
                        );
                    }
                };
    }


    private void drawBookingNetwork(
            GraphicsContext gc,
            double time
    ) {

        double width =
                1100;

        double height =
                220;


        // ---------------------------------------------
        // BACKGROUND
        // ---------------------------------------------

        gc.setFill(
                Color.rgb(
                        3,
                        13,
                        20,
                        0.96
                )
        );

        gc.fillRoundRect(
                0,
                0,
                width,
                height,
                12,
                12
        );


        // ---------------------------------------------
        // GRID
        // ---------------------------------------------

        gc.setStroke(
                Color.rgb(
                        18,
                        53,
                        67,
                        0.32
                )
        );

        gc.setLineWidth(1);

        for (
                int x = 0;
                x <= width;
                x += 40
        ) {

            gc.strokeLine(
                    x,
                    0,
                    x,
                    height
            );
        }

        for (
                int y = 0;
                y <= height;
                y += 40
        ) {

            gc.strokeLine(
                    0,
                    y,
                    width,
                    y
            );
        }


        double centerX =
                550;

        double centerY =
                110;


        // ---------------------------------------------
        // CONNECTION LINES
        // ---------------------------------------------

        gc.setLineWidth(1.2);

        for (BookingNode node : nodes) {

            gc.setStroke(
                    Color.rgb(
                            0,
                            229,
                            255,
                            0.25
                    )
            );

            gc.strokeLine(
                    centerX,
                    centerY,
                    node.x,
                    node.y
            );


            // Moving particle
            double progress =
                    (time * 0.22 +
                            node.x * 0.001)
                            % 1.0;

            double px =
                    centerX +
                            (node.x - centerX)
                                    * progress;

            double py =
                    centerY +
                            (node.y - centerY)
                                    * progress;

            gc.setFill(
                    Color.rgb(
                            0,
                            229,
                            255,
                            0.85
                    )
            );

            gc.fillOval(
                    px - 3,
                    py - 3,
                    6,
                    6
            );
        }


        // ---------------------------------------------
        // STATION NODES
        // ---------------------------------------------

        for (BookingNode node : nodes) {

            double pulse =
                    1 +
                            Math.sin(
                                    time * 3
                                            + node.x
                                            * 0.01
                            ) * 2;

            gc.setFill(
                    Color.rgb(
                            0,
                            229,
                            255,
                            0.12
                    )
            );

            gc.fillOval(
                    node.x - 18 - pulse,
                    node.y - 18 - pulse,
                    36 + pulse * 2,
                    36 + pulse * 2
            );


            gc.setFill(
                    Color.rgb(
                            0,
                            229,
                            255,
                            0.18
                    )
            );

            gc.fillOval(
                    node.x - 9,
                    node.y - 9,
                    18,
                    18
            );


            gc.setFill(
                    CYAN
            );

            gc.fillOval(
                    node.x - 4,
                    node.y - 4,
                    8,
                    8
            );


            gc.setFill(
                    Color.rgb(
                            200,
                            235,
                            245
                    )
            );

            gc.setFont(
                    javafx.scene.text.Font.font(
                            "System",
                            javafx.scene.text.FontWeight.BOLD,
                            9
                    )
            );

            gc.fillText(
                    node.name,
                    node.x - 38,
                    node.y + 30
            );
        }


        // ---------------------------------------------
        // CENTRAL HUB
        // ---------------------------------------------

        double hubPulse =
                Math.sin(time * 3.5) * 3;


        gc.setFill(
                Color.rgb(
                        145,
                        100,
                        255,
                        0.12
                )
        );

        gc.fillOval(
                centerX - 38 - hubPulse,
                centerY - 38 - hubPulse,
                76 + hubPulse * 2,
                76 + hubPulse * 2
        );


        gc.setFill(
                Color.rgb(
                        15,
                        35,
                        50
                )
        );

        gc.fillOval(
                centerX - 30,
                centerY - 30,
                60,
                60
        );


        gc.setStroke(
                Color.rgb(
                        145,
                        100,
                        255,
                        0.7
                )
        );

        gc.setLineWidth(2);

        gc.strokeOval(
                centerX - 30,
                centerY - 30,
                60,
                60
        );


        gc.setFill(
                WHITE
        );

        gc.setFont(
                javafx.scene.text.Font.font(
                        "System",
                        javafx.scene.text.FontWeight.BOLD,
                        10
                )
        );

        gc.fillText(
                "BOOKING",
                centerX - 25,
                centerY - 2
        );

        gc.fillText(
                "HUB",
                centerX - 10,
                centerY + 12
        );


        // ---------------------------------------------
        // BOTTOM INFORMATION
        // ---------------------------------------------

        gc.setFill(
                MUTED
        );

        gc.setFont(
                javafx.scene.text.Font.font(
                        "System",
                        8
                )
        );

        gc.fillText(
                "USER  →  BOOKING  →  STATION",
                centerX - 86,
                205
        );

        gc.setFill(
                Color.rgb(
                        0,
                        229,
                        255,
                        0.7
                )
        );

        gc.fillText(
                "LIVE RESERVATION NETWORK",
                centerX - 82,
                190
        );
    }


    // =========================================================
    // BOOKING TABLE
    // =========================================================

    private VBox createBookingTable() {

        VBox panel =
                new VBox(12);

        panel.setPadding(
                new Insets(16)
        );

        panel.setStyle(
                "-fx-background-color: rgba(5,18,28,0.96);" +
                "-fx-border-color: rgba(0,229,255,0.15);" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );


        HBox heading =
                new HBox();

        heading.setAlignment(
                Pos.CENTER_LEFT
        );


        Label title =
                new Label(
                        "ALL BOOKINGS"
                );

        title.setStyle(
                "-fx-text-fill: #EBFAFF;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );


        Label live =
                new Label(
                        "● LIVE DATABASE"
                );

        live.setStyle(
                "-fx-text-fill: #28FF91;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;"
        );


        HBox.setHgrow(
                title,
                Priority.ALWAYS
        );

        heading.getChildren().addAll(
                title,
                live
        );


        // ---------------------------------------------
        // TABLE HEADER
        // ---------------------------------------------

        HBox tableHeader =
                createTableHeader();


        // ---------------------------------------------
        // BOOKING LIST
        // ---------------------------------------------

        bookingList =
                new VBox(4);

        bookingList.setPadding(
                new Insets(2)
        );


        ScrollPane scroll =
                new ScrollPane(
                        bookingList
                );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background: transparent;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;"
        );

        VBox.setVgrow(
                scroll,
                Priority.ALWAYS
        );


        messageLabel =
                new Label();

        messageLabel.setStyle(
                "-fx-text-fill: #00E5FF;" +
                "-fx-font-size: 10px;"
        );


        panel.getChildren().addAll(
                heading,
                tableHeader,
                scroll,
                messageLabel
        );


        loadBookings();


        return panel;
    }


    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createTableHeader() {

        HBox header =
                new HBox(8);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        9,
                        10,
                        9,
                        10
                )
        );

        header.setStyle(
                "-fx-background-color: rgba(0,229,255,0.055);" +
                "-fx-border-color: rgba(0,229,255,0.10);" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;"
        );


        header.getChildren().addAll(
                columnLabel("ID", 55),
                flexibleColumnLabel("USER"),
                columnLabel("STATION", 110),
                columnLabel("CHARGER", 110),
                columnLabel("DATE / TIME", 170),
                columnLabel("DURATION", 100),
                columnLabel("COST", 90),
                columnLabel("STATUS", 120),
                columnLabel("ACTION", 110)
        );

        return header;
    }


    private Label columnLabel(
            String text,
            double width
    ) {

        Label label =
                new Label(text);

        label.setPrefWidth(width);
        label.setMinWidth(width);

        label.setStyle(
                "-fx-text-fill: #7DA0AF;" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );

        return label;
    }


    private Label flexibleColumnLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #7DA0AF;" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );

        HBox.setHgrow(
                label,
                Priority.ALWAYS
        );

        return label;
    }


    // =========================================================
    // LOAD BOOKINGS
    // =========================================================

    private void loadBookings() {

        if (bookingList == null) {
            return;
        }

        bookingList.getChildren().clear();


        List<BookingRecord> bookings =
                getAllBookings();


        int total = bookings.size();

        int confirmed = 0;

        int cancelled = 0;

        double revenue = 0;


        for (BookingRecord booking : bookings) {

            String status =
                    booking.status == null
                            ? ""
                            : booking.status.trim()
                                    .toUpperCase();


            if ("CANCELLED".equals(status)) {

                cancelled++;

            } else if ("CONFIRMED".equals(status)) {

                confirmed++;

                revenue += booking.cost;
            }
        }


        totalValue.setText(
                String.valueOf(total)
        );

        confirmedValue.setText(
                String.valueOf(confirmed)
        );

        cancelledValue.setText(
                String.valueOf(cancelled)
        );

        revenueValue.setText(
                String.format(
                        "₹%.2f",
                        revenue
                )
        );


        if (bookings.isEmpty()) {

            Label empty =
                    new Label(
                            "No bookings found in the database."
                    );

            empty.setStyle(
                    "-fx-text-fill: #7DA0AF;" +
                    "-fx-font-size: 11px;" +
                    "-fx-padding: 20px;"
            );

            bookingList.getChildren().add(
                    empty
            );

            return;
        }


        for (BookingRecord booking : bookings) {

            bookingList.getChildren().add(
                    createBookingRow(booking)
            );
        }
    }


    // =========================================================
    // GET BOOKINGS FROM MYSQL
    // =========================================================

    private List<BookingRecord> getAllBookings() {

        List<BookingRecord> list =
                new ArrayList<>();


        String sql =
                "SELECT " +
                "b.booking_id, " +
                "b.user_id, " +
                "u.name AS user_name, " +
                "b.station_id, " +
                "b.charger_id, " +
                "b.booking_date, " +
                "b.start_time, " +
                "b.duration, " +
                "b.cost, " +
                "b.status " +
                "FROM bookings b " +
                "LEFT JOIN users u " +
                "ON b.user_id = u.user_id " +
                "ORDER BY b.booking_id DESC";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                BookingRecord booking =
                        new BookingRecord();

                booking.bookingId =
                        result.getInt(
                                "booking_id"
                        );

                booking.userId =
                        result.getInt(
                                "user_id"
                        );

                booking.userName =
                        result.getString(
                                "user_name"
                        );

                booking.stationId =
                        result.getInt(
                                "station_id"
                        );

                booking.chargerId =
                        result.getInt(
                                "charger_id"
                        );

                booking.bookingDate =
                        result.getString(
                                "booking_date"
                        );

                booking.startTime =
                        result.getString(
                                "start_time"
                        );

                booking.duration =
                        result.getInt(
                                "duration"
                        );

                booking.cost =
                        result.getDouble(
                                "cost"
                        );

                booking.status =
                        result.getString(
                                "status"
                        );


                list.add(booking);
            }

        } catch (SQLException e) {

            showMessage(
                    "Failed to load bookings: "
                            + e.getMessage()
            );

            System.out.println(
                    "Booking loading error: "
                            + e.getMessage()
            );
        }


        return list;
    }


    // =========================================================
    // CREATE BOOKING ROW
    // =========================================================

    private HBox createBookingRow(
            BookingRecord booking
    ) {

        HBox row =
                new HBox(8);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        9,
                        10,
                        9,
                        10
                )
        );

        row.setMinHeight(48);

        row.setStyle(
                "-fx-background-color: rgba(5,18,28,0.72);" +
                "-fx-border-color: rgba(18,53,67,0.55);" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;"
        );


        // ---------------------------------------------
        // ID
        // ---------------------------------------------

        Label id =
                tableValue(
                        "#" + booking.bookingId,
                        55,
                        CYAN
                );


        // ---------------------------------------------
        // USER
        // ---------------------------------------------

        String userText =
                booking.userName != null
                        && !booking.userName.isBlank()
                        ? booking.userName
                        : "User #" + booking.userId;


        Label user =
                new Label(userText);

        user.setStyle(
                "-fx-text-fill: #EBFAFF;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;"
        );

        HBox.setHgrow(
                user,
                Priority.ALWAYS
        );


        // ---------------------------------------------
        // STATION
        // ---------------------------------------------

        Label station =
                tableValue(
                        "Station " +
                                booking.stationId,
                        110,
                        WHITE
                );


        // ---------------------------------------------
        // CHARGER
        // ---------------------------------------------

        Label charger =
                tableValue(
                        "Charger " +
                                booking.chargerId,
                        110,
                        WHITE
                );


        // ---------------------------------------------
        // DATE / TIME
        // ---------------------------------------------

        String dateTime =
                String.valueOf(
                        booking.bookingDate
                )
                + "  "
                + String.valueOf(
                        booking.startTime
                );


        Label date =
                tableValue(
                        dateTime,
                        170,
                        MUTED
                );


        // ---------------------------------------------
        // DURATION
        // ---------------------------------------------

        Label duration =
                tableValue(
                        booking.duration
                                + " min",
                        100,
                        WHITE
                );


        // ---------------------------------------------
        // COST
        // ---------------------------------------------

        Label cost =
                tableValue(
                        String.format(
                                "₹%.2f",
                                booking.cost
                        ),
                        90,
                        AMBER
                );


        // ---------------------------------------------
        // STATUS
        // ---------------------------------------------

        String status =
                booking.status == null
                        ? "UNKNOWN"
                        : booking.status
                                .trim()
                                .toUpperCase();


        Color statusColor;

        if ("CANCELLED".equals(status)) {

            statusColor = RED;

        } else if ("CONFIRMED".equals(status)) {

            statusColor = GREEN;

        } else {

            statusColor = AMBER;
        }


        Label statusLabel =
                new Label(
                        "● " + status
                );

        statusLabel.setPrefWidth(
                120
        );

        statusLabel.setMinWidth(
                120
        );

        statusLabel.setStyle(
                "-fx-text-fill: " +
                        toRgb(statusColor) +
                        ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;"
        );


        // ---------------------------------------------
        // ACTION
        // ---------------------------------------------

        HBox actionBox =
                new HBox();

        actionBox.setPrefWidth(
                110
        );

        actionBox.setMinWidth(
                110
        );

        actionBox.setAlignment(
                Pos.CENTER_LEFT
        );


        if ("CONFIRMED".equals(status)) {

            Button cancel =
                    smallButton(
                            "CANCEL"
                    );

            cancel.setPrefWidth(
                    82
            );

            cancel.setOnAction(
                    e -> confirmCancel(
                            booking
                    )
            );

            actionBox.getChildren().add(
                    cancel
            );

        } else {

            Label noAction =
                    new Label(
                            "NO ACTION"
                    );

            noAction.setStyle(
                    "-fx-text-fill: #506B78;" +
                    "-fx-font-size: 8px;" +
                    "-fx-font-weight: bold;"
            );

            actionBox.getChildren().add(
                    noAction
            );
        }


        row.getChildren().addAll(
                id,
                user,
                station,
                charger,
                date,
                duration,
                cost,
                statusLabel,
                actionBox
        );


        // ---------------------------------------------
        // HOVER EFFECT
        // ---------------------------------------------

        row.setOnMouseEntered(
                e -> {

                    row.setStyle(
                            "-fx-background-color: rgba(0,229,255,0.055);" +
                            "-fx-border-color: rgba(0,229,255,0.22);" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;"
                    );


                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(100),
                                    row
                            );

                    scale.setToX(1.002);
                    scale.setToY(1.002);
                    scale.play();
                }
        );


        row.setOnMouseExited(
                e -> {

                    row.setStyle(
                            "-fx-background-color: rgba(5,18,28,0.72);" +
                            "-fx-border-color: rgba(18,53,67,0.55);" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;"
                    );
                }
        );


        return row;
    }


    // =========================================================
    // TABLE VALUE
    // =========================================================

    private Label tableValue(
            String text,
            double width,
            Color color
    ) {

        Label label =
                new Label(text);

        label.setPrefWidth(
                width
        );

        label.setMinWidth(
                width
        );

        label.setStyle(
                "-fx-text-fill: " +
                        toRgb(color) +
                        ";" +
                        "-fx-font-size: 9px;"
        );

        return label;
    }


    // =========================================================
    // CANCEL CONFIRMATION
    // =========================================================

    private void confirmCancel(
            BookingRecord booking
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Cancel Booking"
        );

        alert.setHeaderText(
                "Cancel Booking #"
                        + booking.bookingId
        );

        alert.setContentText(
                "User: "
                        + (
                                booking.userName != null
                                        ? booking.userName
                                        : "User #" + booking.userId
                        )
                        + "\nCharger: "
                        + booking.chargerId
                        + "\nDate: "
                        + booking.bookingDate
                        + "\n\n"
                        + "This booking will be marked as CANCELLED."
        );


        alert.showAndWait().ifPresent(
                result -> {

                    if (result ==
                            ButtonType.OK) {

                        try {

                            boolean success =
                                    bookingService
                                            .cancelBooking(
                                                    booking.bookingId
                                            );


                            if (success) {

                                loadBookings();

                                showMessage(
                                        "Booking #"
                                                + booking.bookingId
                                                + " cancelled successfully."
                                );

                            } else {

                                showMessage(
                                        "Unable to cancel booking #"
                                                + booking.bookingId
                                );
                            }

                        } catch (
                                BookingException ex
                        ) {

                            showMessage(
                                    "Booking cancellation failed: "
                                            + ex.getMessage()
                            );

                            System.out.println(
                                    "Cancel booking error: "
                                            + ex.getMessage()
                            );
                        }
                    }
                }
        );
    }


    // =========================================================
    // SMALL BUTTON
    // =========================================================

    private Button smallButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setMinHeight(30);

        button.setPadding(
                new Insets(
                        5,
                        12,
                        5,
                        12
                )
        );

        button.setStyle(
                "-fx-background-color: rgba(0,229,255,0.08);" +
                "-fx-border-color: rgba(0,229,255,0.30);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-text-fill: #00E5FF;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        button.setOnMouseEntered(
                e -> button.setStyle(
                        "-fx-background-color: rgba(0,229,255,0.16);" +
                        "-fx-border-color: #00E5FF;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-text-fill: #EBFAFF;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        button.setOnMouseExited(
                e -> button.setStyle(
                        "-fx-background-color: rgba(0,229,255,0.08);" +
                        "-fx-border-color: rgba(0,229,255,0.30);" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-text-fill: #00E5FF;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        return button;
    }


    // =========================================================
    // MESSAGE
    // =========================================================

    private void showMessage(
            String message
    ) {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(
                message
        );

        messageLabel.setTextFill(
                CYAN
        );
    }


    // =========================================================
    // ENTRANCE ANIMATION
    // =========================================================

    private void playEntrance(
            VBox sidebar,
            VBox mainContent
    ) {

        sidebar.setOpacity(0);

        mainContent.setOpacity(0);


        FadeTransition sidebarFade =
                new FadeTransition(
                        Duration.millis(450),
                        sidebar
                );

        sidebarFade.setFromValue(0);
        sidebarFade.setToValue(1);


        FadeTransition contentFade =
                new FadeTransition(
                        Duration.millis(600),
                        mainContent
                );

        contentFade.setFromValue(0);
        contentFade.setToValue(1);


        sidebarFade.play();

        contentFade.play();
    }


    // =========================================================
    // STOP ANIMATIONS
    // =========================================================

    private void stopAnimation() {

        if (backgroundAnimation != null) {

            backgroundAnimation.stop();
        }

        if (bookingAnimation != null) {

            bookingAnimation.stop();
        }
    }


    // =========================================================
    // COLOR TO RGB
    // =========================================================

    private String toRgb(
            Color color
    ) {

        int r =
                (int) Math.round(
                        color.getRed() * 255
                );

        int g =
                (int) Math.round(
                        color.getGreen() * 255
                );

        int b =
                (int) Math.round(
                        color.getBlue() * 255
                );

        return String.format(
                "#%02X%02X%02X",
                r,
                g,
                b
        );
    }


    // =========================================================
    // BOOKING RECORD
    // =========================================================

    private static class BookingRecord {

        int bookingId;

        int userId;

        int stationId;

        int chargerId;

        int duration;

        String userName;

        String bookingDate;

        String startTime;

        String status;

        double cost;
    }


    // =========================================================
    // BOOKING NODE
    // =========================================================

    private static class BookingNode {

        double x;

        double y;

        String name;


        BookingNode(
                double x,
                double y,
                String name
        ) {

            this.x = x;

            this.y = y;

            this.name = name;
        }
    }


    // =========================================================
    // SIMPLE SPACER
    // =========================================================

    private static class RegionSpacer
            extends javafx.scene.layout.Region {

        RegionSpacer() {

            VBox.setVgrow(
                    this,
                    Priority.ALWAYS
            );
        }
    }
}