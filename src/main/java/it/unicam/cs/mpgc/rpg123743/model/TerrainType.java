package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta il tipo di terreno di una cella sulla mappa di battaglia.
 * Il terreno influenza il costo di movimento e il bonus difensivo
 * per le unità che vi sostano.
 */
public enum TerrainType {
    /** Pianura — facile da attraversare, nessun bonus difensivo. */
    PLAIN(1, 0),
    /** Foresta — rallenta il movimento, fornisce copertura leggera. */
    FOREST(2, 1),
    /** Montagna — difficile da attraversare, ottima copertura. */
    MOUNTAIN(3, 2),
    /** Forte — posizione fortificata, copertura elevata. */
    FORT(2, 3),
    /** Muro — non attraversabile da nessuna unità. */
    WALL(0, 0);

    private final int movementCost;
    private final int defenceBonus;

    TerrainType(int movementCost, int defenceBonus) {
        this.movementCost = movementCost;
        this.defenceBonus = defenceBonus;
    }

    /** Restituisce il costo in punti movimento per attraversare questo terreno. */
    public int getMovementCost() {
        return movementCost;
    }

    /** Restituisce il bonus difensivo garantito alle unità che sostano su questo terreno. */
    public int getDefenceBonus() {
        return defenceBonus;
    }
}