package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta la classe di un'unità in GridWar.
 * Ogni classe definisce i propri incrementi statistici (growth) da applicare al passaggio di livello,
 * strutturata secondo il principio Open/Closed (OCP) per permettere l'aggiunta di nuove classi
 * senza modificare la logica di gestione del livello dell'unità.
 */
public enum UnitClass {

    /** Combattente corpo a corpo con alti HP e difesa. */
    WARRIOR(3, 2, 1, 0, 1, 0),

    /** Mago combatte a distanza con alto attacco magico. */
    MAGE(2, 3, 0, 2, 1, 0),

    /** Arciere combatte a distanza con alta velocità. */
    ARCHER(2, 2, 1, 1, 2, 0),

    /** Cavaliere corazzato con alta difesa fisica e buona mobilità. */
    KNIGHT(3, 1, 3, 1, 0, 0),

    /** Ladro veloce e agile, ma statistiche difensive basse. */
    THIEF(1, 1, 1, 1, 3, 1),

    /** Curatore di supporto con alta resistenza magica e velocità, ma difese fisiche ridotte. */
    HEALER(2, 1, 0, 3, 2, 0);

    private final int hpBonus;
    private final int atkBonus;
    private final int defBonus;
    private final int resBonus;
    private final int spdBonus;
    private final int movBonus;

    /**
     * Costruttore interno dell'enum per mappare i bonus di crescita di ciascuna classe.
     * Segue rigorosamente l'ordine dei parametri richiesto dal metodo applyLevelUp di {@link Stats}.
     */
    UnitClass(int hpBonus, int atkBonus, int defBonus, int resBonus, int spdBonus, int movBonus) {
        this.hpBonus = hpBonus;
        this.atkBonus = atkBonus;
        this.defBonus = defBonus;
        this.resBonus = resBonus;
        this.spdBonus = spdBonus;
        this.movBonus = movBonus;
    }

    /**
     * Applica i bonus di livello specifici di questa classe all'oggetto statistiche fornito.
     * Evita l'utilizzo di strutture di controllo condizionali (switch/if) esterne.
     *
     * @param stats l'oggetto statistiche dell'unità da potenziare (non nullo).
     */
    public void applyLevelUp(Stats stats) {
        stats.applyLevelUp(hpBonus, atkBonus, defBonus, resBonus, spdBonus, movBonus);
    }
}