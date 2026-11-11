package com.evcharging.service;

import com.evcharging.model.Charger;
import com.evcharging.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChargerService {

    // Add a new charger
    public boolean addCharger(Charger charger) {

        String sql =
                "INSERT INTO chargers " +
                "(station_id, charger_type, power, available) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, charger.getStationId());
            ps.setString(2, charger.getChargerType());
            ps.setDouble(3, charger.getPower());
            ps.setBoolean(4, charger.isAvailable());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Charger added successfully!");
                return true;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to add charger: " + e.getMessage()
            );
        }

        return false;
    }


    // View all chargers
    public List<Charger> getAllChargers() {

        List<Charger> chargers = new ArrayList<>();

        String sql =
                "SELECT charger_id, station_id, charger_type, " +
                "power, available FROM chargers";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Charger charger = new Charger(
                        rs.getInt("charger_id"),
                        rs.getInt("station_id"),
                        rs.getString("charger_type"),
                        rs.getDouble("power"),
                        rs.getBoolean("available")
                );

                chargers.add(charger);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve chargers: "
                            + e.getMessage()
            );
        }

        return chargers;
    }


    // View chargers for a particular station
    public List<Charger> getChargersByStation(int stationId) {

        List<Charger> chargers = new ArrayList<>();

        String sql =
                "SELECT charger_id, station_id, charger_type, " +
                "power, available FROM chargers " +
                "WHERE station_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, stationId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Charger charger = new Charger(
                        rs.getInt("charger_id"),
                        rs.getInt("station_id"),
                        rs.getString("charger_type"),
                        rs.getDouble("power"),
                        rs.getBoolean("available")
                );

                chargers.add(charger);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve station chargers: "
                            + e.getMessage()
            );
        }

        return chargers;
    }


    // Check available chargers at a station
    public List<Charger> getAvailableChargers(int stationId) {

        List<Charger> chargers = new ArrayList<>();

        String sql =
                "SELECT charger_id, station_id, charger_type, " +
                "power, available FROM chargers " +
                "WHERE station_id = ? AND available = TRUE";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, stationId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Charger charger = new Charger(
                        rs.getInt("charger_id"),
                        rs.getInt("station_id"),
                        rs.getString("charger_type"),
                        rs.getDouble("power"),
                        rs.getBoolean("available")
                );

                chargers.add(charger);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to check charger availability: "
                            + e.getMessage()
            );
        }

        return chargers;
    }
    // Deactivate a charger without deleting its database record
public boolean deactivateCharger(int chargerId) {

    String sql =
            "UPDATE chargers " +
            "SET available = FALSE " +
            "WHERE charger_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, chargerId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println(
                    "Charger " + chargerId +
                    " deactivated successfully!"
            );
            return true;
        }

    } catch (SQLException e) {

        System.out.println(
                "Failed to deactivate charger: "
                        + e.getMessage()
        );
    }

    return false;
    }
}