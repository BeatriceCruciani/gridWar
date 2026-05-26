package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;

/**
 * Gestisce la risoluzione del combattimento tra due unità.
 * Dopo che l'attaccante colpisce, il difensore contrattacca se è in gittata.
 * L'esperienza viene assegnata all'attaccante in base all'esito.
 * Questo service è indipendente dalla UI ed è completamente testabile in isolamento.
 */
public class CombatService {

    private static final int EXP_FOR_ATTACK = 10;
    private static final int EXP_FOR_KILL   = 40;
    private static final int MIN_DAMAGE     = 0;

    private final WeaponTriangle weaponTriangle;

    /**
     * Costruisce un nuovo CombatService con il triangolo delle armi specificato.
     *
     * @param weaponTriangle il triangolo delle armi da usare per i calcoli.
     * @throws IllegalArgumentException se weaponTriangle è nullo.
     */
    public CombatService(WeaponTriangle weaponTriangle) {
        if (weaponTriangle == null) throw new IllegalArgumentException("WeaponTriangle must not be null.");
        this.weaponTriangle = weaponTriangle;
    }

    /**
     * Risolve uno scambio di combattimento completo tra attaccante e difensore.
     * L'attaccante colpisce per primo; il difensore contrattacca se in gittata.
     *
     * @param attacker l'unità che inizia il combattimento.
     * @param defender l'unità che riceve l'attacco.
     * @param map      la mappa di battaglia, usata per i bonus del terreno.
     * @return un CombatResult che descrive l'esito completo dello scambio.
     * @throws IllegalArgumentException se una delle unità non ha arma equipaggiata,
     *                                  se una è già sconfitta, o se appartengono alla stessa fazione.
     */
    public CombatResult resolve(Unit attacker, Unit defender, BattleMap map) {
        validateCombatants(attacker, defender);

        Weapon attackerWeapon = attacker.getEquippedWeapon();
        Weapon defenderWeapon = defender.getEquippedWeapon();

        // --- L'attaccante colpisce ---
        int damageDealt = calculateDamage(attacker, defender, map);
        defender.getStats().applyDamage(damageDealt);
        attackerWeapon.use();

        boolean defenderDefeated = !defender.isAlive();

        // --- Il difensore contrattacca se ancora in vita e in gittata ---
        int damageReceived  = 0;
        boolean attackerDefeated = false;

        if (!defenderDefeated && defenderWeapon != null && canCounterAttack(attacker, defender)) {
            damageReceived = calculateDamage(defender, attacker, map);
            attacker.getStats().applyDamage(damageReceived);
            defenderWeapon.use();
            attackerDefeated = !attacker.isAlive();
        }

        // --- Esperienza ---
        int expGained  = defenderDefeated ? EXP_FOR_KILL : EXP_FOR_ATTACK;
        boolean levelledUp = attacker.gainExperience(expGained);

        attacker.markAsActed();

        return new CombatResult(
                attacker, defender,
                damageDealt, damageReceived,
                defenderDefeated, attackerDefeated,
                levelledUp, expGained
        );
    }

    /**
     * Calcola i danni inflitti da un'unità a un bersaglio, applicando
     * il moltiplicatore del triangolo delle armi e il bonus difensivo del terreno.
     * Il danno minimo è zero.
     */
    private int calculateDamage(Unit striker, Unit target, BattleMap map) {
        Weapon weapon       = striker.getEquippedWeapon();
        Stats strikerStats  = striker.getStats();
        Stats targetStats   = target.getStats();

        int baseAttack = strikerStats.getAttack() + weapon.getAttackBonus();

        double triangleMultiplier = weaponTriangle.getMultiplier(
                weapon.getWeaponType(),
                target.getEquippedWeapon() != null ? target.getEquippedWeapon().getWeaponType() : null
        );

        int defence = weapon.getWeaponType() == WeaponType.MAGIC
                ? targetStats.getResistance()
                : targetStats.getDefence();

        int terrainBonus = map.getCell(target.getPosition()).getTerrainType().getDefenceBonus();

        int damage = (int) (baseAttack * triangleMultiplier) - defence - terrainBonus;
        return Math.max(MIN_DAMAGE, damage);
    }

    /**
     * Restituisce {@code true} se il difensore può contrattaccare l'attaccante,
     * in base alla gittata dell'arma del difensore e alla distanza tra le unità.
     */
    private boolean canCounterAttack(Unit attacker, Unit defender) {
        Weapon defenderWeapon = defender.getEquippedWeapon();
        if (defenderWeapon == null) return false;
        int distance = attacker.getPosition().distanceTo(defender.getPosition());
        return defenderWeapon.getRange() >= distance;
    }

    /**
     * Valida che le due unità possano combattere tra loro.
     *
     * @throws IllegalArgumentException se le condizioni di combattimento non sono valide.
     */
    private void validateCombatants(Unit attacker, Unit defender) {
        if (attacker.getEquippedWeapon() == null) {
            throw new IllegalArgumentException(attacker.getName() + " non ha un'arma equipaggiata.");
        }
        if (!attacker.isAlive() || !defender.isAlive()) {
            throw new IllegalArgumentException("Non si può combattere con un'unità sconfitta.");
        }
        if (!attacker.isEnemy(defender)) {
            throw new IllegalArgumentException("Non si può attaccare un'unità della stessa fazione.");
        }
    }
}