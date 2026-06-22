package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.*;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.control.TextInputDialog;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the main battle screen.
 * Handles grid rendering, unit selection, movement and combat,
 * turn progression and delegates AI turns to EnemyService.
 */
public class BattleController {

    private static final int CELL_SIZE = 64;

    private final SceneManager    sceneManager;
    private final GameState       state;
    private final CombatService   combatService;
    private final MovementService movementService;
    private final TurnService     turnService;
    private final EnemyService    enemyService;
    private final SaveService     saveService;

    private Unit selectedUnit;
    private Set<Position> reachableCells  = new HashSet<>();
    private Set<Position> attackableCells = new HashSet<>();

    private GridPane mapGrid;
    private TextArea combatLog;
    private VBox     unitInfoPanel;
    private Label    turnLabel;

    public BattleController(SceneManager sceneManager, GameState state,
                            CombatService combatService, MovementService movementService,
                            TurnService turnService, EnemyService enemyService,
                            SaveService saveService) {
        this.sceneManager    = sceneManager;
        this.state           = state;
        this.combatService   = combatService;
        this.movementService = movementService;
        this.turnService     = turnService;
        this.enemyService    = enemyService;
        this.saveService     = saveService;
    }


    public BorderPane buildView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("battle-screen");
        root.setTop(buildTopBar());
        root.setCenter(buildMapGrid());
        root.setRight(buildSidePanel());
        root.setBottom(buildBottomBar());
        return root;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(10));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("top-bar");

        turnLabel = new Label(getTurnText());
        turnLabel.getStyleClass().add("turn-label");

        Button endTurnBtn = new Button("End Turn");
        endTurnBtn.getStyleClass().add("action-button");
        endTurnBtn.setOnAction(e -> onEndTurn());

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("action-button");
        saveBtn.setOnAction(e -> onSave());

        Button menuBtn = new Button("Menu");
        menuBtn.getStyleClass().add("action-button");
        menuBtn.setOnAction(e -> sceneManager.showMainMenu());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(turnLabel, spacer, endTurnBtn, saveBtn, menuBtn);
        return bar;
    }

    private GridPane buildMapGrid() {
        mapGrid = new GridPane();
        mapGrid.getStyleClass().add("map-grid");
        mapGrid.setPadding(new Insets(10));
        refreshGrid();
        return mapGrid;
    }

    private VBox buildSidePanel() {
        unitInfoPanel = new VBox(10);
        unitInfoPanel.setPadding(new Insets(10));
        unitInfoPanel.setPrefWidth(200);
        unitInfoPanel.getStyleClass().add("side-panel");
        refreshSidePanel();
        return unitInfoPanel;
    }

    private HBox buildBottomBar() {
        combatLog = new TextArea();
        combatLog.setEditable(false);
        combatLog.setPrefHeight(100);
        combatLog.getStyleClass().add("combat-log");
        combatLog.setWrapText(true);

        HBox bar = new HBox(combatLog);
        bar.setPadding(new Insets(5, 10, 10, 10));
        HBox.setHgrow(combatLog, Priority.ALWAYS);
        return bar;
    }


    private void refreshGrid() {
        mapGrid.getChildren().clear();
        BattleMap map = state.getBattleMap();
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                Position pos = new Position(r, c);
                mapGrid.add(buildCellPane(map.getCell(pos), pos), c, r);
            }
        }
    }

    private StackPane buildCellPane(Cell cell, Position pos) {
        StackPane pane = new StackPane();
        pane.setPrefSize(CELL_SIZE, CELL_SIZE);
        pane.getStyleClass().add("cell");

        pane.getChildren().add(loadTerrainTile(cell));
        applyCellHighlight(pane, pos);

        if (cell.isOccupied()) {
            pane.getChildren().add(buildUnitBox(cell.getOccupant()));
        }

        if (cell.isBreakableWall()) {
            Label wallLabel = new Label("BW\n" + cell.getWallHp());
            wallLabel.getStyleClass().add("wall-breakable");
            pane.getChildren().add(wallLabel);
        }

        pane.setOnMouseClicked(e -> onCellClicked(pos));
        return pane;
    }

    /**
     * Carica la tile dell'immagine corrispondente al tipo di terreno della cella.
     * Se l'immagine non esiste, applica il colore CSS di fallback.
     */
    private javafx.scene.Node loadTerrainTile(Cell cell) {
        String fileName = switch (cell.getTerrainType()) {
            case PLAIN          -> "plain.png";
            case FOREST         -> "forest1.png";
            case MOUNTAIN       -> "wall-fort.png";
            case FORT           -> "wall-fort-angle.png";
            case WALL           -> "wall-fort.png";
            case BREAKABLE_WALL -> "wall-fort-breakable.png";
        };

        var stream = getClass().getResourceAsStream(
                "/it/unicam/cs/mpgc/rpg123743/sprites/terrain/" + fileName
        );

        if (stream != null) {
            javafx.scene.image.Image img = new javafx.scene.image.Image(stream);
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);
            imageView.setFitWidth(CELL_SIZE);
            imageView.setFitHeight(CELL_SIZE);
            return imageView;
        }

        // Fallback
        StackPane fallback = new StackPane();
        fallback.getStyleClass().add("terrain-" + cell.getTerrainType().name().toLowerCase());
        fallback.setPrefSize(CELL_SIZE, CELL_SIZE);
        return fallback;
    }

    private void applyCellHighlight(StackPane pane, Position pos) {
        if (reachableCells.contains(pos)) {
            pane.getStyleClass().add("cell-reachable");
        } else if (attackableCells.contains(pos)) {
            pane.getStyleClass().add("cell-attackable");
        }
        if (selectedUnit != null && selectedUnit.getPosition().equals(pos)) {
            pane.getStyleClass().add("cell-selected");
        }
    }

    private VBox buildUnitBox(Unit unit) {
        javafx.scene.Node graphic = loadUnitSprite(unit);

        double hpPercent = (double) unit.getStats().getCurrentHp() / unit.getStats().getMaxHp();
        ProgressBar hpBar = new ProgressBar(hpPercent);
        hpBar.setPrefWidth(CELL_SIZE - 8);
        hpBar.setPrefHeight(6);
        hpBar.getStyleClass().add("hp-bar");
        hpBar.setStyle(getHpBarColor(hpPercent));

        VBox box = new VBox(2, graphic, hpBar);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Carica lo sprite PNG dell'unità dalla cartella resources/sprites.
     * Se lo sprite non esiste, usa una label con la lettera della classe.
     */
    private javafx.scene.Node loadUnitSprite(Unit unit) {
        String faction = unit.getFaction() == Faction.PLAYER ? "player" : "enemy";
        String fileName = switch (unit.getUnitClass()) {
            case WARRIOR -> "warrior-" + faction + ".png";
            case MAGE    -> "mage-" + faction + ".png";
            case ARCHER  -> "archer-" + faction + ".png";
            case KNIGHT  -> "knight-" + faction + ".png";
            case THIEF   -> "thief-" + faction + ".png";
        };

        var stream = getClass().getResourceAsStream(
                "/it/unicam/cs/mpgc/rpg123743/sprites/units/" + fileName
        );

        if (stream != null) {
            javafx.scene.image.Image img = new javafx.scene.image.Image(stream);
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);
            imageView.setFitWidth(CELL_SIZE - 16);
            imageView.setFitHeight(CELL_SIZE - 16);
            imageView.setPreserveRatio(true);
            if (unit.getFaction() == Faction.PLAYER) {
                imageView.setScaleX(-1);
            }
            return imageView;
        }

        // Fallback alla lettera se lo sprite non esiste
        Label label = new Label(getUnitSymbol(unit));
        label.getStyleClass().add(
                unit.getFaction() == Faction.PLAYER ? "unit-player" : "unit-enemy"
        );
        return label;
    }

    private String getHpBarColor(double hpPercent) {
        if (hpPercent > 0.5)  return "-fx-accent: #44ff44;";
        if (hpPercent > 0.25) return "-fx-accent: #ffaa00;";
        return "-fx-accent: #ff4444;";
    }


    private void onCellClicked(Position pos) {
        if (state.getCurrentPhase() != GameState.Phase.PLAYER_TURN) return;

        BattleMap map = state.getBattleMap();
        Cell cell     = map.getCell(pos);

        // Case 0: show enemy stats (unless we are about to attack)
        if (cell.isOccupied() && cell.getOccupant().getFaction() == Faction.ENEMY) {
            if (selectedUnit == null || !attackableCells.contains(pos)) {
                showUnitInfo(cell.getOccupant());
                refreshGrid();
                return;
            }
        }

        // Case 1: move selected unit to reachable empty cell
        if (selectedUnit != null && reachableCells.contains(pos) && !cell.isOccupied()) {
            map.moveUnit(selectedUnit, pos);
            selectedUnit.markAsMoved();
            reachableCells  = new HashSet<>();
            attackableCells = movementService.getAttackRange(selectedUnit, map);
            refreshGrid();
            return;
        }

        // Case 1b: attack breakable wall
        if (selectedUnit != null && attackableCells.contains(pos)
                && cell.isBreakableWall() && !selectedUnit.hasActedThisTurn()) {
            boolean destroyed = cell.damageWall(selectedUnit.getStats().getAttack());
            if (destroyed) {
                log(selectedUnit.getName() + " destroyed a wall at " + pos + "!");
            } else {
                log(selectedUnit.getName() + " attacked a wall at " + pos +
                        " - " + cell.getWallHp() + " HP remaining.");
            }
            selectedUnit.markAsActed();
            clearSelection();
            refreshGrid();
            return;
        }

        // Case 2: attack enemy unit
        if (selectedUnit != null && attackableCells.contains(pos)
                && cell.isOccupied() && cell.getOccupant().isEnemy(selectedUnit)
                && !selectedUnit.hasActedThisTurn()) {
            CombatResult result = combatService.resolve(selectedUnit, cell.getOccupant(), map);
            logCombat(result);
            turnService.removeDefeatedUnits(state);
            clearSelection();
            if (turnService.checkVictory(state) || turnService.checkDefeat(state)) {
                sceneManager.showGameOver(state);
                return;
            }
            refreshGrid();
            return;
        }

        // Case 3: select player unit
        if (cell.isOccupied()
                && cell.getOccupant().getFaction() == Faction.PLAYER
                && !cell.getOccupant().hasFinishedTurn()) {
            selectedUnit    = cell.getOccupant();
            reachableCells  = selectedUnit.hasMovedThisTurn()
                    ? new HashSet<>()
                    : movementService.getReachableCells(selectedUnit, map);
            attackableCells = movementService.getAttackRange(selectedUnit, map);
            showUnitInfo(selectedUnit);
            refreshGrid();
            return;
        }

        // Case 4: deselect
        clearSelection();
        refreshGrid();
    }

    private void onEndTurn() {
        clearSelection();
        turnService.endPlayerTurn(state);
        turnLabel.setText(getTurnText());
        log("--- Enemy Turn ---");
        refreshGrid();

        enemyService.executeTurn(state);
        turnService.removeDefeatedUnits(state);

        if (turnService.checkVictory(state) || turnService.checkDefeat(state)) {
            sceneManager.showGameOver(state);
            return;
        }

        turnService.endEnemyTurn(state);
        turnLabel.setText(getTurnText());
        log("--- Player Turn " + state.getTurnNumber() + " ---");
        refreshGrid();
    }

    private void onSave() {
        TextInputDialog dialog = new TextInputDialog("save1");
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your save");
        dialog.setContentText("Save name:");

        dialog.showAndWait().ifPresent(saveName -> {
            if (saveName == null || saveName.isBlank()) {
                log("Save cancelled — name cannot be empty.");
                return;
            }
            GameState namedState = new GameState(saveName, state.getBattleMap());
            saveService.save(namedState);
            log("Game saved as: " + saveName);
        });
    }


    private void showUnitInfo(Unit unit) {
        unitInfoPanel.getChildren().clear();
        Label header = new Label(unit.getName());
        header.getStyleClass().add("panel-header");
        Stats s = unit.getStats();
        unitInfoPanel.getChildren().addAll(
                header,
                new Label(unit.getFaction() == Faction.PLAYER ? "[Player]" : "[Enemy]"),
                new Label("Class:  " + unit.getUnitClass()),
                new Label("Level:  " + unit.getLevel()),
                new Label("HP:     " + s.getCurrentHp() + "/" + s.getMaxHp()),
                new Label("ATK:    " + s.getAttack()),
                new Label("DEF:    " + s.getDefence()),
                new Label("RES:    " + s.getResistance()),
                new Label("SPD:    " + s.getSpeed()),
                new Label("MOV:    " + s.getMovement()),
                new Label("Weapon: " + (unit.getEquippedWeapon() == null
                        ? "None" : unit.getEquippedWeapon().getName()))
        );
    }

    private void refreshSidePanel() {
        if (selectedUnit != null) {
            showUnitInfo(selectedUnit);
        } else {
            unitInfoPanel.getChildren().clear();
            Label header = new Label("Select a unit");
            header.getStyleClass().add("panel-header");
            unitInfoPanel.getChildren().add(header);
        }
    }


    private void clearSelection() {
        selectedUnit    = null;
        reachableCells  = new HashSet<>();
        attackableCells = new HashSet<>();
        refreshSidePanel();
    }

    private void logCombat(CombatResult result) {
        log(result.toString());
        if (result.isAttackerLevelledUp()) {
            log(result.getAttacker().getName() + " levelled up! Now Lv." +
                    result.getAttacker().getLevel());
        }
    }

    private void log(String message) {
        combatLog.appendText(message + "\n");
    }

    private String getTurnText() {
        return switch (state.getCurrentPhase()) {
            case PLAYER_TURN -> "Turn " + state.getTurnNumber() + "  -  Player";
            case ENEMY_TURN  -> "Turn " + state.getTurnNumber() + "  -  Enemy";
            case VICTORY     -> "Turn " + state.getTurnNumber() + "  -  Victory!";
            case DEFEAT      -> "Turn " + state.getTurnNumber() + "  -  Defeat";
        };
    }

    private String getUnitSymbol(Unit unit) {
        return switch (unit.getUnitClass()) {
            case WARRIOR -> "W";
            case MAGE    -> "M";
            case ARCHER  -> "A";
            case KNIGHT  -> "K";
            case THIEF   -> "T";
        };
    }
}