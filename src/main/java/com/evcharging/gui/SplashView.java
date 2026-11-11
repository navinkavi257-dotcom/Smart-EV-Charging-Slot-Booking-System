package com.evcharging.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import java.util.Random;

public class SplashView {

    private final Random random = new Random();

    private static final double W = 1440;
    private static final double H = 900;

    public void show(Stage stage) {

        // ============================================================
        // ROOT
        // ============================================================

        Pane root = new Pane();

        root.setPrefSize(W, H);

        root.setStyle(
                "-fx-background-color: #020509;"
        );

        // ============================================================
        // BACKGROUND
        // ============================================================

        Rectangle background =
                new Rectangle(W, H);

        background.setFill(
                new RadialGradient(
                        0,
                        0,
                        0.5,
                        0.50,
                        0.45,
                        true,
                        CycleMethod.NO_CYCLE,

                        new Stop(
                                0,
                                Color.web("#0d342f")
                        ),

                        new Stop(
                                0.35,
                                Color.web("#071c22")
                        ),

                        new Stop(
                                0.70,
                                Color.web("#031017")
                        ),

                        new Stop(
                                1,
                                Color.web("#020509")
                        )
                )
        );

        root.getChildren().add(background);

        // ============================================================
        // LARGE ATMOSPHERIC GLOWS
        // ============================================================

        Circle leftGlow =
                new Circle(
                        300,
                        Color.rgb(
                                30,
                                255,
                                180,
                                0.035
                        )
                );

        leftGlow.setCenterX(270);
        leftGlow.setCenterY(420);

        root.getChildren().add(leftGlow);

        Circle rightGlow =
                new Circle(
                        330,
                        Color.rgb(
                                30,
                                200,
                                255,
                                0.035
                        )
                );

        rightGlow.setCenterX(1160);
        rightGlow.setCenterY(390);

        root.getChildren().add(rightGlow);

        // ============================================================
        // GRID
        // ============================================================

        Pane grid =
                new Pane();

        for (int x = 0; x <= W; x += 60) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            H
                    );

            line.setStroke(
                    Color.rgb(
                            40,
                            255,
                            190,
                            0.045
                    )
            );

            line.setStrokeWidth(1);

            grid.getChildren().add(line);
        }

        for (int y = 0; y <= H; y += 60) {

            Line line =
                    new Line(
                            0,
                            y,
                            W,
                            y
                    );

            line.setStroke(
                    Color.rgb(
                            40,
                            220,
                            255,
                            0.04
                    )
            );

            line.setStrokeWidth(1);

            grid.getChildren().add(line);
        }

        root.getChildren().add(grid);

        // ============================================================
        // BACKGROUND PARTICLES
        // ============================================================

        for (int i = 0; i < 85; i++) {

            Circle particle =
                    new Circle(
                            0.8 + random.nextDouble() * 2.2
                    );

            if (i % 3 == 0) {
                particle.setFill(Color.LIGHTGREEN);
            } else if (i % 3 == 1) {
                particle.setFill(Color.CYAN);
            } else {
                particle.setFill(Color.WHITE);
            }

            particle.setOpacity(
                    0.08 + random.nextDouble() * 0.45
            );

            particle.setLayoutX(
                    random.nextDouble() * W
            );

            particle.setLayoutY(
                    random.nextDouble() * H
            );

            root.getChildren().add(particle);

            TranslateTransition floatAnimation =
                    new TranslateTransition(
                            Duration.seconds(
                                    3.0 + random.nextDouble() * 4.0
                            ),
                            particle
                    );

            floatAnimation.setFromY(0);

            floatAnimation.setToY(
                    -40 - random.nextDouble() * 90
            );

            floatAnimation.setCycleCount(
                    Animation.INDEFINITE
            );

            floatAnimation.setAutoReverse(true);

            floatAnimation.play();

            FadeTransition fade =
                    new FadeTransition(
                            Duration.seconds(
                                    2.0 + random.nextDouble() * 3.0
                            ),
                            particle
                    );

            fade.setFromValue(
                    0.10
            );

            fade.setToValue(
                    0.60
            );

            fade.setCycleCount(
                    Animation.INDEFINITE
            );

            fade.setAutoReverse(true);

            fade.play();
        }

        // ============================================================
        // TOP SYSTEM STATUS
        // ============================================================

        Circle onlineDot =
                new Circle(
                        5,
                        Color.LIGHTGREEN
                );

        onlineDot.setCenterX(70);
        onlineDot.setCenterY(65);

        onlineDot.setEffect(
                new Glow(0.8)
        );

        root.getChildren().add(onlineDot);

        Label systemStatus =
                new Label(
                        "SMART EV  /  SYSTEM INITIALIZING"
                );

        systemStatus.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        systemStatus.setTextFill(
                Color.rgb(
                        170,
                        255,
                        220,
                        0.75
                )
        );

        systemStatus.setLayoutX(85);
        systemStatus.setLayoutY(57);

        root.getChildren().add(systemStatus);

        // ============================================================
        // CENTRAL ENERGY CORE
        // ============================================================

        Circle outerGlow =
                new Circle(175);

        outerGlow.setCenterX(720);
        outerGlow.setCenterY(410);

        outerGlow.setFill(
                Color.rgb(
                        30,
                        255,
                        190,
                        0.025
                )
        );

        outerGlow.setStroke(
                Color.rgb(
                        60,
                        255,
                        200,
                        0.08
                )
        );

        outerGlow.setStrokeWidth(2);

        root.getChildren().add(outerGlow);

        // ============================================================
        // OUTER ROTATING RING
        // ============================================================

        Circle outerRing =
                new Circle(145);

        outerRing.setCenterX(720);
        outerRing.setCenterY(410);

        outerRing.setFill(
                Color.TRANSPARENT
        );

        outerRing.setStroke(
                Color.rgb(
                        80,
                        255,
                        210,
                        0.18
                )
        );

        outerRing.setStrokeWidth(2);

        root.getChildren().add(outerRing);

        RotateTransition outerRotation =
                new RotateTransition(
                        Duration.seconds(8),
                        outerRing
                );

        outerRotation.setByAngle(360);

        outerRotation.setCycleCount(
                Animation.INDEFINITE
        );

        outerRotation.play();

        // ============================================================
        // INNER RING
        // ============================================================

        Circle innerRing =
                new Circle(90);

        innerRing.setCenterX(720);
        innerRing.setCenterY(410);

        innerRing.setFill(
                Color.TRANSPARENT
        );

        innerRing.setStroke(
                Color.rgb(
                        40,
                        220,
                        255,
                        0.35
                )
        );

        innerRing.setStrokeWidth(2);

        root.getChildren().add(innerRing);

        RotateTransition innerRotation =
                new RotateTransition(
                        Duration.seconds(4),
                        innerRing
                );

        innerRotation.setByAngle(-360);

        innerRotation.setCycleCount(
                Animation.INDEFINITE
        );

        innerRotation.play();

        // ============================================================
        // CORE
        // ============================================================

        Circle core =
                new Circle(32);

        core.setCenterX(720);
        core.setCenterY(410);

        core.setFill(
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
                                Color.WHITE
                        ),

                        new Stop(
                                0.25,
                                Color.LIGHTGREEN
                        ),

                        new Stop(
                                0.60,
                                Color.CYAN
                        ),

                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        core.setEffect(
                new Glow(1.0)
        );

        root.getChildren().add(core);

        ScaleTransition corePulse =
                new ScaleTransition(
                        Duration.seconds(0.75),
                        core
                );

        corePulse.setFromX(0.75);
        corePulse.setFromY(0.75);

        corePulse.setToX(1.25);
        corePulse.setToY(1.25);

        corePulse.setCycleCount(
                Animation.INDEFINITE
        );

        corePulse.setAutoReverse(true);

        corePulse.play();

        // ============================================================
        // LIGHTNING SYMBOL
        // ============================================================

        Polygon lightning =
                new Polygon(
                        0, 0,
                        30, 0,
                        14, 28,
                        40, 28,
                        0, 82,
                        10, 45,
                        -14, 45
                );

        lightning.setLayoutX(706);
        lightning.setLayoutY(368);

        lightning.setFill(
                Color.WHITE
        );

        lightning.setEffect(
                new Glow(1.2)
        );

        root.getChildren().add(lightning);

        // ============================================================
        // ENERGY CIRCUIT PATH
        // ============================================================

        Line leftCircuit =
                new Line(
                        80,
                        410,
                        570,
                        410
                );

        leftCircuit.setStroke(
                Color.rgb(
                        65,
                        255,
                        195,
                        0.32
                )
        );

        leftCircuit.setStrokeWidth(3);

        Line rightCircuit =
                new Line(
                        870,
                        410,
                        1360,
                        410
                );

        rightCircuit.setStroke(
                Color.rgb(
                        65,
                        220,
                        255,
                        0.32
                )
        );

        rightCircuit.setStrokeWidth(3);

        root.getChildren().addAll(
                leftCircuit,
                rightCircuit
        );

        // ============================================================
        // CIRCUIT NODES
        // ============================================================

        createCircuitNode(root, 180, 410);
        createCircuitNode(root, 330, 410);
        createCircuitNode(root, 480, 410);

        createCircuitNode(root, 960, 410);
        createCircuitNode(root, 1110, 410);
        createCircuitNode(root, 1260, 410);

        // ============================================================
        // ENERGY PARTICLES - LEFT
        // ============================================================

        for (int i = 0; i < 7; i++) {

            Circle energy =
                    new Circle(
                            4,
                            Color.LIGHTGREEN
                    );

            energy.setEffect(
                    new Glow(1)
            );

            energy.setLayoutX(
                    80 + i * 70
            );

            energy.setLayoutY(410);

            root.getChildren().add(energy);

            TranslateTransition travel =
                    new TranslateTransition(
                            Duration.seconds(2.2),
                            energy
                    );

            travel.setFromX(0);
            travel.setToX(490);

            travel.setDelay(
                    Duration.seconds(i * 0.25)
            );

            travel.setCycleCount(
                    Animation.INDEFINITE
            );

            travel.play();
        }

        // ============================================================
        // ENERGY PARTICLES - RIGHT
        // ============================================================

        for (int i = 0; i < 7; i++) {

            Circle energy =
                    new Circle(
                            4,
                            Color.CYAN
                    );

            energy.setEffect(
                    new Glow(1)
            );

            energy.setLayoutX(
                    870 + i * 70
            );

            energy.setLayoutY(410);

            root.getChildren().add(energy);

            TranslateTransition travel =
                    new TranslateTransition(
                            Duration.seconds(2.2),
                            energy
                    );

            travel.setFromX(0);
            travel.setToX(490);

            travel.setDelay(
                    Duration.seconds(i * 0.25)
            );

            travel.setCycleCount(
                    Animation.INDEFINITE
            );

            travel.play();
        }

        // ============================================================
        // EV CAR
        // ============================================================

        EVCarView evCar = new EVCarView();

        Pane car = evCar.create();

        car.setLayoutX(150);
        car.setLayoutY(525);
        car.setTranslateX(-650);

        root.getChildren().add(car);

        TranslateTransition carMove =
                new TranslateTransition(
                        Duration.seconds(2.6),
                        car
                );

        carMove.setFromX(-650);
        carMove.setToX(0);

        carMove.setDelay(
                Duration.seconds(0.7)
        );

        carMove.setInterpolator(
                Interpolator.EASE_OUT
        );

        carMove.play();

        // ============================================================
        // PREMIUM CHARGING STATION
        // ============================================================

        PremiumChargerView premiumCharger =
                new PremiumChargerView();

        Pane premiumChargerPane =
                premiumCharger.create();

        premiumChargerPane.setScaleX(0.72);
        premiumChargerPane.setScaleY(0.72);

        premiumChargerPane.setLayoutX(1040);
        premiumChargerPane.setLayoutY(170);

        premiumChargerPane.setOpacity(0);

        root.getChildren().add(
                premiumChargerPane
        );

        // Station glow entrance

        FadeTransition chargerFade =
                new FadeTransition(
                        Duration.seconds(1.5),
                        premiumChargerPane
                );

        chargerFade.setDelay(
                Duration.seconds(2.3)
        );

        chargerFade.setFromValue(0);
        chargerFade.setToValue(1);

        chargerFade.play();

        ScaleTransition chargerScale =
                new ScaleTransition(
                        Duration.seconds(1.5),
                        premiumChargerPane
                );

        chargerScale.setDelay(
                Duration.seconds(2.3)
        );

        chargerScale.setFromX(0.55);
        chargerScale.setFromY(0.55);

        chargerScale.setToX(0.72);
        chargerScale.setToY(0.72);

        new ParallelTransition(
                chargerFade,
                chargerScale
        ).play();

        // ============================================================
        // TITLE
        // ============================================================

        Label title =
                new Label(
                        "⚡ SMART EV"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        62
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        title.setLayoutX(520);
        title.setLayoutY(92);

        title.setOpacity(0);

        title.setEffect(
                new Glow(0.65)
        );

        root.getChildren().add(title);

        FadeTransition titleFade =
                new FadeTransition(
                        Duration.seconds(1.2),
                        title
                );

        titleFade.setDelay(
                Duration.seconds(3.4)
        );

        titleFade.setFromValue(0);
        titleFade.setToValue(1);

        ScaleTransition titleScale =
                new ScaleTransition(
                        Duration.seconds(1.2),
                        title
                );

        titleScale.setDelay(
                Duration.seconds(3.4)
        );

        titleScale.setFromX(0.85);
        titleScale.setFromY(0.85);

        titleScale.setToX(1);
        titleScale.setToY(1);

        new ParallelTransition(
                titleFade,
                titleScale
        ).play();

        // ============================================================
        // SUBTITLE
        // ============================================================

        Label subtitle =
                new Label(
                        "CHARGING INTELLIGENCE  •  SMART MOBILITY"
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        15
                )
        );

        subtitle.setTextFill(
                Color.rgb(
                        205,
                        220,
                        225,
                        0.9
                )
        );

        subtitle.setLayoutX(525);
        subtitle.setLayoutY(170);

        subtitle.setOpacity(0);

        root.getChildren().add(subtitle);

        FadeTransition subtitleFade =
                new FadeTransition(
                        Duration.seconds(1),
                        subtitle
                );

        subtitleFade.setDelay(
                Duration.seconds(4.1)
        );

        subtitleFade.setFromValue(0);
        subtitleFade.setToValue(1);

        subtitleFade.play();

        // ============================================================
        // ENERGY TRANSFER LABEL
        // ============================================================

        Label energyLabel =
                new Label(
                        "ENERGY TRANSFER"
                );

        energyLabel.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        energyLabel.setTextFill(
                Color.LIGHTGREEN
        );

        energyLabel.setLayoutX(650);
        energyLabel.setLayoutY(650);

        energyLabel.setOpacity(0);

        root.getChildren().add(
                energyLabel
        );

        FadeTransition energyLabelFade =
                new FadeTransition(
                        Duration.seconds(1),
                        energyLabel
                );

        energyLabelFade.setDelay(
                Duration.seconds(3.8)
        );

        energyLabelFade.setFromValue(0);
        energyLabelFade.setToValue(1);

        energyLabelFade.play();

        // ============================================================
        // BATTERY PERCENTAGE
        // ============================================================

        Label battery =
                new Label(
                        "ENERGY TRANSFER   0%"
                );

        battery.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        battery.setTextFill(
                Color.LIGHTGREEN
        );

        battery.setLayoutX(640);
        battery.setLayoutY(675);

        battery.setOpacity(0);

        root.getChildren().add(
                battery
        );

        FadeTransition batteryFade =
                new FadeTransition(
                        Duration.seconds(1),
                        battery
                );

        batteryFade.setDelay(
                Duration.seconds(3.9)
        );

        batteryFade.setFromValue(0);
        batteryFade.setToValue(1);

        batteryFade.play();

        // ============================================================
        // BATTERY BAR
        // ============================================================

        Rectangle batteryOuter =
                new Rectangle(
                        320,
                        18
                );

        batteryOuter.setLayoutX(560);
        batteryOuter.setLayoutY(705);

        batteryOuter.setArcWidth(8);
        batteryOuter.setArcHeight(8);

        batteryOuter.setFill(
                Color.TRANSPARENT
        );

        batteryOuter.setStroke(
                Color.rgb(
                        120,
                        255,
                        200,
                        0.75
                )
        );

        batteryOuter.setStrokeWidth(2);

        batteryOuter.setOpacity(0);

        root.getChildren().add(
                batteryOuter
        );

        Rectangle batteryLevel =
                new Rectangle(
                        0,
                        10
                );

        batteryLevel.setLayoutX(564);
        batteryLevel.setLayoutY(709);

        batteryLevel.setArcWidth(5);
        batteryLevel.setArcHeight(5);

        batteryLevel.setFill(
                new LinearGradient(
                        0,
                        0,
                        1,
                        0,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.LIGHTGREEN
                        ),
                        new Stop(
                                1,
                                Color.CYAN
                        )
                )
        );

        batteryLevel.setOpacity(0);

        root.getChildren().add(
                batteryLevel
        );

        FadeTransition batteryBarFade =
                new FadeTransition(
                        Duration.seconds(1),
                        batteryOuter
                );

        batteryBarFade.setDelay(
                        Duration.seconds(3.9)
                );

        batteryBarFade.setFromValue(0);
        batteryBarFade.setToValue(1);

        batteryBarFade.play();

        FadeTransition batteryLevelFade =
                new FadeTransition(
                        Duration.seconds(1),
                        batteryLevel
                );

        batteryLevelFade.setDelay(
                Duration.seconds(3.9)
        );

        batteryLevelFade.setFromValue(0);
        batteryLevelFade.setToValue(1);

        batteryLevelFade.play();

        // ============================================================
        // BATTERY ANIMATION
        // ============================================================

        Timeline batteryAnimation =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(4.5),
                                new KeyValue(
                                        batteryLevel.widthProperty(),
                                        312
                                )
                        )
                );

        batteryAnimation.setDelay(
                Duration.seconds(3.9)
        );

        batteryAnimation.play();

        // ============================================================
        // PERCENTAGE ANIMATION
        // ============================================================

        Timeline percentage =
                new Timeline();

        for (int i = 0; i <= 100; i += 5) {

            final int value = i;

            percentage.getKeyFrames().add(
                    new KeyFrame(
                            Duration.seconds(
                                    3.9 + i * 0.045
                            ),
                            event ->
                                    battery.setText(
                                            "ENERGY TRANSFER   "
                                                    + value
                                                    + "%"
                                    )
                    )
            );
        }

        percentage.play();

        // ============================================================
        // FINAL MESSAGE
        // ============================================================

        Label ready =
                new Label(
                        "CHARGING SMARTER.   DRIVING GREENER."
                );

        ready.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        ready.setTextFill(
                Color.LIGHTGREEN
        );

        ready.setLayoutX(555);
        ready.setLayoutY(755);

        ready.setOpacity(0);

        root.getChildren().add(ready);

        FadeTransition readyFade =
                new FadeTransition(
                        Duration.seconds(1),
                        ready
                );

        readyFade.setDelay(
                Duration.seconds(6.0)
        );

        readyFade.setFromValue(0);
        readyFade.setToValue(1);

        readyFade.play();

        // ============================================================
        // SMALL FOOTER
        // ============================================================

        Label footer =
                new Label(
                        "EV INFRASTRUCTURE  •  SMART GRID  •  SUSTAINABLE MOBILITY"
                );

        footer.setFont(
                Font.font(
                        "Arial",
                        10
                )
        );

        footer.setTextFill(
                Color.rgb(
                        150,
                        180,
                        185,
                        0.45
                )
        );

        footer.setLayoutX(520);
        footer.setLayoutY(825);

        root.getChildren().add(footer);

        // ============================================================
        // FINAL TRANSITION TO LOGIN
        // ============================================================

        Timeline openLogin =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(8.5),
                                event -> {

                                    FadeTransition fade =
                                            new FadeTransition(
                                                    Duration.seconds(1.2),
                                                    root
                                            );

                                    fade.setFromValue(1);
                                    fade.setToValue(0);

                                    fade.setOnFinished(
                                            finished -> {

                                                LoginView login =
                                                        new LoginView();

                                                login.show(stage);
                                            }
                                    );

                                    fade.play();
                                }
                        )
                );

        openLogin.play();

        // ============================================================
        // SHOW WINDOW
        // ============================================================

        Rectangle2D bounds =
                Screen.getPrimary().getVisualBounds();

        Scene scene =
                new Scene(
                        root,
                        bounds.getWidth(),
                        bounds.getHeight()
                );

        stage.setTitle(
                "Smart EV Charging System"
        );

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
    }

    // ================================================================
    // CIRCUIT NODE
    // ================================================================

    private void createCircuitNode(
            Pane root,
            double x,
            double y) {

        Circle glow =
                new Circle(
                        10,
                        Color.rgb(
                                50,
                                255,
                                200,
                                0.08
                        )
                );

        glow.setCenterX(x);
        glow.setCenterY(y);

        root.getChildren().add(glow);

        Circle node =
                new Circle(
                        4,
                        Color.LIGHTGREEN
                );

        node.setCenterX(x);
        node.setCenterY(y);

        node.setEffect(
                new Glow(1)
        );

        root.getChildren().add(node);
    }

}

   
       
 