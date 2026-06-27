package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Contenitore delle statistiche di un'unità in GridWar.
 * Espone i valori tramite getter e li modifica tramite
 * {@link #applyDamage(int)}, {@link #heal(int)} e {@link #applyLevelUp}.
 */
public class Stats {

    private int maxHp;
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
        if (maxHp <= 0)     throw new IllegalArgumentException("Max HP must be positive.");
        if (movement <= 0)  throw new IllegalArgumentException("Movement must be positive.");
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
     * Ripristina i punti ferita all'unità, fino al massimo consentito.
     *
     * @param amount la quantità di HP da ripristinare (deve essere positiva).
     * @throws IllegalArgumentException se amount non è positivo.
     */
    public void heal(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Heal amount must be positive.");
        this.currentHp = Math.min(maxHp, this.currentHp + amount);
    }

    /**
     * Restituisce {@code true} se l'unità non ha più HP rimanenti.
     *
     * @return {@code true} se l'unità è sconfitta, {@code false} altrimenti.
     */
    public boolean isDead() {
        return currentHp <= 0;
    }

    /** @return i punti ferita massimi. */
    public int getMaxHp()       { return maxHp; }
    /** @return i punti ferita attuali. */
    public int getCurrentHp()   { return currentHp; }
    /** @return il potere d'attacco. */
    public int getAttack()      { return attack; }
    /** @return la difesa fisica. */
    public int getDefence()     { return defence; }
    /** @return la resistenza magica. */
    public int getResistance()  { return resistance; }
    /** @return la velocità. */
    public int getSpeed()       { return speed; }
    /** @return il numero di celle percorribili per turno. */
    public int getMovement()    { return movement; }

    /**
     * Applica i bonus statistici al level up, aumentando tutte le statistiche
     * dei valori specificati. Gli HP correnti aumentano insieme al massimo,
     * come da convenzione Fire Emblem.
     *
     * @param hp  bonus HP massimi.
     * @param atk bonus attacco.
     * @param def bonus difesa.
     * @param res bonus resistenza.
     * @param spd bonus velocità.
     * @param mov bonus movimento.
     * @throws IllegalArgumentException se uno qualsiasi dei bonus è negativo.
     */
    public void applyLevelUp(int hp, int atk, int def, int res, int spd, int mov) {
        if (hp < 0 || atk < 0 || def < 0 || res < 0 || spd < 0 || mov < 0) {
            throw new IllegalArgumentException("Level up bonuses cannot be negative.");
        }
        this.maxHp      += hp;
        this.currentHp  += hp;
        this.attack     += atk;
        this.defence    += def;
        this.resistance += res;
        this.speed      += spd;
        this.movement   += mov;
    }
}