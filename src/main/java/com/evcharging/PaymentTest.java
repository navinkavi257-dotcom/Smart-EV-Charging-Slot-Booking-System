package com.evcharging;

import com.evcharging.interfaces.Payment;
import com.evcharging.payment.FastChargerPayment;
import com.evcharging.payment.SlowChargerPayment;

public class PaymentTest {

    public static void main(String[] args) {

        Payment fastPayment = new FastChargerPayment();
        Payment slowPayment = new SlowChargerPayment();

        System.out.println("===== PAYMENT TEST =====");

        System.out.println(
                "Method: " + fastPayment.getPaymentMethod()
        );
        fastPayment.makePayment(300.0);

        System.out.println();

        System.out.println(
                "Method: " + slowPayment.getPaymentMethod()
        );
        slowPayment.makePayment(200.0);
    }
}