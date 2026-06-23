package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Gestisce la risoluzione del combattimento tra due unità secondo le regole tattiche di GridWar.
 * L'attaccante effettua il primo assalto, seguito dal contrattacco del difensore se quest'ultimo
 * è ancora in vita ed è equipaggiato con un'arma con gittata sufficiente.
 * Questo service è stateless, indipendente dalla UI e completamente testabile in isolamento.
 */
public class CombatService {

    private static final int EXP_FOR_ATTACK = 10;
    private static final int EXP_FOR_KILL   = 40;
    private static final int MIN_DAMAGE     = 0;

    /**
     * Costruisce un nuovo CombatService.
     * Essendo le regole del triangolo integrate direttamente nell'enum WeaponType del modello,
     * questo servizio è ora completamente autonomo e privo di dipendenze esterne.
     */
    public CombatService() {
        // Costruttore vuoto e pulito
    }

    /**
     * Risolve uno scambio di combattimento completo tra un attaccante e un difensore sulla mappa.
     * Applica i modificatori di danno, consuma la durabilità delle armi e assegna l'esperienza.
     *
     * @param attacker l'unità che avvia l'offensiva (non nulla).
     * @param defender l'unità bersaglio del posizionamento (non nulla).
     * @param map      la mappa di battaglia corrente per il calcolo dei bonus difensivi del terreno (non nulla).
     * @return un {@link CombatResult} contenente il report dettagliato dello scontro.
     * @throws IllegalArgumentException se le unità non possono legalmente combattere o sono fuori gittata.
     * @throws NullPointerException se uno dei parametri in ingresso è nullo.
     */
    public CombatResult resolve(Unit attacker, Unit defender, BattleMap map) {
        Objects.requireNonNull(map, "BattleMap cannot be null during combat resolution.");
        validateCombatants(attacker, defender);

        Weapon attackerWeapon = attacker.getEquippedWeapon()
                .orElseThrow(() -> new IllegalStateException("Attacker must have a weapon equipped at this point."));

        // --- 1. FASE OFFENSIVA: L'attaccante colpisce ---
        int damageDealt = calculateDamage(attacker, defender, map);
        defender.takeDamage(damageDealt);
        attackerWeapon.use(); // Consuma la durabilità/usi dell'arma dell'attaccante

        boolean defenderDefeated = !defender.isAlive();

        // --- 2. FASE DIFENSIVA: Il difensore contrattacca (se idoneo) ---
        int damageReceived  = 0;
        boolean attackerDefeated = false;

        if (!defenderDefeated && canCounterAttack(attacker, defender)) {
            Weapon defenderWeapon = defender.getEquippedWeapon()
                    .orElseThrow(() -> new IllegalStateException("Defender must have a weapon equipped at this point."));
            damageReceived = calculateDamage(defender, attacker, map);
            attacker.takeDamage(damageReceived);
            defenderWeapon.use();
            attackerDefeated = !attacker.isAlive();
        }

        // --- 3. FASE PREMIAZIONE: Calcolo e assegnazione dell'esperienza ---
        int expGained  = defenderDefeated ? EXP_FOR_KILL : EXP_FOR_ATTACK;
        boolean levelledUp = attacker.gainExperience(expGained);

        // Segna l'unità come esausta per il turno corrente
        attacker.markAsActed();

        return new CombatResult(
                attacker, defender,
                damageDealt, damageReceived,
                defenderDefeated, attackerDefeated,
                levelledUp, expGained
        );
    }

    /**
     * Calcola il danno netto inflitto da un'unità attaccante ("striker") verso un bersaglio ("target").
     * Computa l'attacco base, il moltiplicatore del triangolo e sottrae le difese del bersaglio (terreno incluso).
     */
    private int calculateDamage(Unit striker, Unit target, BattleMap map) {
        Weapon weapon = striker.getEquippedWeapon()
                .orElseThrow(() -> new IllegalStateException("Striker must have a weapon equipped to deal damage."));
        Stats strikerStats = striker.getStats();
        Stats targetStats  = target.getStats();

        // Forza d'attacco totale dell'attaccante
        int baseAttack = strikerStats.getAttack() + weapon.getAttackBonus();

        // Calcolo del vantaggio tipologico sfruttando direttamente l'enum WeaponType
        WeaponType targetWeaponType = target.getEquippedWeapon().map(Weapon::getWeaponType).orElse(null);
        double triangleMultiplier = weapon.getWeaponType().getRelationAgainst(targetWeaponType).getMultiplier();

        // Selezione della statistica difensiva corretta (Resistenza contro Magia, Difesa contro il Fisico)
        int defence = (weapon.getWeaponType() == WeaponType.MAGIC)
                ? targetStats.getResistance()
                : targetStats.getDefence();

        // Recupero del bonus difensivo offerto dalla cella occupata dal difensore
        int terrainBonus = map.getCell(target.getPosition()).getTerrainType().getDefenceBonus();

        // Formula di danno finale: (Attacco * Moltiplicatore) - Difesa Totale
        int damage = (int) (baseAttack * triangleMultiplier) - defence - terrainBonus;
        return Math.max(MIN_DAMAGE, damage);
    }

    /**
     * Verifica se il difensore possiede i requisiti di gittata per rispondere all'attacco.
     */
    private boolean canCounterAttack(Unit attacker, Unit defender) {
        Optional<Weapon> defenderWeapon = defender.getEquippedWeapon();
        if (defenderWeapon.isEmpty()) return false;

        int distance = attacker.getPosition().distanceTo(defender.getPosition());
        return defenderWeapon.get().getRange() >= distance;
    }

    /**
     * Valida i requisiti formali di ingaggio per impedire azioni illegali a runtime.
     */
    private void validateCombatants(Unit attacker, Unit defender) {
        Objects.requireNonNull(attacker, "Attacker unit cannot be null.");
        Objects.requireNonNull(defender, "Defender unit cannot be null.");

        Weapon attackerWeapon = attacker.getEquippedWeapon()
                .orElseThrow(() -> new IllegalArgumentException(
                        "L'attaccante '" + attacker.getName() + "' non ha armi equipaggiate."));

        if (!attacker.isAlive() || !defender.isAlive()) {
            throw new IllegalArgumentException("Impossibile avviare uno scontro se uno dei partecipanti è già sconfitto.");
        }
        if (!attacker.isEnemy(defender)) {
            throw new IllegalArgumentException("Fuoco amico non consentito: le unità appartengono alla stessa fazione.");
        }

        int distance = attacker.getPosition().distanceTo(defender.getPosition());
        if (attackerWeapon.getRange() < distance) {
            throw new IllegalArgumentException("Attacco non valido: il difensore si trova fuori dalla gittata dell'arma ("
                    + distance + " caselle di distanza contro un range massimo di " + attackerWeapon.getRange() + ").");
        }
    }
}