package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AI semplice che controlla le unità nemiche durante il turno del nemico.
 * Strategia: ogni nemico si avvicina all'unità del giocatore più vicina
 * e attacca se è in gittata.
 * Questa implementazione è volutamente semplice e può essere sostituita
 * o estesa implementando strategie più sofisticate in release future.
 */
public class EnemyService {

    private final MovementService movementService;
    private final CombatService   combatService;

    /**
     * Costruisce un nuovo EnemyAiService con i service specificati.
     *
     * @param movementService il service per il calcolo del movimento.
     * @param combatService   il service per la risoluzione del combattimento.
     */
    public EnemyService(MovementService movementService, CombatService combatService) {
        this.movementService = movementService;
        this.combatService   = combatService;
    }

    /**
     * Esegue il turno dell'AI per tutte le unità nemiche ancora in vita.
     *
     * @param state lo stato di gioco corrente.
     */
    public void executeTurn(GameState state) {
        BattleMap map       = state.getBattleMap();
        List<Unit> enemies  = map.getUnitsByFaction(Faction.ENEMY)
                .stream()
                .filter(Unit::isAlive)
                .toList();

        for (Unit enemy : enemies) {
            executeUnitTurn(enemy, state);
        }
    }

    /**
     * Esegue il turno per una singola unità nemica.
     * L'unità si avvicina all'unità del giocatore più vicina, poi attacca se possibile.
     *
     * @param enemy il nemico di cui eseguire il turno.
     * @param state lo stato di gioco corrente.
     */
    private void executeUnitTurn(Unit enemy, GameState state) {
        BattleMap map      = state.getBattleMap();
        List<Unit> players = map.getUnitsByFaction(Faction.PLAYER)
                .stream()
                .filter(Unit::isAlive)
                .toList();

        if (players.isEmpty()) return;

        Unit target = findNearestUnit(enemy, players);

        // Si avvicina al bersaglio se non è già in gittata
        if (!canAttack(enemy, target)) {
            moveToward(enemy, target, map);
        }

        // Attacca se ora è in gittata
        if (canAttack(enemy, target) && enemy.getEquippedWeapon() != null) {
            combatService.resolve(enemy, target, map);
        }

        enemy.markAsMoved();
        enemy.markAsActed();
    }

    /**
     * Sposta l'unità nemica di un passo verso il bersaglio
     * scegliendo la cella raggiungibile più vicina al bersaglio.
     *
     * @param enemy  l'unità nemica da spostare.
     * @param target il bersaglio verso cui avvicinarsi.
     * @param map    la mappa di battaglia.
     */
    private void moveToward(Unit enemy, Unit target, BattleMap map) {
        Set<Position> reachable = movementService.getReachableCells(enemy, map);
        if (reachable.isEmpty()) return;

        Optional<Position> best = reachable.stream()
                .min((a, b) -> Integer.compare(
                        a.distanceTo(target.getPosition()),
                        b.distanceTo(target.getPosition())
                ));

        best.ifPresent(pos -> map.moveUnit(enemy, pos));
    }

    /**
     * Restituisce {@code true} se il nemico può attaccare il bersaglio
     * dalla sua posizione corrente.
     *
     * @param enemy  l'unità nemica.
     * @param target il bersaglio da attaccare.
     * @return {@code true} se il bersaglio è in gittata.
     */
    private boolean canAttack(Unit enemy, Unit target) {
        if (enemy.getEquippedWeapon() == null) return false;
        int distance = enemy.getPosition().distanceTo(target.getPosition());
        return distance <= enemy.getEquippedWeapon().getRange();
    }

    /**
     * Trova l'unità del giocatore più vicina al nemico dato.
     *
     * @param enemy   l'unità nemica di riferimento.
     * @param players la lista delle unità del giocatore ancora in vita.
     * @return l'unità del giocatore più vicina.
     */
    private Unit findNearestUnit(Unit enemy, List<Unit> players) {
        return players.stream()
                .min((a, b) -> Integer.compare(
                        enemy.getPosition().distanceTo(a.getPosition()),
                        enemy.getPosition().distanceTo(b.getPosition())
                ))
                .orElseThrow();
    }
}