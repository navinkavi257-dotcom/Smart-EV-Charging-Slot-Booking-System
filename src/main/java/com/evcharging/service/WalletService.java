package com.evcharging.service;

import com.evcharging.model.Wallet;
import com.evcharging.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletService {

    // =========================================================
    // CREATE WALLET
    // =========================================================
    public void createWallet(int userId) {

        String sql = """
                INSERT INTO wallets (user_id, balance)
                VALUES (?, 0.00)
                ON DUPLICATE KEY UPDATE user_id = user_id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to create wallet: " + e.getMessage(), e
            );
        }
    }


    // =========================================================
    // GET WALLET
    // =========================================================
    public Wallet getWallet(int userId) {

        String sql = """
                SELECT wallet_id, user_id, balance
                FROM wallets
                WHERE user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Wallet(
                            rs.getInt("wallet_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("balance")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to load wallet: " + e.getMessage(), e
            );
        }

        return null;
    }


    // =========================================================
    // ADD MONEY
    // =========================================================
    public void addMoney(int userId, double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        String updateWalletSql = """
                UPDATE wallets
                SET balance = balance + ?
                WHERE user_id = ?
                """;

        String transactionSql = """
                INSERT INTO wallet_transactions
                (user_id, booking_id, transaction_type, amount, description)
                VALUES (?, NULL, 'CREDIT', ?, ?)
                """;

        try (Connection con = DBConnection.getConnection()) {

            // Start transaction
            con.setAutoCommit(false);

            try {

                // ---------------------------------------------
                // Add money to wallet
                // ---------------------------------------------
                try (PreparedStatement ps =
                             con.prepareStatement(updateWalletSql)) {

                    ps.setDouble(1, amount);
                    ps.setInt(2, userId);

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        throw new RuntimeException(
                                "Wallet not found for user."
                        );
                    }
                }


                // ---------------------------------------------
                // Record transaction
                // ---------------------------------------------
                try (PreparedStatement ps =
                             con.prepareStatement(transactionSql)) {

                    ps.setInt(1, userId);
                    ps.setDouble(2, amount);
                    ps.setString(
                            3,
                            String.format(
                                    "Wallet top-up of ₹%.2f",
                                    amount
                            )
                    );

                    ps.executeUpdate();
                }


                // Both operations succeeded
                con.commit();

            } catch (Exception e) {

                // Undo wallet update if transaction recording fails
                con.rollback();

                throw e;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to add money: " + e.getMessage(), e
            );

        } catch (RuntimeException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to add money: " + e.getMessage(), e
            );
        }
    }


    // =========================================================
    // CHECK BALANCE
    // =========================================================
    public boolean hasEnoughBalance(int userId, double amount) {

        Wallet wallet = getWallet(userId);

        if (wallet == null) {
            return false;
        }

        return wallet.getBalance() >= amount;
    }


    // =========================================================
    // DEDUCT MONEY
    // =========================================================
    public void deductMoney(int userId, double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        String sql = """
                UPDATE wallets
                SET balance = balance - ?
                WHERE user_id = ?
                AND balance >= ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.setDouble(3, amount);

            int rows = ps.executeUpdate();

            if (rows == 0) {

                throw new RuntimeException(
                        "Insufficient wallet balance."
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to deduct money: " + e.getMessage(), e
            );
        }
    }
}