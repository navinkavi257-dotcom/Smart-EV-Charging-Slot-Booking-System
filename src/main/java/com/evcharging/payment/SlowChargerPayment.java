package com.evcharging.payment;

import com.evcharging.interfaces.Payment;

public class SlowChargerPayment implements Payment {

    @Override
    public void makePayment(double amount) {
        System.out.println(
                "Payment of Rs. " + amount +
                " completed using Slow Charger Payment."
        );
    }

    @Override
    public String getPaymentMethod() {
        return "Slow Charger Payment";
    }
}