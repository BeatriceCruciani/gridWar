package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta le statistiche di un'unità in GridWar.
 * Utilizzata sia come statistiche base della classe sia come statistiche correnti dell'unità.
 *   maxHp: punti ferita massimi
 *   currentHp: punti ferita attuali
 *   attack: potere d'attacco fisico o magico
 *   defence: riduzione dei danni contro attacchi fisici
 *   resistance: riduzione dei danni contro attacchi magici
 *   speed: influenza la probabilità di colpire e di schivare
 *   movement: numero di celle percorribili per turno
 */
public class Stats {

    private final int maxHp;
    private int currentHp;
    private int attack;
    private int defence;
    private int resistance;
    private int speed;
    private int movement;

    /**
     * Costruisce un nuovo oggetto statistiche con i valori specificati.
     * Gli HP correnti vengono inizializzati al valore massimo.
     *
     * @param maxHp      punti ferita massimi (deve essere positivo).
     * @param attack     potere d'attacco.
     * @param defence    riduzione danni fisici.
     * @param resistance riduzione danni magici.
     * @param speed      velocità dell'unità.
     * @param movement   celle percorribili per turno (deve essere positivo).
     * @throws IllegalArgumentException se maxHp o movement non sono positivi.
     */
    public Stats(int maxHp, int attack, int defence, int resistance, int speed, int movement) {
        if (maxHp <= 0) throw new IllegalArgumentException("Max HP must be positive.");
        if (movement <= 0) throw new IllegalArgumentException("Movement must be positive.");
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defence = defence;
        this.resistance = resistance;
        this.speed = speed;
        this.movement = movement;
    }

    /**
     * Applica danni all'unità, portando gli HP correnti a un minimo di zero.
     *
     * @param amount la quantità di danni da applicare (non negativa).
     * @throws IllegalArgumentException se amount è negativo.
     */
    public void applyDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Damage amount must be non-negative.");
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    /**
     * Ripristina HP all'unità, fino al massimo consentito.
     *
     * @param amount la quantità di HP da ripristinare (non negativa).
     * @throws IllegalArgumentException se amount è negativo.
     */
    public void restoreHp(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Heal amount must be non-negative.");
        this.currentHp = Math.min(maxHp, this.currentHp + amount);
    }

    /**
     * Restituisce {@code true} se l'unità non ha più HP rimanenti.
     */
    public boolean isDead() {
        return currentHp <= 0;
    }

    /** Restituisce i punti ferita massimi. */
    public int getMaxHp() { return maxHp; }
    /** Restituisce i punti ferita attuali. */
    public int getCurrentHp() { return currentHp; }
    /** Restituisce il potere d'attacco. */
    public int getAttack() { return attack; }
    /** Restituisce la difesa fisica. */
    public int getDefence() { return defence; }
    /** Restituisce la resistenza magica. */
    public int getResistance() { return resistance; }
    /** Restituisce la velocità. */
    public int getSpeed() { return speed; }
    /** Restituisce il numero di celle percorribili per turno. */
    public int getMovement() { return movement; }
}