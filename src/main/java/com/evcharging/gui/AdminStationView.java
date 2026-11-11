package com.evcharging.gui;

import com.evcharging.model.Station;
import com.evcharging.model.User;
import com.evcharging.service.StationService;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SMART EV
 * Premium Station Management
 *
 * Features:
 * - Premium futuristic station cards
 * - Database-backed station information
 * - Search stations
 * - Add station
 * - Refresh station data
 * - Station details popup
 * - Hover animation
 * - Responsive card layout
 * - Admin navigation
 * - Floating green dust atmosphere
 */
public class AdminStationView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BACKGROUND =
            Color.rgb(3, 9, 14);

    private static final Color SIDEBAR =
            Color.rgb(4, 13, 19);

    private static final Color PANEL =
            Color.rgb(7, 20, 29);

    private static final Color PANEL_LIGHT =
            Color.rgb(10, 28, 39);

    private static final Color CYAN =
            Color.rgb(35, 220, 255);

    private static final Color GREEN =
            Color.rgb(68, 239, 139);

    private static final Color PURPLE =
            Color.rgb(166, 112, 255);

    private static final Color AMBER =
            Color.rgb(255, 190, 75);

    private static final Color WHITE =
            Color.rgb(238, 247, 251);

    private static final Color MUTED =
            Color.rgb(126, 151, 163);

    private static final Color BORDER =
            Color.rgb(35, 220, 255, 0.14);

    // =========================================================
    // FLOATING GREEN DUST
    // =========================================================

    private Pane dustLayer;

    private AnimationTimer dustAnimation;

    private final Random dustRandom =
            new Random();

    // =========================================================
    // DATA
    // =========================================================

    private final User loggedInUser;

    private final StationService stationService =
            new StationService();

    private Stage currentStage;

    private FlowPane stationGrid;

    private TextField searchField;

    private Label stationCountLabel;

    private Label chargerCountLabel;

    private Label visibleCountLabel;

    private List<Station> allStations =
            new ArrayList<>();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminStationView(User loggedInUser) {

        this.loggedInUser = loggedInUser;
    }

    // =========================================================
    // SHOW PAGE
    // =========================================================

    public void show(Stage stage) {

        currentStage = stage;

        BorderPane contentRoot =
                new BorderPane();

        contentRoot.setStyle(
                "-fx-background-color: #03090E;"
        );

        // -----------------------------------------------------
        // DUST BACKGROUND
        // -----------------------------------------------------

        dustLayer =
                createFloatingDustLayer();

        StackPane root =
                new StackPane();

        root.setStyle(
                "-fx-background-color: #03090E;"
        );

        root.getChildren().addAll(
                dustLayer,
                contentRoot
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        VBox sidebar =
                createSidebar(stage);

        // -----------------------------------------------------
        // MAIN AREA
        // -----------------------------------------------------

        VBox main =
                createMainContent();

        ScrollPane scrollPane =
                new ScrollPane(main);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        contentRoot.setLeft(sidebar);

        contentRoot.setCenter(
                scrollPane
        );

        // -----------------------------------------------------
        // SCENE
        // -----------------------------------------------------

        Scene scene =
                new Scene(
                        root,
                        1440,
                        900
                );

        scene.setFill(
                BACKGROUND
        );

        stage.setTitle(
                "Smart EV - Station Management"
        );

        stage.setScene(scene);

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

        // IMPORTANT:
        // Start dust ONLY after the scene exists.
        startDustAnimation();

        playEntrance(main);
    }

    // =========================================================
    // CREATE FLOATING GREEN DUST
    // =========================================================

    private Pane createFloatingDustLayer() {

        Pane layer =
                new Pane();

        layer.setMouseTransparent(true);

        layer.setPickOnBounds(false);

        final int PARTICLE_COUNT = 55;

        List<Circle> particles =
                new ArrayList<>();

        List<Double> xPositions =
                new ArrayList<>();

        List<Double> yPositions =
                new ArrayList<>();

        List<Double> speeds =
                new ArrayList<>();

        List<Double> driftAmounts =
                new ArrayList<>();

        List<Double> phases =
                new ArrayList<>();

        List<Double> baseOpacity =
                new ArrayList<>();

        // -----------------------------------------------------
        // CREATE PARTICLES
        // -----------------------------------------------------

        for (int i = 0;
             i < PARTICLE_COUNT;
             i++) {

            double size;

            if (dustRandom.nextDouble() < 0.75) {

                size =
                        1.0
                                + dustRandom.nextDouble()
                                * 1.8;

            } else {

                size =
                        2.0
                                + dustRandom.nextDouble()
                                * 2.0;
            }

            Circle particle =
                    new Circle(size);

            double opacity =
                    0.12
                            + dustRandom.nextDouble()
                            * 0.30;

            particle.setFill(
                    Color.rgb(
                            68,
                            239,
                            139,
                            opacity
                    )
            );

            DropShadow glow =
                    new DropShadow();

            glow.setRadius(
                    size > 2.5
                            ? 8
                            : 5
            );

            glow.setSpread(
                    0.08
            );

            glow.setColor(
                    Color.rgb(
                            68,
                            239,
                            139,
                            0.25
                    )
            );

            particle.setEffect(
                    glow
            );

            double x =
                    dustRandom.nextDouble()
                            * 1500;

            double y =
                    dustRandom.nextDouble()
                            * 900;

            double speed =
                    0.15
                            + dustRandom.nextDouble()
                            * 0.45;

            double drift =
                    4
                            + dustRandom.nextDouble()
                            * 15;

            double phase =
                    dustRandom.nextDouble()
                            * Math.PI
                            * 2;

            particle.setLayoutX(x);

            particle.setLayoutY(y);

            layer.getChildren().add(
                    particle
            );

            particles.add(
                    particle
            );

            xPositions.add(x);

            yPositions.add(y);

            speeds.add(speed);

            driftAmounts.add(drift);

            phases.add(phase);

            baseOpacity.add(opacity);
        }

        // -----------------------------------------------------
        // ANIMATION
        // -----------------------------------------------------

        dustAnimation =
                new AnimationTimer() {

                    private long lastTime = -1;

                    private double elapsed = 0;

                    @Override
                    public void handle(long now) {

                        if (layer.getScene() == null) {

                            stop();

                            return;
                        }

                        if (lastTime < 0) {

                            lastTime = now;

                            return;
                        }

                        double delta =
                                (now - lastTime)
                                        / 1_000_000_000.0;

                        lastTime = now;

                        delta =
                                Math.min(
                                        delta,
                                        0.05
                                );

                        elapsed += delta;

                        double width =
                                layer.getWidth();

                        double height =
                                layer.getHeight();

                        if (width <= 0) {
                            width = 1440;
                        }

                        if (height <= 0) {
                            height = 900;
                        }

                        for (int i = 0;
                             i < particles.size();
                             i++) {

                            Circle particle =
                                    particles.get(i);

                            double x =
                                    xPositions.get(i);

                            double y =
                                    yPositions.get(i);

                            double speed =
                                    speeds.get(i);

                            // Slow upward movement
                            y -=
                                    speed
                                            * 60
                                            * delta;

                            // Reset at top
                            if (y < -25) {

                                y =
                                        height
                                                + 20
                                                + dustRandom
                                                        .nextDouble()
                                                        * 80;

                                x =
                                        dustRandom
                                                .nextDouble()
                                                * width;

                                xPositions.set(
                                        i,
                                        x
                                );
                            }

                            yPositions.set(
                                    i,
                                    y
                            );

                            double phase =
                                    phases.get(i);

                            double drift =
                                    driftAmounts.get(i);

                            // Very soft horizontal movement
                            double floatingX =
                                    x
                                            + Math.sin(
                                                    elapsed
                                                            * 0.35
                                                            + phase
                                            )
                                            * drift;

                            particle.setLayoutX(
                                    floatingX
                            );

                            particle.setLayoutY(
                                    y
                            );

                            // Soft brightness variation
                            double originalOpacity =
                                    baseOpacity.get(i);

                            double breathing =
                                    0.72
                                            + 0.28
                                            * (
                                                    0.5
                                                            + 0.5
                                                            * Math.sin(
                                                                    elapsed
                                                                            * 1.1
                                                                            + phase
                                                            )
                                            );

                            particle.setOpacity(
                                    originalOpacity
                                            * breathing
                            );
                        }
                    }
                };

        // Do NOT start here.
        // It is started after stage.show().

        return layer;
    }

    // =========================================================
    // START DUST ANIMATION
    // =========================================================

    private void startDustAnimation() {

        if (dustAnimation != null) {

            dustAnimation.start();
        }
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent() {

        VBox main =
                new VBox(22);

        main.setPadding(
                new Insets(
                        28,
                        34,
                        34,
                        34
                )
        );

        main.setFillWidth(true);

        HBox header =
                createHeader();

        HBox kpis =
                createKpiSection();

        HBox actionBar =
                createActionBar();

        HBox sectionHeader =
                createSectionHeader();

        stationGrid =
                new FlowPane();

        stationGrid.setHgap(16);

        stationGrid.setVgap(16);

        stationGrid.setPrefWrapLength(
                1000
        );

        stationGrid.setPadding(
                new Insets(
                        2,
                        2,
                        20,
                        2
                )
        );

        loadStations();

        main.getChildren().addAll(
                header,
                kpis,
                actionBar,
                sectionHeader,
                stationGrid
        );

        VBox.setVgrow(
                stationGrid,
                Priority.ALWAYS
        );

        return main;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {

        VBox text =
                new VBox(5);

        Label eyebrow =
                createLabel(
                        "EV INFRASTRUCTURE  /  MANAGEMENT",
                        9,
                        CYAN
                );

        eyebrow.setStyle(
                "-fx-font-weight: bold;"
        );

        Label title =
                createLabel(
                        "STATION CONTROL",
                        30,
                        WHITE
                );

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: 900;"
        );

        Label subtitle =
                createLabel(
                        "Manage and monitor your charging locations",
                        11,
                        MUTED
                );

        text.getChildren().addAll(
                eyebrow,
                title,
                subtitle
        );

        VBox connection =
                new VBox(5);

        connection.setPadding(
                new Insets(
                        12,
                        17,
                        12,
                        17
                )
        );

        connection.setAlignment(
                Pos.CENTER_LEFT
        );

        connection.setStyle(
                "-fx-background-color: rgba(8,28,38,0.92);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(68,239,139,0.20);" +
                "-fx-border-radius: 12;"
        );

        HBox status =
                new HBox(8);

        status.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        4.5,
                        GREEN
                );

        Label live =
                createLabel(
                        "DATABASE CONNECTED",
                        9,
                        GREEN
                );

        live.setStyle(
                "-fx-font-weight: bold;"
        );

        status.getChildren().addAll(
                dot,
                live
        );

        Label jdbc =
                createLabel(
                        "JDBC / MYSQL",
                        8,
                        MUTED
                );

        connection.getChildren().addAll(
                status,
                jdbc
        );

        HBox header =
                new HBox(
                        20,
                        text,
                        connection
                );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        return header;
    }

    // =========================================================
    // KPI SECTION
    // =========================================================

    private HBox createKpiSection() {

        stationCountLabel =
                createLabel(
                        "0",
                        25,
                        WHITE
                );

        chargerCountLabel =
                createLabel(
                        "0",
                        25,
                        WHITE
                );

        visibleCountLabel =
                createLabel(
                        "0",
                        25,
                        WHITE
                );

        HBox row =
                new HBox(14);

        row.getChildren().addAll(

                createKpiCard(
                        "01",
                        "TOTAL STATIONS",
                        "Charging locations",
                        stationCountLabel,
                        CYAN
                ),

                createKpiCard(
                        "02",
                        "TOTAL CHARGERS",
                        "Installed chargers",
                        chargerCountLabel,
                        GREEN
                ),

                createKpiCard(
                        "03",
                        "VISIBLE",
                        "Matching stations",
                        visibleCountLabel,
                        PURPLE
                )
        );

        for (javafx.scene.Node node :
                row.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        return row;
    }

    private StackPane createKpiCard(
            String number,
            String title,
            String subtitle,
            Label value,
            Color accent) {

        StackPane card =
                new StackPane();

        card.setMinHeight(92);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setStyle(
                "-fx-background-color: #07141D;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: rgba(35,220,255,0.13);" +
                "-fx-border-radius: 15;"
        );

        VBox content =
                new VBox(4);

        content.setPadding(
                new Insets(
                        14,
                        17,
                        14,
                        17
                )
        );

        HBox top =
                new HBox(8);

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label index =
                createLabel(
                        number,
                        8,
                        accent
                );

        index.setStyle(
                "-fx-font-weight: bold;"
        );

        Label name =
                createLabel(
                        title,
                        8,
                        MUTED
                );

        name.setStyle(
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                index,
                name
        );

        value.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: 900;"
        );

        Label sub =
                createLabel(
                        subtitle,
                        8,
                        MUTED
                );

        Rectangle line =
                new Rectangle(
                        48,
                        2.5,
                        accent
                );

        line.setArcWidth(4);

        line.setArcHeight(4);

        content.getChildren().addAll(
                top,
                value,
                sub,
                line
        );

        card.getChildren().add(
                content
        );

        StackPane.setAlignment(
                content,
                Pos.CENTER_LEFT
        );

        addHoverEffect(
                card,
                accent
        );

        return card;
    }

    // =========================================================
    // ACTION BAR
    // =========================================================

    private HBox createActionBar() {

        HBox bar =
                new HBox(12);

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(
                        13,
                        14,
                        13,
                        14
                )
        );

        bar.setStyle(
                "-fx-background-color: #07141D;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-radius: 14;"
        );

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search by station name or location..."
        );

        searchField.setPrefHeight(42);

        searchField.setStyle(
                "-fx-background-color: #0B202C;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-radius: 10;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #6F8995;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 0 14;"
        );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        searchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                filterStations(
                                        newValue
                                )
                );

        Button refresh =
                actionButton(
                        "↻  REFRESH",
                        false
                );

        refresh.setOnAction(
                e -> loadStations()
        );

        Button add =
                actionButton(
                        "+  ADD STATION",
                        true
                );

        add.setOnAction(
                e -> showAddStationDialog()
        );

        bar.getChildren().addAll(
                searchField,
                refresh,
                add
        );

        return bar;
    }

    // =========================================================
    // SECTION HEADER
    // =========================================================

    private HBox createSectionHeader() {

        VBox left =
                new VBox(3);

        Label title =
                createLabel(
                        "YOUR CHARGING STATIONS",
                        13,
                        WHITE
                );

        title.setStyle(
                "-fx-font-weight: 900;"
        );

        Label subtitle =
                createLabel(
                        "Select a station to inspect its configuration",
                        9,
                        MUTED
                );

        left.getChildren().addAll(
                title,
                subtitle
        );

        Label live =
                createLabel(
                        "●  LIVE DATABASE VIEW",
                        8,
                        GREEN
                );

        live.setStyle(
                "-fx-font-weight: bold;"
        );

        HBox box =
                new HBox(
                        15,
                        left,
                        live
                );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                left,
                Priority.ALWAYS
        );

        return box;
    }

    // =========================================================
    // LOAD STATIONS
    // =========================================================

    private void loadStations() {

        try {

            allStations =
                    stationService.getAllStations();

            if (allStations == null) {

                allStations =
                        new ArrayList<>();
            }

            updateKpis();

            if (stationGrid != null) {

                displayStations(
                        allStations
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load stations",
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // UPDATE KPI
    // =========================================================

    private void updateKpis() {

        int totalStations =
                allStations.size();

        int totalChargers = 0;

        for (Station station :
                allStations) {

            totalChargers +=
                    station.getTotalChargers();
        }

        if (stationCountLabel != null) {

            stationCountLabel.setText(
                    String.valueOf(
                            totalStations
                    )
            );
        }

        if (chargerCountLabel != null) {

            chargerCountLabel.setText(
                    String.valueOf(
                            totalChargers
                    )
            );
        }

        if (visibleCountLabel != null) {

            visibleCountLabel.setText(
                    String.valueOf(
                            totalStations
                    )
            );
        }
    }

    // =========================================================
    // DISPLAY STATIONS
    // =========================================================

    private void displayStations(
            List<Station> stations) {

        stationGrid.getChildren().clear();

        if (visibleCountLabel != null) {

            visibleCountLabel.setText(
                    String.valueOf(
                            stations.size()
                    )
            );
        }

        if (stations.isEmpty()) {

            stationGrid.getChildren().add(
                    createEmptyState()
            );

            return;
        }

        for (Station station :
                stations) {

            stationGrid.getChildren().add(
                    createStationCard(
                            station
                    )
            );
        }
    }

    // =========================================================
    // STATION CARD
    // =========================================================

    private StackPane createStationCard(
            Station station) {

        StackPane card =
                new StackPane();

        card.setPrefWidth(380);

        card.setMinHeight(225);

        card.setMaxWidth(500);

        card.setStyle(
                "-fx-background-color: #071720;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(35,220,255,0.15);" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1;"
        );

        DropShadow shadow =
                new DropShadow();

        shadow.setRadius(18);

        shadow.setOffsetY(6);

        shadow.setColor(
                Color.rgb(
                        0,
                        0,
                        0,
                        0.35
                )
        );

        card.setEffect(shadow);

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(
                        19,
                        20,
                        18,
                        20
                )
        );

        HBox top =
                new HBox(10);

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane icon =
                createStationIcon();

        VBox stationText =
                new VBox(3);

        Label name =
                createLabel(
                        station.getStationName(),
                        14,
                        WHITE
                );

        name.setStyle(
                "-fx-font-weight: 900;"
        );

        name.setWrapText(true);

        Label location =
                createLabel(
                        station.getLocation(),
                        9,
                        MUTED
                );

        stationText.getChildren().addAll(
                name,
                location
        );

        HBox.setHgrow(
                stationText,
                Priority.ALWAYS
        );

        VBox status =
                new VBox(3);

        status.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label online =
                createLabel(
                        "● ONLINE",
                        8,
                        GREEN
                );

        online.setStyle(
                "-fx-font-weight: bold;"
        );

        Label id =
                createLabel(
                        String.format(
                                "STATION #%02d",
                                station.getStationId()
                        ),
                        7,
                        MUTED
                );

        status.getChildren().addAll(
                online,
                id
        );

        top.getChildren().addAll(
                icon,
                stationText,
                status
        );

        Region separator =
                new Region();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07);"
        );

        HBox chargerRow =
                new HBox(12);

        chargerRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox chargerInfo =
                new VBox(2);

        Label chargerTitle =
                createLabel(
                        "CHARGING CAPACITY",
                        8,
                        MUTED
                );

        chargerTitle.setStyle(
                "-fx-font-weight: bold;"
        );

        Label chargerValue =
                createLabel(
                        station.getTotalChargers()
                                + " CHARGERS",
                        17,
                        WHITE
                );

        chargerValue.setStyle(
                "-fx-font-weight: 900;"
        );

        chargerInfo.getChildren().addAll(
                chargerTitle,
                chargerValue
        );

        HBox.setHgrow(
                chargerInfo,
                Priority.ALWAYS
        );

        StackPane chargerBadge =
                new StackPane();

        chargerBadge.setMinSize(
                52,
                42
        );

        chargerBadge.setMaxSize(
                52,
                42
        );

        chargerBadge.setStyle(
                "-fx-background-color: rgba(35,220,255,0.08);" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(35,220,255,0.15);" +
                "-fx-border-radius: 10;"
        );

        Label lightning =
                createLabel(
                        "⚡",
                        19,
                        CYAN
                );

        chargerBadge.getChildren().add(
                lightning
        );

        chargerRow.getChildren().addAll(
                chargerInfo,
                chargerBadge
        );

        // -----------------------------------------------------
        // INSTALLED CAPACITY VISUAL
        // -----------------------------------------------------

        StackPane bar =
                new StackPane();

        bar.setPrefHeight(5);

        bar.setMaxWidth(
                Double.MAX_VALUE
        );

        bar.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                "-fx-background-radius: 5;"
        );

        Rectangle fill =
                new Rectangle();

        fill.setHeight(5);

        fill.setArcWidth(5);

        fill.setArcHeight(5);

        fill.setFill(
                Color.rgb(
                        35,
                        220,
                        255,
                        0.75
                )
        );

        double capacity =
                Math.min(
                        station.getTotalChargers()
                                / 20.0,
                        1.0
                );

        fill.widthProperty().bind(
                bar.widthProperty()
                        .multiply(capacity)
        );

        bar.getChildren().add(
                fill
        );

        StackPane.setAlignment(
                fill,
                Pos.CENTER_LEFT
        );

        // -----------------------------------------------------
        // BOTTOM
        // -----------------------------------------------------

        HBox bottom =
                new HBox();

        bottom.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button details =
                new Button(
                        "VIEW STATION  →"
                );

        details.setPrefHeight(34);

        details.setStyle(
                "-fx-background-color: rgba(35,220,255,0.08);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: rgba(35,220,255,0.20);" +
                "-fx-border-radius: 9;" +
                "-fx-text-fill: #23DCFF;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 13;"
        );

        details.setOnAction(
                e -> showStationDetails(
                        station
                )
        );

        bottom.getChildren().add(
                details
        );

        content.getChildren().addAll(
                top,
                separator,
                chargerRow,
                bar,
                bottom
        );

        card.getChildren().add(
                content
        );

        addStationHover(
                card,
                details
        );

        return card;
    }

    // =========================================================
    // STATION ICON
    // =========================================================

    private StackPane createStationIcon() {

        StackPane icon =
                new StackPane();

        icon.setMinSize(
                46,
                46
        );

        icon.setMaxSize(
                46,
                46
        );

        icon.setStyle(
                "-fx-background-color: rgba(35,220,255,0.09);" +
                "-fx-background-radius: 13;" +
                "-fx-border-color: rgba(35,220,255,0.18);" +
                "-fx-border-radius: 13;"
        );

        Label symbol =
                createLabel(
                        "⚡",
                        21,
                        CYAN
                );

        icon.getChildren().add(
                symbol
        );

        return icon;
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void filterStations(
            String query) {

        if (query == null) {

            query = "";
        }

        String value =
                query.trim()
                        .toLowerCase();

        List<Station> filtered =
                new ArrayList<>();

        for (Station station :
                allStations) {

            String name =
                    station.getStationName()
                            .toLowerCase();

            String location =
                    station.getLocation()
                            .toLowerCase();

            if (name.contains(value)
                    || location.contains(value)) {

                filtered.add(
                        station
                );
            }
        }

        displayStations(
                filtered
        );
    }

    // =========================================================
    // ADD STATION
    // =========================================================

    private void showAddStationDialog() {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.initOwner(
                currentStage
        );

        dialog.setTitle(
                "Add Charging Station"
        );

        VBox root =
                new VBox(17);

        root.setPadding(
                new Insets(25)
        );

        root.setPrefWidth(430);

        root.setStyle(
                "-fx-background-color: #06121A;"
        );

        Label heading =
                createLabel(
                        "DEPLOY NEW STATION",
                        20,
                        WHITE
                );

        heading.setStyle(
                "-fx-font-weight: 900;"
        );

        Label subtitle =
                createLabel(
                        "Add a new charging location to the EV network",
                        9,
                        MUTED
                );

        TextField nameField =
                createInput(
                        "Station name"
                );

        TextField locationField =
                createInput(
                        "Location"
                );

        TextField chargerField =
                createInput(
                        "Total number of chargers"
                );

        VBox nameBox =
                inputGroup(
                        "STATION NAME",
                        nameField
                );

        VBox locationBox =
                inputGroup(
                        "LOCATION",
                        locationField
                );

        VBox chargerBox =
                inputGroup(
                        "TOTAL CHARGERS",
                        chargerField
                );

        Label status =
                createLabel(
                        "",
                        9,
                        GREEN
                );

        HBox buttons =
                new HBox(10);

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button cancel =
                new Button(
                        "CANCEL"
                );

        cancel.setPrefHeight(38);

        cancel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: rgba(255,255,255,0.10);" +
                "-fx-border-radius: 9;" +
                "-fx-text-fill: #A7BBC4;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        cancel.setOnAction(
                e -> dialog.close()
        );

        Button save =
                new Button(
                        "CREATE STATION"
                );

        save.setPrefHeight(38);

        save.setStyle(
                "-fx-background-color: #23DCFF;" +
                "-fx-background-radius: 9;" +
                "-fx-text-fill: #021017;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: 900;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 16;"
        );

        save.setOnAction(
                e -> {

                    String name =
                            nameField.getText()
                                    .trim();

                    String location =
                            locationField.getText()
                                    .trim();

                    String chargerText =
                            chargerField.getText()
                                    .trim();

                    if (name.isEmpty()
                            || location.isEmpty()
                            || chargerText.isEmpty()) {

                        status.setText(
                                "Please fill all fields."
                        );

                        status.setTextFill(
                                AMBER
                        );

                        return;
                    }

                    try {

                        int totalChargers =
                                Integer.parseInt(
                                        chargerText
                                );

                        if (totalChargers <= 0) {

                            status.setText(
                                    "Chargers must be greater than 0."
                            );

                            status.setTextFill(
                                    AMBER
                            );

                            return;
                        }

                        Station station =
                                new Station(
                                        name,
                                        location,
                                        totalChargers
                                );

                        stationService.addStation(
                                station
                        );

                        dialog.close();

                        loadStations();

                        showSuccess(
                                "Station created successfully."
                        );

                    } catch (
                            NumberFormatException ex) {

                        status.setText(
                                "Enter a valid charger number."
                        );

                        status.setTextFill(
                                AMBER
                        );

                    } catch (
                            Exception ex) {

                        status.setText(
                                "Unable to create station."
                        );

                        status.setTextFill(
                                Color.rgb(
                                        255,
                                        100,
                                        100
                                )
                        );

                        System.out.println(
                                "Add station error: "
                                        + ex.getMessage()
                        );
                    }
                }
        );

        buttons.getChildren().addAll(
                cancel,
                save
        );

        root.getChildren().addAll(
                heading,
                subtitle,
                nameBox,
                locationBox,
                chargerBox,
                status,
                buttons
        );

        Scene scene =
                new Scene(
                        root
                );

        dialog.setScene(scene);

        dialog.showAndWait();
    }

    // =========================================================
    // INPUT GROUP
    // =========================================================

    private VBox inputGroup(
            String title,
            TextField field) {

        VBox box =
                new VBox(6);

        Label label =
                createLabel(
                        title,
                        8,
                        MUTED
                );

        label.setStyle(
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(
                label,
                field
        );

        return box;
    }

    // =========================================================
    // INPUT
    // =========================================================

    private TextField createInput(
            String prompt) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(40);

        field.setStyle(
                "-fx-background-color: #0B202C;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-radius: 9;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #6F8995;" +
                "-fx-font-size: 10px;" +
                "-fx-padding: 0 12;"
        );

        return field;
    }

    // =========================================================
    // STATION DETAILS
    // =========================================================

    private void showStationDetails(
            Station station) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.initOwner(
                currentStage
        );

        dialog.setTitle(
                "Station Details"
        );

        VBox root =
                new VBox(20);

        root.setPadding(
                new Insets(28)
        );

        root.setPrefWidth(520);

        root.setStyle(
                "-fx-background-color: #06121A;"
        );

        HBox heading =
                new HBox(14);

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane icon =
                createStationIcon();

        VBox titleBox =
                new VBox(4);

        Label title =
                createLabel(
                        station.getStationName(),
                        20,
                        WHITE
                );

        title.setStyle(
                "-fx-font-weight: 900;"
        );

        Label location =
                createLabel(
                        station.getLocation(),
                        10,
                        MUTED
                );

        titleBox.getChildren().addAll(
                title,
                location
        );

        HBox.setHgrow(
                titleBox,
                Priority.ALWAYS
        );

        Label status =
                createLabel(
                        "● ONLINE",
                        9,
                        GREEN
                );

        status.setStyle(
                "-fx-font-weight: bold;"
        );

        heading.getChildren().addAll(
                icon,
                titleBox,
                status
        );

        HBox info =
                new HBox(12);

        info.getChildren().addAll(

                detailBox(
                        "STATION ID",
                        "#" + station.getStationId(),
                        CYAN
                ),

                detailBox(
                        "CHARGERS",
                        String.valueOf(
                                station.getTotalChargers()
                        ),
                        GREEN
                )
        );

        for (javafx.scene.Node node :
                info.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        VBox locationBox =
                detailWide(
                        "LOCATION",
                        station.getLocation()
                );

        VBox capacityBox =
                detailWide(
                        "INSTALLED CAPACITY",
                        station.getTotalChargers()
                                + " charging points"
                );

        Label note =
                createLabel(
                        "Station information is loaded directly from the EV charging database.",
                        9,
                        MUTED
                );

        note.setWrapText(true);

        Button close =
                new Button(
                        "CLOSE"
                );

        close.setPrefHeight(38);

        close.setStyle(
                "-fx-background-color: #23DCFF;" +
                "-fx-background-radius: 9;" +
                "-fx-text-fill: #021017;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: 900;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 18;"
        );

        close.setOnAction(
                e -> dialog.close()
        );

        HBox closeRow =
                new HBox(
                        close
                );

        closeRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        root.getChildren().addAll(
                heading,
                info,
                locationBox,
                capacityBox,
                note,
                closeRow
        );

        Scene scene =
                new Scene(
                        root
                );

        dialog.setScene(scene);

        dialog.showAndWait();
    }

    // =========================================================
    // DETAIL BOX
    // =========================================================

    private StackPane detailBox(
            String title,
            String value,
            Color accent) {

        StackPane box =
                new StackPane();

        box.setMinHeight(80);

        box.setStyle(
                "-fx-background-color: #071A24;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-radius: 12;"
        );

        VBox content =
                new VBox(5);

        content.setPadding(
                new Insets(13)
        );

        Label t =
                createLabel(
                        title,
                        8,
                        MUTED
                );

        t.setStyle(
                "-fx-font-weight: bold;"
        );

        Label v =
                createLabel(
                        value,
                        20,
                        accent
                );

        v.setStyle(
                "-fx-font-weight: 900;"
        );

        content.getChildren().addAll(
                t,
                v
        );

        box.getChildren().add(
                content
        );

        StackPane.setAlignment(
                content,
                Pos.CENTER_LEFT
        );

        return box;
    }

    // =========================================================
    // DETAIL WIDE
    // =========================================================

    private VBox detailWide(
            String title,
            String value) {

        VBox box =
                new VBox(6);

        box.setPadding(
                new Insets(13)
        );

        box.setStyle(
                "-fx-background-color: #071A24;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-radius: 12;"
        );

        Label t =
                createLabel(
                        title,
                        8,
                        MUTED
                );

        t.setStyle(
                "-fx-font-weight: bold;"
        );

        Label v =
                createLabel(
                        value,
                        12,
                        WHITE
                );

        v.setStyle(
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(
                t,
                v
        );

        return box;
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private VBox createEmptyState() {

        VBox box =
                new VBox(10);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(50)
        );

        Label icon =
                createLabel(
                        "⚡",
                        32,
                        CYAN
                );

        Label title =
                createLabel(
                        "NO STATIONS FOUND",
                        14,
                        WHITE
                );

        title.setStyle(
                "-fx-font-weight: 900;"
        );

        Label subtitle =
                createLabel(
                        "Add a charging station or change your search.",
                        9,
                        MUTED
                );

        box.getChildren().addAll(
                icon,
                title,
                subtitle
        );

        return box;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(
            Stage stage) {

        VBox sidebar =
                new VBox(12);

        sidebar.setPrefWidth(
                225
        );

        sidebar.setPadding(
                new Insets(
                        24,
                        14,
                        18,
                        14
                )
        );

        sidebar.setStyle(
                "-fx-background-color: #040D13;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-width: 0 1 0 0;"
        );

        Label logo =
                createLabel(
                        "⚡ SMART EV",
                        23,
                        WHITE
                );

        logo.setStyle(
                "-fx-font-weight: 900;"
        );

        Label sub =
                createLabel(
                        "ADMIN CONTROL CENTER",
                        8,
                        CYAN
                );

        sub.setStyle(
                "-fx-font-weight: bold;"
        );

        VBox identity =
                new VBox(4);

        identity.setPadding(
                new Insets(
                        15,
                        10,
                        15,
                        10
                )
        );

        Label name =
                createLabel(
                        loggedInUser != null
                                ? loggedInUser.getName()
                                : "Administrator",
                        11,
                        WHITE
                );

        name.setStyle(
                "-fx-font-weight: bold;"
        );

        Label role =
                createLabel(
                        "SUPER ADMINISTRATOR",
                        7,
                        MUTED
                );

        HBox online =
                new HBox(6);

        online.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        3.5,
                        GREEN
                );

        Label status =
                createLabel(
                        "SYSTEM ONLINE",
                        7,
                        GREEN
                );

        status.setStyle(
                "-fx-font-weight: bold;"
        );

        online.getChildren().addAll(
                dot,
                status
        );

        identity.getChildren().addAll(
                name,
                role,
                online
        );

        Label navigation =
                createLabel(
                        "NAVIGATION",
                        8,
                        MUTED
                );

        navigation.setPadding(
                new Insets(
                        7,
                        0,
                        2,
                        5
                )
        );

        Button overview =
                navButton(
                        "◈",
                        "Overview",
                        false
                );

        Button stations =
                navButton(
                        "⌁",
                        "Stations",
                        true
                );

        Button chargers =
                navButton(
                        "◉",
                        "Chargers",
                        false
                );

        Button bookings =
                navButton(
                        "▣",
                        "Bookings",
                        false
                );

        Button users =
                navButton(
                        "♙",
                        "Users",
                        false
                );

        Button reports =
                navButton(
                        "▤",
                        "Reports",
                        false
                );

        Button settings =
                navButton(
                        "⚙",
                        "Settings",
                        false
                );

        overview.setOnAction(
                e -> {

                    stopDustAnimation();

                    new AdminDashboardView(
                            loggedInUser
                    ).show(stage);
                }
        );

        chargers.setOnAction(
                e -> {

                    stopDustAnimation();

                    new ChargerManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );

        bookings.setOnAction(
                e -> {

                    stopDustAnimation();

                    new BookingManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );

        users.setOnAction(
                e -> {

                    stopDustAnimation();

                    new UserManagementView(
                            loggedInUser
                    ).show(stage);
                }
        );

        reports.setOnAction(
                e -> {

                    stopDustAnimation();

                    new ReportView(
                            loggedInUser
                    ).show(stage);
                }
        );

        settings.setOnAction(
                e -> {

                    stopDustAnimation();

                    new SettingsView(
                            loggedInUser
                    ).show(stage);
                }
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        VBox system =
                new VBox(5);

        system.setPadding(
                new Insets(12)
        );

        system.setStyle(
                "-fx-background-color: rgba(8,29,39,0.70);" +
                "-fx-background-radius: 11;" +
                "-fx-border-color: rgba(35,220,255,0.12);" +
                "-fx-border-radius: 11;"
        );

        Label systemTitle =
                createLabel(
                        "SYSTEM STATUS",
                        8,
                        MUTED
                );

        Label systemState =
                createLabel(
                        "● ALL SYSTEMS OPERATIONAL",
                        8,
                        GREEN
                );

        systemState.setStyle(
                "-fx-font-weight: bold;"
        );

        Label database =
                createLabel(
                        "MySQL / JDBC",
                        7,
                        MUTED
                );

        system.getChildren().addAll(
                systemTitle,
                systemState,
                database
        );

        Button logout =
                navButton(
                        "↪",
                        "Logout",
                        false
                );

        logout.setOnAction(
                e -> {

                    stopDustAnimation();

                    new LoginView()
                            .show(stage);
                }
        );

        sidebar.getChildren().addAll(
                logo,
                sub,
                identity,
                navigation,
                overview,
                stations,
                chargers,
                bookings,
                users,
                reports,
                settings,
                spacer,
                system,
                logout
        );

        return sidebar;
    }

    // =========================================================
    // STOP DUST
    // =========================================================

    private void stopDustAnimation() {

        if (dustAnimation != null) {

            dustAnimation.stop();
        }
    }

    // =========================================================
    // NAV BUTTON
    // =========================================================

    private Button navButton(
            String icon,
            String text,
            boolean active) {

        Button button =
                new Button(
                        icon + "    " + text
                );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(
                41
        );

        String base =
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 12;";

        if (active) {

            button.setStyle(
                    base +
                    "-fx-background-color: rgba(35,220,255,0.13);" +
                    "-fx-border-color: rgba(35,220,255,0.35);" +
                    "-fx-text-fill: #23DCFF;"
            );

        } else {

            button.setStyle(
                    base +
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: transparent;" +
                    "-fx-text-fill: #8FA6B0;"
            );

            button.setOnMouseEntered(
                    e -> button.setStyle(
                            base +
                            "-fx-background-color: rgba(35,220,255,0.07);" +
                            "-fx-border-color: rgba(35,220,255,0.16);" +
                            "-fx-text-fill: white;"
                    )
            );

            button.setOnMouseExited(
                    e -> button.setStyle(
                            base +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-text-fill: #8FA6B0;"
                    )
            );
        }

        return button;
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private Button actionButton(
            String text,
            boolean primary) {

        Button button =
                new Button(text);

        button.setPrefHeight(
                42
        );

        if (primary) {

            button.setStyle(
                    "-fx-background-color: #23DCFF;" +
                    "-fx-background-radius: 10;" +
                    "-fx-text-fill: #021017;" +
                    "-fx-font-size: 9px;" +
                    "-fx-font-weight: 900;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0 17;"
            );

            button.setOnMouseEntered(
                    e -> button.setStyle(
                            "-fx-background-color: #63E9FF;" +
                            "-fx-background-radius: 10;" +
                            "-fx-text-fill: #021017;" +
                            "-fx-font-size: 9px;" +
                            "-fx-font-weight: 900;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 0 17;"
                    )
            );

            button.setOnMouseExited(
                    e -> button.setStyle(
                            "-fx-background-color: #23DCFF;" +
                            "-fx-background-radius: 10;" +
                            "-fx-text-fill: #021017;" +
                            "-fx-font-size: 9px;" +
                            "-fx-font-weight: 900;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 0 17;"
                    )
            );

        } else {

            button.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.04);" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: rgba(35,220,255,0.13);" +
                    "-fx-border-radius: 10;" +
                    "-fx-text-fill: #B1C2C9;" +
                    "-fx-font-size: 9px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0 15;"
            );

            button.setOnMouseEntered(
                    e -> button.setStyle(
                            "-fx-background-color: rgba(35,220,255,0.08);" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: rgba(35,220,255,0.28);" +
                            "-fx-border-radius: 10;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 9px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 0 15;"
                    )
            );

            button.setOnMouseExited(
                    e -> button.setStyle(
                            "-fx-background-color: rgba(255,255,255,0.04);" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: rgba(35,220,255,0.13);" +
                            "-fx-border-radius: 10;" +
                            "-fx-text-fill: #B1C2C9;" +
                            "-fx-font-size: 9px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 0 15;"
                    )
            );
        }

        return button;
    }

    // =========================================================
    // KPI HOVER
    // =========================================================

    private void addHoverEffect(
            StackPane card,
            Color accent) {

        card.setOnMouseEntered(
                e -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(160),
                                    card
                            );

                    scale.setToX(1.015);

                    scale.setToY(1.015);

                    scale.play();

                    card.setStyle(
                            "-fx-background-color: #0A202C;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: rgba(35,220,255,0.42);" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-width: 1;"
                    );
                }
        );

        card.setOnMouseExited(
                e -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(160),
                                    card
                            );

                    scale.setToX(1);

                    scale.setToY(1);

                    scale.play();

                    card.setStyle(
                            "-fx-background-color: #07141D;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: rgba(35,220,255,0.13);" +
                            "-fx-border-radius: 15;"
                    );
                }
        );
    }

    // =========================================================
    // STATION CARD HOVER
    // =========================================================

    private void addStationHover(
            StackPane card,
            Button details) {

        card.setOnMouseEntered(
                e -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(170),
                                    card
                            );

                    scale.setToX(1.018);

                    scale.setToY(1.018);

                    scale.play();

                    card.setStyle(
                            "-fx-background-color: #0A202C;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-color: rgba(35,220,255,0.42);" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-width: 1;"
                    );
                }
        );

        card.setOnMouseExited(
                e -> {

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(170),
                                    card
                            );

                    scale.setToX(1);

                    scale.setToY(1);

                    scale.play();

                    card.setStyle(
                            "-fx-background-color: #071720;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-color: rgba(35,220,255,0.15);" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-width: 1;"
                    );
                }
        );
    }

    // =========================================================
    // ENTRANCE ANIMATION
    // =========================================================

    private void playEntrance(
            VBox main) {

        main.setOpacity(0);

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(500),
                        main
                );

        fade.setFromValue(0);

        fade.setToValue(1);

        fade.play();
    }

    // =========================================================
    // LABEL
    // =========================================================

    private Label createLabel(
            String text,
            double size,
            Color color) {

        Label label =
                new Label(text);

        label.setTextFill(
                color
        );

        label.setStyle(
                "-fx-font-size: "
                        + size
                        + "px;"
        );

        return label;
    }

    // =========================================================
    // SUCCESS
    // =========================================================

    private void showSuccess(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Smart EV"
        );

        alert.setHeaderText(
                "Operation Successful"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Smart EV"
        );

        alert.setHeaderText(
                title
        );

        alert.setContentText(
                message == null
                        ? "Unknown error."
                        : message
        );

        alert.showAndWait();
    }
}