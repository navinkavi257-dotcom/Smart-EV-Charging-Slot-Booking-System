package com.evcharging.model;

public class Charger {

    // Private data members - Encapsulation
    private int chargerId;
    private int stationId;
    private String chargerType;
    private double power;
    private boolean available;

    // Constructor used when retrieving a charger from MySQL
    public Charger(int chargerId, int stationId,
                   String chargerType, double power,
                   boolean available) {

        this.chargerId = chargerId;
        this.stationId = stationId;
        this.chargerType = chargerType;
        this.power = power;
        this.available = available;
    }

    // Constructor used when adding a new charger
    // chargerId will be generated automatically by MySQL
    public Charger(int stationId, String chargerType,
                   double power, boolean available) {

        this.chargerId = 0;
        this.stationId = stationId;
        this.chargerType = chargerType;
        this.power = power;
        this.available = available;
    }

    // Getter for charger ID
    public int getChargerId() {
        return chargerId;
    }

    // Getter for station ID
    public int getStationId() {
        return stationId;
    }

    // Getter for charger type
    public String getChargerType() {
        return chargerType;
    }

    // Getter for power
    public double getPower() {
        return power;
    }

    // Getter for availability
    public boolean isAvailable() {
        return available;
    }

    // Setter for availability
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Display charger details
    public void displayCharger() {

        System.out.println("======================================");
        System.out.println("             CHARGER DETAILS");
        System.out.println("======================================");
        System.out.println("Charger ID   : " + chargerId);
        System.out.println("Station ID   : " + stationId);
        System.out.println("Charger Type : " + chargerType);
        System.out.println("Power        : " + power + " kW");
        System.out.println("Available    : " +
                (available ? "Yes" : "No"));
        System.out.println("======================================");
    }
}