package com.evcharging.thread;

public class BookingProcessingThread extends Thread {

    private int bookingId;

    public BookingProcessingThread(int bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public void run() {

        System.out.println();
        System.out.println("=================================");
        System.out.println("BOOKING PROCESSING THREAD STARTED");
        System.out.println("Booking ID: " + bookingId);

        try {
            // Simulate booking processing
            Thread.sleep(1000);

            System.out.println("Checking booking details...");

            Thread.sleep(1000);

            System.out.println("Processing booking...");

            Thread.sleep(1000);

            System.out.println("Booking processing completed!");

        } catch (InterruptedException e) {

            System.out.println(
                    "Booking processing interrupted."
            );

            Thread.currentThread().interrupt();
        }

        System.out.println("BOOKING PROCESSING THREAD FINISHED");
        System.out.println("=================================");
    }
}