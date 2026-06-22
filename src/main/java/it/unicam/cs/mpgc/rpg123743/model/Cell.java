package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta una singola cella sulla griglia della mappa di battaglia di GridWar.
 * Ogni cella possiede un tipo di terreno dinamico e può opzionalmente ospitare un'unità.
 * Gestisce lo stato e la transizione dei terreni distruttibili (es. muri abbattibili).
 */
public class Cell {

    private final Position position;
    private TerrainType terrainType;
    private Unit occupant;
    private int wallHp;

    /**
     * Costruisce una nuova cella con posizione e tipo di terreno specificati.
     *
     * @param position    la posizione della cella sulla griglia (non nulla).
     * @param terrainType il tipo di terreno iniziale della cella (non nullo).
     * @throws NullPointerException se position o terrainType sono nulli.
     */
    public Cell(Position position, TerrainType terrainType) {
        this.position = Objects.requireNonNull(position, "Position must not be null.");
        this.terrainType = Objects.requireNonNull(terrainType, "Terrain type must not be null.");
        this.occupant = null;
        this.wallHp = terrainType.getBaseWallHp();
    }

    /**
     * Restituisce {@code true} se questa cella può essere attraversata o occupata da un'unità.
     * Delega la decisione al tipo di terreno corrente, in modo che la regola di passabilità
     * sia definita in un solo posto ({@link TerrainType#isPassable()}).
     *
     * @return {@code true} se la cella è calpestabile, {@code false} se è un ostacolo.
     */
    public boolean isPassable() {
        return terrainType.isPassable();
    }

    /**
     * Restituisce {@code true} se questa cella è attraversabile e non è occupata da alcuna unità.
     *
     * @return {@code true} se un'unità può fermarsi su questa cella.
     */
    public boolean isAvailable() {
        return isPassable() && occupant == null;
    }

    /**
     * Posiziona un'unità su questa cella.
     * Garantisce la coerenza dello stato vietando valori nulli o sovrascritture accidentali.
     *
     * @param unit l'unità non nulla da posizionare.
     * @throws NullPointerException se l'unità fornita è nulla (usare {@link #clearOccupant()} per svuotare).
     * @throws IllegalStateException se la cella è già occupata da un'altra unità.
     */
    public void setOccupant(Unit unit) {
        Objects.requireNonNull(unit, "Occupant cannot be null. Use clearOccupant() instead.");
        if (this.occupant != null) {
            throw new IllegalStateException("Cell " + position + " is already occupied by " + occupant.getName());
        }
        this.occupant = unit;
    }

    /**
     * Rimuove l'unità da questa cella, rendendola nuovamente libera.
     */
    public void clearOccupant() {
        this.occupant = null;
    }

    /**
     * Restituisce {@code true} se la cella è attualmente occupata da un'unità.
     *
     * @return {@code true} se c'è un occupante, {@code false} altrimenti.
     */
    public boolean isOccupied() {
        return occupant != null;
    }

    /**
     * Restituisce {@code true} se questa cella contiene un muro distruttibile ancora integro.
     *
     * @return {@code true} se il muro è presente e ha ancora HP.
     */
    public boolean isBreakableWall() {
        return terrainType == TerrainType.BREAKABLE_WALL && wallHp > 0;
    }

    /**
     * Applica danno al muro distruttibile se presente.
     * Se gli HP scendono a zero, il muro crolla e la cella si trasforma permanentemente in Pianura (PLAIN).
     *
     * @param damage il danno da applicare (deve essere strettamente positivo).
     * @return {@code true} se il muro è stato distrutto a seguito di questo attacco, {@code false} altrimenti.
     * @throws IllegalArgumentException se il danno è minore o uguale a zero.
     */
    public boolean damageWall(int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be positive. Got: " + damage);
        }
        if (!isBreakableWall()) {
            return false;
        }

        this.wallHp = Math.max(0, this.wallHp - damage);

        if (this.wallHp == 0) {
            this.terrainType = TerrainType.PLAIN; // Metamorfosi pulita: scompare il muro, nascono le pianure!
            return true;
        }
        return false;
    }

    /**
     * Restituisce la posizione di questa cella sulla griglia.
     *
     * @return la posizione della cella.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Restituisce il tipo di terreno attualmente associato a questa cella.
     *
     * @return il tipo di terreno.
     */
    public TerrainType getTerrainType() {
        return terrainType;
    }

    /**
     * Restituisce l'unità che occupa questa cella, se presente.
     *
     * @return l'unità occupante, oppure {@code null} se la cella è libera.
     */
    public Unit getOccupant() {
        return occupant;
    }

    /**
     * Restituisce gli HP correnti del muro distruttibile, se applicabile.
     *
     * @return gli HP correnti del muro (0 se non distruttibile o già crollato).
     */
    public int getWallHp() {
        return wallHp;
    }
}