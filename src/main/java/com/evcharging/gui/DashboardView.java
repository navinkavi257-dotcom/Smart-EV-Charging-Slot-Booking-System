package com.evcharging.gui;

import com.evcharging.model.Booking;
import com.evcharging.model.User;
import com.evcharging.service.BookingService;
import com.evcharging.service.StationService;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.effect.Glow;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DashboardView {

    private final User loggedInUser;

    private final StationService stationService =
            new StationService();

    private final BookingService bookingService =
            new BookingService();

    private final Random random =
            new Random(25);


    public DashboardView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }


    public void show(Stage stage) {

        // =========================================================
        // TOP BAR
        // =========================================================

        Label logo =
                label(
                        "⚡ SMART EV",
                        25,
                        Color.WHITE
                );

        Label online =
                label(
                        "●  SYSTEM ONLINE",
                        11,
                        Color.LIGHTGREEN
                );

        online.setEffect(
                new Glow(0.8)
        );

        Label welcome =
                label(
                        "Welcome, " +
                        loggedInUser.getName(),
                        14,
                        Color.LIGHTGRAY
                );

        VBox userBox =
                new VBox(
                        3,
                        online,
                        welcome
                );

        userBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        HBox topBar =
                new HBox(
                        logo,
                        userBox
                );

        topBar.setAlignment(
                Pos.CENTER
        );

        HBox.setHgrow(
                userBox,
                javafx.scene.layout.Priority.ALWAYS
        );

        topBar.setPadding(
                new Insets(
                        15,
                        24,
                        15,
                        24
                )
        );

        topBar.setStyle(
                "-fx-background-color: #050d14;" +
                "-fx-border-color: #17343d;" +
                "-fx-border-width: 0 0 1 0;"
        );


        // =========================================================
        // SIDEBAR
        // =========================================================

        Button dashboard =
                menuButton(
                        "⌂   DASHBOARD"
                );

        Button station =
                menuButton(
                        "⚡   FIND STATION"
                );

        Button booking =
                menuButton(
                        "▣   BOOK SLOT"
                );

        Button history =
                menuButton(
                        "▤   MY BOOKINGS"
                );

        Button wallet =
                menuButton(
                        "₹   MY WALLET"
                );

        Button profile =
                menuButton(
                        "◉   PROFILE"
                );

        Button logout =
                menuButton(
                        "⇦   LOGOUT"
                );

        dashboard.setStyle(
                selectedStyle()
        );


        VBox sidebar =
                new VBox(
                        10,
                        dashboard,
                        station,
                        booking,
                        history,
                        wallet,
                        profile,
                        logout
                );

        sidebar.setPrefWidth(205);
        sidebar.setMinWidth(205);
        sidebar.setMaxWidth(205);

        sidebar.setPadding(
                new Insets(
                        25,
                        12,
                        25,
                        12
                )
        );

        sidebar.setStyle(
                "-fx-background-color: #040b11;" +
                "-fx-border-color: #102a32;" +
                "-fx-border-width: 0 1 0 0;"
        );


        // =========================================================
        // DATABASE DATA
        // =========================================================

        int stationCount = 0;
        int bookingCount = 0;
        int activeCount = 0;

        try {

            stationCount =
                    stationService
                            .getAllStations()
                            .size();

            List<Booking> bookings =
                    bookingService
                            .getBookingsByUser(
                                    loggedInUser.getUserId()
                            );

            bookingCount =
                    bookings.size();

            for (Booking b : bookings) {

                if (
                        "CONFIRMED"
                                .equalsIgnoreCase(
                                        b.getStatus()
                                )
                ) {

                    activeCount++;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Dashboard data error: "
                    + e.getMessage()
            );
        }


        // =========================================================
        // BACKGROUND
        // =========================================================

        Pane background =
                createBackground();


        // =========================================================
        // HEADER
        // =========================================================

        Label title =
                label(
                        "SMART CHARGING",
                        31,
                        Color.WHITE
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        31
                )
        );

        title.setEffect(
                new Glow(0.25)
        );

        Label subtitle =
                label(
                        "INTELLIGENT EV ENERGY CONTROL  •  LIVE SYSTEM",
                        11,
                        Color.rgb(
                                80,
                                235,
                                205
                        )
                );

        VBox heading =
                new VBox(
                        3,
                        title,
                        subtitle
                );


        // =========================================================
        // STAT CARDS
        // =========================================================

        HBox stats =
                new HBox(
                        14,

                        statCard(
                                "⚡",
                                "STATIONS",
                                String.valueOf(
                                        stationCount
                                ),
                                "CONNECTED"
                        ),

                        statCard(
                                "▣",
                                "BOOKINGS",
                                String.valueOf(
                                        bookingCount
                                ),
                                "RESERVATIONS"
                        ),

                        statCard(
                                "●",
                                "ACTIVE",
                                String.valueOf(
                                        activeCount
                                ),
                                "CONFIRMED"
                        )
                );

        stats.setAlignment(
                Pos.CENTER_LEFT
        );


        // =========================================================
        // REACTOR
        // =========================================================

        StackPane reactor =
                createReactor();


        // =========================================================
        // QUICK COMMANDS
        // =========================================================

        Label quick =
                label(
                        "QUICK COMMANDS",
                        15,
                        Color.WHITE
                );

        quick.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );


        VBox find =
                actionCard(
                        "⚡",
                        "FIND STATION",
                        "Search charging stations"
                );

        VBox book =
                actionCard(
                        "↯",
                        "BOOK SLOT",
                        "Reserve charging time"
                );

        VBox myBookings =
                actionCard(
                        "▤",
                        "MY BOOKINGS",
                        "View booking history"
                );

        VBox myWallet =
                actionCard(
                        "₹",
                        "MY WALLET",
                        "Manage wallet balance"
                );


        HBox actions =
                new HBox(
                        16,
                        find,
                        book,
                        myBookings,
                        myWallet
                );

        actions.setAlignment(
                Pos.CENTER_LEFT
        );


        // =========================================================
        // NETWORK
        // =========================================================

        HBox network =
                networkBar();

        network.setMaxWidth(
                Double.MAX_VALUE
        );


        // =========================================================
        // CONTENT
        // =========================================================

        VBox content =
                new VBox(
                        13,
                        heading,
                        stats,
                        reactor,
                        quick,
                        actions,
                        network
                );

        content.setPadding(
                new Insets(
                        23,
                        25,
                        25,
                        25
                )
        );

        content.setAlignment(
                Pos.TOP_LEFT
        );

        content.setFillWidth(
                true
        );


        // =========================================================
        // CENTER
        // =========================================================

        StackPane center =
                new StackPane(
                        background,
                        content
                );

        center.setStyle(
                "-fx-background-color: #02080d;"
        );

        StackPane.setAlignment(
                content,
                Pos.TOP_LEFT
        );


        // =========================================================
        // ROOT
        // =========================================================

        BorderPane root =
                new BorderPane();

        root.setTop(
                topBar
        );

        root.setLeft(
                sidebar
        );

        root.setCenter(
                center
        );


        // =========================================================
        // NAVIGATION
        // =========================================================

        station.setOnAction(
                e ->
                        new StationView(
                                loggedInUser
                        ).show(stage)
        );

        booking.setOnAction(
                e ->
                        new BookingView(
                                loggedInUser
                        ).show(stage)
        );

        history.setOnAction(
                e ->
                        new MyBookingsView(
                                loggedInUser
                        ).show(stage)
        );

        wallet.setOnAction(
                e ->
                        new WalletView(
                                loggedInUser
                        ).show(stage)
        );

        profile.setOnAction(
                e ->
                        new ProfileView(
                                loggedInUser
                        ).show(stage)
        );

        logout.setOnAction(
                e ->
                        new LoginView()
                                .show(stage)
        );


        // =========================================================
        // QUICK COMMAND NAVIGATION
        // =========================================================

        find.setOnMouseClicked(
                e ->
                        new StationView(
                                loggedInUser
                        ).show(stage)
        );

        book.setOnMouseClicked(
                e ->
                        new BookingView(
                                loggedInUser
                        ).show(stage)
        );

        myBookings.setOnMouseClicked(
                e ->
                        new MyBookingsView(
                                loggedInUser
                        ).show(stage)
        );

        myWallet.setOnMouseClicked(
                e ->
                        new WalletView(
                                loggedInUser
                        ).show(stage)
        );


        // =========================================================
        // SCENE
        // =========================================================

        Scene scene =
                new Scene(
                        root,
                        1180,
                        760
                );

        scene.setFill(
                Color.web(
                        "#02080d"
                )
        );

        stage.setTitle(
                "Smart EV Charging System"
        );

        stage.setScene(
                scene
        );


        // =========================================================
        // SCREEN VISUAL BOUNDS
        // =========================================================

        Rectangle2D bounds =
                Screen.getPrimary()
                        .getVisualBounds();

        stage.setX(
                bounds.getMinX()
        );

        stage.setY(
                bounds.getMinY()
        );

        stage.setWidth(
                bounds.getWidth()
        );

        stage.setHeight(
                bounds.getHeight()
        );

        stage.show();


        // =========================================================
        // ENTRY ANIMATIONS
        // =========================================================

        enter(
                heading,
                0
        );

        enter(
                stats,
                120
        );

        enter(
                reactor,
                240
        );

        enter(
                quick,
                390
        );

        enter(
                actions,
                450
        );

        enter(
                network,
                530
        );
    }


    // =============================================================
    // LABEL
    // =============================================================

    private Label label(
            String text,
            double size,
            Color color
    ) {

        Label label =
                new Label(
                        text
                );

        label.setFont(
                Font.font(
                        "Arial",
                        size
                )
        );

        label.setTextFill(
                color
        );

        return label;
    }


    // =============================================================
    // RESPONSIVE BACKGROUND
    // =============================================================

    private Pane createBackground() {

        Pane pane =
                new Pane();

        pane.setMouseTransparent(
                true
        );


        // =========================================================
        // RESPONSIVE BASE
        // =========================================================

        Rectangle base =
                new Rectangle();

        base.widthProperty()
                .bind(
                        pane.widthProperty()
                );

        base.heightProperty()
                .bind(
                        pane.heightProperty()
                );

        base.setFill(
                new LinearGradient(
                        0,
                        0,
                        1,
                        1,
                        true,
                        CycleMethod.NO_CYCLE,

                        new Stop(
                                0,
                                Color.web(
                                        "#02080d"
                                )
                        ),

                        new Stop(
                                0.48,
                                Color.web(
                                        "#06151b"
                                )
                        ),

                        new Stop(
                                1,
                                Color.web(
                                        "#02090e"
                                )
                        )
                )
        );

        pane.getChildren().add(
                base
        );


        // =========================================================
        // ATMOSPHERE
        // =========================================================

        Circle atmosphere =
                new Circle(
                        310
                );

        atmosphere.setFill(
                new RadialGradient(
                        0,
                        0,
                        0.5,
                        0.5,
                        1,
                        true,
                        CycleMethod.NO_CYCLE,

                        new Stop(
                                0,
                                Color.rgb(
                                        20,
                                        210,
                                        190,
                                        0.10
                                )
                        ),

                        new Stop(
                                0.55,
                                Color.rgb(
                                        10,
                                        120,
                                        130,
                                        0.035
                                )
                        ),

                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        atmosphere.layoutXProperty()
                .bind(
                        pane.widthProperty()
                                .multiply(
                                        0.55
                                )
                );

        atmosphere.layoutYProperty()
                .bind(
                        pane.heightProperty()
                                .multiply(
                                        0.50
                                )
                );

        pane.getChildren().add(
                atmosphere
        );


        // =========================================================
        // RESPONSIVE GRID
        // =========================================================

        for (
                int x = 0;
                x <= 1600;
                x += 45
        ) {

            Line line =
                    new Line();

            line.startXProperty()
                    .set(x);

            line.endXProperty()
                    .set(x);

            line.startYProperty()
                    .set(0);

            line.endYProperty()
                    .bind(
                            pane.heightProperty()
                    );

            line.setStroke(
                    Color.rgb(
                            55,
                            190,
                            185,
                            0.075
                    )
            );

            line.setStrokeWidth(
                    x % 225 == 0
                            ? 1.2
                            : 0.6
            );

            pane.getChildren().add(
                    line
            );
        }


        for (
                int y = 0;
                y <= 1200;
                y += 45
        ) {

            Line line =
                    new Line();

            line.startXProperty()
                    .set(0);

            line.endXProperty()
                    .bind(
                            pane.widthProperty()
                    );

            line.startYProperty()
                    .set(y);

            line.endYProperty()
                    .set(y);

            line.setStroke(
                    Color.rgb(
                            55,
                            190,
                            185,
                            0.075
                    )
            );

            line.setStrokeWidth(
                    y % 225 == 0
                            ? 1.2
                            : 0.6
            );

            pane.getChildren().add(
                    line
            );
        }


// =========================================================
// PARTICLES / SPARKS
// =========================================================

List<Circle> particles = new ArrayList<>();

for (int i = 0; i < 70; i++) {

    Circle particle =
            new Circle(
                    0.9 + random.nextDouble() * 2.2
            );

    particle.setFill(
            i % 3 == 0
                    ? Color.LIGHTGREEN
                    : Color.CYAN
    );

    particle.setOpacity(
            0.18 + random.nextDouble() * 0.62
    );

    // Random starting position
    double startX = random.nextDouble();
    double startY = random.nextDouble();

    particle.layoutXProperty()
            .bind(
                    pane.widthProperty()
                            .multiply(startX)
            );

    particle.layoutYProperty().unbind();

    particle.setLayoutY(
            startY * 760
    );

    particle.setTranslateY(0);

    pane.getChildren().add(particle);

    particles.add(particle);
}


// =========================================================
// SPARK ANIMATION
// =========================================================

AnimationTimer particleTimer =
        new AnimationTimer() {

            private long previousTime = 0;

            @Override
            public void handle(long now) {

                if (previousTime == 0) {
                    previousTime = now;
                    return;
                }

                double delta =
                        (now - previousTime)
                                / 1_000_000_000.0;

                previousTime = now;

                double paneHeight = pane.getHeight();

                if (paneHeight <= 0) {
                    paneHeight = 760;
                }

                for (Circle particle : particles) {

                    double currentY =
                            particle.getTranslateY();

                    // Move spark upward
                    currentY -= delta * 7;

                    /*
                     * When the spark reaches the top,
                     * place it back at the bottom.
                     *
                     * Because layoutY is the original
                     * starting position, we compensate
                     * for it here.
                     */
                    double startY =
                            particle.getLayoutY();

                    if (startY + currentY < -20) {

                        currentY =
                                paneHeight - startY;
                    }

                    particle.setTranslateY(
                            currentY
                    );
                }
            }
        };

particleTimer.start();

        // =========================================================
        // MOVING SCANNER
        // =========================================================

        Rectangle scanner =
                new Rectangle(
                        2,
                        1
                );

        scanner.widthProperty()
                .set(2);

        scanner.heightProperty()
                .bind(
                        pane.heightProperty()
                );

        scanner.setFill(
                Color.rgb(
                        60,
                        255,
                        220,
                        0.12
                )
        );

        pane.getChildren().add(
                scanner
        );


        Timeline scan =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        scanner
                                                .translateXProperty(),
                                        -10
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(5),

                                new KeyValue(
                                        scanner
                                                .translateXProperty(),
                                        1400
                                )
                        )
                );

        scan.setCycleCount(
                Animation.INDEFINITE
        );

        scan.play();


        return pane;
    }


    // =============================================================
    // REACTOR
    // =============================================================

    private StackPane createReactor() {

        StackPane reactor =
                new StackPane();

        reactor.setPrefHeight(
                175
        );

        reactor.setMinHeight(
                175
        );

        reactor.setMaxHeight(
                175
        );


        Circle outer =
                ring(
                        72,
                        Color.rgb(
                                70,
                                255,
                                220,
                                0.25
                        ),
                        2
                );

        Circle middle =
                ring(
                        55,
                        Color.rgb(
                                70,
                                255,
                                220,
                                0.55
                        ),
                        2.5
                );

        Circle inner =
                ring(
                        39,
                        Color.rgb(
                                120,
                                255,
                                220,
                                0.75
                        ),
                        1.5
                );


        Circle core =
                new Circle(
                        31
                );

        core.setFill(
                new RadialGradient(
                        0,
                        0,
                        0.5,
                        0.5,
                        1,
                        true,
                        CycleMethod.NO_CYCLE,

                        new Stop(
                                0,
                                Color.rgb(
                                        210,
                                        255,
                                        250,
                                        0.95
                                )
                        ),

                        new Stop(
                                0.35,
                                Color.rgb(
                                        50,
                                        255,
                                        220,
                                        0.75
                                )
                        ),

                        new Stop(
                                1,
                                Color.rgb(
                                        0,
                                        170,
                                        180,
                                        0.20
                                )
                        )
                )
        );

        core.setStroke(
                Color.CYAN
        );

        core.setStrokeWidth(
                2
        );

        core.setEffect(
                new Glow(1)
        );


        Label lightning =
                label(
                        "⚡",
                        30,
                        Color.WHITE
                );

        lightning.setEffect(
                new Glow(1)
        );


        StackPane coreGroup =
                new StackPane(
                        outer,
                        middle,
                        inner,
                        core,
                        lightning
                );


        // =========================================================
        // OUTER ROTATION
        // =========================================================

        Timeline r1 =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        outer
                                                .rotateProperty(),
                                        0
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(4),

                                new KeyValue(
                                        outer
                                                .rotateProperty(),
                                        360
                                )
                        )
                );

        r1.setCycleCount(
                Animation.INDEFINITE
        );

        r1.play();


        // =========================================================
        // MIDDLE ROTATION
        // =========================================================

        Timeline r2 =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        middle
                                                .rotateProperty(),
                                        360
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(2.7),

                                new KeyValue(
                                        middle
                                                .rotateProperty(),
                                        0
                                )
                        )
                );

        r2.setCycleCount(
                Animation.INDEFINITE
        );

        r2.play();


        // =========================================================
        // INNER ROTATION
        // =========================================================

        Timeline r3 =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        inner
                                                .rotateProperty(),
                                        0
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(1.8),

                                new KeyValue(
                                        inner
                                                .rotateProperty(),
                                        -360
                                )
                        )
                );

        r3.setCycleCount(
                Animation.INDEFINITE
        );

        r3.play();


        // =========================================================
        // CORE PULSE
        // =========================================================

        ScaleTransition pulse =
                new ScaleTransition(
                        Duration.seconds(0.75),
                        coreGroup
                );

        pulse.setFromX(
                0.92
        );

        pulse.setFromY(
                0.92
        );

        pulse.setToX(
                1.08
        );

        pulse.setToY(
                1.08
        );

        pulse.setAutoReverse(
                true
        );

        pulse.setCycleCount(
                Animation.INDEFINITE
        );

        pulse.play();


        // =========================================================
        // ORBIT PARTICLES
        // =========================================================

        Pane orbit =
                new Pane();

        orbit.setPrefSize(
                175,
                175
        );

        orbit.setMinSize(
                175,
                175
        );

        orbit.setMaxSize(
                175,
                175
        );


        for (
                int i = 0;
                i < 12;
                i++
        ) {

            Circle particle =
                    new Circle(
                            i % 3 == 0
                                    ? 3.5
                                    : 2.5,

                            i % 2 == 0
                                    ? Color.CYAN
                                    : Color.LIGHTGREEN
                    );

            double angle =
                    Math.toRadians(
                            i * 30
                    );

            particle.setLayoutX(
                    87.5 +
                    Math.cos(angle) * 76
            );

            particle.setLayoutY(
                    87.5 +
                    Math.sin(angle) * 76
            );

            particle.setEffect(
                    new Glow(1)
            );

            orbit.getChildren().add(
                    particle
            );
        }


        Timeline orbitAnimation =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        orbit
                                                .rotateProperty(),
                                        0
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(4.5),

                                new KeyValue(
                                        orbit
                                                .rotateProperty(),
                                        360
                                )
                        )
                );

        orbitAnimation.setCycleCount(
                Animation.INDEFINITE
        );

        orbitAnimation.play();


        // =========================================================
        // LEFT INFORMATION
        // =========================================================

        Label ready =
                label(
                        "EV READY",
                        10,
                        Color.LIGHTGREEN
                );

        Label battery =
                label(
                        "78%",
                        21,
                        Color.LIGHTGREEN
                );

        Label batteryHint =
                label(
                        "BATTERY",
                        8,
                        Color.rgb(
                                150,
                                190,
                                190
                        )
                );


        VBox left =
                new VBox(
                        3,
                        ready,
                        battery,
                        batteryHint
                );

        left.setAlignment(
                Pos.CENTER_RIGHT
        );


        // =========================================================
        // RIGHT INFORMATION
        // =========================================================

        Label powerTitle =
                label(
                        "LIVE POWER",
                        10,
                        Color.CYAN
                );

        Label power =
                label(
                        "42.6 kW",
                        20,
                        Color.WHITE
                );

        power.setEffect(
                new Glow(0.7)
        );

        Label flow =
                label(
                        "ENERGY FLOW",
                        8,
                        Color.rgb(
                                150,
                                190,
                                190
                        )
                );


        VBox right =
                new VBox(
                        3,
                        powerTitle,
                        power,
                        flow
                );

        right.setAlignment(
                Pos.CENTER_LEFT
        );


        // =========================================================
        // REACTOR VISUAL
        // =========================================================

        StackPane reactorCore =
                new StackPane(
                        orbit,
                        coreGroup
                );

        HBox visual =
                new HBox(
                        55,
                        left,
                        reactorCore,
                        right
                );

        visual.setAlignment(
                Pos.CENTER
        );

        reactor.getChildren().add(
                visual
        );


        // =========================================================
        // REACTOR CLICK
        // =========================================================

        reactor.setOnMouseClicked(
                event -> {

                    pulse.stop();

                    ScaleTransition boost =
                            new ScaleTransition(
                                    Duration.millis(300),
                                    coreGroup
                            );

                    boost.setFromX(
                            1
                    );

                    boost.setFromY(
                            1
                    );

                    boost.setToX(
                            1.25
                    );

                    boost.setToY(
                            1.25
                    );

                    boost.setAutoReverse(
                            true
                    );

                    boost.setCycleCount(
                            2
                    );

                    boost.setOnFinished(
                            e ->
                                    pulse.play()
                    );

                    boost.play();

                    power.setText(
                            "60.0 kW"
                    );
                }
        );


        reactor.setOnMouseExited(
                event ->
                        power.setText(
                                "42.6 kW"
                        )
        );


        return reactor;
    }


    // =============================================================
    // RING
    // =============================================================

    private Circle ring(
            double radius,
            Color stroke,
            double width
    ) {

        Circle circle =
                new Circle(
                        radius
                );

        circle.setFill(
                Color.TRANSPARENT
        );

        circle.setStroke(
                stroke
        );

        circle.setStrokeWidth(
                width
        );

        circle.setEffect(
                new Glow(0.7)
        );

        return circle;
    }


    // =============================================================
    // STAT CARD
    // =============================================================

    private VBox statCard(
            String icon,
            String title,
            String value,
            String status
    ) {

        Label iconLabel =
                label(
                        icon,
                        22,
                        Color.CYAN
                );

        Label titleLabel =
                label(
                        title,
                        10,
                        Color.rgb(
                                135,
                                165,
                                170
                        )
                );

        Label valueLabel =
                label(
                        value,
                        30,
                        Color.WHITE
                );

        Label statusLabel =
                label(
                        status,
                        9,
                        Color.LIGHTGREEN
                );


        VBox card =
                new VBox(
                        3,
                        iconLabel,
                        titleLabel,
                        valueLabel,
                        statusLabel
                );

        card.setPrefWidth(215);
        card.setMinWidth(215);
        card.setMaxWidth(215);

        card.setPrefHeight(112);
        card.setMinHeight(112);
        card.setMaxHeight(112);

        card.setPadding(
                new Insets(
                        13
                )
        );

        card.setStyle(
                cardStyle()
        );

        hover(
                card
        );

        return card;
    }


    // =============================================================
    // ACTION CARD
    // =============================================================

    private VBox actionCard(
            String icon,
            String title,
            String description
    ) {

        Label iconLabel =
                label(
                        icon,
                        25,
                        Color.LIGHTGREEN
                );

        iconLabel.setEffect(
                new Glow(0.7)
        );


        Label titleLabel =
                label(
                        title,
                        14,
                        Color.WHITE
                );


        Label descriptionLabel =
                label(
                        description,
                        10,
                        Color.LIGHTGRAY
                );


        VBox card =
                new VBox(
                        7,
                        iconLabel,
                        titleLabel,
                        descriptionLabel
                );

        card.setPrefWidth(230);
        card.setMinWidth(230);
        card.setMaxWidth(230);

        card.setPrefHeight(105);
        card.setMinHeight(105);
        card.setMaxHeight(105);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(
                        14,
                        16,
                        14,
                        16
                )
        );

        card.setStyle(
                cardStyle()
        );

        hover(
                card
        );

        return card;
    }


    // =============================================================
    // NETWORK BAR
    // =============================================================

    private HBox networkBar() {

        Circle dot =
                new Circle(
                        5,
                        Color.LIGHTGREEN
                );

        dot.setEffect(
                new Glow(1)
        );


        ScaleTransition pulse =
                new ScaleTransition(
                        Duration.seconds(0.7),
                        dot
                );

        pulse.setFromX(
                0.65
        );

        pulse.setFromY(
                0.65
        );

        pulse.setToX(
                1.35
        );

        pulse.setToY(
                1.35
        );

        pulse.setAutoReverse(
                true
        );

        pulse.setCycleCount(
                Animation.INDEFINITE
        );

        pulse.play();


        Label title =
                label(
                        "LIVE CHARGING NETWORK",
                        11,
                        Color.WHITE
                );

        Label status =
                label(
                        "●  READY TO CHARGE     •     ALL SYSTEMS NOMINAL",
                        9,
                        Color.LIGHTGREEN
                );


        VBox info =
                new VBox(
                        2,
                        title,
                        status
                );


        HBox bar =
                new HBox(
                        11,
                        dot,
                        info
                );

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(
                        10,
                        15,
                        10,
                        15
                )
        );

        bar.setMaxWidth(
                Double.MAX_VALUE
        );

        bar.setStyle(
                "-fx-background-color: #091a20;" +
                "-fx-border-color: #21665f;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );

        return bar;
    }


    // =============================================================
    // MENU BUTTON
    // =============================================================

    private Button menuButton(
            String text
    ) {

        Button button =
                new Button(
                        text
                );

        button.setPrefWidth(180);
        button.setMinWidth(180);
        button.setMaxWidth(180);

        button.setPrefHeight(44);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setStyle(
                normalStyle()
        );


        button.setOnMouseEntered(
                event -> {

                    button.setStyle(
                            hoverStyle()
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(130),
                                    button
                            );

                    scale.setToX(
                            1.03
                    );

                    scale.setToY(
                            1.03
                    );

                    scale.play();
                }
        );


        button.setOnMouseExited(
                event -> {

                    if (
                            !button.getText()
                                    .contains(
                                            "DASHBOARD"
                                    )
                    ) {

                        button.setStyle(
                                normalStyle()
                        );

                    } else {

                        button.setStyle(
                                selectedStyle()
                        );
                    }


                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(130),
                                    button
                            );

                    scale.setToX(
                            1
                    );

                    scale.setToY(
                            1
                    );

                    scale.play();
                }
        );


        return button;
    }


    // =============================================================
    // CARD HOVER
    // =============================================================

    private void hover(
            VBox card
    ) {

        card.setOnMouseEntered(
                event -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(170),
                                    card
                            );

                    scale.setToX(
                            1.035
                    );

                    scale.setToY(
                            1.035
                    );

                    scale.play();


                    TranslateTransition move =
                            new TranslateTransition(
                                    Duration.millis(170),
                                    card
                            );

                    move.setToY(
                            -4
                    );

                    move.play();


                    card.setStyle(
                            "-fx-background-color: #0d2b31;" +
                            "-fx-border-color: #4dffd0;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 14;" +
                            "-fx-background-radius: 14;"
                    );
                }
        );


        card.setOnMouseExited(
                event -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(170),
                                    card
                            );

                    scale.setToX(
                            1
                    );

                    scale.setToY(
                            1
                    );

                    scale.play();


                    TranslateTransition move =
                            new TranslateTransition(
                                    Duration.millis(170),
                                    card
                            );

                    move.setToY(
                            0
                    );

                    move.play();


                    card.setStyle(
                            cardStyle()
                    );
                }
        );
    }


    // =============================================================
    // CARD STYLE
    // =============================================================

    private String cardStyle() {

        return
                "-fx-background-color: #081920;" +
                "-fx-border-color: #1d5b59;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;";
    }


    // =============================================================
    // ENTRY ANIMATION
    // =============================================================

    private void enter(
            Node node,
            int delay
    ) {

        node.setOpacity(
                0
        );

        node.setTranslateY(
                18
        );


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(600),
                        node
                );

        fade.setDelay(
                Duration.millis(
                        delay
                )
        );

        fade.setFromValue(
                0
        );

        fade.setToValue(
                1
        );


        TranslateTransition move =
                new TranslateTransition(
                        Duration.millis(600),
                        node
                );

        move.setDelay(
                Duration.millis(
                        delay
                )
        );

        move.setFromY(
                18
        );

        move.setToY(
                0
        );


        fade.play();
        move.play();
    }


    // =============================================================
    // NORMAL MENU STYLE
    // =============================================================

    private String normalStyle() {

        return
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #d5e2e4;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;";
    }


    // =============================================================
    // HOVER MENU STYLE
    // =============================================================

    private String hoverStyle() {

        return
                "-fx-background-color: #12363a;" +
                "-fx-text-fill: #65ffd7;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #2d8f83;" +
                "-fx-border-radius: 10;";
    }


    // =============================================================
    // SELECTED MENU STYLE
    // =============================================================

    private String selectedStyle() {

        return
                "-fx-background-color: #16413f;" +
                "-fx-text-fill: #65ffd7;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #4dffd0;" +
                "-fx-border-radius: 10;";
    }
}