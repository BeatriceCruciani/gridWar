package it.unicam.cs.mpgc.rpg123743.ui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione JavaFX di GridWar.
 * Inizializza l'applicazione e delega il controllo a SceneManager.
 */
public class JavaFXApp extends Application {

    /**
     * Metodo principale di JavaFX — viene chiamato automaticamente all'avvio.
     * Configura la finestra principale e mostra il menu principale.
     *
     * @param primaryStage la finestra principale dell'applicazione.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GridWar");
        primaryStage.setResizable(false);

        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.showMainMenu();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}