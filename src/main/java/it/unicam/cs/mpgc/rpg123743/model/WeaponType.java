package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta il tipo di arma che un'unità può impugnare in GridWar.
 * SWORD, AXE e LANCE formano il triangolo delle armi.
 * BOW e MAGIC sono neutrali e non partecipano al triangolo.
 */
public enum WeaponType {
    /** Spada — batte l'ascia nel triangolo delle armi. */
    SWORD,
    /** Ascia — batte la lancia nel triangolo delle armi. */
    AXE,
    /** Lancia — batte la spada nel triangolo delle armi. */
    LANCE,
    /** Arco — neutro, attacca a distanza. */
    BOW,
    /** Magia — neutro, ignora la difesa fisica e usa la resistenza del bersaglio. */
    MAGIC
}