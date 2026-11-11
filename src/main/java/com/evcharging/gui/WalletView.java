package com.evcharging.gui;

import com.evcharging.model.User;
import com.evcharging.model.Wallet;
import com.evcharging.service.WalletService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class WalletView {

    // =========================================================
    // USER / SERVICE
    // =========================================================

    private final User loggedInUser;

    private final WalletService walletService =
            new WalletService();

    private final Random random =
            new Random(42);


    // =========================================================
    // UI FIELDS
    // =========================================================

    private Label balanceLabel;
    private Label statusLabel;

    private TextField amountField;

    private VBox balanceCard;

    private StackPane walletCenter;

    private StackPane qrOverlay;


    // =========================================================
    // SHOW WALLET
    // =========================================================

    public void show(Stage stage) {

        // -----------------------------------------------------
        // MAKE SURE WALLET EXISTS
        // -----------------------------------------------------

        walletService.createWallet(
                loggedInUser.getUserId()
        );


        // -----------------------------------------------------
        // BASE
        // -----------------------------------------------------

        BorderPane base =
                new BorderPane();

        base.setStyle(
                "-fx-background-color: #02090E;"
        );


        // -----------------------------------------------------
        // BACKGROUND
        // -----------------------------------------------------

        Pane background =
                createBackground();

        StackPane center =
                new StackPane();

        walletCenter = center;

        center.getChildren().add(
                background
        );


        // -----------------------------------------------------
        // MAIN CONTENT
        // -----------------------------------------------------

        VBox content =
                new VBox(18);

        content.setPadding(
                new Insets(
                        24,
                        30,
                        28,
                        30
                )
        );


        // -----------------------------------------------------
        // TOP BAR
        // -----------------------------------------------------

        HBox topBar =
                createTopBar(stage);


        // -----------------------------------------------------
        // HEADING
        // -----------------------------------------------------

        VBox heading =
                createHeading();


        // -----------------------------------------------------
        // MAIN AREA
        // -----------------------------------------------------

        HBox mainArea =
                new HBox(18);

        mainArea.setAlignment(
                Pos.TOP_CENTER
        );


        // -----------------------------------------------------
        // BALANCE CARD
        // -----------------------------------------------------

        balanceCard =
                createBalanceCard();


        // -----------------------------------------------------
        // ADD MONEY CARD
        // -----------------------------------------------------

        VBox rightPanel =
                createAddMoneyCard();


        mainArea.getChildren().addAll(
                balanceCard,
                rightPanel
        );


        // -----------------------------------------------------
        // ACTIVITY
        // -----------------------------------------------------

        VBox activity =
                createActivityPanel();


        // -----------------------------------------------------
        // ADD TO CONTENT
        // -----------------------------------------------------

        content.getChildren().addAll(
                topBar,
                heading,
                mainArea,
                activity
        );


        center.getChildren().add(
                content
        );


        base.setCenter(center);


        // -----------------------------------------------------
        // SCENE
        // -----------------------------------------------------

        Scene scene =
                new Scene(
                        base,
                        1250,
                        800
                );


        // -----------------------------------------------------
        // STAGE
        // -----------------------------------------------------

        Rectangle2D bounds =
                Screen.getPrimary()
                        .getVisualBounds();

        stage.setScene(scene);

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


        // -----------------------------------------------------
        // LOAD BALANCE
        // -----------------------------------------------------

        refreshBalance();


        // -----------------------------------------------------
        // ENTRANCE ANIMATION
        // -----------------------------------------------------

        animateEntrance(
                topBar,
                heading,
                mainArea,
                activity
        );
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public WalletView(User loggedInUser) {

        this.loggedInUser =
                loggedInUser;
    }


    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar(Stage stage) {

        HBox bar =
                new HBox(18);

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(
                        12,
                        16,
                        12,
                        16
                )
        );

        bar.setStyle(
                "-fx-background-color: rgba(7,20,26,0.92);"
                        + "-fx-border-color: #18373D;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-radius: 12;"
        );


        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        Button backButton =
                new Button(
                        "←  DASHBOARD"
                );

        backButton.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        backButton.setTextFill(
                Color.web("#8BA7AE")
        );

        backButton.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: #29444A;"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-cursor: hand;"
        );

         backButton.setOnAction(e -> {
        new DashboardView(
                        loggedInUser
                         ).show(stage);
                });

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        Label logo =
                new Label(
                        "⚡ SMART EV"
                );

        logo.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        16
                )
        );

        logo.setTextFill(
                Color.web("#5FF2C8")
        );


        // -----------------------------------------------------
        // ONLINE
        // -----------------------------------------------------

        Label online =
                new Label(
                        "● WALLET ONLINE"
                );

        online.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        online.setTextFill(
                Color.web("#5FF2C8")
        );


        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        Label title =
                new Label(
                        "FINANCIAL CONTROL"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        title.setTextFill(
                Color.web("#607C84")
        );


        bar.getChildren().addAll(
                backButton,
                logo,
                online,
                spacer,
                title
        );

        return bar;
    }


    // =========================================================
    // HEADING
    // =========================================================

    private VBox createHeading() {

        VBox box =
                new VBox(4);

        Label title =
                new Label(
                        "MY WALLET"
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
                        "DIGITAL FUNDS  •  RESERVATION FEES  •  ENERGY PAYMENTS"
                );

        subtitle.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        10
                )
        );

        subtitle.setTextFill(
                Color.web("#6D8991")
        );


        box.getChildren().addAll(
                title,
                subtitle
        );

        return box;
    }


    // =========================================================
    // BALANCE CARD
    // =========================================================

    private VBox createBalanceCard() {

        VBox card =
                new VBox(12);

        card.setPrefWidth(560);

        card.setMinHeight(280);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right,"
                        + "#102D32,"
                        + "#07151A"
                        + ");"
                        + "-fx-border-color: #20D6C7;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 16;"
                        + "-fx-border-radius: 16;"
        );


        // -----------------------------------------------------
        // SMALL TITLE
        // -----------------------------------------------------

        Label core =
                new Label(
                        "WALLET CORE"
                );

        core.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        core.setTextFill(
                Color.web("#5FF2C8")
        );


        // -----------------------------------------------------
        // AVAILABLE
        // -----------------------------------------------------

        Label available =
                new Label(
                        "AVAILABLE BALANCE"
                );

        available.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        available.setTextFill(
                Color.web("#78959C")
        );


        // -----------------------------------------------------
        // BALANCE
        // -----------------------------------------------------

        balanceLabel =
                new Label(
                        "₹0.00"
                );

        balanceLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        46
                )
        );

        balanceLabel.setTextFill(
                Color.web("#FFFFFF")
        );


        // -----------------------------------------------------
        // SEPARATOR
        // -----------------------------------------------------

        Region separator =
                new Region();

        separator.setPrefHeight(1);

        separator.setMaxWidth(
                Double.MAX_VALUE
        );

        separator.setStyle(
                "-fx-background-color: #1D4248;"
        );


        // -----------------------------------------------------
        // INFO
        // -----------------------------------------------------

        Label info =
                new Label(
                        "Funds available for slot reservations "
                                + "and actual EV energy consumption."
                );

        info.setWrapText(true);

        info.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        11
                )
        );

        info.setTextFill(
                Color.web("#78959C")
        );


        // -----------------------------------------------------
        // ENERGY RATE
        // -----------------------------------------------------

        Label rate =
                new Label(
                        "⚡ ENERGY RATE   ₹20.00 / kWh"
                );

        rate.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        rate.setTextFill(
                Color.web("#5FF2C8")
        );


        card.getChildren().addAll(
                core,
                available,
                balanceLabel,
                separator,
                info,
                rate
        );

        return card;
    }


    // =========================================================
    // ADD MONEY CARD
    // =========================================================

    private VBox createAddMoneyCard() {

        VBox card =
                new VBox(12);

        card.setPrefWidth(440);

        card.setMinHeight(280);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle(
                "-fx-background-color: rgba(9,23,29,0.95);"
                        + "-fx-border-color: #29484F;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 16;"
                        + "-fx-border-radius: 16;"
        );


        // -----------------------------------------------------
        // SMALL TITLE
        // -----------------------------------------------------

        Label command =
                new Label(
                        "WALLET COMMAND"
                );

        command.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        command.setTextFill(
                Color.web("#55DFFF")
        );


        // -----------------------------------------------------
        // HEADING
        // -----------------------------------------------------

        Label heading =
                new Label(
                        "ADD FUNDS"
                );

        heading.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        22
                )
        );

        heading.setTextFill(
                Color.WHITE
        );


        // -----------------------------------------------------
        // DESCRIPTION
        // -----------------------------------------------------

        Label description =
                new Label(
                        "Enter an amount and pay securely "
                                + "using a scannable QR code."
                );

        description.setWrapText(true);

        description.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        10
                )
        );

        description.setTextFill(
                Color.web("#78959C")
        );


        // -----------------------------------------------------
        // AMOUNT FIELD
        // -----------------------------------------------------

        amountField =
                new TextField();

        amountField.setPromptText(
                "Enter amount  ₹"
        );

        amountField.setPrefHeight(42);

        amountField.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        13
                )
        );

        amountField.setStyle(
                "-fx-background-color: #061217;"
                        + "-fx-text-fill: white;"
                        + "-fx-prompt-text-fill: #547078;"
                        + "-fx-border-color: #23444A;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 9;"
                        + "-fx-border-radius: 9;"
        );


        // -----------------------------------------------------
        // ADD BUTTON
        // -----------------------------------------------------

        Button addButton =
                new Button(
                        "＋  ADD MONEY"
                );

        addButton.setMaxWidth(
                Double.MAX_VALUE
        );

        addButton.setPrefHeight(42);

        addButton.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        addButton.setTextFill(
                Color.web("#00100D")
        );

        addButton.setStyle(
                "-fx-background-color: #5FF2C8;"
                        + "-fx-background-radius: 9;"
                        + "-fx-cursor: hand;"
        );


        addButton.setOnMouseEntered(e ->
                addButton.setStyle(
                        "-fx-background-color: #8FFFF0;"
                                + "-fx-background-radius: 9;"
                                + "-fx-cursor: hand;"
                )
        );


        addButton.setOnMouseExited(e ->
                addButton.setStyle(
                        "-fx-background-color: #5FF2C8;"
                                + "-fx-background-radius: 9;"
                                + "-fx-cursor: hand;"
                )
        );


        // -----------------------------------------------------
        // IMPORTANT:
        // QR PAYMENT STARTS HERE
        // -----------------------------------------------------

        addButton.setOnAction(
                e -> startQrPayment()
        );


        // -----------------------------------------------------
        // QUICK AMOUNTS
        // -----------------------------------------------------

        HBox quickAmounts =
                new HBox(8);

        quickAmounts.setAlignment(
                Pos.CENTER_LEFT
        );


        Button fiveHundred =
                quickAmountButton("₹500");

        Button thousand =
                quickAmountButton("₹1000");

        Button twoThousand =
                quickAmountButton("₹2000");


        fiveHundred.setOnAction(
                e -> amountField.setText("500")
        );

        thousand.setOnAction(
                e -> amountField.setText("1000")
        );

        twoThousand.setOnAction(
                e -> amountField.setText("2000")
        );


        quickAmounts.getChildren().addAll(
                fiveHundred,
                thousand,
                twoThousand
        );


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        statusLabel =
                new Label(
                        "READY FOR WALLET TRANSACTION"
                );

        statusLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        statusLabel.setTextFill(
                Color.web("#8BA7AE")
        );


        card.getChildren().addAll(
                command,
                heading,
                description,
                amountField,
                addButton,
                quickAmounts,
                statusLabel
        );

        return card;
    }


    // =========================================================
    // QUICK AMOUNT BUTTON
    // =========================================================

    private Button quickAmountButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(30);

        button.setPadding(
                new Insets(
                        4,
                        14,
                        4,
                        14
                )
        );

        button.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        button.setTextFill(
                Color.web("#7EA0A8")
        );

        button.setStyle(
                "-fx-background-color: #09191E;"
                        + "-fx-border-color: #244149;"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-cursor: hand;"
        );

        return button;
    }


    // =========================================================
    // ACTIVITY PANEL
    // =========================================================

    private VBox createActivityPanel() {

        VBox panel =
                new VBox(10);

        panel.setPadding(
                new Insets(18)
        );

        panel.setStyle(
                "-fx-background-color: rgba(6,17,22,0.90);"
                        + "-fx-border-color: #17353B;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 14;"
                        + "-fx-border-radius: 14;"
        );


        Label title =
                new Label(
                        "WALLET TRANSACTION LOG"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        title.setTextFill(
                Color.web("#55DFFF")
        );


        VBox items =
                new VBox(8);


        items.getChildren().addAll(

                activityItem(
                        "₹30",
                        "BOOKING FEE",
                        "Deducted when slot is reserved"
                ),

                activityItem(
                        "₹20/kWh",
                        "ENERGY RATE",
                        "Actual electricity calculation"
                ),

                activityItem(
                        "₹30",
                        "CANCELLATION REFUND",
                        "Returned to wallet when eligible"
                )
        );


        panel.getChildren().addAll(
                title,
                items
        );

        return panel;
    }


    // =========================================================
    // ACTIVITY ITEM
    // =========================================================

    private HBox activityItem(
            String value,
            String title,
            String description
    ) {

        HBox row =
                new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );

        row.setStyle(
                "-fx-background-color: #071318;"
                        + "-fx-background-radius: 8;"
        );


        Label valueLabel =
                new Label(value);

        valueLabel.setMinWidth(80);

        valueLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        valueLabel.setTextFill(
                Color.web("#5FF2C8")
        );


        VBox details =
                new VBox(2);


        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        titleLabel.setTextFill(
                Color.WHITE
        );


        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        9
                )
        );

        descriptionLabel.setTextFill(
                Color.web("#668189")
        );


        details.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );


        row.getChildren().addAll(
                valueLabel,
                details
        );

        return row;
    }


    // =========================================================
    // REFRESH BALANCE
    // =========================================================

    private void refreshBalance() {

        Wallet wallet =
                walletService.getWallet(
                        loggedInUser.getUserId()
                );

        if (wallet == null) {

            balanceLabel.setText(
                    "₹0.00"
            );

            return;
        }


        balanceLabel.setText(
                String.format(
                        "₹%.2f",
                        wallet.getBalance()
                )
        );
    }


    // =========================================================
    // BALANCE ANIMATION
    // =========================================================

    private void animateBalance() {

        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(250),
                        balanceLabel
                );

        scale.setFromX(1.0);
        scale.setFromY(1.0);

        scale.setToX(1.08);
        scale.setToY(1.08);

        scale.setAutoReverse(true);

        scale.setCycleCount(2);

        scale.play();
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        if (statusLabel == null) {
            return;
        }

        statusLabel.setText(
                "✕ " + message
        );

        statusLabel.setTextFill(
                Color.web("#FF6B7A")
        );
    }


    // =========================================================
    // BACKGROUND
    // =========================================================

    private Pane createBackground() {

        Pane pane =
                new Pane();

        pane.setPrefSize(
                1300,
                800
        );

        pane.setStyle(
                "-fx-background-color: #02090E;"
        );

        pane.setMouseTransparent(
                true
        );


        // -----------------------------------------------------
        // GRID
        // -----------------------------------------------------

        for (
                int x = 0;
                x <= 1300;
                x += 55
        ) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            800
                    );

            line.setStroke(
                    Color.rgb(
                            70,
                            220,
                            230,
                            0.035
                    )
            );

            line.setStrokeWidth(1);

            pane.getChildren().add(
                    line
            );
        }


        for (
                int y = 0;
                y <= 800;
                y += 55
        ) {

            Line line =
                    new Line(
                            0,
                            y,
                            1300,
                            y
                    );

            line.setStroke(
                    Color.rgb(
                            70,
                            220,
                            230,
                            0.035
                    )
            );

            line.setStrokeWidth(1);

            pane.getChildren().add(
                    line
            );
        }


        // -----------------------------------------------------
        // PARTICLES
        // -----------------------------------------------------

        for (
                int i = 0;
                i < 30;
                i++
        ) {

            double x =
                    random.nextDouble()
                            * 1300;

            double y =
                    random.nextDouble()
                            * 800;


            Circle particle =
                    new Circle(
                            0.7
                    );


            if (random.nextBoolean()) {

                particle.setFill(
                        Color.rgb(
                                95,
                                242,
                                200,
                                0.20
                                        + random.nextDouble()
                                        * 0.25
                        )
                );

            } else {

                particle.setFill(
                        Color.rgb(
                                85,
                                223,
                                255,
                                0.15
                                        + random.nextDouble()
                                        * 0.20
                        )
                );
            }


            particle.setTranslateX(x);
            particle.setTranslateY(y);


            pane.getChildren().add(
                    particle
            );


            TranslateTransition move =
                    new TranslateTransition(
                            Duration.seconds(
                                    5
                                            + random.nextDouble()
                                            * 6
                            ),
                            particle
                    );


            move.setFromY(y);

            move.setToY(
                    y - 40
            );

            move.setAutoReverse(true);

            move.setCycleCount(
                    Animation.INDEFINITE
            );

            move.setDelay(
                    Duration.seconds(
                            random.nextDouble()
                                    * 4
                    )
            );

            move.play();
        }


        return pane;
    }


    // =========================================================
    // ENTRANCE ANIMATION
    // =========================================================

    private void animateEntrance(
            HBox topBar,
            VBox heading,
            HBox mainArea,
            VBox activity
    ) {

        fadeSlide(
                topBar,
                0
        );

        fadeSlide(
                heading,
                100
        );

        fadeSlide(
                mainArea,
                200
        );

        fadeSlide(
                activity,
                300
        );
    }


    private void fadeSlide(
            Region node,
            double delay
    ) {

        node.setOpacity(0);

        node.setTranslateY(15);


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(450),
                        node
                );

        fade.setFromValue(0);

        fade.setToValue(1);

        fade.setDelay(
                Duration.millis(delay)
        );


        TranslateTransition move =
                new TranslateTransition(
                        Duration.millis(450),
                        node
                );

        move.setFromY(15);

        move.setToY(0);

        move.setDelay(
                Duration.millis(delay)
        );


        ParallelTransition animation =
                new ParallelTransition(
                        fade,
                        move
                );

        animation.play();
    }


    // =========================================================
    // QR PAYMENT
    // =========================================================

    private void startQrPayment() {

        try {

            String text =
                    amountField.getText()
                            .trim();


            if (text.isEmpty()) {

                showError(
                        "Enter an amount first."
                );

                return;
            }


            double amount =
                    Double.parseDouble(text);


            if (amount <= 0) {

                showError(
                        "Enter an amount greater than ₹0."
                );

                return;
            }


            showQrPayment(
                    amount
            );

        } catch (
                NumberFormatException e
        ) {

            showError(
                    "Please enter a valid amount."
            );

        } catch (
                Exception e
        ) {

            showError(
                    "Unable to start QR payment: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // SHOW QR PAYMENT
    // =========================================================

    private void showQrPayment(
            double amount
    ) {

        if (walletCenter == null) {

            showError(
                    "Payment screen is not ready."
            );

            return;
        }


        // -----------------------------------------------------
        // OVERLAY
        // -----------------------------------------------------

        qrOverlay =
                new StackPane();


        qrOverlay.setPrefSize(
                1250,
                800
        );


        qrOverlay.setStyle(
                "-fx-background-color: "
                        + "rgba(0,8,12,0.92);"
        );


        // -----------------------------------------------------
        // CARD
        // -----------------------------------------------------

        VBox card =
                new VBox(16);


        card.setAlignment(
                Pos.TOP_CENTER
        );


        card.setPrefWidth(
                430
        );


        card.setMaxWidth(
                430
        );


        card.setPadding(
                new Insets(
                        28,
                        30,
                        28,
                        30
                )
        );


        card.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right,"
                        + "#10262B,"
                        + "#071419"
                        + ");"
                        + "-fx-border-color: #20D6C7;"
                        + "-fx-border-width: 1.2;"
                        + "-fx-background-radius: 18;"
                        + "-fx-border-radius: 18;"
        );


        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Label smallTitle =
                new Label(
                        "SMART EV PAYMENT"
                );


        smallTitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );


        smallTitle.setTextFill(
                Color.web("#5FF2C8")
        );


        Label title =
                new Label(
                        "SCAN TO PAY"
                );


        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        26
                )
        );


        title.setTextFill(
                Color.WHITE
        );


        Label amountLabel =
                new Label(
                        String.format(
                                "₹%.2f",
                                amount
                        )
                );


        amountLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        30
                )
        );


        amountLabel.setTextFill(
                Color.web("#5FF2C8")
        );


        Label instruction =
                new Label(
                        "Scan this QR code using another phone"
                );


        instruction.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        12
                )
        );


        instruction.setTextFill(
                Color.web("#8BA7AE")
        );


        // -----------------------------------------------------
        // CREATE QR
        // -----------------------------------------------------

        ImageView qrImage;


        try {

            String qrData =
                    "SMART_EV_PAYMENT"
                            + "|USER_ID:"
                            + loggedInUser.getUserId()
                            + "|AMOUNT:"
                            + String.format(
                                    "%.2f",
                                    amount
                            )
                            + "|CURRENCY:INR"
                            + "|TYPE:WALLET_TOPUP";


            WritableImage image =
                    generateQrImage(
                            qrData,
                            260
                    );


            qrImage =
                    new ImageView(
                            image
                    );


            qrImage.setFitWidth(
                    260
            );


            qrImage.setFitHeight(
                    260
            );


            qrImage.setPreserveRatio(
                    true
            );


        } catch (Exception e) {

            showError(
                    "Unable to generate QR code: "
                            + e.getMessage()
            );

            return;
        }


        // -----------------------------------------------------
        // QR HOLDER
        // -----------------------------------------------------

        StackPane qrHolder =
                new StackPane();


        qrHolder.setPrefSize(
                280,
                280
        );


        qrHolder.setMaxSize(
                280,
                280
        );


        qrHolder.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #5FF2C8;"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 12;"
        );


        qrHolder.getChildren().add(
                qrImage
        );


        // -----------------------------------------------------
        // SCANNING LINE
        // -----------------------------------------------------

        Rectangle scanLine =
                new Rectangle(
                        240,
                        2
                );


        scanLine.setFill(
                Color.web("#5FF2C8")
        );


        scanLine.setOpacity(
                0.9
        );


        qrHolder.getChildren().add(
                scanLine
        );


        TranslateTransition scanAnimation =
                new TranslateTransition(
                        Duration.seconds(1.8),
                        scanLine
                );


        scanAnimation.setFromY(
                -115
        );


        scanAnimation.setToY(
                115
        );


        scanAnimation.setAutoReverse(
                true
        );


        scanAnimation.setCycleCount(
                Animation.INDEFINITE
        );


        scanAnimation.play();


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        Label scanStatus =
                new Label(
                        "● WAITING FOR PAYMENT"
                );


        scanStatus.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );


        scanStatus.setTextFill(
                Color.web("#55DFFF")
        );


        Label demoInfo =
                new Label(
                        "After scanning, confirm the payment below."
                );


        demoInfo.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        10
                )
        );


        demoInfo.setTextFill(
                Color.web("#6F8B92")
        );


        // -----------------------------------------------------
        // COMPLETE BUTTON
        // -----------------------------------------------------

        Button completeButton =
                new Button(
                        "✓  PAYMENT COMPLETED"
                );


        completeButton.setMaxWidth(
                Double.MAX_VALUE
        );


        completeButton.setPrefHeight(
                44
        );


        completeButton.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );


        completeButton.setTextFill(
                Color.web("#00100D")
        );


        completeButton.setStyle(
                "-fx-background-color: #5FF2C8;"
                        + "-fx-background-radius: 10;"
                        + "-fx-cursor: hand;"
        );


        completeButton.setOnMouseEntered(
                e ->
                        completeButton.setStyle(
                                "-fx-background-color: #8FFFF0;"
                                        + "-fx-background-radius: 10;"
                                        + "-fx-cursor: hand;"
                        )
        );


        completeButton.setOnMouseExited(
                e ->
                        completeButton.setStyle(
                                "-fx-background-color: #5FF2C8;"
                                        + "-fx-background-radius: 10;"
                                        + "-fx-cursor: hand;"
                        )
        );


        completeButton.setOnAction(e -> {

            scanStatus.setText(
                    "✓ PAYMENT CONFIRMED"
            );


            scanStatus.setTextFill(
                    Color.web("#5FF2C8")
            );


            completeButton.setDisable(
                    true
            );


            scanAnimation.stop();


            finishQrPayment(
                    amount,
                    card,
                    qrOverlay
            );
        });


        // -----------------------------------------------------
        // CANCEL
        // -----------------------------------------------------

        Button cancelButton =
                new Button(
                        "CANCEL"
                );


        cancelButton.setPrefHeight(
                34
        );


        cancelButton.setPadding(
                new Insets(
                        5,
                        22,
                        5,
                        22
                )
        );


        cancelButton.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );


        cancelButton.setTextFill(
                Color.web("#8BA7AE")
        );


        cancelButton.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: #29444A;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );


        cancelButton.setOnAction(e -> {

            scanAnimation.stop();


            walletCenter.getChildren().remove(
                    qrOverlay
            );


            qrOverlay = null;


            statusLabel.setText(
                    "READY FOR WALLET TRANSACTION"
            );


            statusLabel.setTextFill(
                    Color.web("#8BA7AE")
            );
        });


        // -----------------------------------------------------
        // CARD CONTENT
        // -----------------------------------------------------

        card.getChildren().addAll(
                smallTitle,
                title,
                amountLabel,
                instruction,
                qrHolder,
                scanStatus,
                demoInfo,
                completeButton,
                cancelButton
        );


        qrOverlay.getChildren().add(
                card
        );


        // -----------------------------------------------------
        // SHOW OVERLAY
        // -----------------------------------------------------

        walletCenter.getChildren().add(
                qrOverlay
        );


        // -----------------------------------------------------
        // ENTRANCE
        // -----------------------------------------------------

        card.setOpacity(
                0
        );


        card.setScaleX(
                0.92
        );


        card.setScaleY(
                0.92
        );


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(300),
                        card
                );


        fade.setFromValue(
                0
        );


        fade.setToValue(
                1
        );


        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(350),
                        card
                );


        scale.setFromX(
                0.92
        );


        scale.setFromY(
                0.92
        );


        scale.setToX(
                1
        );


        scale.setToY(
                1
        );


        ParallelTransition entrance =
                new ParallelTransition(
                        fade,
                        scale
                );


        entrance.play();
    }


    // =========================================================
    // GENERATE QR IMAGE
    // =========================================================

    private WritableImage generateQrImage(
            String data,
            int size
    ) throws Exception {


        Map<EncodeHintType, Object> hints =
                new HashMap<>();


        hints.put(
                EncodeHintType.MARGIN,
                1
        );


        BitMatrix matrix =
                new MultiFormatWriter()
                        .encode(
                                data,
                                BarcodeFormat.QR_CODE,
                                size,
                                size,
                                hints
                        );


        WritableImage image =
                new WritableImage(
                        size,
                        size
                );


        PixelWriter writer =
                image.getPixelWriter();


        for (
                int y = 0;
                y < size;
                y++
        ) {

            for (
                    int x = 0;
                    x < size;
                    x++
            ) {

                writer.setColor(
                        x,
                        y,
                        matrix.get(x, y)
                                ? Color.BLACK
                                : Color.WHITE
                );
            }
        }


        return image;
    }


    // =========================================================
    // FINISH QR PAYMENT
    // =========================================================

    private void finishQrPayment(
            double amount,
            VBox card,
            StackPane overlay
    ) {


        // -----------------------------------------------------
        // SUCCESS ICON
        // -----------------------------------------------------

        Label successIcon =
                new Label(
                        "✓"
                );


        successIcon.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        64
                )
        );


        successIcon.setTextFill(
                Color.web("#5FF2C8")
        );


        // -----------------------------------------------------
        // SUCCESS TITLE
        // -----------------------------------------------------

        Label successTitle =
                new Label(
                        "PAYMENT SUCCESSFUL"
                );


        successTitle.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        23
                )
        );


        successTitle.setTextFill(
                Color.WHITE
        );


        // -----------------------------------------------------
        // SUCCESS AMOUNT
        // -----------------------------------------------------

        Label successAmount =
                new Label(
                        String.format(
                                "₹%.2f",
                                amount
                        )
                );


        successAmount.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        32
                )
        );


        successAmount.setTextFill(
                Color.web("#5FF2C8")
        );


        // -----------------------------------------------------
        // SUCCESS MESSAGE
        // -----------------------------------------------------

        Label successText =
                new Label(
                        "Wallet balance updated successfully"
                );


        successText.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        12
                )
        );


        successText.setTextFill(
                Color.web("#8BA7AE")
        );


        // -----------------------------------------------------
        // REPLACE QR WITH SUCCESS
        // -----------------------------------------------------

        card.getChildren().clear();


        card.setAlignment(
                Pos.CENTER
        );


        card.getChildren().addAll(
                successIcon,
                successTitle,
                successAmount,
                successText
        );


        // -----------------------------------------------------
        // UPDATE DATABASE
        // -----------------------------------------------------

        try {

            walletService.addMoney(
                    loggedInUser.getUserId(),
                    amount
            );


            amountField.clear();


            refreshBalance();


            animateBalance();


            statusLabel.setText(
                    String.format(
                            "✓ ₹%.2f ADDED SUCCESSFULLY",
                            amount
                    )
            );


            statusLabel.setTextFill(
                    Color.web("#5FF2C8")
            );


        } catch (Exception e) {

            card.getChildren().clear();


            Label error =
                    new Label(
                            "PAYMENT UPDATE FAILED"
                    );


            error.setFont(
                    Font.font(
                            "System",
                            FontWeight.BOLD,
                            20
                    )
            );


            error.setTextFill(
                    Color.web("#FF6B7A")
            );


            Label message =
                    new Label(
                            e.getMessage()
                    );


            message.setWrapText(
                    true
            );


            message.setTextFill(
                    Color.web("#AABCC2")
            );


            card.getChildren().addAll(
                    error,
                    message
            );


            return;
        }


        // -----------------------------------------------------
        // SUCCESS ANIMATION
        // -----------------------------------------------------

        successIcon.setScaleX(
                0.5
        );


        successIcon.setScaleY(
                0.5
        );


        successIcon.setOpacity(
                0
        );


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(350),
                        successIcon
                );


        fade.setFromValue(
                0
        );


        fade.setToValue(
                1
        );


        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(450),
                        successIcon
                );


        scale.setFromX(
                0.5
        );


        scale.setFromY(
                0.5
        );


        scale.setToX(
                1
        );


        scale.setToY(
                1
        );


        ParallelTransition successAnimation =
                new ParallelTransition(
                        fade,
                        scale
                );


        successAnimation.play();


        // -----------------------------------------------------
        // CLOSE AFTER SUCCESS
        // -----------------------------------------------------

        successAnimation.setOnFinished(
                e -> {

                    PauseTransition pause =
                            new PauseTransition(
                                    Duration.seconds(1.8)
                            );


                    pause.setOnFinished(
                            event -> {

                                FadeTransition close =
                                        new FadeTransition(
                                                Duration.millis(300),
                                                overlay
                                        );


                                close.setFromValue(
                                        1
                                );


                                close.setToValue(
                                        0
                                );


                                close.setOnFinished(
                                        done -> {

                                            if (
                                                    walletCenter
                                                            != null
                                            ) {

                                                walletCenter
                                                        .getChildren()
                                                        .remove(
                                                                overlay
                                                        );
                                            }


                                            qrOverlay =
                                                    null;
                                        }
                                );


                                close.play();
                            }
                    );


                    pause.play();
                }
        );
    }
}