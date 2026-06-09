package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller per la schermata di fine partita.
 * Mostra un messaggio di vittoria o sconfitta e permette
 * al giocatore di tornare al menu principale.
 */
public class GameOverController {

    private final SceneManager sceneManager;
    private final GameState    state;

    /**
     * Costruisce un nuovo GameOverController.
     *
     * @param sceneManager il gestore delle scene per la navigazione.
     * @param state        lo stato finale della partita.
     */
    public GameOverController(SceneManager sceneManager, GameState state) {
        this.sceneManager = sceneManager;
        this.state        = state;
    }

    /**
     * Costruisce e restituisce la vista della schermata di fine partita.
     *
     * @return il nodo radice della schermata di fine partita.
     */
    public VBox buildView() {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.getStyleClass().add("game-over-screen");

        boolean victory = state.getCurrentPhase() == GameState.Phase.VICTORY;

        Text result = new Text(victory ? "VICTORY" : "DEFEAT");
        result.getStyleClass().add(victory ? "victory-text" : "defeat-text");

        Text turns = new Text("Turs: " + state.getTurnNumber());
        turns.getStyleClass().add("subtitle");

        Button menuBtn = new Button("Main Menu");
        menuBtn.getStyleClass().add("menu-button");
        menuBtn.setOnAction(e -> sceneManager.showMainMenu());

        root.getChildren().addAll(result, turns, menuBtn);
        return root;
    }
}