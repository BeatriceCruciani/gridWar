package it.unicam.cs.mpgc.rpg123743;

import it.unicam.cs.mpgc.rpg123743.model.*;
import it.unicam.cs.mpgc.rpg123743.service.CombatService;
import it.unicam.cs.mpgc.rpg123743.service.WeaponTriangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CombatService")
class CombatServiceTest {

    private CombatService combatService;
    private BattleMap map;

    @BeforeEach
    void setUp() {
        combatService = new CombatService(new WeaponTriangle());
        map = buildTestMap();
    }

    @Test
    @DisplayName("Attacker deals positive damage to defender")
    void attackerDealsDamage() {
        Unit attacker = buildUnit("Hero",  Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 12, 5, 3, 6, 4), new Position(0, 0), WeaponType.SWORD);
        Unit defender = buildUnit("Enemy", Faction.ENEMY, UnitClass.WARRIOR,
                new Stats(30, 10, 5, 3, 5, 4), new Position(0, 1), WeaponType.AXE);
        map.placeUnit(attacker);
        map.placeUnit(defender);

        CombatResult result = combatService.resolve(attacker, defender, map);

        assertTrue(result.getDamageDealt() > 0);
    }

    @Test
    @DisplayName("Weapon triangle advantage increases damage compared to disadvantage")
    void weaponTriangleAdvantageIncreasesDamage() {
        Unit sword1  = buildUnit("Sword1", Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 10, 0, 0, 5, 4), new Position(0, 0), WeaponType.SWORD);
        Unit axeUser = buildUnit("Axe",    Faction.ENEMY,  UnitClass.WARRIOR,
                new Stats(30, 10, 0, 0, 5, 4), new Position(0, 1), WeaponType.AXE);
        map.placeUnit(sword1);
        map.placeUnit(axeUser);
        CombatResult advantageResult = combatService.resolve(sword1, axeUser, map);

        BattleMap map2   = buildTestMap();
        Unit sword2      = buildUnit("Sword2", Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 10, 0, 0, 5, 4), new Position(0, 0), WeaponType.SWORD);
        Unit lanceUser   = buildUnit("Lance",  Faction.ENEMY,  UnitClass.WARRIOR,
                new Stats(30, 10, 0, 0, 5, 4), new Position(0, 1), WeaponType.LANCE);
        map2.placeUnit(sword2);
        map2.placeUnit(lanceUser);
        CombatResult disadvantageResult = combatService.resolve(sword2, lanceUser, map2);

        assertTrue(advantageResult.getDamageDealt() > disadvantageResult.getDamageDealt());
    }

    @Test
    @DisplayName("Defender counter-attacks when in range")
    void defenderCounterAttacks() {
        Unit attacker = buildUnit("Hero",  Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 10, 0, 0, 5, 4), new Position(0, 0), WeaponType.SWORD);
        Unit defender = buildUnit("Enemy", Faction.ENEMY,  UnitClass.WARRIOR,
                new Stats(30, 10, 0, 0, 5, 4), new Position(0, 1), WeaponType.SWORD);
        map.placeUnit(attacker);
        map.placeUnit(defender);

        CombatResult result = combatService.resolve(attacker, defender, map);

        assertTrue(result.getDamageReceived() > 0);
    }

    @Test
    @DisplayName("Defeated defender is marked as not alive")
    void defeatedDefenderIsNotAlive() {
        Unit attacker = buildUnit("Hero",  Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 100, 0, 0, 5, 4), new Position(0, 0), WeaponType.SWORD);
        Unit defender = buildUnit("Enemy", Faction.ENEMY,  UnitClass.WARRIOR,
                new Stats(1, 1, 0, 0, 5, 4),   new Position(0, 1), WeaponType.SWORD);
        map.placeUnit(attacker);
        map.placeUnit(defender);

        CombatResult result = combatService.resolve(attacker, defender, map);

        assertTrue(result.isDefenderDefeated());
        assertFalse(defender.isAlive());
    }

    @Test
    @DisplayName("Attacking a friendly unit throws IllegalArgumentException")
    void cannotAttackFriendly() {
        Unit a = buildUnit("A", Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 10, 5, 3, 5, 4), new Position(0, 0), WeaponType.SWORD);
        Unit b = buildUnit("B", Faction.PLAYER, UnitClass.WARRIOR,
                new Stats(30, 10, 5, 3, 5, 4), new Position(0, 1), WeaponType.SWORD);
        map.placeUnit(a);
        map.placeUnit(b);

        assertThrows(IllegalArgumentException.class, () -> combatService.resolve(a, b, map));
    }

    // --- Helpers ---

    private BattleMap buildTestMap() {
        BattleMap m = new BattleMap("Test Map", 5, 5);
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                m.setCell(new Cell(new Position(r, c), TerrainType.PLAIN));
        return m;
    }

    private Unit buildUnit(String name, Faction faction, UnitClass unitClass,
                           Stats stats, Position pos, WeaponType weaponType) {
        Unit unit   = new Unit(name, faction, unitClass, stats, pos);
        Weapon weapon = new Weapon("Test Weapon", "A test weapon.", 99, weaponType, 5, 1);
        unit.addItem(weapon);
        unit.equipWeapon(weapon);
        return unit;
    }
}