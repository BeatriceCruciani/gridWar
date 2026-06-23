package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;
import java.util.List;
import java.util.Objects;

/**
 * Gestisce l'avanzamento dei turni e verifica le condizioni di vittoria e sconfitta.
 * Il turno del giocatore termina quando tutte le unità del giocatore hanno completato
 * le loro azioni. La vittoria si ottiene sconfiggendo tutte le unità nemiche.
 * La sconfitta avviene quando tutte le unità del giocatore vengono eliminate.
 */
public class TurnService {

    /**
     * Costruttore stateless autonomo.
     */
    public TurnService() {
        // Costruttore vuoto pulito, in linea con gli altri Service
    }

    /**
     * Termina il turno del giocatore e inizia il turno del nemico.
     * Azzera lo stato del turno di tutte le unità del giocatore.
     *
     * @param state lo stato di gioco corrente.
     */
    public void endPlayerTurn(GameState state) {
        Objects.requireNonNull(state, "GameState cannot be null when ending player turn.");
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
        Objects.requireNonNull(state, "GameState cannot be null when ending enemy turn.");
        resetFactionUnits(state.getBattleMap(), Faction.ENEMY);
        state.advancePhase();
    }

    /**
     * Verifica se la condizione di vittoria è soddisfatta: nessuna unità nemica rimasta in vita.
     *
     * <p><b>Importante:</b> questo metodo deve essere invocato <b>prima</b> di
     * {@link #removeDefeatedUnits(GameState)} nel ciclo di gioco. Si basa sulla
     * presenza delle unità nemiche sulla mappa (anche se sconfitte ma non ancora
     * rimosse) per distinguere "nessun nemico è mai esistito" da "tutti i nemici
     * sono stati sconfitti". Se i cadaveri vengono rimossi prima di questa chiamata,
     * la lista delle unità nemiche risulta vuota e la vittoria non viene rilevata.</p>
     *
     * @param state lo stato di gioco corrente.
     * @return {@code true} se tutti i nemici sono stati sconfitti.
     */
    public boolean checkVictory(GameState state) {
        Objects.requireNonNull(state, "GameState cannot be null during victory check.");

        List<Unit> enemies = state.getBattleMap().getUnitsByFaction(Faction.ENEMY);

        // Se non ci sono mai stati nemici, evitiamo un falso positivo (lista vuota = nessun nemico mai esistito).
        boolean victory = !enemies.isEmpty() && enemies.stream().noneMatch(Unit::isAlive);

        if (victory) {
            state.setPhase(GameState.Phase.VICTORY);
        }
        return victory;
    }

    /**
     * Verifica se la condizione di sconfitta è soddisfatta: tutte le unità del giocatore sconfitte.
     *
     * <p><b>Importante:</b> vale la stessa nota su ordine di chiamata descritta in
     * {@link #checkVictory(GameState)}: invocare prima di {@link #removeDefeatedUnits(GameState)}.</p>
     *
     * @param state lo stato di gioco corrente.
     * @return {@code true} se tutte le unità del giocatore sono state sconfitte.
     */
    public boolean checkDefeat(GameState state) {
        Objects.requireNonNull(state, "GameState cannot be null during defeat check.");

        List<Unit> players = state.getBattleMap().getUnitsByFaction(Faction.PLAYER);

        // Struttura speculare a checkVictory per prevenire bug di liste vuote a seguito della rimozione dei corpi
        boolean defeat = !players.isEmpty() && players.stream().noneMatch(Unit::isAlive);

        if (defeat) {
            state.setPhase(GameState.Phase.DEFEAT);
        }
        return defeat;
    }

    /**
     * Restituisce {@code true} se tutte le unità del giocatore ancora in vita
     * hanno completato il loro turno.
     *
     * @param state lo stato di gioco corrente.
     * @return {@code true} se tutte le unità attive hanno agito.
     */
    public boolean allPlayerUnitsFinished(GameState state) {
        Objects.requireNonNull(state, "GameState cannot be null when checking player actions status.");

        List<Unit> activePlayers = state.getBattleMap().getUnitsByFaction(Faction.PLAYER)
                .stream()
                .filter(Unit::isAlive)
                .toList();

        // Se non ci sono giocatori vivi, non ha senso dire che hanno "finito il turno" con successo
        if (activePlayers.isEmpty()) return false;

        return activePlayers.stream().allMatch(Unit::hasFinishedTurn);
    }

    /**
     * Rimuove dalla mappa tutte le unità che sono state sconfitte.
     * Sfrutta la sicurezza del registro interno di BattleMap per evitare ConcurrentModificationException.
     * Da invocare dopo {@link #checkVictory(GameState)} e {@link #checkDefeat(GameState)}
     * nel ciclo di gioco
     * @param state lo stato di gioco corrente.
     */
    public void removeDefeatedUnits(GameState state) {
        Objects.requireNonNull(state, "GameState cannot be null during defeated units cleanup.");
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