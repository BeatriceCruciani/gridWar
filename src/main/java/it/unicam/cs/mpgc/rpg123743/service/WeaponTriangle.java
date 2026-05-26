package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.WeaponType;

/**
 * Implementa il sistema di vantaggio del triangolo delle armi.
 * SWORD batte AXE, AXE batte LANCE, LANCE batte SWORD.
 * BOW e MAGIC sono neutrali e non interagiscono con il triangolo.
 * Il vantaggio garantisce un moltiplicatore di danno +20%, lo svantaggio -20%.
 */
public class WeaponTriangle {

    private static final double ADVANTAGE_MULTIPLIER    = 1.2;
    private static final double DISADVANTAGE_MULTIPLIER = 0.8;
    private static final double NEUTRAL_MULTIPLIER      = 1.0;

    /**
     * Restituisce il moltiplicatore di danno per il tipo di arma dell'attaccante
     * contro il tipo di arma del difensore.
     *
     * @param attacker il tipo di arma dell'unità attaccante.
     * @param defender il tipo di arma dell'unità difensore.
     * @return 1.2 per vantaggio, 0.8 per svantaggio, 1.0 per neutro.
     */
    public double getMultiplier(WeaponType attacker, WeaponType defender) {
        if (attacker == null || defender == null) return NEUTRAL_MULTIPLIER;
        return switch (attacker) {
            case SWORD -> defender == WeaponType.AXE   ? ADVANTAGE_MULTIPLIER
                    : defender == WeaponType.LANCE ? DISADVANTAGE_MULTIPLIER
                    : NEUTRAL_MULTIPLIER;
            case AXE   -> defender == WeaponType.LANCE ? ADVANTAGE_MULTIPLIER
                    : defender == WeaponType.SWORD ? DISADVANTAGE_MULTIPLIER
                    : NEUTRAL_MULTIPLIER;
            case LANCE -> defender == WeaponType.SWORD ? ADVANTAGE_MULTIPLIER
                    : defender == WeaponType.AXE   ? DISADVANTAGE_MULTIPLIER
                    : NEUTRAL_MULTIPLIER;
            default    -> NEUTRAL_MULTIPLIER;
        };
    }

    /**
     * Restituisce {@code true} se l'attaccante ha vantaggio nel triangolo delle armi
     * contro il difensore.
     *
     * @param attacker il tipo di arma dell'attaccante.
     * @param defender il tipo di arma del difensore.
     * @return {@code true} se l'attaccante ha vantaggio.
     */
    public boolean hasAdvantage(WeaponType attacker, WeaponType defender) {
        return getMultiplier(attacker, defender) == ADVANTAGE_MULTIPLIER;
    }
}