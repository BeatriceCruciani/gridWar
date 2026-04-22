package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta lo stato corrente di una sessione di gioco.
 * È l'oggetto radice che viene serializzato in JSON per la persistenza.
 * Contiene il numero del turno corrente, la fase attiva e l'esito della battaglia.
 */
public class GameState {

    /**
     * Rappresenta la fase corrente del gioco.
     */
    public enum Phase {
        /** Turno del giocatore — le unità del giocatore possono agire. */
        PLAYER_TURN,
        /** Turno del nemico — le unità nemiche vengono mosse dall'AI. */
        ENEMY_TURN,
        /** Vittoria — tutte le unità nemiche sono state sconfitte. */
        VICTORY,
        /** Sconfitta — tutte le unità del giocatore sono state sconfitte. */
        DEFEAT
    }

    private int turnNumber;
    private Phase currentPhase;
    private final BattleMap battleMap;
    private final String saveName;

    /**
     * Costruisce un nuovo stato di gioco con il nome del salvataggio e la mappa specificati.
     * Il turno parte da 1 e la fase iniziale è {@link Phase#PLAYER_TURN}.
     *
     * @param saveName  il nome del salvataggio (non nullo né vuoto).
     * @param battleMap la mappa di battaglia associata a questa sessione.
     * @throws IllegalArgumentException se saveName è nullo o vuoto, o battleMap è nulla.
     */
    public GameState(String saveName, BattleMap battleMap) {
        if (saveName == null || saveName.isBlank()) {
            throw new IllegalArgumentException("Save name must not be blank.");
        }
        if (battleMap == null) {
            throw new IllegalArgumentException("BattleMap must not be null.");
        }
        this.saveName = saveName;
        this.battleMap = battleMap;
        this.turnNumber = 1;
        this.currentPhase = Phase.PLAYER_TURN;
    }

    /**
     * Avanza alla fase successiva del gioco.
     * PLAYER_TURN → ENEMY_TURN → PLAYER_TURN (incrementando il numero di turno).
     * Non ha effetto se la partita è già terminata (VICTORY o DEFEAT).
     */
    public void advancePhase() {
        if (currentPhase == Phase.PLAYER_TURN) {
            currentPhase = Phase.ENEMY_TURN;
        } else if (currentPhase == Phase.ENEMY_TURN) {
            currentPhase = Phase.PLAYER_TURN;
            turnNumber++;
        }
    }

    /**
     * Restituisce {@code true} se la partita è ancora in corso.
     */
    public boolean isOngoing() {
        return currentPhase == Phase.PLAYER_TURN || currentPhase == Phase.ENEMY_TURN;
    }

    /** Imposta la fase corrente del gioco. */
    public void setPhase(Phase phase) { this.currentPhase = phase; }

    /** Restituisce il numero del turno corrente. */
    public int getTurnNumber() { return turnNumber; }
    /** Restituisce la fase corrente del gioco. */
    public Phase getCurrentPhase() { return currentPhase; }
    /** Restituisce la mappa di battaglia associata a questa sessione. */
    public BattleMap getBattleMap() { return battleMap; }
    /** Restituisce il nome del salvataggio. */
    public String getSaveName() { return saveName; }
}