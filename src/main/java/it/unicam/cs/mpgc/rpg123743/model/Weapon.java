package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta un'arma che un'unità può equipaggiare e usare in combattimento.
 * Un'arma ha un tipo (usato per il triangolo delle armi), un bonus all'attacco
 * e una gittata (1 = corpo a corpo, 2 = distanza).
 */
public class Weapon extends Item {

    private final WeaponType weaponType;
    private final int attackBonus;
    private final int range;

    /**
     * Costruisce una nuova arma con le caratteristiche specificate.
     *
     * @param name        il nome dell'arma.
     * @param description la descrizione dell'arma.
     * @param durability  il numero di utilizzi disponibili.
     * @param weaponType  il tipo di arma (usato per il triangolo delle armi).
     * @param attackBonus il bonus aggiunto all'attacco dell'unità.
     * @param range       la gittata dell'arma in celle (minimo 1).
     * @throws IllegalArgumentException se attackBonus è negativo o range è inferiore a 1.
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
        this.weaponType = weaponType;
        this.attackBonus = attackBonus;
        this.range = range;
    }

    /** Restituisce il tipo di arma. */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /** Restituisce il bonus all'attacco fornito da questa arma. */
    public int getAttackBonus() {
        return attackBonus;
    }

    /** Restituisce la gittata dell'arma in celle. */
    public int getRange() {
        return range;
    }
}