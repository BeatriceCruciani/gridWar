package it.unicam.cs.mpgc.rpg123743.util;

import it.unicam.cs.mpgc.rpg123743.model.*;

/**
 * Factory che costruisce lo stato iniziale del gioco per una nuova partita.
 * Crea la mappa di battaglia, posiziona il terreno, crea le unità con
 * le loro statistiche e l'equipaggiamento iniziale, e le posiziona sulla griglia.
 * Questa classe centralizza tutti i dati di gioco in un unico posto.
 * In una release futura, i dati di mappa e unità potrebbero essere caricati
 * da file JSON esterni per supportare più livelli e un editor di mappe.
 */
public class GameStateFactory {

    private GameStateFactory() { /* classe di utilità, non istanziabile */ }

    /**
     * Crea e restituisce lo stato di gioco predefinito per la prima battaglia.
     * Mappa: griglia 10x10 con terreno misto.
     * Unità giocatore: Guerriero, Mago, Arciere, Cavaliere, Ladro.
     * Unità nemiche: 4 nemici di classi diverse.
     *
     * @return il GameState iniziale pronto per essere usato.
     */
    public static GameState createDefaultGame() {
        BattleMap map = buildMap();
        GameState state = new GameState("autosave", map);
        placePlayerUnits(map);
        placeEnemyUnits(map);
        return state;
    }

    //Mappa

    private static BattleMap buildMap() {
        BattleMap map = new BattleMap("Ashborne Plains", 10, 10);
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                TerrainType terrain = assignTerrain(r, c);
                map.setCell(new Cell(new Position(r, c), terrain));
            }
        }
        return map;
    }

    private static TerrainType assignTerrain(int r, int c) {
        if ((r == 4 || r == 5) && (c == 4 || c == 5)) return TerrainType.WALL;
        if (r <= 3 && c >= 6) return TerrainType.FOREST;
        if (r >= 7 && c <= 2) return TerrainType.MOUNTAIN;
        if (r == 5 && c == 8) return TerrainType.FORT;
        return TerrainType.PLAIN;
    }

    //Unità giocatore

    private static void placePlayerUnits(BattleMap map) {
        Unit warrior = buildWarrior("Chrom", Faction.PLAYER, new Position(8, 0));
        Unit mage    = buildMage("Robin",     Faction.PLAYER, new Position(9, 1));
        Unit archer  = buildArcher("Lyn",  Faction.PLAYER, new Position(8, 2));
        Unit knight  = buildKnight("Ephraim", Faction.PLAYER, new Position(9, 0));
        Unit thief   = buildThief("Selene",  Faction.PLAYER, new Position(7, 1));
        for (Unit unit : new Unit[]{warrior, mage, archer, knight, thief}) {
            map.placeUnit(unit);
        }
    }

    //Unità nemiche

    private static void placeEnemyUnits(BattleMap map) {
        Unit e1 = buildWarrior("Dark Soldier", Faction.ENEMY, new Position(1, 9));
        Unit e2 = buildArcher("Dark Archer",   Faction.ENEMY, new Position(0, 7));
        Unit e3 = buildMage("Dark Mage",       Faction.ENEMY, new Position(1, 8));
        Unit e4 = buildKnight("Dark Knight",   Faction.ENEMY, new Position(0, 9));
        for (Unit unit : new Unit[]{e1, e2, e3, e4}) {
            map.placeUnit(unit);
        }
    }

    //Builder delle unità

    private static Unit buildWarrior(String name, Faction faction, Position pos) {
        Stats stats = new Stats(30, 12, 8, 3, 6, 4);
        Unit unit = new Unit(name, faction, UnitClass.WARRIOR, stats, pos);
        Weapon sword = new Weapon("Iron Sword", "Una robusta spada di ferro.", 30, WeaponType.SWORD, 5, 1);
        unit.addItem(sword);
        unit.equipWeapon(sword);
        return unit;
    }

    private static Unit buildMage(String name, Faction faction, Position pos) {
        Stats stats = new Stats(20, 14, 3, 8, 8, 4);
        Unit unit = new Unit(name, faction, UnitClass.MAGE, stats, pos);
        Weapon tome = new Weapon("Fire Tome", "Un tomo di fuoco elementare.", 25, WeaponType.MAGIC, 6, 2);
        unit.addItem(tome);
        unit.equipWeapon(tome);
        return unit;
    }

    private static Unit buildArcher(String name, Faction faction, Position pos) {
        Stats stats = new Stats(22, 11, 5, 4, 9, 5);
        Unit unit = new Unit(name, faction, UnitClass.ARCHER, stats, pos);
        Weapon bow = new Weapon("Iron Bow", "Un arco di ferro affidabile.", 30, WeaponType.BOW, 5, 2);
        unit.addItem(bow);
        unit.equipWeapon(bow);
        return unit;
    }

    private static Unit buildKnight(String name, Faction faction, Position pos) {
        Stats stats = new Stats(28, 10, 12, 4, 5, 3);
        Unit unit = new Unit(name, faction, UnitClass.KNIGHT, stats, pos);
        Weapon lance = new Weapon("Iron Lance", "Una pesante lancia di ferro.", 30, WeaponType.LANCE, 5, 1);
        unit.addItem(lance);
        unit.equipWeapon(lance);
        return unit;
    }

    private static Unit buildThief(String name, Faction faction, Position pos) {
        Stats stats = new Stats(18, 9, 4, 4, 13, 6);
        Unit unit = new Unit(name, faction, UnitClass.THIEF, stats, pos);
        Weapon dagger = new Weapon("Dagger", "Un pugnale leggero.", 35, WeaponType.SWORD, 3, 1);
        unit.addItem(dagger);
        unit.equipWeapon(dagger);
        Consumable potion = new Consumable("Vulnerary", "Ripristina 10 HP.", 3, 10);
        unit.addItem(potion);
        return unit;
    }
}
