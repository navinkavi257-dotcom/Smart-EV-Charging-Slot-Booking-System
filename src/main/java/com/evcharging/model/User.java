package com.evcharging.model;

public class User {

    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;

    // Constructor used when reading user from database
    public User(
            int userId,
            String name,
            String email,
            String password,
            String phone,
            String role) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    // Keep the old 5-parameter constructor
    // for existing project code.
    public User(
            int userId,
            String name,
            String email,
            String password,
            String phone) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = "USER";
    }

    // Constructor used when registering a new user
    public User(
            String name,
            String email,
            String password,
            String phone) {

        this.userId = 0;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = "USER";
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public void displayUser() {

        System.out.println(
                "======================================"
        );

        System.out.println(
                "              USER DETAILS"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "User ID : " + userId
        );

        System.out.println(
                "Name    : " + name
        );

        System.out.println(
                "Email   : " + email
        );

        System.out.println(
                "Phone   : " + phone
        );

        System.out.println(
                "Role    : " + role
        );


        System.out.println(
                "======================================"
        );
    }
}

