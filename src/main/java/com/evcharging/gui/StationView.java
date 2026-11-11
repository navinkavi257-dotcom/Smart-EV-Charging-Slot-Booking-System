package com.evcharging.gui;

import com.evcharging.model.Station;
import com.evcharging.model.User;
import com.evcharging.service.StationService;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class StationView {

    private final StationService stationService =
            new StationService();

    private final User loggedInUser;
    private final Random random = new Random(17);

    private VBox stationContainer;
    private Label resultLabel;
    private TextField searchField;

    public StationView() {
        this.loggedInUser = null;
    }

    public StationView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage stage) {

        // =========================================================
        // TOP BAR
        // =========================================================

        Label logo = label(
                "⚡ SMART EV",
                25,
                Color.WHITE
        );

        Label online = label(
                "●  SYSTEM ONLINE",
                11,
                Color.LIGHTGREEN
        );

        online.setEffect(new Glow(0.8));

        Label page = label(
                "FIND CHARGING STATION",
                12,
                Color.LIGHTGRAY
        );

        VBox topInfo = new VBox(2, online, page);
        topInfo.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(logo, topInfo);

        topBar.setAlignment(Pos.CENTER);
        HBox.setHgrow(topInfo, Priority.ALWAYS);

        topBar.setPadding(
                new Insets(15, 24, 15, 24)
        );

        topBar.setStyle(
                "-fx-background-color: #050d14;" +
                "-fx-border-color: #17343d;" +
                "-fx-border-width: 0 0 1 0;"
        );

        // =========================================================
        // SEARCH HEADER
        // =========================================================

        Label title = label(
                "FIND STATION",
                31,
                Color.WHITE
        );

        title.setEffect(new Glow(0.25));

        Label subtitle = label(
                "DISCOVER • SEARCH • CONNECT TO YOUR EV NETWORK",
                11,
                Color.rgb(80, 235, 205)
        );

        VBox heading = new VBox(
                4,
                title,
                subtitle
        );

        // =========================================================
        // SEARCH BOX
        // =========================================================

        searchField = new TextField();

        searchField.setPromptText(
                "Enter city or location..."
        );

        searchField.setPrefHeight(45);
        searchField.setPrefWidth(470);

        searchField.setStyle(
                "-fx-background-color: #081920;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #688084;" +
                "-fx-font-size: 14px;" +
                "-fx-border-color: #21665f;" +
                "-fx-border-width: 1.2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        Button searchButton =
                new Button("SEARCH");

        searchButton.setPrefSize(125, 45);

        searchButton.setStyle(searchButtonStyle());

        Button allButton =
                new Button("ALL STATIONS");

        allButton.setPrefSize(135, 45);
        allButton.setStyle(secondaryButtonStyle());

        HBox searchRow = new HBox(
                10,
                searchField,
                searchButton,
                allButton
        );

        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchButton.setOnAction(e ->
                searchStations()
        );

        allButton.setOnAction(e ->
                loadAllStations()
        );

        searchField.setOnAction(e ->
                searchStations()
        );

        // =========================================================
        // RESULT HEADER
        // =========================================================

        resultLabel = label(
                "Loading charging stations...",
                11,
                Color.LIGHTGREEN
        );

        // =========================================================
        // STATION CARDS
        // =========================================================

        stationContainer = new VBox(12);

        stationContainer.setPadding(
                new Insets(5, 2, 20, 2)
        );

        ScrollPane scroll = new ScrollPane(
                stationContainer
        );

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        // =========================================================
        // BACK BUTTON
        // =========================================================

        Button back = new Button(
                "←  BACK TO DASHBOARD"
        );

        back.setPrefHeight(42);
        back.setPrefWidth(190);
        back.setStyle(secondaryButtonStyle());

        back.setOnAction(e -> {

            if (loggedInUser != null) {
                new DashboardView(loggedInUser).show(stage);
            } else {
                new LoginView().show(stage);
            }
        });

        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER_LEFT);

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = new VBox(
                15,
                heading,
                searchRow,
                resultLabel,
                scroll,
                bottom
        );

        content.setPadding(
                new Insets(25, 30, 22, 30)
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);

        StackPane center = new StackPane(
                createBackground(),
                content
        );

        center.setStyle(
                "-fx-background-color: #02080d;"
        );

        // =========================================================
        // ROOT
        // =========================================================

        BorderPane root = new BorderPane();

        root.setTop(topBar);
        root.setCenter(center);

        Scene scene = new Scene(
                root,
                1180,
                760
        );

        stage.setTitle(
                "Smart EV - Find Station"
        );

        stage.setScene(scene);
        stage.show();

        loadAllStations();

        fadeIn(content);
    }

    // =============================================================
    // LOAD ALL
    // =============================================================

    private void loadAllStations() {

        try {

            List<Station> stations =
                    stationService.getAllStations();

            displayStations(stations);

            resultLabel.setText(
                    stations.size()
                            + " charging station"
                            + (stations.size() == 1 ? "" : "s")
                            + " connected to the network"
            );

        } catch (Exception e) {

            resultLabel.setText(
                    "Unable to load stations: "
                            + e.getMessage()
            );
        }
    }

    // =============================================================
    // SEARCH
    // =============================================================

    private void searchStations() {

        String location =
                searchField.getText().trim();

        if (location.isEmpty()) {

            loadAllStations();
            return;
        }

        try {

            List<Station> stations =
                    stationService.searchStations(
                            location
                    );

            displayStations(stations);

            resultLabel.setText(
                    stations.size()
                            + " station"
                            + (stations.size() == 1 ? "" : "s")
                            + " found for \""
                            + location
                            + "\""
            );

        } catch (Exception e) {

            resultLabel.setText(
                    "Search error: "
                            + e.getMessage()
            );
        }
    }

    // =============================================================
    // DISPLAY
    // =============================================================

    private void displayStations(
            List<Station> stations
    ) {

        stationContainer.getChildren().clear();

        if (stations.isEmpty()) {

            VBox empty = new VBox(10);

            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(45));

            Label icon = label(
                    "⌁",
                    42,
                    Color.CYAN
            );

            icon.setEffect(new Glow(1));

            Label message = label(
                    "NO STATIONS FOUND",
                    18,
                    Color.WHITE
            );

            Label hint = label(
                    "Try another city or location.",
                    11,
                    Color.LIGHTGRAY
            );

            empty.getChildren().addAll(
                    icon,
                    message,
                    hint
            );

            stationContainer.getChildren().add(empty);

            return;
        }

        for (int i = 0; i < stations.size(); i++) {

            VBox card =
                    createStationCard(
                            stations.get(i),
                            i
                    );

            stationContainer.getChildren().add(card);
        }
    }

    // =============================================================
    // STATION CARD
    // =============================================================

    private VBox createStationCard(
            Station station,
            int index
    ) {

        Label stationIcon = label(
                "⚡",
                28,
                Color.CYAN
        );

        stationIcon.setEffect(new Glow(0.9));

        StackPane iconBox = new StackPane(
                stationIcon
        );

        iconBox.setPrefSize(55, 55);

        iconBox.setStyle(
                "-fx-background-color: #09262c;" +
                "-fx-border-color: #21665f;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );

        Label name = label(
                station.getStationName(),
                17,
                Color.WHITE
        );

        Label id = label(
                "STATION ID  •  "
                        + station.getStationId(),
                9,
                Color.rgb(105, 175, 175)
        );

        Label location = label(
                "⌖  " + station.getLocation(),
                11,
                Color.LIGHTGRAY
        );

        VBox stationInfo = new VBox(
                4,
                name,
                id,
                location
        );

        HBox.setHgrow(
                stationInfo,
                Priority.ALWAYS
        );

        Label chargerTitle = label(
                "CHARGERS",
                9,
                Color.rgb(120, 165, 170)
        );

        Label chargerValue = label(
                String.valueOf(
                        station.getTotalChargers()
                ),
                24,
                Color.LIGHTGREEN
        );

        Label chargerStatus = label(
                "AVAILABLE NETWORK",
                8,
                Color.LIGHTGREEN
        );

        VBox chargerBox = new VBox(
                1,
                chargerTitle,
                chargerValue,
                chargerStatus
        );

        chargerBox.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(
                14,
                iconBox,
                stationInfo,
                chargerBox
        );

        header.setAlignment(Pos.CENTER_LEFT);

        Circle onlineDot =
                new Circle(4, Color.LIGHTGREEN);

        onlineDot.setEffect(new Glow(1));

        Label online = label(
                "STATION ONLINE",
                9,
                Color.LIGHTGREEN
        );

        HBox status = new HBox(
                7,
                onlineDot,
                online
        );

        status.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(
                13,
                header,
                status
        );

        card.setPadding(
                new Insets(15)
        );

        card.setStyle(cardStyle());

        // Interactive hover
        card.setOnMouseEntered(e -> {

            card.setStyle(cardHoverStyle());

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(160),
                            card
                    );

            scale.setToX(1.015);
            scale.setToY(1.015);
            scale.play();

            TranslateTransition move =
                    new TranslateTransition(
                            Duration.millis(160),
                            card
                    );

            move.setToX(5);
            move.play();
        });

        card.setOnMouseExited(e -> {

            card.setStyle(cardStyle());

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(160),
                            card
                    );

            scale.setToX(1);
            scale.setToY(1);
            scale.play();

            TranslateTransition move =
                    new TranslateTransition(
                            Duration.millis(160),
                            card
                    );

            move.setToX(0);
            move.play();
        });

        // Click interaction
        card.setOnMouseClicked(e -> {

            card.setStyle(
                    "-fx-background-color: #10353a;" +
                    "-fx-border-color: #65ffd7;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 14;" +
                    "-fx-background-radius: 14;"
            );

            resultLabel.setText(
                    "SELECTED  •  "
                            + station.getStationName()
                            + "  •  "
                            + station.getLocation()
            );
        });

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(500),
                        card
                );

        fade.setDelay(
                Duration.millis(index * 90)
        );

        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        return card;
    }

    // =============================================================
    // ANIMATED BACKGROUND
    // =============================================================

    private Pane createBackground() {

        Pane pane = new Pane();
        pane.setMouseTransparent(true);

        Rectangle base =
                new Rectangle(1200, 720);

        base.setFill(
                Color.web("#02080d")
        );

        pane.getChildren().add(base);

        // Grid
        for (int x = 0; x <= 1200; x += 45) {

            Line line =
                    new Line(
                            x, 0,
                            x, 760
                    );

            line.setStroke(
                    Color.rgb(
                            55,
                            190,
                            185,
                            0.075
                    )
            );

            pane.getChildren().add(line);
        }

        for (int y = 0; y <= 760; y += 45) {

            Line line =
                    new Line(
                            0, y,
                            1200, y
                    );

            line.setStroke(
                    Color.rgb(
                            55,
                            190,
                            185,
                            0.075
                    )
            );

            pane.getChildren().add(line);
        }

        // Floating particles
        for (int i = 0; i < 45; i++) {

            Circle p =
                    new Circle(
                            1 + random.nextDouble() * 2
                    );

            p.setFill(
                    i % 2 == 0
                            ? Color.CYAN
                            : Color.LIGHTGREEN
            );

            p.setOpacity(
                    0.2 + random.nextDouble() * 0.55
            );

            p.setLayoutX(
                    random.nextDouble() * 1180
            );

            p.setLayoutY(
                    random.nextDouble() * 700
            );

            pane.getChildren().add(p);
        }

        return pane;
    }

    // =============================================================
    // HELPERS
    // =============================================================

    private Label label(
            String text,
            double size,
            Color color
    ) {

        Label label = new Label(text);

        label.setFont(
                Font.font("Arial", size)
        );

        label.setTextFill(color);

        return label;
    }

    private String cardStyle() {

        return
                "-fx-background-color: #07171d;" +
                "-fx-border-color: #1c5556;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;";
    }

    private String cardHoverStyle() {

        return
                "-fx-background-color: #0b252c;" +
                "-fx-border-color: #4dffd0;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;";
    }

    private String searchButtonStyle() {

        return
                "-fx-background-color: #16443f;" +
                "-fx-text-fill: #65ffd7;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #4dffd0;" +
                "-fx-border-radius: 10;";
    }

    private String secondaryButtonStyle() {

        return
                "-fx-background-color: #091a20;" +
                "-fx-text-fill: #c8d8da;" +
                "-fx-font-size: 11px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #28535a;" +
                "-fx-border-radius: 10;";
    }

    private void fadeIn(Node node) {

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(650),
                        node
                );

        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
