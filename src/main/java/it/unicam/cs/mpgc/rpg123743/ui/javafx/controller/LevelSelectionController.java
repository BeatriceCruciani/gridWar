package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.model.MapLevel;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import it.unicam.cs.mpgc.rpg123743.util.GameStateFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.Objects;

/**
 * Controller per la schermata di selezione del livello.
 * Mostra l'elenco delle mappe disponibili e una preview testuale
 * (nome, descrizione, dimensioni, numero di nemici) della mappa selezionata.
 * La struttura visiva è definita in level-selection.fxml.
 */
public class LevelSelectionController {

    @FXML
    private ListView<MapLevel> levelListView;

    @FXML
    private Label previewTitle;

    @FXML
    private Label previewDescription;

    @FXML
    private Label previewDetails;

    @FXML
    private Button startButton;

    private SceneManager sceneManager;

    /**
     * Costruttore senza argomenti richiesto da FXMLLoader.
     */
    public LevelSelectionController() {
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
        populateLevelList();
    }

    /**
     * Metodo invocato automaticamente da JavaFX dopo l'iniezione dei campi {@code @FXML},
     * prima che {@link #init(SceneManager)} sia chiamato. Configura il rendering delle
     * voci della lista e lo stato iniziale del pannello di anteprima.
     */
    @FXML
    private void initialize() {
        levelListView.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(MapLevel level, boolean empty) {
                super.updateItem(level, empty);
                setText(empty || level == null ? null : level.getDisplayName());
            }
        });

        levelListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldLevel, newLevel) -> updatePreview(newLevel));

        startButton.setDisable(true);
    }

    /**
     * Popola la lista dei livelli con tutte le costanti disponibili in {@link MapLevel}.
     * Aggiungere un nuovo livello all'enum lo rende automaticamente selezionabile qui,
     * senza alcuna modifica a questo controller (principio Open/Closed).
     */
    private void populateLevelList() {
        levelListView.getItems().setAll(MapLevel.values());
    }

    /**
     * Aggiorna il pannello di anteprima con i metadati del livello selezionato.
     * Se nessun livello è selezionato, il pannello viene svuotato e il bottone
     * di avvio disabilitato.
     *
     * @param level il livello attualmente selezionato, o {@code null} se nessuno.
     */
    private void updatePreview(MapLevel level) {
        if (level == null) {
            previewTitle.setText("");
            previewDescription.setText("");
            previewDetails.setText("");
            startButton.setDisable(true);
            return;
        }
        previewTitle.setText(level.getDisplayName());
        previewDescription.setText(level.getDescription());
        previewDetails.setText(String.format("Size: %d x %d   |   Enemies: %d",
                level.getWidth(), level.getHeight(), level.getEnemyCount()));
        startButton.setDisable(false);
    }

    /**
     * Gestisce l'avvio della partita sul livello attualmente selezionato.
     * Costruisce lo stato iniziale tramite {@link GameStateFactory} e delega
     * la transizione di scena a {@link SceneManager}.
     */
    @FXML
    private void onStart() {
        MapLevel selected = levelListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        GameState state = GameStateFactory.createGame(selected);
        sceneManager.showBattle(state);
    }

    /**
     * Torna alla schermata del menu principale.
     */
    @FXML
    private void onBack() {
        sceneManager.showMainMenu();
    }
}