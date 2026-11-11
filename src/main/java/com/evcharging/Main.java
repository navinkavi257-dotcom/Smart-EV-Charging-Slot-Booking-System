package com.evcharging;

import com.evcharging.gui.SplashView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        SplashView splash =
                new SplashView();

        splash.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}