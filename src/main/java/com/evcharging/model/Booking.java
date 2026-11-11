package com.evcharging.model;

public class Booking {

    private int bookingId;
    private int userId;
    private int stationId;
    private int chargerId;

    private String bookingDate;
    private String startTime;

    private int duration;

    private double cost;
    private double bookingFee;
    private double energyConsumed;
    private double chargingCost;

    private String status;


    public Booking(
            int bookingId,
            int userId,
            int stationId,
            int chargerId,
            String bookingDate,
            String startTime,
            int duration,
            double cost,
            double bookingFee,
            double energyConsumed,
            double chargingCost,
            String status) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.stationId = stationId;
        this.chargerId = chargerId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.duration = duration;
        this.cost = cost;
        this.bookingFee = bookingFee;
        this.energyConsumed = energyConsumed;
        this.chargingCost = chargingCost;
        this.status = status;
    }


    public Booking(
            int userId,
            int stationId,
            int chargerId,
            String bookingDate,
            String startTime,
            int duration,
            double cost,
            double bookingFee,
            double energyConsumed,
            double chargingCost,
            String status) {

        this.bookingId = 0;
        this.userId = userId;
        this.stationId = stationId;
        this.chargerId = chargerId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.duration = duration;
        this.cost = cost;
        this.bookingFee = bookingFee;
        this.energyConsumed = energyConsumed;
        this.chargingCost = chargingCost;
        this.status = status;
    }


    public Booking(
            int bookingId,
            int userId,
            int stationId,
            int chargerId,
            String bookingDate,
            String startTime,
            int duration,
            double cost,
            String status) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.stationId = stationId;
        this.chargerId = chargerId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.duration = duration;
        this.cost = cost;
        this.bookingFee = 30.00;
        this.energyConsumed = 0.00;
        this.chargingCost = 0.00;
        this.status = status;
    }


    public Booking(
            int userId,
            int stationId,
            int chargerId,
            String bookingDate,
            String startTime,
            int duration,
            double cost,
            String status) {

        this.bookingId = 0;
        this.userId = userId;
        this.stationId = stationId;
        this.chargerId = chargerId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.duration = duration;
        this.cost = cost;
        this.bookingFee = 30.00;
        this.energyConsumed = 0.00;
        this.chargingCost = 0.00;
        this.status = status;
    }


    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getStationId() {
        return stationId;
    }

    public int getChargerId() {
        return chargerId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getBookingFee() {
        return bookingFee;
    }

    public void setBookingFee(double bookingFee) {
        this.bookingFee = bookingFee;
    }

    public double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }

    public double getChargingCost() {
        return chargingCost;
    }

    public void setChargingCost(double chargingCost) {
        this.chargingCost = chargingCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void displayBooking() {

        System.out.println("======================================");
        System.out.println("           BOOKING DETAILS");
        System.out.println("======================================");
        System.out.println("Booking ID       : " + bookingId);
        System.out.println("User ID          : " + userId);
        System.out.println("Station ID       : " + stationId);
        System.out.println("Charger ID       : " + chargerId);
        System.out.println("Date             : " + bookingDate);
        System.out.println("Start Time       : " + startTime);
        System.out.println("Duration         : " + duration + " minutes");
        System.out.println("Booking Fee      : ₹" + bookingFee);
        System.out.println("Energy Consumed  : " + energyConsumed + " kWh");
        System.out.println("Charging Cost    : ₹" + chargingCost);
        System.out.println("Total Paid       : ₹" + (bookingFee + chargingCost));
        System.out.println("Status           : " + status);
        System.out.println("======================================");
    }
}