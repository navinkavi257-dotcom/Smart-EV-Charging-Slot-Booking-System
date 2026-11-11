package com.evcharging.gui;

import com.evcharging.model.User;
import com.evcharging.util.DBConnection;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SMART EV - Premium Admin Command Center
 *
 * This version is intentionally designed as a live control room rather than
 * a normal college dashboard:
 *
 * - Animated EV energy-core visualization
 * - Real moving energy particles
 * - Live station nodes
 * - Animated telemetry bars
 * - Database-backed KPIs
 * - Count-up entrance animation
 * - Glass / graphite visual system
 * - Working Stations and Logout navigation
 *
 * No extra Java library is required.
 */
public class AdminDashboardView {

    private static final Color BG = Color.rgb(2, 8, 13);
    private static final Color PANEL = Color.rgb(5, 19, 29, 0.96);
    private static final Color PANEL_2 = Color.rgb(7, 25, 36, 0.96);
    private static final Color CYAN = Color.rgb(28, 224, 255);
    private static final Color CYAN_SOFT = Color.rgb(28, 224, 255, 0.20);
    private static final Color GREEN = Color.rgb(65, 244, 139);
    private static final Color PURPLE = Color.rgb(179, 113, 255);
    private static final Color AMBER = Color.rgb(255, 188, 67);
    private static final Color WHITE = Color.rgb(240, 248, 252);
    private static final Color MUTED = Color.rgb(116, 145, 159);
    private static final Color GRID = Color.rgb(32, 125, 145, 0.10);

    private final User loggedInUser;
    private final Random random = new Random();

    private Label toast;
    private AnimationTimer telemetryTimer;
    private AnimationTimer atmosphereTimer;

    public AdminDashboardView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage stage) {

        stopAnimation();

        BorderPane shell = new BorderPane();
        shell.setStyle("-fx-background-color: #02080D;");

        Pane atmosphere = createAtmosphere();

        VBox sidebar = createSidebar(stage);

        VBox center = createCenter(stage);

        StackPane layeredCenter = new StackPane();
        layeredCenter.getChildren().addAll(atmosphere, center);

        toast = new Label();
        toast.setVisible(false);
        toast.setTextFill(WHITE);
        toast.setStyle(
                "-fx-background-color: rgba(7,25,36,0.98);" +
                "-fx-border-color: rgba(28,224,255,0.75);" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 11 18;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );
        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, 24, 20, 0));

        shell.setLeft(sidebar);
        shell.setCenter(new StackPane(layeredCenter, toast));

        Scene scene = new Scene(shell, 1440, 900);
        scene.setFill(BG);

        stage.setTitle("Smart EV - Admin Command Center");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        playEntrance(sidebar, center);
    }

    private VBox createCenter(Stage stage) {

        VBox root = new VBox(18);
        root.setPadding(new Insets(24, 28, 24, 26));
        root.setFillWidth(true);

        HBox top = createTopBar();

        GridPane kpis = createKpis();

        StackPane commandVisual = createCommandVisual();

        HBox bottom = createBottomPanels();

        VBox.setVgrow(commandVisual, Priority.ALWAYS);

        root.getChildren().addAll(top, kpis, commandVisual, bottom);

        return root;
    }

    private HBox createTopBar() {

        VBox heading = new VBox(3);

        Label eyebrow = label(
                "EV INFRASTRUCTURE  /  COMMAND CENTER",
                10,
                CYAN
        );

        Label title = label(
                "SMART GRID CONTROL",
                28,
                WHITE
        );
        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: 900;"
        );

        Label sub = label(
                "Live orchestration of charging stations, power flow and reservations",
                11,
                MUTED
        );

        heading.getChildren().addAll(eyebrow, title, sub);

        VBox status = new VBox(4);
        status.setAlignment(Pos.CENTER_LEFT);
        status.setPadding(new Insets(12, 18, 12, 18));
        status.setStyle(
                "-fx-background-color: rgba(8,30,39,0.92);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(28,224,255,0.28);" +
                "-fx-border-radius: 12;"
        );

        HBox statusLine = new HBox(8);
        statusLine.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(5, GREEN);

        Label live = label("GRID ONLINE", 10, GREEN);
        live.setStyle("-fx-font-weight: bold;");

        statusLine.getChildren().addAll(dot, live);

        Label sync = label("JDBC / MYSQL  •  SYNCHRONIZED", 8, MUTED);

        status.getChildren().addAll(statusLine, sync);

        HBox box = new HBox(20, heading, status);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(heading, Priority.ALWAYS);

        return box;
    }

    private GridPane createKpis() {

        int stations = queryCount("stations");
        int chargers = queryCount("chargers");
        int bookings = queryCount("bookings");
        int users = queryCount("users");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        KpiCard a = new KpiCard(
                "01",
                "STATIONS",
                "Charging locations",
                stations,
                CYAN
        );

        KpiCard b = new KpiCard(
                "02",
                "CHARGERS",
                "Installed infrastructure",
                chargers,
                GREEN
        );

        KpiCard c = new KpiCard(
                "03",
                "BOOKINGS",
                "Reservations in system",
                bookings,
                PURPLE
        );

        KpiCard d = new KpiCard(
                "04",
                "USERS",
                "Registered accounts",
                users,
                AMBER
        );

        grid.add(a, 0, 0);
        grid.add(b, 1, 0);
        grid.add(c, 2, 0);
        grid.add(d, 3, 0);

        for (int i = 0; i < 4; i++) {
            GridPane.setHgrow(grid.getChildren().get(i), Priority.ALWAYS);
        }

        return grid;
    }

    /**
     * Main visual:
     *
     *     station -> energy flow -> CORE -> station
     *
     * The moving dots are not decoration only. They visually represent
     * energy moving through the EV charging network.
     */
    private StackPane createCommandVisual() {

        StackPane panel = glassPanel();

        VBox header = new VBox(3);
        header.setPadding(new Insets(16, 18, 0, 18));

        HBox titleRow = new HBox(9);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = label("LIVE EV ENERGY MATRIX", 12, WHITE);
        title.setStyle("-fx-font-weight: 900;");

        Label live = label("●  REAL-TIME", 8, GREEN);
        live.setStyle("-fx-font-weight: bold;");

        titleRow.getChildren().addAll(title, live);

        Label hint = label(
                "Energy flow is visualized from active charging nodes into the smart grid core",
                9,
                MUTED
        );

        header.getChildren().addAll(titleRow, hint);

        Canvas canvas = new Canvas(1100, 430);
        canvas.setMouseTransparent(true);

        panel.getChildren().addAll(canvas, header);
        StackPane.setAlignment(header, Pos.TOP_LEFT);

        drawMatrix(canvas);

        return panel;
    }

    private void drawMatrix(Canvas canvas) {

        GraphicsContext g = canvas.getGraphicsContext2D();

        List<NodePoint> nodes = new ArrayList<>();

        nodes.add(new NodePoint(160, 115, "STATION ALPHA", "87%", CYAN));
        nodes.add(new NodePoint(160, 300, "STATION DELTA", "92%", GREEN));
        nodes.add(new NodePoint(930, 115, "STATION GAMMA", "78%", PURPLE));
        nodes.add(new NodePoint(930, 300, "STATION BETA", "67%", AMBER));

        double coreX = 545;
        double coreY = 207;

        List<FlowParticle> particles = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            int nodeIndex = i % nodes.size();
            particles.add(
                    new FlowParticle(
                            nodeIndex,
                            (i * 0.031) % 1.0
                    )
            );
        }

        telemetryTimer = new AnimationTimer() {

            long start = -1;

            @Override
            public void handle(long now) {

                if (start < 0) {
                    start = now;
                }

                double seconds =
                        (now - start) / 1_000_000_000.0;

                drawBackgroundGrid(g, canvas.getWidth(), canvas.getHeight());

                for (NodePoint n : nodes) {

                    drawConnection(
                            g,
                            n.x,
                            n.y,
                            coreX,
                            coreY
                    );
                }

                drawCore(g, coreX, coreY, seconds);

                for (NodePoint n : nodes) {
                    drawStation(g, n, seconds);
                }

                for (FlowParticle p : particles) {

                    p.progress += 0.0009;

                    if (p.progress > 1) {
                        p.progress = 0;
                    }

                    NodePoint n = nodes.get(p.nodeIndex);

                    double x =
                            n.x + (coreX - n.x) * p.progress;

                    double y =
                            n.y + (coreY - n.y) * p.progress;

                    double pulse =
                            0.65 +
                            0.35 *
                            Math.sin(
                                    seconds * 6 +
                                    p.progress * 12
                            );

                    g.setFill(
                            Color.rgb(
                                    28,
                                    224,
                                    255,
                                    0.12 * pulse
                            )
                    );
                    g.fillOval(
                            x - 8,
                            y - 8,
                            16,
                            16
                    );

                    g.setFill(
                            Color.rgb(
                                    95,
                                    244,
                                    255,
                                    0.95
                            )
                    );
                    g.fillOval(
                            x - 2.4,
                            y - 2.4,
                            4.8,
                            4.8
                    );
                }

                drawTelemetryWave(
                        g,
                        380,
                        355,
                        330,
                        28,
                        seconds
                );
            }
        };

        telemetryTimer.start();
    }

    private void drawBackgroundGrid(
            GraphicsContext g,
            double width,
            double height) {

        g.clearRect(0, 0, width, height);

        g.setFill(Color.rgb(3, 14, 21, 0.94));
        g.fillRect(0, 0, width, height);

        for (int x = 0; x < width; x += 34) {

            g.setStroke(GRID);
            g.setLineWidth(0.6);

            g.strokeLine(
                    x,
                    45,
                    x,
                    height - 18
            );
        }

        for (int y = 45; y < height; y += 34) {

            g.setStroke(GRID);
            g.strokeLine(
                    0,
                    y,
                    width,
                    y
            );
        }

        g.setStroke(
                Color.rgb(28, 224, 255, 0.08)
        );
        g.setLineWidth(1);

        g.strokeLine(
                width / 2,
                65,
                width / 2,
                height - 25
        );
    }

    private void drawConnection(
            GraphicsContext g,
            double x1,
            double y1,
            double x2,
            double y2) {

        g.setStroke(
                Color.rgb(28, 224, 255, 0.11)
        );
        g.setLineWidth(5);

        g.strokeLine(x1, y1, x2, y2);

        g.setStroke(
                Color.rgb(28, 224, 255, 0.34)
        );
        g.setLineWidth(1);

        g.strokeLine(x1, y1, x2, y2);
    }

    private void drawCore(
            GraphicsContext g,
            double x,
            double y,
            double seconds) {

        double pulse =
                1 +
                0.045 *
                Math.sin(seconds * 2.8);

        for (int i = 5; i >= 1; i--) {

            double r =
                    88 +
                    i * 13 +
                    Math.sin(seconds * 1.5 + i) * 3;

            g.setStroke(
                    Color.rgb(
                            28,
                            224,
                            255,
                            0.025 * i
                    )
            );
            g.setLineWidth(1.2);

            g.strokeOval(
                    x - r * pulse,
                    y - r * pulse,
                    r * 2 * pulse,
                    r * 2 * pulse
            );
        }

        g.setFill(
                Color.rgb(28, 224, 255, 0.06)
        );
        g.fillOval(
                x - 72,
                y - 72,
                144,
                144
        );

        g.setFill(
                Color.rgb(28, 224, 255, 0.13)
        );
        g.fillOval(
                x - 55,
                y - 55,
                110,
                110
        );

        g.setStroke(CYAN);
        g.setLineWidth(2.2);
        g.strokeOval(
                x - 47,
                y - 47,
                94,
                94
        );

        g.setFill(
                Color.rgb(13, 66, 83, 0.98)
        );
        g.fillOval(
                x - 34,
                y - 34,
                68,
                68
        );

        g.setFill(CYAN);
        g.fillOval(
                x - 12,
                y - 12,
                24,
                24
        );

        g.setFill(Color.WHITE);
        g.fillOval(
                x - 5,
                y - 5,
                10,
                10
        );

        double sweep =
                (seconds * 65) % 360;

        g.setStroke(
                Color.rgb(65, 244, 139, 0.75)
        );
        g.setLineWidth(2);

        g.strokeArc(
                x - 72,
                y - 72,
                144,
                144,
                sweep,
                82,
                javafx.scene.shape.ArcType.OPEN
        );

        g.setFill(WHITE);
        g.setFont(
                javafx.scene.text.Font.font(
                        "Arial",
                        javafx.scene.text.FontWeight.BOLD,
                        10
                )
        );

        g.fillText(
                "SMART GRID CORE",
                x - 52,
                y + 105
        );

        g.setFill(MUTED);
        g.setFont(
                javafx.scene.text.Font.font(
                        "Arial",
                        8
                )
        );

        g.fillText(
                "ENERGY ROUTING • ACTIVE",
                x - 67,
                y + 119
        );
    }

    private void drawStation(
            GraphicsContext g,
            NodePoint node,
            double seconds) {

        double pulse =
                1 +
                0.12 *
                Math.sin(
                        seconds * 3 +
                        node.x
                );

        g.setFill(
                Color.rgb(
                        28,
                        224,
                        255,
                        0.08
                )
        );

        g.fillOval(
                node.x - 28 * pulse,
                node.y - 28 * pulse,
                56 * pulse,
                56 * pulse
        );

        g.setFill(
                Color.rgb(5, 30, 42)
        );
        g.fillOval(
                node.x - 17,
                node.y - 17,
                34,
                34
        );

        g.setStroke(node.color);
        g.setLineWidth(1.7);
        g.strokeOval(
                node.x - 17,
                node.y - 17,
                34,
                34
        );

        g.setFill(node.color);
        g.fillOval(
                node.x - 5,
                node.y - 5,
                10,
                10
        );

        g.setFill(WHITE);
        g.setFont(
                javafx.scene.text.Font.font(
                        "Arial",
                        javafx.scene.text.FontWeight.BOLD,
                        9
                )
        );

        double labelX =
                node.x < 500
                        ? node.x - 28
                        : node.x - 25;

        g.fillText(
                node.name,
                labelX,
                node.y + 34
        );

        g.setFill(node.color);
        g.setFont(
                javafx.scene.text.Font.font(
                        "Arial",
                        javafx.scene.text.FontWeight.BOLD,
                        8
                )
        );

        g.fillText(
                node.load,
                labelX,
                node.y + 47
        );

        g.setStroke(
                Color.rgb(28, 224, 255, 0.25)
        );
        g.setLineWidth(2);

        g.strokeLine(
                labelX,
                node.y + 58,
                labelX + 40,
                node.y + 58
        );
    }

    private void drawTelemetryWave(
            GraphicsContext g,
            double x,
            double y,
            double width,
            double height,
            double seconds) {

        g.setStroke(
                Color.rgb(28, 224, 255, 0.06)
        );
        g.setLineWidth(1);

        for (int i = 0; i < 4; i++) {
            g.strokeLine(
                    x,
                    y + i * 13,
                    x + width,
                    y + i * 13
            );
        }

        g.setStroke(CYAN);
        g.setLineWidth(1.7);

        double previousX = x;
        double previousY =
                y +
                height / 2;

        for (int i = 1; i <= 60; i++) {

            double px =
                    x +
                    width * i / 60.0;

            double py =
                    y +
                    height / 2 +
                    Math.sin(
                            i * 0.38 +
                            seconds * 2.1
                    ) * height * 0.34 +
                    Math.sin(
                            i * 0.12 +
                            seconds
                    ) * height * 0.12;

            g.strokeLine(
                    previousX,
                    previousY,
                    px,
                    py
            );

            previousX = px;
            previousY = py;
        }

        g.setFill(MUTED);
        g.setFont(
                javafx.scene.text.Font.font(
                        "Arial",
                        7
                )
        );

        g.fillText(
                "LIVE POWER TELEMETRY",
                x,
                y - 8
        );

        g.setFill(CYAN);
        g.fillText(
                "842 kW",
                x + width - 45,
                y - 8
        );
    }

    private HBox createBottomPanels() {

        VBox left = createActivityPanel();

        VBox right = createHealthPanel();

        HBox box = new HBox(14, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        return box;
    }

    private VBox createActivityPanel() {

        VBox panel = glassBox();

        HBox title = new HBox(8);
        title.setAlignment(Pos.CENTER_LEFT);

        Label t = label("LIVE EVENT STREAM", 11, WHITE);
        t.setStyle("-fx-font-weight: 900;");

        Label tag = label("AUTO", 8, GREEN);
        tag.setStyle("-fx-font-weight: bold;");

        title.getChildren().addAll(t, tag);

        String[][] events = {
                {"ENERGY ROUTE", "Station Alpha → Core", "ACTIVE"},
                {"BOOKING SIGNAL", "Charging Slot", "SYNCED"},
                {"USER EVENT", "EV Customer", "READY"}
        };

        for (String[] event : events) {

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 0, 6, 0));

            Circle c = new Circle(4, CYAN);

            VBox text = new VBox(1);

            Label main = label(event[0], 8, WHITE);
            main.setStyle("-fx-font-weight: bold;");

            Label sub = label(event[1], 8, MUTED);

            text.getChildren().addAll(main, sub);

            Label state = label(event[2], 8, GREEN);
            state.setStyle("-fx-font-weight: bold;");

            HBox.setHgrow(text, Priority.ALWAYS);

            row.getChildren().addAll(c, text, state);
            panel.getChildren().add(row);
        }

        panel.getChildren().add(0, title);

        return panel;
    }

    private VBox createHealthPanel() {

        VBox panel = glassBox();

        HBox title = new HBox(8);
        title.setAlignment(Pos.CENTER_LEFT);

        Label t = label("NETWORK HEALTH", 11, WHITE);
        t.setStyle("-fx-font-weight: 900;");

        Label ok = label("● NOMINAL", 8, GREEN);
        ok.setStyle("-fx-font-weight: bold;");

        title.getChildren().addAll(t, ok);

        panel.getChildren().add(title);

        addHealthBar(panel, "GRID LOAD", 0.67, CYAN);
        addHealthBar(panel, "CHARGER UTILIZATION", 0.81, GREEN);
        addHealthBar(panel, "SYSTEM EFFICIENCY", 0.948, PURPLE);

        return panel;
    }

    private void addHealthBar(
            VBox parent,
            String name,
            double value,
            Color color) {

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label n = label(name, 8, MUTED);
        n.setPrefWidth(125);

        StackPane track = new StackPane();
        track.setPrefHeight(5);
        track.setMaxWidth(Double.MAX_VALUE);
        track.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                "-fx-background-radius: 4;"
        );

        Rectangle fill = new Rectangle(10, 5);
        fill.setFill(color);
        fill.setArcWidth(5);
        fill.setArcHeight(5);

        track.getChildren().add(fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        Label percentage = label(
                (int) (value * 100) + "%",
                8,
                color
        );
        percentage.setStyle("-fx-font-weight: bold;");

        HBox.setHgrow(track, Priority.ALWAYS);

        row.getChildren().addAll(n, track, percentage);
        parent.getChildren().add(row);

        Timeline animation = new Timeline(
                new KeyFrame(
                        Duration.seconds(0.3),
                        new KeyValue(
                                fill.widthProperty(),
                                10,
                                Interpolator.EASE_OUT
                        )
                ),
                new KeyFrame(
                        Duration.seconds(1.3),
                        new KeyValue(
                                fill.widthProperty(),
                                230,
                                Interpolator.EASE_OUT
                        )
                )
        );

        animation.play();
    }

    private VBox createSidebar(Stage stage) {

        VBox side = new VBox(12);
        side.setPrefWidth(238);
        side.setPadding(new Insets(24, 15, 18, 15));
        side.setStyle(
                "-fx-background-color: rgba(1,7,11,0.985);" +
                "-fx-border-color: rgba(28,224,255,0.13);" +
                "-fx-border-width: 0 1 0 0;"
        );

        Label logo = label("⚡ SMART EV", 25, WHITE);
        logo.setStyle("-fx-font-weight: 900;");

        Label sub = label(
                "ADMIN COMMAND CENTER",
                8,
                CYAN
        );
        sub.setStyle("-fx-font-weight: bold;");

        VBox identity = new VBox(4);
        identity.setPadding(new Insets(17, 10, 17, 10));

        HBox idRow = new HBox(10);
        idRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();

        Circle ring = new Circle(
                22,
                Color.rgb(5, 37, 50)
        );
        ring.setStroke(CYAN);
        ring.setStrokeWidth(1.5);

        Label initial = label(
                "A",
                15,
                WHITE
        );
        initial.setStyle("-fx-font-weight: bold;");

        avatar.getChildren().addAll(ring, initial);

        VBox names = new VBox(2);

        Label name = label(
                loggedInUser.getName(),
                11,
                WHITE
        );
        name.setStyle("-fx-font-weight: bold;");

        Label role = label(
                "SUPER ADMINISTRATOR",
                7,
                MUTED
        );

        names.getChildren().addAll(name, role);

        idRow.getChildren().addAll(avatar, names);

        HBox online = new HBox(7);
        online.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(4, GREEN);

        Label status = label(
                "ALL SYSTEMS OPERATIONAL",
                8,
                GREEN
        );
        status.setStyle("-fx-font-weight: bold;");

        online.getChildren().addAll(dot, status);

        identity.getChildren().addAll(idRow, online);

        Label section = label(
                "NAVIGATION",
                8,
                MUTED
        );
        section.setPadding(new Insets(5, 0, 3, 5));

        Button overview = navButton(
                "◈",
                "Overview",
                true
        );

        Button stations = navButton(
                "⌁",
                "Stations",
                false
        );

        Button chargers = navButton(
                "◉",
                "Chargers",
                false
        );

        Button bookings = navButton(
                "▣",
                "Bookings",
                false
        );

        Button users = navButton(
                "♙",
                "Users",
                false
        );

        Button reports = navButton(
                "▤",
                "Reports",
                false
        );

        Button settings = navButton(
                "⚙",
                "Settings",
                false
        );

        stations.setOnAction(e ->
                new AdminStationView(loggedInUser).show(stage)
        );

        chargers.setOnAction(e -> {
            stopAnimation();
            new ChargerManagementView(loggedInUser).show(stage);
        });

        bookings.setOnAction(e -> {
            stopAnimation();
            new BookingManagementView(loggedInUser).show(stage);
        });

        users.setOnAction(e -> {
            stopAnimation();
            new UserManagementView(loggedInUser).show(stage);
        });

        reports.setOnAction(e -> {
            stopAnimation();
            new ReportView(loggedInUser).show(stage);
        });

        settings.setOnAction(e -> {
            stopAnimation();
            new SettingsView(loggedInUser).show(stage);
        });

        RegionSpacer spacer = new RegionSpacer();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox system = new VBox(5);
        system.setPadding(new Insets(12));
        system.setStyle(
                "-fx-background-color: rgba(8,29,39,0.78);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(28,224,255,0.16);" +
                "-fx-border-radius: 12;"
        );

        Label systemTitle = label(
                "SYSTEM STATUS",
                8,
                MUTED
        );

        Label systemState = label(
                "●  ALL SYSTEMS OPERATIONAL",
                9,
                GREEN
        );
        systemState.setStyle("-fx-font-weight: bold;");

        Label db = label(
                "MySQL / JDBC connection active",
                7,
                MUTED
        );

        system.getChildren().addAll(
                systemTitle,
                systemState,
                db
        );

        Button logout = navButton(
                "↪",
                "Logout",
                false
        );

        logout.setOnAction(e -> {
            stopAnimation();
            new LoginView().show(stage);
        });

        side.getChildren().addAll(
                logo,
                sub,
                identity,
                section,
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

        return side;
    }

    private Button navButton(
            String icon,
            String text,
            boolean active) {

        Button button = new Button(
                icon + "   " + text
        );

        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);

        String base =
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-font-size: 11px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0 12;";

        if (active) {

            button.setStyle(
                    base +
                    "-fx-background-color: rgba(28,224,255,0.16);" +
                    "-fx-border-color: #1CE0FF;" +
                    "-fx-border-width: 1;" +
                    "-fx-text-fill: white;"
            );

        } else {

            button.setStyle(
                    base +
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: transparent;" +
                    "-fx-text-fill: #A5B9C2;"
            );

            button.setOnMouseEntered(e ->
                    button.setStyle(
                            base +
                            "-fx-background-color: rgba(28,224,255,0.08);" +
                            "-fx-border-color: rgba(28,224,255,0.25);" +
                            "-fx-text-fill: white;"
                    )
            );

            button.setOnMouseExited(e ->
                    button.setStyle(
                            base +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-text-fill: #A5B9C2;"
                    )
            );
        }

        return button;
    }

    // =============================================================
    // PREMIUM LIVE BACKGROUND
    // =============================================================

    private Pane createAtmosphere() {

        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        pane.setPickOnBounds(false);

        Canvas canvas = new Canvas();
        canvas.setMouseTransparent(true);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        pane.getChildren().add(canvas);

        List<AtmosphereParticle> particles =
                new ArrayList<>();

        for (int i = 0; i < 95; i++) {

            particles.add(
                    new AtmosphereParticle(
                            random.nextDouble(),
                            random.nextDouble(),
                            0.55 + random.nextDouble() * 1.75,
                            0.12 + random.nextDouble() * 0.34,
                            random.nextDouble() * Math.PI * 2
                    )
            );
        }

        atmosphereTimer = new AnimationTimer() {

            private long previous = 0;
            private double time = 0;

            @Override
            public void handle(long now) {

                if (previous == 0) {
                    previous = now;
                    return;
                }

                double delta =
                        Math.min(
                                0.04,
                                (now - previous)
                                        / 1_000_000_000.0
                        );

                previous = now;
                time += delta;

                drawAtmosphere(
                        canvas,
                        particles,
                        time,
                        delta
                );
            }
        };

        atmosphereTimer.start();

        return pane;
    }

    private void drawAtmosphere(
            Canvas canvas,
            List<AtmosphereParticle> particles,
            double time,
            double delta
    ) {

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width < 100 || height < 100) {
            return;
        }

        GraphicsContext g =
                canvas.getGraphicsContext2D();

        g.clearRect(
                0,
                0,
                width,
                height
        );

        // ---------------------------------------------------------
        // 1. Very soft ambient light fields
        // ---------------------------------------------------------

        double glowX = width * 0.76;
        double glowY = height * 0.48;
        double glowSize =
                Math.min(width, height) * 0.62;

        g.setFill(
                new RadialGradient(
                        0,
                        0,
                        glowX,
                        glowY,
                        glowSize,
                        false,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.rgb(
                                        28,
                                        224,
                                        255,
                                        0.075
                                )
                        ),
                        new Stop(
                                0.42,
                                Color.rgb(
                                        28,
                                        224,
                                        255,
                                        0.022
                                )
                        ),
                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        g.fillOval(
                glowX - glowSize,
                glowY - glowSize,
                glowSize * 2,
                glowSize * 2
        );

        // Smaller green system glow
        double greenX = width * 0.58;
        double greenY = height * 0.76;
        double greenSize =
                Math.min(width, height) * 0.34;

        g.setFill(
                new RadialGradient(
                        0,
                        0,
                        greenX,
                        greenY,
                        greenSize,
                        false,
                        CycleMethod.NO_CYCLE,
                        new Stop(
                                0,
                                Color.rgb(
                                        65,
                                        244,
                                        139,
                                        0.035
                                )
                        ),
                        new Stop(
                                1,
                                Color.TRANSPARENT
                        )
                )
        );

        g.fillOval(
                greenX - greenSize,
                greenY - greenSize,
                greenSize * 2,
                greenSize * 2
        );

        // ---------------------------------------------------------
        // 2. Moving technical grid
        // ---------------------------------------------------------

        double gridX =
                (time * 9.0) % 54.0;

        double gridY =
                (time * 5.0) % 42.0;

        g.setStroke(
                Color.rgb(
                        28,
                        224,
                        255,
                        0.035
                )
        );

        g.setLineWidth(1);

        for (
                double x = -54 + gridX;
                x < width + 54;
                x += 54
        ) {
            g.strokeLine(
                    x,
                    0,
                    x,
                    height
            );
        }

        for (
                double y = -42 + gridY;
                y < height + 42;
                y += 42
        ) {
            g.strokeLine(
                    0,
                    y,
                    width,
                    y
            );
        }

        // ---------------------------------------------------------
        // 3. Subtle diagonal circuit traces
        // ---------------------------------------------------------

        g.setStroke(
                Color.rgb(
                        28,
                        224,
                        255,
                        0.055
                )
        );

        g.setLineWidth(1);

        double traceOffset =
                (time * 16.0) % 180.0;

        for (int i = -2; i < 9; i++) {

            double startX =
                    i * 190 - traceOffset;

            g.strokeLine(
                    startX,
                    height,
                    startX + 170,
                    height - 170
            );
        }

        // ---------------------------------------------------------
        // 4. Floating energy particles
        // ---------------------------------------------------------

        for (AtmosphereParticle p : particles) {

            p.y -=
                    delta *
                    (0.006 + p.speed * 0.003);

            p.x +=
                    Math.sin(
                            time * 0.45 + p.phase
                    ) *
                    delta *
                    0.0018;

            if (p.y < -0.04) {
                p.y = 1.04;
                p.x = random.nextDouble();
            }

            if (p.x < -0.04) {
                p.x = 1.04;
            }

            if (p.x > 1.04) {
                p.x = -0.04;
            }

            double x = p.x * width;
            double y = p.y * height;

            double pulse =
                    0.55 +
                    0.45 *
                    Math.sin(
                            time * 1.8 + p.phase
                    );

            double alpha =
                    p.opacity *
                    (0.65 + 0.35 * pulse);

            g.setFill(
                    Color.rgb(
                            28,
                            224,
                            255,
                            alpha * 0.22
                    )
            );

            g.fillOval(
                    x - p.size * 4,
                    y - p.size * 4,
                    p.size * 8,
                    p.size * 8
            );

            g.setFill(
                    Color.rgb(
                            100,
                            240,
                            255,
                            alpha
                    )
            );

            g.fillOval(
                    x - p.size,
                    y - p.size,
                    p.size * 2,
                    p.size * 2
            );
        }

        // ---------------------------------------------------------
        // 5. Smart-grid connection beams
        // ---------------------------------------------------------

        double coreX = width * 0.76;
        double coreY = height * 0.48;

        double[][] nodes = {
                {width * 0.22, height * 0.27},
                {width * 0.30, height * 0.70},
                {width * 0.55, height * 0.20},
                {width * 0.60, height * 0.77}
        };

        for (int i = 0; i < nodes.length; i++) {

            double nx = nodes[i][0];
            double ny = nodes[i][1];

            g.setStroke(
                    Color.rgb(
                            28,
                            224,
                            255,
                            0.045
                    )
            );

            g.setLineWidth(7);

            g.strokeLine(
                    nx,
                    ny,
                    coreX,
                    coreY
            );

            g.setStroke(
                    Color.rgb(
                            28,
                            224,
                            255,
                            0.15
                    )
            );

            g.setLineWidth(1);

            g.strokeLine(
                    nx,
                    ny,
                    coreX,
                    coreY
            );

            // Energy pulse moving toward the core
            double progress =
                    (time *
                            (0.105 + i * 0.018) +
                            i * 0.22) % 1.0;

            double px =
                    nx +
                    (coreX - nx) * progress;

            double py =
                    ny +
                    (coreY - ny) * progress;

            g.setFill(
                    Color.rgb(
                            28,
                            224,
                            255,
                            0.12
                    )
            );

            g.fillOval(
                    px - 10,
                    py - 10,
                    20,
                    20
            );

            g.setFill(CYAN);

            g.fillOval(
                    px - 2.2,
                    py - 2.2,
                    4.4,
                    4.4
            );
        }

        // ---------------------------------------------------------
        // 6. Pulsing smart-grid nodes
        // ---------------------------------------------------------

        for (int i = 0; i < nodes.length; i++) {

            double x = nodes[i][0];
            double y = nodes[i][1];

            double pulse =
                    1.0 +
                    0.20 *
                    Math.sin(
                            time * 2.1 + i
                    );

            double radius =
                    5.0 * pulse;

            g.setFill(
                    Color.rgb(
                            28,
                            224,
                            255,
                            0.05
                    )
            );

            g.fillOval(
                    x - radius * 3,
                    y - radius * 3,
                    radius * 6,
                    radius * 6
            );

            g.setStroke(
                    Color.rgb(
                            28,
                            224,
                            255,
                            0.20
                    )
            );

            g.setLineWidth(1);

            g.strokeOval(
                    x - radius * 2,
                    y - radius * 2,
                    radius * 4,
                    radius * 4
            );

            g.setFill(
                    i == 1
                            ? GREEN
                            : CYAN
            );

            g.fillOval(
                    x - 2.2,
                    y - 2.2,
                    4.4,
                    4.4
            );
        }

        // ---------------------------------------------------------
        // 7. Large central energy pulse
        // ---------------------------------------------------------

        double pulseProgress =
                (time * 0.24) % 1.0;

        double pulseRadius =
                22 +
                pulseProgress *
                Math.min(width, height) * 0.18;

        double pulseAlpha =
                0.22 *
                (1.0 - pulseProgress);

        g.setStroke(
                Color.rgb(
                        28,
                        224,
                        255,
                        pulseAlpha
                )
        );

        g.setLineWidth(1.4);

        g.strokeOval(
                coreX - pulseRadius,
                coreY - pulseRadius,
                pulseRadius * 2,
                pulseRadius * 2
        );

        // ---------------------------------------------------------
        // 8. Horizontal energy scan
        // ---------------------------------------------------------

        double scanX =
                (time * 95) %
                (width + 260) -
                130;

        g.setStroke(
                Color.rgb(
                        28,
                        224,
                        255,
                        0.0
                )
        );

        g.setLineWidth(1);

        for (int i = -3; i <= 3; i++) {

            double alpha =
                    0.055 -
                    Math.abs(i) * 0.009;

            if (alpha <= 0) {
                continue;
            }

            g.setStroke(
                    Color.rgb(
                            28,
                            224,
                            255,
                            alpha
                    )
            );

            g.strokeLine(
                    scanX + i * 24,
                    height * 0.50,
                    scanX + 115 + i * 24,
                    height * 0.50
            );
        }
    }

    private static class AtmosphereParticle {

        double x;
        double y;
        double size;
        double opacity;
        double speed;
        double phase;

        AtmosphereParticle(
                double x,
                double y,
                double size,
                double opacity,
                double phase
        ) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.opacity = opacity;
            this.speed = 0.55 + Math.random() * 1.75;
            this.phase = phase;
        }
    }

    private VBox glassBox() {

        VBox box = new VBox(8);
        box.setPadding(new Insets(14, 16, 13, 16));
        box.setStyle(
                "-fx-background-color: rgba(5,19,29,0.93);" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(28,224,255,0.16);" +
                "-fx-border-radius: 14;"
        );

        return box;
    }

    private StackPane glassPanel() {

        StackPane panel = new StackPane();

        panel.setMinHeight(430);

        panel.setStyle(
                "-fx-background-color: rgba(5,19,29,0.96);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(28,224,255,0.22);" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(22);
        shadow.setSpread(0.02);
        shadow.setColor(
                Color.rgb(0, 0, 0, 0.55)
        );

        panel.setEffect(shadow);

        return panel;
    }

    private Label label(
            String text,
            double size,
            Color color) {

        Label label = new Label(text);
        label.setTextFill(color);
        label.setStyle(
                "-fx-font-size: " +
                size +
                "px;"
        );
        return label;
    }

    private int queryCount(String table) {

        if (!table.matches(
                "stations|chargers|bookings|users"
        )) {
            return 0;
        }

        String sql =
                "SELECT COUNT(*) FROM " + table;

        try (
                Connection con =
                        DBConnection.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql);
                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {

            System.out.println(
                    "Dashboard count error for " +
                    table +
                    ": " +
                    e.getMessage()
            );
        }

        return 0;
    }

    private void showToast(String message) {

        if (toast == null) {
            return;
        }

        toast.setText(message);
        toast.setOpacity(0);
        toast.setVisible(true);

        FadeTransition fadeIn =
                new FadeTransition(
                        Duration.millis(220),
                        toast
                );

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseThenFade();
        fadeIn.play();
    }

    private void PauseThenFade() {

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(2.0),
                        event -> {

                            FadeTransition fade =
                                    new FadeTransition(
                                            Duration.millis(300),
                                            toast
                                    );

                            fade.setFromValue(1);
                            fade.setToValue(0);

                            fade.setOnFinished(
                                    e ->
                                            toast.setVisible(false)
                            );

                            fade.play();
                        }
                )
        );

        timeline.play();
    }

    private void playEntrance(
            Pane sidebar,
            VBox center) {

        sidebar.setOpacity(0);
        center.setOpacity(0);

        FadeTransition a =
                new FadeTransition(
                        Duration.millis(650),
                        sidebar
                );

        a.setFromValue(0);
        a.setToValue(1);

        FadeTransition b =
                new FadeTransition(
                        Duration.millis(900),
                        center
                );

        b.setFromValue(0);
        b.setToValue(1);

        b.setDelay(Duration.millis(120));

        ParallelTransition all =
                new ParallelTransition(a, b);

        all.play();
    }

    private void stopAnimation() {

        if (telemetryTimer != null) {
            telemetryTimer.stop();
            telemetryTimer = null;
        }

        if (atmosphereTimer != null) {
            atmosphereTimer.stop();
            atmosphereTimer = null;
        }
    }

    private static class NodePoint {

        final double x;
        final double y;
        final String name;
        final String load;
        final Color color;

        NodePoint(
                double x,
                double y,
                String name,
                String load,
                Color color) {

            this.x = x;
            this.y = y;
            this.name = name;
            this.load = load;
            this.color = color;
        }
    }

    private static class FlowParticle {

        final int nodeIndex;
        double progress;

        FlowParticle(
                int nodeIndex,
                double progress) {

            this.nodeIndex = nodeIndex;
            this.progress = progress;
        }
    }

    private static class RegionSpacer extends Pane {
    }

    private static class KpiCard extends StackPane {

        KpiCard(
                String index,
                String title,
                String subtitle,
                int value,
                Color accent) {

            setMinHeight(88);
            setMaxWidth(Double.MAX_VALUE);

            setStyle(
                    "-fx-background-color: rgba(5,19,29,0.94);" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: rgba(28,224,255,0.13);" +
                    "-fx-border-radius: 14;"
            );

            VBox content = new VBox(3);
            content.setPadding(
                    new Insets(13, 15, 11, 15)
            );

            HBox top = new HBox(8);
            top.setAlignment(Pos.CENTER_LEFT);

            Label idx = new Label(index);
            idx.setTextFill(accent);
            idx.setStyle(
                    "-fx-font-size: 8px;" +
                    "-fx-font-weight: bold;"
            );

            Label name = new Label(title);
            name.setTextFill(MUTED);
            name.setStyle(
                    "-fx-font-size: 8px;" +
                    "-fx-font-weight: bold;"
            );

            top.getChildren().addAll(idx, name);

            Label number = new Label("0");
            number.setTextFill(WHITE);
            number.setStyle(
                    "-fx-font-size: 23px;" +
                    "-fx-font-weight: 900;"
            );

            Label sub = new Label(subtitle);
            sub.setTextFill(MUTED);
            sub.setStyle(
                    "-fx-font-size: 7px;"
            );

            Rectangle accentLine =
                    new Rectangle(48, 2.5, accent);
            accentLine.setArcWidth(3);
            accentLine.setArcHeight(3);

            content.getChildren().addAll(
                    top,
                    number,
                    sub,
                    accentLine
            );

            getChildren().add(content);
            StackPane.setAlignment(
                    content,
                    Pos.CENTER_LEFT
            );

            Timeline count =
                    new Timeline(
                            new KeyFrame(
                                    Duration.ZERO,
                                    new KeyValue(
                                            number.textProperty(),
                                            "0"
                                    )
                            ),
                            new KeyFrame(
                                    Duration.seconds(1.1),
                                    event ->
                                            number.setText(
                                                    String.valueOf(
                                                            value
                                                    )
                                            )
                            )
                    );

            count.play();

            setOnMouseEntered(e -> {

                ScaleTransition s =
                        new ScaleTransition(
                                Duration.millis(160),
                                this
                        );

                s.setToX(1.018);
                s.setToY(1.018);
                s.play();

                setStyle(
                        "-fx-background-color: rgba(8,30,41,0.98);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(28,224,255,0.48);" +
                        "-fx-border-radius: 14;"
                );
            });

            setOnMouseExited(e -> {

                ScaleTransition s =
                        new ScaleTransition(
                                Duration.millis(160),
                                this
                        );

                s.setToX(1);
                s.setToY(1);
                s.play();

                setStyle(
                        "-fx-background-color: rgba(5,19,29,0.94);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(28,224,255,0.13);" +
                        "-fx-border-radius: 14;"
                );
            });
        }
    }
}
