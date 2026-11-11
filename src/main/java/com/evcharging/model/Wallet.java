package com.evcharging.model;

public class Wallet {

    private int walletId;
    private int userId;
    private double balance;

    public Wallet(int walletId, int userId, double balance) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
    }

    public int getWalletId() {
        return walletId;
    }

    public int getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}