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
    private Set<Position> reachableCells  = new HashSet<>();
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
        this.sceneManager    = Objects.requireNonNull(sm, "SceneManager cannot be null.");
        this.state           = Objects.requireNonNull(st, "GameState cannot be null.");
        this.combatService   = Objects.requireNonNull(cs, "CombatService cannot be null.");
        this.movementService = Objects.requireNonNull(ms, "MovementService cannot be null.");
        this.turnService     = Objects.requireNonNull(ts, "TurnService cannot be null.");
        this.enemyService    = Objects.requireNonNull(es, "EnemyService cannot be null.");
        this.saveService     = Objects.requireNonNull(ss, "SaveService cannot be null.");
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
     * Aggiorna i set di movimento e attacco per l'unità selezionata.
     */
    private void updateMovementData() {
        if (selectedUnit != null) {
            this.reachableCells  = movementService.getReachableCells(selectedUnit, state.getBattleMap());
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
        if (pos.equals(selectedUnit.getPosition())) {
            cancelOrWait();
            refreshView();
            return;
        }
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
     * per un'azione (un'unità da attaccare/curare, o un muro distruttibile da colpire).
     * Una cella puramente vuota nel raggio d'attacco geometrico non costituisce un
     * bersaglio valido: in quel caso, se la cella è anche raggiungibile, il click
     * viene trattato come un movimento.
     */
    private boolean hasActionTarget(BattleMap map, Position pos) {
        Cell cell = map.getCell(pos);
        return cell.isOccupied() || cell.isBreakableWall();
    }

    /**
     * Smista l'azione da eseguire su una cella che contiene un bersaglio valido
     * (verificato a monte da {@link #hasActionTarget}): un'unità da attaccare
     * o curare, oppure un muro distruttibile da colpire.
     */
    private void handleActionTarget(BattleMap map, Position pos) {
        Cell cell = map.getCell(pos);
        if (cell.isOccupied()) {
            Unit target = cell.getOccupant();
            if (target.getFaction() == selectedUnit.getFaction()) tryHeal(target);
            else tryAttack(map, pos);
            return;
        }
        tryAttackWall(cell);
    }

    /**
     * Seleziona l'unità presente nella cella cliccata, se appartiene al giocatore,
     * è viva e non ha già concluso il proprio turno.
     */
    private void trySelectUnit(BattleMap map, Position pos) {
        Cell cell = map.getCell(pos);
        if (!cell.isOccupied()) return;

        Unit unit = cell.getOccupant();
        uiManager.showUnitInfo(unit, null, null);

        if (unit.getFaction() != Faction.PLAYER || !unit.isAlive() || unit.hasFinishedTurn()) return;

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
     * è ancora concluso dopo il movimento, mostra il bottone "Wait".
     */
    private void tryMove(BattleMap map, Position pos) {
        try {
            movementService.move(selectedUnit, pos, map);
        } catch (Exception ex) {
            return;
        }
        selectedUnit.markAsMoved();
        updateMovementData();
        reachableCells = Set.of();
        if (selectedUnit.hasFinishedTurn()) {
            deselect();
        } else {
            uiManager.showUnitInfo(selectedUnit, this::onUseItem, this::onWait);
        }
    }

    /**
     * Termina volontariamente il turno dell'unità selezionata senza compiere
     * ulteriori azioni.
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
     * {@link CombatService#resolve} chiama internamente {@code markAsActed()}
     * sull'attaccante, quindi non è necessario farlo qui.
     */
    private void tryAttack(BattleMap map, Position pos) {
        CombatResult result = combatService.resolve(selectedUnit, map.getCell(pos).getOccupant(), map);
        uiManager.log(result.toString());
        deselect();
        checkEndConditions();
    }

    /**
     * Cura l'unità bersaglio con il bastone equipaggiato dall'unità selezionata.
     * Se l'unità non ha un'arma equipaggiata, o se l'arma non è di tipo
     * {@link WeaponType#STAFF}, l'azione fallisce con un messaggio nel log
     * anziché in silenzio.
     */
    private void tryHeal(Unit target) {
        Weapon staff = getEquippedWeaponOrLog(WeaponType.STAFF, "heal");
        if (staff == null) return;
        if (!target.isAlive()) {
            uiManager.log("Cannot heal a defeated unit.");
            return;
        }
        target.heal(staff.getAttackBonus());
        staff.use();
        uiManager.log(selectedUnit.getName() + " heals " + target.getName() + ".");
        selectedUnit.markAsActed();
        deselect();
    }

    /**
     * Colpisce un muro distruttibile con l'arma equipaggiata dall'unità selezionata.
     * Se l'unità non ha nessun'arma equipaggiata l'azione fallisce con un messaggio
     * nel log anziché in silenzio.
     */
    private void tryAttackWall(Cell wallCell) {
        Weapon weapon = getEquippedWeaponOrLog(null, "attack the wall");
        if (weapon == null) return;
        int damage = selectedUnit.getStats().getAttack() + weapon.getAttackBonus();
        boolean destroyed = wallCell.damageWall(damage);
        weapon.use();
        uiManager.log(selectedUnit.getName() + " strikes the wall for " + damage + " damage.");
        selectedUnit.markAsActed();
        if (destroyed) updateMovementData();
        deselect();
    }

    /**
     * Restituisce l'arma equipaggiata dall'unità selezionata se presente e,
     * quando {@code requiredType} non è {@code null}, se è del tipo richiesto.
     * In caso contrario logga un messaggio descrittivo e restituisce {@code null},
     * evitando il fallimento silenzioso che si aveva con {@code ifPresent}.
     *
     * @param requiredType il tipo d'arma richiesto, o {@code null} se qualsiasi arma è accettata.
     * @param actionName   il nome dell'azione tentata, usato nel messaggio di log.
     * @return l'arma equipaggiata valida, o {@code null} se il prerequisito non è soddisfatto.
     */
    private Weapon getEquippedWeaponOrLog(WeaponType requiredType, String actionName) {
        Optional<Weapon> equipped = selectedUnit.getEquippedWeapon();
        if (equipped.isEmpty()) {
            uiManager.log(selectedUnit.getName() + " has no weapon equipped and cannot " + actionName + ".");
            return null;
        }
        Weapon weapon = equipped.get();
        if (requiredType != null && weapon.getWeaponType() != requiredType) {
            uiManager.log(selectedUnit.getName() + " needs a " + requiredType + " to " + actionName + ".");
            return null;
        }
        return weapon;
    }

    /**
     * Annulla la selezione corrente. Se l'unità si è già mossa in questo turno,
     * il click viene interpretato come Wait implicito.
     */
    private void cancelOrWait() {
        if (selectedUnit != null && selectedUnit.hasMovedThisTurn()) {
            onWait();
        } else {
            deselect();
        }
    }

    /**
     * Deseleziona l'unità corrente, azzera i set di movimento/attacco e
     * svuota il pannello informativo laterale.
     */
    private void deselect() {
        selectedUnit      = null;
        reachableCells    = Set.of();
        attackableCells   = Set.of();
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

    @FXML
    private void onEndTurn() {
        deselect();
        turnService.endPlayerTurn(state);
        enemyService.executeTurn(state);
        turnService.endEnemyTurn(state);
        if (checkEndConditions()) return;
        refreshView();
    }

    @FXML
    private void onSave() {
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