package com.evcharging.gui;

import com.evcharging.exception.InvalidLoginException;
import com.evcharging.model.User;
import com.evcharging.service.UserService;
import com.evcharging.util.DBConnection;

import javafx.animation.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class LoginView {

    private final UserService userService = new UserService();

    private final Random random = new Random();

    // =============================================================
    // SHOW LOGIN PAGE
    // =============================================================

    public void show(Stage stage) {

        // =========================================================
        // ROOT
        // =========================================================

        Pane root = new Pane();

        root.setPrefSize(1440, 900);

        root.setStyle(
                "-fx-background-color: #02080B;"
        );

        // =========================================================
        // MAIN BACKGROUND
        // =========================================================

        Rectangle background =
                new Rectangle(
                        0,
                        0,
                        1440,
                        900
                );

        background.setFill(
                new LinearGradient(
                        0,
                        0,
                        1,
                        1,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.web("#061A1D")
                        ),
                        new Stop(
                                0.38,
                                Color.web("#071316")
                        ),
                        new Stop(
                                0.72,
                                Color.web("#040B0F")
                        ),
                        new Stop(
                                1,
                                Color.web("#010405")
                        )
                )
        );

        root.getChildren().add(background);

        // =========================================================
        // LEFT SIDE LARGE GLOW
        // =========================================================

        Circle largeGlow =
                new Circle(
                        285,
                        430,
                        310
                );

        largeGlow.setFill(
                new RadialGradient(
                        0,
                        0,
                        0.5,
                        0.5,
                        0.5,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.web("#22E6B0", 0.18)
                        ),
                        new Stop(
                                0.38,
                                Color.web("#16B995", 0.08)
                        ),
                        new Stop(
                                0.72,
                                Color.web("#0A6556", 0.03)
                        ),
                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        largeGlow.setMouseTransparent(true);

        root.getChildren().add(
                largeGlow
        );

        // =========================================================
        // SECOND GLOW
        // =========================================================

        Circle secondGlow =
                new Circle(
                        475,
                        600,
                        190
                );

        secondGlow.setFill(
                new RadialGradient(
                        0,
                        0,
                        0.5,
                        0.5,
                        0.5,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.web("#38DDB2", 0.08)
                        ),
                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        root.getChildren().add(
                secondGlow
        );

        // =========================================================
        // BACKGROUND GRID
        // =========================================================

        Pane grid =
                new Pane();

        for (int x = 0; x <= 580; x += 40) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            900
                    );

            line.setStroke(
                    Color.web(
                            "#42CDB0",
                            0.065
                    )
            );

            line.setStrokeWidth(1);

            grid.getChildren().add(
                    line
            );
        }

        for (int y = 0; y <= 900; y += 40) {

            Line line =
                    new Line(
                            0,
                            y,
                            580,
                            y
                    );

            line.setStroke(
                    Color.web(
                            "#42CDB0",
                            0.065
                    )
            );

            line.setStrokeWidth(1);

            grid.getChildren().add(
                    line
            );
        }

        root.getChildren().add(
                grid
        );

 

// =========================================================
// ATMOSPHERIC DUST + ENERGY SPARKS
// =========================================================

Pane particles =
        new Pane();

for (int i = 0; i < 75; i++) {

    double size =
            0.6 + random.nextDouble() * 2.2;

    Circle particle =
            new Circle(size);

    particle.setCenterX(
            10 + random.nextDouble() * 555
    );

    particle.setCenterY(
            10 + random.nextDouble() * 850
    );

    double opacity =
            0.12 + random.nextDouble() * 0.65;

    // Mostly soft dust, with some brighter energy sparks
    if (i % 6 == 0) {

        particle.setRadius(
                1.5 + random.nextDouble() * 2
        );

        particle.setFill(
                Color.web(
                        "#8FFFF0",
                        opacity
                )
        );

        particle.setEffect(
                new Glow(1.0)
        );

    } else {

        particle.setFill(
                Color.web(
                        "#4FCFB3",
                        opacity
                )
        );

        particle.setEffect(
                new Glow(0.35)
        );
    }

    particles.getChildren().add(
            particle
    );

    // Gentle floating movement
    TranslateTransition movement =
            new TranslateTransition(
                    Duration.seconds(
                            5 + random.nextDouble() * 8
                    ),
                    particle
            );

    movement.setByX(
            -18 + random.nextDouble() * 36
    );

    movement.setByY(
            -35 - random.nextDouble() * 90
    );

    movement.setAutoReverse(true);

    movement.setCycleCount(
            Animation.INDEFINITE
    );

    movement.play();

    // Natural brightness variation
    FadeTransition fade =
            new FadeTransition(
                    Duration.seconds(
                            1.5 + random.nextDouble() * 3
                    ),
                    particle
            );

    fade.setFromValue(
            0.15
    );

    fade.setToValue(
            Math.min(
                    1.0,
                    opacity + 0.25
            )
    );

    fade.setAutoReverse(
            true
    );

    fade.setCycleCount(
            Animation.INDEFINITE
    );

    fade.play();
}

// A few tiny fast-moving sparks
for (int i = 0; i < 14; i++) {

    Circle spark =
            new Circle(
                    1.2
            );

    spark.setCenterX(
            30 + random.nextDouble() * 520
    );

    spark.setCenterY(
            120 + random.nextDouble() * 650
    );

    spark.setFill(
            Color.web(
                    "#B8FFF1",
                    0.8
            )
    );

    spark.setEffect(
            new Glow(1.2)
    );

    particles.getChildren().add(
            spark
    );

    TranslateTransition sparkMove =
            new TranslateTransition(
                    Duration.seconds(
                            1.8 + random.nextDouble() * 3
                    ),
                    spark
            );

    sparkMove.setByX(
            -25 + random.nextDouble() * 50
    );

    sparkMove.setByY(
            -45 - random.nextDouble() * 100
    );

    sparkMove.setCycleCount(
            Animation.INDEFINITE
    );

    sparkMove.setAutoReverse(
            true
    );

    sparkMove.play();

    FadeTransition sparkFade =
            new FadeTransition(
                    Duration.seconds(
                            0.8 + random.nextDouble() * 1.5
                    ),
                    spark
            );

    sparkFade.setFromValue(0.1);
    sparkFade.setToValue(1.0);

    sparkFade.setAutoReverse(true);

    sparkFade.setCycleCount(
            Animation.INDEFINITE
    );

    sparkFade.play();
}

root.getChildren().add(
        particles
);

        // =========================================================
        // SMART EV BRAND
        // =========================================================

        Label smartEV =
                new Label(
                        "⚡ SMART EV"
                );

        smartEV.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        42
                )
        );

        smartEV.setTextFill(
                Color.web(
                        "#65F4CE"
                )
        );

        smartEV.setLayoutX(65);
        smartEV.setLayoutY(35);

        smartEV.setEffect(
                new Glow(
                        0.35
                )
        );

        root.getChildren().add(
                smartEV
        );

        Label tagline =
                new Label(
                        "SMART CHARGING  •  SMART MOBILITY  •  SMART FUTURE"
                );

        tagline.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        tagline.setTextFill(
                Color.web(
                        "#789C96"
                )
        );

        tagline.setLayoutX(69);
        tagline.setLayoutY(88);

        root.getChildren().add(
                tagline
        );

        // =========================================================
        // SDG BADGES
        // =========================================================

        Label sdg7 =
                createSDGBadge(
                        "SDG 7",
                        "CLEAN ENERGY",
                        70,
                        125
                );

        Label sdg9 =
                createSDGBadge(
                        "SDG 9",
                        "INNOVATION",
                        205,
                        125
                );

        Label sdg11 =
                createSDGBadge(
                        "SDG 11",
                        "SMART CITIES",
                        340,
                        125
                );

        root.getChildren().addAll(
                sdg7,
                sdg9,
                sdg11
        );

        // =========================================================
        // MAIN LEFT DESCRIPTION
        // =========================================================

        Label cleanEnergy =
                new Label(
                        "INTELLIGENT EV ENERGY NETWORK"
                );

        cleanEnergy.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        cleanEnergy.setTextFill(
                Color.WHITE
        );

        cleanEnergy.setLayoutX(70);
        cleanEnergy.setLayoutY(175);

        root.getChildren().add(
                cleanEnergy
        );

        Label description =
                new Label(
                        "Powering the future with clean energy,\n"
                                + "intelligent charging and connected mobility."
                );

        description.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        description.setTextFill(
                Color.web(
                        "#8AA9A5"
                )
        );

        description.setLayoutX(70);
        description.setLayoutY(208);

        root.getChildren().add(
                description
        );

        // =========================================================
        // CHARGING STATION
        // =========================================================

        Group chargingStation =
                new Group();

        Circle chargerGlow =
                new Circle(
                        150,
                        375,
                        90
                );

        chargerGlow.setFill(
                Color.web(
                        "#38E7B5",
                        0.08
                )
        );

        chargerGlow.setEffect(
                new Glow(
                        0.8
                )
        );

        Rectangle chargerBody =
                new Rectangle(
                        112,
                        305,
                        76,
                        155
                );

        chargerBody.setArcWidth(18);
        chargerBody.setArcHeight(18);

        chargerBody.setFill(
                Color.web(
                        "#10272A"
                )
        );

        chargerBody.setStroke(
                Color.web(
                        "#55EBC6"
                )
        );

        chargerBody.setStrokeWidth(2);

        Rectangle chargerTop =
                new Rectangle(
                        121,
                        318,
                        58,
                        45
                );

        chargerTop.setArcWidth(8);
        chargerTop.setArcHeight(8);

        chargerTop.setFill(
                Color.web(
                        "#071416"
                )
        );

        chargerTop.setStroke(
                Color.web(
                        "#255A52"
                )
        );

        Label chargeLabel =
                new Label(
                        "80%"
                );

        chargeLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        chargeLabel.setTextFill(
                Color.web(
                        "#6BFFE0"
                )
        );

        chargeLabel.setLayoutX(133);
        chargeLabel.setLayoutY(331);

        Circle chargerLight =
                new Circle(
                        150,
                        385,
                        7
                );

        chargerLight.setFill(
                Color.web(
                        "#68FFDB"
                )
        );

        chargerLight.setEffect(
                new Glow(
                        1
                )
        );

        Circle holder =
                new Circle(
                        150,
                        430,
                        9
                );

        holder.setFill(
                Color.web(
                        "#071316"
                )
        );

        holder.setStroke(
                Color.web(
                        "#54E9C5"
                )
        );

        Rectangle chargerBase =
                new Rectangle(
                        95,
                        458,
                        110,
                        8
                );

        chargerBase.setArcWidth(5);
        chargerBase.setArcHeight(5);

        chargerBase.setFill(
                Color.web(
                        "#1A3538"
                )
        );

        chargingStation.getChildren().addAll(
                chargerGlow,
                chargerBody,
                chargerTop,
                chargeLabel,
                chargerLight,
                holder,
                chargerBase
        );

        root.getChildren().add(
                chargingStation
        );

        // =========================================================
        // CHARGER PULSE
        // =========================================================

        FadeTransition chargerPulse =
                new FadeTransition(
                        Duration.seconds(
                                0.8
                        ),
                        chargerLight
                );

        chargerPulse.setFromValue(
                0.25
        );

        chargerPulse.setToValue(
                1
        );

        chargerPulse.setAutoReverse(
                true
        );

        chargerPulse.setCycleCount(
                Animation.INDEFINITE
        );

        chargerPulse.play();

        // =========================================================
        // EV CAR
        // =========================================================

        Group car =
                new Group();

        Ellipse carGlow =
                new Ellipse(
                        350,
                        535,
                        175,
                        30
                );

        carGlow.setFill(
                Color.web(
                        "#35E5B2",
                        0.10
                )
        );

        carGlow.setEffect(
                new Glow(
                        0.8
                )
        );

        Path carBody =
                new Path();

        carBody.getElements().add(
                new MoveTo(
                        210,
                        510
                )
        );

        carBody.getElements().add(
                new LineTo(
                        230,
                        470
                )
        );

        carBody.getElements().add(
                new LineTo(
                        270,
                        443
                )
        );

        carBody.getElements().add(
                new LineTo(
                        390,
                        443
                )
        );

        carBody.getElements().add(
                new LineTo(
                        435,
                        475
                )
        );

        carBody.getElements().add(
                new LineTo(
                        485,
                        490
                )
        );

        carBody.getElements().add(
                new LineTo(
                        495,
                        515
                )
        );

        carBody.getElements().add(
                new LineTo(
                        490,
                        530
                )
        );

        carBody.getElements().add(
                new LineTo(
                        215,
                        530
                )
        );

        carBody.getElements().add(
                new ClosePath()
        );

        carBody.setFill(
                Color.web(
                        "#102B2F"
                )
        );

        carBody.setStroke(
                Color.web(
                        "#56EFC7"
                )
        );

        carBody.setStrokeWidth(2);

        Polygon frontWindow =
                new Polygon(
                        352, 449,
                        390, 449,
                        425, 475,
                        357, 475
                );

        frontWindow.setFill(
                Color.web(
                        "#071518"
                )
        );

        frontWindow.setStroke(
                Color.web(
                        "#317B70"
                )
        );

        Polygon rearWindow =
                new Polygon(
                        275, 449,
                        345, 449,
                        350, 475,
                        255, 475
                );

        rearWindow.setFill(
                Color.web(
                        "#071518"
                )
        );

        rearWindow.setStroke(
                Color.web(
                        "#317B70"
                )
        );

        Line roofLine =
                new Line(
                        270,
                        443,
                        390,
                        443
                );

        roofLine.setStroke(
                Color.web(
                        "#67FFE0"
                )
        );

        roofLine.setStrokeWidth(2);

        Circle rearWheel =
                new Circle(
                        275,
                        530,
                        25
                );

        rearWheel.setFill(
                Color.web(
                        "#05090B"
                )
        );

        rearWheel.setStroke(
                Color.web(
                        "#4DE2C0"
                )
        );

        rearWheel.setStrokeWidth(2);

        Circle frontWheel =
                new Circle(
                        440,
                        530,
                        25
                );

        frontWheel.setFill(
                Color.web(
                        "#05090B"
                )
        );

        frontWheel.setStroke(
                Color.web(
                        "#4DE2C0"
                )
        );

        frontWheel.setStrokeWidth(2);

        Circle rearCenter =
                new Circle(
                        275,
                        530,
                        9
                );

        rearCenter.setFill(
                Color.web(
                        "#183638"
                )
        );

        Circle frontCenter =
                new Circle(
                        440,
                        530,
                        9
                );

        frontCenter.setFill(
                Color.web(
                        "#183638"
                )
        );

        Circle headlight =
                new Circle(
                        484,
                        501,
                        5
                );

        headlight.setFill(
                Color.web(
                        "#B8FFF1"
                )
        );

        headlight.setEffect(
                new Glow(
                        1
                )
        );

        Label evSymbol =
                new Label(
                        "EV"
                );

        evSymbol.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        evSymbol.setTextFill(
                Color.web(
                        "#70FFE0"
                )
        );

        evSymbol.setLayoutX(375);
        evSymbol.setLayoutY(485);

        car.getChildren().addAll(
                carGlow,
                carBody,
                rearWindow,
                frontWindow,
                roofLine,
                rearWheel,
                frontWheel,
                rearCenter,
                frontCenter,
                headlight,
                evSymbol
        );

        root.getChildren().add(
                car
        );

        // =========================================================
        // CAR FLOATING ANIMATION
        // =========================================================

        TranslateTransition carFloat =
                new TranslateTransition(
                        Duration.seconds(2.8),
                        car
                );

        carFloat.setFromY(0);
        carFloat.setToY(-5);

        carFloat.setAutoReverse(
                true
        );

        carFloat.setCycleCount(
                Animation.INDEFINITE
        );

        carFloat.play();

        // =========================================================
        // CHARGING CABLE
        // =========================================================

        CubicCurve cable =
                new CubicCurve(
                        150,
                        430,
                        150,
                        565,
                        205,
                        575,
                        260,
                        510
                );

        cable.setFill(
                Color.TRANSPARENT
        );

        cable.setStroke(
                Color.web(
                        "#3BD9AE"
                )
        );

        cable.setStrokeWidth(5);

        root.getChildren().add(
                cable
        );

        CubicCurve cableGlow =
                new CubicCurve(
                        150,
                        430,
                        150,
                        565,
                        205,
                        575,
                        260,
                        510
                );

        cableGlow.setFill(
                Color.TRANSPARENT
        );

        cableGlow.setStroke(
                Color.web(
                        "#52FFE0",
                        0.25
                )
        );

        cableGlow.setStrokeWidth(13);

        cableGlow.setEffect(
                new Glow(
                        0.8
                )
        );

        root.getChildren().add(
                cableGlow
        );

        // =========================================================
        // ENERGY DOTS THROUGH CABLE
        // =========================================================

        for (int i = 0; i < 6; i++) {

            Circle energy =
                    new Circle(
                            4,
                            Color.web(
                                    "#A2FFED"
                            )
                    );

            energy.setEffect(
                    new Glow(
                            1
                    )
            );

            root.getChildren().add(
                    energy
            );

            PathTransition path =
                    new PathTransition(
                            Duration.seconds(
                                    2.1
                            ),
                            cable,
                            energy
                    );

            path.setDelay(
                    Duration.millis(
                            i * 350
                    )
            );

            path.setCycleCount(
                    Animation.INDEFINITE
            );

            path.play();
        }

        // =========================================================
        // CHARGING RINGS
        // =========================================================

        Circle ring1 =
                new Circle(
                        350,
                        490,
                        115
                );

        ring1.setFill(
                Color.TRANSPARENT
        );

        ring1.setStroke(
                Color.web(
                        "#42E7B6",
                        0.16
                )
        );

        ring1.setStrokeWidth(2);

        Circle ring2 =
                new Circle(
                        350,
                        490,
                        145
                );

        ring2.setFill(
                Color.TRANSPARENT
        );

        ring2.setStroke(
                Color.web(
                        "#42E7B6",
                        0.08
                )
        );

        ring2.setStrokeWidth(1.5);

        root.getChildren().addAll(
                ring1,
                ring2
        );

        RotateTransition ringRotate =
                new RotateTransition(
                        Duration.seconds(
                                9
                        ),
                        ring1
                );

        ringRotate.setByAngle(
                360
        );

        ringRotate.setCycleCount(
                Animation.INDEFINITE
        );

        ringRotate.play();

        RotateTransition ringRotate2 =
                new RotateTransition(
                        Duration.seconds(
                                13
                        ),
                        ring2
                );

        ringRotate2.setByAngle(
                -360
        );

        ringRotate2.setCycleCount(
                Animation.INDEFINITE
        );

        ringRotate2.play();

       
        // =========================================================
        // BOTTOM STATUS
        // =========================================================

        Label bottomText =
                new Label(
                        "CLEAN ENERGY   •   SMART MOBILITY   •   FUTURE"
                );

        bottomText.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        bottomText.setTextFill(
                Color.web(
                        "#527A74"
                )
        );

        bottomText.setLayoutX(105);
        bottomText.setLayoutY(840);

        root.getChildren().add(
                bottomText
        );

        // =========================================================
        // RIGHT LOGIN CARD
        // =========================================================

        Rectangle card =
                new Rectangle(
                        700,
                        55,
                        600,
                        790
                );

        card.setArcWidth(32);
        card.setArcHeight(32);

        card.setFill(
                Color.web(
                        "#081519",
                        0.985
                )
        );

        card.setStroke(
                Color.web(
                        "#4EE5C2",
                        0.30
                )
        );

        card.setStrokeWidth(1.5);

        card.setEffect(
                new DropShadow(
                        35,
                        Color.web(
                                "#00FFD0",
                                0.18
                        )
                )
        );

        root.getChildren().add(
                card
        );

        // =========================================================
        // CARD TOP LINE
        // =========================================================

        Rectangle cardTopGlow =
                new Rectangle(
                        735,
                        55,
                        530,
                        2
                );

        cardTopGlow.setFill(
                new LinearGradient(
                        0,
                        0,
                        1,
                        0,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.TRANSPARENT
                        ),
                        new Stop(
                                0.5,
                                Color.web(
                                        "#5BFFE0"
                                )
                        ),
                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        cardTopGlow.setEffect(
                new Glow(
                        0.8
                )
        );

        root.getChildren().add(
                cardTopGlow
        );

        // =========================================================
        // CARD ICON
        // =========================================================

        Circle loginIconCircle =
                new Circle(
                        755,
                        122,
                        29
                );

        loginIconCircle.setFill(
                Color.web(
                        "#143E38"
                )
        );

        loginIconCircle.setStroke(
                Color.web(
                        "#55E9C3"
                )
        );

        loginIconCircle.setStrokeWidth(1.5);

        root.getChildren().add(
                loginIconCircle
        );

        Label loginIcon =
                new Label(
                        "⚡"
                );

        loginIcon.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        loginIcon.setTextFill(
                Color.web(
                        "#6DFFE0"
                )
        );

        loginIcon.setLayoutX(744);
        loginIcon.setLayoutY(105);

        root.getChildren().add(
                loginIcon
        );

        // =========================================================
        // WELCOME
        // =========================================================

        Label welcome =
                new Label(
                        "WELCOME BACK"
                );

        welcome.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        31
                )
        );

        welcome.setTextFill(
                Color.WHITE
        );

        welcome.setLayoutX(805);
        welcome.setLayoutY(88);

        root.getChildren().add(
                welcome
        );

        Label loginSubtitle =
                new Label(
                        "Continue your smart charging journey"
                );

        loginSubtitle.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        loginSubtitle.setTextFill(
                Color.web(
                        "#7E9A97"
                )
        );

        loginSubtitle.setLayoutX(807);
        loginSubtitle.setLayoutY(132);

        root.getChildren().add(
                loginSubtitle
        );

        // =========================================================
        // EMAIL LABEL
        // =========================================================

        Label emailLabel =
                new Label(
                        "EMAIL ADDRESS"
                );

        styleLabel(
                emailLabel
        );

        emailLabel.setLayoutX(755);
        emailLabel.setLayoutY(195);

        root.getChildren().add(
                emailLabel
        );

        // =========================================================
        // EMAIL FIELD
        // =========================================================

        TextField emailField =
                new TextField();

        emailField.setPromptText(
                "Enter your email"
        );

        styleField(
                emailField
        );

        emailField.setLayoutX(755);
        emailField.setLayoutY(220);

        emailField.setPrefWidth(490);
        emailField.setPrefHeight(52);

        root.getChildren().add(
                emailField
        );

        // =========================================================
        // PASSWORD LABEL
        // =========================================================

        Label passwordLabel =
                new Label(
                        "PASSWORD"
                );

        styleLabel(
                passwordLabel
        );

        passwordLabel.setLayoutX(755);
        passwordLabel.setLayoutY(300);

        root.getChildren().add(
                passwordLabel
        );

        // =========================================================
        // PASSWORD FIELD
        // =========================================================

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Enter your password"
        );

        styleField(
                passwordField
        );

        passwordField.setLayoutX(755);
        passwordField.setLayoutY(325);

        passwordField.setPrefWidth(490);
        passwordField.setPrefHeight(52);

        // =========================================================
        // VISIBLE PASSWORD FIELD
        // =========================================================

        TextField visiblePassword =
                new TextField();

        visiblePassword.setPromptText(
                "Enter your password"
        );

        styleField(
                visiblePassword
        );

        visiblePassword.setLayoutX(755);
        visiblePassword.setLayoutY(325);

        visiblePassword.setPrefWidth(490);
        visiblePassword.setPrefHeight(52);

        visiblePassword.setVisible(false);

        visiblePassword.textProperty().bindBidirectional(
                passwordField.textProperty()
        );

        root.getChildren().addAll(
                passwordField,
                visiblePassword
        );

        // =========================================================
        // SHOW PASSWORD CHECKBOX
        // =========================================================

        CheckBox showPassword =
                new CheckBox(
                        "Show password"
                );

        showPassword.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        10
                )
        );

        showPassword.setTextFill(
                Color.web(
                        "#71908B"
                )
        );

        showPassword.setLayoutX(755);
        showPassword.setLayoutY(390);

        showPassword.setStyle(
                "-fx-cursor: hand;"
        );

        root.getChildren().add(
                showPassword
        );

        showPassword.setOnAction(
                event -> {

                    boolean visible =
                            showPassword.isSelected();

                    passwordField.setVisible(
                            !visible
                    );

                    visiblePassword.setVisible(
                            visible
                    );
                }
        );

        // =========================================================
        // FORGOT PASSWORD
        // =========================================================

        Button forgotPassword =
                new Button(
                        "Forgot Password?"
                );

        forgotPassword.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        forgotPassword.setTextFill(
                Color.web(
                        "#64EAC7"
                )
        );

        forgotPassword.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 2;"
        );

        forgotPassword.setLayoutX(1135);
        forgotPassword.setLayoutY(388);

        root.getChildren().add(
                forgotPassword
        );

        forgotPassword.setOnMouseEntered(
                event ->
                        forgotPassword.setTextFill(
                                Color.WHITE
                        )
        );

        forgotPassword.setOnMouseExited(
                event ->
                        forgotPassword.setTextFill(
                                Color.web(
                                        "#64EAC7"
                                )
                        )
        );

        forgotPassword.setOnAction(
                event ->
                        showForgotPasswordDialog(
                                stage
                        )
        );

        // =========================================================
        // MESSAGE
        // =========================================================

        Label message =
                new Label();

        message.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        message.setWrapText(
                true
        );

        message.setPrefWidth(
                490
        );

        message.setLayoutX(755);
        message.setLayoutY(420);

        root.getChildren().add(
                message
        );

        // =========================================================
        // LOGIN BUTTON
        // =========================================================

        Button loginButton =
                new Button(
                        "⚡   LOGIN TO SMART EV"
                );

        loginButton.setPrefWidth(
                490
        );

        loginButton.setPrefHeight(
                55
        );

        loginButton.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        loginButton.setTextFill(
                Color.web(
                        "#03110E"
                )
        );

        loginButton.setStyle(
                "-fx-background-color: #55E9C2;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;"
        );

        loginButton.setLayoutX(755);
        loginButton.setLayoutY(465);

        root.getChildren().add(
                loginButton
        );

        // =========================================================
        // LOGIN BUTTON HOVER
        // =========================================================

        loginButton.setOnMouseEntered(
                event -> {

                    loginButton.setStyle(
                            "-fx-background-color: #8BFFE3;" +
                            "-fx-background-radius: 12;" +
                            "-fx-cursor: hand;"
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    loginButton
                            );

                    scale.setToX(1.02);
                    scale.setToY(1.02);

                    scale.play();
                }
        );

        loginButton.setOnMouseExited(
                event -> {

                    loginButton.setStyle(
                            "-fx-background-color: #55E9C2;" +
                            "-fx-background-radius: 12;" +
                            "-fx-cursor: hand;"
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    loginButton
                            );

                    scale.setToX(1);
                    scale.setToY(1);

                    scale.play();
                }
        );

        // =========================================================
        // LOGIN FUNCTIONALITY
        // =========================================================

        loginButton.setOnAction(
                event -> {

                    String email =
                            emailField
                                    .getText()
                                    .trim();

                    String password =
                            passwordField
                                    .getText();

                    if (
                            email.isEmpty()
                                    ||
                            password.isEmpty()
                    ) {

                        message.setTextFill(
                                Color.LIGHTCORAL
                        );

                        message.setText(
                                "Please enter email and password."
                        );

                        return;
                    }

                    try {

                        User user =
                                userService.loginUser(
                                        email,
                                        password
                                );

                        message.setTextFill(
                                Color.LIGHTGREEN
                        );

                        message.setText(
                                "Login successful! Welcome, "
                                        + user.getName()
                        );

                        System.out.println(
                                "GUI Login successful. User ID: "
                                        + user.getUserId()
                                        + " | Role: "
                                        + user.getRole()
                        );

                        // =================================================
                        // ADMIN
                        // =================================================

                        if (
                                "ADMIN".equalsIgnoreCase(
                                        user.getRole()
                                )
                        ) {

                            AdminDashboardView
                                    adminDashboard =
                                    new AdminDashboardView(
                                            user
                                    );

                            adminDashboard.show(
                                    stage
                            );
                        }

                        // =================================================
                        // NORMAL USER
                        // =================================================

                        else {

                            DashboardView
                                    dashboardView =
                                    new DashboardView(
                                            user
                                    );

                            dashboardView.show(
                                    stage
                            );
                        }

                    } catch (
                            InvalidLoginException e
                    ) {

                        message.setTextFill(
                                Color.LIGHTCORAL
                        );

                        message.setText(
                                e.getMessage()
                        );
                    }
                }
        );

        // =========================================================
        // REGISTER BUTTON
        // =========================================================

        Button registerButton =
                new Button(
                        "CREATE NEW ACCOUNT   →"
                );

        registerButton.setPrefWidth(
                490
        );

        registerButton.setPrefHeight(
                48
        );

        registerButton.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        registerButton.setTextFill(
                Color.web(
                        "#69EAC7"
                )
        );

        registerButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: #285A52;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        registerButton.setLayoutX(755);
        registerButton.setLayoutY(540);

        root.getChildren().add(
                registerButton
        );

        registerButton.setOnMouseEntered(
                event -> {

                    registerButton.setStyle(
                            "-fx-background-color: #12332E;" +
                            "-fx-border-color: #63EAC6;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
                    );
                }
        );

        registerButton.setOnMouseExited(
                event -> {

                    registerButton.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: #285A52;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
                    );
                }
        );

        registerButton.setOnAction(
                event -> {

                    RegistrationView
                            registrationView =
                            new RegistrationView();

                    registrationView.show(
                            stage
                    );
                }
        );

        // =========================================================
        // SECURITY INFORMATION
        // =========================================================

        Label security =
                new Label(
                        "🔒  SECURE ACCESS"
                                +
                                "     •     "
                                +
                                "CLEAN ENERGY"
                                +
                                "     •     "
                                +
                                "SMART MOBILITY"
                );

        security.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        security.setTextFill(
                Color.web(
                        "#55706D"
                )
        );

        security.setLayoutX(805);
        security.setLayoutY(620);

        root.getChildren().add(
                security
        );

        // =========================================================
        // CARD STATUS FOOTER
        // =========================================================

        Label footerStatus =
                new Label(
                        "LETS MAKE THE WORLD A BETTER PLACE"
                );

        footerStatus.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        footerStatus.setTextFill(
                Color.web(
                        "#3E625D"
                )
        );

        footerStatus.setLayoutX(805);
        footerStatus.setLayoutY(650);

        root.getChildren().add(
                footerStatus
        );

        // =========================================================
        // LOGIN CARD ENTRANCE
        // =========================================================

        card.setTranslateX(
                90
        );

        card.setOpacity(
                0
        );

        FadeTransition cardFade =
                new FadeTransition(
                        Duration.millis(850),
                        card
                );

        cardFade.setToValue(
                1
        );

        TranslateTransition cardSlide =
                new TranslateTransition(
                        Duration.millis(900),
                        card
                );

        cardSlide.setToX(
                0
        );

        ParallelTransition cardEntrance =
                new ParallelTransition(
                        cardFade,
                        cardSlide
                );

        cardEntrance.setDelay(
                Duration.millis(200)
        );

        cardEntrance.play();

        // =========================================================
        // RIGHT SIDE CONTENT ENTRANCE
        // =========================================================

        Node[] rightElements = {
                cardTopGlow,
                loginIconCircle,
                loginIcon,
                welcome,
                loginSubtitle,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                visiblePassword,
                showPassword,
                forgotPassword,
                message,
                loginButton,
                registerButton,
                security,
                footerStatus
        };

        for (int i = 0; i < rightElements.length; i++) {

            Node node =
                    rightElements[i];

            if (node == card) {
                continue;
            }

            node.setOpacity(
                    0
            );

            fadeIn(
                    node,
                    400 + i * 45
            );
        }

        // =========================================================
        // LEFT ENTRANCE
        // =========================================================

        smartEV.setOpacity(0);
        tagline.setOpacity(0);
        sdg7.setOpacity(0);
        sdg9.setOpacity(0);
        sdg11.setOpacity(0);
        cleanEnergy.setOpacity(0);
        description.setOpacity(0);
        chargingStation.setOpacity(0);
        car.setOpacity(0);
        cable.setOpacity(0);
        cableGlow.setOpacity(0);
        ring1.setOpacity(0);
        ring2.setOpacity(0);
        bottomText.setOpacity(0);

        fadeIn(
                smartEV,
                200
        );

        fadeIn(
                tagline,
                300
        );

        fadeIn(
                sdg7,
                400
        );

        fadeIn(
                sdg9,
                480
        );

        fadeIn(
                sdg11,
                560
        );

        fadeIn(
                cleanEnergy,
                650
        );

        fadeIn(
                description,
                720
        );

        fadeIn(
                chargingStation,
                800
        );

        fadeIn(
                car,
                950
        );

        fadeIn(
                cable,
                1050
        );

        fadeIn(
                cableGlow,
                1050
        );

        fadeIn(
                ring1,
                1150
        );

        fadeIn(
                ring2,
                1250
        );

       

        fadeIn(
                bottomText,
                1500
        );

        // =========================================================
        // SCENE
        // =========================================================

        Scene scene =
                new Scene(
                        root
                );

        stage.setTitle(
                "Smart EV Charging System - Login"
        );

        stage.setScene(
                scene
        );

        // =========================================================
        // TRUE SCREEN FIT
        // =========================================================

        Rectangle2D bounds =
                Screen
                        .getPrimary()
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

    // =============================================================
    // SDG BADGE
    // =============================================================

    private Label createSDGBadge(
            String title,
            String subtitle,
            double x,
            double y) {

        Label badge =
                new Label(
                        title
                                +
                                "\n"
                                +
                                subtitle
                );

        badge.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        8
                )
        );

        badge.setTextFill(
                Color.web(
                        "#63FFD6"
                )
        );

        badge.setAlignment(
                javafx.geometry.Pos.CENTER
        );

        badge.setPrefWidth(
                115
        );

        badge.setPrefHeight(
                40
        );

        badge.setLayoutX(
                x
        );

        badge.setLayoutY(
                y
        );

        badge.setStyle(
                "-fx-background-color: rgba(10,35,35,0.85);" +
                "-fx-border-color: rgba(78,229,194,0.35);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        return badge;
    }


    // =============================================================
    // LABEL STYLE
    // =============================================================

    private void styleLabel(
            Label label) {

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        label.setTextFill(
                Color.web(
                        "#62EAC5"
                )
        );
    }

    // =============================================================
    // FIELD STYLE
    // =============================================================

    private void styleField(
            TextInputControl field) {

        field.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        field.setStyle(
                "-fx-background-color: #071316;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #58736F;" +
                "-fx-border-color: #254842;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 0 15 0 15;"
        );

        field.focusedProperty().addListener(
                (
                        observable,
                        oldValue,
                        focused
                ) -> {

                    if (focused) {

                        field.setStyle(
                                "-fx-background-color: #0B1C20;" +
                                "-fx-text-fill: white;" +
                                "-fx-prompt-text-fill: #58736F;" +
                                "-fx-border-color: #55E9C2;" +
                                "-fx-border-width: 1.5;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 0 15 0 15;"
                        );

                    } else {

                        field.setStyle(
                                "-fx-background-color: #071316;" +
                                "-fx-text-fill: white;" +
                                "-fx-prompt-text-fill: #58736F;" +
                                "-fx-border-color: #254842;" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 0 15 0 15;"
                        );
                    }
                }
        );
    }

    // =============================================================
    // FORGOT PASSWORD WINDOW
    // =============================================================

    private void showForgotPasswordDialog(
            Stage owner) {

        Stage dialog =
                new Stage();

        dialog.setTitle(
                "Smart EV - Password Recovery"
        );

        dialog.initOwner(
                owner
        );

        Pane root =
                new Pane();

        root.setPrefSize(
                500,
                430
        );

        root.setStyle(
                "-fx-background-color: #061114;"
        );

        // =========================================================
        // BACKGROUND
        // =========================================================

        Rectangle background =
                new Rectangle(
                        0,
                        0,
                        500,
                        430
                );

        background.setFill(
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
                                        "#0A2425"
                                )
                        ),
                        new Stop(
                                1,
                                Color.web(
                                        "#020708"
                                )
                        )
                )
        );

        root.getChildren().add(
                background
        );

        // =========================================================
        // TITLE
        // =========================================================

        Label title =
                new Label(
                        "PASSWORD RECOVERY"
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

        title.setLayoutX(
                40
        );

        title.setLayoutY(
                35
        );

        root.getChildren().add(
                title
        );

        Label subtitle =
                new Label(
                        "Reset your Smart EV account password"
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        subtitle.setTextFill(
                Color.web(
                        "#75918D"
                )
        );

        subtitle.setLayoutX(
                42
        );

        subtitle.setLayoutY(
                72
        );

        root.getChildren().add(
                subtitle
        );

        // =========================================================
        // EMAIL
        // =========================================================

        Label emailLabel =
                new Label(
                        "REGISTERED EMAIL"
                );

        styleLabel(
                emailLabel
        );

        emailLabel.setLayoutX(
                42
        );

        emailLabel.setLayoutY(
                115
        );

        root.getChildren().add(
                emailLabel
        );

        TextField email =
                new TextField();

        email.setPromptText(
                "Enter registered email"
        );

        styleField(
                email
        );

        email.setLayoutX(
                42
        );

        email.setLayoutY(
                140
        );

        email.setPrefWidth(
                415
        );

        email.setPrefHeight(
                48
        );

        root.getChildren().add(
                email
        );

        // =========================================================
        // NEW PASSWORD
        // =========================================================

        Label newPasswordLabel =
                new Label(
                        "NEW PASSWORD"
                );

        styleLabel(
                newPasswordLabel
        );

        newPasswordLabel.setLayoutX(
                42
        );

        newPasswordLabel.setLayoutY(
                205
        );

        root.getChildren().add(
                newPasswordLabel
        );

        PasswordField newPassword =
                new PasswordField();

        newPassword.setPromptText(
                "Enter new password"
        );

        styleField(
                newPassword
        );

        newPassword.setLayoutX(
                42
        );

        newPassword.setLayoutY(
                230
        );

        newPassword.setPrefWidth(
                415
        );

        newPassword.setPrefHeight(
                48
        );

        root.getChildren().add(
                newPassword
        );

        // =========================================================
        // MESSAGE
        // =========================================================

        Label message =
                new Label();

        message.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        message.setWrapText(
                true
        );

        message.setPrefWidth(
                415
        );

        message.setLayoutX(
                42
        );

        message.setLayoutY(
                292
        );

        root.getChildren().add(
                message
        );

        // =========================================================
        // RESET BUTTON
        // =========================================================

        Button reset =
                new Button(
                        "RESET PASSWORD"
                );

        reset.setPrefWidth(
                200
        );

        reset.setPrefHeight(
                45
        );

        reset.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        reset.setTextFill(
                Color.web(
                        "#03110E"
                )
        );

        reset.setStyle(
                "-fx-background-color: #55E9C2;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        reset.setLayoutX(
                42
        );

        reset.setLayoutY(
                345
        );

        root.getChildren().add(
                reset
        );

        // =========================================================
        // CANCEL BUTTON
        // =========================================================

        Button cancel =
                new Button(
                        "CANCEL"
                );

        cancel.setPrefWidth(
                150
        );

        cancel.setPrefHeight(
                45
        );

        cancel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        cancel.setTextFill(
                Color.web(
                        "#69EAC7"
                )
        );

        cancel.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: #285A52;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        cancel.setLayoutX(
                260
        );

        cancel.setLayoutY(
                345
        );

        root.getChildren().add(
                cancel
        );

        cancel.setOnAction(
                event ->
                        dialog.close()
        );

        // =========================================================
        // RESET FUNCTION
        // =========================================================

        reset.setOnAction(
                event -> {

                    String enteredEmail =
                            email
                                    .getText()
                                    .trim();

                    String password =
                            newPassword
                                    .getText();

                    if (
                            enteredEmail.isEmpty()
                                    ||
                            password.isEmpty()
                    ) {

                        message.setTextFill(
                                Color.LIGHTCORAL
                        );

                        message.setText(
                                "Please enter your email and new password."
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

                    String checkSql =
                            "SELECT user_id FROM users WHERE email = ?";

                    String updateSql =
                            "UPDATE users SET password = ? WHERE email = ?";

                    try (
                            Connection con =
                                    DBConnection.getConnection();

                            PreparedStatement check =
                                    con.prepareStatement(
                                            checkSql
                                    )
                    ) {

                        check.setString(
                                1,
                                enteredEmail
                        );

                        try (
                                ResultSet rs =
                                        check.executeQuery()
                        ) {

                            if (!rs.next()) {

                                message.setTextFill(
                                        Color.LIGHTCORAL
                                );

                                message.setText(
                                        "No account found with this email."
                                );

                                return;
                            }
                        }

                        try (
                                PreparedStatement update =
                                        con.prepareStatement(
                                                updateSql
                                        )
                        ) {

                            update.setString(
                                    1,
                                    password
                            );

                            update.setString(
                                    2,
                                    enteredEmail
                            );

                            int rows =
                                    update.executeUpdate();

                            if (rows > 0) {

                                message.setTextFill(
                                        Color.LIGHTGREEN
                                );

                                message.setText(
                                        "Password reset successfully. "
                                                +
                                                "You can now login."
                                );

                                PauseTransition pause =
                                        new PauseTransition(
                                                Duration.seconds(
                                                        1.5
                                                )
                                        );

                                pause.setOnFinished(
                                        e ->
                                                dialog.close()
                                );

                                pause.play();

                            } else {

                                message.setTextFill(
                                        Color.LIGHTCORAL
                                );

                                message.setText(
                                        "Password reset failed."
                                );
                            }
                        }

                    } catch (
                            Exception e
                    ) {

                        message.setTextFill(
                                Color.LIGHTCORAL
                        );

                        message.setText(
                                "Unable to reset password: "
                                        +
                                        e.getMessage()
                        );

                        e.printStackTrace();
                    }
                }
        );

        Scene scene =
                new Scene(
                        root
                );

        dialog.setScene(
                scene
        );

        dialog.setResizable(
                false
        );

        dialog.show();
    }

    // =============================================================
    // FADE HELPER
    // =============================================================

    private void fadeIn(
            javafx.scene.Node node,
            int delay) {

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(
                                700
                        ),
                        node
                );

        fade.setDelay(
                Duration.millis(
                        delay
                )
        );

        fade.setToValue(
                1
        );

        fade.play();
    }
}