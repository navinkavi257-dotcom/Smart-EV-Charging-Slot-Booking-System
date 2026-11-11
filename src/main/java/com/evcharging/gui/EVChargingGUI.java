package com.evcharging.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class EVChargingGUI extends Application {

    @Override
    public void start(Stage stage) {

        SplashView splashView =
                new SplashView();

        splashView.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}