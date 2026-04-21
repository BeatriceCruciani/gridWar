package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta la classe di un'unità in GridWar.
 * Ogni classe ha statistiche base e affinità con le armi diverse.
 */
public enum UnitClass {
    /** Combattente corpo a corpo con alti HP e difesa. */
    WARRIOR,
    /** Mago combatte a distanza con alto attacco magico. */
    MAGE,
    /** Arciere combatte a distanza con alta velocità. */
    ARCHER,
    /** Cavaliere corazzato con alta difesa fisica e buona mobilità. */
    KNIGHT,
    /** Ladro veloce e agile, ma statistiche difensive basse. */
    THIEF
}