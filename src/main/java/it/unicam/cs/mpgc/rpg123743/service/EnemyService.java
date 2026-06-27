package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * AI semplice che controlla le unità nemiche durante il turno del nemico.
 * Strategia: ogni nemico si avvicina all'unità del giocatore più vicina
 * e attacca se è in gittata.
 */
public class EnemyService {

    private final MovementService movementService;
    private final CombatService   combatService;

    /**
     * Costruisce un nuovo EnemyService con i service specificati.
     */
    public EnemyService(MovementService movementService, CombatService combatService) {
        this.movementService = Objects.requireNonNull(movementService, "MovementService cannot be null.");
        this.combatService   = Objects.requireNonNull(combatService, "CombatService cannot be null.");
    }

    /**
     * Esegue il turno dell'AI per tutte le unità nemiche ancora in vita.
     */
    public void executeTurn(GameState state) {
        Objects.requireNonNull(state, "Game state cannot be null during enemy turn execution.");
        BattleMap map = state.getBattleMap();

        List<Unit> enemies = map.getUnitsByFaction(Faction.ENEMY)
                .stream()
                .filter(Unit::isAlive)
                .toList();

        for (Unit enemy : enemies) {
            executeUnitTurn(enemy, state);
        }
    }

    /**
     * Esegue il turno per una singola unità nemica.
     */
    private void executeUnitTurn(Unit enemy, GameState state) {
        BattleMap map      = state.getBattleMap();
        List<Unit> players = map.getUnitsByFaction(Faction.PLAYER)
                .stream()
                .filter(Unit::isAlive)
                .toList();

        // Se non ci sono più giocatori vivi, l'AI non ha nulla da fare
        if (players.isEmpty()) return;

        Unit target = findNearestUnit(enemy, players);

        // Si avvicina al bersaglio se non è già in gittata per attaccarlo
        if (!canAttack(enemy, target)) {
            moveToward(enemy, target, map);
        }

        // Attacca se (dopo l'eventuale movimento) si trova in gittata
        // canAttack verifica internamente anche la presenza dell'arma
        if (canAttack(enemy, target)) {
            combatService.resolve(enemy, target, map);
        }

        // Garantisce che l'unità consumi il suo turno anche se non ha potuto attaccare
        enemy.markAsMoved();
        enemy.markAsActed();
    }

    /**
     * Sposta l'unità nemica scegliendo la cella raggiungibile più vicina al bersaglio.
     */
    private void moveToward(Unit enemy, Unit target, BattleMap map) {
        Set<Position> reachable = movementService.getReachableCells(enemy, map);
        if (reachable.isEmpty()) return;

        Optional<Position> bestPosition = reachable.stream()
                .min((a, b) -> Integer.compare(
                        a.distanceTo(target.getPosition()),
                        b.distanceTo(target.getPosition())
                ));

        bestPosition.ifPresent(pos -> movementService.move(enemy, pos, map));
    }

    /**
     * Restituisce true se il nemico ha un'arma ed è in gittata per colpire il bersaglio.
     */
    private boolean canAttack(Unit enemy, Unit target) {
        Optional<Weapon> weapon = enemy.getEquippedWeapon();
        if (weapon.isEmpty()) return false;
        int distance = enemy.getPosition().distanceTo(target.getPosition());
        return distance <= weapon.get().getRange();
    }

    /**
     * Trova l'unità del giocatore numericamente più vicina al nemico (distanza di Manhattan/Chebyshev).
     */
    private Unit findNearestUnit(Unit enemy, List<Unit> players) {
        return players.stream()
                .min((a, b) -> Integer.compare(
                        enemy.getPosition().distanceTo(a.getPosition()),
                        enemy.getPosition().distanceTo(b.getPosition())
                ))
                .orElseThrow(); // Sicuro: protetto dal controllo players.isEmpty() a monte
    }
}