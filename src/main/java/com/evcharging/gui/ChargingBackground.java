package com.evcharging.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.Random;

public class ChargingBackground {

    private static final double WIDTH = 1500;
    private static final double HEIGHT = 950;

    private final Random random = new Random();

    public Pane create() {

        Pane root = new Pane();

        root.setPrefSize(WIDTH, HEIGHT);
        root.setStyle(
                "-fx-background-color: #02080D;"
        );

        root.setMouseTransparent(true);

        // -----------------------------------------
        // SUBTLE ATMOSPHERIC GLOW
        // -----------------------------------------

        Circle glow = new Circle(
                180,
                Color.rgb(
                        40,
                        255,
                        190,
                        0.035
                )
        );

        glow.setCenterX(300);
        glow.setCenterY(350);

        root.getChildren().add(glow);

        // -----------------------------------------
        // DUST
        // -----------------------------------------

        createDust(root);

        // -----------------------------------------
        // SPARKS
        // -----------------------------------------

        createSparks(root);

        return root;
    }

    // =================================================
    // DUST PARTICLES
    // =================================================

    private void createDust(Pane root) {

        for (int i = 0; i < 100; i++) {

            double x =
                    random.nextDouble() * WIDTH;

            double y =
                    random.nextDouble() * HEIGHT;

            double size;

            int type = random.nextInt(10);

            if (type < 7) {
                size = 0.7;
            } else if (type < 9) {
                size = 1.1;
            } else {
                size = 1.5;
            }

            Circle particle =
                    new Circle(size);

            particle.setCenterX(x);
            particle.setCenterY(y);

            if (random.nextBoolean()) {

                particle.setFill(
                        Color.rgb(
                                105,
                                255,
                                220,
                                0.25 +
                                random.nextDouble() * 0.35
                        )
                );

            } else {

                particle.setFill(
                        Color.rgb(
                                100,
                                210,
                                255,
                                0.18 +
                                random.nextDouble() * 0.30
                        )
                );
            }

            particle.setEffect(
                    new Glow(
                            size > 1
                                    ? 0.7
                                    : 0.3
                    )
            );

            root.getChildren().add(
                    particle
            );

            animateDust(particle);
        }
    }

    private void animateDust(
            Circle particle) {

        double moveX =
                -15 +
                random.nextDouble() * 30;

        double moveY =
                -20 +
                random.nextDouble() * 40;

        double duration =
                5 +
                random.nextDouble() * 8;

        TranslateTransition move =
                new TranslateTransition(
                        Duration.seconds(duration),
                        particle
                );

        move.setFromX(0);
        move.setFromY(0);

        move.setToX(moveX);
        move.setToY(moveY);

        move.setAutoReverse(true);

        move.setCycleCount(
                Animation.INDEFINITE
        );

        move.setDelay(
                Duration.seconds(
                        random.nextDouble() * 4
                )
        );

        move.play();

        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(
                                2 +
                                random.nextDouble() * 3
                        ),
                        particle
                );

        fade.setFromValue(
                0.15
        );

        fade.setToValue(
                0.8
        );

        fade.setAutoReverse(true);

        fade.setCycleCount(
                Animation.INDEFINITE
        );

        fade.setDelay(
                Duration.seconds(
                        random.nextDouble() * 4
                )
        );

        fade.play();
    }

    // =================================================
    // SMALL ELECTRIC SPARKS
    // =================================================

    private void createSparks(Pane root) {

        for (int i = 0; i < 15; i++) {

            double x =
                    random.nextDouble() * WIDTH;

            double y =
                    random.nextDouble() * HEIGHT;

            Line spark =
                    new Line(
                            0,
                            0,
                            5 +
                            random.nextDouble() * 9,
                            0
                    );

            spark.setTranslateX(x);
            spark.setTranslateY(y);

            spark.setStroke(
                    random.nextBoolean()
                            ? Color.web("#5FF2C8")
                            : Color.web("#55DFFF")
            );

            spark.setStrokeWidth(
                    1 +
                    random.nextDouble()
            );

            spark.setOpacity(0);

            spark.setEffect(
                    new Glow(1.5)
            );

            root.getChildren().add(
                    spark
            );

            animateSpark(
                    spark
            );
        }
    }

    private void animateSpark(
            Line spark) {

        double distance =
                30 +
                random.nextDouble() * 80;

        double duration =
                1 +
                random.nextDouble() * 2;

        TranslateTransition move =
                new TranslateTransition(
                        Duration.seconds(duration),
                        spark
                );

        move.setFromX(
                spark.getTranslateX()
        );

        move.setFromY(
                spark.getTranslateY()
        );

        move.setToX(
                spark.getTranslateX()
                        + distance
        );

        move.setToY(
                spark.getTranslateY()
                        - 10
                        - random.nextDouble() * 25
        );

        move.setCycleCount(
                Animation.INDEFINITE
        );

        move.setDelay(
                Duration.seconds(
                        random.nextDouble() * 6
                )
        );

        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(
                                duration
                        ),
                        spark
                );

        fade.setFromValue(0);

        fade.setToValue(
                0.7 +
                random.nextDouble() * 0.3
        );

        fade.setAutoReverse(true);

        fade.setCycleCount(
                Animation.INDEFINITE
        );

        fade.setDelay(
                Duration.seconds(
                        random.nextDouble() * 6
                )
        );

        ParallelTransition animation =
                new ParallelTransition(
                        move,
                        fade
                );

        animation.play();
    }
}