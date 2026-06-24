package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import java.util.Objects;

/**
 * Controller per la schermata di fine partita.
 * La struttura visiva è definita in game-over.fxml.
 * Mostra vittoria o sconfitta e permette di tornare al menu.
 */
public class GameOverController {

    @FXML private Text resultText;
    @FXML private Label turnsLabel;
    @FXML private Button menuBtn;

    private SceneManager sceneManager;
    private GameState    state;

    /**
     * Costruttore senza argomenti richiesto da FXMLLoader.
     */
    public GameOverController() {}

    /**
     * Inizializza il controller con i dati necessari e aggiorna immediatamente la UI.
     * Chiamato da SceneManager dopo il caricamento dell'FXML.
     *
     * @param sceneManager il gestore delle scene (non nullo).
     * @param state        lo stato finale della partita (non nullo).
     * @throws NullPointerException se uno dei parametri è nullo.
     */
    public void init(SceneManager sceneManager, GameState state) {
        this.sceneManager = Objects.requireNonNull(sceneManager, "SceneManager cannot be null.");
        this.state        = Objects.requireNonNull(state, "GameState cannot be null.");
        updateView();
    }

    /**
     * Aggiorna la UI con il risultato della partita (vittoria o sconfitta)
     * e il numero di turni completati.
     */
    private void updateView() {
        boolean victory = state.getCurrentPhase() == GameState.Phase.VICTORY;
        resultText.setText(victory ? "VICTORY!" : "DEFEAT");
        resultText.getStyleClass().add(victory ? "victory-text" : "defeat-text");
        turnsLabel.setText("Turns completed: " + state.getTurnNumber());
    }

    /**
     * Torna alla schermata del menu principale.
     */
    @FXML
    private void onMenu() {
        sceneManager.showMainMenu();
    }
}