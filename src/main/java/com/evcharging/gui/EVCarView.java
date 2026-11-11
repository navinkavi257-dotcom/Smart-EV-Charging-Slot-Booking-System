package com.evcharging.gui;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class EVCarView {

    public Pane create() {

        Pane carPane = new Pane();

        carPane.setPrefSize(
                300,
                120
        );

        // =========================================================
        // CAR GROUP
        // =========================================================

        Group car = new Group();

        // =========================================================
        // CAR GLOW / UNDERGLOW
        // =========================================================

        Ellipse carGlow =
                new Ellipse(
                        140,
                        92,
                        145,
                        15
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

        // =========================================================
        // MAIN CAR BODY
        // =========================================================

        Path carBody =
                new Path();

        carBody.getElements().add(
                new MoveTo(
                        0,
                        67
                )
        );

        carBody.getElements().add(
                new LineTo(
                        20,
                        27
                )
        );

        carBody.getElements().add(
                new LineTo(
                        60,
                        0
                )
        );

        carBody.getElements().add(
                new LineTo(
                        180,
                        0
                )
        );

        carBody.getElements().add(
                new LineTo(
                        225,
                        32
                )
        );

        carBody.getElements().add(
                new LineTo(
                        275,
                        47
                )
        );

        carBody.getElements().add(
                new LineTo(
                        285,
                        72
                )
        );

        carBody.getElements().add(
                new LineTo(
                        280,
                        87
                )
        );

        carBody.getElements().add(
                new LineTo(
                        5,
                        87
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

        // =========================================================
        // REAR WINDOW
        // =========================================================

        Polygon rearWindow =
                new Polygon(
                        65, 6,
                        135, 6,
                        140, 32,
                        45, 32
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

        // =========================================================
        // FRONT WINDOW
        // =========================================================

        Polygon frontWindow =
                new Polygon(
                        142, 6,
                        180, 6,
                        215, 32,
                        147, 32
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

        // =========================================================
        // ROOF LINE
        // =========================================================

        Line roofLine =
                new Line(
                        60,
                        0,
                        180,
                        0
                );

        roofLine.setStroke(
                Color.web(
                        "#67FFE0"
                )
        );

        roofLine.setStrokeWidth(2);

        // =========================================================
        // REAR WHEEL
        // =========================================================

        Circle rearWheel =
                new Circle(
                        65,
                        87,
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

        // =========================================================
        // FRONT WHEEL
        // =========================================================

        Circle frontWheel =
                new Circle(
                        230,
                        87,
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

        // =========================================================
        // REAR WHEEL CENTER
        // =========================================================

        Circle rearCenter =
                new Circle(
                        65,
                        87,
                        9
                );

        rearCenter.setFill(
                Color.web(
                        "#183638"
                )
        );

        // =========================================================
        // FRONT WHEEL CENTER
        // =========================================================

        Circle frontCenter =
                new Circle(
                        230,
                        87,
                        9
                );

        frontCenter.setFill(
                Color.web(
                        "#183638"
                )
        );

        // =========================================================
        // HEADLIGHT
        // =========================================================

        Circle headlight =
                new Circle(
                        274,
                        58,
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

        // =========================================================
        // EV SYMBOL
        // =========================================================

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

        evSymbol.setLayoutX(165);
        evSymbol.setLayoutY(42);

        // =========================================================
        // ADD CAR PARTS
        // =========================================================

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

        carPane.getChildren().add(
                car
        );

        // =========================================================
        // VERY SUBTLE FLOATING ANIMATION
        // =========================================================

        TranslateTransition carFloat =
                new TranslateTransition(
                        Duration.seconds(2.8),
                        car
                );

        carFloat.setFromY(0);
        carFloat.setToY(-5);

        carFloat.setAutoReverse(true);

        carFloat.setCycleCount(
                Animation.INDEFINITE
        );

        carFloat.play();

        return carPane;
    }
}