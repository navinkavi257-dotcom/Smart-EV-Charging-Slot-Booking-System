package com.evcharging.gui;

import com.evcharging.exception.BookingException;
import com.evcharging.model.Booking;
import com.evcharging.model.User;
import com.evcharging.service.BookingService;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

public class MyBookingsView {

    private final User loggedInUser;

    private final BookingService bookingService =
            new BookingService();

    private final Random random =
            new Random(42);

    private VBox bookingContainer;

    private Label totalValue;
    private Label activeValue;
    private Label chargingValue;
    private Label completedValue;


    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    public MyBookingsView(User loggedInUser) {

        this.loggedInUser =
                loggedInUser;
    }


    // =============================================================
    // SHOW
    // =============================================================

    public void show(Stage stage) {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: #02090e;"
        );


        Pane background =
                createBackground();


        StackPane center =
                new StackPane(
                        background
                );


        VBox content =
                new VBox(18);

        content.setPadding(
                new Insets(24, 30, 28, 30)
        );


        HBox topBar =
                createTopBar(stage);

        VBox heading =
                createHeading();

        HBox stats =
                createStats();


        bookingContainer =
                new VBox(14);

        bookingContainer.setFillWidth(
                true
        );


        ScrollPane scroll =
                new ScrollPane(
                        bookingContainer
                );

        scroll.setFitToWidth(
                true
        );

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );


        VBox.setVgrow(
                scroll,
                Priority.ALWAYS
        );


        content.getChildren().addAll(
                heading,
                stats,
                createSectionTitle(),
                scroll
        );


        center.getChildren().add(
                content
        );


        StackPane.setAlignment(
                content,
                Pos.TOP_LEFT
        );


        root.setTop(
                topBar
        );

        root.setCenter(
                center
        );


        Scene scene =
                new Scene(
                        root,
                        1250,
                        800
                );


        scene.setFill(
                Color.web("#02090e")
        );


        stage.setTitle(
                "Smart EV - My Bookings"
        );

        stage.setScene(
                scene
        );


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


        loadBookings();


        animateEntrance(
                heading,
                0
        );

        animateEntrance(
                stats,
                100
        );

        animateEntrance(
                scroll,
                180
        );
    }


    // =============================================================
    // TOP BAR
    // =============================================================

    private HBox createTopBar(
            Stage stage) {

        Button back =
                new Button(
                        "← DASHBOARD"
                );


        back.setPrefHeight(
                38
        );

        back.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        back.setStyle(
                secondaryButtonStyle()
        );


        back.setOnMouseEntered(
                e -> back.setStyle(
                        primaryButtonStyle()
                )
        );


        back.setOnMouseExited(
                e -> back.setStyle(
                        secondaryButtonStyle()
                )
        );


        back.setOnAction(
                e ->
                        new DashboardView(
                                loggedInUser
                        ).show(stage)
        );


        Label logo =
                new Label(
                        "⚡ SMART EV"
                );

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        24
                )
        );

        logo.setTextFill(
                Color.web("#5ff2c8")
        );


        Label online =
                new Label(
                        "● SYSTEM ONLINE"
                );

        online.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        online.setTextFill(
                Color.LIGHTGREEN
        );


        Label title =
                new Label(
                        "CHARGING CONTROL"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        title.setTextFill(
                Color.web("#8fa7ad")
        );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        HBox bar =
                new HBox(
                        18,
                        back,
                        logo,
                        online,
                        spacer,
                        title
                );


        bar.setAlignment(
                Pos.CENTER_LEFT
        );


        bar.setPadding(
                new Insets(
                        12,
                        22,
                        12,
                        22
                )
        );


        bar.setStyle(
                "-fx-background-color: #030c12;" +
                "-fx-border-color: #123b42;" +
                "-fx-border-width: 0 0 1 0;"
        );


        return bar;
    }


    // =============================================================
    // HEADING
    // =============================================================

    private VBox createHeading() {

        Label title =
                new Label(
                        "MY BOOKINGS"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        32
                )
        );

        title.setTextFill(
                Color.WHITE
        );


        Label subtitle =
                new Label(
                        "RESERVATION CONTROL  •  CHARGING SESSIONS  •  ENERGY BILLING"
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        subtitle.setTextFill(
                Color.web("#5ff2c8")
        );


        return new VBox(
                4,
                title,
                subtitle
        );
    }


    // =============================================================
    // STATS
    // =============================================================

    private HBox createStats() {

        totalValue =
                createValueLabel();

        activeValue =
                createValueLabel();

        chargingValue =
                createValueLabel();

        completedValue =
                createValueLabel();


        VBox total =
                statCard(
                        "▣",
                        "TOTAL",
                        totalValue,
                        "RESERVATIONS"
                );


        VBox active =
                statCard(
                        "●",
                        "CONFIRMED",
                        activeValue,
                        "READY"
                );


        VBox charging =
                statCard(
                        "⚡",
                        "CHARGING",
                        chargingValue,
                        "LIVE SESSION"
                );


        VBox completed =
                statCard(
                        "✓",
                        "COMPLETED",
                        completedValue,
                        "ENERGY BILLED"
                );


        HBox row =
                new HBox(
                        14,
                        total,
                        active,
                        charging,
                        completed
                );


        row.setFillHeight(
                true
        );


        return row;
    }


    private Label createValueLabel() {

        Label label =
                new Label("0");

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        27
                )
        );

        label.setTextFill(
                Color.WHITE
        );

        return label;
    }


    private VBox statCard(
            String icon,
            String title,
            Label value,
            String footer) {

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        iconLabel.setTextFill(
                Color.web("#20d6c7")
        );


        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        titleLabel.setTextFill(
                Color.web("#78949a")
        );


        Label footerLabel =
                new Label(
                        footer
                );

        footerLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        footerLabel.setTextFill(
                Color.web("#5ff2c8")
        );


        VBox card =
                new VBox(
                        6,
                        iconLabel,
                        titleLabel,
                        value,
                        footerLabel
                );


        card.setPadding(
                new Insets(14)
        );


        card.setMinHeight(
                125
        );


        card.setPrefWidth(
                230
        );


        card.setStyle(
                "-fx-background-color: rgba(7,25,32,0.95);" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #15535c;" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1;"
        );


        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );


        return card;
    }


    // =============================================================
    // SECTION TITLE
    // =============================================================

    private Label createSectionTitle() {

        Label label =
                new Label(
                        "RESERVATION CONTROL"
                );

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        label.setTextFill(
                Color.WHITE
        );


        return label;
    }


    // =============================================================
    // LOAD BOOKINGS
    // =============================================================

    private void loadBookings() {

        bookingContainer
                .getChildren()
                .clear();


        List<Booking> bookings =
                bookingService.getBookingsByUser(
                        loggedInUser.getUserId()
                );


        int confirmed = 0;
        int charging = 0;
        int completed = 0;


        if (bookings == null ||
                bookings.isEmpty()) {

            totalValue.setText("0");
            activeValue.setText("0");
            chargingValue.setText("0");
            completedValue.setText("0");


            Label empty =
                    new Label(
                            "NO RESERVATIONS FOUND\n\n" +
                            "Book a charging slot from the dashboard."
                    );


            empty.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            15
                    )
            );

            empty.setTextFill(
                    Color.web("#8fa7ad")
            );

            empty.setAlignment(
                    Pos.CENTER
            );

            empty.setMaxWidth(
                    Double.MAX_VALUE
            );

            empty.setPadding(
                    new Insets(60)
            );


            bookingContainer
                    .getChildren()
                    .add(empty);


            return;
        }


        for (Booking booking :
                bookings) {

            if ("CONFIRMED".equalsIgnoreCase(
                    booking.getStatus()
            )) {

                confirmed++;
            }


            if ("CHARGING".equalsIgnoreCase(
                    booking.getStatus()
            )) {

                charging++;
            }


            if ("COMPLETED".equalsIgnoreCase(
                    booking.getStatus()
            )) {

                completed++;
            }


            bookingContainer
                    .getChildren()
                    .add(
                            createBookingCard(
                                    booking
                            )
                    );
        }


        totalValue.setText(
                String.valueOf(
                        bookings.size()
                )
        );

        activeValue.setText(
                String.valueOf(
                        confirmed
                )
        );

        chargingValue.setText(
                String.valueOf(
                        charging
                )
        );

        completedValue.setText(
                String.valueOf(
                        completed
                )
        );
    }


    // =============================================================
    // BOOKING CARD
    // =============================================================

    private VBox createBookingCard(
            Booking booking) {

        VBox card =
                new VBox(
                        12
                );


        card.setPadding(
                new Insets(17)
        );


        card.setMaxWidth(
                Double.MAX_VALUE
        );


        String status =
                booking.getStatus();


        Color statusColor =
                getStatusColor(
                        status
                );


        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        HBox header =
                new HBox();


        Label id =
                new Label(
                        "BOOKING #" +
                        booking.getBookingId()
                );

        id.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        17
                )
        );

        id.setTextFill(
                Color.WHITE
        );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Label statusLabel =
                new Label(
                        "● " +
                        status
                                .toUpperCase()
                );

        statusLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        statusLabel.setTextFill(
                statusColor
        );


        header.getChildren().addAll(
                id,
                spacer,
                statusLabel
        );


        // ---------------------------------------------------------
        // DETAILS
        // ---------------------------------------------------------

        HBox details =
                new HBox(
                        24
                );


        details.getChildren().addAll(

                infoBlock(
                        "STATION",
                        "STATION #" +
                        booking.getStationId()
                ),

                infoBlock(
                        "CHARGER",
                        "CHARGER #" +
                        booking.getChargerId()
                ),

                infoBlock(
                        "DATE",
                        booking.getBookingDate()
                ),

                infoBlock(
                        "TIME",
                        booking.getStartTime()
                ),

                infoBlock(
                        "DURATION",
                        booking.getDuration() +
                        " min"
                ),

                infoBlock(
                        "ENERGY",
                        String.format(
                                "%.3f kWh",
                                booking.getEnergyConsumed()
                        )
                ),

                infoBlock(
                        "CHARGING COST",
                        String.format(
                                "₹%.2f",
                                booking.getChargingCost()
                        )
                )
        );


        // ---------------------------------------------------------
        // PAYMENT LINE
        // ---------------------------------------------------------

        Label payment =
                new Label(
                        String.format(
                                "Booking Fee  ₹%.2f     •     " +
                                "Charging Cost  ₹%.2f     •     " +
                                "Total Paid  ₹%.2f",
                                booking.getBookingFee(),
                                booking.getChargingCost(),
                                booking.getBookingFee()
                                        +
                                booking.getChargingCost()
                        )
                );


        payment.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );


        payment.setTextFill(
                Color.web("#c9dadd")
        );


        // ---------------------------------------------------------
        // ACTIONS
        // ---------------------------------------------------------

        HBox actions =
                new HBox(
                        10
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );


        if ("CONFIRMED".equalsIgnoreCase(
                status
        )) {

            Button start =
                    new Button(
                            "⚡ START CHARGING"
                    );


            stylePrimaryButton(
                    start
            );


            start.setPrefHeight(
                    40
            );


            start.setOnAction(
                    e -> {

                        try {

                            bookingService.startCharging(
                                    booking.getBookingId()
                            );


                            new ChargingSessionView(
                                    loggedInUser,
                                    booking.getBookingId()
                            ).show(
                                    (Stage)
                                            start.getScene()
                                                    .getWindow()
                            );


                        } catch (BookingException ex) {

                            showError(
                                    ex.getMessage()
                            );
                        }
                    }
            );


            Button cancel =
                    new Button(
                            "× CANCEL"
                    );


            styleDangerButton(
                    cancel
            );


            cancel.setPrefHeight(
                    40
            );


            cancel.setOnAction(
                    e ->
                            confirmCancellation(
                                    booking
                            )
            );


            actions.getChildren().addAll(
                    cancel,
                    start
            );


        } else if ("CHARGING".equalsIgnoreCase(
                status
        )) {

            Button continueButton =
                    new Button(
                            "⚡ OPEN LIVE SESSION"
                    );


            stylePrimaryButton(
                    continueButton
            );


            continueButton.setPrefHeight(
                    40
            );


            continueButton.setOnAction(
                    e ->
                            new ChargingSessionView(
                                    loggedInUser,
                                    booking.getBookingId()
                            ).show(
                                    (Stage)
                                            continueButton
                                                    .getScene()
                                                    .getWindow()
                            )
            );


            actions.getChildren().add(
                    continueButton
            );


        } else if ("COMPLETED".equalsIgnoreCase(
                status
        )) {

            Button receipt =
                    new Button(
                            "▣ VIEW RECEIPT"
                    );


            styleSecondaryButton(
                    receipt
            );


            receipt.setPrefHeight(
                    40
            );


            receipt.setOnAction(
                    e ->
                            showReceipt(
                                    booking
                            )
            );


            actions.getChildren().add(
                    receipt
            );


        } else {

            Label noAction =
                    new Label(
                            "NO ACTION AVAILABLE"
                    );

            noAction.setTextFill(
                    Color.web("#63777c")
            );


            actions.getChildren().add(
                    noAction
            );
        }


        card.getChildren().addAll(
                header,
                details,
                payment,
                actions
        );


        card.setStyle(
                "-fx-background-color: rgba(6,23,30,0.96);" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " +
                toHex(statusColor) +
                ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );


        addCardHover(
                card
        );


        return card;
    }


    // =============================================================
    // INFO BLOCK
    // =============================================================

    private VBox infoBlock(
            String title,
            String value) {

        Label top =
                new Label(
                        title
                );

        top.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        top.setTextFill(
                Color.web("#6e898f")
        );


        Label bottom =
                new Label(
                        value
                );

        bottom.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        bottom.setTextFill(
                Color.WHITE
        );


        VBox box =
                new VBox(
                        4,
                        top,
                        bottom
                );


        box.setMinWidth(
                105
        );


        return box;
    }


// =============================================================
// FUTURISTIC CANCELLATION COMMAND
// =============================================================

private void confirmCancellation(Booking booking) {

    Stage owner =
            (Stage) bookingContainer
                    .getScene()
                    .getWindow();

    Stage dialog =
            new Stage();

    dialog.initOwner(owner);

    dialog.setTitle("Smart EV - Cancel Reservation");

    dialog.setResizable(false);

    // ---------------------------------------------------------
    // MAIN PANEL
    // ---------------------------------------------------------

    VBox panel =
            new VBox(18);

    panel.setAlignment(Pos.CENTER_LEFT);

    panel.setPadding(
            new Insets(28)
    );

    panel.setPrefWidth(470);

    panel.setStyle(
            "-fx-background-color: #030d13;" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: #71313a;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 18;"
    );

    // ---------------------------------------------------------
    // TOP INDICATOR
    // ---------------------------------------------------------

    HBox indicator =
            new HBox(10);

    indicator.setAlignment(
            Pos.CENTER_LEFT
    );

    Circle warningDot =
            new Circle(6);

    warningDot.setFill(
            Color.web("#ff6978")
    );

    Label command =
            new Label(
                    "RESERVATION COMMAND"
            );

    command.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    10
            )
    );

    command.setTextFill(
            Color.web("#ff6978")
    );

    indicator.getChildren().addAll(
            warningDot,
            command
    );

    // ---------------------------------------------------------
    // TITLE
    // ---------------------------------------------------------

    Label title =
            new Label(
                    "CANCEL BOOKING"
            );

    title.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    25
            )
    );

    title.setTextFill(
            Color.WHITE
    );

    // ---------------------------------------------------------
    // BOOKING ID
    // ---------------------------------------------------------

    Label bookingId =
            new Label(
                    "BOOKING #" +
                    booking.getBookingId()
            );

    bookingId.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    13
            )
    );

    bookingId.setTextFill(
            Color.web("#7de3e8")
    );

    // ---------------------------------------------------------
    // DETAILS PANEL
    // ---------------------------------------------------------

    VBox details =
            new VBox(8);

    details.setPadding(
            new Insets(16)
    );

    details.setStyle(
            "-fx-background-color: #071921;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #173b43;" +
            "-fx-border-radius: 12;"
    );

    Label station =
            dialogInfo(
                    "STATION",
                    "STATION #" +
                    booking.getStationId()
            );

    Label charger =
            dialogInfo(
                    "CHARGER",
                    "CHARGER #" +
                    booking.getChargerId()
            );

    Label slot =
            dialogInfo(
                    "RESERVED SLOT",
                    booking.getBookingDate() +
                    "  •  " +
                    booking.getStartTime()
            );

    Label fee =
            dialogInfo(
                    "REFUND",
                    String.format(
                            "₹%.2f BOOKING FEE → WALLET",
                            booking.getBookingFee()
                    )
            );

    details.getChildren().addAll(
            station,
            charger,
            slot,
            fee
    );

    // ---------------------------------------------------------
    // WARNING
    // ---------------------------------------------------------

    Label warning =
            new Label(
                    "Are you sure you want to cancel this reservation?\n" +
                    "The reserved charging slot will become available again."
            );

    warning.setWrapText(
            true
    );

    warning.setFont(
            Font.font(
                    "Arial",
                    FontWeight.NORMAL,
                    12
            )
    );

    warning.setTextFill(
            Color.web("#a8bec2")
    );

    // ---------------------------------------------------------
    // BUTTONS
    // ---------------------------------------------------------

    HBox buttons =
            new HBox(10);

    buttons.setAlignment(
            Pos.CENTER_RIGHT
    );

    Button keep =
            new Button(
                    "KEEP RESERVATION"
            );

    keep.setPrefHeight(40);

    keep.setStyle(
            secondaryButtonStyle()
    );

    keep.setOnMouseEntered(
            e ->
                    keep.setStyle(
                            "-fx-background-color: #10272e;" +
                            "-fx-text-fill: white;" +
                            "-fx-border-color: #5ff2c8;" +
                            "-fx-border-radius: 9;" +
                            "-fx-background-radius: 9;" +
                            "-fx-font-weight: bold;"
                    )
    );

    keep.setOnMouseExited(
            e ->
                    keep.setStyle(
                            secondaryButtonStyle()
                    )
    );

    Button cancel =
            new Button(
                    "× CONFIRM CANCEL"
            );

    cancel.setPrefHeight(40);

    cancel.setStyle(
            "-fx-background-color: #431d23;" +
            "-fx-text-fill: #ff9da6;" +
            "-fx-border-color: #ff6978;" +
            "-fx-border-radius: 9;" +
            "-fx-background-radius: 9;" +
            "-fx-font-weight: bold;"
    );

    cancel.setOnMouseEntered(
            e ->
                    cancel.setStyle(
                            "-fx-background-color: #ff6978;" +
                            "-fx-text-fill: #16070a;" +
                            "-fx-border-color: #ff9da6;" +
                            "-fx-border-radius: 9;" +
                            "-fx-background-radius: 9;" +
                            "-fx-font-weight: bold;"
                    )
    );

    cancel.setOnMouseExited(
            e ->
                    cancel.setStyle(
                            "-fx-background-color: #431d23;" +
                            "-fx-text-fill: #ff9da6;" +
                            "-fx-border-color: #ff6978;" +
                            "-fx-border-radius: 9;" +
                            "-fx-background-radius: 9;" +
                            "-fx-font-weight: bold;"
                    )
    );

    keep.setOnAction(
            e -> dialog.close()
    );

    cancel.setOnAction(
            e -> {

                try {

                    bookingService.cancelBooking(
                            booking.getBookingId()
                    );

                    dialog.close();

                    loadBookings();

                    showCancellationSuccess(
                            booking
                    );

                } catch (BookingException ex) {

                    showError(
                            ex.getMessage()
                    );
                }
            }
    );

    buttons.getChildren().addAll(
            keep,
            cancel
    );

    // ---------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------

    panel.getChildren().addAll(
            indicator,
            title,
            bookingId,
            details,
            warning,
            buttons
    );

    StackPane root =
            new StackPane(
                    panel
            );

    root.setStyle(
            "-fx-background-color: #02090e;"
    );

    Scene scene =
            new Scene(
                    root,
                    520,
                    480
            );

    scene.setFill(
            Color.web("#02090e")
    );

    dialog.setScene(scene);

    // ---------------------------------------------------------
    // ENTRANCE ANIMATION
    // ---------------------------------------------------------

    panel.setOpacity(0);

    panel.setScaleX(0.94);

    panel.setScaleY(0.94);

    FadeTransition fade =
            new FadeTransition(
                    Duration.millis(220),
                    panel
            );

    fade.setFromValue(0);

    fade.setToValue(1);

    ScaleTransition scale =
            new ScaleTransition(
                    Duration.millis(220),
                    panel
            );

    scale.setFromX(0.94);

    scale.setFromY(0.94);

    scale.setToX(1);

    scale.setToY(1);

    fade.play();

    scale.play();

    dialog.show();
}


// =============================================================
// DIALOG INFO
// =============================================================

private Label dialogInfo(
        String title,
        String value) {

    Label label =
            new Label(
                    title +
                    "     " +
                    value
            );

    label.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    11
            )
    );

    label.setTextFill(
            Color.web("#c9dadd")
    );

    return label;
}


// =============================================================
// CANCELLATION SUCCESS
// =============================================================

private void showCancellationSuccess(
        Booking booking) {

    Alert alert =
            new Alert(
                    Alert.AlertType.INFORMATION
            );

    alert.setTitle(
            "Smart EV"
    );

    alert.setHeaderText(
            "✓ RESERVATION CANCELLED"
    );

    alert.setContentText(
            String.format(
                    "Booking #%d has been cancelled.\n\n" +
                    "₹%.2f booking fee has been refunded " +
                    "to your wallet.",
                    booking.getBookingId(),
                    booking.getBookingFee()
            )
    );

    alert.showAndWait();
}

    // =============================================================
    // RECEIPT
    // =============================================================

    private void showReceipt(
            Booking booking) {

        String receipt =
                "⚡ SMART EV\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "CHARGING RECEIPT\n\n" +
                "Booking ID       : #" +
                booking.getBookingId() + "\n" +
                "Station          : #" +
                booking.getStationId() + "\n" +
                "Charger          : #" +
                booking.getChargerId() + "\n" +
                "Date             : " +
                booking.getBookingDate() + "\n" +
                "Start Time       : " +
                booking.getStartTime() + "\n\n" +
                "Energy Consumed  : " +
                String.format(
                        "%.3f kWh",
                        booking.getEnergyConsumed()
                ) + "\n" +
                "Energy Rate      : ₹" +
                String.format(
                        "%.2f",
                        bookingService.getRatePerKWh()
                ) +
                " / kWh\n\n" +
                "Booking Fee      : ₹" +
                String.format(
                        "%.2f",
                        booking.getBookingFee()
                ) + "\n" +
                "Charging Cost    : ₹" +
                String.format(
                        "%.2f",
                        booking.getChargingCost()
                ) + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "TOTAL PAID       : ₹" +
                String.format(
                        "%.2f",
                        booking.getBookingFee()
                                +
                        booking.getChargingCost()
                ) +
                "\n━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "STATUS: COMPLETED\n" +
                "Thank you for using Smart EV.";


        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                "Smart EV Receipt"
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                receipt
        );


        alert.showAndWait();
    }


    // =============================================================
    // ERROR
    // =============================================================

    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "Smart EV"
        );

        alert.setHeaderText(
                "Operation Failed"
        );

        alert.setContentText(
                message
        );


        alert.showAndWait();
    }


    // =============================================================
    // STATUS COLOR
    // =============================================================

    private Color getStatusColor(
            String status) {

        if ("COMPLETED".equalsIgnoreCase(
                status
        )) {

            return Color.web("#5ff2c8");
        }


        if ("CHARGING".equalsIgnoreCase(
                status
        )) {

            return Color.web("#20d6c7");
        }


        if ("CANCELLED".equalsIgnoreCase(
                status
        )) {

            return Color.web("#ff6978");
        }


        return Color.web("#7de3e8");
    }


    private String toHex(
            Color color) {

        return String.format(
                "#%02X%02X%02X",
                (int)
                        Math.round(
                                color.getRed()
                                        * 255
                        ),
                (int)
                        Math.round(
                                color.getGreen()
                                        * 255
                        ),
                (int)
                        Math.round(
                                color.getBlue()
                                        * 255
                        )
        );
    }


    // =============================================================
    // BUTTON STYLES
    // =============================================================

    private void stylePrimaryButton(
            Button button) {

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );


        button.setStyle(
                primaryButtonStyle()
        );


        button.setOnMouseEntered(
                e ->
                        button.setStyle(
                                "-fx-background-color: #5ff2c8;" +
                                "-fx-text-fill: #031012;" +
                                "-fx-border-color: #5ff2c8;" +
                                "-fx-border-radius: 9;" +
                                "-fx-background-radius: 9;" +
                                "-fx-font-weight: bold;"
                        )
        );


        button.setOnMouseExited(
                e ->
                        button.setStyle(
                                primaryButtonStyle()
                        )
        );
    }


    private void styleSecondaryButton(
            Button button) {

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );


        button.setStyle(
                secondaryButtonStyle()
        );
    }


    private void styleDangerButton(
            Button button) {

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );


        button.setStyle(
                "-fx-background-color: #101e23;" +
                "-fx-text-fill: #ff9aa2;" +
                "-fx-border-color: #71313a;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-weight: bold;"
        );


        button.setOnMouseEntered(
                e ->
                        button.setStyle(
                                "-fx-background-color: #431d23;" +
                                "-fx-text-fill: #ffb6bd;" +
                                "-fx-border-color: #ff6978;" +
                                "-fx-border-radius: 9;" +
                                "-fx-background-radius: 9;" +
                                "-fx-font-weight: bold;"
                        )
        );


        button.setOnMouseExited(
                e ->
                        button.setStyle(
                                "-fx-background-color: #101e23;" +
                                "-fx-text-fill: #ff9aa2;" +
                                "-fx-border-color: #71313a;" +
                                "-fx-border-radius: 9;" +
                                "-fx-background-radius: 9;" +
                                "-fx-font-weight: bold;"
                        )
        );
    }


    private String primaryButtonStyle() {

        return
                "-fx-background-color: #15534b;" +
                "-fx-text-fill: #72ffdb;" +
                "-fx-border-color: #65ffd7;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-weight: bold;";
    }


    private String secondaryButtonStyle() {

        return
                "-fx-background-color: #091a20;" +
                "-fx-text-fill: #c8d8da;" +
                "-fx-border-color: #28535a;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-weight: bold;";
    }


    // =============================================================
    // CARD HOVER
    // =============================================================

    private void addCardHover(
            VBox card) {

        ScaleTransition grow =
                new ScaleTransition(
                        Duration.millis(140),
                        card
                );


        grow.setToX(
                1.008
        );

        grow.setToY(
                1.008
        );


        ScaleTransition shrink =
                new ScaleTransition(
                        Duration.millis(140),
                        card
                );


        shrink.setToX(
                1.0
        );

        shrink.setToY(
                1.0
        );


        card.setOnMouseEntered(
                e -> {

                    shrink.stop();

                    grow.playFromStart();
                }
        );


        card.setOnMouseExited(
                e -> {

                    grow.stop();

                    shrink.playFromStart();
                }
        );
    }


    // =============================================================
    // BACKGROUND
    // =============================================================

    private Pane createBackground() {

        Pane pane =
                new Pane();

        pane.setPrefSize(
                1300,
                800
        );

        pane.setMouseTransparent(
                true
        );


        Rectangle base =
                new Rectangle(
                        1300,
                        800
                );

        base.setFill(
                Color.web("#02090e")
        );


        pane.getChildren().add(
                base
        );


        // ---------------------------------------------------------
        // GRID
        // ---------------------------------------------------------

        for (int x = 0;
             x <= 1300;
             x += 55) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            800
                    );


            line.setStroke(
                    Color.rgb(
                            35,
                            180,
                            175,
                            0.055
                    )
            );


            pane.getChildren().add(
                    line
            );
        }


        for (int y = 0;
             y <= 800;
             y += 55) {

            Line line =
                    new Line(
                            0,
                            y,
                            1300,
                            y
                    );


            line.setStroke(
                    Color.rgb(
                            35,
                            180,
                            175,
                            0.055
                    )
            );


            pane.getChildren().add(
                    line
            );
        }


        // ---------------------------------------------------------
        // PARTICLES
        // ---------------------------------------------------------

        List<Circle> particles =
                new ArrayList<>();


        for (int i = 0;
             i < 35;
             i++) {

            Circle particle =
                    new Circle(
                            1 +
                            random.nextDouble() * 1.7
                    );


            particle.setFill(
                    i % 2 == 0
                            ? Color.CYAN
                            : Color.LIGHTGREEN
            );


            particle.setOpacity(
                    0.12 +
                    random.nextDouble() * 0.35
            );


            particle.setLayoutX(
                    random.nextDouble() * 1300
            );


            particle.setLayoutY(
                    random.nextDouble() * 800
            );


            pane.getChildren().add(
                    particle
            );


            particles.add(
                    particle
            );
        }


        for (Circle particle :
                particles) {

            TranslateTransition move =
                    new TranslateTransition(
                            Duration.seconds(
                                    4 +
                                    random.nextDouble() * 4
                            ),
                            particle
                    );


            move.setByY(
                    -20
            );


            move.setAutoReverse(
                    true
            );


            move.setCycleCount(
                    TranslateTransition.INDEFINITE
            );


            move.play();
        }


        return pane;
    }


    // =============================================================
    // ENTRANCE
    // =============================================================

    private void animateEntrance(
            javafx.scene.Node node,
            int delay) {

        node.setOpacity(
                0
        );

        node.setTranslateY(
                12
        );


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(450),
                        node
                );


        fade.setDelay(
                Duration.millis(delay)
        );


        fade.setFromValue(
                0
        );

        fade.setToValue(
                1
        );


        TranslateTransition slide =
                new TranslateTransition(
                        Duration.millis(450),
                        node
                );


        slide.setDelay(
                Duration.millis(delay)
        );


        slide.setFromY(
                12
        );

        slide.setToY(
                0
        );


        fade.play();

        slide.play();
    }
}