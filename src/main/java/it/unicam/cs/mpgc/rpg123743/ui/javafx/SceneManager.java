package it.unicam.cs.mpgc.rpg123743.ui.javafx;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.repository.JsonGameRepository;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.BattleController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.GameOverController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.LevelSelectionController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.MainMenuController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.SaveSelectionController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Gestisce le transizioni tra le schermate dell'applicazione GridWar.
 * Centralizza l'inizializzazione di tutti i servizi di business (Composition Root)
 * e inietta le dipendenze necessarie nei rispettivi controller grafici.
 */
public class SceneManager {

    private static final int WIDTH  = 900;
    private static final int HEIGHT = 700;

    private final Stage primaryStage;

    // Servizi centralizzati dell'applicazione
    private final CombatService   combatService;
    private final MovementService movementService;
    private final TurnService     turnService;
    private final EnemyService    enemyService;
    private final SaveService     saveService;

    /**
     * Costruisce lo SceneManager e inizializza l'intero stack dei servizi.
     *
     * @param primaryStage la finestra principale dell'applicazione.
     */
    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inizializzazione pulita dei servizi di business
        this.combatService   = new CombatService();
        this.movementService = new MovementService();
        this.turnService     = new TurnService();
        this.enemyService    = new EnemyService(movementService, combatService);
        this.saveService     = new SaveService(
                new JsonGameRepository(
                        Path.of(System.getProperty("user.home"), ".gridwar", "saves")
                )
        );
    }

    /**
     * Mostra la schermata del menu principale.
     */
    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/rpg123743/main-menu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.init(this);

            replaceSceneContent(root, "GridWar — Main Menu");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main-menu.fxml", e);
        }
    }

    /**
     * Mostra la schermata di selezione del livello per iniziare una nuova partita.
     */
    public void showLevelSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/rpg123743/level-selection.fxml"));
            Parent root = loader.load();

            LevelSelectionController controller = loader.getController();
            controller.init(this);

            replaceSceneContent(root, "GridWar — Select Level");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load level-selection.fxml", e);
        }
    }

    /**
     * Mostra la schermata di gestione dei salvataggi, da cui è possibile
     * caricare o eliminare una partita salvata.
     */
    public void showSaveSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/rpg123743/save-selection.fxml"));
            Parent root = loader.load();

            SaveSelectionController controller = loader.getController();
            controller.init(this, saveService);

            replaceSceneContent(root, "GridWar — Your Saves");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load save-selection.fxml", e);
        }
    }

    /**
     * Mostra la schermata di battaglia iniettando lo stato e tutti i servizi nel controller FXML.
     *
     * @param state lo stato di gioco da visualizzare e gestire nella schermata di battaglia.
     */
    public void showBattle(GameState state) {
        String fxmlPath = "/it/unicam/cs/mpgc/rpg123743/battle-view.fxml";
        var resource = getClass().getResource(fxmlPath);

        if (resource == null) {
            throw new RuntimeException("ERRORE CRITICO: Non trovo il file FXML in: " + fxmlPath +
                    ". Controlla che sia in src/main/resources/it/unicam/cs/mpgc/rpg123743/");
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            BattleController controller = loader.getController();
            controller.init(this, state, combatService, movementService, turnService, enemyService, saveService);

            replaceSceneContent(root, "GridWar — Battle Zone");
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il caricamento di battle-view.fxml", e);
        }
    }

    /**
     * Mostra la schermata di fine partita.
     *
     * @param state lo stato finale della partita (vittoria o sconfitta).
     */
    public void showGameOver(GameState state) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/rpg123743/game-over.fxml"));
            Parent root = loader.load();

            GameOverController controller = loader.getController();
            controller.init(this, state);

            replaceSceneContent(root, "GridWar — Game Over");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load game-over.fxml", e);
        }
    }

    /**
     * Utility interna per cambiare il contenuto della scena senza ricreare lo stage,
     * applicando il foglio di stile globale.
     */
    private void replaceSceneContent(Parent root, String title) {
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        applyStylesheet(scene);
        primaryStage.setTitle(title);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void applyStylesheet(Scene scene) {
        var resource = getClass().getResource("/it/unicam/cs/mpgc/rpg123743/style.css");
        if (resource != null) {
            String css = resource.toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
        }
    }
}