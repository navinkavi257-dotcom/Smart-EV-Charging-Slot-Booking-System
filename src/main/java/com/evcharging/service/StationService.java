package com.evcharging.service;

import com.evcharging.model.Station;
import com.evcharging.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StationService {

    // Add a new charging station
    public boolean addStation(Station station) {

        String sql =
                "INSERT INTO stations " +
                "(station_name, location, total_chargers) " +
                "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, station.getStationName());
            ps.setString(2, station.getLocation());
            ps.setInt(3, station.getTotalChargers());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Station added successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to add station: " + e.getMessage()
            );
        }

        return false;
    }

    // View all charging stations
    public List<Station> getAllStations() {

        List<Station> stations = new ArrayList<>();

        String sql =
                "SELECT station_id, station_name, location, " +
                "total_chargers FROM stations";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Station station = new Station(
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        rs.getString("location"),
                        rs.getInt("total_chargers")
                );

                stations.add(station);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to retrieve stations: "
                            + e.getMessage()
            );
        }

        return stations;
    }

    // Search stations by location
    public List<Station> searchStations(String location) {

        List<Station> stations = new ArrayList<>();

        String sql =
                "SELECT station_id, station_name, location, " +
                "total_chargers FROM stations " +
                "WHERE location LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + location + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Station station = new Station(
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        rs.getString("location"),
                        rs.getInt("total_chargers")
                );

                stations.add(station);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Station search failed: "
                            + e.getMessage()
            );
        }

        return stations;
    }
}