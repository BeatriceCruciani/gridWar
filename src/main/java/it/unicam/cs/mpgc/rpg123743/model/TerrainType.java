package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta il tipo di terreno di una cella sulla mappa di battaglia in GridWar.
 * Regola l'accessibilità della cella, i costi di movimento e i bonus difensivi.
 * Le proprietà qui definite sono configurazioni statiche e immutabili.
 */
public enum TerrainType {

    /** Pianura — Facile da attraversare, nessun bonus difensivo. */
    PLAIN(true, 1, 0, 0),

    /** Foresta — Rallenta il movimento, fornisce copertura leggera. */
    FOREST(true, 2, 1, 0),

    /** Montagna — Difficile da attraversare, ottima copertura difensiva. */
    MOUNTAIN(true, 3, 2, 0),

    /** Muro — Struttura permanente non attraversabile da alcuna unità. */
    WALL(false, 0, 0, 0),

    /** Muro distruttibile — Barriera inizialmente impassabile che può essere attaccata e demolita. */
    BREAKABLE_WALL(false, 0, 0, 20);

    private final boolean passable;
    private final int movementCost;
    private final int defenceBonus;
    private final int baseWallHp;

    /**
     * Costruttore interno per configurare le proprietà del terreno.
     *
     * @param passable     {@code true} se le unità possono attraversare questo terreno.
     * @param movementCost punti movimento necessari per entrare nella cella (significativo solo se passabile).
     * @param defenceBonus bonus di difesa garantito alle unità che occupano la cella.
     * @param baseWallHp   HP massimi iniziali del muro (0 se non distruttibile).
     */
    TerrainType(boolean passable, int movementCost, int defenceBonus, int baseWallHp) {
        this.passable = passable;
        this.movementCost = movementCost;
        this.defenceBonus = defenceBonus;
        this.baseWallHp = baseWallHp;
    }

    /**
     * Restituisce {@code true} se il terreno è normalmente attraversabile dalle unità.
     *
     * @return {@code true} se passabile, {@code false} se blocca il movimento (muri).
     */
    public boolean isPassable() { return passable; }

    /**
     * Restituisce {@code true} se questo terreno rappresenta un oggetto di mappa
     * che può essere preso di mira e distrutto.
     *
     * @return {@code true} se il terreno ha HP distruttibili.
     */
    public boolean isDestructible() { return baseWallHp > 0; }

    /**
     * Restituisce il costo in punti movimento per entrare in questa cella.
     * Ha senso invocare questo metodo solo su terreni passabili: su un terreno
     * impassabile il valore è 0 e non deve essere usato per calcoli di percorso.
     *
     * @return il costo di movimento, o 0 se il terreno non è attraversabile.
     */
    public int getMovementCost() { return movementCost; }

    /** @return il bonus di difesa aggiunto alla statistica dell'unità che occupa la cella. */
    public int getDefenceBonus() { return defenceBonus; }

    /** @return gli HP massimi iniziali del muro (0 se il terreno non è distruttibile). */
    public int getBaseWallHp() { return baseWallHp; }
}