package it.unicam.cs.mpgc.rpg123743.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un'unità sulla mappa di battaglia.
 * Un'unità appartiene a una fazione, ha una classe, delle statistiche,
 * una posizione sulla griglia e un inventario di oggetti.
 * Ogni turno un'unità può muoversi una volta e compiere un'azione una volta.
 */
public class Unit {

    private static final int MAX_INVENTORY_SIZE = 3;
    private static final int MAX_LEVEL = 20;
    private static final int EXPERIENCE_PER_LEVEL = 100;

    private final String name;
    private final Faction faction;
    private final UnitClass unitClass;
    private final Stats stats;
    private final List<Item> inventory;

    private Weapon equippedWeapon;
    private Position position;
    private boolean hasMovedThisTurn;
    private boolean hasActedThisTurn;
    private int level;
    private int experience;

    /**
     * Costruisce una nuova unità con i parametri specificati.
     * Il livello iniziale è 1, l'esperienza è 0 e lo stato del turno è azzerato.
     *
     * @param name      il nome dell'unità (non nullo né vuoto).
     * @param faction   la fazione di appartenenza.
     * @param unitClass la classe dell'unità.
     * @param stats     le statistiche dell'unità.
     * @param position  la posizione iniziale sulla griglia.
     * @throws IllegalArgumentException se il nome è vuoto o uno dei parametri è nullo.
     */
    public Unit(String name, Faction faction, UnitClass unitClass, Stats stats, Position position) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unit name must not be blank.");
        }
        if (faction == null || unitClass == null || stats == null || position == null) {
            throw new IllegalArgumentException("Unit fields must not be null.");
        }
        this.name = name;
        this.faction = faction;
        this.unitClass = unitClass;
        this.stats = stats;
        this.position = position;
        this.inventory = new ArrayList<>();
        this.hasMovedThisTurn = false;
        this.hasActedThisTurn = false;
        this.level = 1;
        this.experience = 0;
    }

    // --- Inventario ---

    /**
     * Aggiunge un oggetto all'inventario dell'unità se c'è spazio disponibile.
     *
     * @param item l'oggetto da aggiungere.
     * @return {@code true} se l'oggetto è stato aggiunto, {@code false} se l'inventario è pieno.
     */
    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) return false;
        inventory.add(item);
        return true;
    }

    /**
     * Rimuove un oggetto dall'inventario dell'unità.
     *
     * @param item l'oggetto da rimuovere.
     * @return {@code true} se l'oggetto è stato rimosso, {@code false} se non era presente.
     */
    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }

    /**
     * Equipaggia un'arma dall'inventario dell'unità.
     * L'arma deve essere già presente nell'inventario.
     *
     * @param weapon l'arma da equipaggiare.
     * @return {@code true} se l'arma è stata equipaggiata, {@code false} se non trovata nell'inventario.
     */
    public boolean equipWeapon(Weapon weapon) {
        if (!inventory.contains(weapon)) return false;
        this.equippedWeapon = weapon;
        return true;
    }

    /**
     * Restituisce una vista non modificabile dell'inventario dell'unità.
     *
     * @return lista non modificabile degli oggetti nell'inventario.
     */
    public List<Item> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    // --- Stato del turno ---

    /**
     * Azzera lo stato del turno dell'unità all'inizio di un nuovo turno.
     */
    public void resetTurnState() {
        this.hasMovedThisTurn = false;
        this.hasActedThisTurn = false;
    }

    /**
     * Segna questa unità come già mossa in questo turno.
     */
    public void markAsMoved() {
        this.hasMovedThisTurn = true;
    }

    /**
     * Segna questa unità come già azione compiuta in questo turno.
     */
    public void markAsActed() {
        this.hasActedThisTurn = true;
    }

    /**
     * Restituisce {@code true} se l'unità ha completato sia il movimento sia l'azione in questo turno.
     */
    public boolean hasFinishedTurn() {
        return hasMovedThisTurn && hasActedThisTurn;
    }

    // --- Esperienza e livellamento ---

    /**
     * Aggiunge esperienza all'unità. Se viene raggiunta la soglia necessaria,
     * l'unità guadagna un livello (fino al livello massimo {@value MAX_LEVEL}).
     *
     * @param amount la quantità di esperienza da aggiungere.
     * @return {@code true} se l'unità ha guadagnato un livello.
     */
    public boolean gainExperience(int amount) {
        if (level >= MAX_LEVEL) return false;
        this.experience += amount;
        if (this.experience >= EXPERIENCE_PER_LEVEL) {
            this.experience -= EXPERIENCE_PER_LEVEL;
            this.level++;
            return true;
        }
        return false;
    }

    // --- Query sullo stato ---

    /** Restituisce {@code true} se l'unità è ancora in vita. */
    public boolean isAlive() {
        return !stats.isDead();
    }

    /** Restituisce {@code true} se l'unità specificata appartiene a una fazione avversaria. */
    public boolean isEnemy(Unit other) {
        return this.faction != other.faction;
    }

    // --- Getter ---

    /** Restituisce il nome dell'unità. */
    public String getName() { return name; }
    /** Restituisce la fazione dell'unità. */
    public Faction getFaction() { return faction; }
    /** Restituisce la classe dell'unità. */
    public UnitClass getUnitClass() { return unitClass; }
    /** Restituisce le statistiche dell'unità. */
    public Stats getStats() { return stats; }
    /** Restituisce l'arma attualmente equipaggiata, o {@code null} se nessuna. */
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    /** Restituisce la posizione corrente dell'unità sulla griglia. */
    public Position getPosition() { return position; }
    /** Restituisce {@code true} se l'unità si è già mossa in questo turno. */
    public boolean hasMovedThisTurn() { return hasMovedThisTurn; }
    /** Restituisce {@code true} se l'unità ha già compiuto un'azione in questo turno. */
    public boolean hasActedThisTurn() { return hasActedThisTurn; }
    /** Restituisce il livello corrente dell'unità. */
    public int getLevel() { return level; }
    /** Restituisce l'esperienza accumulata nel livello corrente. */
    public int getExperience() { return experience; }

    // --- Setter (solo la posizione cambia durante il gameplay) ---

    /**
     * Aggiorna la posizione dell'unità sulla griglia.
     *
     * @param position la nuova posizione (non nulla).
     * @throws IllegalArgumentException se position è nulla.
     */
    public void setPosition(Position position) {
        if (position == null) throw new IllegalArgumentException("Position must not be null.");
        this.position = position;
    }

    @Override
    public String toString() {
        return name + " [" + unitClass + ", Lv." + level + ", HP:" +
                stats.getCurrentHp() + "/" + stats.getMaxHp() + "]";
    }
}