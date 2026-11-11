package com.evcharging.gui;

import javafx.scene.control.Alert;
import com.evcharging.model.Booking;
import com.evcharging.model.Charger;
import com.evcharging.model.Station;
import com.evcharging.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvoiceView {

    private final User user;
    private final Booking booking;
    private final Station station;
    private final Charger charger;

    public InvoiceView(
            User user,
            Booking booking,
            Station station,
            Charger charger) {

        this.user = user;
        this.booking = booking;
        this.station = station;
        this.charger = charger;
    }

    public void show(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #071116;"
        );

        VBox outer = new VBox(20);

        outer.setAlignment(Pos.TOP_CENTER);

        outer.setPadding(
                new Insets(30)
        );

        VBox invoice = createInvoice();

        HBox buttons = createButtons(stage);

        outer.getChildren().addAll(
                invoice,
                buttons
        );

        root.setCenter(outer);

        Scene scene = new Scene(
                root,
                1050,
                850
        );

        stage.setScene(scene);

        Rectangle2D bounds =
                Screen.getPrimary().getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.show();
    }

    private VBox createInvoice() {

        VBox invoice = new VBox(18);

        invoice.setMaxWidth(850);

        invoice.setPadding(
                new Insets(35, 45, 35, 45)
        );

        invoice.setStyle(
                "-fx-background-color: #f8fafb;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;"
        );

        HBox companyHeader =
                createCompanyHeader();

        Line line1 =
                createLine();

        Label receiptTitle =
                new Label("PAYMENT RECEIPT");

        receiptTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        24
                )
        );

        receiptTitle.setTextFill(
                Color.web("#10252b")
        );

        GridPane invoiceDetails =
                createInvoiceDetails();

        Line line2 =
                createLine();

        Label customerTitle =
                sectionTitle("CUSTOMER");

        Label customerName =
                valueLabel(
                        user.getName()
                );

        Label customerEmail =
                valueLabel(
                        user.getEmail()
                );

        Line line3 =
                createLine();

        Label chargingTitle =
                sectionTitle(
                        "CHARGING DETAILS"
                );

        GridPane chargingDetails =
                createChargingDetails();

        Line line4 =
                createLine();

        GridPane amountTable =
                createAmountTable();

        Line line5 =
                createLine();

        HBox paidBox =
                createPaidBox();

        Label thankYou =
                new Label(
                        "Thank you for using Smart EV Charging."
                );

        thankYou.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        13
                )
        );

        thankYou.setTextFill(
                Color.web("#617277")
        );

        Label confirmation =
                new Label(
                        "Your charging slot has been confirmed."
                );

        confirmation.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        confirmation.setTextFill(
                Color.web("#168c72")
        );

        VBox footer =
                new VBox(
                        5,
                        thankYou,
                        confirmation
                );

        footer.setAlignment(
                Pos.CENTER
        );

        invoice.getChildren().addAll(
                companyHeader,
                line1,
                receiptTitle,
                invoiceDetails,
                line2,
                customerTitle,
                customerName,
                customerEmail,
                line3,
                chargingTitle,
                chargingDetails,
                line4,
                amountTable,
                line5,
                paidBox,
                footer
        );

        return invoice;
    }

    private HBox createCompanyHeader() {

        Label logo =
                new Label("⚡");

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        38
                )
        );

        logo.setTextFill(
                Color.web("#0aa892")
        );

        Label company =
                new Label(
                        "SMART EV CHARGING"
                );

        company.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        23
                )
        );

        company.setTextFill(
                Color.web("#10252b")
        );

        Label subtitle =
                new Label(
                        "Smart EV Charging Slot Booking System"
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        subtitle.setTextFill(
                Color.web("#687a7f")
        );

        VBox companyInfo =
                new VBox(
                        3,
                        company,
                        subtitle
                );

        Label paid =
                new Label(
                        "PAID ✓"
                );

        paid.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        paid.setTextFill(
                Color.web("#168c72")
        );

        HBox header =
                new HBox(
                        12,
                        logo,
                        companyInfo
                );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                companyInfo,
                javafx.scene.layout.Priority.ALWAYS
        );

        header.getChildren().add(
                paid
        );

        return header;
    }

    private GridPane createInvoiceDetails() {

        GridPane grid =
                new GridPane();

        grid.setHgap(70);
        grid.setVgap(8);

        addDetail(
                grid,
                0,
                "INVOICE NO.",
                generateInvoiceNumber()
        );

        addDetail(
                grid,
                1,
                "BOOKING ID",
                "EVB-" + booking.getBookingId()
        );

        addDetail(
                grid,
                2,
                "PAYMENT DATE",
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "dd MMM yyyy"
                        )
                )
        );

        addDetail(
                grid,
                3,
                "PAYMENT METHOD",
                "Online Payment"
        );

        return grid;
    }

    private GridPane createChargingDetails() {

        GridPane grid =
                new GridPane();

        grid.setHgap(70);
        grid.setVgap(10);

        String stationName =
                station != null
                        ? station.getStationName()
                        : "Charging Station";

        String location =
                station != null
                        ? station.getLocation()
                        : "—";

        String chargerType =
                charger != null
                        ? charger.getChargerType()
                        : "EV Charger";

        String power =
                charger != null
                        ? String.format(
                                "%.0f kW",
                                charger.getPower()
                        )
                        : "—";

        addDetail(
                grid,
                0,
                "STATION",
                stationName
        );

        addDetail(
                grid,
                1,
                "LOCATION",
                location
        );

        addDetail(
                grid,
                2,
                "CHARGER",
                chargerType
        );

        addDetail(
                grid,
                3,
                "POWER",
                power
        );

        addDetail(
                grid,
                4,
                "DATE",
                booking.getBookingDate()
        );

        addDetail(
                grid,
                5,
                "START TIME",
                formatTime(
                        booking.getStartTime()
                )
        );

        addDetail(
                grid,
                6,
                "DURATION",
                booking.getDuration()
                        + " minutes"
        );

        return grid;
    }

    private GridPane createAmountTable() {

        GridPane grid =
                new GridPane();

        grid.setHgap(20);
        grid.setVgap(12);

        Label description =
                tableHeader("DESCRIPTION");

        Label amount =
                tableHeader("AMOUNT");

        grid.add(
                description,
                0,
                0
        );

        grid.add(
                amount,
                1,
                0
        );

        Label charging =
                valueLabel(
                        "EV Charging Service"
                );

        Label chargingAmount =
                amountLabel(
                        booking.getCost()
                );

        grid.add(
                charging,
                0,
                1
        );

        grid.add(
                chargingAmount,
                1,
                1
        );

        Label subtotal =
                valueLabel("Subtotal");

        Label subtotalAmount =
                amountLabel(
                        booking.getCost()
                );

        grid.add(
                subtotal,
                0,
                2
        );

        grid.add(
                subtotalAmount,
                1,
                2
        );

        Label tax =
                valueLabel("Tax");

        Label taxAmount =
                amountLabel(0);

        grid.add(
                tax,
                0,
                3
        );

        grid.add(
                taxAmount,
                1,
                3
        );

        Label bookingFee =
                valueLabel(
                        "Booking Fee"
                );

        Label bookingFeeAmount =
                amountLabel(0);

        grid.add(
                bookingFee,
                0,
                4
        );

        grid.add(
                bookingFeeAmount,
                1,
                4
        );

        Label total =
                new Label("TOTAL PAID");

        total.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        17
                )
        );

        total.setTextFill(
                Color.web("#10252b")
        );

        Label totalAmount =
                new Label(
                        formatMoney(
                                booking.getCost()
                        )
                );

        totalAmount.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        totalAmount.setTextFill(
                Color.web("#0a9f89")
        );

        grid.add(
                total,
                0,
                5
        );

        grid.add(
                totalAmount,
                1,
                5
        );

        return grid;
    }

    private HBox createPaidBox() {

        Label check =
                new Label("✓");

        check.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        30
                )
        );

        check.setTextFill(
                Color.web("#168c72")
        );

        Label text =
                new Label(
                        "PAYMENT SUCCESSFUL\n" +
                        "BOOKING CONFIRMED"
                );

        text.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        text.setTextFill(
                Color.web("#168c72")
        );

        HBox box =
                new HBox(
                        12,
                        check,
                        text
                );

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(14)
        );

        box.setStyle(
                "-fx-background-color: #e8f8f3;" +
                "-fx-background-radius: 10;"
        );

        return box;
    }

    private HBox createButtons(Stage stage) {

        Button save =
                new Button(
                        "SAVE RECEIPT"
                );

        save.setPrefSize(
                170,
                45
        );

        save.setStyle(
                "-fx-background-color: #12b8a3;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
        );

        save.setOnAction(e ->
                saveReceipt()
        );

        Button close =
                new Button("DONE");

        close.setPrefSize(
                130,
                45
        );

        close.setStyle(
                "-fx-background-color: #10252b;" +
                "-fx-text-fill: #8de8df;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
        );

        close.setOnAction(e ->
                new DashboardView(user).show(stage)
        );

        HBox buttons =
                new HBox(
                        15,
                        save,
                        close
                );

        buttons.setAlignment(
                Pos.CENTER
        );

        return buttons;
    }

    private void saveReceipt() {

        Alert alert =
                new Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Receipt"
        );

        alert.setHeaderText(
                "Receipt Ready"
        );

        alert.setContentText(
                "PDF receipt generation will be connected next."
        );

        alert.showAndWait();
    }

    private void addDetail(
            GridPane grid,
            int row,
            String title,
            String value) {

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        titleLabel.setTextFill(
                Color.web("#809196")
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        valueLabel.setTextFill(
                Color.web("#10252b")
        );

        grid.add(
                titleLabel,
                0,
                row
        );

        grid.add(
                valueLabel,
                1,
                row
        );
    }

    private Label sectionTitle(
            String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        label.setTextFill(
                Color.web("#0a9f89")
        );

        return label;
    }

    private Label tableHeader(
            String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        label.setTextFill(
                Color.web("#809196")
        );

        return label;
    }

    private Label valueLabel(
            String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        label.setTextFill(
                Color.web("#26383d")
        );

        return label;
    }

    private Label amountLabel(
            double amount) {

        Label label =
                new Label(
                        formatMoney(amount)
                );

        label.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        label.setTextFill(
                Color.web("#26383d")
        );

        return label;
    }

    private Line createLine() {

        Line line =
                new Line(
                        0,
                        0,
                        760,
                        0
                );

        line.setStroke(
                Color.web("#dce4e6")
        );

        return line;
    }

    private String generateInvoiceNumber() {

        return "INV-"
                + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern(
                                "yyyyMMdd-HHmmss"
                        )
                );
    }

    private String formatTime(
            String time) {

        try {

            java.time.LocalTime localTime =
                    java.time.LocalTime.parse(time);

            return localTime.format(
                    DateTimeFormatter.ofPattern(
                            "hh:mm a"
                    )
            );

        } catch (Exception e) {

            return time;
        }
    }

    private String formatMoney(
            double amount) {

        return String.format(
                "₹%,.2f",
                amount
        );
    }
}