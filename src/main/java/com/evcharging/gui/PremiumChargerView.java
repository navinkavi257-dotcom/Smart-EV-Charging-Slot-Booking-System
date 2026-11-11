package com.evcharging.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PremiumChargerView {

    private final Random random = new Random();

    public Pane create() {

        Pane root = new Pane();

        root.setPrefSize(250, 410);

        // =========================================================
        // CHARGER BODY
        // =========================================================

        Rectangle shadow =
                new Rectangle(
                        55,
                        42,
                        190,
                        330
                );

        shadow.setArcWidth(30);
        shadow.setArcHeight(30);

        shadow.setFill(
                Color.rgb(0, 0, 0, 0.45)
        );

        shadow.setTranslateX(7);
        shadow.setTranslateY(8);

        Rectangle body =
                new Rectangle(
                        55,
                        40,
                        190,
                        330
                );

        body.setArcWidth(30);
        body.setArcHeight(30);

        body.setFill(
                Color.rgb(5, 20, 27)
        );

        body.setStroke(
                Color.rgb(70, 255, 215)
        );

        body.setStrokeWidth(1.5);

        root.getChildren().addAll(
                shadow,
                body
        );

        // =========================================================
        // TOP LIGHT
        // =========================================================

        Circle topLight =
                new Circle(
                        150,
                        62,
                        5
                );

        topLight.setFill(
                Color.rgb(80, 255, 210)
        );

        root.getChildren().add(topLight);

        ScaleTransition topPulse =
                new ScaleTransition(
                        Duration.seconds(1.1),
                        topLight
                );

        topPulse.setFromX(0.7);
        topPulse.setFromY(0.7);
        topPulse.setToX(1.3);
        topPulse.setToY(1.3);
        topPulse.setAutoReverse(true);
        topPulse.setCycleCount(
                ScaleTransition.INDEFINITE
        );

        topPulse.play();

        // =========================================================
        // DISPLAY
        // =========================================================

        Rectangle display =
                new Rectangle(
                        78,
                        90,
                        145,
                        100
                );

        display.setArcWidth(18);
        display.setArcHeight(18);

        display.setFill(
                Color.rgb(2, 12, 17)
        );

        display.setStroke(
                Color.rgb(28, 130, 125)
        );

        display.setStrokeWidth(1.2);

        root.getChildren().add(display);

        Label powerTitle =
                label(
                        "LIVE POWER",
                        9,
                        Color.rgb(110, 170, 175)
                );

        powerTitle.setLayoutX(94);
        powerTitle.setLayoutY(105);

        root.getChildren().add(powerTitle);

        Label power =
                label(
                        "42.6",
                        27,
                        Color.rgb(100, 255, 220)
                );

        power.setLayoutX(94);
        power.setLayoutY(118);

        root.getChildren().add(power);

        Label kw =
                label(
                        "kW",
                        10,
                        Color.LIGHTGRAY
                );

        kw.setLayoutX(163);
        kw.setLayoutY(132);

        root.getChildren().add(kw);

        Label percentage =
                label(
                        "78%",
                        16,
                        Color.LIGHTGREEN
                );

        percentage.setLayoutX(188);
        percentage.setLayoutY(119);

        root.getChildren().add(percentage);

        // =========================================================
        // BATTERY
        // =========================================================

        Rectangle batteryOuter =
                new Rectangle(
                        94,
                        158,
                        108,
                        14
                );

        batteryOuter.setArcWidth(7);
        batteryOuter.setArcHeight(7);

        batteryOuter.setFill(
                Color.rgb(10, 30, 34)
        );

        batteryOuter.setStroke(
                Color.rgb(60, 150, 140)
        );

        Rectangle batteryFill =
                new Rectangle(
                        96,
                        160,
                        81,
                        10
                );

        batteryFill.setArcWidth(5);
        batteryFill.setArcHeight(5);

        batteryFill.setFill(
                Color.rgb(70, 255, 205)
        );

        Rectangle batteryTip =
                new Rectangle(
                        202,
                        162,
                        5,
                        6
                );

        batteryTip.setFill(
                Color.rgb(80, 220, 190)
        );

        root.getChildren().addAll(
                batteryOuter,
                batteryFill,
                batteryTip
        );

        // =========================================================
        // CHARGING CORE
        // =========================================================

        Circle glow =
                new Circle(
                        150,
                        245,
                        60
                );

        glow.setFill(
                Color.rgb(40, 255, 210, 0.035)
        );

        root.getChildren().add(glow);

        Circle coreOuter =
                new Circle(
                        150,
                        245,
                        42
                );

        coreOuter.setFill(
                Color.rgb(4, 30, 34)
        );

        coreOuter.setStroke(
                Color.rgb(50, 210, 190)
        );

        coreOuter.setStrokeWidth(1.2);

        root.getChildren().add(coreOuter);

        // =========================================================
        // ROTATING ENERGY RINGS
        // =========================================================

        Arc ring1 =
                new Arc(
                        150,
                        245,
                        53,
                        53,
                        25,
                        115
                );

        ring1.setFill(null);
        ring1.setStroke(
                Color.rgb(70, 255, 215)
        );
        ring1.setStrokeWidth(2);
        ring1.setType(ArcType.OPEN);

        Arc ring2 =
                new Arc(
                        150,
                        245,
                        47,
                        47,
                        190,
                        95
                );

        ring2.setFill(null);
        ring2.setStroke(
                Color.rgb(60, 190, 255)
        );
        ring2.setStrokeWidth(2);
        ring2.setType(ArcType.OPEN);

        root.getChildren().addAll(
                ring1,
                ring2
        );

        RotateTransition rotate1 =
                new RotateTransition(
                        Duration.seconds(3),
                        ring1
                );

        rotate1.setByAngle(360);
        rotate1.setCycleCount(
                RotateTransition.INDEFINITE
        );

        RotateTransition rotate2 =
                new RotateTransition(
                        Duration.seconds(2.2),
                        ring2
                );

        rotate2.setByAngle(-360);
        rotate2.setCycleCount(
                RotateTransition.INDEFINITE
        );

        rotate1.play();
        rotate2.play();

        // =========================================================
        // INNER CORE
        // =========================================================

        Circle core =
                new Circle(
                        150,
                        245,
                        23
                );

        core.setFill(
                Color.rgb(20, 100, 90)
        );

        core.setStroke(
                Color.rgb(120, 255, 225)
        );

        core.setStrokeWidth(1.5);

        root.getChildren().add(core);

        ScaleTransition corePulse =
                new ScaleTransition(
                        Duration.seconds(0.9),
                        core
                );

        corePulse.setFromX(0.85);
        corePulse.setFromY(0.85);
        corePulse.setToX(1.12);
        corePulse.setToY(1.12);
        corePulse.setAutoReverse(true);
        corePulse.setCycleCount(
                ScaleTransition.INDEFINITE
        );

        corePulse.play();

        // =========================================================
        // LIGHTNING
        // =========================================================

        Label lightning =
                label(
                        "ϟ",
                        28,
                        Color.WHITE
                );

        lightning.setLayoutX(141);
        lightning.setLayoutY(230);

        root.getChildren().add(lightning);

        FadeTransition lightningPulse =
                new FadeTransition(
                        Duration.seconds(0.7),
                        lightning
                );

        lightningPulse.setFromValue(0.55);
        lightningPulse.setToValue(1);
        lightningPulse.setAutoReverse(true);
        lightningPulse.setCycleCount(
                FadeTransition.INDEFINITE
        );

        lightningPulse.play();

        // =========================================================
        // ORBIT PARTICLES
        // =========================================================

        List<Circle> particles =
                new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Circle particle =
                    new Circle(
                            2
                    );

            particle.setFill(
                    i % 2 == 0
                            ? Color.CYAN
                            : Color.LIGHTGREEN
            );

            double angle =
                    Math.toRadians(
                            i * 36
                    );

            particle.setLayoutX(
                    150 + Math.cos(angle) * 58
            );

            particle.setLayoutY(
                    245 + Math.sin(angle) * 58
            );

            particles.add(particle);

            root.getChildren().add(
                    particle
            );
        }

        Timeline orbit =
                new Timeline(
                        new KeyFrame(
                                Duration.ZERO
                        ),
                        new KeyFrame(
                                Duration.seconds(4),
                                new KeyValue(
                                        root.rotateProperty(),
                                        360
                                )
                        )
                );

        orbit.setCycleCount(
                Timeline.INDEFINITE
        );

        // Don't rotate the entire charger.
        // Instead use individual particle movement.

        for (int i = 0; i < particles.size(); i++) {

            Circle particle =
                    particles.get(i);

            double start =
                    i * 36;

            Timeline particleOrbit =
                    new Timeline(
                            new KeyFrame(
                                    Duration.ZERO,
                                    new KeyValue(
                                            particle.layoutXProperty(),
                                            150 +
                                            Math.cos(
                                                    Math.toRadians(start)
                                            ) * 58
                                    ),
                                    new KeyValue(
                                            particle.layoutYProperty(),
                                            245 +
                                            Math.sin(
                                                    Math.toRadians(start)
                                            ) * 58
                                    )
                            ),
                            new KeyFrame(
                                    Duration.seconds(3.5),
                                    new KeyValue(
                                            particle.layoutXProperty(),
                                            150 +
                                            Math.cos(
                                                    Math.toRadians(start + 360)
                                            ) * 58
                                    ),
                                    new KeyValue(
                                            particle.layoutYProperty(),
                                            245 +
                                            Math.sin(
                                                    Math.toRadians(start + 360)
                                            ) * 58
                                    )
                            )
                    );

            particleOrbit.setCycleCount(
                    Timeline.INDEFINITE
            );

            particleOrbit.play();
        }

        // =========================================================
        // STATUS
        // =========================================================

        Circle statusDot =
                new Circle(
                        4,
                        Color.LIGHTGREEN
                );

        statusDot.setLayoutX(91);
        statusDot.setLayoutY(305);

        Label status =
                label(
                        "CHARGING • ACTIVE",
                        10,
                        Color.LIGHTGREEN
                );

        status.setLayoutX(102);
        status.setLayoutY(298);

        root.getChildren().addAll(
                statusDot,
                status
        );

        // =========================================================
        // ENERGY LINE
        // =========================================================

        Line energyLine =
                new Line(
                        78,
                        328,
                        223,
                        328
                );

        energyLine.setStroke(
                Color.rgb(50, 180, 175, 0.35)
        );

        energyLine.setStrokeWidth(1);

        root.getChildren().add(
                energyLine
        );

        // =========================================================
        // CONNECTOR
        // =========================================================

        Line connector =
                new Line(
                        150,
                        370,
                        150,
                        390
                );

        connector.setStroke(
                Color.rgb(80, 255, 215)
        );

        connector.setStrokeWidth(4);

        root.getChildren().add(
                connector
        );

        Circle connectorEnd =
                new Circle(
                        150,
                        394,
                        7
                );

        connectorEnd.setFill(
                Color.rgb(20, 80, 75)
        );

        connectorEnd.setStroke(
                Color.rgb(90, 255, 220)
        );

        root.getChildren().add(
                connectorEnd
        );

        // =========================================================
        // ENTRY ANIMATION
        // =========================================================

        root.setOpacity(0);

        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(1.2),
                        root
                );

        fade.setFromValue(0);
        fade.setToValue(1);

        fade.play();

        return root;
    }

    // =============================================================
    // LABEL HELPER
    // =============================================================

    private Label label(
            String text,
            double size,
            Color color
    ) {

        Label label =
                new Label(text);

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
}