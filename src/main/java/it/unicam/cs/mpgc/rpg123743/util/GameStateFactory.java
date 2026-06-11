package it.unicam.cs.mpgc.rpg123743.util;

import it.unicam.cs.mpgc.rpg123743.model.*;

/**
 * Factory che costruisce lo stato iniziale del gioco per ogni livello.
 * Centralizza tutti i dati di mappa e unità in un unico posto.
 * In una release futura i dati potrebbero essere caricati da file JSON esterni.
 */
public class GameStateFactory {

    private GameStateFactory() { /* classe di utilità */ }

    /**
     * Crea e restituisce lo stato di gioco per il livello specificato.
     *
     * @param level il livello da caricare.
     * @return il GameState iniziale pronto per essere usato.
     */
    public static GameState createGame(MapLevel level) {
        return switch (level) {
            case ASHBORNE_PLAINS -> createAshbornePlains();
            case FORT_SIEGE      -> createFortSiege();
            case FROZEN_PASS     -> createFrozenPass();
        };
    }

    // =========================================================
    // MAPPA 1 — Ashborne Plains
    // =========================================================

    private static GameState createAshbornePlains() {
        BattleMap map = new BattleMap("Ashborne Plains", 10, 10);
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                map.setCell(new Cell(new Position(r, c), ashborneTerrain(r, c)));

        GameState state = new GameState("autosave", map);
        placeAshbornePlayerUnits(map);
        placeAshborneEnemyUnits(map);
        return state;
    }

    private static TerrainType ashborneTerrain(int r, int c) {
        if ((r == 4 || r == 5) && (c == 4 || c == 5)) return TerrainType.WALL;
        if (r <= 3 && c >= 6) return TerrainType.FOREST;
        if (r >= 7 && c <= 2) return TerrainType.MOUNTAIN;
        if (r == 5 && c == 8) return TerrainType.FORT;
        return TerrainType.PLAIN;
    }

    private static void placeAshbornePlayerUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Arthur", Faction.PLAYER, new Position(8, 0)));
        placeUnit(map, buildMage("Lyra",     Faction.PLAYER, new Position(9, 1)));
        placeUnit(map, buildArcher("Robin",  Faction.PLAYER, new Position(8, 2)));
        placeUnit(map, buildKnight("Gareth", Faction.PLAYER, new Position(9, 0)));
        placeUnit(map, buildThief("Selene",  Faction.PLAYER, new Position(7, 1)));
    }

    private static void placeAshborneEnemyUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Dark Soldier", Faction.ENEMY, new Position(1, 9)));
        placeUnit(map, buildArcher("Dark Archer",   Faction.ENEMY, new Position(0, 7)));
        placeUnit(map, buildMage("Dark Mage",       Faction.ENEMY, new Position(1, 8)));
        placeUnit(map, buildKnight("Dark Knight",   Faction.ENEMY, new Position(0, 9)));
    }

    // =========================================================
    // MAPPA 2 — Fort Siege
    // =========================================================

    private static GameState createFortSiege() {
        BattleMap map = new BattleMap("Fort Siege", 10, 10);
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                map.setCell(new Cell(new Position(r, c), fortSiegeTerrain(r, c)));

        GameState state = new GameState("autosave", map);
        placeFortSiegePlayerUnits(map);
        placeFortSiegeEnemyUnits(map);
        return state;
    }

    private static TerrainType fortSiegeTerrain(int r, int c) {
        // Mura esterne del forte
        if (r == 2 && c >= 3 && c <= 7) return TerrainType.WALL;
        if (r == 6 && c >= 3 && c <= 7) return TerrainType.WALL;
        if (c == 3 && r >= 2 && r <= 6) return TerrainType.WALL;
        if (c == 7 && r >= 2 && r <= 6) return TerrainType.WALL;
        // Ingresso del forte
        if (r == 6 && c == 5) return TerrainType.PLAIN;
        // Interno del forte con forti
        if (r >= 3 && r <= 5 && c >= 4 && c <= 6) return TerrainType.FORT;
        // Foresta ai lati
        if (c <= 1) return TerrainType.FOREST;
        if (c >= 8) return TerrainType.FOREST;
        return TerrainType.PLAIN;
    }

    private static void placeFortSiegePlayerUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Arthur", Faction.PLAYER, new Position(9, 4)));
        placeUnit(map, buildMage("Lyra",     Faction.PLAYER, new Position(9, 6)));
        placeUnit(map, buildArcher("Robin",  Faction.PLAYER, new Position(8, 3)));
        placeUnit(map, buildKnight("Gareth", Faction.PLAYER, new Position(9, 5)));
        placeUnit(map, buildThief("Selene",  Faction.PLAYER, new Position(8, 7)));
    }

    private static void placeFortSiegeEnemyUnits(BattleMap map) {
        // Guardie esterne
        placeUnit(map, buildWarrior("Guard",        Faction.ENEMY, new Position(7, 4)));
        placeUnit(map, buildWarrior("Guard",        Faction.ENEMY, new Position(7, 6)));
        placeUnit(map, buildArcher("Tower Archer",  Faction.ENEMY, new Position(2, 4)));
        placeUnit(map, buildArcher("Tower Archer",  Faction.ENEMY, new Position(2, 6)));
        // Guardie interne
        placeUnit(map, buildKnight("Fort Knight",   Faction.ENEMY, new Position(4, 4)));
        placeUnit(map, buildKnight("Fort Knight",   Faction.ENEMY, new Position(4, 6)));
        // Comandante
        placeUnit(map, buildWarrior("Commander",    Faction.ENEMY, new Position(3, 5)));
    }

    // =========================================================
    // MAPPA 3 — Frozen Pass
    // =========================================================

    private static GameState createFrozenPass() {
        BattleMap map = new BattleMap("Frozen Pass", 10, 10);
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                map.setCell(new Cell(new Position(r, c), frozenPassTerrain(r, c)));

        GameState state = new GameState("autosave", map);
        placeFrozenPassPlayerUnits(map);
        placeFrozenPassEnemyUnits(map);
        return state;
    }

    private static TerrainType frozenPassTerrain(int r, int c) {
        // Montagne ai bordi — corridoio centrale
        if (c == 0 || c == 9) return TerrainType.MOUNTAIN;
        if (c == 1 && r != 5) return TerrainType.MOUNTAIN;
        if (c == 8 && r != 4) return TerrainType.MOUNTAIN;
        // Foreste nel corridoio
        if ((r == 2 || r == 7) && c >= 2 && c <= 7) return TerrainType.FOREST;
        // Muri come rocce
        if (r == 4 && (c == 3 || c == 4)) return TerrainType.WALL;
        if (r == 5 && (c == 5 || c == 6)) return TerrainType.WALL;
        // Un forte a metà strada
        if (r == 5 && c == 2) return TerrainType.FORT;
        return TerrainType.PLAIN;
    }

    private static void placeFrozenPassPlayerUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Arthur", Faction.PLAYER, new Position(9, 2)));
        placeUnit(map, buildMage("Lyra",     Faction.PLAYER, new Position(9, 4)));
        placeUnit(map, buildArcher("Robin",  Faction.PLAYER, new Position(9, 6)));
        placeUnit(map, buildKnight("Gareth", Faction.PLAYER, new Position(9, 3)));
        placeUnit(map, buildThief("Selene",  Faction.PLAYER, new Position(9, 5)));
    }

    private static void placeFrozenPassEnemyUnits(BattleMap map) {
        // Prima linea
        placeUnit(map, buildWarrior("Scout",        Faction.ENEMY, new Position(1, 3)));
        placeUnit(map, buildWarrior("Scout",        Faction.ENEMY, new Position(1, 5)));
        placeUnit(map, buildArcher("Sniper",        Faction.ENEMY, new Position(1, 7)));
        // Seconda linea
        placeUnit(map, buildKnight("Ice Knight",    Faction.ENEMY, new Position(3, 2)));
        placeUnit(map, buildKnight("Ice Knight",    Faction.ENEMY, new Position(3, 7)));
        placeUnit(map, buildMage("Frost Mage",      Faction.ENEMY, new Position(3, 4)));
        placeUnit(map, buildMage("Frost Mage",      Faction.ENEMY, new Position(3, 6)));
        // Terza linea
        placeUnit(map, buildWarrior("Elite Guard",  Faction.ENEMY, new Position(0, 2)));
        placeUnit(map, buildWarrior("Elite Guard",  Faction.ENEMY, new Position(0, 5)));
        placeUnit(map, buildArcher("Elite Archer",  Faction.ENEMY, new Position(0, 7)));
        placeUnit(map, buildThief("Shadow",         Faction.ENEMY, new Position(2, 4)));
        placeUnit(map, buildKnight("Warlord",       Faction.ENEMY, new Position(0, 4)));
    }

    // =========================================================
    // Builder delle unità — condivisi tra tutte le mappe
    // =========================================================

    private static Unit buildWarrior(String name, Faction faction, Position pos) {
        Stats stats = new Stats(30, 12, 8, 3, 6, 4);
        Unit unit = new Unit(name, faction, UnitClass.WARRIOR, stats, pos);
        Weapon sword = new Weapon("Iron Sword", "A sturdy iron sword.", 30, WeaponType.SWORD, 5, 1);
        unit.addItem(sword);
        unit.equipWeapon(sword);
        return unit;
    }

    private static Unit buildMage(String name, Faction faction, Position pos) {
        Stats stats = new Stats(20, 14, 3, 8, 8, 4);
        Unit unit = new Unit(name, faction, UnitClass.MAGE, stats, pos);
        Weapon tome = new Weapon("Fire Tome", "A basic fire tome.", 25, WeaponType.MAGIC, 6, 2);
        unit.addItem(tome);
        unit.equipWeapon(tome);
        return unit;
    }

    private static Unit buildArcher(String name, Faction faction, Position pos) {
        Stats stats = new Stats(22, 11, 5, 4, 9, 5);
        Unit unit = new Unit(name, faction, UnitClass.ARCHER, stats, pos);
        Weapon bow = new Weapon("Iron Bow", "A reliable bow.", 30, WeaponType.BOW, 5, 2);
        unit.addItem(bow);
        unit.equipWeapon(bow);
        return unit;
    }

    private static Unit buildKnight(String name, Faction faction, Position pos) {
        Stats stats = new Stats(28, 10, 12, 4, 5, 3);
        Unit unit = new Unit(name, faction, UnitClass.KNIGHT, stats, pos);
        Weapon lance = new Weapon("Iron Lance", "A heavy iron lance.", 30, WeaponType.LANCE, 5, 1);
        unit.addItem(lance);
        unit.equipWeapon(lance);
        return unit;
    }

    private static Unit buildThief(String name, Faction faction, Position pos) {
        Stats stats = new Stats(18, 9, 4, 4, 13, 6);
        Unit unit = new Unit(name, faction, UnitClass.THIEF, stats, pos);
        Weapon dagger = new Weapon("Dagger", "A swift dagger.", 35, WeaponType.SWORD, 3, 1);
        unit.addItem(dagger);
        unit.equipWeapon(dagger);
        Consumable potion = new Consumable("Vulnerary", "Restores 10 HP.", 3, 10);
        unit.addItem(potion);
        return unit;
    }

    private static void placeUnit(BattleMap map, Unit unit) {
        map.placeUnit(unit);
    }
}