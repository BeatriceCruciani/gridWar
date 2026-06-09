package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.*;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller per la schermata principale di battaglia.
 * Gestisce il rendering della griglia, la selezione delle unità,
 * il movimento e il combattimento, l'avanzamento dei turni
 * e delega i turni nemici a EnemyService.
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

    // Stato della UI
    private Unit selectedUnit;
    private Set<Position> reachableCells  = new HashSet<>();
    private Set<Position> attackableCells = new HashSet<>();

    // Nodi UI
    private GridPane mapGrid;
    private TextArea combatLog;
    private VBox     unitInfoPanel;
    private Label    turnLabel;

    /**
     * Costruisce un nuovo BattleController con tutti i service necessari.
     */
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

    /**
     * Costruisce e restituisce la vista completa della battaglia.
     *
     * @return il nodo radice della schermata di battaglia.
     */
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
                Position pos  = new Position(r, c);
                Cell cell     = map.getCell(pos);
                StackPane pane = buildCellPane(cell, pos);
                mapGrid.add(pane, c, r);
            }
        }
    }

    private StackPane buildCellPane(Cell cell, Position pos) {
        StackPane pane = new StackPane();
        pane.setPrefSize(CELL_SIZE, CELL_SIZE);
        pane.getStyleClass().add("cell");
        pane.getStyleClass().add("terrain-" + cell.getTerrainType().name().toLowerCase());

        if (reachableCells.contains(pos)) {
            pane.getStyleClass().add("cell-reachable");
        } else if (attackableCells.contains(pos)) {
            pane.getStyleClass().add("cell-attackable");
        }
        if (selectedUnit != null && selectedUnit.getPosition().equals(pos))
            pane.getStyleClass().add("cell-selected");

        if (cell.isOccupied()) {
            Unit unit  = cell.getOccupant();
            Label label = new Label(getUnitSymbol(unit));
            label.getStyleClass().add(
                    unit.getFaction() == Faction.PLAYER ? "unit-player" : "unit-enemy"
            );
            pane.getChildren().add(label);
        }

        pane.setOnMouseClicked(e -> onCellClicked(pos));
        return pane;
    }


    private void onCellClicked(Position pos) {
        if (state.getCurrentPhase() != GameState.Phase.PLAYER_TURN) return;

        BattleMap map = state.getBattleMap();
        Cell cell     = map.getCell(pos);

        // Caso 0: mostra le stat di qualsiasi unità cliccata
        if (cell.isOccupied()) {
            Unit clicked = cell.getOccupant();
            if (clicked.getFaction() == Faction.ENEMY) {
                if (selectedUnit != null && attackableCells.contains(pos)) {
                } else {
                    showUnitInfo(clicked);
                    refreshGrid();
                    return;
                }
            }
        }

        // Caso 1: cella raggiungibile vuota → sposta l'unità selezionata
        if (selectedUnit != null && reachableCells.contains(pos) && !cell.isOccupied()) {
            map.moveUnit(selectedUnit, pos);
            selectedUnit.markAsMoved();
            reachableCells  = new HashSet<>();
            attackableCells = movementService.getAttackRange(selectedUnit, map);
            refreshGrid();
            return;
        }

        // Caso 2: cella attaccabile con nemico → attacca
        if (selectedUnit != null && attackableCells.contains(pos)
                && cell.isOccupied() && cell.getOccupant().isEnemy(selectedUnit)) {
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

        // Caso 3: cella con unità del giocatore → seleziona
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

        // Caso 4: click altrove → deseleziona
        clearSelection();
        refreshGrid();
    }

    private void showUnitInfo(Unit unit) {
        unitInfoPanel.getChildren().clear();
        Label header = new Label(unit.getName());
        header.getStyleClass().add("panel-header");

        String faction = unit.getFaction() == Faction.PLAYER ? "[Player]" : "[Enemy]";
        Stats s = unit.getStats();

        unitInfoPanel.getChildren().addAll(
                header,
                new Label(faction),
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

    private void onEndTurn() {
        clearSelection();
        turnService.endPlayerTurn(state);
        turnLabel.setText(getTurnText());
        log("Enemy Turn");
        refreshGrid();

        enemyService.executeTurn(state);
        turnService.removeDefeatedUnits(state);

        if (turnService.checkVictory(state) || turnService.checkDefeat(state)) {
            sceneManager.showGameOver(state);
            return;
        }

        turnService.endEnemyTurn(state);
        turnLabel.setText(getTurnText());
        log("Player Turn " + state.getTurnNumber());
        refreshGrid();
    }

    private void onSave() {
        saveService.save(state);
        log("Game Saved");
    }


    private void clearSelection() {
        selectedUnit    = null;
        reachableCells  = new HashSet<>();
        attackableCells = new HashSet<>();
        refreshSidePanel();
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

    private void logCombat(CombatResult result) {
        log(result.toString());
        if (result.isAttackerLevelledUp()) {
            log(result.getAttacker().getName() + " ha guadagnato un livello! Ora Lv." +
                    result.getAttacker().getLevel());
        }
    }

    private void log(String message) {
        combatLog.appendText(message + "\n");
    }

    private String getTurnText() {
        return switch (state.getCurrentPhase()) {
            case PLAYER_TURN -> "Turn " + state.getTurnNumber() + "  —  Player";
            case ENEMY_TURN  -> "Turn " + state.getTurnNumber() + "  —  Enemy";
            case VICTORY     -> "Turn " + state.getTurnNumber() + "  —  Victory";
            case DEFEAT      -> "Turn " + state.getTurnNumber() + "  —  Defeat";
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
