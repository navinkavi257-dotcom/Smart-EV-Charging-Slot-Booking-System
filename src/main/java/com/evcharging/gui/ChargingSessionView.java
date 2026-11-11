package com.evcharging.gui;

import com.evcharging.exception.BookingException;
import com.evcharging.model.Booking;
import com.evcharging.model.User;
import com.evcharging.model.Wallet;
import com.evcharging.service.BookingService;
import com.evcharging.service.WalletService;
import com.evcharging.util.DBConnection;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * SIMPLE LIVE EV CHARGING SESSION
 *
 * User-friendly charging screen.
 *
 * Booking flow:
 *
 * 1. Booking fee is paid during reservation.
 * 2. Charging starts.
 * 3. Actual energy consumption is simulated.
 * 4. Charging cost is calculated from actual kWh.
 * 5. When the user finishes charging,
 *    BookingService.completeCharging() performs
 *    the final wallet deduction.
 *
 * IMPORTANT:
 * ChargingSessionView does NOT directly deduct money.
 */
public class ChargingSessionView {

    private final User loggedInUser;
    private final int bookingId;

    private final BookingService bookingService =
            new BookingService();

    private final WalletService walletService =
            new WalletService();

    private Booking booking;

    private double chargerPower = 60.0;

    private double energyConsumed = 0.0;

    private boolean charging = false;
    private boolean completed = false;

    private Timeline chargingTimer;

    // =========================================================
    // MAIN UI LABELS
    // =========================================================

    private Label statusLabel;
    private Label energyLabel;
    private Label costLabel;
    private Label walletLabel;
    private Label timeLabel;

    private Label chargerLabel;
    private Label powerLabel;

    private Label messageLabel;

    // Receipt labels
    private Label receiptBookingFeeLabel;
    private Label receiptEnergyLabel;
    private Label receiptChargingCostLabel;
    private Label receiptTotalLabel;

    // Simple status indicator
    private Circle statusDot;

    // =========================================================
    // SESSION
    // =========================================================

    private long sessionSeconds = 0;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public ChargingSessionView(
            User loggedInUser,
            int bookingId) {

        this.loggedInUser = loggedInUser;
        this.bookingId = bookingId;
    }

    public ChargingSessionView(
            int bookingId,
            User loggedInUser) {

        this(loggedInUser, bookingId);
    }

    // =========================================================
    // SHOW
    // =========================================================

    public void show(Stage stage) {

        try {

            booking =
                    bookingService.getBookingById(
                            bookingId
                    );

            if (booking == null) {

                showError(
                        "Booking not found."
                );

                return;
            }

            if (booking.getUserId()
                    != loggedInUser.getUserId()) {

                showError(
                        "This booking does not belong " +
                        "to the logged-in user."
                );

                return;
            }

            if ("CANCELLED".equalsIgnoreCase(
                    booking.getStatus())) {

                showError(
                        "This booking has been cancelled."
                );

                return;
            }

            if ("COMPLETED".equalsIgnoreCase(
                    booking.getStatus())) {

                completed = true;

                energyConsumed =
                        booking.getEnergyConsumed();
            }

            loadChargerPower();

        } catch (BookingException e) {

            showError(
                    e.getMessage()
            );

            return;

        } catch (Exception e) {

            showError(
                    "Unable to load charging session: "
                    + e.getMessage()
            );

            return;
        }

        buildScene(stage);

        if (completed) {

            showCompletedState();

        } else if ("CHARGING".equalsIgnoreCase(
                booking.getStatus())) {

            energyConsumed =
                    booking.getEnergyConsumed();

            startSimulationTimer();

        } else {

            startBooking();
        }
    }

    // =========================================================
    // BUILD SCENE
    // =========================================================

    private void buildScene(Stage stage) {

        StackPane root =
                new StackPane();

        root.setStyle(
                "-fx-background-color: #02080D;"
        );

        // -----------------------------------------------------
        // SIMPLE DUST + SPARK BACKGROUND
        // -----------------------------------------------------

        createBackground(root);

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(20)
        );

        content.setMaxWidth(
                1320
        );

        // =====================================================
        // TOP BAR
        // =====================================================

        HBox topBar =
                new HBox(14);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        Button back =
                new Button(
                        "←  MY BOOKINGS"
                );

        back.setStyle(
                backButtonStyle()
        );

        back.setOnAction(e -> {

            stopTimer();

            new MyBookingsView(
                    loggedInUser
            ).show(stage);
        });

        VBox heading =
                new VBox(2);

        Label brand =
                label(
                        "⚡ SMART EV",
                        12,
                        Color.web("#5ff2c8")
                );

        brand.setStyle(
                "-fx-font-weight: bold;"
        );

        Label title =
                label(
                        "LIVE CHARGING",
                        25,
                        Color.WHITE
                );

        title.setStyle(
                "-fx-font-weight: bold;"
        );

        heading.getChildren().addAll(
                brand,
                title
        );

        HBox.setHgrow(
                heading,
                Priority.ALWAYS
        );

        statusDot =
                new Circle(
                        5,
                        Color.web("#ffcc66")
                );

        statusDot.setEffect(
                new Glow(0.7)
        );

        statusLabel =
                label(
                        "● CONNECTING",
                        11,
                        Color.web("#ffcc66")
                );

        statusLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        HBox statusBox =
                new HBox(
                        7,
                        statusDot,
                        statusLabel
                );

        statusBox.setAlignment(
                Pos.CENTER
        );

        topBar.getChildren().addAll(
                back,
                heading,
                statusBox
        );

        // =====================================================
        // SIMPLE INTRO
        // =====================================================

        VBox intro =
                new VBox(3);

        intro.setAlignment(
                Pos.CENTER
        );

        Label introTitle =
                label(
                        "Your vehicle is currently charging",
                        18,
                        Color.WHITE
                );

        introTitle.setStyle(
                "-fx-font-weight: bold;"
        );

        Label introSubtitle =
                label(
                        "Energy and cost are updated automatically",
                        10,
                        Color.web("#78969B")
                );

        intro.getChildren().addAll(
                introTitle,
                introSubtitle
        );

        // =====================================================
        // BOOKING INFORMATION
        // =====================================================

        HBox infoRow =
                new HBox(10);

        infoRow.setAlignment(
                Pos.CENTER
        );

        VBox bookingCard =
                createInfoCard(
                        "BOOKING",
                        "#" + bookingId,
                        Color.web("#5FE8FF")
                );

        VBox chargerCard =
                createInfoCard(
                        "CHARGER",
                        "CH-" + booking.getChargerId(),
                        Color.web("#5FF2C8")
                );

        powerLabel =
                label(
                        String.format(
                                "%.0f kW",
                                chargerPower
                        ),
                        15,
                        Color.web("#7DE3E8")
                );

        powerLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        VBox powerCard =
                createInfoCard(
                        "POWER",
                        String.format(
                                "%.0f kW",
                                chargerPower
                        ),
                        Color.web("#7DE3E8")
                );

        timeLabel =
                label(
                        "00:00",
                        15,
                        Color.WHITE
                );

        timeLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        VBox timeCard =
                createInfoCard(
                        "CHARGING TIME",
                        "00:00",
                        Color.WHITE
                );

        // Replace the generated value labels with
        // the actual live labels.
        timeCard.getChildren().set(
                1,
                timeLabel
        );

        infoRow.getChildren().addAll(
                bookingCard,
                chargerCard,
                powerCard,
                timeCard
        );

        for (javafx.scene.Node node :
                infoRow.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        // =====================================================
        // MAIN AREA
        // =====================================================

        HBox mainArea =
                new HBox(14);

        mainArea.setAlignment(
                Pos.TOP_CENTER
        );

        VBox chargingPanel =
                createChargingPanel();

        VBox paymentPanel =
                createPaymentPanel();

        chargingPanel.setPrefWidth(
                820
        );

        paymentPanel.setPrefWidth(
                420
        );

        HBox.setHgrow(
                chargingPanel,
                Priority.ALWAYS
        );

        mainArea.getChildren().addAll(
                chargingPanel,
                paymentPanel
        );

        // =====================================================
        // MESSAGE
        // =====================================================

        messageLabel =
                label(
                        "Preparing your charging session...",
                        10,
                        Color.web("#78969B")
                );

        messageLabel.setWrapText(
                true
        );

        messageLabel.setAlignment(
                Pos.CENTER
        );

        messageLabel.setMaxWidth(
                1100
        );

        content.getChildren().addAll(
                topBar,
                intro,
                infoRow,
                mainArea,
                messageLabel
        );

        root.getChildren().add(
                content
        );

        StackPane.setAlignment(
                content,
                Pos.TOP_CENTER
        );

        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(
                        root,
                        1280,
                        800
                );

        scene.setFill(
                Color.web("#02080D")
        );

        stage.setTitle(
                "Smart EV - Live Charging"
        );

        stage.setScene(
                scene
        );

        // Same stable visual-bounds sizing
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
    }

    // =========================================================
    // BACKGROUND
    // =========================================================

    private void createBackground(
            StackPane root) {

        ChargingBackground background =
                new ChargingBackground();

        javafx.scene.layout.Pane
                animatedBackground =
                background.create();

        animatedBackground.prefWidthProperty()
                .bind(
                        root.widthProperty()
                );

        animatedBackground.prefHeightProperty()
                .bind(
                        root.heightProperty()
                );

        root.getChildren().add(
                0,
                animatedBackground
        );
    }

    // =========================================================
    // CHARGING PANEL
    // =========================================================

    private VBox createChargingPanel() {

        VBox panel =
                new VBox(14);

        panel.setPadding(
                new Insets(20)
        );

        panel.setStyle(
                panelStyle()
        );

        // -----------------------------------------------------
        // PANEL TITLE
        // -----------------------------------------------------

        HBox panelHeader =
                new HBox();

        VBox heading =
                new VBox(2);

        Label title =
                label(
                        "CHARGING STATUS",
                        12,
                        Color.WHITE
                );

        title.setStyle(
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                label(
                        "Simple live charging information",
                        9,
                        Color.web("#78969B")
                );

        heading.getChildren().addAll(
                title,
                subtitle
        );

        HBox.setHgrow(
                heading,
                Priority.ALWAYS
        );

        Label live =
                label(
                        "LIVE",
                        9,
                        Color.web("#5FF2C8")
                );

        live.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-border-color: #23665B;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 5 10 5 10;"
        );

        panelHeader.getChildren().addAll(
                heading,
                live
        );

        // -----------------------------------------------------
        // SIMPLE CHARGING INDICATOR
        // -----------------------------------------------------

        StackPane chargingIndicator =
                new StackPane();

        chargingIndicator.setPrefHeight(
                205
        );

        VBox indicatorContent =
                new VBox(8);

        indicatorContent.setAlignment(
                Pos.CENTER
        );

        // Outer simple circle
        Circle outer =
                new Circle(
                        75
                );

        outer.setFill(
                Color.rgb(
                        10,
                        38,
                        39,
                        0.65
                )
        );

        outer.setStroke(
                Color.rgb(
                        95,
                        242,
                        200,
                        0.35
                )
        );

        outer.setStrokeWidth(
                2
        );

        // Inner circle
        Circle inner =
                new Circle(
                        58
                );

        inner.setFill(
                Color.rgb(
                        95,
                        242,
                        200,
                        0.07
                )
        );

        inner.setStroke(
                Color.rgb(
                        95,
                        242,
                        200,
                        0.55
                )
        );

        inner.setStrokeWidth(
                2
        );

        Label lightning =
                label(
                        "⚡",
                        38,
                        Color.WHITE
                );

        lightning.setEffect(
                new Glow(0.8)
        );

        Label chargingText =
                label(
                        "CHARGING NOW",
                        12,
                        Color.web("#5FF2C8")
                );

        chargingText.setStyle(
                "-fx-font-weight: bold;"
        );

        indicatorContent.getChildren().addAll(
                lightning,
                chargingText
        );

        chargingIndicator.getChildren().addAll(
                outer,
                inner,
                indicatorContent
        );

        // -----------------------------------------------------
        // SIMPLE PULSE
        // -----------------------------------------------------

        ScaleTransition pulse =
                new ScaleTransition(
                        Duration.seconds(1.5),
                        inner
                );

        pulse.setFromX(0.96);
        pulse.setFromY(0.96);

        pulse.setToX(1.04);
        pulse.setToY(1.04);

        pulse.setAutoReverse(
                true
        );

        pulse.setCycleCount(
                Animation.INDEFINITE
        );

        pulse.play();

        // -----------------------------------------------------
        // ENERGY USED
        // -----------------------------------------------------

        Label energyCaption =
                label(
                        "ENERGY USED",
                        9,
                        Color.web("#78969B")
                );

        energyCaption.setAlignment(
                Pos.CENTER
        );

        energyLabel =
                label(
                        "0.000 kWh",
                        34,
                        Color.WHITE
                );

        energyLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        VBox energyBox =
                new VBox(2);

        energyBox.setAlignment(
                Pos.CENTER
        );

        energyBox.setPadding(
                new Insets(13)
        );

        energyBox.setStyle(
                "-fx-background-color: rgba(5,22,28,0.88);" +
                "-fx-border-color: #16454C;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        energyBox.getChildren().addAll(
                energyCaption,
                energyLabel
        );

        // -----------------------------------------------------
        // COST
        // -----------------------------------------------------

        Label costCaption =
                label(
                        "CURRENT CHARGING COST",
                        9,
                        Color.web("#78969B")
                );

        costCaption.setAlignment(
                Pos.CENTER
        );

        costLabel =
                label(
                        "₹0.00",
                        27,
                        Color.web("#5FF2C8")
                );

        costLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        VBox costBox =
                new VBox(2);

        costBox.setAlignment(
                Pos.CENTER
        );

        costBox.setPadding(
                new Insets(13)
        );

        costBox.setStyle(
                "-fx-background-color: rgba(5,22,28,0.88);" +
                "-fx-border-color: #16454C;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        costBox.getChildren().addAll(
                costCaption,
                costLabel
        );

        // -----------------------------------------------------
        // VALUE ROW
        // -----------------------------------------------------

        HBox values =
                new HBox(10);

        HBox.setHgrow(
                energyBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                costBox,
                Priority.ALWAYS
        );

        values.getChildren().addAll(
                energyBox,
                costBox
        );

        // -----------------------------------------------------
        // SIMPLE PROGRESS BAR
        // -----------------------------------------------------

        Label progressTitle =
                label(
                        "ENERGY USED",
                        8,
                        Color.web("#78969B")
                );

        StackPane progressBar =
                new StackPane();

        progressBar.setPrefHeight(
                8
        );

        progressBar.setStyle(
                "-fx-background-color: #07151A;" +
                "-fx-background-radius: 5;"
        );

        javafx.scene.shape.Rectangle
                progressFill =
                new javafx.scene.shape.Rectangle(
                        0,
                        8
                );

        progressFill.setFill(
                Color.web("#5FF2C8")
        );

        progressFill.setArcWidth(
                8
        );

        progressFill.setArcHeight(
                8
        );
        progressFill.setId("chargingProgress");

        progressBar.getChildren().add(
                progressFill
        );

        // Store progress fill for updates
        progressBar.setUserData(
                progressFill
        );

        // -----------------------------------------------------
        // FINISH BUTTON
        // -----------------------------------------------------

        Button finish =
                new Button(
                        "✓  FINISH CHARGING"
                );

        finish.setPrefHeight(
                46
        );

        finish.setMaxWidth(
                Double.MAX_VALUE
        );

        finish.setStyle(
                finishButtonStyle()
        );

        finish.setOnAction(
                e -> finishCharging()
        );

        panel.getChildren().addAll(
                panelHeader,
                chargingIndicator,
                values,
                progressTitle,
                progressBar,
                finish
        );

        // Store progress bar reference
        panel.setUserData(
                progressBar
        );

        return panel;
    }

    // =========================================================
    // PAYMENT / RECEIPT PANEL
    // =========================================================

    private VBox createPaymentPanel() {

        VBox panel =
                new VBox(12);

        panel.setPadding(
                new Insets(20)
        );

        panel.setStyle(
                panelStyle()
        );

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        Label title =
                label(
                        "PAYMENT SUMMARY",
                        12,
                        Color.WHITE
                );

        title.setStyle(
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                label(
                        "Your wallet and charging cost",
                        9,
                        Color.web("#78969B")
                );

        // -----------------------------------------------------
        // WALLET
        // -----------------------------------------------------

        VBox walletBox =
                new VBox(4);

        walletBox.setPadding(
                new Insets(15)
        );

        walletBox.setStyle(
                "-fx-background-color: rgba(7,29,31,0.90);" +
                "-fx-border-color: #23665B;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        Label walletTitle =
                label(
                        "WALLET BALANCE",
                        8,
                        Color.web("#78969B")
                );

        walletLabel =
                label(
                        "₹0.00",
                        25,
                        Color.web("#5FF2C8")
                );

        walletLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        walletBox.getChildren().addAll(
                walletTitle,
                walletLabel
        );

        // -----------------------------------------------------
        // BILL
        // -----------------------------------------------------

        VBox billBox =
                new VBox(10);

        billBox.setPadding(
                new Insets(15)
        );

        billBox.setStyle(
                "-fx-background-color: rgba(3,15,20,0.92);" +
                "-fx-border-color: #16454C;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        Label billTitle =
                label(
                        "SESSION RECEIPT",
                        9,
                        Color.WHITE
                );

        receiptBookingFeeLabel =
                billRow(
                        "Booking Fee",
                        String.format(
                                "₹%.2f",
                                bookingService
                                        .getBookingFee()
                        )
                );

        receiptEnergyLabel =
                billRow(
                        "Energy Used",
                        "0.000 kWh"
                );

        receiptChargingCostLabel =
                billRow(
                        "Charging Cost",
                        "₹0.00"
                );

        Label separator =
                label(
                        "────────────────────",
                        7,
                        Color.web("#214047")
                );

        receiptTotalLabel =
                billRow(
                        "TOTAL",
                        "₹0.00"
                );

        receiptTotalLabel.setTextFill(
                Color.WHITE
        );

        receiptTotalLabel.setFont(
                Font.font(
                        "Arial",
                        javafx.scene.text.FontWeight.BOLD,
                        19
                )
        );

        billBox.getChildren().addAll(
                billTitle,
                receiptBookingFeeLabel,
                receiptEnergyLabel,
                receiptChargingCostLabel,
                separator,
                receiptTotalLabel
        );

        // -----------------------------------------------------
        // HOW PAYMENT WORKS
        // -----------------------------------------------------

        VBox explanation =
                new VBox(6);

        explanation.setPadding(
                new Insets(14)
        );

        explanation.setStyle(
                "-fx-background-color: rgba(5,20,26,0.75);" +
                "-fx-border-color: #123B42;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        Label explanationTitle =
                label(
                        "HOW IT WORKS",
                        9,
                        Color.web("#5FF2C8")
                );

        explanationTitle.setStyle(
                "-fx-font-weight: bold;"
        );

        Label line1 =
                label(
                        "• Booking fee is already paid.",
                        9,
                        Color.web("#91A9AE")
                );

        Label line2 =
                label(
                        "• Charging cost depends on actual energy used.",
                        9,
                        Color.web("#91A9AE")
                );

        Label line3 =
                label(
                        "• Final charging cost is deducted when you finish.",
                        9,
                        Color.web("#91A9AE")
                );

        line1.setWrapText(true);
        line2.setWrapText(true);
        line3.setWrapText(true);

        explanation.getChildren().addAll(
                explanationTitle,
                line1,
                line2,
                line3
        );

        // -----------------------------------------------------
        // CHARGER INFORMATION
        // -----------------------------------------------------

        VBox chargerInfo =
                new VBox(5);

        chargerInfo.setPadding(
                new Insets(14)
        );

        chargerInfo.setStyle(
                "-fx-background-color: rgba(5,20,26,0.75);" +
                "-fx-border-color: #123B42;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        Label chargerTitle =
                label(
                        "YOUR CHARGER",
                        8,
                        Color.web("#78969B")
                );

        Label chargerValue =
                label(
                        "Charger #" +
                        booking.getChargerId(),
                        16,
                        Color.WHITE
                );

        chargerValue.setStyle(
                "-fx-font-weight: bold;"
        );

        Label chargerPowerText =
                label(
                        String.format(
                                "%.0f kW charging power",
                                this.chargerPower
                        ),
                        9,
                        Color.web("#5FF2C8")
                );

        chargerInfo.getChildren().addAll(
                chargerTitle,
                chargerValue,
                chargerPowerText
        );

        panel.getChildren().addAll(
                title,
                subtitle,
                walletBox,
                billBox,
                explanation,
                chargerInfo
        );

        loadWalletLabel();

        return panel;
    }

    // =========================================================
    // INFO CARD
    // =========================================================

    private VBox createInfoCard(
            String title,
            String value,
            Color color) {

        Label titleLabel =
                label(
                        title,
                        7,
                        Color.web("#718C92")
                );

        Label valueLabel =
                label(
                        value,
                        15,
                        color
                );

        valueLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        VBox box =
                new VBox(
                        4,
                        titleLabel,
                        valueLabel
                );

        box.setPadding(
                new Insets(10)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setStyle(
                "-fx-background-color: rgba(5,20,26,0.90);" +
                "-fx-border-color: #143D44;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;"
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        return box;
    }

    // =========================================================
    // BILL ROW
    // =========================================================

    private Label billRow(
            String title,
            String value) {

        Label row =
                label(
                        title + "     " + value,
                        10,
                        Color.web("#91A9AE")
                );

        return row;
    }

    // =========================================================
    // START BOOKING
    // =========================================================

    private void startBooking() {

        try {

            booking =
                    bookingService.startCharging(
                            bookingId
                    );

            charging = true;

            setChargingStatus();

            messageLabel.setText(
                    "Charging started. " +
                    "Your energy usage and cost are now updating."
            );

            messageLabel.setTextFill(
                    Color.web("#78969B")
            );

            startSimulationTimer();

        } catch (BookingException e) {

            showError(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // TIMER
    // =========================================================

    private void startSimulationTimer() {

        charging = true;

        setChargingStatus();

        if (chargingTimer != null) {
            chargingTimer.stop();
        }

        chargingTimer =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(1),
                                e -> updateEnergy()
                        )
                );

        chargingTimer.setCycleCount(
                Animation.INDEFINITE
        );

        chargingTimer.play();
    }

    // =========================================================
    // UPDATE ENERGY
    // =========================================================

    private void updateEnergy() {

        if (!charging || completed) {
            return;
        }

        /*
         * Existing simulation is preserved:
         *
         * 1 real second
         * =
         * 10 charging seconds.
         */

        double simulatedSeconds =
                10.0;

        energyConsumed +=
                chargerPower *
                simulatedSeconds /
                3600.0;

        energyConsumed =
                Math.round(
                        energyConsumed * 1000.0
                ) / 1000.0;

        sessionSeconds++;

        double chargingCost =
                bookingService.calculateChargingCost(
                        energyConsumed
                );

        // -----------------------------------------------------
        // UPDATE MAIN VALUES
        // -----------------------------------------------------

        energyLabel.setText(
                String.format(
                        "%.3f kWh",
                        energyConsumed
                )
        );

        costLabel.setText(
                String.format(
                        "₹%.2f",
                        chargingCost
                )
        );

        timeLabel.setText(
                formatTime(
                        sessionSeconds
                )
        );

        // -----------------------------------------------------
        // UPDATE PROGRESS
        // -----------------------------------------------------

        updateProgress();

        // -----------------------------------------------------
        // UPDATE RECEIPT
        // -----------------------------------------------------

        updateReceipt();

        // -----------------------------------------------------
        // UPDATE WALLET DISPLAY
        // -----------------------------------------------------

        loadWalletLabel();
    }

    // =========================================================
    // PROGRESS
    // =========================================================

    private void updateProgress() {

        if (energyLabel == null) {
            return;
        }

        javafx.scene.Node node =
                energyLabel
                        .getScene()
                        .lookup(
                                "#chargingProgress"
                        );

        if (node instanceof javafx.scene.shape.Rectangle) {

            javafx.scene.shape.Rectangle
                    fill =
                    (javafx.scene.shape.Rectangle)
                            node;

            double progress =
                    Math.min(
                            1.0,
                            energyConsumed / 20.0
                    );

            fill.setWidth(
                    500 * progress
            );
        }
    }

    // =========================================================
    // FINISH CHARGING
    // =========================================================

    private void finishCharging() {

        if (completed) {
            return;
        }

        if (!charging) {

            showError(
                    "Charging is not currently running."
            );

            return;
        }

        if (energyConsumed <= 0) {

            showError(
                    "Please allow the charging session " +
                    "to consume some energy before finishing."
            );

            return;
        }

        double currentCost =
                bookingService.calculateChargingCost(
                        energyConsumed
                );

        Alert confirm =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirm.setTitle(
                "Finish Charging"
        );

        confirm.setHeaderText(
                "Finish your charging session?"
        );

        confirm.setContentText(
                String.format(
                        "Energy Used: %.3f kWh\n" +
                        "Charging Cost: ₹%.2f\n\n" +
                        "The charging cost will be " +
                        "deducted from your wallet.",
                        energyConsumed,
                        currentCost
                )
        );

        confirm.showAndWait()
                .ifPresent(result -> {

                    if (result == ButtonType.OK) {

                        completeSession();
                    }
                });
    }

    // =========================================================
    // COMPLETE SESSION
    // =========================================================

    private void completeSession() {

        stopTimer();

        try {

            /*
             * IMPORTANT:
             *
             * BookingService handles the wallet deduction.
             *
             * We do NOT deduct wallet money here.
             */

            Booking completedBooking =
                    bookingService.completeCharging(
                            bookingId,
                            energyConsumed
                    );

            booking =
                    completedBooking;

            completed = true;

            charging = false;

            energyConsumed =
                    completedBooking.getEnergyConsumed();

            statusLabel.setText(
                    "● CHARGING COMPLETE"
            );

            statusLabel.setTextFill(
                    Color.web("#5FF2C8")
            );

            statusDot.setFill(
                    Color.web("#5FF2C8")
            );

            messageLabel.setText(
                    String.format(
                            "✓ Charging completed successfully. " +
                            "%.3f kWh used • ₹%.2f charged.",
                            completedBooking
                                    .getEnergyConsumed(),
                            completedBooking
                                    .getChargingCost()
                    )
            );

            messageLabel.setTextFill(
                    Color.web("#5FF2C8")
            );

            updateReceipt();

            loadWalletLabel();

        } catch (BookingException e) {

            /*
             * If wallet balance is insufficient,
             * BookingService rolls the transaction back.
             *
             * Charging remains active.
             */

            charging = true;

            statusLabel.setText(
                    "● PAYMENT REQUIRED"
            );

            statusLabel.setTextFill(
                    Color.web("#FF6978")
            );

            statusDot.setFill(
                    Color.web("#FF6978")
            );

            messageLabel.setText(
                    e.getMessage()
            );

            messageLabel.setTextFill(
                    Color.web("#FF6978")
            );

            if (chargingTimer == null) {

                startSimulationTimer();
            }
        }
    }

    // =========================================================
    // RECEIPT
    // =========================================================

    private void updateReceipt() {

        if (booking == null) {
            return;
        }

        double finalChargingCost =
                completed
                        ? booking.getChargingCost()
                        : bookingService.calculateChargingCost(
                                energyConsumed
                        );

        double total =
                booking.getBookingFee()
                + finalChargingCost;

        if (receiptBookingFeeLabel != null) {

            receiptBookingFeeLabel.setText(
                    String.format(
                            "Booking Fee     ₹%.2f",
                            booking.getBookingFee()
                    )
            );
        }

        if (receiptEnergyLabel != null) {

            receiptEnergyLabel.setText(
                    String.format(
                            "Energy Used     %.3f kWh",
                            energyConsumed
                    )
            );
        }

        if (receiptChargingCostLabel != null) {

            receiptChargingCostLabel.setText(
                    String.format(
                            "Charging Cost    ₹%.2f",
                            finalChargingCost
                    )
            );
        }

        if (receiptTotalLabel != null) {

            receiptTotalLabel.setText(
                    String.format(
                            "TOTAL            ₹%.2f",
                            total
                    )
            );
        }
    }

    // =========================================================
    // COMPLETED STATE
    // =========================================================

    private void showCompletedState() {

        charging = false;

        statusLabel.setText(
                "● CHARGING COMPLETE"
        );

        statusLabel.setTextFill(
                Color.web("#5FF2C8")
        );

        statusDot.setFill(
                Color.web("#5FF2C8")
        );

        messageLabel.setText(
                "✓ This charging session has already been completed."
        );

        messageLabel.setTextFill(
                Color.web("#5FF2C8")
        );

        energyLabel.setText(
                String.format(
                        "%.3f kWh",
                        energyConsumed
                )
        );

        costLabel.setText(
                String.format(
                        "₹%.2f",
                        booking.getChargingCost()
                )
        );

        updateReceipt();

        loadWalletLabel();
    }

    // =========================================================
    // WALLET
    // =========================================================

    private void loadWalletLabel() {

        if (walletLabel == null) {
            return;
        }

        try {

            walletService.createWallet(
                    loggedInUser.getUserId()
            );

            Wallet wallet =
                    walletService.getWallet(
                            loggedInUser.getUserId()
                    );

            if (wallet == null) {

                walletLabel.setText(
                        "Wallet unavailable"
                );

            } else {

                walletLabel.setText(
                        String.format(
                                "₹%.2f",
                                wallet.getBalance()
                        )
                );
            }

        } catch (Exception e) {

            walletLabel.setText(
                    "Wallet unavailable"
            );
        }
    }

    // =========================================================
    // LOAD CHARGER POWER
    // =========================================================

    private void loadChargerPower()
            throws Exception {

        String sql =
                "SELECT power FROM chargers " +
                "WHERE charger_id = ? " +
                "AND station_id = ?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    booking.getChargerId()
            );

            ps.setInt(
                    2,
                    booking.getStationId()
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    chargerPower =
                            rs.getDouble(
                                    "power"
                            );
                }
            }
        }
    }

    // =========================================================
    // STATUS
    // =========================================================

    private void setChargingStatus() {

        if (statusLabel == null) {
            return;
        }

        statusLabel.setText(
                "● CHARGING LIVE"
        );

        statusLabel.setTextFill(
                Color.web("#5FF2C8")
        );

        if (statusDot != null) {

            statusDot.setFill(
                    Color.web("#5FF2C8")
            );
        }
    }

    // =========================================================
    // TIME
    // =========================================================

    private String formatTime(
            long seconds) {

        long minutes =
                seconds / 60;

        long secs =
                seconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                secs
        );
    }

    // =========================================================
    // STOP TIMER
    // =========================================================

    private void stopTimer() {

        charging = false;

        if (chargingTimer != null) {

            chargingTimer.stop();

            chargingTimer = null;
        }
    }

    // =========================================================
    // LABEL HELPER
    // =========================================================

    private Label label(
            String text,
            double size,
            Color color) {

        Label label =
                new Label(text);

        label.setFont(
                javafx.scene.text.Font.font(
                        "Arial",
                        javafx.scene.text.FontWeight.NORMAL,
                        size
                )
        );

        label.setTextFill(
                color
        );

        return label;
    }

    // =========================================================
    // PANEL STYLE
    // =========================================================

    private String panelStyle() {

        return
                "-fx-background-color: rgba(5,18,24,0.94);" +
                "-fx-border-color: #16454C;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;";
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================

    private String backButtonStyle() {

        return
                "-fx-background-color: #07151B;" +
                "-fx-text-fill: #D9F7F5;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #204850;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9 14 9 14;";
    }

    // =========================================================
    // FINISH BUTTON
    // =========================================================

    private String finishButtonStyle() {

        return
                "-fx-background-color: #103C39;" +
                "-fx-text-fill: #5FF2C8;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #2ED9B6;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-size: 12px;";
    }

    // =========================================================
    // ERROR
    // =========================================================

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
                "Charging Session"
        );

        alert.setContentText(
                message == null
                        ? "Unknown error."
                        : message
        );

        alert.showAndWait();
    }
}