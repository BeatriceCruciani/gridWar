package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta una singola cella sulla griglia della mappa di battaglia.
 * Ogni cella ha un tipo di terreno fisso e può opzionalmente contenere un'unità.
 */
public class Cell {

    private final Position position;
    private final TerrainType terrainType;
    private Unit occupant;
    private int wallHp;

    /**
     * Costruisce una nuova cella con posizione e tipo di terreno specificati.
     *
     * @param position    la posizione della cella sulla griglia.
     * @param terrainType il tipo di terreno della cella.
     * @throws IllegalArgumentException se position o terrainType sono nulli.
     */
    public Cell(Position position, TerrainType terrainType) {
        if (position == null || terrainType == null) {
            throw new IllegalArgumentException("Position and terrain type must not be null.");
        }
        this.position    = position;
        this.terrainType = terrainType;
        this.occupant    = null;
        this.wallHp      = terrainType.getWallHp();
    }

    /**
     * Restituisce {@code true} se questa cella può essere attraversata da un'unità.
     * Il terreno di tipo WALL non è mai attraversabile.
     */
    public boolean isPassable() {
        return terrainType != TerrainType.WALL
                && !(terrainType == TerrainType.BREAKABLE_WALL && wallHp > 0);
    }

    /**
     * Restituisce {@code true} se questa cella è attraversabile e non è occupata da alcuna unità.
     */
    public boolean isAvailable() {
        return isPassable() && occupant == null;
    }

    /**
     * Posiziona un'unità su questa cella.
     *
     * @param unit l'unità da posizionare.
     * @throws IllegalStateException se la cella è già occupata da un'altra unità.
     */
    public void setOccupant(Unit unit) {
        if (unit != null && occupant != null) {
            throw new IllegalStateException("Cell " + position + " is already occupied by " + occupant.getName());
        }
        this.occupant = unit;
    }

    /**
     * Rimuove l'unità da questa cella.
     */
    public void clearOccupant() {
        this.occupant = null;
    }

    /** Restituisce {@code true} se la cella è occupata da un'unità. */
    public boolean isOccupied() {
        return occupant != null;
    }

    /** Restituisce la posizione della cella sulla griglia. */
    public Position getPosition() { return position; }
    /** Restituisce il tipo di terreno della cella. */
    public TerrainType getTerrainType() { return terrainType; }
    /** Restituisce l'unità che occupa la cella, o {@code null} se vuota. */
    public Unit getOccupant() { return occupant; }

    /**
     * Restituisce true se questa cella contiene un muro distruttibile ancora in piedi.
     */
    public boolean isBreakableWall() {
        return terrainType == TerrainType.BREAKABLE_WALL && wallHp > 0;
    }

    /**
     * Applica danno al muro distruttibile.
     * Se gli HP scendono a zero il muro diventa pianura percorribile.
     *
     * @param damage il danno da applicare.
     * @return true se il muro è stato distrutto.
     */
    public boolean damageWall(int damage) {
        if (!isBreakableWall()) return false;
        wallHp = Math.max(0, wallHp - damage);
        return wallHp == 0;
    }

    /** Restituisce gli HP rimanenti del muro. */
    public int getWallHp() { return wallHp; }
}