package com.evcharging.service;

import com.evcharging.exception.BookingException;
import com.evcharging.interfaces.Bookable;
import com.evcharging.model.Booking;
import com.evcharging.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookingService implements Bookable {

    // =============================================================
    // CURRENT PRICING
    // =============================================================

    public static final double BOOKING_FEE = 30.00;
    public static final double RATE_PER_KWH = 20.00;

    // =============================================================
    // OLD / LEGACY RATES
    // Kept so older parts of the project still compile.
    // =============================================================

    private static final double RATE_3_3_KW = 0.70;
    private static final double RATE_7_5_KW = 1.80;
    private static final double RATE_60_KW = 18.00;
    private static final double RATE_120_KW = 45.00;

    // =============================================================
    // TIME CONVERSION HELPER
    // FIX:
    // "10:00" -> "10:00:00"
    // "10:00:00" -> stays unchanged
    // =============================================================

    private java.sql.Time toSqlTime(String time)
            throws BookingException {

        if (time == null || time.trim().isEmpty()) {
            throw new BookingException("Booking time cannot be empty.");
        }

        String normalized = time.trim();

        // UI normally sends HH:mm
        if (normalized.matches("\\d{2}:\\d{2}")) {
            normalized = normalized + ":00";
        }

        try {
            return java.sql.Time.valueOf(normalized);

        } catch (IllegalArgumentException e) {
            throw new BookingException(
                    "Invalid booking time: " + time
            );
        }
    }

    // =============================================================
    // CREATE BOOKING
    // =============================================================

    public boolean createBooking(Booking booking)
            throws BookingException {

        if (booking == null) {
            throw new BookingException(
                    "Booking details cannot be empty."
            );
        }

        if (booking.getDuration() <= 0) {
            throw new BookingException(
                    "Booking duration must be greater than zero."
            );
        }

        if (booking.getBookingDate() == null ||
                booking.getBookingDate().trim().isEmpty()) {

            throw new BookingException(
                    "Booking date cannot be empty."
            );
        }

        // Convert the UI time safely.
        java.sql.Time sqlStartTime =
                toSqlTime(booking.getStartTime());

        final String chargerSql =
                "SELECT charger_id FROM chargers " +
                "WHERE charger_id = ? AND station_id = ?";

        final String overlapSql =
                "SELECT booking_id FROM bookings " +
                "WHERE charger_id = ? " +
                "AND station_id = ? " +
                "AND booking_date = ? " +
                "AND status IN ('CONFIRMED','CHARGING') " +
                "AND start_time < ADDTIME(?, SEC_TO_TIME(duration * 60)) " +
                "AND ADDTIME(start_time, SEC_TO_TIME(duration * 60)) > ? " +
                "LIMIT 1";

        final String walletSql =
                "SELECT balance FROM wallets " +
                "WHERE user_id = ? FOR UPDATE";

        final String insertSql =
                "INSERT INTO bookings " +
                "(user_id, station_id, charger_id, booking_date, " +
                "start_time, duration, cost, booking_fee, " +
                "energy_consumed, charging_cost, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        final String walletDeductSql =
                "UPDATE wallets " +
                "SET balance = balance - ? " +
                "WHERE user_id = ? AND balance >= ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // =================================================
                // 1. CHECK CHARGER EXISTS
                // =================================================

                try (PreparedStatement ps =
                             con.prepareStatement(chargerSql)) {

                    ps.setInt(1, booking.getChargerId());
                    ps.setInt(2, booking.getStationId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new BookingException(
                                    "Charger not found at this station."
                            );
                        }
                    }
                }

                // =================================================
                // 2. CHECK DATE + TIME OVERLAP
                // =================================================

                try (PreparedStatement ps =
                             con.prepareStatement(overlapSql)) {

                    ps.setInt(1, booking.getChargerId());
                    ps.setInt(2, booking.getStationId());

                    ps.setDate(
                            3,
                            java.sql.Date.valueOf(
                                    booking.getBookingDate()
                            )
                    );

                    ps.setTime(4, sqlStartTime);
                    ps.setTime(5, sqlStartTime);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {

                            throw new BookingException(
                                    "This charger is already booked " +
                                    "during the selected date and time."
                            );
                        }
                    }
                }

                // =================================================
                // 3. BOOKING FEE
                // =================================================

                double bookingFee =
                        booking.getBookingFee();

                if (bookingFee < 0) {

                    throw new BookingException(
                            "Invalid booking fee."
                    );
                }

                // =================================================
                // 4. CHECK WALLET
                // =================================================

                if (bookingFee > 0) {

                    double balance;

                    try (PreparedStatement ps =
                                 con.prepareStatement(walletSql)) {

                        ps.setInt(
                                1,
                                booking.getUserId()
                        );

                        try (ResultSet rs =
                                     ps.executeQuery()) {

                            if (!rs.next()) {

                                throw new BookingException(
                                        "Wallet not found. " +
                                        "Please open your wallet " +
                                        "and add money first."
                                );
                            }

                            balance =
                                    rs.getDouble("balance");
                        }
                    }

                    if (balance < bookingFee) {

                        throw new BookingException(
                                String.format(
                                        "Insufficient wallet balance. " +
                                        "Booking fee is ₹%.2f.",
                                        bookingFee
                                )
                        );
                    }
                }

                // =================================================
                // 5. INSERT BOOKING
                // =================================================

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     insertSql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    ps.setInt(
                            1,
                            booking.getUserId()
                    );

                    ps.setInt(
                            2,
                            booking.getStationId()
                    );

                    ps.setInt(
                            3,
                            booking.getChargerId()
                    );

                    ps.setDate(
                            4,
                            java.sql.Date.valueOf(
                                    booking.getBookingDate()
                            )
                    );

                    // IMPORTANT:
                    // Use converted HH:mm:ss value
                    ps.setTime(
                            5,
                            sqlStartTime
                    );

                    ps.setInt(
                            6,
                            booking.getDuration()
                    );

                    // Old cost field is retained for compatibility.
                    ps.setDouble(
                            7,
                            booking.getCost()
                    );

                    ps.setDouble(
                            8,
                            booking.getBookingFee()
                    );

                    ps.setDouble(
                            9,
                            booking.getEnergyConsumed()
                    );

                    ps.setDouble(
                            10,
                            booking.getChargingCost()
                    );

                    ps.setString(
                            11,
                            booking.getStatus()
                    );

                    int rows =
                            ps.executeUpdate();

                    if (rows == 0) {

                        throw new BookingException(
                                "Booking could not be created."
                        );
                    }

                    // =================================================
                    // GET GENERATED BOOKING ID
                    // =================================================

                    try (ResultSet keys =
                                 ps.getGeneratedKeys()) {

                        if (keys.next()) {

                            booking.setBookingId(
                                    keys.getInt(1)
                            );

                        } else {

                            throw new BookingException(
                                    "Booking created, but booking ID " +
                                    "could not be retrieved."
                            );
                        }
                    }
                }

                // =================================================
                // 6. DEDUCT ONLY BOOKING FEE
                // =================================================

                if (bookingFee > 0) {

                    try (PreparedStatement ps =
                                 con.prepareStatement(
                                         walletDeductSql
                                 )) {

                        ps.setDouble(
                                1,
                                bookingFee
                        );

                        ps.setInt(
                                2,
                                booking.getUserId()
                        );

                        ps.setDouble(
                                3,
                                bookingFee
                        );

                        if (ps.executeUpdate() == 0) {

                            throw new BookingException(
                                    "Booking fee could not be " +
                                    "deducted from wallet."
                            );
                        }
                    }
                }

                // =================================================
                // 7. COMMIT
                // =================================================

                con.commit();

                return true;

            } catch (BookingException e) {

                con.rollback();

                throw e;

            } catch (IllegalArgumentException e) {

                con.rollback();

                throw new BookingException(
                        "Invalid booking date or time."
                );

            } catch (SQLException e) {

                con.rollback();

                throw new BookingException(
                        "Booking failed: " +
                        e.getMessage()
                );
            }

        } catch (SQLException e) {

            throw new BookingException(
                    "Database transaction failed: " +
                    e.getMessage()
            );
        }
    }

    // =============================================================
    // BASIC CHARGER EXISTENCE CHECK
    // =============================================================

    public boolean isChargerAvailable(
            int chargerId,
            int stationId)
            throws BookingException {

        String sql =
                "SELECT charger_id FROM chargers " +
                "WHERE charger_id = ? AND station_id = ?";

        try (Connection con =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, chargerId);
            ps.setInt(2, stationId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (!rs.next()) {

                    throw new BookingException(
                            "Charger not found at this station."
                    );
                }

                return true;
            }

        } catch (BookingException e) {

            throw e;

        } catch (SQLException e) {

            throw new BookingException(
                    "Unable to check charger availability: " +
                    e.getMessage()
            );
        }
    }

    // =============================================================
// SLOT AVAILABILITY
// =============================================================

public boolean isChargerAvailable(
        int chargerId,
        int stationId,
        String bookingDate,
        String startTime,
        int duration)
        throws BookingException {

    if (duration <= 0) {

        throw new BookingException(
                "Duration must be greater than zero."
        );
    }

    if (bookingDate == null ||
            bookingDate.trim().isEmpty()) {

        throw new BookingException(
                "Booking date cannot be empty."
        );
    }

    // Check that the charger exists.
    isChargerAvailable(
            chargerId,
            stationId
    );

    // Convert HH:mm -> HH:mm:ss safely.
    java.sql.Time sqlStartTime =
            toSqlTime(startTime);

    String sql =
            "SELECT booking_id FROM bookings " +
            "WHERE charger_id = ? " +
            "AND station_id = ? " +
            "AND booking_date = ? " +
            "AND status IN ('CONFIRMED','CHARGING') " +
            "AND start_time < ADDTIME(?, SEC_TO_TIME(duration * 60)) " +
            "AND ADDTIME(start_time, SEC_TO_TIME(duration * 60)) > ? " +
            "LIMIT 1";

    try (Connection con =
                 DBConnection.getConnection();
         PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setInt(
                1,
                chargerId
        );

        ps.setInt(
                2,
                stationId
        );

        ps.setDate(
                3,
                java.sql.Date.valueOf(
                        bookingDate
                )
        );

        ps.setTime(
                4,
                sqlStartTime
        );

        ps.setTime(
                5,
                sqlStartTime
        );

        try (ResultSet rs =
                     ps.executeQuery()) {

            // No overlapping booking = available
            return !rs.next();
        }

    } catch (IllegalArgumentException e) {

        throw new BookingException(
                "Invalid booking date or time."
        );

    } catch (SQLException e) {

        throw new BookingException(
                "Unable to check slot availability: " +
                e.getMessage()
        );
    }
}
    // =============================================================
    // BOOKING FEE
    // =============================================================

    public double getBookingFee() {

        return BOOKING_FEE;
    }

    // =============================================================
    // ELECTRICITY RATE
    // =============================================================

    public double getRatePerKWh() {

        return RATE_PER_KWH;
    }

    // =============================================================
    // CALCULATE ACTUAL CHARGING COST
    // =============================================================

    public double calculateChargingCost(
            double energyConsumed) {

        if (energyConsumed < 0) {

            throw new IllegalArgumentException(
                    "Energy consumed cannot be negative."
            );
        }

        return energyConsumed * RATE_PER_KWH;
    }

    // =============================================================
    // LEGACY RATE METHODS
    // =============================================================

    public double getRatePerMinute(
            double power) {

        if (Math.abs(power - 3.3) < 0.01) {

            return RATE_3_3_KW;
        }

        if (Math.abs(power - 7.5) < 0.01) {

            return RATE_7_5_KW;
        }

        if (Math.abs(power - 60.0) < 0.01) {

            return RATE_60_KW;
        }

        if (Math.abs(power - 120.0) < 0.01) {

            return RATE_120_KW;
        }

        return 0.0;
    }

    public double calculateCost(
            double power,
            int duration) {

        return getRatePerMinute(power)
                * duration;
    }

    public double calculateCost(
            int duration) {

        return 0.0;
    }

    public String getRateText(
            double power) {

        double rate =
                getRatePerMinute(power);

        if (rate <= 0) {

            return "Rate unavailable";
        }

        return String.format(
                "Rs. %.2f / minute",
                rate
        );
    }

    // =============================================================
    // GET BOOKING BY ID
    // =============================================================

    public Booking getBookingById(
            int bookingId)
            throws BookingException {

        String sql =
                "SELECT booking_id, user_id, station_id, " +
                "charger_id, booking_date, start_time, " +
                "duration, cost, booking_fee, " +
                "energy_consumed, charging_cost, status " +
                "FROM bookings " +
                "WHERE booking_id = ?";

        try (Connection con =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    bookingId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return createBookingFromResultSet(
                            rs
                    );
                }
            }

        } catch (SQLException e) {

            throw new BookingException(
                    "Unable to load booking: " +
                    e.getMessage()
            );
        }

        throw new BookingException(
                "Booking not found: " +
                bookingId
        );
    }

    // =============================================================
    // GET ALL BOOKINGS FOR USER
    // =============================================================

    public List<Booking> getBookingsByUser(
            int userId) {

        List<Booking> bookings =
                new ArrayList<>();

        String sql =
                "SELECT booking_id, user_id, station_id, " +
                "charger_id, booking_date, start_time, " +
                "duration, cost, booking_fee, " +
                "energy_consumed, charging_cost, status " +
                "FROM bookings " +
                "WHERE user_id = ? " +
                "ORDER BY booking_id DESC";

        try (Connection con =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    userId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    bookings.add(
                            createBookingFromResultSet(
                                    rs
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve bookings: " +
                    e.getMessage()
            );
        }

        return bookings;
    }

    // =============================================================
    // CREATE BOOKING OBJECT FROM DATABASE
    // =============================================================

    private Booking createBookingFromResultSet(
            ResultSet rs)
            throws SQLException {

        java.sql.Date sqlDate =
                rs.getDate("booking_date");

        java.sql.Time sqlTime =
                rs.getTime("start_time");

        String date =
                sqlDate != null
                        ? sqlDate.toString()
                        : "";

        String time =
                sqlTime != null
                        ? sqlTime.toString()
                        : "";

        return new Booking(

                rs.getInt("booking_id"),

                rs.getInt("user_id"),

                rs.getInt("station_id"),

                rs.getInt("charger_id"),

                date,

                time,

                rs.getInt("duration"),

                rs.getDouble("cost"),

                rs.getDouble("booking_fee"),

                rs.getDouble("energy_consumed"),

                rs.getDouble("charging_cost"),

                rs.getString("status")
        );
    }

    // =============================================================
    // START CHARGING
    // =============================================================

    public Booking startCharging(
            int bookingId)
            throws BookingException {

        String sql =
                "UPDATE bookings " +
                "SET status = 'CHARGING' " +
                "WHERE booking_id = ? " +
                "AND status = 'CONFIRMED'";

        try (Connection con =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    bookingId
            );

            int rows =
                    ps.executeUpdate();

            if (rows == 0) {

                throw new BookingException(
                        "Booking cannot be started. " +
                        "It may not be confirmed."
                );
            }

            return getBookingById(
                    bookingId
            );

        } catch (BookingException e) {

            throw e;

        } catch (SQLException e) {

            throw new BookingException(
                    "Unable to start charging: " +
                    e.getMessage()
            );
        }
    }

    // =============================================================
// COMPLETE CHARGING
// =============================================================

public Booking completeCharging(
        int bookingId,
        double energyConsumed)
        throws BookingException {

    if (energyConsumed <= 0) {
        throw new BookingException(
                "Energy consumed must be greater than zero."
        );
    }

    double chargingCost =
            calculateChargingCost(energyConsumed);

    final String bookingLockSql =
            "SELECT user_id, status " +
            "FROM bookings " +
            "WHERE booking_id = ? " +
            "FOR UPDATE";

    final String walletLockSql =
            "SELECT balance " +
            "FROM wallets " +
            "WHERE user_id = ? " +
            "FOR UPDATE";

    final String updateBookingSql =
            "UPDATE bookings " +
            "SET energy_consumed = ?, " +
            "charging_cost = ?, " +
            "status = 'COMPLETED' " +
            "WHERE booking_id = ? " +
            "AND status IN ('CONFIRMED','CHARGING')";

    final String walletDeductSql =
            "UPDATE wallets " +
            "SET balance = balance - ? " +
            "WHERE user_id = ? " +
            "AND balance >= ?";

    try (Connection con = DBConnection.getConnection()) {

        con.setAutoCommit(false);

        try {

            int userId;
            String status;

            // =================================================
            // 1. LOCK BOOKING
            // =================================================

            try (PreparedStatement ps =
                         con.prepareStatement(bookingLockSql)) {

                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {

                        throw new BookingException(
                                "Booking not found."
                        );
                    }

                    userId =
                            rs.getInt("user_id");

                    status =
                            rs.getString("status");
                }
            }

            // =================================================
            // 2. CHECK BOOKING STATUS
            // =================================================

            if (!"CONFIRMED".equalsIgnoreCase(status)
                    &&
                !"CHARGING".equalsIgnoreCase(status)) {

                throw new BookingException(
                        "Charging session cannot be completed " +
                        "because the booking status is " +
                        status + "."
                );
            }

            // =================================================
            // 3. LOCK WALLET
            // =================================================

            double balance;

            try (PreparedStatement ps =
                         con.prepareStatement(walletLockSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs =
                             ps.executeQuery()) {

                    if (!rs.next()) {

                        throw new BookingException(
                                "Wallet not found for this user."
                        );
                    }

                    balance =
                            rs.getDouble("balance");
                }
            }

            // =================================================
            // 4. CHECK WALLET BALANCE
            // =================================================

            if (balance < chargingCost) {

                throw new BookingException(
                        String.format(
                                "Insufficient wallet balance " +
                                "for charging. Required: ₹%.2f, " +
                                "Available: ₹%.2f. " +
                                "Add money to the wallet " +
                                "and try again.",
                                chargingCost,
                                balance
                        )
                );
            }

            // =================================================
            // 5. SAVE ENERGY + CHARGING COST
            // =================================================

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 updateBookingSql)) {

                ps.setDouble(
                        1,
                        energyConsumed
                );

                ps.setDouble(
                        2,
                        chargingCost
                );

                ps.setInt(
                        3,
                        bookingId
                );

                if (ps.executeUpdate() == 0) {

                    throw new BookingException(
                            "Charging session could not be completed."
                    );
                }
            }

            // =================================================
            // 6. DEDUCT ACTUAL ELECTRICITY COST
            // =================================================

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 walletDeductSql)) {

                ps.setDouble(
                        1,
                        chargingCost
                );

                ps.setInt(
                        2,
                        userId
                );

                ps.setDouble(
                        3,
                        chargingCost
                );

                if (ps.executeUpdate() == 0) {

                    throw new BookingException(
                            "Charging cost could not be " +
                            "deducted from wallet."
                    );
                }
            }

            // =================================================
            // 7. COMMIT
            // =================================================

            con.commit();

        } catch (BookingException e) {

            con.rollback();
            throw e;

        } catch (SQLException e) {

            con.rollback();

            throw new BookingException(
                    "Unable to complete charging: " +
                    e.getMessage()
            );
        }

    } catch (SQLException e) {

        throw new BookingException(
                "Charging transaction failed: " +
                e.getMessage()
        );
    }

    // Get the completed booking AFTER the transaction
    return getBookingById(bookingId);
}
        // =============================================================
        // CANCEL BOOKING
        // =============================================================

        public boolean cancelBooking(int bookingId) throws BookingException {

        String findSql =
                "SELECT user_id, booking_fee, status " +
                "FROM bookings " +
                "WHERE booking_id = ?";

        String cancelSql =
                "UPDATE bookings " +
                "SET status = 'CANCELLED' " +
                "WHERE booking_id = ? " +
                "AND status = 'CONFIRMED'";

        String refundSql =
                "UPDATE wallets " +
                "SET balance = balance + ? " +
                "WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection()) {

                con.setAutoCommit(false);

        try {

            int userId;
            double bookingFee;
            String status;

            // -------------------------------------------------
            // 1. FIND BOOKING
            // -------------------------------------------------

            try (PreparedStatement ps =
                         con.prepareStatement(findSql)) {

                ps.setInt(1, bookingId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new BookingException(
                                "Booking not found."
                        );
                    }

                    userId = rs.getInt("user_id");
                    bookingFee = rs.getDouble("booking_fee");
                    status = rs.getString("status");
                }
            }

            // -------------------------------------------------
            // 2. CHECK BOOKING STATUS
            // -------------------------------------------------

            if (!"CONFIRMED".equalsIgnoreCase(status)) {

                throw new BookingException(
                        "Only confirmed bookings can be cancelled."
                );
            }

            // -------------------------------------------------
            // 3. CANCEL BOOKING
            // -------------------------------------------------

            try (PreparedStatement ps =
                         con.prepareStatement(cancelSql)) {

                ps.setInt(1, bookingId);

                int rows = ps.executeUpdate();

                if (rows == 0) {

                    throw new BookingException(
                            "Booking could not be cancelled."
                    );
                }
            }

            // -------------------------------------------------
            // 4. REFUND BOOKING FEE
            // -------------------------------------------------

            if (bookingFee > 0) {

                try (PreparedStatement ps =
                             con.prepareStatement(refundSql)) {

                    ps.setDouble(1, bookingFee);
                    ps.setInt(2, userId);

                    if (ps.executeUpdate() == 0) {

                        throw new BookingException(
                                "Booking cancelled, but refund failed."
                        );
                    }
                }
            }

            // -------------------------------------------------
            // 5. COMMIT TRANSACTION
            // -------------------------------------------------

            con.commit();

            return true;

        } catch (BookingException e) {

            con.rollback();
            throw e;

        } catch (SQLException e) {

            con.rollback();

            throw new BookingException(
                    "Cancellation failed: " +
                    e.getMessage()
            );

        } finally {

            con.setAutoCommit(true);
        }

    } catch (SQLException e) {

        throw new BookingException(
                "Cancellation transaction failed: " +
                e.getMessage()
        );
    }
}
        // =============================================================
        // BOOKABLE INTERFACE
        // =============================================================

        @Override
        public void bookSlot() {

        System.out.println(
                "Book slot operation is handled by createBooking()."
        );
        }

        @Override
        public void cancelBooking() {

        System.out.println(
                "Cancel booking operation requires a booking ID."
        );
        }
}
