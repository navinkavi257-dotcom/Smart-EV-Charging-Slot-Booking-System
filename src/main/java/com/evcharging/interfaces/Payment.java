package com.evcharging.interfaces;

public interface Payment {

    void makePayment(double amount);

    String getPaymentMethod();
}