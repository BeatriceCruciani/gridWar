package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.service.SaveService;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import it.unicam.cs.mpgc.rpg123743.util.GameStateFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Controller per la schermata del menu principale.
 * Fornisce le opzioni per iniziare una nuova partita,
 * caricare un salvataggio esistente o uscire dal gioco.
 */
public class MainMenuController {

    private final SceneManager sceneManager;
    private final SaveService  saveService;

    /**
     * Costruisce un nuovo MainMenuController.
     *
     * @param sceneManager il gestore delle scene per la navigazione.
     * @param saveService  il service per il caricamento dei salvataggi.
     */
    public MainMenuController(SceneManager sceneManager, SaveService saveService) {
        this.sceneManager = sceneManager;
        this.saveService  = saveService;
    }

    /**
     * Costruisce e restituisce la vista del menu principale.
     *
     * @return il nodo radice della schermata del menu principale.
     */
    public VBox buildView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("main-menu");

        Text title    = new Text("GRIDWAR");
        title.getStyleClass().add("title");

        Text subtitle = new Text("A Tactical RPG");
        subtitle.getStyleClass().add("subtitle");

        Button newGameBtn  = createButton("Nuova Partita", this::onNewGame);
        Button loadGameBtn = createButton("Carica Partita", this::onLoadGame);
        Button quitBtn     = createButton("Esci", this::onQuit);

        root.getChildren().addAll(title, subtitle, newGameBtn, loadGameBtn, quitBtn);
        return root;
    }

    // --- Gestori degli eventi ---

    private void onNewGame() {
        GameState state = GameStateFactory.createDefaultGame();
        sceneManager.showBattle(state);
    }

    private void onLoadGame() {
        List<String> saves = saveService.listSaves();
        if (saves.isEmpty()) {
            showAlert("Nessun salvataggio", "Non ci sono partite salvate da caricare.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(saves.get(0), saves);
        dialog.setTitle("Carica Partita");
        dialog.setHeaderText("Seleziona un salvataggio");
        dialog.setContentText("Salvataggio:");

        Optional<String> choice = dialog.showAndWait();
        choice.ifPresent(saveName ->
                saveService.load(saveName).ifPresentOrElse(
                        sceneManager::showBattle,
                        () -> showAlert("Errore", "Impossibile caricare: " + saveName)
                )
        );
    }

    private void onQuit() {
        javafx.application.Platform.exit();
    }

    // --- Metodi di supporto ---

    private Button createButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("menu-button");
        btn.setOnAction(e -> action.run());
        btn.setPrefWidth(200);
        return btn;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}