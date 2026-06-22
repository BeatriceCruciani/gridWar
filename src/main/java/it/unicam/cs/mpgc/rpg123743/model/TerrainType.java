package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta il tipo di terreno di una cella sulla mappa di battaglia in GridWar.
 * Regola l'accessibilità della cella, i costi di movimento e i bonus difensivi.
 * Le proprietà qui definite sono configurazioni statiche e immutabili.
 */
public enum TerrainType {

    /** Pianura — Facile da attraversare, nessun bonus difensivo. */
    PLAIN(1, 0, 0),

    /** Foresta — Rallenta il movimento, fornisce copertura leggera. */
    FOREST(2, 1, 0),

    /** Montagna — Difficile da attraversare, ottima copertura difensiva. */
    MOUNTAIN(3, 2, 0),

    /** Forte — Posizione fortificata calpestabile; garantisce protezione elevata. */
    FORT(2, 3, 0),

    /** Muro — Struttura permanente non attraversabile da alcuna unità. */
    WALL(-1, 0, 0),

    /** Muro distruttibile — Barriera inizialmente impassabile che può essere attaccata e demolita. */
    BREAKABLE_WALL(-1, 0, 20);

    private final int movementCost;
    private final int defenceBonus;
    private final int baseWallHp;

    /**
     * Costruttore interno per configurare le proprietà del terreno.
     * Un costo di movimento pari a -1 indica che il terreno è intrinsecamente impassabile.
     */
    TerrainType(int movementCost, int defenceBonus, int baseWallHp) {
        this.movementCost = movementCost;
        this.defenceBonus = defenceBonus;
        this.baseWallHp = baseWallHp;
    }

    /**
     * Restituisce {@code true} se il terreno è normalmente attraversabile dalle unità.
     * * @return {@code true} se passabile, {@code false} se blocca il movimento (muri).
     */
    public boolean isPassable() {
        return this.movementCost != -1;
    }

    /**
     * Restituisce {@code true} se questo terreno rappresenta un oggetto di mappa che può essere preso di mira e distrutto.
     * * @return {@code true} se il terreno ha degli HP distruttibili.
     */
    public boolean isDestructible() {
        return this.baseWallHp > 0;
    }

    /** @return il costo in punti movimento per entrare in questa cella. */
    public int getMovementCost() { return movementCost; }

    /** @return il bonus di difesa aggiunto alla statistica dell'unità che occupa la cella. */
    public int getDefenceBonus() { return defenceBonus; }

    /** @return gli HP massimi iniziali del muro (0 se il terreno non è distruttibile). */
    public int getBaseWallHp() { return baseWallHp; }
}