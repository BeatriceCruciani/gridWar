package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta il tipo di terreno di una cella sulla mappa di battaglia.
 * Il terreno influenza il costo di movimento e il bonus difensivo.
 * I muri distruttibili hanno HP e possono essere eliminati durante il gioco.
 */
public enum TerrainType {
    /** Pianura — facile da attraversare, nessun bonus difensivo. */
    PLAIN(1, 0, 0),
    /** Foresta — rallenta il movimento, fornisce copertura leggera. */
    FOREST(2, 1, 0),
    /** Montagna — difficile da attraversare, ottima copertura. */
    MOUNTAIN(3, 2, 0),
    /** Forte — posizione fortificata, copertura elevata. */
    FORT(2, 3, 0),
    /** Muro — non attraversabile da nessuna unità. */
    WALL(0, 0, 0),
    /** Muro distruttibile — può essere attaccato e rimosso. */
    BREAKABLE_WALL(0, 1, 20);

    private final int movementCost;
    private final int defenceBonus;
    private final int wallHp;

    TerrainType(int movementCost, int defenceBonus, int wallHp) {
        this.movementCost = movementCost;
        this.defenceBonus = defenceBonus;
        this.wallHp       = wallHp;
    }

    /** Restituisce il costo in punti movimento per attraversare questo terreno. */
    public int getMovementCost() { return movementCost; }

    /** Restituisce il bonus difensivo garantito alle unità su questo terreno. */
    public int getDefenceBonus() { return defenceBonus; }

    /** Restituisce gli HP del muro (0 se non distruttibile). */
    public int getWallHp() { return wallHp; }
}