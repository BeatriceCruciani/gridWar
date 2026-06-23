package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta un'arma che un'unità può equipaggiare e usare in combattimento.
 * Un'arma ha un tipo (utilizzato per determinare i vantaggi nel triangolo delle armi),
 * un bonus fisso all'attacco e una gittata specifica.
 *
 * Lo stato "equipaggiata" è tracciato direttamente da questa classe tramite
 * {@link #isEquipped()}, invece che da un riferimento duplicato in {@link Unit}.
 * Questo evita di mantenere sincronizzati due riferimenti alla stessa istanza
 * (uno nell'inventario, uno come "arma corrente"), problema che si manifesterebbe
 * in particolare dopo la deserializzazione da JSON, dove Gson non preserva
 * l'identità tra oggetti riferiti più volte nel grafo.</p>
 */
public class Weapon extends Item {

    private final WeaponType weaponType;
    private final int attackBonus;
    private final int range;
    private boolean equipped;

    /**
     * Costruisce una nuova arma con le caratteristiche specificate.
     * L'arma non è equipaggiata di default.
     *
     * @param name        il nome dell'arma.
     * @param description la descrizione dell'arma.
     * @param durability  il numero di utilizzi disponibili (-1 per infiniti).
     * @param weaponType  il tipo di arma (usato per il triangolo delle armi, non nullo).
     * @param attackBonus il bonus aggiunto all'attacco dell'unità (non negativo).
     * @param range       la gittata dell'arma in celle (minimo 1).
     * @throws IllegalArgumentException se attackBonus è negativo o range è inferiore a 1.
     * @throws NullPointerException se weaponType è nullo.
     */
    public Weapon(String name, String description, int durability,
                  WeaponType weaponType, int attackBonus, int range) {
        super(name, description, durability);
        if (attackBonus < 0) {
            throw new IllegalArgumentException("Attack bonus must be non-negative.");
        }
        if (range < 1) {
            throw new IllegalArgumentException("Range must be at least 1.");
        }
        this.weaponType = Objects.requireNonNull(weaponType, "Weapon type must not be null.");
        this.attackBonus = attackBonus;
        this.range = range;
        this.equipped = false;
    }

    /**
     * Restituisce il tipo di arma (es. SPADA, ASCIA, LANCIA).
     *
     * @return il tipo di arma associato.
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Restituisce il bonus al danno (Might) fornito da questa arma.
     *
     * @return il bonus all'attacco.
     */
    public int getAttackBonus() {
        return attackBonus;
    }

    /**
     * Restituisce la gittata massima dell'arma in celle (1 per corpo a corpo, 2 per distanza).
     *
     * @return la gittata dell'arma.
     */
    public int getRange() {
        return range;
    }

    /**
     * Restituisce {@code true} se questa arma è attualmente equipaggiata dalla
     * sua unità proprietaria.
     *
     * @return {@code true} se equipaggiata.
     */
    public boolean isEquipped() {
        return equipped;
    }

    /**
     * Imposta lo stato di equipaggiamento di questa arma. Da usare esclusivamente
     * tramite {@link Unit#equipWeapon(Weapon)}, che garantisce che al massimo
     * un'arma per unità sia equipaggiata contemporaneamente.
     *
     * @param equipped {@code true} per equipaggiare, {@code false} per disequipaggiare.
     */
    void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }
}