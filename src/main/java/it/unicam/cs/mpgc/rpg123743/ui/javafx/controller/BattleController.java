package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.*;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.util.*;

/**
 * Controller principale di battaglia. Coordina i servizi di gioco
 * (movimento, combattimento, turni, salvataggio) e delega il rendering
 * della vista a {@link BattleUIManager}.
 */
public class BattleController {

    private SceneManager sceneManager;
    private GameState state;
    private CombatService combatService;
    private MovementService movementService;
    private TurnService turnService;
    private EnemyService enemyService;
    private SaveService saveService;

    private BattleUIManager uiManager;
    private Unit selectedUnit;
    private Set<Position> reachableCells = new HashSet<>();
    private Set<Position> attackableCells = new HashSet<>();

    @FXML private GridPane mapGrid;
    @FXML private TextArea combatLog;
    @FXML private VBox unitInfoPanel;
    @FXML private Label turnLabel;

    /**
     * Inizializza il controller con lo stato di gioco e i service necessari.
     */
    public void init(SceneManager sm, GameState st, CombatService cs, MovementService ms,
                     TurnService ts, EnemyService es, SaveService ss) {
        this.sceneManager = Objects.requireNonNull(sm, "SceneManager cannot be null.");
        this.state = Objects.requireNonNull(st, "GameState cannot be null.");
        this.combatService = Objects.requireNonNull(cs, "CombatService cannot be null.");
        this.movementService = Objects.requireNonNull(ms, "MovementService cannot be null.");
        this.turnService = Objects.requireNonNull(ts, "TurnService cannot be null.");
        this.enemyService = Objects.requireNonNull(es, "EnemyService cannot be null.");
        this.saveService = Objects.requireNonNull(ss, "SaveService cannot be null.");
        this.uiManager = new BattleUIManager(mapGrid, combatLog, unitInfoPanel, turnLabel);
        refreshView();
    }

    /**
     * Ridisegna la griglia e l'etichetta del turno.
     */
    private void refreshView() {
        uiManager.refreshGrid(state.getBattleMap(), reachableCells, attackableCells, selectedUnit, this::onCellClicked);
        uiManager.updateTurnLabel("Turn " + state.getTurnNumber() + " - " + state.getCurrentPhase());
    }

    /**
     * Aggiorna internamente i set di movimento e attacco per l'unità selezionata.
     */
    private void updateMovementData() {
        if (selectedUnit != null) {
            this.reachableCells = movementService.getReachableCells(selectedUnit, state.getBattleMap());
            this.attackableCells = movementService.getAttackRange(selectedUnit, state.getBattleMap());
        }
    }

    /**
     * Gestisce il click su una cella della griglia.
     */
    private void onCellClicked(Position pos) {
        BattleMap map = state.getBattleMap();
        if (selectedUnit == null) {
            trySelectUnit(map, pos);
            refreshView();
            return;
        }
        if (pos.equals(selectedUnit.getPosition())) { cancelOrWait(); refreshView(); return; }

        if (attackableCells.contains(pos) && hasActionTarget(map, pos)) {
            handleActionTarget(map, pos);
        } else if (reachableCells.contains(pos)) {
            tryMove(map, pos);
        } else {
            cancelOrWait();
        }
        refreshView();
    }

    /**
     * Restituisce {@code true} se la cella indicata contiene un bersaglio valido
     * per un'azione (un'unità da attaccare/curare, o un muro distruttibile da
     * colpire). Una cella puramente vuota nel raggio d'attacco geometrico non
     * costituisce un bersaglio valido: in quel caso, se la cella è anche
     * raggiungibile, il click deve essere trattato come un movimento.
     */
    private boolean hasActionTarget(BattleMap map, Position pos) {
        Cell cell = map.getCell(pos);
        return cell.isOccupied() || cell.isBreakableWall();
    }

    /**
     * Annulla la selezione corrente. Se l'unità si è già mossa in questo turno,
     * il click viene interpretato come una rinuncia implicita ad agire ulteriormente
     * (equivalente a premere "Wait"): il turno dell'unità viene concluso definitivamente,
     * impedendo che venga riselezionata per muoversi di nuovo. Se invece l'unità non
     * si è ancora mossa, la selezione viene semplicemente annullata senza conseguenze.
     */
    private void cancelOrWait() {
        if (selectedUnit != null && selectedUnit.hasMovedThisTurn()) {
            onWait();
        } else {
            deselect();
        }
    }

    /**
     * Smista l'azione da eseguire su una cella che contiene un bersaglio valido
     * (verificato a monte da {@link #hasActionTarget}): un'unità da attaccare
     * o curare, oppure un muro distruttibile da colpire.
     */
    private void handleActionTarget(BattleMap map, Position pos){
        Cell cell = map.getCell(pos);
        if (cell.isOccupied()){
            Unit target = cell.getOccupant();
            if (target.getFaction() == selectedUnit.getFaction()) tryHeal(target);
            else tryAttack(map, pos);
            return;
        }
        tryAttackWall(cell);
    }

    /**
     * Seleziona l'unità presente nella cella cliccata, se appartiene al giocatore,
     * è viva e non ha già concluso il proprio turno. Subito dopo la selezione il
     * bottone "Wait" non viene mostrato: l'unità deve prima muoversi (o decidere
     * di restare ferma cliccando di nuovo sulla propria cella) prima di poter
     * terminare volontariamente il turno.
     */
    private void trySelectUnit(BattleMap map, Position pos) {
        Cell cell = map.getCell(pos);
        if (!cell.isOccupied()) return;

        Unit unit = cell.getOccupant();
        uiManager.showUnitInfo(unit, null, null);

        if (unit.getFaction() != Faction.PLAYER || !unit.isAlive() || unit.hasFinishedTurn()) {
            return;
        }

        selectedUnit = unit;
        updateMovementData();
        uiManager.showUnitInfo(unit, this::onUseItem, null);
    }

    /**
     * Gestisce l'utilizzo di un consumabile da parte dell'unità selezionata.
     */
    private void onUseItem(Consumable consumable) {
        if (selectedUnit == null || selectedUnit.hasActedThisTurn()) return;
        consumable.useOn(selectedUnit);
        uiManager.log(selectedUnit.getName() + " used " + consumable.getName() + ".");
        selectedUnit.markAsActed();
        if (selectedUnit.hasFinishedTurn()) {
            deselect();
        } else {
            updateMovementData();
            uiManager.showUnitInfo(selectedUnit, this::onUseItem, null);
        }
        refreshView();
    }

    /**
     * Sposta l'unità selezionata nella posizione indicata. Se il turno non
     * è ancora concluso dopo il movimento, il pannello informativo viene
     * aggiornato mostrando anche il bottone "Wait", che permette di terminare
     * volontariamente il turno senza compiere un'ulteriore azione.
     */
    private void tryMove(BattleMap map, Position pos) {
        try {
            movementService.move(selectedUnit, pos, map);
        } catch (Exception ex) {
            return;
        }
        selectedUnit.markAsMoved();
        updateMovementData();
        // L'unità si è già mossa in questo turno: il movimento è bloccato,
        // anche se può ancora attaccare/curare/usare oggetti dalla nuova posizione.
        reachableCells = Set.of();
        if (selectedUnit.hasFinishedTurn()) {
            deselect();
        } else {
            uiManager.showUnitInfo(selectedUnit, this::onUseItem, this::onWait);
        }
    }

    /**
     * Termina volontariamente il turno dell'unità selezionata senza compiere
     * un'azione di attacco, cura o utilizzo oggetti.
     */
    private void onWait() {
        if (selectedUnit == null) return;
        uiManager.log(selectedUnit.getName() + " waits.");
        selectedUnit.markAsActed();
        deselect();
        refreshView();
    }

    /**
     * Risolve l'attacco dell'unità selezionata contro il difensore indicato.
     * Non chiama esplicitamente {@code markAsActed()}: {@link CombatService#resolve}
     * lo fa già internamente sull'attaccante, una sola volta.
     */
    private void tryAttack(BattleMap map, Position pos) {
        CombatResult result = combatService.resolve(selectedUnit, map.getCell(pos).getOccupant(), map);
        uiManager.log(result.toString());
        deselect();
        checkEndConditions();
    }

    private void tryHeal(Unit target) {
        selectedUnit.getEquippedWeapon().ifPresent(w -> {
            if (w.getWeaponType() == WeaponType.STAFF && target.isAlive()) {
                target.heal(w.getAttackBonus());
                w.use();
                uiManager.log(selectedUnit.getName() + " heals " + target.getName() + ".");
                selectedUnit.markAsActed();
                deselect();
            }
        });
    }

    private void tryAttackWall(Cell wallCell) {
        selectedUnit.getEquippedWeapon().ifPresent(w -> {
            int damage = selectedUnit.getStats().getAttack() + w.getAttackBonus();
            boolean destroyed = wallCell.damageWall(damage);
            w.use();
            uiManager.log(selectedUnit.getName() + " strikes the wall for " + damage + " damage.");
            selectedUnit.markAsActed();
            if (destroyed) updateMovementData();
            deselect();
        });
    }

    /**
     * Deseleziona l'unità corrente, azzera i set di movimento/attacco e
     * svuota il pannello informativo laterale.
     */
    private void deselect() {
        selectedUnit = null;
        reachableCells = Set.of();
        attackableCells = Set.of();
        uiManager.clearUnitInfo();
    }

    private boolean checkEndConditions() {
        if (turnService.checkVictory(state) || turnService.checkDefeat(state)) {
            sceneManager.showGameOver(state);
            return true;
        }
        turnService.removeDefeatedUnits(state);
        return false;
    }

    @FXML private void onEndTurn() {
        deselect();
        turnService.endPlayerTurn(state);
        enemyService.executeTurn(state);
        turnService.endEnemyTurn(state);
        if (checkEndConditions()) return;
        refreshView();
    }

    @FXML private void onSave() {
        TextInputDialog dialog = new TextInputDialog(state.getSaveName());
        dialog.setTitle("Save Game");
        dialog.setHeaderText(null);
        dialog.setContentText("Save name:");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                state.setSaveName(name.trim());
                saveService.save(state);
                uiManager.log("Game saved as \"" + name.trim() + "\".");
            }
        });
    }

    @FXML private void onMenuClicked() { sceneManager.showMainMenu(); }
}