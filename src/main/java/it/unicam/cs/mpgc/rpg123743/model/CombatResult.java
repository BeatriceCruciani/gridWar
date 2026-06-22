package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta l'esito di uno scambio di combattimento tra due unità.
 * Restituito da {@code CombatService} dopo la risoluzione di un attacco.
 * La UI utilizza questo oggetto per mostrare il feedback del combattimento al giocatore.
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
public record CombatResult(
        Unit attacker,
        Unit defender,
        int damageDealt,
        int damageReceived,
        boolean defenderDefeated,
        boolean attackerDefeated,
        boolean attackerLevelledUp,
        int experienceGained
) {

    /**
     * Valida i parametri del record al momento della costruzione.
     * Garantisce che i riferimenti alle unità non siano nulli e che i valori
     * numerici (danni, esperienza) non siano negativi.
     *
     * @throws NullPointerException se attacker o defender sono nulli.
     * @throws IllegalArgumentException se damageDealt, damageReceived o experienceGained sono negativi.
     */
    public CombatResult {
        Objects.requireNonNull(attacker, "Attacker cannot be null.");
        Objects.requireNonNull(defender, "Defender cannot be null.");
        if (damageDealt < 0 || damageReceived < 0 || experienceGained < 0) {
            throw new IllegalArgumentException("Damage and experience values must be non-negative.");
        }
    }

    @Override
    public String toString() {
        return attacker.getName() + " ha inflitto " + damageDealt + " danni a " + defender.getName()
                + (defenderDefeated ? " [SCONFITTO]" : "")
                + " | danni ricevuti: " + damageReceived
                + (attackerDefeated ? " [ATTACCANTE SCONFITTO]" : "")
                + (attackerLevelledUp ? " | LEVEL UP!" : "");
    }
}