package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;

import java.util.List;

/**
 * Gestisce l'avanzamento dei turni e verifica le condizioni di vittoria e sconfitta.
 * Il turno del giocatore termina quando tutte le unità del giocatore hanno completato
 * le loro azioni. La vittoria si ottiene sconfiggendo tutte le unità nemiche.
 * La sconfitta avviene quando tutte le unità del giocatore vengono eliminate.
 */
public class TurnService {

    /**
     * Termina il turno del giocatore e inizia il turno del nemico.
     * Azzera lo stato del turno di tutte le unità del giocatore.
     *
     * @param state lo stato di gioco corrente.
     */
    public void endPlayerTurn(GameState state) {
        resetFactionUnits(state.getBattleMap(), Faction.PLAYER);
        state.advancePhase();
    }

    /**
     * Termina il turno del nemico e inizia il turno successivo del giocatore.
     * Azzera lo stato del turno di tutte le unità nemiche.
     *
     * @param state lo stato di gioco corrente.
     */
    public void endEnemyTurn(GameState state) {
        resetFactionUnits(state.getBattleMap(), Faction.ENEMY);
        state.advancePhase();
    }

    /**
     * Verifica se la condizione di vittoria è soddisfatta: tutte le unità nemiche sconfitte.
     *
     * @param state lo stato di gioco corrente.
     * @return {@code true} se tutti i nemici sono stati sconfitti.
     */
    public boolean checkVictory(GameState state) {
        List<Unit> enemies = state.getBattleMap().getUnitsByFaction(Faction.ENEMY);
        boolean victory    = enemies.stream().noneMatch(Unit::isAlive);
        if (victory) state.setPhase(GameState.Phase.VICTORY);
        return victory;
    }

    /**
     * Verifica se la condizione di sconfitta è soddisfatta: tutte le unità del giocatore sconfitte.
     *
     * @param state lo stato di gioco corrente.
     * @return {@code true} se tutte le unità del giocatore sono state sconfitte.
     */
    public boolean checkDefeat(GameState state) {
        List<Unit> players = state.getBattleMap().getUnitsByFaction(Faction.PLAYER);
        boolean defeat     = players.stream().noneMatch(Unit::isAlive);
        if (defeat) state.setPhase(GameState.Phase.DEFEAT);
        return defeat;
    }

    /**
     * Restituisce {@code true} se tutte le unità del giocatore ancora in vita
     * hanno completato il loro turno.
     *
     * @param state lo stato di gioco corrente.
     * @return {@code true} se tutte le unità hanno agito.
     */
    public boolean allPlayerUnitsFinished(GameState state) {
        return state.getBattleMap().getUnitsByFaction(Faction.PLAYER)
                .stream()
                .filter(Unit::isAlive)
                .allMatch(Unit::hasFinishedTurn);
    }

    /**
     * Rimuove dalla mappa tutte le unità che sono state sconfitte.
     *
     * @param state lo stato di gioco corrente.
     */
    public void removeDefeatedUnits(GameState state) {
        BattleMap map = state.getBattleMap();
        for (Faction faction : Faction.values()) {
            map.getUnitsByFaction(faction).stream()
                    .filter(u -> !u.isAlive())
                    .forEach(map::removeUnit);
        }
    }

    /**
     * Azzera lo stato del turno di tutte le unità della fazione specificata.
     */
    private void resetFactionUnits(BattleMap map, Faction faction) {
        map.getUnitsByFaction(faction).forEach(Unit::resetTurnState);
    }
}