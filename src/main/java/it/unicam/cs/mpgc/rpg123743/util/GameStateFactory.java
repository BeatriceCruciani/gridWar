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
            case WALL_BREACH      -> createWallBreach();
            case TANGLED_HIGHLANDS     -> createTangledHiglands();
        };
    }

    // MAPPA 1 — Ashborne Plains
    private static GameState createAshbornePlains() {
        BattleMap map = new BattleMap("Ashborne Plains", 10, 10);
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                map.setCell(new Cell(new Position(r, c), ashborneTerrain(r, c)));

        GameState state = new GameState("autosave Ashborne Plains", map);
        placeAshbornePlayerUnits(map);
        placeAshborneEnemyUnits(map);
        return state;
    }

    private static TerrainType ashborneTerrain(int r, int c) {
        if (r <= 3 && c >= 6) return TerrainType.FOREST;
        if (r >= 7 && c <= 2) return TerrainType.MOUNTAIN;
        return TerrainType.PLAIN;
    }

    private static void placeAshbornePlayerUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Lucina", Faction.PLAYER, new Position(8, 0)));
        placeUnit(map, buildMage("Robin",     Faction.PLAYER, new Position(9, 1)));
        placeUnit(map, buildArcher("Niles",  Faction.PLAYER, new Position(8, 2)));
        placeUnit(map, buildKnight("Xander", Faction.PLAYER, new Position(9, 0)));
        placeUnit(map, buildThief("Anna",  Faction.PLAYER, new Position(7, 1)));
        placeUnit(map, buildHealer("Lissa",  Faction.PLAYER, new Position(9, 2)));
    }

    private static void placeAshborneEnemyUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Dark Soldier", Faction.ENEMY, new Position(1, 9)));
        placeUnit(map, buildArcher("Dark Archer",   Faction.ENEMY, new Position(0, 7)));
        placeUnit(map, buildMage("Dark Mage",       Faction.ENEMY, new Position(1, 8)));
        placeUnit(map, buildKnight("Dark Knight",   Faction.ENEMY, new Position(0, 9)));
    }

    // MAPPA 2 — Wall Breach
    private static GameState createWallBreach() {
        BattleMap map = new BattleMap("Wall Breach", 10, 10);
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                map.setCell(new Cell(new Position(r, c), wallBreachTerrain(r, c)));

        GameState state = new GameState("autosave Wall Breach", map);
        placeWallBreachPlayerUnits(map);
        placeWallBreachEnemyUnits(map);
        return state;
    }

    private static TerrainType wallBreachTerrain(int r, int c) {
        if (c == 0 || c == 9) return TerrainType.MOUNTAIN;
        if (r == 5) {
            if (c == 3 || c == 6) return TerrainType.BREAKABLE_WALL;
            return TerrainType.WALL;
        }
        return TerrainType.PLAIN;
    }

    private static void placeWallBreachPlayerUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Lucina", Faction.PLAYER, new Position(8, 3)));
        placeUnit(map, buildMage("Robin",     Faction.PLAYER, new Position(8, 6)));
        placeUnit(map, buildArcher("Niles",  Faction.PLAYER, new Position(9, 4)));
        placeUnit(map, buildKnight("Xander", Faction.PLAYER, new Position(8, 4)));
        placeUnit(map, buildThief("Anna",  Faction.PLAYER, new Position(9, 5)));
        placeUnit(map, buildHealer("Lissa",  Faction.PLAYER, new Position(9, 6)));
    }

    private static void placeWallBreachEnemyUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Guard",        Faction.ENEMY, new Position(4, 3)));
        placeUnit(map, buildWarrior("Guard",        Faction.ENEMY, new Position(4, 6)));
        placeUnit(map, buildArcher("Tower Archer",  Faction.ENEMY, new Position(2, 3)));
        placeUnit(map, buildArcher("Tower Archer",  Faction.ENEMY, new Position(2, 6)));
        placeUnit(map, buildKnight("Fort Knight",   Faction.ENEMY, new Position(3, 4)));
        placeUnit(map, buildKnight("Fort Knight",   Faction.ENEMY, new Position(3, 5)));
        placeUnit(map, buildWarrior("Commander",    Faction.ENEMY, new Position(1, 5)));
    }

    // MAPPA 3 — tangled Highlands
    private static GameState createTangledHiglands() {
        BattleMap map = new BattleMap("Tangled Higlands", 10, 10);
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                map.setCell(new Cell(new Position(r, c), TangledHiglandsTerrain(r, c)));

        GameState state = new GameState("autosave Tangled Higlands", map);
        placeTangledHiglandsPlayerUnits(map);
        placeTangledHiglandsEnemyUnits(map);
        return state;
    }

    private static TerrainType TangledHiglandsTerrain(int r, int c) {
        if ((r + c) % 5 == 0) return TerrainType.FOREST;
        if ((2 * r + c) % 7 == 0) return TerrainType.MOUNTAIN;
        return TerrainType.PLAIN;
    }

    private static void placeTangledHiglandsPlayerUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Lucina", Faction.PLAYER, new Position(9, 1)));
        placeUnit(map, buildMage("Robin",     Faction.PLAYER, new Position(9, 3)));
        placeUnit(map, buildArcher("Niles",  Faction.PLAYER, new Position(9, 5)));
        placeUnit(map, buildKnight("Xander", Faction.PLAYER, new Position(9, 7)));
        placeUnit(map, buildThief("Anna",  Faction.PLAYER, new Position(9, 8)));
        placeUnit(map, buildHealer("Lissa",  Faction.PLAYER, new Position(9, 4)));
    }

    private static void placeTangledHiglandsEnemyUnits(BattleMap map) {
        placeUnit(map, buildWarrior("Scout",        Faction.ENEMY, new Position(1, 1)));
        placeUnit(map, buildWarrior("Scout",        Faction.ENEMY, new Position(1, 3)));
        placeUnit(map, buildWarrior("Scout",        Faction.ENEMY, new Position(1, 5)));
        placeUnit(map, buildArcher("Sniper",        Faction.ENEMY, new Position(1, 7)));
        placeUnit(map, buildArcher("Sniper",        Faction.ENEMY, new Position(1, 8)));
        placeUnit(map, buildKnight("Ice Knight",    Faction.ENEMY, new Position(3, 0)));
        placeUnit(map, buildKnight("Ice Knight",    Faction.ENEMY, new Position(3, 2)));
        placeUnit(map, buildKnight("Ice Knight",    Faction.ENEMY, new Position(3, 8)));
        placeUnit(map, buildMage("Frost Mage",      Faction.ENEMY, new Position(3, 4)));
        placeUnit(map, buildMage("Frost Mage",      Faction.ENEMY, new Position(3, 6)));
        placeUnit(map, buildWarrior("Elite Guard",  Faction.ENEMY, new Position(0, 2)));
        placeUnit(map, buildWarrior("Elite Guard",  Faction.ENEMY, new Position(0, 5)));
        placeUnit(map, buildArcher("Elite Archer",  Faction.ENEMY, new Position(0, 7)));
        placeUnit(map, buildThief("Shadow",         Faction.ENEMY, new Position(2, 4)));
        placeUnit(map, buildThief("Shadow",         Faction.ENEMY, new Position(2, 9)));
        placeUnit(map, buildKnight("Warlord",       Faction.ENEMY, new Position(0, 4)));
    }

    // Builder delle unità
    private static Unit buildWarrior(String name, Faction faction, Position pos) {
        Stats stats = new Stats(30, 12, 8, 3, 6, 3);
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
        Consumable potion = new HealingPotion("Vulnerary", "Restores 10 HP.", 3, 10);
        unit.addItem(potion);
        return unit;
    }

    private static Unit buildArcher(String name, Faction faction, Position pos) {
        Stats stats = new Stats(22, 11, 5, 4, 9, 4);
        Unit unit = new Unit(name, faction, UnitClass.ARCHER, stats, pos);
        Weapon bow = new Weapon("Iron Bow", "A reliable bow.", 30, WeaponType.BOW, 5, 2);
        unit.addItem(bow);
        unit.equipWeapon(bow);
        Consumable potion = new HealingPotion("Vulnerary", "Restores 10 HP.", 3, 10);
        unit.addItem(potion);
        return unit;
    }

    private static Unit buildKnight(String name, Faction faction, Position pos) {
        Stats stats = new Stats(28, 10, 12, 4, 5, 5);
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
        Consumable potion = new HealingPotion("Vulnerary", "Restores 10 HP.", 3, 10);
        unit.addItem(potion);

        return unit;
    }

    /**
     * Costruisce un'unità Healer, dedicata al supporto del gruppo.
     * Statistiche orientate a resistenza magica e velocità, con difesa fisica
     * e attacco contenuti. È equipaggiata con un bastone, usato da
     * {@code BattleController} per curare un alleato a distanza invece di
     * infliggere danno (vedi {@link WeaponType#STAFF}).
     */
    private static Unit buildHealer(String name, Faction faction, Position pos) {
        Stats stats = new Stats(20, 8, 2, 9, 7, 5);
        Unit unit = new Unit(name, faction, UnitClass.HEALER, stats, pos);
        Weapon staff = new Weapon("Healing Staff", "A staff imbued with restorative magic.", 30, WeaponType.STAFF, 8, 2);
        unit.addItem(staff);
        unit.equipWeapon(staff);
        Consumable potion = new HealingPotion("Vulnerary", "Restores 10 HP.", 3, 10);
        unit.addItem(potion);
        return unit;
    }

    private static void placeUnit(BattleMap map, Unit unit) {
        map.placeUnit(unit);
    }
}