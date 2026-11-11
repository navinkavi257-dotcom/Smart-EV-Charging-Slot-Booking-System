package com.evcharging.gui;

import com.evcharging.model.Charger;
import com.evcharging.model.Station;
import com.evcharging.model.User;
import com.evcharging.service.ChargerService;
import com.evcharging.service.StationService;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ChargerManagementView {

    private final User loggedInUser;

    private final ChargerService chargerService = new ChargerService();
    private final StationService stationService = new StationService();

    private TableView<Charger> chargerTable;

    private Label totalLabel;
    private Label activeLabel;
    private Label inactiveLabel;
    private Label fastLabel;

    private ComboBox<String> stationBox;
    private ComboBox<String> typeBox;
    private ComboBox<String> powerBox;

    private Label previewPower;
    private Label previewRate;
    private Label previewType;
    private Label previewStation;

    private Pane energyPane;

    private Timeline energyAnimation;

    private List<Station> stationList = new ArrayList<>();

    public ChargerManagementView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // ============================================================
    // SHOW PAGE
    // ============================================================

    public void show(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #050914;"
        );

        // Background
        StackPane background = createBackground();

        // Sidebar
        VBox sidebar = createSidebar(stage);

        // Main content
        VBox content = new VBox(18);
        content.setPadding(new Insets(22, 28, 25, 28));
        content.setMaxWidth(Double.MAX_VALUE);
        content.setMaxHeight(Double.MAX_VALUE);

        // Header
        HBox header = createHeader(stage);

        // Statistics
        HBox statistics = createStatistics();

        // Deployment section
        HBox deploymentArea = createDeploymentArea();

        // Table
        VBox tableSection = createChargerTable();

        VBox centerContent = new VBox(
                18,
                header,
                statistics,
                deploymentArea,
                tableSection
        );

        centerContent.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );

        root.setLeft(sidebar);
        root.setCenter(scrollPane);

        StackPane mainStack = new StackPane(
                background,
                root
        );

        Scene scene = new Scene(
                mainStack,
                1440,
                900
        );

        scene.setFill(Color.web("#050914"));

        stage.setTitle("Smart EV - Charger Management");
        stage.setScene(scene);

        // ========================================================
        // FIX: Do not use stage.setMaximized(true)
        // This prevents the slow left-to-right expansion problem.
        // ========================================================

        Rectangle2D bounds =
                javafx.stage.Screen.getPrimary().getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.show();

        refreshChargers();

        startEnergyAnimation();
    }

    // ============================================================
    // HEADER
    // ============================================================

    private HBox createHeader(Stage stage) {

        VBox titleBox = new VBox(4);

        Label title = new Label(
                "CHARGER DEPLOYMENT CENTER"
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        27
                )
        );

        title.setTextFill(Color.WHITE);

        Label subtitle = new Label(
                "SMART GRID  •  POWER INFRASTRUCTURE  •  LIVE CHARGER CONTROL"
        );

        subtitle.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        11
                )
        );

        subtitle.setTextFill(
                Color.web("#7e91aa")
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer = new Region();
        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label gridStatus = new Label(
                "●  GRID ONLINE"
        );

        gridStatus.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        gridStatus.setTextFill(
                Color.web("#31f5a4")
        );

        Button refresh = new Button(
                "↻  REFRESH"
        );

        refresh.setStyle(
                "-fx-background-color: #0b1828;" +
                "-fx-text-fill: #55e6ff;" +
                "-fx-border-color: #164b64;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 7;" +
                "-fx-border-radius: 7;" +
                "-fx-padding: 9 15;" +
                "-fx-font-weight: bold;"
        );

        refresh.setOnAction(e -> refreshChargers());

        HBox header = new HBox(
                18,
                titleBox,
                spacer,
                gridStatus,
                refresh
        );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        return header;
    }

    // ============================================================
    // STATISTICS
    // ============================================================

    private HBox createStatistics() {

        totalLabel = new Label("0");
        activeLabel = new Label("0");
        inactiveLabel = new Label("0");
        fastLabel = new Label("0");

        HBox box = new HBox(14);

        box.getChildren().addAll(
                createStatCard(
                        "TOTAL CHARGERS",
                        totalLabel,
                        "#55e6ff",
                        "◉"
                ),

                createStatCard(
                        "ACTIVE",
                        activeLabel,
                        "#31f5a4",
                        "●"
                ),

                createStatCard(
                        "INACTIVE",
                        inactiveLabel,
                        "#ff647c",
                        "○"
                ),

                createStatCard(
                        "FAST CHARGERS",
                        fastLabel,
                        "#c98cff",
                        "⚡"
                )
        );

        return box;
    }

    private VBox createStatCard(
            String title,
            Label value,
            String color,
            String icon
    ) {

        Label iconLabel = new Label(icon);

        iconLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        20
                )
        );

        iconLabel.setTextFill(
                Color.web(color)
        );

        Label titleLabel = new Label(title);

        titleLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        titleLabel.setTextFill(
                Color.web("#788aa4")
        );

        value.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        27
                )
        );

        value.setTextFill(
                Color.web(color)
        );

        VBox card = new VBox(
                7,
                iconLabel,
                titleLabel,
                value
        );

        card.setPadding(
                new Insets(14, 18, 14, 18)
        );

        card.setPrefWidth(190);
        card.setMinHeight(105);

        card.setStyle(
                "-fx-background-color: rgba(9,20,34,0.94);" +
                "-fx-border-color: " + color + "44;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        return card;
    }

    // ============================================================
    // DEPLOYMENT AREA
    // ============================================================

    private HBox createDeploymentArea() {

        VBox deployment = createDeploymentPanel();

        VBox preview = createPreviewPanel();

        HBox area = new HBox(
                18,
                deployment,
                preview
        );

        HBox.setHgrow(
                deployment,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                preview,
                Priority.ALWAYS
        );

        return area;
    }

    // ============================================================
    // DEPLOYMENT PANEL
    // ============================================================

    private VBox createDeploymentPanel() {

        Label title = new Label(
                "⚡  NEW CHARGER DEPLOYMENT"
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        15
                )
        );

        title.setTextFill(
                Color.web("#55e6ff")
        );

        Label description = new Label(
                "Configure a charger according to its charging class."
        );

        description.setFont(
                Font.font(
                        "System",
                        11
                )
        );

        description.setTextFill(
                Color.web("#74869e")
        );

        stationBox = new ComboBox<>();

        typeBox = new ComboBox<>();

        powerBox = new ComboBox<>();

        stationBox.setPrefHeight(40);
        typeBox.setPrefHeight(40);
        powerBox.setPrefHeight(40);

        stationBox.setPromptText(
                "Select charging station"
        );

        typeBox.getItems().addAll(
                "Slow",
                "Fast"
        );

        typeBox.setPromptText(
                "Select charger type"
        );

        powerBox.setPromptText(
                "Select power"
        );

        styleComboBox(stationBox);
        styleComboBox(typeBox);
        styleComboBox(powerBox);

        // ========================================================
        // IMPORTANT:
        // Charger type controls available power options.
        // ========================================================

        typeBox.setOnAction(e -> updatePowerOptions());

        powerBox.setOnAction(e -> updatePreview());

        stationBox.setOnAction(e -> updatePreview());

        Button deployButton = new Button(
                "⚡  DEPLOY CHARGER"
        );

        deployButton.setPrefHeight(43);
        deployButton.setMaxWidth(Double.MAX_VALUE);

        deployButton.setStyle(
                "-fx-background-color: linear-gradient(to right,#087f98,#0ca9c9);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 7;"
        );

        deployButton.setOnMouseEntered(e ->
                deployButton.setStyle(
                        "-fx-background-color: #13c9ed;" +
                        "-fx-text-fill: #031018;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 7;"
                )
        );

        deployButton.setOnMouseExited(e ->
                deployButton.setStyle(
                        "-fx-background-color: linear-gradient(to right,#087f98,#0ca9c9);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 7;"
                )
        );

        deployButton.setOnAction(
                e -> deployCharger()
        );

        VBox stationField =
                createField(
                        "CHARGING STATION",
                        stationBox
                );

        VBox typeField =
                createField(
                        "CHARGER CLASS",
                        typeBox
                );

        VBox powerField =
                createField(
                        "POWER OUTPUT",
                        powerBox
                );

        VBox form = new VBox(
                13,
                stationField,
                typeField,
                powerField,
                deployButton
        );

        VBox panel = new VBox(
                8,
                title,
                description,
                new Separator(),
                form
        );

        panel.setPadding(
                new Insets(18)
        );

        panel.setMinWidth(450);

        panel.setStyle(
                "-fx-background-color: rgba(8,18,31,0.96);" +
                "-fx-border-color: #17394d;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );

        loadStations();

        return panel;
    }

    private VBox createField(
            String labelText,
            Control control
    ) {

        Label label = new Label(
                labelText
        );

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        label.setTextFill(
                Color.web("#70859f")
        );

        VBox box = new VBox(
                5,
                label,
                control
        );

        return box;
    }

    private void styleComboBox(
            ComboBox<String> combo
    ) {

        combo.setMaxWidth(
                Double.MAX_VALUE
        );

        combo.setStyle(
                "-fx-background-color: #081523;" +
                "-fx-border-color: #173b50;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-text-fill: white;"
        );
    }

    // ============================================================
    // POWER OPTIONS
    // ============================================================

    private void updatePowerOptions() {

        powerBox.getItems().clear();

        String type = typeBox.getValue();

        if (type == null) {
            updatePreview();
            return;
        }

        if (type.equalsIgnoreCase("Slow")) {

            powerBox.getItems().addAll(
                    "3.3 kW",
                    "7.5 kW"
            );

        } else if (type.equalsIgnoreCase("Fast")) {

            powerBox.getItems().addAll(
                    "60 kW",
                    "120 kW"
            );
        }

        powerBox.getSelectionModel().selectFirst();

        updatePreview();
    }

    // ============================================================
    // RATE CALCULATION
    // ============================================================

    private double getRateForPower(
            String power
    ) {

        if (power == null) {
            return 0;
        }

        switch (power) {

            case "3.3 kW":
                return 11.00;

            case "7.5 kW":
                return 13.00;

            case "60 kW":
                return 20.00;

            case "120 kW":
                return 24.00;

            default:
                return 0;
        }
    }

    // ============================================================
    // PREVIEW
    // ============================================================

    private VBox createPreviewPanel() {

        Label title = new Label(
                "LIVE CHARGER PREVIEW"
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        15
                )
        );

        title.setTextFill(
                Color.web("#31f5a4")
        );

        Label subtitle = new Label(
                "REAL-TIME POWER FLOW SIMULATION"
        );

        subtitle.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        9
                )
        );

        subtitle.setTextFill(
                Color.web("#657991")
        );

        energyPane = new Pane();

        energyPane.setPrefHeight(155);

        energyPane.setMinHeight(155);

        energyPane.setStyle(
                "-fx-background-color: #040c15;" +
                "-fx-border-color: #123245;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        createEnergyVisualization();

        previewPower = new Label("--");
        previewRate = new Label("₹-- / kWh");
        previewType = new Label("--");
        previewStation = new Label("--");

        previewPower.setTextFill(
                Color.WHITE
        );

        previewRate.setTextFill(
                Color.web("#31f5a4")
        );

        previewType.setTextFill(
                Color.web("#55e6ff")
        );

        previewStation.setTextFill(
                Color.web("#d5deea")
        );

        for (Label label : new Label[]{
                previewPower,
                previewRate,
                previewType,
                previewStation
        }) {

            label.setFont(
                    Font.font(
                            "System",
                            FontWeight.BOLD,
                            12
                    )
            );
        }

        GridPane details = new GridPane();

        details.setHgap(30);
        details.setVgap(8);

        details.add(
                createPreviewLabel("POWER"),
                0,
                0
        );

        details.add(
                previewPower,
                0,
                1
        );

        details.add(
                createPreviewLabel("RATE"),
                1,
                0
        );

        details.add(
                previewRate,
                1,
                1
        );

        details.add(
                createPreviewLabel("TYPE"),
                2,
                0
        );

        details.add(
                previewType,
                2,
                1
        );

        details.add(
                createPreviewLabel("STATION"),
                3,
                0
        );

        details.add(
                previewStation,
                3,
                1
        );

        VBox panel = new VBox(
                7,
                title,
                subtitle,
                energyPane,
                details
        );

        panel.setPadding(
                new Insets(18)
        );

        panel.setStyle(
                "-fx-background-color: rgba(8,18,31,0.96);" +
                "-fx-border-color: #17394d;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );

        return panel;
    }

    private Label createPreviewLabel(
            String text
    ) {

        Label label = new Label(text);

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        label.setTextFill(
                Color.web("#657991")
        );

        return label;
    }

    // ============================================================
    // ENERGY VISUALIZATION
    // ============================================================

    private void createEnergyVisualization() {

        double width = 650;
        double centerY = 78;

        // Grid
        for (int x = 0; x <= width; x += 35) {

            Line line = new Line(
                    x,
                    0,
                    x,
                    155
            );

            line.setStroke(
                    Color.web("#0b2534")
            );

            energyPane.getChildren().add(line);
        }

        for (int y = 0; y <= 155; y += 30) {

            Line line = new Line(
                    0,
                    y,
                    width,
                    y
            );

            line.setStroke(
                    Color.web("#0b2534")
            );

            energyPane.getChildren().add(line);
        }

        // GRID NODE
        Circle gridNode = new Circle(
                65,
                centerY,
                18
        );

        gridNode.setFill(
                Color.web("#102c3c")
        );

        gridNode.setStroke(
                Color.web("#55e6ff")
        );

        gridNode.setStrokeWidth(2);

        Label gridText = new Label("GRID");

        gridText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        gridText.setTextFill(
                Color.web("#55e6ff")
        );

        gridText.setLayoutX(49);
        gridText.setLayoutY(70);

        // Charger
        Rectangle charger = new Rectangle(
                245,
                55,
                110,
                46
        );

        charger.setArcWidth(10);
        charger.setArcHeight(10);

        charger.setFill(
                Color.web("#0b2632")
        );

        charger.setStroke(
                Color.web("#31f5a4")
        );

        charger.setStrokeWidth(2);

        Label chargerText = new Label(
                "CHARGER"
        );

        chargerText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        chargerText.setTextFill(
                Color.web("#31f5a4")
        );

        chargerText.setLayoutX(270);
        chargerText.setLayoutY(70);

        // EV
        Circle evNode = new Circle(
                535,
                centerY,
                21
        );

        evNode.setFill(
                Color.web("#18283c")
        );

        evNode.setStroke(
                Color.web("#c98cff")
        );

        evNode.setStrokeWidth(2);

        Label evText = new Label(
                "EV"
        );

        evText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        evText.setTextFill(
                Color.web("#c98cff")
        );

        evText.setLayoutX(527);
        evText.setLayoutY(70);

        // Connections
        Line line1 = new Line(
                83,
                centerY,
                245,
                centerY
        );

        line1.setStroke(
                Color.web("#31f5a4")
        );

        line1.setStrokeWidth(2);

        Line line2 = new Line(
                355,
                centerY,
                514,
                centerY
        );

        line2.setStroke(
                Color.web("#c98cff")
        );

        line2.setStrokeWidth(2);

        energyPane.getChildren().addAll(
                gridNode,
                gridText,
                charger,
                chargerText,
                evNode,
                evText,
                line1,
                line2
        );

        // Moving energy particles
        for (int i = 0; i < 8; i++) {

            Circle particle = new Circle(
                    4,
                    Color.web("#31f5a4")
            );

            particle.setLayoutX(
                    90 + (i * 45)
            );

            particle.setLayoutY(
                    centerY
            );

            energyPane.getChildren().add(
                    particle
            );

            TranslateTransition transition =
                    new TranslateTransition(
                            Duration.seconds(2.2),
                            particle
                    );

            transition.setFromX(0);
            transition.setToX(150);

            transition.setCycleCount(
                    Animation.INDEFINITE
            );

            transition.setDelay(
                    Duration.millis(i * 220)
            );

            transition.play();
        }
    }

    // ============================================================
    // TABLE
    // ============================================================

    private VBox createChargerTable() {

        Label title = new Label(
                "CHARGER NETWORK"
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        15
                )
        );

        title.setTextFill(
                Color.web("#55e6ff")
        );

        Label subtitle = new Label(
                "LIVE DEPLOYED CHARGING INFRASTRUCTURE"
        );

        subtitle.setFont(
                Font.font(
                        "System",
                        9
                )
        );

        subtitle.setTextFill(
                Color.web("#667b94")
        );

        chargerTable =
                new TableView<>();

        chargerTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        chargerTable.setPrefHeight(330);

        chargerTable.setStyle(
                "-fx-background-color: #07111d;" +
                "-fx-control-inner-background: #07111d;" +
                "-fx-table-cell-border-color: #122536;" +
                "-fx-border-color: #17394d;" +
                "-fx-border-radius: 10;"
        );

        TableColumn<Charger, String> idColumn =
                new TableColumn<>("ID");

        idColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getChargerId()
                        )
                )
        );

        TableColumn<Charger, String> stationColumn =
                new TableColumn<>("STATION");

        stationColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        getStationName(
                                data.getValue().getStationId()
                        )
                )
        );

        TableColumn<Charger, String> typeColumn =
                new TableColumn<>("TYPE");

        typeColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getChargerType()
                )
        );

        TableColumn<Charger, String> powerColumn =
                new TableColumn<>("POWER");

        powerColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        String.format(
                                "%.1f kW",
                                data.getValue().getPower()
                        )
                )
        );

        TableColumn<Charger, String> rateColumn =
                new TableColumn<>("RATE");

        rateColumn.setCellValueFactory(
                data -> {

                    String power =
                            convertPowerToString(
                                    data.getValue().getPower()
                            );

                    double rate =
                            getRateForPower(power);

                    return new javafx.beans.property.SimpleStringProperty(
                            String.format(
                                    "₹%.2f / kWh",
                                    rate
                            )
                    );
                }
        );

        TableColumn<Charger, String> statusColumn =
                new TableColumn<>("STATUS");

        statusColumn.setCellValueFactory(
                data -> {

                    String status =
                            data.getValue().isAvailable()
                                    ? "● AVAILABLE"
                                    : "○ INACTIVE";

                    return new javafx.beans.property.SimpleStringProperty(
                            status
                    );
                }
        );

        TableColumn<Charger, Void> controlColumn =
                new TableColumn<>("CONTROL");

        controlColumn.setCellFactory(
                column -> new TableCell<>() {

                    private final Button button =
                            new Button();

                    {

                        button.setStyle(
                                "-fx-background-color: #26131a;" +
                                "-fx-text-fill: #ff647c;" +
                                "-fx-border-color: #6e2939;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-font-size: 10px;" +
                                "-fx-font-weight: bold;"
                        );

                        button.setOnAction(
                                event -> {

                                    Charger charger =
                                            getTableView()
                                                    .getItems()
                                                    .get(getIndex());

                                    if (charger.isAvailable()) {

                                        deactivateCharger(
                                                charger
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {

                            Charger charger =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            if (charger.isAvailable()) {

                                button.setText(
                                        "DEACTIVATE"
                                );

                                setGraphic(button);

                            } else {

                                button.setText(
                                        "OFFLINE"
                                );

                                setGraphic(button);
                            }
                        }
                    }
                }
        );

        chargerTable.getColumns().addAll(
                idColumn,
                stationColumn,
                typeColumn,
                powerColumn,
                rateColumn,
                statusColumn,
                controlColumn
        );

        VBox tableBox = new VBox(
                7,
                title,
                subtitle,
                chargerTable
        );

        VBox.setVgrow(
                chargerTable,
                Priority.ALWAYS
        );

        tableBox.setPadding(
                new Insets(17)
        );

        tableBox.setStyle(
                "-fx-background-color: rgba(8,18,31,0.96);" +
                "-fx-border-color: #17394d;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
        );

        return tableBox;
    }

    // ============================================================
    // POWER STRING
    // ============================================================

    private String convertPowerToString(
            double power
    ) {

        if (Math.abs(power - 3.3) < 0.01) {
            return "3.3 kW";
        }

        if (Math.abs(power - 7.5) < 0.01) {
            return "7.5 kW";
        }

        if (Math.abs(power - 60.0) < 0.01) {
            return "60 kW";
        }

        if (Math.abs(power - 120.0) < 0.01) {
            return "120 kW";
        }

        return String.format(
                "%.1f kW",
                power
        );
    }

    // ============================================================
    // UPDATE PREVIEW
    // ============================================================

    private void updatePreview() {

        if (previewPower == null) {
            return;
        }

        String power =
                powerBox.getValue();

        String type =
                typeBox.getValue();

        String station =
                stationBox.getValue();

        if (power == null) {

            previewPower.setText("--");
            previewRate.setText("₹-- / kWh");

        } else {

            double rate =
                    getRateForPower(power);

            previewPower.setText(
                    power
            );

            previewRate.setText(
                    String.format(
                            "₹%.2f / kWh",
                            rate
                    )
            );
        }

        previewType.setText(
                type == null
                        ? "--"
                        : type
        );

        previewStation.setText(
                station == null
                        ? "--"
                        : station
        );
    }

    // ============================================================
    // LOAD STATIONS
    // ============================================================

    private void loadStations() {

        try {

            stationList =
                    stationService.getAllStations();

            stationBox.getItems().clear();

            for (Station station : stationList) {

                stationBox.getItems().add(
                        station.getStationId()
                                + " | "
                                + station.getStationName()
                );
            }

        } catch (Exception e) {

            showError(
                    "Unable to load stations.\n\n"
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // GET STATION NAME
    // ============================================================

    private String getStationName(
            int stationId
    ) {

        for (Station station : stationList) {

            if (station.getStationId()
                    == stationId) {

                return station.getStationName();
            }
        }

        return "Station #" + stationId;
    }

    // ============================================================
    // DEPLOY CHARGER
    // ============================================================

    private void deployCharger() {

        String stationValue =
                stationBox.getValue();

        String type =
                typeBox.getValue();

        String powerValue =
                powerBox.getValue();

        if (stationValue == null
                || type == null
                || powerValue == null) {

            showWarning(
                    "Please select station, charger type and power."
            );

            return;
        }

        int stationId;

        try {

            stationId =
                    Integer.parseInt(
                            stationValue
                                    .split("\\|")[0]
                                    .trim()
                    );

        } catch (Exception e) {

            showError(
                    "Invalid station selection."
            );

            return;
        }

        double power =
                Double.parseDouble(
                        powerValue
                                .replace(
                                        " kW",
                                        ""
                                )
                );

        // Safety validation
        if (type.equalsIgnoreCase("Slow")
                && power > 7.5) {

            showWarning(
                    "Slow chargers can only use 3.3 kW or 7.5 kW."
            );

            return;
        }

        if (type.equalsIgnoreCase("Fast")
                && power < 60) {

            showWarning(
                    "Fast chargers can only use 60 kW or 120 kW."
            );

            return;
        }

        double rate =
                getRateForPower(
                        powerValue
                );

        showDeploymentDialog(
                stationId,
                type,
                power,
                rate
        );
    }

    // ============================================================
    // DEPLOYMENT DIALOG
    // ============================================================

    private void showDeploymentDialog(
            int stationId,
            String type,
            double power,
            double rate
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Charger Deployment"
        );

        dialog.setHeaderText(
                "⚡ DEPLOYING CHARGER"
        );

        VBox content =
                new VBox(15);

        content.setPadding(
                new Insets(20)
        );

        Label chargerInfo =
                new Label(
                        type
                                + " Charger  •  "
                                + power
                                + " kW  •  ₹"
                                + String.format(
                                        "%.2f",
                                        rate
                                )
                                + " / kWh"
                );

        chargerInfo.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        chargerInfo.setTextFill(
                Color.web("#55e6ff")
        );

        ProgressBar progress =
                new ProgressBar(0);

        progress.setPrefWidth(420);

        Label status =
                new Label(
                        "Connecting to charging station..."
                );

        status.setTextFill(
                Color.web("#8ea4bd")
        );

        content.getChildren().addAll(
                chargerInfo,
                progress,
                status
        );

        dialog.getDialogPane()
                .setContent(content);

        dialog.getDialogPane()
                .getButtonTypes()
                .add(
                        ButtonType.CANCEL
                );

        Button cancelButton =
                (Button) dialog
                        .getDialogPane()
                        .lookupButton(
                                ButtonType.CANCEL
                        );

        cancelButton.setDisable(true);

        Timeline deployment =
                new Timeline();

        KeyFrame frame1 =
                new KeyFrame(
                        Duration.seconds(0.7),
                        new KeyValue(
                                progress.progressProperty(),
                                0.25
                        )
                );

        KeyFrame frame2 =
                new KeyFrame(
                        Duration.seconds(1.3),
                        new KeyValue(
                                progress.progressProperty(),
                                0.50
                        )
                );

        KeyFrame frame3 =
                new KeyFrame(
                        Duration.seconds(1.9),
                        new KeyValue(
                                progress.progressProperty(),
                                0.75
                        )
                );

        KeyFrame frame4 =
                new KeyFrame(
                        Duration.seconds(2.5),
                        new KeyValue(
                                progress.progressProperty(),
                                1.0
                        )
                );

        deployment.getKeyFrames().addAll(
                frame1,
                frame2,
                frame3,
                frame4
        );

        deployment.currentTimeProperty()
                .addListener(
                        (obs, oldTime, newTime) -> {

                            double seconds =
                                    newTime.toSeconds();

                            if (seconds < 0.7) {

                                status.setText(
                                        "Connecting to charging station..."
                                );

                            } else if (seconds < 1.3) {

                                status.setText(
                                        "Allocating power channel..."
                                );

                            } else if (seconds < 1.9) {

                                status.setText(
                                        "Registering charger in database..."
                                );

                            } else {

                                status.setText(
                                        "Synchronizing with smart grid..."
                                );
                            }
                        }
                );

        deployment.setOnFinished(
                e -> {

                    try {

                        Charger charger =
                                new Charger(
                                        stationId,
                                        type,
                                        power,
                                        true
                                );

                        chargerService.addCharger(
                                charger
                        );

                        dialog.close();

                        refreshChargers();

                        showSuccess(
                                "Charger deployed successfully!\n\n"
                                        + "Type: "
                                        + type
                                        + "\n"
                                        + "Power: "
                                        + power
                                        + " kW\n"
                                        + "Rate: ₹"
                                        + String.format(
                                                "%.2f",
                                                rate
                                        )
                                        + " / kWh"
                        );

                    } catch (Exception ex) {

                        dialog.close();

                        showError(
                                "Failed to deploy charger.\n\n"
                                        + ex.getMessage()
                        );
                    }
                }
        );

        deployment.play();

        dialog.show();
    }

    // ============================================================
    // REFRESH
    // ============================================================

    private void refreshChargers() {

        try {

            List<Charger> chargers =
                    chargerService.getAllChargers();

            chargerTable.getItems().setAll(
                    chargers
            );

            int total =
                    chargers.size();

            int active = 0;
            int inactive = 0;
            int fast = 0;

            for (Charger charger : chargers) {

                if (charger.isAvailable()) {

                    active++;

                } else {

                    inactive++;
                }

                if (charger.getChargerType()
                        .equalsIgnoreCase("Fast")) {

                    fast++;
                }
            }

            totalLabel.setText(
                    String.valueOf(total)
            );

            activeLabel.setText(
                    String.valueOf(active)
            );

            inactiveLabel.setText(
                    String.valueOf(inactive)
            );

            fastLabel.setText(
                    String.valueOf(fast)
            );

            loadStations();

        } catch (Exception e) {

            showError(
                    "Unable to load chargers.\n\n"
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // DEACTIVATE
    // ============================================================

    private void deactivateCharger(
            Charger charger
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Deactivate Charger"
        );

        confirmation.setHeaderText(
                "Deactivate Charger #"
                        + charger.getChargerId()
        );

        confirmation.setContentText(
                "Are you sure you want to deactivate this charger?"
        );

        confirmation.showAndWait()
                .ifPresent(
                        result -> {

                            if (result
                                    == ButtonType.OK) {

                                try {

                                    chargerService
                                            .deactivateCharger(
                                                    charger.getChargerId()
                                            );

                                    refreshChargers();

                                } catch (Exception e) {

                                    showError(
                                            "Unable to deactivate charger.\n\n"
                                                    + e.getMessage()
                                    );
                                }
                            }
                        }
                );
    }

    // ============================================================
    // SIDEBAR
    // ============================================================

    private VBox createSidebar(
            Stage stage
    ) {

        VBox sidebar =
                new VBox(8);

        sidebar.setPadding(
                new Insets(
                        22,
                        14,
                        20,
                        14
                )
        );

        sidebar.setPrefWidth(220);

        sidebar.setStyle(
                "-fx-background-color: rgba(3,10,19,0.98);" +
                "-fx-border-color: #102d40;" +
                "-fx-border-width: 0 1 0 0;"
        );

        Label logo =
                new Label("⚡");

        logo.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        30
                )
        );

        logo.setTextFill(
                Color.web("#55e6ff")
        );

        Label brand =
                new Label(
                        "SMART EV"
                );

        brand.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        17
                )
        );

        brand.setTextFill(
                Color.WHITE
        );

        Label admin =
                new Label(
                        "ADMIN CONTROL"
                );

        admin.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        admin.setTextFill(
                Color.web("#60758d")
        );

        VBox logoBox =
                new VBox(
                        1,
                        logo,
                        brand,
                        admin
                );

        logoBox.setPadding(
                new Insets(
                        0,
                        0,
                        22,
                        8
                )
        );

        sidebar.getChildren()
                .add(logoBox);

        Button dashboard =
                createNavButton(
                        "⌂",
                        "DASHBOARD"
                );

        Button stations =
                createNavButton(
                        "▣",
                        "STATIONS"
                );

        Button chargers =
                createNavButton(
                        "⚡",
                        "CHARGERS"
                );

        Button bookings =
                createNavButton(
                        "◷",
                        "BOOKINGS"
                );

        Button users =
                createNavButton(
                        "♙",
                        "USERS"
                );

        Button reports =
                createNavButton(
                        "▤",
                        "REPORTS"
                );

        Button settings =
                createNavButton(
                        "⚙",
                        "SETTINGS"
                );

        chargers.setStyle(
                "-fx-background-color: #0b2735;" +
                "-fx-text-fill: #55e6ff;" +
                "-fx-border-color: #14526a;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 7;" +
                "-fx-border-radius: 7;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 11 13;" +
                "-fx-font-weight: bold;"
        );

        sidebar.getChildren().addAll(
                dashboard,
                stations,
                chargers,
                bookings,
                users,
                reports,
                settings
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        Label system =
                new Label(
                        "●  SYSTEM ONLINE"
                );

        system.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        system.setTextFill(
                Color.web("#31f5a4")
        );

        system.setPadding(
                new Insets(
                        10,
                        10,
                        10,
                        10
                )
        );

        Button logout =
                createNavButton(
                        "⇥",
                        "LOGOUT"
                );

        logout.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #ff647c;" +
                "-fx-border-color: transparent;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 11 13;" +
                "-fx-font-weight: bold;"
        );

        sidebar.getChildren().addAll(
                system,
                logout
        );

        // ========================================================
        // NAVIGATION
        // ========================================================

        dashboard.setOnAction(e -> {

            stopEnergyAnimation();

            new AdminDashboardView(
                    loggedInUser
            ).show(stage);
        });

        stations.setOnAction(e -> {

            stopEnergyAnimation();

            new AdminStationView(
                    loggedInUser
            ).show(stage);
        });

        chargers.setOnAction(e -> {

            // Already on this page
            refreshChargers();
        });

        bookings.setOnAction(e -> {

            stopEnergyAnimation();

            new BookingManagementView(
                    loggedInUser
            ).show(stage);
        });

        users.setOnAction(e -> {

            stopEnergyAnimation();

            new UserManagementView(
                    loggedInUser
            ).show(stage);
        });

        reports.setOnAction(e -> {

            stopEnergyAnimation();

            new ReportView(
                    loggedInUser
            ).show(stage);
        });

        settings.setOnAction(e -> {

            stopEnergyAnimation();

            new SettingsView(
                    loggedInUser
            ).show(stage);
        });

        logout.setOnAction(e -> {

            stopEnergyAnimation();

            new LoginView().show(stage);
        });

        return sidebar;
    }

    // ============================================================
    // NAV BUTTON
    // ============================================================

    private Button createNavButton(
            String icon,
            String text
    ) {

        Button button =
                new Button(
                        icon + "   " + text
                );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(43);

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #7f91a8;" +
                "-fx-border-color: transparent;" +
                "-fx-background-radius: 7;" +
                "-fx-border-radius: 7;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 11 13;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #0b1d2c;" +
                        "-fx-text-fill: #55e6ff;" +
                        "-fx-border-color: #123d52;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 7;" +
                        "-fx-border-radius: 7;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 11 13;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #7f91a8;" +
                        "-fx-border-color: transparent;" +
                        "-fx-background-radius: 7;" +
                        "-fx-border-radius: 7;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 11 13;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
                )
        );

        return button;
    }

    // ============================================================
    // BACKGROUND
    // ============================================================

    private StackPane createBackground() {

        StackPane pane =
                new StackPane();

        pane.setStyle(
                "-fx-background-color: #050914;"
        );

        Pane grid =
                new Pane();

        for (int x = 0; x < 1600; x += 45) {

            Line line =
                    new Line(
                            x,
                            0,
                            x,
                            1000
                    );

            line.setStroke(
                    Color.web("#071824")
            );

            grid.getChildren().add(line);
        }

        for (int y = 0; y < 1000; y += 45) {

            Line line =
                    new Line(
                            0,
                            y,
                            1600,
                            y
                    );

            line.setStroke(
                    Color.web("#071824")
            );

            grid.getChildren().add(line);
        }

        pane.getChildren().add(
                grid
        );

        // Floating particles
        for (int i = 0; i < 35; i++) {

            Circle particle =
                    new Circle(
                            1.5,
                            Color.web("#0e6b80")
                    );

            particle.setOpacity(
                    0.35
            );

            particle.setLayoutX(
                    30 + ((i * 83) % 1450)
            );

            particle.setLayoutY(
                    20 + ((i * 127) % 850)
            );

            TranslateTransition movement =
                    new TranslateTransition(
                            Duration.seconds(
                                    4 + (i % 4)
                            ),
                            particle
                    );

            movement.setFromY(0);
            movement.setToY(
                    25 + (i % 30)
            );

            movement.setCycleCount(
                    Animation.INDEFINITE
            );

            movement.setAutoReverse(true);

            movement.play();

            pane.getChildren().add(
                    particle
            );
        }

        return pane;
    }

    // ============================================================
    // ENERGY ANIMATION
    // ============================================================

    private void startEnergyAnimation() {

        if (energyPane == null) {
            return;
        }

        energyAnimation =
                new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(
                                        energyPane.opacityProperty(),
                                        0.72
                                )
                        ),
                        new KeyFrame(
                                Duration.seconds(1),
                                new KeyValue(
                                        energyPane.opacityProperty(),
                                        1.0
                                )
                        ),
                        new KeyFrame(
                                Duration.seconds(2),
                                new KeyValue(
                                        energyPane.opacityProperty(),
                                        0.72
                                )
                        )
                );

        energyAnimation.setCycleCount(
                Animation.INDEFINITE
        );

        energyAnimation.play();
    }

    private void stopEnergyAnimation() {

        if (energyAnimation != null) {

            energyAnimation.stop();
        }
    }

    // ============================================================
    // ALERTS
    // ============================================================

    private void showWarning(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                "Smart EV"
        );

        alert.setHeaderText(
                "Invalid Configuration"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    private void showError(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Smart EV"
        );

        alert.setHeaderText(
                "Operation Failed"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    private void showSuccess(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Smart EV"
        );

        alert.setHeaderText(
                "✓ Charger Ready"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }
}