package com.evcharging.service;

import com.evcharging.exception.InvalidLoginException;
import com.evcharging.model.User;
import com.evcharging.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

// Register a new USER
public boolean registerUser(User user) {

    String sql =
            "INSERT INTO users " +
            "(name, email, password, phone, role) " +
            "VALUES (?, ?, ?, ?, 'USER')";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setString(4, user.getPhone());

        int rows = statement.executeUpdate();

        return rows > 0;

    } catch (Exception e) {

        System.out.println(
                "Registration error: "
                        + e.getMessage()
        );

        return false;
    }
}
    // Login an existing user
    public User loginUser(
            String email,
            String password)
            throws InvalidLoginException {

        String sql =
                "SELECT * FROM users " +
                "WHERE email = ? AND password = ?";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("phone"),
                            resultSet.getString("role")
                    );
                }
            }

            throw new InvalidLoginException(
                    "Invalid email or password."
            );

        } catch (InvalidLoginException e) {

            throw e;

        } catch (Exception e) {

            throw new InvalidLoginException(
                    "Database error: "
                            + e.getMessage()
            );
        }
    }


    // Get all registered users
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql =
                "SELECT user_id, name, email, password, phone, role " +
                "FROM users " +
                "ORDER BY user_id DESC";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                User user =
                        new User(
                                resultSet.getInt("user_id"),
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                resultSet.getString("phone"),
                                resultSet.getString("role")
                        );

                users.add(user);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve users: "
                            + e.getMessage()
            );
        }

        return users;
    }
            // Create a new ADMIN account
    public boolean createAdmin(User user) {

        String sql =
                "INSERT INTO users " +
                "(name, email, password, phone, role) " +
                "VALUES (?, ?, ?, ?, 'ADMIN')";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getPhone());

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            System.out.println(
                    "Admin creation error: "
                            + e.getMessage()
            );

            return false;
        }
    }
}