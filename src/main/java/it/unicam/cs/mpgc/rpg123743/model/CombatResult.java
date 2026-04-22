package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta l'esito di uno scambio di combattimento tra due unità.
 * Restituito da {@code CombatService} dopo la risoluzione di un attacco.
 * La UI utilizza questo oggetto per mostrare il feedback del combattimento al giocatore.
 */
public class CombatResult {

    private final Unit attacker;
    private final Unit defender;
    private final int damageDealt;
    private final int damageReceived;
    private final boolean defenderDefeated;
    private final boolean attackerDefeated;
    private final boolean attackerLevelledUp;
    private final int experienceGained;

    /**
     * Costruisce un nuovo risultato di combattimento con tutti i dettagli dell'esito.
     *
     * @param attacker           l'unità che ha attaccato.
     * @param defender           l'unità che ha subito l'attacco.
     * @param damageDealt        i danni inflitti al difensore.
     * @param damageReceived     i danni ricevuti dall'attaccante (contrattacco).
     * @param defenderDefeated   {@code true} se il difensore è stato sconfitto.
     * @param attackerDefeated   {@code true} se l'attaccante è stato sconfitto dal contrattacco.
     * @param attackerLevelledUp {@code true} se l'attaccante ha guadagnato un livello.
     * @param experienceGained   l'esperienza guadagnata dall'attaccante.
     */
    public CombatResult(Unit attacker, Unit defender,
                        int damageDealt, int damageReceived,
                        boolean defenderDefeated, boolean attackerDefeated,
                        boolean attackerLevelledUp, int experienceGained) {
        this.attacker = attacker;
        this.defender = defender;
        this.damageDealt = damageDealt;
        this.damageReceived = damageReceived;
        this.defenderDefeated = defenderDefeated;
        this.attackerDefeated = attackerDefeated;
        this.attackerLevelledUp = attackerLevelledUp;
        this.experienceGained = experienceGained;
    }

    /** Restituisce l'unità attaccante. */
    public Unit getAttacker() { return attacker; }
    /** Restituisce l'unità difensore. */
    public Unit getDefender() { return defender; }
    /** Restituisce i danni inflitti al difensore. */
    public int getDamageDealt() { return damageDealt; }
    /** Restituisce i danni ricevuti dall'attaccante durante il contrattacco. */
    public int getDamageReceived() { return damageReceived; }
    /** Restituisce {@code true} se il difensore è stato sconfitto. */
    public boolean isDefenderDefeated() { return defenderDefeated; }
    /** Restituisce {@code true} se l'attaccante è stato sconfitto dal contrattacco. */
    public boolean isAttackerDefeated() { return attackerDefeated; }
    /** Restituisce {@code true} se l'attaccante ha guadagnato un livello. */
    public boolean isAttackerLevelledUp() { return attackerLevelledUp; }
    /** Restituisce l'esperienza guadagnata dall'attaccante. */
    public int getExperienceGained() { return experienceGained; }

    @Override
    public String toString() {
        return attacker.getName() + " ha inflitto " + damageDealt + " danni a " + defender.getName()
                + (defenderDefeated ? " [SCONFITTO]" : "")
                + " | danni ricevuti: " + damageReceived
                + (attackerLevelledUp ? " | LEVEL UP!" : "");
    }
}