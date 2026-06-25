package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.service.SaveService;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller per la schermata di gestione dei salvataggi.
 * Mostra l'elenco dei salvataggi disponibili e permette di caricarne uno
 * o di eliminarlo definitivamente. La struttura visiva è definita in
 * save-selection.fxml.
 */
public class SaveSelectionController {

    @FXML
    private ListView<String> saveListView;

    @FXML
    private Button loadButton;

    @FXML
    private Button deleteButton;

    private SceneManager sceneManager;
    private SaveService saveService;

    /**
     * Costruttore senza argomenti richiesto da FXMLLoader.
     */
    public SaveSelectionController() {
        // Lasciato vuoto intenzionalmente per JavaFX
    }

    /**
     * Inizializza il controller con il gestore delle scene e il service di salvataggio.
     * Chiamato da SceneManager dopo il caricamento dell'FXML.
     *
     * @param sceneManager il gestore delle scene (non nullo).
     * @param saveService  il service per i salvataggi (non nullo).
     * @throws NullPointerException se uno dei parametri è nullo.
     */
    public void init(SceneManager sceneManager, SaveService saveService) {
        this.sceneManager = Objects.requireNonNull(sceneManager, "SceneManager cannot be null.");
        this.saveService = Objects.requireNonNull(saveService, "SaveService cannot be null.");
        refreshList();
    }

    /**
     * Metodo invocato automaticamente da JavaFX dopo l'iniezione dei campi {@code @FXML}.
     * Configura lo stato iniziale dei bottoni e la reattività alla selezione.
     */
    @FXML
    private void initialize() {
        saveListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean hasSelection = newVal != null;
                    loadButton.setDisable(!hasSelection);
                    deleteButton.setDisable(!hasSelection);
                });
        loadButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    /**
     * Ricarica l'elenco dei salvataggi disponibili dal repository.
     */
    private void refreshList() {
        saveListView.getItems().setAll(saveService.listSaves());
    }

    /**
     * Carica il salvataggio selezionato e avvia la battaglia da quello stato.
     * Se il caricamento fallisce (file mancante o corrotto), mostra un avviso
     * e resta sulla schermata corrente.
     */
    @FXML
    private void onLoad() {
        String selected = saveListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Optional<GameState> loaded = saveService.load(selected);
        if (loaded.isPresent()) {
            sceneManager.showBattle(loaded.get());
        } else {
            showAlert("Error", "Cannot load save: " + selected);
        }
    }

    /**
     * Elimina definitivamente il salvataggio selezionato, previa conferma
     * dell'utente, e aggiorna l'elenco visualizzato.
     */
    @FXML
    private void onDelete() {
        String selected = saveListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Save");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete \"" + selected + "\"? This cannot be undone.");

        confirm.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
            saveService.delete(selected);
            refreshList();
        });
    }

    /**
     * Torna alla schermata del menu principale.
     */
    @FXML
    private void onBack() {
        sceneManager.showMainMenu();
    }

    /**
     * Mostra un alert informativo a schermo.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}