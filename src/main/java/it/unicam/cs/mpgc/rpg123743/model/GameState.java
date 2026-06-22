package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta lo stato corrente di una sessione di gioco in GridWar.
 * È l'oggetto radice utilizzato per la persistenza e la sincronizzazione del ciclo di gioco.
 * Contiene il numero del turno corrente, la fase attiva e il riferimento alla mappa.
 */
public class GameState {

    /**
     * Rappresenta la fase corrente del ciclo di gioco o l'esito della partita.
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
    private String saveName;

    /**
     * Costruisce un nuovo stato di gioco con il nome del salvataggio e la mappa specificati.
     * Il turno parte da 1 e la fase iniziale è {@link Phase#PLAYER_TURN}.
     *
     * @param saveName  il nome del salvataggio (non nullo né vuoto).
     * @param battleMap la mappa di battaglia associata a questa sessione.
     * @throws IllegalArgumentException se saveName è nullo o vuoto.
     * @throws NullPointerException se battleMap è nulla.
     */
    public GameState(String saveName, BattleMap battleMap) {
        if (saveName == null || saveName.isBlank()) {
            throw new IllegalArgumentException("Save name must not be blank.");
        }
        this.saveName = saveName;
        this.battleMap = Objects.requireNonNull(battleMap, "BattleMap must not be null.");
        this.turnNumber = 1;
        this.currentPhase = Phase.PLAYER_TURN;
    }

    /**
     * Avanza alla fase successiva del ciclo di gioco in modo deterministico.
     * Sfrutta uno switch expression per garantire la robustezza del cambio stato.
     * Non ha effetto se la partita è già terminata (VICTORY o DEFEAT).
     */
    public void advancePhase() {
        this.currentPhase = switch (this.currentPhase) {
            case PLAYER_TURN -> Phase.ENEMY_TURN;
            case ENEMY_TURN -> {
                this.turnNumber++;
                yield Phase.PLAYER_TURN;
            }
            case VICTORY, DEFEAT -> this.currentPhase; // Stato terminale, non cambia
        };
    }

    /**
     * Restituisce {@code true} se la partita è ancora in corso (fase attiva di gioco).
     *
     * @return {@code true} se la partita non è né vinta né persa.
     */
    public boolean isOngoing() {
        return currentPhase == Phase.PLAYER_TURN || currentPhase == Phase.ENEMY_TURN;
    }

    /**
     * Imposta direttamente la fase corrente del gioco, bypassando la normale
     * sequenza gestita da {@link #advancePhase()}. Da utilizzare esclusivamente
     * da sistemi di controllo esterni (es. un servizio che verifica le condizioni
     * di vittoria/sconfitta) per forzare la transizione a {@link Phase#VICTORY}
     * o {@link Phase#DEFEAT}. Non usare per il normale avanzamento dei turni.
     *
     * @param phase la nuova fase (non nulla).
     * @throws NullPointerException se phase è nulla.
     */
    public void setPhase(Phase phase) {
        this.currentPhase = Objects.requireNonNull(phase, "Phase cannot be null.");
    }

    /**
     * Imposta il nome del salvataggio associato a questa sessione di gioco.
     *
     * @param saveName il nuovo nome del salvataggio (non nullo e non vuoto).
     * @throws IllegalArgumentException se il nome è vuoto o composto da soli spazi.
     */
    public void setSaveName(String saveName) {
        if (saveName == null || saveName.isBlank()) {
            throw new IllegalArgumentException("Save name must not be blank.");
        }
        this.saveName = saveName;
    }

    /** @return il numero del turno corrente. */
    public int getTurnNumber() { return turnNumber; }

    /** @return la fase corrente del ciclo di gioco. */
    public Phase getCurrentPhase() { return currentPhase; }

    /** @return la mappa di battaglia associata a questa sessione. */
    public BattleMap getBattleMap() { return battleMap; }

    /** @return il nome del salvataggio corrente. */
    public String getSaveName() { return saveName; }
}