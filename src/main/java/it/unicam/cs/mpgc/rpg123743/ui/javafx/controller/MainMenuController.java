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
import it.unicam.cs.mpgc.rpg123743.model.MapLevel;
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

        Button newGameBtn  = createButton("New Game", this::onNewGame);
        Button loadGameBtn = createButton("Load Game", this::onLoadGame);
        Button quitBtn     = createButton("Quit", this::onQuit);

        root.getChildren().addAll(title, subtitle, newGameBtn, loadGameBtn, quitBtn);
        return root;
    }

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
            int index     = options.indexOf(choice);
            MapLevel level = levels.get(index);
            GameState state = GameStateFactory.createGame(level);
            sceneManager.showBattle(state);
        });
    }

    private void onLoadGame() {
        List<String> saves = saveService.listSaves();
        if (saves.isEmpty()) {
            showAlert("No Saves", "No saved games to load");
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

    private void onQuit() {
        javafx.application.Platform.exit();
    }


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