package com.evcharging.model;

public class Station {

    // Private data members - Encapsulation
    private int stationId;
    private String stationName;
    private String location;
    private int totalChargers;

    // Constructor used when retrieving a station from MySQL
    public Station(int stationId, String stationName,
                   String location, int totalChargers) {

        this.stationId = stationId;
        this.stationName = stationName;
        this.location = location;
        this.totalChargers = totalChargers;
    }

    // Constructor used when adding a new station
    // stationId will be generated automatically by MySQL
    public Station(String stationName, String location,
                   int totalChargers) {

        this.stationId = 0;
        this.stationName = stationName;
        this.location = location;
        this.totalChargers = totalChargers;
    }

    // Getter for station ID
    public int getStationId() {
        return stationId;
    }

    // Getter for station name
    public String getStationName() {
        return stationName;
    }

    // Getter for location
    public String getLocation() {
        return location;
    }

    // Getter for total chargers
    public int getTotalChargers() {
        return totalChargers;
    }

    // Display station details
    public void displayStation() {

        System.out.println("======================================");
        System.out.println("          CHARGING STATION");
        System.out.println("======================================");
        System.out.println("Station ID      : " + stationId);
        System.out.println("Station Name    : " + stationName);
        System.out.println("Location        : " + location);
        System.out.println("Total Chargers  : " + totalChargers);
        System.out.println("======================================");
    }
}