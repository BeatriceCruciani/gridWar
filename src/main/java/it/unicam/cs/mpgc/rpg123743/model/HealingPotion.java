package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta un consumabile specifico per il ripristino dei punti vita (HP).
 * Quando utilizzato, ripristina una quantità fissa di HP all'unità bersaglio.
 */
public class HealingPotion extends Consumable {

    private final int healAmount;

    /**
     * Costruisce una nuova pozione di cura.
     *
     * @param name        il nome della pozione.
     * @param description la descrizione della pozione.
     * @param durability  il numero di utilizzi disponibili.
     * @param healAmount  la quantità di HP ripristinata ad ogni utilizzo (deve essere positiva).
     * @throws IllegalArgumentException se healAmount non è positivo.
     */
    public HealingPotion(String name, String description, int durability, int healAmount) {
        super(name, description, durability);
        if (healAmount <= 0) {
            throw new IllegalArgumentException("Heal amount must be positive.");
        }
        this.healAmount = healAmount;
    }

    /**
     * Implementazione dell'effetto specifico: cura l'unità bersaglio del valore stabilito.
     *
     * @param target l'unità da curare.
     */
    @Override
    protected void applyEffect(Unit target) {
        // Assume che l'unità abbia un metodo per ricevere cure (es. heal)
        target.heal(healAmount);
    }

    /**
     * Restituisce la quantità di HP ripristinata da questo oggetto.
     * * @return i punti vita ripristinati per singolo utilizzo.
     */
    public int getHealAmount() {
        return healAmount;
    }
}
