package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta un oggetto consumabile come una pozione.
 * Quando utilizzato, ripristina una quantità fissa di HP all'unità.
 */
public class Consumable extends Item {

    private final int healAmount;

    /**
     * Costruisce un nuovo oggetto consumabile.
     *
     * @param name        il nome dell'oggetto.
     * @param description la descrizione dell'oggetto.
     * @param durability  il numero di utilizzi disponibili.
     * @param healAmount  la quantità di HP ripristinata ad ogni utilizzo (deve essere positiva).
     * @throws IllegalArgumentException se healAmount non è positivo.
     */
    public Consumable(String name, String description, int durability, int healAmount) {
        super(name, description, durability);
        if (healAmount <= 0) {
            throw new IllegalArgumentException("Heal amount must be positive.");
        }
        this.healAmount = healAmount;
    }

    /** Restituisce la quantità di HP ripristinata da questo oggetto. */
    public int getHealAmount() {
        return healAmount;
    }
}