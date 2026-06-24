package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.BattleMap;
import it.unicam.cs.mpgc.rpg123743.model.Cell;
import it.unicam.cs.mpgc.rpg123743.model.CombatResult;
import it.unicam.cs.mpgc.rpg123743.model.Consumable;
import it.unicam.cs.mpgc.rpg123743.model.Faction;
import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.model.Position;
import it.unicam.cs.mpgc.rpg123743.model.Unit;
import it.unicam.cs.mpgc.rpg123743.service.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Controller principale di battaglia. Coordina i servizi di gioco
 * (movimento, combattimento, turni, salvataggio) e delega il rendering
 * della vista a {@link BattleUIManager}.
 *
 * <p>Gestisce il flusso di interazione del giocatore tramite click sulla griglia:
 * selezione di un'unità, spostamento, attacco, uso di consumabili e deselezione.
 * Non contiene regole di gioco proprie: ogni decisione (celle raggiungibili,
 * danno, vittoria) è delegata ai rispettivi service.</p>
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
     * Chiamato da {@code SceneManager} dopo il caricamento dell'FXML.
     *
     * @param sm  il gestore delle scene (non nullo).
     * @param st  lo stato di gioco corrente (non nullo).
     * @param cs  il service di combattimento (non nullo).
     * @param ms  il service di movimento (non nullo).
     * @param ts  il service di gestione turni (non nullo).
     * @param es  il service dell'AI nemica (non nullo).
     * @param ss  il service di salvataggio (non nullo).
     * @throws NullPointerException se uno dei parametri è nullo.
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
     * Ridisegna la griglia e l'etichetta del turno in base allo stato corrente.
     */
    private void refreshView() {
        uiManager.refreshGrid(state.getBattleMap(), reachableCells, attackableCells, selectedUnit, this::onCellClicked);
        uiManager.updateTurnLabel("Turn " + state.getTurnNumber() + " - " + state.getCurrentPhase());
    }

    /**
     * Gestisce il click su una cella della griglia, implementando il flusso
     * tipico di selezione/movimento/attacco di un gioco tattico a turni:
     * <ul>
     *     <li>se nessuna unità è selezionata, seleziona l'unità del giocatore
     *     cliccata (se presente, viva e con il turno non ancora concluso);</li>
     *     <li>se si clicca di nuovo sull'unità selezionata, la deseleziona;</li>
     *     <li>se si clicca su una cella raggiungibile, sposta l'unità;</li>
     *     <li>se si clicca su una cella attaccabile occupata da un nemico vivo,
     *     risolve il combattimento;</li>
     *     <li>in ogni altro caso, deseleziona.</li>
     * </ul>
     *
     * @param pos la posizione della cella cliccata.
     */
    private void onCellClicked(Position pos) {
        BattleMap map = state.getBattleMap();

        if (selectedUnit == null) {
            trySelectUnit(map, pos);
            refreshView();
            return;
        }

        if (pos.equals(selectedUnit.getPosition())) {
            deselect();
            refreshView();
            return;
        }

        if (attackableCells.contains(pos) && map.getCell(pos).isOccupied()) {
            tryAttack(map, pos);
            refreshView();
            return;
        }

        if (reachableCells.contains(pos)) {
            tryMove(map, pos);
            refreshView();
            return;
        }

        deselect();
        refreshView();
    }

    /**
     * Tenta di selezionare l'unità del giocatore presente nella posizione indicata,
     * a condizione che sia viva e non abbia già concluso il proprio turno.
     */
    private void trySelectUnit(BattleMap map, Position pos) {
        Cell cell = map.getCell(pos);
        if (!cell.isOccupied()) return;

        Unit unit = cell.getOccupant();
        if (unit.getFaction() != Faction.PLAYER || !unit.isAlive() || unit.hasFinishedTurn()) return;

        selectedUnit = unit;
        reachableCells = movementService.getReachableCells(unit, map);
        attackableCells = movementService.getAttackRange(unit, map);
        uiManager.showUnitInfo(unit, this::onUseItem);
    }

    /**
     * Gestisce l'utilizzo di un consumabile da parte dell'unità attualmente selezionata.
     * Applica l'effetto del consumabile e segna l'unità come avente compiuto la propria
     * azione per il turno (usare un oggetto conta come azione, allo stesso modo di un
     * attacco), poi aggiorna la vista.
     *
     * @param consumable il consumabile da utilizzare, già presente nell'inventario
     *                    dell'unità selezionata.
     */
    private void onUseItem(Consumable consumable) {
        if (selectedUnit == null || selectedUnit.hasActedThisTurn()) return;

        consumable.useOn(selectedUnit);
        uiManager.log(selectedUnit.getName() + " used " + consumable.getName() + ".");
        selectedUnit.markAsActed();

        if (selectedUnit.hasFinishedTurn()) {
            deselect();
        } else {
            attackableCells = Set.of();
        }
        refreshView();
    }

    /**
     * Sposta l'unità selezionata nella posizione indicata e ricalcola la
     * gittata d'attacco dalla nuova posizione, mantenendo l'unità selezionata
     * per permettere un attacco immediato dopo lo spostamento.
     */
    private void tryMove(BattleMap map, Position pos) {
        movementService.move(selectedUnit, pos, map);
        selectedUnit.markAsMoved();
        reachableCells = Set.of();
        attackableCells = selectedUnit.hasActedThisTurn()
                ? Set.of()
                : movementService.getAttackRange(selectedUnit, map);
        if (selectedUnit.hasFinishedTurn()) {
            deselect();
        }
    }

    /**
     * Risolve un attacco dell'unità selezionata contro l'unità presente nella
     * posizione indicata, registra l'esito nel log di combattimento e gestisce
     * l'eventuale conclusione della partita.
     */
    private void tryAttack(BattleMap map, Position pos) {
        Unit defender = map.getCell(pos).getOccupant();
        CombatResult result = combatService.resolve(selectedUnit, defender, map);
        uiManager.log(result.toString());

        deselect();
        checkEndConditions();
    }

    /**
     * Deseleziona l'unità corrente e azzera le celle evidenziate.
     */
    private void deselect() {
        selectedUnit = null;
        reachableCells = Set.of();
        attackableCells = Set.of();
    }

    /**
     * Verifica le condizioni di vittoria e sconfitta. Se la partita è terminata,
     * mostra la schermata di fine partita e restituisce {@code true}. Altrimenti,
     * rimuove dalla mappa le unità sconfitte nel turno corrente e restituisce
     * {@code false}. Il controllo precede sempre la rimozione, come richiesto
     * dal contratto di {@link TurnService#checkVictory(GameState)}.
     *
     * @return {@code true} se la partita è terminata.
     */
    private boolean checkEndConditions() {
        if (turnService.checkVictory(state) || turnService.checkDefeat(state)) {
            sceneManager.showGameOver(state);
            return true;
        }
        turnService.removeDefeatedUnits(state);
        return false;
    }

    /**
     * Termina il turno del giocatore, esegue il turno dell'AI nemica e verifica
     * le condizioni di fine partita.
     */
    @FXML
    private void onEndTurn() {
        deselect();
        turnService.endPlayerTurn(state);
        enemyService.executeTurn(state);
        turnService.endEnemyTurn(state);

        if (checkEndConditions()) return;
        refreshView();
    }

    /**
     * Salva lo stato di gioco corrente.
     */
    @FXML
    private void onSave() {
        saveService.save(state);
    }

    /**
     * Torna alla schermata del menu principale.
     */
    @FXML
    private void onMenuClicked() {
        sceneManager.showMainMenu();
    }
}