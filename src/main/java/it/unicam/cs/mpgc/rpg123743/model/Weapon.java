package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta un'arma che un'unità può equipaggiare e usare in combattimento.
 * Un'arma ha un tipo (utilizzato per determinare i vantaggi nel triangolo delle armi),
 * un bonus fisso all'attacco e una gittata specifica.
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
    }

    /** * Restituisce il tipo di arma (es. SPADA, ASCIA, LANCIA).
     * @return il tipo di arma associato.
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /** * Restituisce il bonus al danno (Might) fornito da questa arma.
     * @return il bonus all'attacco.
     */
    public int getAttackBonus() {
        return attackBonus;
    }

    /** * Restituisce la gittata massima dell'arma in celle (1 per corpo a corpo, 2 per distanza).
     * @return la gittata dell'arma.
     */
    public int getRange() {
        return range;
    }
}