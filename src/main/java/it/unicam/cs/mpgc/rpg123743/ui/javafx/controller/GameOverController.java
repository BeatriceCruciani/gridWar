package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la schermata di fine partita.
 * La struttura visiva è definita in game-over.fxml.
 * Mostra vittoria o sconfitta e permette di tornare al menu.
 */
public class GameOverController implements Initializable {

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
     * Inizializza il controller con i dati necessari.
     * Chiamato da SceneManager dopo il caricamento dell'FXML.
     *
     * @param sceneManager il gestore delle scene.
     * @param state        lo stato finale della partita.
     */
    public void init(SceneManager sceneManager, GameState state) {
        this.sceneManager = sceneManager;
        this.state        = state;
        updateView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // I componenti FXML sono pronti ma state non è ancora disponibile.
        // L'aggiornamento della UI avviene in init() dopo che state viene iniettato.
    }

    /**
     * Aggiorna la UI con il risultato della partita.
     */
    private void updateView() {
        boolean victory = state.getCurrentPhase() == GameState.Phase.VICTORY;
        resultText.setText(victory ? "VICTORY!" : "DEFEAT");
        resultText.getStyleClass().add(victory ? "victory-text" : "defeat-text");
        turnsLabel.setText("Turns completed: " + state.getTurnNumber());
    }

    @FXML
    private void onMenu() {
        sceneManager.showMainMenu();
    }
}