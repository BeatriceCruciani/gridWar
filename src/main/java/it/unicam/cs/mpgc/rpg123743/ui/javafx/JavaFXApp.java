package it.unicam.cs.mpgc.rpg123743.ui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point GridWar JavaFX application.
 */
public class JavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GridWar");
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
