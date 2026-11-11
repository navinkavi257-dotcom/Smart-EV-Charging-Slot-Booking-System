package com.evcharging.gui;

import com.evcharging.exception.BookingException;
import com.evcharging.model.Booking;
import com.evcharging.model.Charger;
import com.evcharging.model.Station;
import com.evcharging.model.User;
import com.evcharging.model.Wallet;
import com.evcharging.service.BookingService;
import com.evcharging.service.ChargerService;
import com.evcharging.service.StationService;
import com.evcharging.service.WalletService;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


public class BookingView {

    // ============================================================
    // USER + SERVICES
    // ============================================================

    private final User loggedInUser;

    private final StationService stationService =
            new StationService();

    private final ChargerService chargerService =
            new ChargerService();

    private final BookingService bookingService =
            new BookingService();

    private final WalletService walletService =
            new WalletService();

    /*
     * Random without a fixed seed makes the background feel
     * slightly different every time the page opens.
     */
    private final Random random =
            new Random();

    // ============================================================
    // BACKGROUND ANIMATION MANAGEMENT
    // ============================================================

    /*
     * All continuous background animations are stored here.
     *
     * This prevents old animations from continuing forever if
     * this BookingView is shown again.
     */
    private final List<Animation> backgroundAnimations =
            new ArrayList<>();

    // ============================================================
    // UI
    // ============================================================

    private ComboBox<Station> stationBox;

    private ComboBox<String> districtBox;

    private GridPane chargerContainer;

    private DatePicker datePicker;

    private ComboBox<String> timeBox;

    private Spinner<Integer> durationSpinner;

    private Label selectedChargerLabel;

    private Label rateLabel;

    private Label costLabel;

    private Label messageLabel;

    private Label availabilityLabel;

    private Label walletBalanceLabel;

    private Label paymentStatusLabel;

    private Label summaryStationLabel;

    private Label summaryChargerLabel;

    private Label summaryDateLabel;

    private Label summaryTimeLabel;

    private Label summaryDurationLabel;

    private Label summaryCostLabel;

    private Label systemStatusLabel;

    private Circle systemIndicator;

    private Charger selectedCharger;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public BookingView(User loggedInUser) {

        this.loggedInUser = loggedInUser;
    }


    // ============================================================
    // SHOW
    // ============================================================

    public void show(Stage stage) {

        /*
         * Stop any background animations belonging to a previous
         * display of this same BookingView.
         */
        stopBackgroundAnimations();

        StackPane root =
                new StackPane();

        root.setStyle(
                "-fx-background-color: #02080d;"
        );

        // ========================================================
        // FUTURISTIC BACKGROUND
        // ========================================================

        Pane background =
                createBackground();

        root.getChildren().add(
                background
        );

        // ========================================================
        // MAIN CONTENT
        // ========================================================

        BorderPane main =
                new BorderPane();

        main.setPadding(
                new Insets(18)
        );

        // ========================================================
        // TOP HUD
        // ========================================================

       main.setTop(
                createTopBar(stage)
        );
        // ========================================================
        // LEFT CONTROL MATRIX
        // ========================================================

        VBox left =
                new VBox(12);

        left.setPrefWidth(355);

        left.setMinWidth(335);

        left.setMaxWidth(370);

        VBox locationPanel =
                createLocationPanel();

        VBox schedulePanel =
                createSchedulePanel();

        VBox walletPanel =
                createWalletPanel();

        left.getChildren().addAll(
                locationPanel,
                schedulePanel,
                walletPanel
        );

        // ========================================================
        // RIGHT COMMAND CENTER
        // ========================================================

        VBox right =
                createCommandCenter();

        HBox.setHgrow(
                right,
                Priority.ALWAYS
        );

        HBox center =
                new HBox(16);

        center.setPadding(
                new Insets(
                        12,
                        0,
                        0,
                        0
                )
        );

        center.getChildren().addAll(
                left,
                right
        );

        main.setCenter(
                center
        );

        root.getChildren().add(
                main
        );

        // ========================================================
        // SCENE
        // ========================================================

        Scene scene =
                new Scene(
                        root,
                        1300,
                        820
                );

        stage.setScene(
                scene
        );

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

        // ========================================================
        // LOAD DATA
        // ========================================================

        loadStations();

        loadWalletBalance();

        updateCost();

        updateSummary();

        // ========================================================
        // ENTRANCE ANIMATION
        // ========================================================

        animateEntrance(main);
    }


    // ============================================================
    // STOP BACKGROUND ANIMATIONS
    // ============================================================

    private void stopBackgroundAnimations() {

        for (Animation animation :
                backgroundAnimations) {

            if (animation != null) {
                animation.stop();
            }
        }

        backgroundAnimations.clear();
    }


    // ============================================================
    // REGISTER BACKGROUND ANIMATION
    // ============================================================

    private void registerBackgroundAnimation(
            Animation animation) {

        if (animation == null) {
            return;
        }

        backgroundAnimations.add(
                animation
        );

        animation.play();
    }


// ============================================================
// TOP BAR
// ============================================================

private HBox createTopBar(Stage stage) {

    HBox bar =
            new HBox(16);

    bar.setAlignment(
            Pos.CENTER_LEFT
    );

    bar.setPadding(
            new Insets(
                    4,
                    6,
                    8,
                    6
            )
    );

    // --------------------------------------------------------
    // LOGO
    // --------------------------------------------------------

    Label logo =
            new Label(
                    "⚡ SMART EV"
            );

    logo.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    25
            )
    );

    logo.setTextFill(
            Color.web("#5ff2c8")
    );

    // --------------------------------------------------------
    // COMMAND TITLE
    // --------------------------------------------------------

    Label command =
            new Label(
                    "SMART CHARGING RESERVATION"
            );

    command.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    17
            )
    );

    command.setTextFill(
            Color.WHITE
    );

    // --------------------------------------------------------
    // USER
    // --------------------------------------------------------

    Label user =
            new Label(
                    "USER // " +
                    loggedInUser.getName()
            );

    user.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    10
            )
    );

    user.setTextFill(
            Color.web("#718b92")
    );

    // --------------------------------------------------------
    // SPACER
    // --------------------------------------------------------

    Region spacer =
            new Region();

    HBox.setHgrow(
            spacer,
            Priority.ALWAYS
    );

    // --------------------------------------------------------
    // DASHBOARD BUTTON
    // --------------------------------------------------------

    Button dashboardButton =
            new Button(
                    "← DASHBOARD"
            );

    dashboardButton.setPrefHeight(
            36
    );

    dashboardButton.setStyle(
            "-fx-background-color: #081a20;" +
            "-fx-text-fill: #7de3e8;" +
            "-fx-border-color: #23545d;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 10px;" +
            "-fx-cursor: hand;"
    );

    dashboardButton.setOnMouseEntered(
            e -> dashboardButton.setStyle(
                    "-fx-background-color: #123b3d;" +
                    "-fx-text-fill: #5ff2c8;" +
                    "-fx-border-color: #5ff2c8;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 10px;" +
                    "-fx-cursor: hand;"
            )
    );

    dashboardButton.setOnMouseExited(
            e -> dashboardButton.setStyle(
                    "-fx-background-color: #081a20;" +
                    "-fx-text-fill: #7de3e8;" +
                    "-fx-border-color: #23545d;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 10px;" +
                    "-fx-cursor: hand;"
            )
    );

    dashboardButton.setOnAction(
            e -> {

                stopBackgroundAnimations();

                new DashboardView(
                        loggedInUser
                ).show(stage);
            }
    );

    // --------------------------------------------------------
    // SYSTEM INDICATOR
    // --------------------------------------------------------

    systemIndicator =
            new Circle(
                    4,
                    Color.web("#5ff2c8")
            );

    systemStatusLabel =
            new Label(
                    "BOOKING SYSTEM ONLINE"
            );

    systemStatusLabel.setFont(
            Font.font(
                    "Arial",
                    FontWeight.BOLD,
                    10
            )
    );

    systemStatusLabel.setTextFill(
            Color.web("#5ff2c8")
    );

    HBox status =
            new HBox(
                    7,
                    systemIndicator,
                    systemStatusLabel
            );

    status.setAlignment(
            Pos.CENTER
    );

    status.setPadding(
            new Insets(
                    7,
                    12,
                    7,
                    12
            )
    );

    status.setStyle(
            "-fx-background-color: rgba(15,45,43,0.75);" +
            "-fx-border-color: #205d55;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;"
    );

    // --------------------------------------------------------
    // ADD EVERYTHING TO TOP BAR
    // --------------------------------------------------------

    bar.getChildren().addAll(
            logo,
            command,
            user,
            spacer,
            dashboardButton,
            status
    );

    // --------------------------------------------------------
    // STATUS PULSE
    // --------------------------------------------------------

    Timeline pulse =
            new Timeline(

                    new KeyFrame(
                            Duration.ZERO,

                            new KeyValue(
                                    systemIndicator
                                            .radiusProperty(),
                                    4
                            )
                    ),

                    new KeyFrame(
                            Duration.seconds(0.8),

                            new KeyValue(
                                    systemIndicator
                                            .radiusProperty(),
                                    6
                            )
                    ),

                    new KeyFrame(
                            Duration.seconds(1.6),

                            new KeyValue(
                                    systemIndicator
                                            .radiusProperty(),
                                    4
                            )
                    )
            );

    pulse.setCycleCount(
            Timeline.INDEFINITE
    );

    // Register it so it is stopped when leaving BookingView
    registerBackgroundAnimation(
            pulse
    );

    return bar;
}

    // ============================================================
    // LOCATION PANEL
    // ============================================================

    private VBox createLocationPanel() {

        VBox panel =
                createPanel();

        Label title =
                createSectionTitle(
                        "01  CHARGING LOCATION"
                );

        Label sub =
                createFieldLabel(
                        "CHARGING LOCATION"
                );

        districtBox =
                new ComboBox<>();

        districtBox.setMaxWidth(
                Double.MAX_VALUE
        );

        districtBox.setPrefHeight(
                43
        );

        districtBox.setPromptText(
                "SELECT CHARGING LOCATION"
        );

        districtBox.setStyle(
                comboBoxStyle()
        );

        districtBox.setOnAction(
                e -> filterStationsByDistrict()
        );

        Label locationStatus =
                new Label(
                        "◈ CHARGING NETWORK ONLINE"
                );

        locationStatus.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        locationStatus.setTextFill(
                Color.web("#5ff2c8")
        );

        Label stationTitle =
                createFieldLabel(
                        "CHARGING STATION"
                );

        stationBox =
                new ComboBox<>();

        stationBox.setMaxWidth(
                Double.MAX_VALUE
        );

        stationBox.setPrefHeight(
                43
        );

        stationBox.setPromptText(
                "SELECT CHARGING STATION"
        );

        stationBox.setStyle(
                comboBoxStyle()
        );

        stationBox.setCellFactory(
                list ->
                        new ListCell<Station>() {

                            @Override
                            protected void updateItem(
                                    Station station,
                                    boolean empty) {

                                super.updateItem(
                                        station,
                                        empty
                                );

                                if (empty ||
                                        station == null) {

                                    setText(null);

                                } else {

                                    setText(
                                            station
                                                    .getStationName()
                                    );
                                }
                            }
                        }
        );

        stationBox.setButtonCell(
                new ListCell<Station>() {

                    @Override
                    protected void updateItem(
                            Station station,
                            boolean empty) {

                        super.updateItem(
                                station,
                                empty
                        );

                        if (empty ||
                                station == null) {

                            setText(null);

                        } else {

                            setText(
                                    station
                                            .getStationName()
                            );
                        }
                    }
                }
        );

        stationBox.setOnAction(
                e -> {

                    selectedCharger =
                            null;

                    if (selectedChargerLabel != null) {

                        selectedChargerLabel.setText(
                                "NO CHARGER SELECTED — CHOOSE A CHARGER TO CONTINUE"
                        );
                    }

                    clearMessage();

                    updateSummary();

                    loadChargers();
                }
        );

        panel.getChildren().addAll(
                title,
                sub,
                districtBox,
                locationStatus,
                stationTitle,
                stationBox
        );

        return panel;
    }


    // ============================================================
    // SCHEDULE PANEL
    // ============================================================

    private VBox createSchedulePanel() {

        VBox panel =
                createPanel();

        Label title =
                createSectionTitle(
                        "02  CHARGING SCHEDULE"
                );

        // --------------------------------------------------------
        // DATE
        // --------------------------------------------------------

        datePicker =
                new DatePicker(
                        LocalDate.now()
                );

        datePicker.setMaxWidth(
                Double.MAX_VALUE
        );

        datePicker.setPrefHeight(
                42
        );

        datePicker.setStyle(
                datePickerStyle()
        );

        datePicker.setDayCellFactory(
                picker ->
                        new DateCell() {

                            @Override
                            public void updateItem(
                                    LocalDate date,
                                    boolean empty) {

                                super.updateItem(
                                        date,
                                        empty
                                );

                                if (!empty &&
                                        date.isBefore(
                                                LocalDate.now()
                                        )) {

                                    setDisable(
                                            true
                                    );
                                }
                            }
                        }
        );

        datePicker.setOnAction(
                e -> {

                    populateTimeSlots();

                    validateDateAndTime();

                    updateSummary();

                    updateCost();

                    loadChargers();
                }
        );

        // --------------------------------------------------------
        // TIME
        // --------------------------------------------------------

        timeBox =
                new ComboBox<>();

        timeBox.setMaxWidth(
                Double.MAX_VALUE
        );

        timeBox.setPrefHeight(
                42
        );

        timeBox.setPromptText(
                "SELECT START TIME"
        );

        timeBox.setStyle(
                comboBoxStyle()
        );

        populateTimeSlots();

        timeBox.setOnAction(
                e -> {

                    validateDateAndTime();

                    updateSummary();

                    updateCost();

                    loadChargers();
                }
        );

        // --------------------------------------------------------
        // DURATION
        // --------------------------------------------------------

        durationSpinner =
                new Spinner<>(
                        15,
                        240,
                        45,
                        15
                );

        durationSpinner.setMaxWidth(
                Double.MAX_VALUE
        );

        durationSpinner.setPrefHeight(
                42
        );

        durationSpinner.setStyle(
                spinnerStyle()
        );

        durationSpinner.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            updateSummary();

                            updateCost();

                            loadChargers();
                        }
                );

        // --------------------------------------------------------
        // CHARGER
        // --------------------------------------------------------

        selectedChargerLabel =
                new Label(
                        "NO CHARGER SELECTED"
                );

        selectedChargerLabel.setWrapText(
                true
        );

        selectedChargerLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        selectedChargerLabel.setTextFill(
                Color.web("#7de3e8")
        );

        // --------------------------------------------------------
        // RATE
        // --------------------------------------------------------

        rateLabel =
                new Label(
                        "ENERGY RATE: --"
                );

        rateLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        rateLabel.setTextFill(
                Color.web("#5ff2c8")
        );

        // --------------------------------------------------------
        // COST
        // --------------------------------------------------------

        costLabel =
                new Label(
                        "BOOKING FEE: ₹0.00"
                );

        costLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        17
                )
        );

        costLabel.setTextFill(
                Color.WHITE
        );

        panel.getChildren().addAll(

                title,

                createFieldLabel(
                        "DATE"
                ),

                datePicker,

                createFieldLabel(
                        "START TIME"
                ),

                timeBox,

                createFieldLabel(
                        "DURATION"
                ),

                durationSpinner,

                selectedChargerLabel,

                rateLabel,

                costLabel
        );

        return panel;
    }


    // ============================================================
    // WALLET PANEL
    // ============================================================

    private VBox createWalletPanel() {

        VBox panel =
                createPanel();

        Label title =
                createSectionTitle(
                        "03  DIGITAL WALLET"
                );

        walletBalanceLabel =
                new Label(
                        "Wallet Balance: ₹0.00"
                );

        walletBalanceLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        19
                )
        );

        walletBalanceLabel.setTextFill(
                Color.WHITE
        );

        paymentStatusLabel =
                new Label(
                        "PAYMENT SYSTEM READY"
                );

        paymentStatusLabel.setWrapText(
                true
        );

        paymentStatusLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        paymentStatusLabel.setTextFill(
                Color.web("#5ff2c8")
        );

        Label info =
                new Label(
                        "Booking fee is deducted when " +
                        "the reservation is confirmed. " +
                        "Charging cost is calculated later " +
                        "from actual energy consumed."
                );

        info.setWrapText(
                true
        );

        info.setFont(
                Font.font(
                        "Arial",
                        10
                )
        );

        info.setTextFill(
                Color.web("#82979c")
        );

        panel.getChildren().addAll(
                title,
                walletBalanceLabel,
                paymentStatusLabel,
                info
        );

        return panel;
    }


    // ============================================================
    // COMMAND CENTER
    // ============================================================

    private VBox createCommandCenter() {

        VBox right =
                new VBox(12);

        right.setMaxWidth(
                Double.MAX_VALUE
        );

        // ========================================================
        // LIVE ENERGY GRID
        // ========================================================

        VBox chargerPanel =
                createPanel();

        Label title =
                createSectionTitle(
                        "04  LIVE ENERGY GRID"
                );

        availabilityLabel =
                new Label(
                        "SELECT A CHARGING STATION TO VIEW LIVE STATUS"
                );

        availabilityLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        availabilityLabel.setTextFill(
                Color.web("#6f9198")
        );

        chargerContainer =
                new GridPane();

        chargerContainer.setHgap(
                13
        );

        chargerContainer.setVgap(
                13
        );

        chargerContainer.setPadding(
                new Insets(4)
        );

        ColumnConstraints c1 =
                new ColumnConstraints();

        c1.setPercentWidth(
                50
        );

        ColumnConstraints c2 =
                new ColumnConstraints();

        c2.setPercentWidth(
                50
        );

        chargerContainer
                .getColumnConstraints()
                .addAll(
                        c1,
                        c2
                );

        ScrollPane scroll =
                new ScrollPane(
                        chargerContainer
                );

        scroll.setFitToWidth(
                true
        );

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );

        VBox.setVgrow(
                scroll,
                Priority.ALWAYS
        );

        chargerPanel.getChildren().addAll(
                title,
                availabilityLabel,
                scroll
        );

        VBox.setVgrow(
                chargerPanel,
                Priority.ALWAYS
        );

        // ========================================================
        // RESERVATION CORE
        // ========================================================

        VBox summary =
                createSummaryPanel();

        // ========================================================
        // MESSAGE
        // ========================================================

        messageLabel =
                new Label();

        messageLabel.setWrapText(
                true
        );

        messageLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        messageLabel.setVisible(
                false
        );

        messageLabel.setManaged(
                false
        );

        // ========================================================
        // BOOK BUTTON
        // ========================================================

        Button book =
                new Button(
                        "⚡  INITIALIZE RESERVATION"
                );

        book.setMaxWidth(
                Double.MAX_VALUE
        );

        book.setPrefHeight(
                56
        );

        book.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        book.setStyle(
                bookButtonStyle(false)
        );

        book.setOnMouseEntered(
                e -> {

                    book.setStyle(
                            bookButtonStyle(true)
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    book
                            );

                    scale.setToX(
                            1.015
                    );

                    scale.setToY(
                            1.015
                    );

                    scale.play();
                }
        );

        book.setOnMouseExited(
                e -> {

                    book.setStyle(
                            bookButtonStyle(false)
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    book
                            );

                    scale.setToX(
                            1
                    );

                    scale.setToY(
                            1
                    );

                    scale.play();
                }
        );

        book.setOnAction(
                e -> confirmBooking()
        );

        right.getChildren().addAll(
                chargerPanel,
                summary,
                messageLabel,
                book
        );

        return right;
    }


    // ============================================================
    // SUMMARY
    // ============================================================

    private VBox createSummaryPanel() {

        VBox panel =
                createPanel();

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                createSectionTitle(
                        "05  RESERVATION CORE"
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label state =
                new Label(
                        "READY TO BOOK"
                );

        state.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        state.setTextFill(
                Color.web("#5ff2c8")
        );

        header.getChildren().addAll(
                title,
                spacer,
                state
        );

        summaryStationLabel =
                createSummaryLabel(
                        "STATION // NOT SELECTED"
                );

        summaryChargerLabel =
                createSummaryLabel(
                        "CHARGER // NOT SELECTED"
                );

        summaryDateLabel =
                createSummaryLabel(
                        "DATE // --"
                );

        summaryTimeLabel =
                createSummaryLabel(
                        "START // --"
                );

        summaryDurationLabel =
                createSummaryLabel(
                        "DURATION // --"
                );

        summaryCostLabel =
                createSummaryLabel(
                        "BOOKING FEE // ₹0.00"
                );

        summaryCostLabel.setTextFill(
                Color.web("#5ff2c8")
        );

        panel.getChildren().addAll(
                header,
                summaryStationLabel,
                summaryChargerLabel,
                summaryDateLabel,
                summaryTimeLabel,
                summaryDurationLabel,
                summaryCostLabel
        );

        return panel;
    }


    private Label createSummaryLabel(
            String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        label.setTextFill(
                Color.web("#c4d7db")
        );

        label.setWrapText(
                true
        );

        return label;
    }


    // ============================================================
    // SUMMARY UPDATE
    // ============================================================

    private void updateSummary() {

        if (summaryStationLabel == null) {
            return;
        }

        Station station =
                stationBox == null
                        ? null
                        : stationBox.getValue();

        if (station == null) {

            summaryStationLabel.setText(
                    "STATION // --"
            );

        } else {

            summaryStationLabel.setText(
                    "STATION // " +
                    station.getStationName()
            );
        }

        if (selectedCharger == null) {

            summaryChargerLabel.setText(
                    "CHARGER // --"
            );

        } else {

            summaryChargerLabel.setText(
                    String.format(
                            "CHARGER // %d • %s • %.1f kW",
                            selectedCharger.getChargerId(),
                            selectedCharger.getChargerType(),
                            selectedCharger.getPower()
                    )
            );
        }

        if (datePicker != null &&
                datePicker.getValue() != null) {

            summaryDateLabel.setText(
                    "DATE // " +
                    datePicker.getValue()
            );

        } else {

            summaryDateLabel.setText(
                    "DATE // --"
            );
        }

        if (timeBox != null &&
                timeBox.getValue() != null) {

            summaryTimeLabel.setText(
                    "START // " +
                    timeBox.getValue()
            );

        } else {

            summaryTimeLabel.setText(
                    "START // --"
            );
        }

        if (durationSpinner != null &&
                durationSpinner.getValue() != null) {

            summaryDurationLabel.setText(
                    "DURATION // " +
                    durationSpinner.getValue() +
                    " MINUTES"
            );

        } else {

            summaryDurationLabel.setText(
                    "DURATION // --"
            );
        }

        summaryCostLabel.setText(
                String.format(
                        "BOOKING FEE // ₹%.2f",
                        bookingService.getBookingFee()
                )
        );
    }


    // ============================================================
    // LOAD STATIONS
    // ============================================================

    private void loadStations() {

        stationBox.getItems().clear();

        districtBox.getItems().clear();

        try {

            List<Station> stations =
                    stationService.getAllStations();

            if (stations == null ||
                    stations.isEmpty()) {

                availabilityLabel.setText(
                        "NO CHARGING STATIONS AVAILABLE"
                );

                return;
            }

                Set<String> locations =
                        new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

                for (Station station : stations) {

                if (station.getLocation() != null &&
                        !station.getLocation()
                                .trim()
                                .isEmpty()) {

                        String location =
                                station.getLocation()
                                        .trim();

                        // Normalize display format
                        location =
                                location.substring(0, 1).toUpperCase()
                                + location.substring(1).toLowerCase();

                        locations.add(location);
                }
                }

            districtBox.getItems().add(
                    "ALL LOCATIONS"
            );

            districtBox.getItems().addAll(
                    locations
            );

            districtBox
                    .getSelectionModel()
                    .selectFirst();

            stationBox.getItems().addAll(
                    stations
            );

            /*
             * Selecting the first station triggers the existing
             * stationBox onAction handler, which loads chargers.
             *
             * Therefore we do NOT call loadChargers() again here.
             */
            stationBox
                    .getSelectionModel()
                    .selectFirst();

        } catch (Exception e) {

            showError(
                    "Unable to load stations: " +
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // FILTER STATIONS
    // ============================================================

    private void filterStationsByDistrict() {

        if (districtBox == null ||
                stationBox == null) {

            return;
        }

        String location =
                districtBox.getValue();

        if (location == null) {
            return;
        }

        try {

            List<Station> stations =
                    stationService.getAllStations();

            stationBox.getItems().clear();

            for (Station station : stations) {

                if (station == null) {
                    continue;
                }

                boolean match =
                        "ALL LOCATIONS".equals(
                                location
                        )
                        ||
                        (
                                station.getLocation() != null
                                &&
                                station.getLocation()
                                        .trim()
                                        .equalsIgnoreCase(
                                                location.trim()
                                        )
                        );

                if (match) {

                    stationBox.getItems()
                            .add(station);
                }
            }

            selectedCharger = null;

            if (stationBox.getItems().isEmpty()) {

                availabilityLabel.setText(
                        "NO CHARGING STATIONS FOUND"
                );

                chargerContainer
                        .getChildren()
                        .clear();

                updateSummary();

                return;
            }

            /*
             * Selecting the first station triggers
             * stationBox's onAction handler.
             *
             * That handler calls loadChargers().
             */
            stationBox
                    .getSelectionModel()
                    .selectFirst();

        } catch (Exception e) {

            showError(
                    "Unable to filter stations: " +
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // LOAD CHARGERS
    // ============================================================

    private void loadChargers() {

        if (chargerContainer == null) {
            return;
        }

        chargerContainer
                .getChildren()
                .clear();

        selectedCharger = null;

        if (selectedChargerLabel != null) {

            selectedChargerLabel.setText(
                    "NO CHARGER SELECTED"
            );
        }

        if (rateLabel != null) {

            rateLabel.setText(
                    "ENERGY RATE: --"
            );
        }

        updateSummary();

        Station station =
                stationBox.getValue();

        if (station == null) {

            availabilityLabel.setText(
                    "SELECT A STATION TO INITIALIZE GRID"
            );

            return;
        }

        LocalDate date =
                datePicker.getValue();

        String time =
                timeBox.getValue();

        Integer duration =
                durationSpinner.getValue();

        if (date == null ||
                time == null ||
                duration == null) {

            availabilityLabel.setText(
                    "SELECT DATE, TIME AND DURATION"
            );

            return;
        }

        if (date.isBefore(
                LocalDate.now()
        )) {

            availabilityLabel.setText(
                    "SELECTED DATE IS IN THE PAST"
            );

            return;
        }

        if (date.equals(
                LocalDate.now()
        )) {

            try {

                if (LocalTime.parse(time)
                        .isBefore(
                                LocalTime.now()
                        )) {

                    availabilityLabel.setText(
                            "SELECTED TIME HAS PASSED"
                    );

                    return;
                }

            } catch (Exception e) {

                availabilityLabel.setText(
                        "INVALID TIME"
                );

                return;
            }
        }

        try {

            List<Charger> chargers =
                    chargerService
                            .getChargersByStation(
                                    station.getStationId()
                            );

            if (chargers == null ||
                    chargers.isEmpty()) {

                availabilityLabel.setText(
                        "NO CHARGERS AVAILABLE AT THIS STATION"
                );

                return;
            }

            int column = 0;

            int row = 0;

            int availableCount = 0;

            int index = 0;

            for (Charger charger : chargers) {

                /*
                 * IMPORTANT:
                 *
                 * charger.isAvailable()
                 * checks the admin status.
                 *
                 * bookingService.isChargerAvailable()
                 * checks the selected booking slot.
                 */
                boolean slotAvailable =
                        charger.isAvailable()
                        &&
                        bookingService
                                .isChargerAvailable(
                                        charger.getChargerId(),
                                        station.getStationId(),
                                        date.toString(),
                                        time,
                                        duration
                                );

                VBox card =
                        createChargerCard(
                                charger,
                                slotAvailable
                        );

                chargerContainer.add(
                        card,
                        column,
                        row
                );

                column++;

                if (column >= 2) {

                    column = 0;

                    row++;
                }

                if (slotAvailable) {

                    availableCount++;
                }

                animateCard(
                        card,
                        index++
                );
            }

            availabilityLabel.setText(
                    availableCount +
                    " ACTIVE CHARGER(S) • " +
                    date +
                    " • " +
                    time
            );

        } catch (Exception e) {

            showError(
                    "Unable to load chargers: " +
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // CHARGER CARD
    // ============================================================

    private VBox createChargerCard(
            Charger charger,
            boolean slotAvailable) {

        boolean selectable =
                charger.isAvailable()
                &&
                slotAvailable;

        VBox card =
                new VBox(6);

        card.setUserData(
                charger
        );

        card.setPadding(
                new Insets(14)
        );

        card.setMinHeight(
                135
        );

        card.setPrefHeight(
                135
        );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setStyle(
                selectable
                        ? chargerCardStyle(false)
                        : chargerCardUnavailableStyle()
        );

        // ========================================================
        // TOP LINE
        // ========================================================

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label id =
                new Label(
                        "⚡ CHARGER " +
                        charger.getChargerId()
                );

        id.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        id.setTextFill(
                Color.WHITE
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Circle indicator =
                new Circle(
                        4,
                        selectable
                                ? Color.web("#5ff2c8")
                                : Color.web("#ff596b")
                );

        top.getChildren().addAll(
                id,
                spacer,
                indicator
        );

        // ========================================================
        // TYPE
        // ========================================================

        Label type =
                new Label(
                        charger.getChargerType()
                                .toUpperCase()
                                + " CHARGER"
                );

        type.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        type.setTextFill(
                Color.web("#7de3e8")
        );

        // ========================================================
        // POWER
        // ========================================================

        Label power =
                new Label(
                        String.format(
                                "%.1f kW",
                                charger.getPower()
                        )
                );

        power.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        25
                )
        );

        power.setTextFill(
                Color.web("#5ff2c8")
        );

        // ========================================================
        // STATUS
        // ========================================================

        String statusText;

        if (!charger.isAvailable()) {

            statusText =
                    "● ADMIN DISABLED";

        } else if (!slotAvailable) {

            statusText =
                    "● SLOT OCCUPIED";

        } else {

            statusText =
                    "● AVAILABLE";
        }

        Label status =
                new Label(
                        statusText
                );

        status.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        status.setTextFill(
                selectable
                        ? Color.web("#5ff2c8")
                        : Color.web("#ff6978")
        );

        card.getChildren().addAll(
                top,
                type,
                power,
                status
        );

        // ========================================================
        // HOVER
        // ========================================================

        card.setOnMouseEntered(
                e -> {

                    if (!selectable) {
                        return;
                    }

                    if (selectedCharger != charger) {

                        card.setStyle(
                                chargerCardHoverStyle()
                        );
                    }

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    card
                            );

                    scale.setToX(
                            1.018
                    );

                    scale.setToY(
                            1.018
                    );

                    scale.play();
                }
        );

        card.setOnMouseExited(
                e -> {

                    if (!selectable) {
                        return;
                    }

                    card.setStyle(
                            charger == selectedCharger
                                    ? chargerCardStyle(true)
                                    : chargerCardStyle(false)
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    card
                            );

                    scale.setToX(1);

                    scale.setToY(1);

                    scale.play();
                }
        );

        // ========================================================
        // CLICK
        // ========================================================

        card.setOnMouseClicked(
                e -> {

                    if (!selectable) {

                        showError(
                                !charger.isAvailable()
                                        ?
                                        "Charger " +
                                        charger.getChargerId() +
                                        " is disabled by admin."
                                        :
                                        "Charger " +
                                        charger.getChargerId() +
                                        " is occupied at this time."
                        );

                        return;
                    }

                    selectedCharger =
                            charger;

                    updateSelectedChargerStyles();

                    selectedChargerLabel.setText(
                            String.format(
                                    "SELECTED // CHARGER %d • %s • %.1f kW",
                                    charger.getChargerId(),
                                    charger.getChargerType()
                                            .toUpperCase(),
                                    charger.getPower()
                            )
                    );

                    rateLabel.setText(
                            String.format(
                                    "ENERGY RATE // ₹%.2f / kWh",
                                    bookingService
                                            .getRatePerKWh()
                            )
                    );

                    paymentStatusLabel.setText(
                            String.format(
                                   "BOOKING FEE // ₹%.2f • CHARGING COST BILLED AFTER SESSION",
                                    bookingService
                                            .getBookingFee()
                            )
                    );

                    paymentStatusLabel.setTextFill(
                            Color.web("#5ff2c8")
                    );

                    updateSummary();

                    updateCost();

                    clearMessage();

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(180),
                                    card
                            );

                    scale.setFromX(1);

                    scale.setFromY(1);

                    scale.setToX(1.04);

                    scale.setToY(1.04);

                    scale.setAutoReverse(
                            true
                    );

                    scale.setCycleCount(
                            2
                    );

                    scale.play();
                }
        );

        return card;
    }


    // ============================================================
    // SELECTED CARD STYLE
    // ============================================================

    private void updateSelectedChargerStyles() {

        if (chargerContainer == null) {
            return;
        }

        for (Node node :
                chargerContainer.getChildren()) {

            if (!(node instanceof VBox)) {
                continue;
            }

            VBox card =
                    (VBox) node;

            Object object =
                    card.getUserData();

            if (!(object instanceof Charger)) {
                continue;
            }

            Charger charger =
                    (Charger) object;

            if (!charger.isAvailable()) {
                continue;
            }

            card.setStyle(
                    charger == selectedCharger
                            ? chargerCardStyle(true)
                            : chargerCardStyle(false)
            );
        }
    }


    // ============================================================
    // COST
    // ============================================================

    private void updateCost() {

        if (costLabel == null) {
            return;
        }

        costLabel.setText(
                String.format(
                        "BOOKING FEE // ₹%.2f",
                        bookingService.getBookingFee()
                )
        );
    }


    // ============================================================
    // WALLET
    // ============================================================

    private void loadWalletBalance() {

        try {

            walletService.createWallet(
                    loggedInUser.getUserId()
            );

            Wallet wallet =
                    walletService.getWallet(
                            loggedInUser.getUserId()
                    );

            if (wallet != null) {

                walletBalanceLabel.setText(
                        String.format(
                                "Wallet Balance: ₹%.2f",
                                wallet.getBalance()
                        )
                );

            } else {

                walletBalanceLabel.setText(
                        "Wallet Balance: ₹0.00"
                );
            }

        } catch (Exception e) {

            walletBalanceLabel.setText(
                    "Wallet Balance: unavailable"
            );
        }
    }


    // ============================================================
    // CONFIRM BOOKING
    // ============================================================

    private void confirmBooking() {

        clearMessage();

        Station station =
                stationBox.getValue();

        if (station == null) {

            showError(
                    "Please select a charging station."
            );

            return;
        }

        if (selectedCharger == null) {

            showError(
                    "Please select an available charger."
            );

            return;
        }

        LocalDate date =
                datePicker.getValue();

        if (date == null ||
                date.isBefore(
                        LocalDate.now()
                )) {

            showError(
                    "Please select a valid booking date."
            );

            return;
        }

        String selectedTime =
                timeBox.getValue();

        if (selectedTime == null ||
                selectedTime.isBlank()) {

            showError(
                    "Please select a start time."
            );

            return;
        }

        Integer duration =
                durationSpinner.getValue();

        if (duration == null ||
                duration <= 0) {

            showError(
                    "Please select a valid duration."
            );

            return;
        }

        if (date.equals(
                LocalDate.now()
        )) {

            try {

                if (LocalTime.parse(
                        selectedTime
                ).isBefore(
                        LocalTime.now()
                )) {

                    showError(
                            "Please select a future time."
                    );

                    return;
                }

            } catch (Exception e) {

                showError(
                        "Invalid time selected."
                );

                return;
            }
        }

        try {

            // ====================================================
            // ADMIN AVAILABILITY
            // ====================================================

            if (!selectedCharger.isAvailable()) {

                showError(
                        "This charger is currently disabled by admin."
                );

                loadChargers();

                return;
            }

            // ====================================================
            // SLOT AVAILABILITY
            // ====================================================

            boolean available =
                    bookingService
                            .isChargerAvailable(
                                    selectedCharger
                                            .getChargerId(),

                                    station.getStationId(),

                                    date.toString(),

                                    selectedTime,

                                    duration
                            );

            if (!available) {

                showError(
                        "This charger is already booked " +
                        "during the selected time."
                );

                loadChargers();

                return;
            }

            // ====================================================
            // BOOKING FEE
            // ====================================================

            double bookingFee =
                    bookingService
                            .getBookingFee();

            if (!walletService
                    .hasEnoughBalance(
                            loggedInUser.getUserId(),
                            bookingFee
                    )) {

                showError(
                        String.format(
                                "Insufficient wallet balance. " +
                                "Booking fee is ₹%.2f.",
                                bookingFee
                        )
                );

                return;
            }

            // ====================================================
            // CREATE BOOKING
            // ====================================================

            Booking booking =
                    new Booking(

                            loggedInUser
                                    .getUserId(),

                            station
                                    .getStationId(),

                            selectedCharger
                                    .getChargerId(),

                            date.toString(),

                            selectedTime,

                            duration,

                            0.00,

                            bookingFee,

                            0.00,

                            0.00,

                            "CONFIRMED"
                    );

            boolean created =
                    bookingService
                            .createBooking(
                                    booking
                            );

            if (!created) {

                showError(
                        "Booking could not be created."
                );

                return;
            }

            // ====================================================
            // PAYMENT SUCCESS
            // ====================================================

            paymentStatusLabel.setText(
                    String.format(
                           "✓ BOOKING CONFIRMED • FEE PAID // ₹%.2f",
                            bookingFee
                    )
            );

            paymentStatusLabel.setTextFill(
                    Color.web("#5ff2c8")
            );

            loadWalletBalance();

            // ====================================================
            // SUCCESS MESSAGE
            // ====================================================

            showSuccess(
                    String.format(
                            "✓ RESERVATION INITIALIZED%n%n" +
                            "BOOKING ID // %d%n" +
                            "STATION // %s%n" +
                            "CHARGER // %d • %.1f kW%n" +
                            "DATE // %s%n" +
                            "START // %s%n" +
                            "DURATION // %d MINUTES%n%n" +
                           "BOOKING FEE PAID // ₹%.2f%n" +
                           "CHARGING COST // BILLED AFTER SESSION%n%n" +
                            "Charging cost will be calculated " +
                            "after charging using actual energy consumed.",

                            booking.getBookingId(),

                            station.getStationName(),

                            selectedCharger
                                    .getChargerId(),

                            selectedCharger
                                    .getPower(),

                            date,

                            selectedTime,

                            duration,

                            bookingFee
                    )
            );

            loadChargers();

        } catch (BookingException e) {

            paymentStatusLabel.setText(
                    "BOOKING FAILED"
            );

            paymentStatusLabel.setTextFill(
                    Color.web("#ff6978")
            );

            showError(
                    e.getMessage()
            );

        } catch (Exception e) {

            paymentStatusLabel.setText(
                    "BOOKING FAILED"
            );

            paymentStatusLabel.setTextFill(
                    Color.web("#ff6978")
            );

            showError(
                    "Booking failed: " +
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // TIME SLOTS
    // ============================================================

    private void populateTimeSlots() {

        if (timeBox == null) {
            return;
        }

        timeBox.getItems().clear();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "HH:mm"
                );

        LocalDate selectedDate =
                datePicker.getValue();

        LocalTime now =
                LocalTime.now();

        LocalTime firstAllowed;

        if (now.getMinute() < 30) {

            firstAllowed =
                    now.withMinute(30)
                            .withSecond(0)
                            .withNano(0);

        } else {

            firstAllowed =
                    now.plusHours(1)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);
        }

        for (int hour = 0;
             hour < 24;
             hour++) {

            for (int minute :
                    new int[]{0, 30}) {

                LocalTime slot =
                        LocalTime.of(
                                hour,
                                minute
                        );

                if (selectedDate != null &&
                        selectedDate.equals(
                                LocalDate.now()
                        ) &&
                        slot.isBefore(
                                firstAllowed
                        )) {

                    continue;
                }

                timeBox.getItems().add(
                        slot.format(
                                formatter
                        )
                );
            }
        }

        if (timeBox.getItems().isEmpty()) {
            return;
        }

        if (selectedDate != null &&
                selectedDate.equals(
                        LocalDate.now()
                )) {

            timeBox
                    .getSelectionModel()
                    .selectFirst();

        } else if (
                timeBox.getItems()
                        .contains("10:00")
        ) {

            timeBox
                    .getSelectionModel()
                    .select(
                            "10:00"
                    );

        } else {

            timeBox
                    .getSelectionModel()
                    .selectFirst();
        }
    }


    // ============================================================
    // VALIDATE DATE / TIME
    // ============================================================

    private void validateDateAndTime() {

        if (datePicker == null ||
                timeBox == null) {

            return;
        }

        LocalDate date =
                datePicker.getValue();

        String time =
                timeBox.getValue();

        if (date == null ||
                time == null) {

            return;
        }

        if (date.isBefore(
                LocalDate.now()
        )) {

            showError(
                    "Selected date is in the past."
            );

            return;
        }

        if (date.equals(
                LocalDate.now()
        )) {

            try {

                LocalTime selected =
                        LocalTime.parse(
                                time
                        );

                if (selected.isBefore(
                        LocalTime.now()
                )) {

                    showError(
                            "Selected time has already passed."
                    );

                    return;
                }

            } catch (Exception e) {

                showError(
                        "Invalid time selected."
                );

                return;
            }
        }

        clearMessage();
    }


    // ============================================================
    // FUTURISTIC BACKGROUND
    // ============================================================

    private Pane createBackground() {

        Pane pane =
                new Pane();

        pane.setMouseTransparent(
                true
        );

        // ========================================================
        // BASE
        // ========================================================

        Rectangle base =
                new Rectangle();

        base.widthProperty().bind(
                pane.widthProperty()
        );

        base.heightProperty().bind(
                pane.heightProperty()
        );

        base.setFill(
                Color.web("#02080d")
        );

        pane.getChildren().add(
                base
        );

        // ========================================================
        // GRID
        // ========================================================

        createAnimatedGrid(
                pane
        );

        // ========================================================
        // ENERGY NETWORK
        // ========================================================

        createEnergyNetwork(
                pane
        );

        // ========================================================
        // HOLOGRAPHIC RINGS
        // ========================================================

        Circle ring1 =
                createHologramRing(
                        230,
                        0.13
                );

        ring1.centerXProperty().bind(
                pane.widthProperty()
                        .multiply(0.17)
        );

        ring1.centerYProperty().bind(
                pane.heightProperty()
                        .multiply(0.42)
        );

        Circle ring2 =
                createHologramRing(
                        165,
                        0.09
                );

        ring2.centerXProperty().bind(
                pane.widthProperty()
                        .multiply(0.17)
        );

        ring2.centerYProperty().bind(
                pane.heightProperty()
                        .multiply(0.42)
        );

        Circle ring3 =
                createHologramRing(
                        300,
                        0.07
                );

        ring3.centerXProperty().bind(
                pane.widthProperty()
                        .multiply(0.82)
        );

        ring3.centerYProperty().bind(
                pane.heightProperty()
                        .multiply(0.72)
        );

        pane.getChildren().addAll(
                ring1,
                ring2,
                ring3
        );

        animateDashedRing(
                ring1,
                18
        );

        animateDashedRing(
                ring2,
                -25
        );

        animateDashedRing(
                ring3,
                32
        );

        // ========================================================
        // FLOATING PARTICLES
        // ========================================================

        createFloatingParticles(
                pane
        );

        // ========================================================
        // NETWORK NODES
        // ========================================================

        createNetworkNodes(
                pane
        );

        // ========================================================
        // SCAN LINES
        // ========================================================

        createScanLines(
                pane
        );

        // ========================================================
        // CENTRAL ENERGY CORE
        // ========================================================

        createEnergyCore(
                pane
        );

        // ========================================================
        // RANDOM FLASHES
        // ========================================================

        Timeline flashTimeline =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(1.6),
                                e ->
                                        createEnergyFlash(
                                                pane
                                        )
                        )
                );

        flashTimeline.setCycleCount(
                Timeline.INDEFINITE
        );

        registerBackgroundAnimation(
                flashTimeline
        );

        return pane;
    }


    // ============================================================
    // ANIMATED GRID
    // ============================================================

    private void createAnimatedGrid(
            Pane pane) {

        // --------------------------------------------------------
        // VERTICAL GRID
        // --------------------------------------------------------

        for (int x = 0;
             x <= 1800;
             x += 70) {

            Line line =
                    new Line();

            line.setStartX(
                    x
            );

            line.setEndX(
                    x
            );

            line.setStartY(
                    0
            );

            line.endYProperty().bind(
                    pane.heightProperty()
            );

            line.setStroke(
                    Color.web("#09242a")
            );

            line.setOpacity(
                    0.24
            );

            pane.getChildren().add(
                    line
            );

            animateGridLine(
                    line,
                    x % 140 == 0
            );
        }

        // --------------------------------------------------------
        // HORIZONTAL GRID
        // --------------------------------------------------------

        for (int y = 0;
             y <= 1200;
             y += 70) {

            Line line =
                    new Line();

            line.setStartY(
                    y
            );

            line.setEndY(
                    y
            );

            line.setStartX(
                    0
            );

            line.endXProperty().bind(
                    pane.widthProperty()
            );

            line.setStroke(
                    Color.web("#09242a")
            );

            line.setOpacity(
                    0.24
            );

            pane.getChildren().add(
                    line
            );

            animateGridLine(
                    line,
                    y % 140 == 0
            );
        }
    }


    // ============================================================
    // GRID LINE ANIMATION
    // ============================================================

    private void animateGridLine(
            Line line,
            boolean strong) {

        Timeline animation =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        line.opacityProperty(),
                                        strong
                                                ? 0.36
                                                : 0.20
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(2.5),

                                new KeyValue(
                                        line.opacityProperty(),
                                        strong
                                                ? 0.12
                                                : 0.06
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(5),

                                new KeyValue(
                                        line.opacityProperty(),
                                        strong
                                                ? 0.36
                                                : 0.20
                                )
                        )
                );

        animation.setCycleCount(
                Timeline.INDEFINITE
        );

        animation.setDelay(
                Duration.millis(
                        random.nextInt(2500)
                )
        );

        registerBackgroundAnimation(
                animation
        );
    }


    // ============================================================
    // ENERGY NETWORK
    // ============================================================

    private void createEnergyNetwork(
            Pane pane) {

        for (int i = 0;
             i < 7;
             i++) {

            double y =
                    110 +
                    i * 105;

            Polyline route =
                    new Polyline();

            route.getPoints().addAll(

                    0.0,
                    y,

                    140.0,
                    y,

                    220.0,
                    y - 35,

                    420.0,
                    y - 35,

                    500.0,
                    y + 30,

                    720.0,
                    y + 30,

                    800.0,
                    y - 25,

                    1060.0,
                    y - 25,

                    1140.0,
                    y + 25,

                    1500.0,
                    y + 25
            );

            route.setFill(
                    Color.TRANSPARENT
            );

            route.setStroke(
                    Color.web("#15515a")
            );

            route.setStrokeWidth(
                    i % 2 == 0
                            ? 1.3
                            : 0.8
            );

            route.setOpacity(
                    0.42
            );

            pane.getChildren().add(
                    route
            );

            // Main energy packet
            createEnergyPacket(
                    pane,
                    route,
                    i,
                    0
            );

            // Second packet gives the network more life
            if (i % 2 == 0) {

                createEnergyPacket(
                        pane,
                        route,
                        i,
                        1600
                );
            }
        }
    }


    // ============================================================
    // ENERGY PACKET FOLLOWING ROUTE
    // ============================================================

    private void createEnergyPacket(
            Pane pane,
            Polyline route,
            int index,
            int delayMillis) {

        Circle packet =
                new Circle(
                        index % 2 == 0
                                ? 3.4
                                : 2.6,
                        Color.web("#5ff2c8")
                );

        packet.setOpacity(
                0.85
        );

        pane.getChildren().add(
                packet
        );

        List<Double> points =
                route.getPoints();

        if (points.size() < 4) {
            return;
        }

        double startX =
                points.get(0);

        double startY =
                points.get(1);

        packet.setLayoutX(
                startX
        );

        packet.setLayoutY(
                startY
        );

        Timeline movement =
                new Timeline();

        int pointCount =
                points.size() / 2;

        double totalDuration =
                5.0 +
                index * 0.45;

        for (int i = 0;
             i < pointCount;
             i++) {

            double x =
                    points.get(
                            i * 2
                    );

            double y =
                    points.get(
                            i * 2 + 1
                    );

            double progress =
                    (double) i /
                    (pointCount - 1);

            movement.getKeyFrames().add(
                    new KeyFrame(
                            Duration.seconds(
                                    totalDuration *
                                    progress
                            ),
                            new KeyValue(
                                    packet
                                            .layoutXProperty(),
                                    x
                            ),
                            new KeyValue(
                                    packet
                                            .layoutYProperty(),
                                    y
                            )
                    )
            );
        }

        movement.setCycleCount(
                Timeline.INDEFINITE
        );

        movement.setDelay(
                Duration.millis(
                        delayMillis +
                        index * 450L
                )
        );

        registerBackgroundAnimation(
                movement
        );

        // --------------------------------------------------------
        // PACKET GLOW
        // --------------------------------------------------------

        Timeline glow =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        packet
                                                .opacityProperty(),
                                        0.20
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(0.7),

                                new KeyValue(
                                        packet
                                                .opacityProperty(),
                                        0.95
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(1.4),

                                new KeyValue(
                                        packet
                                                .opacityProperty(),
                                        0.25
                                )
                        )
                );

        glow.setCycleCount(
                Timeline.INDEFINITE
        );

        glow.setDelay(
                Duration.millis(
                        delayMillis
                )
        );

        registerBackgroundAnimation(
                glow
        );
    }


    // ============================================================
    // HOLOGRAM RING
    // ============================================================

    private Circle createHologramRing(
            double radius,
            double opacity) {

        Circle ring =
                new Circle(
                        radius
                );

        ring.setFill(
                Color.TRANSPARENT
        );

        ring.setStroke(
                Color.web("#28d6c5")
        );

        ring.setStrokeWidth(
                1
        );

        ring.setOpacity(
                opacity
        );

        /*
         * Dashed stroke makes rotation visually visible.
         */
        ring.getStrokeDashArray().addAll(
                12.0,
                18.0,
                4.0,
                22.0
        );

        return ring;
    }


    // ============================================================
    // DASHED RING ANIMATION
    // ============================================================

    private void animateDashedRing(
            Circle ring,
            double speed) {

        Timeline rotation =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        ring
                                                .strokeDashOffsetProperty(),
                                        0
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(
                                        Math.abs(speed)
                                ),

                                new KeyValue(
                                        ring
                                                .strokeDashOffsetProperty(),
                                        speed > 0
                                                ? -120
                                                : 120
                                )
                        )
                );

        rotation.setCycleCount(
                Timeline.INDEFINITE
        );

        registerBackgroundAnimation(
                rotation
        );
    }


    // ============================================================
    // FLOATING PARTICLES
    // ============================================================

    private void createFloatingParticles(
            Pane pane) {

        /*
         * Reduced from 70 to 45.
         *
         * The page still looks animated but uses fewer
         * continuously running animations.
         */
        for (int i = 0;
             i < 45;
             i++) {

            Circle particle =
                    new Circle(
                            0.8 +
                            random.nextDouble() * 2.0
                    );

            particle.setFill(
                    i % 3 == 0
                            ? Color.web("#5ff2c8")
                            : Color.web("#2db9c8")
            );

            particle.setOpacity(
                    0.10 +
                    random.nextDouble() * 0.40
            );

            particle.setLayoutX(
                    random.nextDouble() * 1600
            );

            particle.setLayoutY(
                    random.nextDouble() * 900
            );

            pane.getChildren().add(
                    particle
            );

            double duration =
                    4 +
                    random.nextDouble() * 6;

            TranslateTransition movement =
                    new TranslateTransition(
                            Duration.seconds(
                                    duration
                            ),
                            particle
                    );

            movement.setByY(
                    -40 -
                    random.nextDouble() * 100
            );

            movement.setByX(
                    -20 +
                    random.nextDouble() * 40
            );

            movement.setAutoReverse(
                    true
            );

            movement.setCycleCount(
                    TranslateTransition.INDEFINITE
            );

            registerBackgroundAnimation(
                    movement
            );
        }
    }


    // ============================================================
    // NETWORK NODES
    // ============================================================

    private void createNetworkNodes(
            Pane pane) {

        for (int i = 0;
             i < 20;
             i++) {

            Circle node =
                    new Circle(
                            3.5,
                            Color.web("#35d9c7")
                    );

            node.setCenterX(
                    70 +
                    random.nextDouble() * 1400
            );

            node.setCenterY(
                    70 +
                    random.nextDouble() * 720
            );

            node.setOpacity(
                    0.30
            );

            pane.getChildren().add(
                    node
            );

            Timeline nodePulse =
                    new Timeline(

                            new KeyFrame(
                                    Duration.ZERO,

                                    new KeyValue(
                                            node.radiusProperty(),
                                            2.5
                                    ),

                                    new KeyValue(
                                            node.opacityProperty(),
                                            0.15
                                    )
                            ),

                            new KeyFrame(
                                    Duration.seconds(1.2),

                                    new KeyValue(
                                            node.radiusProperty(),
                                            6
                                    ),

                                    new KeyValue(
                                            node.opacityProperty(),
                                            0.75
                                    )
                            ),

                            new KeyFrame(
                                    Duration.seconds(2.4),

                                    new KeyValue(
                                            node.radiusProperty(),
                                            2.5
                                    ),

                                    new KeyValue(
                                            node.opacityProperty(),
                                            0.15
                                    )
                            )
                    );

            nodePulse.setCycleCount(
                    Timeline.INDEFINITE
            );

            nodePulse.setDelay(
                    Duration.seconds(
                            random.nextDouble() * 2
                    )
            );

            registerBackgroundAnimation(
                    nodePulse
            );
        }
    }


    // ============================================================
    // SCAN LINES
    // ============================================================

    private void createScanLines(
            Pane pane) {

        /*
         * Horizontal scanning beams.
         *
         * Earlier version accidentally created tall vertical
         * rectangles. These now move horizontally across the
         * entire screen.
         */
        for (int i = 0;
             i < 3;
             i++) {

            Rectangle scan =
                    new Rectangle();

            scan.setHeight(
                    2
            );

            scan.widthProperty().bind(
                    pane.widthProperty()
            );

            scan.setFill(
                    Color.web("#35d9c7")
            );

            scan.setOpacity(
                    0.035
            );

            scan.setLayoutY(
                    -150 -
                    i * 250
            );

            pane.getChildren().add(
                    scan
            );

            TranslateTransition scanMove =
                    new TranslateTransition(
                            Duration.seconds(
                                    7 +
                                    i * 2
                            ),
                            scan
                    );

            scanMove.setFromY(
                    0
            );

            scanMove.setToY(
                    1200
            );

            scanMove.setCycleCount(
                    TranslateTransition.INDEFINITE
            );

            scanMove.setDelay(
                    Duration.seconds(
                            i * 2
                    )
            );

            registerBackgroundAnimation(
                    scanMove
            );

            // Glow pulse for scanner
            Timeline scanGlow =
                    new Timeline(

                            new KeyFrame(
                                    Duration.ZERO,

                                    new KeyValue(
                                            scan.opacityProperty(),
                                            0.02
                                    )
                            ),

                            new KeyFrame(
                                    Duration.seconds(1.5),

                                    new KeyValue(
                                            scan.opacityProperty(),
                                            0.08
                                    )
                            ),

                            new KeyFrame(
                                    Duration.seconds(3),

                                    new KeyValue(
                                            scan.opacityProperty(),
                                            0.02
                                    )
                            )
                    );

            scanGlow.setCycleCount(
                    Timeline.INDEFINITE
            );

            registerBackgroundAnimation(
                    scanGlow
            );
        }
    }


    // ============================================================
    // ENERGY CORE
    // ============================================================

    private void createEnergyCore(
            Pane pane) {

        Circle outerGlow =
                new Circle(
                        95,
                        Color.web("#073d40")
                );

        outerGlow.setOpacity(
                0.10
        );

        outerGlow.centerXProperty().bind(
                pane.widthProperty()
                        .multiply(0.53)
        );

        outerGlow.centerYProperty().bind(
                pane.heightProperty()
                        .multiply(0.50)
        );

        pane.getChildren().add(
                outerGlow
        );

        Circle middleGlow =
                new Circle(
                        55,
                        Color.web("#0b7770")
                );

        middleGlow.setOpacity(
                0.08
        );

        middleGlow.centerXProperty().bind(
                pane.widthProperty()
                        .multiply(0.53)
        );

        middleGlow.centerYProperty().bind(
                pane.heightProperty()
                        .multiply(0.50)
        );

        pane.getChildren().add(
                middleGlow
        );

        Circle core =
                new Circle(
                        12,
                        Color.web("#5ff2c8")
                );

        core.setOpacity(
                0.18
        );

        core.centerXProperty().bind(
                pane.widthProperty()
                        .multiply(0.53)
        );

        core.centerYProperty().bind(
                pane.heightProperty()
                        .multiply(0.50)
        );

        pane.getChildren().add(
                core
        );

        // --------------------------------------------------------
        // OUTER PULSE
        // --------------------------------------------------------

        Timeline outerPulse =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        outerGlow.radiusProperty(),
                                        70
                                ),

                                new KeyValue(
                                        outerGlow.opacityProperty(),
                                        0.06
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(2),

                                new KeyValue(
                                        outerGlow.radiusProperty(),
                                        135
                                ),

                                new KeyValue(
                                        outerGlow.opacityProperty(),
                                        0.20
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(4),

                                new KeyValue(
                                        outerGlow.radiusProperty(),
                                        70
                                ),

                                new KeyValue(
                                        outerGlow.opacityProperty(),
                                        0.06
                                )
                        )
                );

        outerPulse.setCycleCount(
                Timeline.INDEFINITE
        );

        registerBackgroundAnimation(
                outerPulse
        );

        // --------------------------------------------------------
        // MIDDLE PULSE
        // --------------------------------------------------------

        Timeline middlePulse =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        middleGlow.radiusProperty(),
                                        35
                                ),

                                new KeyValue(
                                        middleGlow.opacityProperty(),
                                        0.05
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(1.5),

                                new KeyValue(
                                        middleGlow.radiusProperty(),
                                        75
                                ),

                                new KeyValue(
                                        middleGlow.opacityProperty(),
                                        0.18
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(3),

                                new KeyValue(
                                        middleGlow.radiusProperty(),
                                        35
                                ),

                                new KeyValue(
                                        middleGlow.opacityProperty(),
                                        0.05
                                )
                        )
                );

        middlePulse.setCycleCount(
                Timeline.INDEFINITE
        );

        registerBackgroundAnimation(
                middlePulse
        );

        // --------------------------------------------------------
        // CORE PULSE
        // --------------------------------------------------------

        Timeline corePulse =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,

                                new KeyValue(
                                        core.radiusProperty(),
                                        8
                                ),

                                new KeyValue(
                                        core.opacityProperty(),
                                        0.15
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(1),

                                new KeyValue(
                                        core.radiusProperty(),
                                        20
                                ),

                                new KeyValue(
                                        core.opacityProperty(),
                                        0.60
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(2),

                                new KeyValue(
                                        core.radiusProperty(),
                                        8
                                ),

                                new KeyValue(
                                        core.opacityProperty(),
                                        0.15
                                )
                        )
                );

        corePulse.setCycleCount(
                Timeline.INDEFINITE
        );

        registerBackgroundAnimation(
                corePulse
        );
    }


    // ============================================================
    // ENERGY FLASH
    // ============================================================

    private void createEnergyFlash(
            Pane pane) {

        Line flash =
                new Line(
                        0,
                        0,
                        100,
                        0
                );

        flash.setStroke(
                Color.web("#5ff2c8")
        );

        flash.setStrokeWidth(
                1.5
        );

        flash.setOpacity(
                0
        );

        flash.setLayoutX(
                100 +
                random.nextDouble() * 1200
        );

        flash.setLayoutY(
                100 +
                random.nextDouble() * 650
        );

        pane.getChildren().add(
                flash
        );

        FadeTransition fadeIn =
                new FadeTransition(
                        Duration.millis(100),
                        flash
                );

        fadeIn.setFromValue(
                0
        );

        fadeIn.setToValue(
                0.80
        );

        FadeTransition fadeOut =
                new FadeTransition(
                        Duration.millis(400),
                        flash
                );

        fadeOut.setFromValue(
                0.80
        );

        fadeOut.setToValue(
                0
        );

        ParallelTransition sequence =
                new ParallelTransition(
                        fadeIn,
                        fadeOut
                );

        sequence.setOnFinished(
                e ->
                        pane.getChildren()
                                .remove(flash)
        );

        sequence.play();
    }


    // ============================================================
    // PANEL
    // ============================================================

    private VBox createPanel() {

        VBox panel =
                new VBox(9);

        panel.setPadding(
                new Insets(16)
        );

        panel.setMaxWidth(
                Double.MAX_VALUE
        );

        panel.setStyle(
                "-fx-background-color: rgba(6,20,26,0.94);" +
                "-fx-border-color: #16414a;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;"
        );

        return panel;
    }


    // ============================================================
    // SECTION TITLE
    // ============================================================

    private Label createSectionTitle(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        label.setTextFill(
                Color.web("#7de3e8")
        );

        return label;
    }


    // ============================================================
    // FIELD LABEL
    // ============================================================

    private Label createFieldLabel(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        9
                )
        );

        label.setTextFill(
                Color.web("#607a81")
        );

        return label;
    }


    // ============================================================
    // COMBO STYLE
    // ============================================================

    private String comboBoxStyle() {

        return
                "-fx-background-color: #071319;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #23545d;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-weight: bold;";
    }


    // ============================================================
    // DATE PICKER STYLE
    // ============================================================

    private String datePickerStyle() {

        return
                "-fx-background-color: #071319;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #23545d;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-weight: bold;";
    }


    // ============================================================
    // SPINNER STYLE
    // ============================================================

    private String spinnerStyle() {

        return
                "-fx-background-color: #071319;" +
                "-fx-border-color: #23545d;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-font-weight: bold;";
    }


    // ============================================================
    // CHARGER NORMAL
    // ============================================================

    private String chargerCardStyle(
            boolean selected) {

        if (selected) {

            return
                    "-fx-background-color: #123b3d;" +
                    "-fx-border-color: #5ff2c8;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 13;" +
                    "-fx-background-radius: 13;" +
                    "-fx-cursor: hand;";
        }

        return
                "-fx-background-color: #081a20;" +
                "-fx-border-color: #20505a;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 13;" +
                "-fx-background-radius: 13;" +
                "-fx-cursor: hand;";
    }


    // ============================================================
    // CHARGER HOVER
    // ============================================================

    private String chargerCardHoverStyle() {

        return
                "-fx-background-color: #102e34;" +
                "-fx-border-color: #35cfc0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 13;" +
                "-fx-background-radius: 13;" +
                "-fx-cursor: hand;";
    }


    // ============================================================
    // CHARGER UNAVAILABLE
    // ============================================================

    private String chargerCardUnavailableStyle() {

        return
                "-fx-background-color: #16191d;" +
                "-fx-border-color: #60343b;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 13;" +
                "-fx-background-radius: 13;" +
                "-fx-opacity: 0.68;";
    }


    // ============================================================
    // BOOK BUTTON
    // ============================================================

    private String bookButtonStyle(
            boolean hover) {

        if (hover) {

            return
                    "-fx-background-color: #72ffe0;" +
                    "-fx-text-fill: #021012;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 11;" +
                    "-fx-border-color: #a6fff0;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;";
        }

        return
                "-fx-background-color: #1bc8b6;" +
                "-fx-text-fill: #021012;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 11;" +
                "-fx-border-color: #45ead5;" +
                "-fx-border-width: 1;" +
                "-fx-cursor: hand;";
    }


    // ============================================================
    // CARD ANIMATION
    // ============================================================

    private void animateCard(
            VBox card,
            int index) {

        card.setOpacity(
                0
        );

        card.setScaleX(
                0.92
        );

        card.setScaleY(
                0.92
        );

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(
                                350 +
                                index * 70
                        ),
                        card
                );

        fade.setFromValue(
                0
        );

        fade.setToValue(
                1
        );

        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(
                                350 +
                                index * 70
                        ),
                        card
                );

        scale.setFromX(
                0.92
        );

        scale.setFromY(
                0.92
        );

        scale.setToX(
                1
        );

        scale.setToY(
                1
        );

        TranslateTransition slide =
                new TranslateTransition(
                        Duration.millis(
                                350 +
                                index * 70
                        ),
                        card
                );

        slide.setFromY(
                18
        );

        slide.setToY(
                0
        );

        ParallelTransition animation =
                new ParallelTransition(
                        fade,
                        scale,
                        slide
                );

        animation.play();
    }


    // ============================================================
    // PAGE ENTRANCE
    // ============================================================

    private void animateEntrance(
            Node node) {

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(
                                650
                        ),
                        node
                );

        fade.setFromValue(
                0
        );

        fade.setToValue(
                1
        );

        TranslateTransition slide =
                new TranslateTransition(
                        Duration.millis(
                                650
                        ),
                        node
                );

        slide.setFromY(
                18
        );

        slide.setToY(
                0
        );

        ParallelTransition animation =
                new ParallelTransition(
                        fade,
                        slide
                );

        animation.play();
    }


    // ============================================================
    // ERROR
    // ============================================================

    private void showError(
            String message) {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(
                "⚠  " +
                message
        );

        messageLabel.setTextFill(
                Color.web("#ff6978")
        );

        messageLabel.setStyle(
                "-fx-background-color: rgba(80,20,28,0.85);" +
                "-fx-border-color: #873d49;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-padding: 10;"
        );

        messageLabel.setVisible(
                true
        );

        messageLabel.setManaged(
                true
        );

        TranslateTransition shake =
                new TranslateTransition(
                        Duration.millis(65),
                        messageLabel
                );

        shake.setFromX(
                -4
        );

        shake.setToX(
                4
        );

        shake.setAutoReverse(
                true
        );

        shake.setCycleCount(
                4
        );

        shake.play();
    }


    // ============================================================
    // SUCCESS
    // ============================================================

    private void showSuccess(
            String message) {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(
                message
        );

        messageLabel.setTextFill(
                Color.web("#5ff2c8")
        );

        messageLabel.setStyle(
                "-fx-background-color: rgba(10,53,45,0.90);" +
                "-fx-border-color: #2d927d;" +
                "-fx-border-radius: 9;" +
                "-fx-background-radius: 9;" +
                "-fx-padding: 12;"
        );

        messageLabel.setVisible(
                true
        );

        messageLabel.setManaged(
                true
        );

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(400),
                        messageLabel
                );

        fade.setFromValue(
                0
        );

        fade.setToValue(
                1
        );

        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(400),
                        messageLabel
                );

        scale.setFromX(
                0.96
        );

        scale.setFromY(
                0.96
        );

        scale.setToX(
                1
        );

        scale.setToY(
                1
        );

        ParallelTransition animation =
                new ParallelTransition(
                        fade,
                        scale
                );

        animation.play();
    }


    // ============================================================
    // CLEAR MESSAGE
    // ============================================================

    private void clearMessage() {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(
                ""
        );

        messageLabel.setVisible(
                false
        );

        messageLabel.setManaged(
                false
        );

        messageLabel.setTranslateX(
                0
        );
    }
}