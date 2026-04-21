package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta la fazione di appartenenza di un'unità.
 * Le unità PLAYER sono controllate dal giocatore umano.
 * Le unità ENEMY sono controllate dall'intelligenza artificiale.
 */
public enum Faction {
    /** Fazione del giocatore — unità controllate dall'utente. */
    PLAYER,
    /** Fazione nemica — unità controllate dall'AI. */
    ENEMY
}