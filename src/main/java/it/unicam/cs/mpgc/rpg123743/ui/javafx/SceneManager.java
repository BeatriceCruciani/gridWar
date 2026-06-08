package it.unicam.cs.mpgc.rpg123743.ui.javafx;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.repository.JsonGameRepository;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.BattleController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.GameOverController;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.controller.MainMenuController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * Gestisce le transizioni tra le schermate dell'applicazione.
 * Funge da hub centrale che istanzia i service e i controller,
 * e cambia la schermata principale tra menu, battaglia e game over.
 * Il cablaggio di tutti i service (dependency injection) avviene qui.
 */
public class SceneManager {

    private static final int WIDTH  = 900;
    private static final int HEIGHT = 700;

    private final Stage primaryStage;

    // Service — creati una volta sola e condivisi tra i controller
    private final WeaponTriangle  weaponTriangle;
    private final CombatService   combatService;
    private final MovementService movementService;
    private final TurnService     turnService;
    private final EnemyService    enemyService;
    private final SaveService     saveService;

    /**
     * Costruisce un nuovo SceneManager e inizializza tutti i service.
     *
     * @param primaryStage la finestra principale dell'applicazione.
     */
    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Cablaggio dei service
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
     * Mostra la schermata del menu principale.
     */
    public void showMainMenu() {
        MainMenuController controller = new MainMenuController(this, saveService);
        Scene scene = new Scene(controller.buildView(), WIDTH, HEIGHT);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    /**
     * Avvia una nuova partita con lo stato fornito e mostra la schermata di battaglia.
     *
     * @param state lo stato di gioco da usare.
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
     * Mostra la schermata di fine partita con lo stato finale del gioco.
     *
     * @param state lo stato di gioco finale.
     */
    public void showGameOver(GameState state) {
        GameOverController controller = new GameOverController(this, state);
        Scene scene = new Scene(controller.buildView(), WIDTH, HEIGHT);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    /**
     * Applica il foglio di stile CSS alla scena specificata.
     */
    private void applyStylesheet(Scene scene) {
        String css = getClass().getResource(
                "/it/unicam/cs/mpgc/rpg123743/style.css"
        ).toExternalForm();
        scene.getStylesheets().add(css);
    }
}