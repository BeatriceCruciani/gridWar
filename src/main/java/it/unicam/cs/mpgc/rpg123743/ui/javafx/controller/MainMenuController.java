package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.fxml.FXML;
import java.util.Objects;

/**
 * Controller per la schermata del menu principale.
 * La struttura visiva è definita in main-menu.fxml.
 * Fornisce le opzioni per iniziare una nuova partita, caricare o gestire
 * i salvataggi esistenti, oppure uscire dal gioco. La selezione del livello
 * e la gestione dei salvataggi avvengono in schermate dedicate
 * ({@link LevelSelectionController} e {@link SaveSelectionController}),
 * a cui questo controller si limita a delegare la navigazione.
 */
public class MainMenuController {

    private SceneManager sceneManager;

    /**
     * Costruttore senza argomenti richiesto da FXMLLoader.
     */
    public MainMenuController() {
        // Lasciato vuoto intenzionalmente per JavaFX
    }

    /**
     * Inizializza il controller con il gestore delle scene.
     * Chiamato da SceneManager dopo il caricamento dell'FXML.
     *
     * @param sceneManager il gestore delle scene (non nullo).
     * @throws NullPointerException se sceneManager è nullo.
     */
    public void init(SceneManager sceneManager) {
        this.sceneManager = Objects.requireNonNull(sceneManager, "SceneManager cannot be null.");
    }

    /**
     * Mostra la schermata di selezione del livello per iniziare una nuova partita.
     */
    @FXML
    private void onNewGame() {
        sceneManager.showLevelSelection();
    }

    /**
     * Mostra la schermata di gestione dei salvataggi, da cui è possibile
     * caricare o eliminare una partita salvata.
     */
    @FXML
    private void onLoadGame() {
        sceneManager.showSaveSelection();
    }

    /**
     * Gestisce la chiusura pulita dell'applicazione.
     */
    @FXML
    private void onQuit() {
        javafx.application.Platform.exit();
    }
}