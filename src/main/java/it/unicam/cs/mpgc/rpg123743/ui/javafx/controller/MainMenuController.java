package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.model.MapLevel;
import it.unicam.cs.mpgc.rpg123743.service.SaveService;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import it.unicam.cs.mpgc.rpg123743.util.GameStateFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;

import java.util.List;
import java.util.Optional;

/**
 * Controller per la schermata del menu principale.
 * La struttura visiva è definita in main-menu.fxml.
 * Fornisce le opzioni per iniziare una nuova partita,
 * caricare un salvataggio esistente o uscire dal gioco.
 */
public class MainMenuController {

    private SceneManager sceneManager;
    private SaveService  saveService;

    /**
     * Costruttore senza argomenti richiesto da FXMLLoader.
     */
    public MainMenuController() {}

    /**
     * Inizializza il controller con i service necessari.
     * Chiamato da SceneManager dopo il caricamento dell'FXML.
     *
     * @param sceneManager il gestore delle scene.
     * @param saveService  il service per i salvataggi.
     */
    public void init(SceneManager sceneManager, SaveService saveService) {
        this.sceneManager = sceneManager;
        this.saveService  = saveService;
    }

    @FXML
    private void onNewGame() {
        List<MapLevel> levels = List.of(MapLevel.values());
        List<String> options  = levels.stream()
                .map(l -> l.getDisplayName() + " - " + l.getDescription())
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("New Game");
        dialog.setHeaderText("Select a map");
        dialog.setContentText("Map:");

        dialog.showAndWait().ifPresent(choice -> {
            int index      = options.indexOf(choice);
            MapLevel level = levels.get(index);
            GameState state = GameStateFactory.createGame(level);
            sceneManager.showBattle(state);
        });
    }

    @FXML
    private void onLoadGame() {
        List<String> saves = saveService.listSaves();
        if (saves.isEmpty()) {
            showAlert("No Saves", "No saved games to load.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(saves.get(0), saves);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Select a save");
        dialog.setContentText("Save:");

        Optional<String> choice = dialog.showAndWait();
        choice.ifPresent(saveName ->
                saveService.load(saveName).ifPresentOrElse(
                        sceneManager::showBattle,
                        () -> showAlert("Error", "Cannot load save: " + saveName)
                )
        );
    }

    @FXML
    private void onQuit() {
        javafx.application.Platform.exit();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}