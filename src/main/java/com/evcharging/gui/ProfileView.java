package com.evcharging.gui;

import com.evcharging.model.Booking;
import com.evcharging.model.User;
import com.evcharging.service.BookingService;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

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

public class ProfileView {

    private final User loggedInUser;

    private final BookingService bookingService =
            new BookingService();

    private final Random random =
            new Random(50);

    public ProfileView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // =========================================================
    // SHOW PROFILE
    // =========================================================

    public void show(Stage stage) {

        // =====================================================
        // TOP BAR
        // =====================================================

        Label logo =
                label(
                        "⚡ SMART EV",
                        25,
                        Color.WHITE
                );

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        25
                )
        );

        logo.setEffect(
                new Glow(0.35)
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
                Priority.ALWAYS
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

        // =====================================================
        // SIDEBAR
        // =====================================================

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

        profile.setStyle(
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

        sidebar.setPrefWidth(
                205
        );

        sidebar.setMinWidth(
                205
        );

        sidebar.setMaxWidth(
                205
        );

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

        // =====================================================
        // BOOKING DATA
        // =====================================================

        int totalBookings = 0;
        int activeBookings = 0;
        int cancelledBookings = 0;

        try {

            List<Booking> bookings =
                    bookingService.getBookingsByUser(
                            loggedInUser.getUserId()
                    );

            totalBookings =
                    bookings.size();

            for (
                    Booking bookingData :
                    bookings
            ) {

                if (
                        "CONFIRMED"
                                .equalsIgnoreCase(
                                        bookingData.getStatus()
                                )
                ) {

                    activeBookings++;

                } else if (
                        "CANCELLED"
                                .equalsIgnoreCase(
                                        bookingData.getStatus()
                                )
                ) {

                    cancelledBookings++;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Profile booking data error: "
                    + e.getMessage()
            );
        }

        // =====================================================
        // BACKGROUND
        // =====================================================

        Pane background =
                createBackground();

        // =====================================================
        // MAIN TITLE
        // =====================================================

        Label title =
                label(
                        "MY PROFILE",
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
                new Glow(0.3)
        );

        Label subtitle =
                label(
                        "EV DRIVER ACCOUNT  •  PERSONAL INFORMATION",
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

        heading.setAlignment(
                Pos.TOP_LEFT
        );

        // =====================================================
        // PROFILE HEADER
        // =====================================================

        StackPane avatar =
                createAvatar();

        Label name =
                label(
                        safeText(
                                loggedInUser.getName(),
                                "EV USER"
                        ),
                        22,
                        Color.WHITE
                );

        name.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        Label account =
                label(
                        "REGISTERED EV USER",
                        10,
                        Color.LIGHTGREEN
                );

        VBox identity =
                new VBox(
                        4,
                        name,
                        account
                );

        identity.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox profileHeader =
                new HBox(
                        18,
                        avatar,
                        identity
                );

        profileHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        profileHeader.setPadding(
                new Insets(17)
        );

        profileHeader.setMaxWidth(
                Double.MAX_VALUE
        );

        profileHeader.setStyle(
                cardStyle()
        );

        // =====================================================
        // USER INFORMATION
        // =====================================================

        VBox userInfo =
                informationCard(
                        "USER INFORMATION",
                        "USER ID",
                        String.valueOf(
                                loggedInUser.getUserId()
                        ),
                        "NAME",
                        safeText(
                                loggedInUser.getName(),
                                "Not provided"
                        ),
                        "EMAIL",
                        safeText(
                                loggedInUser.getEmail(),
                                "Not provided"
                        ),
                        "PHONE",
                        safeText(
                                loggedInUser.getPhone(),
                                "Not provided"
                        )
                );

        // =====================================================
        // ACCOUNT STATUS
        // =====================================================

        Circle statusDot =
                new Circle(
                        5,
                        Color.LIGHTGREEN
                );

        statusDot.setEffect(
                new Glow(1)
        );

        Label statusTitle =
                label(
                        "ACCOUNT STATUS",
                        10,
                        Color.rgb(
                                135,
                                165,
                                170
                        )
                );

        Label status =
                label(
                        "ACCOUNT ACTIVE",
                        14,
                        Color.LIGHTGREEN
                );

        status.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        VBox statusBox =
                new VBox(
                        4,
                        statusTitle,
                        status
                );

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox statusCard =
                new HBox(
                        12,
                        statusDot,
                        statusBox
                );

        statusCard.setAlignment(
                Pos.CENTER_LEFT
        );

        statusCard.setPadding(
                new Insets(14)
        );

        statusCard.setMaxWidth(
                Double.MAX_VALUE
        );

        statusCard.setStyle(
                "-fx-background-color: #081c18;" +
                "-fx-border-color: #26755e;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );

        // =====================================================
        // BOOKING STATISTICS
        // =====================================================

        VBox totalCard =
                statCard(
                        "▣",
                        "TOTAL BOOKINGS",
                        String.valueOf(
                                totalBookings
                        )
                );

        VBox activeCard =
                statCard(
                        "●",
                        "ACTIVE",
                        String.valueOf(
                                activeBookings
                        )
                );

        VBox cancelledCard =
                statCard(
                        "×",
                        "CANCELLED",
                        String.valueOf(
                                cancelledBookings
                        )
                );

        HBox statistics =
                new HBox(
                        12,
                        totalCard,
                        activeCard,
                        cancelledCard
                );

        statistics.setAlignment(
                Pos.CENTER_LEFT
        );

        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        Button back =
                new Button(
                        "←  BACK TO DASHBOARD"
                );

        styleActionButton(
                back,
                false
        );

        Button viewBookings =
                new Button(
                        "▤  VIEW MY BOOKINGS"
                );

        styleActionButton(
                viewBookings,
                true
        );

        HBox actions =
                new HBox(
                        14,
                        back,
                        viewBookings
                );

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        // =====================================================
        // CONTENT
        // =====================================================

        VBox content =
                new VBox(
                        13,
                        heading,
                        profileHeader,
                        userInfo,
                        statusCard,
                        statistics,
                        actions
                );

        content.setPadding(
                new Insets(
                        22,
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

        // =====================================================
        // CENTER
        // =====================================================

        StackPane center =
                new StackPane(
                        background,
                        content
                );

        StackPane.setAlignment(
                content,
                Pos.TOP_LEFT
        );

        center.setStyle(
                "-fx-background-color: #02080d;"
        );

        // =====================================================
        // ROOT
        // =====================================================

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

        // =====================================================
        // NAVIGATION
        // =====================================================

        dashboard.setOnAction(
                e ->
                        new DashboardView(
                                loggedInUser
                        ).show(stage)
        );

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

        logout.setOnAction(
                e ->
                        new LoginView().show(stage)
        );

        back.setOnAction(
                e ->
                        new DashboardView(
                                loggedInUser
                        ).show(stage)
        );

        viewBookings.setOnAction(
                e ->
                        new MyBookingsView(
                                loggedInUser
                        ).show(stage)
        );

        // =====================================================
        // SCENE
        // =====================================================

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
                "Smart EV Charging System - Profile"
        );

        stage.setScene(
                scene
        );

        // =====================================================
        // SCREEN SIZE
        // =====================================================

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

        // =====================================================
        // ENTRY ANIMATIONS
        // =====================================================

        enter(
                heading,
                0
        );

        enter(
                profileHeader,
                100
        );

        enter(
                userInfo,
                180
        );

        enter(
                statusCard,
                260
        );

        enter(
                statistics,
                340
        );

        enter(
                actions,
                420
        );
    }

    // =========================================================
    // AVATAR
    // =========================================================

    private StackPane createAvatar() {

        Circle outer =
                new Circle(
                        34
                );

        outer.setFill(
                Color.rgb(
                        8,
                        45,
                        48
                )
        );

        outer.setStroke(
                Color.rgb(
                        77,
                        255,
                        208
                )
        );

        outer.setStrokeWidth(
                1.8
        );

        outer.setEffect(
                new Glow(0.8)
        );

        Label icon =
                label(
                        "◉",
                        30,
                        Color.LIGHTGREEN
                );

        icon.setEffect(
                new Glow(0.7)
        );

        StackPane avatar =
                new StackPane(
                        outer,
                        icon
                );

        avatar.setPrefSize(
                70,
                70
        );

        ScaleTransition pulse =
                new ScaleTransition(
                        Duration.seconds(1.5),
                        avatar
                );

        pulse.setFromX(
                0.97
        );

        pulse.setFromY(
                0.97
        );

        pulse.setToX(
                1.03
        );

        pulse.setToY(
                1.03
        );

        pulse.setAutoReverse(
                true
        );

        pulse.setCycleCount(
                Animation.INDEFINITE
        );

        pulse.play();

        return avatar;
    }

    // =========================================================
    // INFORMATION CARD
    // =========================================================

    private VBox informationCard(
            String heading,
            String label1,
            String value1,
            String label2,
            String value2,
            String label3,
            String value3,
            String label4,
            String value4
    ) {

        Label title =
                label(
                        heading,
                        12,
                        Color.CYAN
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        VBox row1 =
                infoRow(
                        label1,
                        value1
                );

        VBox row2 =
                infoRow(
                        label2,
                        value2
                );

        VBox row3 =
                infoRow(
                        label3,
                        value3
                );

        VBox row4 =
                infoRow(
                        label4,
                        value4
                );

        VBox card =
                new VBox(
                        8,
                        title,
                        row1,
                        row2,
                        row3,
                        row4
                );

        card.setPadding(
                new Insets(14)
        );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setStyle(
                cardStyle()
        );

        hover(
                card
        );

        return card;
    }

    // =========================================================
    // INFORMATION ROW
    // =========================================================

    private VBox infoRow(
            String title,
            String value
    ) {

        Label t =
                label(
                        title,
                        9,
                        Color.rgb(
                                130,
                                165,
                                170
                        )
                );

        t.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        Label v =
                label(
                        safeText(
                                value,
                                "Not provided"
                        ),
                        14,
                        Color.WHITE
                );

        VBox row =
                new VBox(
                        2,
                        t,
                        v
                );

        row.setPadding(
                new Insets(
                        5,
                        9,
                        5,
                        9
                )
        );

        row.setMaxWidth(
                Double.MAX_VALUE
        );

        row.setStyle(
                "-fx-background-color: #07151b;" +
                "-fx-background-radius: 8;"
        );

        return row;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox statCard(
            String icon,
            String title,
            String value
    ) {

        Label i =
                label(
                        icon,
                        20,
                        Color.CYAN
                );

        i.setEffect(
                new Glow(0.7)
        );

        Label t =
                label(
                        title,
                        9,
                        Color.rgb(
                                135,
                                165,
                                170
                        )
                );

        Label v =
                label(
                        value,
                        27,
                        Color.WHITE
                );

        v.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        27
                )
        );

        VBox card =
                new VBox(
                        4,
                        i,
                        t,
                        v
                );

        card.setPrefWidth(
                215
        );

        card.setMinWidth(
                215
        );

        card.setMaxWidth(
                215
        );

        card.setPrefHeight(
                100
        );

        card.setMinHeight(
                100
        );

        card.setMaxHeight(
                100
        );

        card.setPadding(
                new Insets(12)
        );

        card.setStyle(
                cardStyle()
        );

        hover(
                card
        );

        return card;
    }

    // =========================================================
    // BACKGROUND
    // =========================================================

    private Pane createBackground() {

        Pane pane =
                new Pane();

        pane.setMouseTransparent(
                true
        );

        // =====================================================
        // RESPONSIVE BASE
        // =====================================================

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
                                0.50,
                                Color.web(
                                        "#06171d"
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

        // =====================================================
        // ATMOSPHERE
        // =====================================================

        Circle atmosphere =
                new Circle(
                        300
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

        // =====================================================
        // GRID
        // =====================================================

        for (
                int x = 0;
                x <= 2000;
                x += 45
        ) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            1500
                    );

            line.setStroke(
                    Color.rgb(
                            55,
                            190,
                            185,
                            0.07
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
                y <= 1500;
                y += 45
        ) {

            Line line =
                    new Line(
                            0,
                            y,
                            2000,
                            y
                    );

            line.setStroke(
                    Color.rgb(
                            55,
                            190,
                            185,
                            0.07
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

        // =====================================================
        // PARTICLES
        // =====================================================

        List<Circle> particles =
                new ArrayList<>();

        for (
                int i = 0;
                i < 60;
                i++
        ) {

            Circle particle =
                    new Circle(
                            0.8 +
                            random.nextDouble() * 2
                    );

            particle.setFill(
                    i % 2 == 0
                            ? Color.CYAN
                            : Color.LIGHTGREEN
            );

            particle.setOpacity(
                    0.15 +
                    random.nextDouble() * 0.65
            );

            final double xRatio =
                    random.nextDouble();

            particle.layoutXProperty()
                    .bind(
                            pane.widthProperty()
                                    .multiply(
                                            xRatio
                                    )
                    );

            particle.setLayoutY(
                    random.nextDouble() *
                    900
            );

            pane.getChildren().add(
                    particle
            );

            particles.add(
                    particle
            );
        }

        AnimationTimer timer =
                new AnimationTimer() {

                    private long previous;

                    @Override
                    public void handle(
                            long now
                    ) {

                        if (
                                previous == 0
                        ) {

                            previous = now;
                            return;
                        }

                        double delta =
                                (
                                        now -
                                        previous
                                )
                                /
                                1_000_000_000.0;

                        previous = now;

                        for (
                                Circle particle :
                                particles
                        ) {

                            particle.setLayoutY(
                                    particle.getLayoutY()
                                    -
                                    delta * 6
                            );

                            if (
                                    particle.getLayoutY()
                                    < -5
                            ) {

                                particle.setLayoutY(
                                        Math.max(
                                                pane.getHeight(),
                                                745
                                        )
                                );
                            }
                        }
                    }
                };

        timer.start();

        return pane;
    }

    // =========================================================
    // MENU BUTTON
    // =========================================================

    private Button menuButton(
            String text
    ) {

        Button button =
                new Button(
                        text
                );

        button.setPrefWidth(
                180
        );

        button.setMinWidth(
                180
        );

        button.setMaxWidth(
                180
        );

        button.setPrefHeight(
                44
        );

        button.setMinHeight(
                44
        );

        button.setMaxHeight(
                44
        );

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
                            button.getText()
                                    .contains(
                                            "PROFILE"
                                    )
                    ) {

                        button.setStyle(
                                selectedStyle()
                        );

                    } else {

                        button.setStyle(
                                normalStyle()
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

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private void styleActionButton(
            Button button,
            boolean primary
    ) {

        button.setPrefWidth(
                210
        );

        button.setMinWidth(
                210
        );

        button.setMaxWidth(
                210
        );

        button.setPrefHeight(
                43
        );

        button.setMinHeight(
                43
        );

        button.setMaxHeight(
                43
        );

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        button.setStyle(
                primary
                        ? primaryButtonStyle()
                        : secondaryButtonStyle()
        );

        button.setOnMouseEntered(
                event -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(140),
                                    button
                            );

                    scale.setToX(
                            1.04
                    );

                    scale.setToY(
                            1.04
                    );

                    scale.play();
                }
        );

        button.setOnMouseExited(
                event -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(140),
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
    }

    // =========================================================
    // CARD HOVER
    // =========================================================

    private void hover(
            VBox card
    ) {

        card.setOnMouseEntered(
                event -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(160),
                                    card
                            );

                    scale.setToX(
                            1.025
                    );

                    scale.setToY(
                            1.025
                    );

                    scale.play();

                    TranslateTransition move =
                            new TranslateTransition(
                                    Duration.millis(160),
                                    card
                            );

                    move.setToY(
                            -3
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
                                    Duration.millis(160),
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
                                    Duration.millis(160),
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

    // =========================================================
    // CARD STYLE
    // =========================================================

    private String cardStyle() {

        return
                "-fx-background-color: #081920;" +
                "-fx-border-color: #1d5b59;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;";
    }

    // =========================================================
    // ENTRY ANIMATION
    // =========================================================

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

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value,
            String fallback
    ) {

        if (
                value == null ||
                value.trim().isEmpty()
        ) {

            return fallback;
        }

        return value;
    }

    // =========================================================
    // LABEL
    // =========================================================

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

    // =========================================================
    // NORMAL BUTTON STYLE
    // =========================================================

    private String normalStyle() {

        return
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #d5e2e4;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;";
    }

    // =========================================================
    // HOVER BUTTON STYLE
    // =========================================================

    private String hoverStyle() {

        return
                "-fx-background-color: #12363a;" +
                "-fx-text-fill: #65ffd7;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #2d8f83;" +
                "-fx-border-radius: 10;";
    }

    // =========================================================
    // SELECTED BUTTON STYLE
    // =========================================================

    private String selectedStyle() {

        return
                "-fx-background-color: #16413f;" +
                "-fx-text-fill: #65ffd7;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #4dffd0;" +
                "-fx-border-radius: 10;";
    }

    // =========================================================
    // PRIMARY BUTTON STYLE
    // =========================================================

    private String primaryButtonStyle() {

        return
                "-fx-background-color: #16413f;" +
                "-fx-text-fill: #65ffd7;" +
                "-fx-border-color: #4dffd0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;";
    }

    // =========================================================
    // SECONDARY BUTTON STYLE
    // =========================================================

    private String secondaryButtonStyle() {

        return
                "-fx-background-color: #081920;" +
                "-fx-text-fill: #d5e2e4;" +
                "-fx-border-color: #1d5b59;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;";
    }
}