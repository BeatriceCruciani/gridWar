package it.unicam.cs.mpgc.rpg123743.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Rappresenta un'unità sulla mappa di battaglia in stile Fire Emblem.
 * Un'unità appartiene a una fazione, ha una classe, delle statistiche,
 * una posizione sulla griglia e un inventario di oggetti limitato.
 * Ogni turno un'unità può muoversi una volta e compiere un'azione una volta.
 *
 * <p>Questa classe è l'unico punto d'ingresso per modificare gli HP dell'unità
 * (tramite {@link #heal(int)} e {@link #takeDamage(int)}): {@link Stats} resta
 * un contenitore di dati senza conoscenza delle regole di gioco (es. permadeath).
 * {@link #getStats()} è da considerarsi accesso in sola lettura.</p>
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
        this.name = name;
        this.faction = Objects.requireNonNull(faction, "Faction must not be null.");
        this.unitClass = Objects.requireNonNull(unitClass, "Unit class must not be null.");
        this.stats = Objects.requireNonNull(stats, "Stats must not be null.");
        this.position = Objects.requireNonNull(position, "Position must not be null.");
        this.inventory = new ArrayList<>();
        this.hasMovedThisTurn = false;
        this.hasActedThisTurn = false;
        this.level = 1;
        this.experience = 0;
    }

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
     * Se l'oggetto rimosso era l'arma attualmente equipaggiata, l'unità
     * viene automaticamente disarmata per evitare di mantenere un riferimento
     * a un'arma che non possiede più.
     *
     * @param item l'oggetto da rimuovere.
     * @return {@code true} se l'oggetto è stato rimosso, {@code false} se non era presente.
     */
    public boolean removeItem(Item item) {
        boolean removed = inventory.remove(item);
        if (removed && item.equals(equippedWeapon)) {
            this.equippedWeapon = null;
        }
        return removed;
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

    /**
     * Azzera lo stato del turno dell'unità all'inizio di un nuovo turno.
     */
    public void resetTurnState() {
        this.hasMovedThisTurn = false;
        this.hasActedThisTurn = false;
    }

    /** Segna questa unità come già mossa in questo turno. */
    public void markAsMoved() { this.hasMovedThisTurn = true; }

    /** Segna questa unità come già azione compiuta in questo turno. */
    public void markAsActed() { this.hasActedThisTurn = true; }

    /**
     * Restituisce {@code true} se l'unità ha completato sia il movimento sia l'azione in questo turno.
     *
     * @return {@code true} se il turno dell'unità è concluso.
     */
    public boolean hasFinishedTurn() {
        return hasMovedThisTurn && hasActedThisTurn;
    }

    /**
     * Ripristina una quantità specificata di punti vita (HP) all'unità.
     * Non ha effetto se l'unità è già stata sconfitta (permadeath).
     * La logica di calcolo del tetto massimo è delegata a {@link Stats}.
     *
     * @param amount la quantità di HP da ripristinare (deve essere positiva).
     * @throws IllegalArgumentException se amount è minore o uguale a zero.
     */
    public void heal(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Heal amount must be positive. Got: " + amount);
        }
        if (!isAlive()) {
            return; // Permadeath: le unità a 0 HP non possono essere curate
        }
        this.stats.heal(amount);
    }

    /**
     * Applica danno all'unità. Questo è l'unico punto d'ingresso previsto per
     * infliggere danno: centralizza qui eventuali future regole di gioco legate
     * al danno (es. invulnerabilità temporanea, danno riflesso, trigger di eventi),
     * evitando che il combat system manipoli {@link Stats} direttamente.
     *
     * @param amount la quantità di danno da applicare (deve essere non negativa).
     * @throws IllegalArgumentException se amount è negativo.
     */
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage amount must be non-negative. Got: " + amount);
        }
        this.stats.applyDamage(amount);
    }

    /**
     * Aggiunge esperienza all'unità. Se vengono raggiunte una o più soglie di livello
     * in un'unica chiamata (es. grandi quantità di esperienza), l'unità sale di
     * più livelli consecutivamente, fino al livello massimo {@value MAX_LEVEL}.
     *
     * @param amount la quantità di esperienza da aggiungere.
     * @return {@code true} se l'unità ha guadagnato almeno un livello.
     */
    public boolean gainExperience(int amount) {
        if (level >= MAX_LEVEL) return false;
        this.experience += amount;
        boolean leveledUp = false;
        while (this.experience >= EXPERIENCE_PER_LEVEL && level < MAX_LEVEL) {
            this.experience -= EXPERIENCE_PER_LEVEL;
            this.level++;
            applyLevelUpBonus();
            leveledUp = true;
        }
        return leveledUp;
    }

    /**
     * Applica i bonus statistici al level up delegando la responsabilità a {@link UnitClass}.
     * Questo approccio rispetta il principio Open/Closed (OCP), rimuovendo switch rigidi.
     */
    private void applyLevelUpBonus() {
        this.unitClass.applyLevelUp(this.stats);
    }

    /**
     * Restituisce {@code true} se l'unità è ancora in vita.
     *
     * @return {@code true} se l'unità è ancora in vita.
     */
    public boolean isAlive() {
        return !stats.isDead();
    }

    /**
     * Restituisce {@code true} se l'unità specificata appartiene a una fazione avversaria.
     *
     * @param other l'altra unità da verificare.
     * @return {@code true} se è un nemico.
     */
    public boolean isEnemy(Unit other) {
        return this.faction != other.faction;
    }

    /** @return il nome dell'unità. */
    public String getName() { return name; }

    /** @return la fazione di appartenenza. */
    public Faction getFaction() { return faction; }

    /** @return la classe dell'unità. */
    public UnitClass getUnitClass() { return unitClass; }

    /**
     * Restituisce le statistiche dell'unità. Da considerarsi accesso in sola
     * lettura: per modificare gli HP usare esclusivamente {@link #heal(int)}
     * e {@link #takeDamage(int)}, che applicano le regole di gioco (es. permadeath)
     * prima di delegare a {@link Stats}.
     *
     * @return le statistiche dell'unità.
     */
    public Stats getStats() { return stats; }

    /** @return l'arma attualmente equipaggiata, o {@code null} se nessuna. */
    public Weapon getEquippedWeapon() { return equippedWeapon; }

    /** @return la posizione corrente sulla griglia. */
    public Position getPosition() { return position; }

    /** @return {@code true} se l'unità si è già mossa in questo turno. */
    public boolean hasMovedThisTurn() { return hasMovedThisTurn; }

    /** @return {@code true} se l'unità ha già agito in questo turno. */
    public boolean hasActedThisTurn() { return hasActedThisTurn; }

    /** @return il livello corrente dell'unità. */
    public int getLevel() { return level; }

    /** @return l'esperienza corrente dell'unità. */
    public int getExperience() { return experience; }

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