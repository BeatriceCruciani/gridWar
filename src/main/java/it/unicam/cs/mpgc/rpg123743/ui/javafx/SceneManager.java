package it.unicam.cs.mpgc.rpg123743.ui.javafx;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.repository.JsonGameRepository;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.BattleController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.GameOverController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Gestisce le transizioni tra le schermate dell'applicazione.
 * Carica i file FXML per le schermate statiche (menu, game over)
 * e costruisce in Java la schermata dinamica di battaglia.
 * Il cablaggio di tutti i service avviene qui.
 */
public class SceneManager {

    private static final int WIDTH  = 900;
    private static final int HEIGHT = 700;

    private final Stage primaryStage;

    private final WeaponTriangle  weaponTriangle;
    private final CombatService   combatService;
    private final MovementService movementService;
    private final TurnService     turnService;
    private final EnemyService    enemyService;
    private final SaveService     saveService;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.weaponTriangle  = new WeaponTriangle();
        this.combatService   = new CombatService(weaponTriangle);
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
     * Mostra la schermata del menu principale caricando main-menu.fxml.
     */
    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123743/main-menu.fxml")
            );
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.init(this, saveService);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            applyStylesheet(scene);
            primaryStage.setScene(scene);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load main-menu.fxml", e);
        }
    }

    /**
     * Avvia una nuova partita con lo stato fornito e mostra la schermata di battaglia.
     * La battaglia viene costruita in Java perché la griglia è dinamica.
     */
    public void showBattle(GameState state) {
        BattleController controller = new BattleController(
                this, state,
                combatService, movementService, turnService, enemyService, saveService
        );
        Scene scene = new Scene(controller.buildView(), WIDTH, HEIGHT);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    /**
     * Mostra la schermata di fine partita caricando game-over.fxml.
     */
    public void showGameOver(GameState state) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123743/game-over.fxml")
            );
            Parent root = loader.load();

            GameOverController controller = loader.getController();
            controller.init(this, state);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            applyStylesheet(scene);
            primaryStage.setScene(scene);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load game-over.fxml", e);
        }
    }

    private void applyStylesheet(Scene scene) {
        String css = getClass().getResource(
                "/it/unicam/cs/mpgc/rpg123743/style.css"
        ).toExternalForm();
        scene.getStylesheets().add(css);
    }
}