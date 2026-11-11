package com.evcharging.payment;

import com.evcharging.interfaces.Payment;

public class FastChargerPayment implements Payment {

    @Override
    public void makePayment(double amount) {
        System.out.println(
                "Payment of Rs. " + amount +
                " completed using Fast Charger Payment."
        );
    }

    @Override
    public String getPaymentMethod() {
        return "Fast Charger Payment";
    }
}