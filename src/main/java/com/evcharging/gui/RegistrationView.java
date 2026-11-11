package com.evcharging.gui;

import com.evcharging.model.User;
import com.evcharging.service.UserService;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class RegistrationView {

    private final UserService userService = new UserService();
    private final Random random = new Random();

    // ============================================================
    // LOGIN-MATCHING DESIGN SIZE
    // ============================================================

    private static final double WIDTH = 1440;
    private static final double HEIGHT = 900;

    public void show(Stage stage) {

        Pane root = new Pane();

        root.setPrefSize(
                WIDTH,
                HEIGHT
        );

        root.setStyle(
                "-fx-background-color: #020509;"
        );

        // ============================================================
        // BACKGROUND
        // ============================================================

        Pane background = new Pane();

        background.setPrefSize(
                WIDTH,
                HEIGHT
        );

        background.setStyle(
                "-fx-background-color: "
                        + "linear-gradient(to bottom right, "
                        + "#020509, #081725, #03100f);"
        );

        root.getChildren().add(background);

        // ============================================================
        // SMART CITY GRID
        // ============================================================

        for (int x = 0; x <= WIDTH; x += 55) {

            Line line = new Line(
                    x,
                    0,
                    x,
                    HEIGHT
            );

            line.setStroke(
                    Color.rgb(
                            70,
                            200,
                            255,
                            0.045
                    )
            );

            root.getChildren().add(line);
        }

        for (int y = 0; y <= HEIGHT; y += 55) {

            Line line = new Line(
                    0,
                    y,
                    WIDTH,
                    y
            );

            line.setStroke(
                    Color.rgb(
                            70,
                            255,
                            190,
                            0.045
                    )
            );

            root.getChildren().add(line);
        }

        // ============================================================
        // LEFT SIDE ATMOSPHERIC GLOW
        // ============================================================

        Circle glow1 =
                new Circle(
                        260,
                        Color.rgb(
                                0,
                                255,
                                190,
                                0.035
                        )
                );

        glow1.setLayoutX(330);
        glow1.setLayoutY(430);

        glow1.setEffect(
                new Glow(0.8)
        );

        root.getChildren().add(
                glow1
        );

        Circle glow2 =
                new Circle(
                        180,
                        Color.rgb(
                                0,
                                190,
                                255,
                                0.025
                        )
                );

        glow2.setLayoutX(760);
        glow2.setLayoutY(200);

        glow2.setEffect(
                new Glow(0.8)
        );

        root.getChildren().add(
                glow2
        );

        // ============================================================
        // FLOATING CITY PARTICLES
        // ============================================================

        for (int i = 0; i < 65; i++) {

            Circle particle =
                    new Circle(
                            1.2 + random.nextDouble() * 2.8
                    );

            particle.setFill(
                    i % 2 == 0
                            ? Color.CYAN
                            : Color.LIGHTGREEN
            );

            particle.setOpacity(
                    0.12 + random.nextDouble() * 0.5
            );

            particle.setLayoutX(
                    30 + random.nextDouble() * 650
            );

            particle.setLayoutY(
                    40 + random.nextDouble() * 780
            );

            root.getChildren().add(
                    particle
            );

            TranslateTransition movement =
                    new TranslateTransition(
                            Duration.seconds(
                                    3 + random.nextDouble() * 4
                            ),
                            particle
                    );

            movement.setFromY(0);

            movement.setToY(
                    -60 - random.nextDouble() * 100
            );

            movement.setCycleCount(
                    TranslateTransition.INDEFINITE
            );

            movement.setAutoReverse(true);

            movement.play();
        }

        // ============================================================
        // SMART CITY BUILDINGS
        // ============================================================

        createBuilding(
                root,
                80,
                570,
                95,
                260
        );

        createBuilding(
                root,
                195,
                480,
                110,
                350
        );

        createBuilding(
                root,
                325,
                610,
                85,
                220
        );

        createBuilding(
                root,
                430,
                510,
                120,
                320
        );

        createBuilding(
                root,
                570,
                590,
                80,
                240
        );

        // ============================================================
        // CONNECTED CITY NETWORK
        // ============================================================

        Line network1 =
                new Line(
                        128,
                        570,
                        250,
                        480
                );

        Line network2 =
                new Line(
                        250,
                        480,
                        490,
                        510
                );

        Line network3 =
                new Line(
                        490,
                        510,
                        610,
                        590
                );

        Line network4 =
                new Line(
                        610,
                        590,
                        680,
                        530
                );

        for (Line line :
                new Line[]{
                        network1,
                        network2,
                        network3,
                        network4
                }) {

            line.setStroke(
                    Color.rgb(
                            70,
                            255,
                            200,
                            0.45
                    )
            );

            line.setStrokeWidth(2);

            root.getChildren().add(
                    line
            );
        }

        // ============================================================
        // NETWORK NODES
        // ============================================================

        Circle node1 =
                createNetworkNode(
                        root,
                        128,
                        570
                );

        Circle node2 =
                createNetworkNode(
                        root,
                        250,
                        480
                );

        Circle node3 =
                createNetworkNode(
                        root,
                        490,
                        510
                );

        Circle node4 =
                createNetworkNode(
                        root,
                        610,
                        590
                );

        Circle node5 =
                createNetworkNode(
                        root,
                        680,
                        530
                );

        // ============================================================
        // MOVING NETWORK ENERGY
        // ============================================================

        Circle networkEnergy =
                new Circle(
                        5,
                        Color.CYAN
                );

        networkEnergy.setEffect(
                new Glow(1)
        );

        root.getChildren().add(
                networkEnergy
        );

        Timeline networkAnimation =
                new Timeline();

        networkAnimation.getKeyFrames().addAll(

                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                networkEnergy
                                        .layoutXProperty(),
                                128
                        ),
                        new KeyValue(
                                networkEnergy
                                        .layoutYProperty(),
                                570
                        )
                ),

                new KeyFrame(
                        Duration.seconds(1.2),
                        new KeyValue(
                                networkEnergy
                                        .layoutXProperty(),
                                250
                        ),
                        new KeyValue(
                                networkEnergy
                                        .layoutYProperty(),
                                480
                        )
                ),

                new KeyFrame(
                        Duration.seconds(2.6),
                        new KeyValue(
                                networkEnergy
                                        .layoutXProperty(),
                                490
                        ),
                        new KeyValue(
                                networkEnergy
                                        .layoutYProperty(),
                                510
                        )
                ),

                new KeyFrame(
                        Duration.seconds(3.8),
                        new KeyValue(
                                networkEnergy
                                        .layoutXProperty(),
                                610
                        ),
                        new KeyValue(
                                networkEnergy
                                        .layoutYProperty(),
                                590
                        )
                ),

                new KeyFrame(
                        Duration.seconds(5),
                        new KeyValue(
                                networkEnergy
                                        .layoutXProperty(),
                                680
                        ),
                        new KeyValue(
                                networkEnergy
                                        .layoutYProperty(),
                                530
                        )
                )
        );

        networkAnimation.setCycleCount(
                Timeline.INDEFINITE
        );

        networkAnimation.play();

        // ============================================================
        // SDG 9
        // ============================================================

        Label sdg9 =
                new Label(
                        "SDG 9"
                );

        sdg9.setFont(
                Font.font(
                        "Arial",
                        22
                )
        );

        sdg9.setTextFill(
                Color.CYAN
        );

        sdg9.setLayoutX(130);
        sdg9.setLayoutY(120);

        root.getChildren().add(
                sdg9
        );

        Label sdg9Title =
                new Label(
                        "INDUSTRY • INNOVATION • INFRASTRUCTURE"
                );

        sdg9Title.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        sdg9Title.setTextFill(
                Color.WHITE
        );

        sdg9Title.setLayoutX(85);
        sdg9Title.setLayoutY(155);

        root.getChildren().add(
                sdg9Title
        );

        // ============================================================
        // SDG 11
        // ============================================================

        Label sdg11 =
                new Label(
                        "SDG 11"
                );

        sdg11.setFont(
                Font.font(
                        "Arial",
                        22
                )
        );

        sdg11.setTextFill(
                Color.LIGHTGREEN
        );

        sdg11.setLayoutX(130);
        sdg11.setLayoutY(190);

        root.getChildren().add(
                sdg11
        );

        Label sdg11Title =
                new Label(
                        "SUSTAINABLE CITIES & COMMUNITIES"
                );

        sdg11Title.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        sdg11Title.setTextFill(
                Color.WHITE
        );

        sdg11Title.setLayoutX(105);
        sdg11Title.setLayoutY(225);

        root.getChildren().add(
                sdg11Title
        );

        // ============================================================
        // LEFT MAIN TITLE
        // ============================================================

        Label smartEV =
                new Label(
                        "SMART EV"
                );

        smartEV.setFont(
                Font.font(
                        "Arial",
                        42
                )
        );

        smartEV.setTextFill(
                Color.WHITE
        );

        smartEV.setEffect(
                new Glow(0.5)
        );

        smartEV.setLayoutX(85);
        smartEV.setLayoutY(280);

        root.getChildren().add(
                smartEV
        );

        // ============================================================
        // LEFT SUBTITLE
        // ============================================================

        Label mobility =
                new Label(
                        "SMART MOBILITY NETWORK"
                );

        mobility.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        mobility.setTextFill(
                Color.CYAN
        );

        mobility.setLayoutX(90);
        mobility.setLayoutY(335);

        root.getChildren().add(
                mobility
        );

        // ============================================================
        // LEFT DESCRIPTION
        // ============================================================

        Label description =
                new Label(
                        "Join the smart mobility network\n"
                                + "and help build cleaner,\n"
                                + "connected cities."
                );

        description.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        description.setTextFill(
                Color.LIGHTGRAY
        );

        description.setLineSpacing(5);

        description.setLayoutX(90);
        description.setLayoutY(370);

        root.getChildren().add(
                description
        );

        // ============================================================
        // NETWORK STATUS
        // ============================================================

        Circle statusDot =
                new Circle(
                        5,
                        Color.LIGHTGREEN
                );

        statusDot.setEffect(
                new Glow(0.8)
        );

        statusDot.setLayoutX(92);
        statusDot.setLayoutY(470);

        root.getChildren().add(
                statusDot
        );

        Label status =
                new Label(
                        "SMART CITY NETWORK • ONLINE"
                );

        status.setFont(
                Font.font(
                        "Arial",
                        11
                )
        );

        status.setTextFill(
                Color.LIGHTGREEN
        );

        status.setLayoutX(108);
        status.setLayoutY(463);

        root.getChildren().add(
                status
        );

        // ============================================================
        // REGISTRATION CARD
        // ============================================================

        VBox card =
                new VBox(12);

        card.setAlignment(
                Pos.CENTER
        );

        card.setPadding(
                new Insets(30)
        );

        card.setPrefWidth(400);
        card.setPrefHeight(700);

        card.setLayoutX(900);
        card.setLayoutY(100);

        card.setStyle(
                "-fx-background-color: rgba(10, 25, 30, 0.95);"
                        + "-fx-background-radius: 25;"
                        + "-fx-border-color: rgba(80, 220, 255, 0.45);"
                        + "-fx-border-width: 1.5;"
                        + "-fx-border-radius: 25;"
        );

        // ============================================================
        // TITLE
        // ============================================================

        Label title =
                new Label(
                        "CREATE ACCOUNT"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        29
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        title.setEffect(
                new Glow(0.4)
        );

        // ============================================================
        // SUBTITLE
        // ============================================================

        Label subtitle =
                new Label(
                        "Join Smart EV Mobility"
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        subtitle.setTextFill(
                Color.LIGHTGRAY
        );

        // ============================================================
        // NAME
        // ============================================================

        TextField nameField =
                new TextField();

        nameField.setPromptText(
                "Full Name"
        );

        nameField.setMaxWidth(300);
        nameField.setPrefHeight(42);

        styleField(nameField);

        // ============================================================
        // EMAIL
        // ============================================================

        TextField emailField =
                new TextField();

        emailField.setPromptText(
                "Email"
        );

        emailField.setMaxWidth(300);
        emailField.setPrefHeight(42);

        styleField(emailField);

        // ============================================================
        // PASSWORD
        // ============================================================

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Password"
        );

        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(42);

        styleField(passwordField);

        // ============================================================
        // PHONE
        // ============================================================

        TextField phoneField =
                new TextField();

        phoneField.setPromptText(
                "Phone Number"
        );

        phoneField.setMaxWidth(300);
        phoneField.setPrefHeight(42);

        styleField(phoneField);

        // ============================================================
        // REGISTER BUTTON
        // ============================================================

        Button registerButton =
                new Button(
                        "CREATE ACCOUNT"
                );

        registerButton.setPrefWidth(300);
        registerButton.setPrefHeight(45);

        registerButton.setStyle(
                "-fx-background-color: #159b8a;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 10;"
                        + "-fx-cursor: hand;"
        );

        // ============================================================
        // BACK BUTTON
        // ============================================================

        Button backButton =
                new Button(
                        "← BACK TO LOGIN"
                );

        backButton.setPrefWidth(300);
        backButton.setPrefHeight(40);

        backButton.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: #72ffd0;"
                        + "-fx-border-color: #286d60;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        // ============================================================
        // MESSAGE
        // ============================================================

        Label message =
                new Label();

        message.setWrapText(true);
        message.setMaxWidth(300);

        message.setAlignment(
                Pos.CENTER
        );

        message.setTextFill(
                Color.LIGHTCORAL
        );

        // ============================================================
        // REGISTER ACTION
        // ============================================================

        registerButton.setOnAction(event -> {

            String name =
                    nameField
                            .getText()
                            .trim();

            String email =
                    emailField
                            .getText()
                            .trim();

            String password =
                    passwordField
                            .getText();

            String phone =
                    phoneField
                            .getText()
                            .trim();

            // Basic validation

            if (name.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()
                    || phone.isEmpty()) {

                message.setTextFill(
                        Color.LIGHTCORAL
                );

                message.setText(
                        "Please fill in all fields."
                );

                return;
            }

            if (!email.contains("@")
                    || !email.contains(".")) {

                message.setTextFill(
                        Color.LIGHTCORAL
                );

                message.setText(
                        "Please enter a valid email."
                );

                return;
            }

            if (password.length() < 4) {

                message.setTextFill(
                        Color.LIGHTCORAL
                );

                message.setText(
                        "Password must contain at least 4 characters."
                );

                return;
            }

            // ========================================================
            // CREATE USER OBJECT
            // ========================================================

            User user =
                    new User(
                            name,
                            email,
                            password,
                            phone
                    );

            boolean registered =
                    userService.registerUser(
                            user
                    );

            if (registered) {

                message.setTextFill(
                        Color.LIGHTGREEN
                );

                message.setText(
                        "Registration successful! "
                                + "You can now login."
                );

                nameField.clear();
                emailField.clear();
                passwordField.clear();
                phoneField.clear();

            } else {

                message.setTextFill(
                        Color.LIGHTCORAL
                );

                message.setText(
                        "Registration failed. "
                                + "Email may already be registered."
                );
            }
        });

        // ============================================================
        // BACK TO LOGIN
        // ============================================================

        backButton.setOnAction(event -> {

            LoginView loginView =
                    new LoginView();

            loginView.show(stage);
        });

        // ============================================================
        // ADD CARD CONTENT
        // ============================================================

        card.getChildren().addAll(
                title,
                subtitle,
                nameField,
                emailField,
                passwordField,
                phoneField,
                registerButton,
                backButton,
                message
        );

        root.getChildren().add(
                card
        );

        // ============================================================
        // CARD ENTRANCE ANIMATION
        // ============================================================

        card.setOpacity(0);
        card.setTranslateX(90);

        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(1.2),
                        card
                );

        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide =
                new TranslateTransition(
                        Duration.seconds(1.2),
                        card
                );

        slide.setFromX(90);
        slide.setToX(0);

        new ParallelTransition(
                fade,
                slide
        ).play();

        // ============================================================
        // FULL SCREEN
        // ============================================================

        javafx.geometry.Rectangle2D bounds =
                Screen.getPrimary()
                        .getVisualBounds();

        Scene scene =
                new Scene(
                        root,
                        bounds.getWidth(),
                        bounds.getHeight()
                );

        stage.setTitle(
                "Smart EV Charging System - Registration"
        );

        stage.setScene(scene);

        // ============================================================
        // MATCH LOGINVIEW WINDOW BEHAVIOR
        // ============================================================

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

    // ================================================================
    // FIELD STYLE
    // ================================================================

    private void styleField(
            TextField field) {

        field.setStyle(
                "-fx-background-color: #071217;"
                        + "-fx-text-fill: white;"
                        + "-fx-prompt-text-fill: #78909c;"
                        + "-fx-border-color: #285c58;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
        );
    }

    // ================================================================
    // NETWORK NODE
    // ================================================================

    private Circle createNetworkNode(
            Pane root,
            double x,
            double y) {

        Circle node =
                new Circle(
                        5,
                        Color.CYAN
                );

        node.setLayoutX(x);
        node.setLayoutY(y);

        node.setEffect(
                new Glow(0.8)
        );

        root.getChildren().add(
                node
        );

        return node;
    }

    // ================================================================
    // SMART CITY BUILDING
    // ================================================================

    private void createBuilding(
            Pane root,
            double x,
            double y,
            double width,
            double height) {

        Rectangle building =
                new Rectangle(
                        width,
                        height
                );

        building.setLayoutX(x);
        building.setLayoutY(y);

        building.setFill(
                Color.rgb(
                        8,
                        35,
                        42,
                        0.95
                )
        );

        building.setStroke(
                Color.rgb(
                        60,
                        210,
                        190,
                        0.35
                )
        );

        root.getChildren().add(
                building
        );

        // ============================================================
        // WINDOWS
        // ============================================================

        for (int row = 0;
             row < 5;
             row++) {

            for (int col = 0;
                 col < 3;
                 col++) {

                double windowX =
                        x + 18 + col * 27;

                double windowY =
                        y + 25 + row * 40;

                // Don't draw windows outside building
                if (windowX > x + width - 8
                        || windowY > y + height - 8) {
                    continue;
                }

                Circle window =
                        new Circle(
                                3
                        );

                window.setCenterX(
                        windowX
                );

                window.setCenterY(
                        windowY
                );

                window.setFill(
                        Color.rgb(
                                80,
                                255,
                                210,
                                0.65
                        )
                );

                root.getChildren().add(
                        window
                );
            }
        }
    }
}