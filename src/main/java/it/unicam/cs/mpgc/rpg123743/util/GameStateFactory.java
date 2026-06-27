package it.unicam.cs.mpgc.rpg123743.util;

import it.unicam.cs.mpgc.rpg123743.model.*;

/**
 * Costruisce lo stato iniziale del gioco per ogni livello di mappa.
 * Le dimensioni della griglia sono lette da {@link MapLevel} (unica fonte di verità).
 * La creazione delle unità è delegata a {@link UnitFactory}.
 */
public final class GameStateFactory {

    private GameStateFactory() {}

    /**
     * Crea e restituisce lo stato di gioco iniziale per il livello specificato.
     *
     * @param level il livello da caricare.
     * @return il {@link GameState} iniziale pronto per essere usato.
     */
    public static GameState createGame(MapLevel level) {
        return switch (level) {
            case ASHBORNE_PLAINS   -> createAshbornePlains(level);
            case WALL_BREACH       -> createWallBreach(level);
            case TANGLED_HIGHLANDS -> createTangledHighlands(level);
        };
    }

    // Livello 1 — Ashborne Plains (8x8)
    private static GameState createAshbornePlains(MapLevel level) {
        BattleMap map = buildMap(level);
        placeAshborneUnits(map);
        return new GameState("autosave " + level.getDisplayName(), map);
    }

    private static void placeAshborneUnits(BattleMap map) {
        map.placeUnit(UnitFactory.warrior("Lucina", Faction.PLAYER, new Position(6, 0)));
        map.placeUnit(UnitFactory.mage   ("Robin",  Faction.PLAYER, new Position(7, 1)));
        map.placeUnit(UnitFactory.archer ("Niles",  Faction.PLAYER, new Position(6, 2)));
        map.placeUnit(UnitFactory.knight ("Xander", Faction.PLAYER, new Position(7, 0)));
        map.placeUnit(UnitFactory.thief  ("Anna",   Faction.PLAYER, new Position(5, 1)));
        map.placeUnit(UnitFactory.healer ("Lissa",  Faction.PLAYER, new Position(7, 2)));
        map.placeUnit(UnitFactory.warrior("Dark Soldier", Faction.ENEMY, new Position(1, 7)));
        map.placeUnit(UnitFactory.archer ("Dark Archer",  Faction.ENEMY, new Position(0, 5)));
        map.placeUnit(UnitFactory.mage   ("Dark Mage",    Faction.ENEMY, new Position(1, 6)));
        map.placeUnit(UnitFactory.knight ("Dark Knight",  Faction.ENEMY, new Position(0, 7)));
    }

    private static TerrainType ashborneTerrain(int r, int c) {
        if (r <= 2 && c >= 5) return TerrainType.FOREST;
        if (r >= 5 && c <= 2) return TerrainType.MOUNTAIN;
        return TerrainType.PLAIN;
    }

    // Livello 2 — Wall Breach (12x12)
    private static GameState createWallBreach(MapLevel level) {
        BattleMap map = buildMap(level);
        placeWallBreachUnits(map);
        return new GameState("autosave " + level.getDisplayName(), map);
    }

    private static void placeWallBreachUnits(BattleMap map) {
        map.placeUnit(UnitFactory.warrior("Lucina", Faction.PLAYER, new Position(9, 4)));
        map.placeUnit(UnitFactory.mage   ("Robin",  Faction.PLAYER, new Position(9, 7)));
        map.placeUnit(UnitFactory.archer ("Niles",  Faction.PLAYER, new Position(10, 5)));
        map.placeUnit(UnitFactory.knight ("Xander", Faction.PLAYER, new Position(9, 5)));
        map.placeUnit(UnitFactory.thief  ("Anna",   Faction.PLAYER, new Position(10, 6)));
        map.placeUnit(UnitFactory.healer ("Lissa",  Faction.PLAYER, new Position(10, 7)));
        map.placeUnit(UnitFactory.warrior("Guard",        Faction.ENEMY, new Position(5, 4)));
        map.placeUnit(UnitFactory.warrior("Guard",        Faction.ENEMY, new Position(5, 7)));
        map.placeUnit(UnitFactory.archer ("Tower Archer", Faction.ENEMY, new Position(3, 4)));
        map.placeUnit(UnitFactory.archer ("Tower Archer", Faction.ENEMY, new Position(3, 7)));
        map.placeUnit(UnitFactory.knight ("Fort Knight",  Faction.ENEMY, new Position(4, 5)));
        map.placeUnit(UnitFactory.knight ("Fort Knight",  Faction.ENEMY, new Position(4, 6)));
        map.placeUnit(UnitFactory.warrior("Commander",    Faction.ENEMY, new Position(2, 6)));
    }

    private static TerrainType wallBreachTerrain(int r, int c) {
        if (c == 0 || c == 11)    return TerrainType.MOUNTAIN;
        if (r == 6) {
            if (c == 4 || c == 7) return TerrainType.BREAKABLE_WALL;
            return TerrainType.WALL;
        }
        return TerrainType.PLAIN;
    }

    // Livello 3 — Tangled Highlands (16x16)
    private static GameState createTangledHighlands(MapLevel level) {
        BattleMap map = buildMap(level);
        placeTangledHighlandsUnits(map);
        return new GameState("autosave " + level.getDisplayName(), map);
    }

    private static void placeTangledHighlandsUnits(BattleMap map) {
        map.placeUnit(UnitFactory.warrior("Lucina", Faction.PLAYER, new Position(15, 1)));
        map.placeUnit(UnitFactory.mage   ("Robin",  Faction.PLAYER, new Position(15, 4)));
        map.placeUnit(UnitFactory.archer ("Niles",  Faction.PLAYER, new Position(15, 7)));
        map.placeUnit(UnitFactory.knight ("Xander", Faction.PLAYER, new Position(15, 10)));
        map.placeUnit(UnitFactory.thief  ("Anna",   Faction.PLAYER, new Position(15, 13)));
        map.placeUnit(UnitFactory.healer ("Lissa",  Faction.PLAYER, new Position(15, 15)));
        map.placeUnit(UnitFactory.warrior("Scout",        Faction.ENEMY, new Position(1, 1)));
        map.placeUnit(UnitFactory.warrior("Scout",        Faction.ENEMY, new Position(1, 5)));
        map.placeUnit(UnitFactory.warrior("Scout",        Faction.ENEMY, new Position(1, 9)));
        map.placeUnit(UnitFactory.archer ("Sniper",       Faction.ENEMY, new Position(1, 12)));
        map.placeUnit(UnitFactory.archer ("Sniper",       Faction.ENEMY, new Position(1, 14)));
        map.placeUnit(UnitFactory.knight ("Knight",       Faction.ENEMY, new Position(4, 1)));
        map.placeUnit(UnitFactory.knight ("Knight",       Faction.ENEMY, new Position(4, 7)));
        map.placeUnit(UnitFactory.knight ("Knight",       Faction.ENEMY, new Position(4, 14)));
        map.placeUnit(UnitFactory.mage   ("Mage",         Faction.ENEMY, new Position(4, 4)));
        map.placeUnit(UnitFactory.mage   ("Mage",         Faction.ENEMY, new Position(4, 10)));
        map.placeUnit(UnitFactory.warrior("Elite Guard",  Faction.ENEMY, new Position(0, 3)));
        map.placeUnit(UnitFactory.warrior("Elite Guard",  Faction.ENEMY, new Position(0, 11)));
        map.placeUnit(UnitFactory.archer ("Elite Archer", Faction.ENEMY, new Position(0, 8)));
        map.placeUnit(UnitFactory.thief  ("Shadow",       Faction.ENEMY, new Position(3, 6)));
        map.placeUnit(UnitFactory.thief  ("Shadow",       Faction.ENEMY, new Position(3, 13)));
        map.placeUnit(UnitFactory.knight ("Warlord",      Faction.ENEMY, new Position(0, 6)));
    }

    private static TerrainType tangledHighlandsTerrain(int r, int c) {
        if ((r + c) % 5 == 0)     return TerrainType.FOREST;
        if ((2 * r + c) % 7 == 0) return TerrainType.MOUNTAIN;
        return TerrainType.PLAIN;
    }

    /**
     * Costruisce una {@link BattleMap} per il livello indicato, popolando
     * ogni cella con il terreno appropriato tramite la strategia specifica del livello.
     *
     * @param level il livello di cui costruire la mappa.
     * @return la mappa con tutte le celle inizializzate.
     */
    private static BattleMap buildMap(MapLevel level) {
        BattleMap map = new BattleMap(level.getDisplayName(), level.getHeight(), level.getWidth());
        TerrainStrategy terrain = terrainStrategyFor(level);
        for (int r = 0; r < level.getHeight(); r++)
            for (int c = 0; c < level.getWidth(); c++)
                map.setCell(new Cell(new Position(r, c), terrain.terrainAt(r, c)));
        return map;
    }

    /**
     * Interfaccia funzionale che astrae la logica di generazione del terreno,
     * permettendo a {@link #buildMap} di essere indipendente dal livello specifico.
     */
    @FunctionalInterface
    private interface TerrainStrategy {
        TerrainType terrainAt(int r, int c);
    }

    /**
     * Restituisce la {@link TerrainStrategy} appropriata per il livello indicato.
     *
     * @param level il livello di cui ottenere la strategia.
     * @return la strategia di generazione del terreno.
     */
    private static TerrainStrategy terrainStrategyFor(MapLevel level) {
        return switch (level) {
            case ASHBORNE_PLAINS   -> GameStateFactory::ashborneTerrain;
            case WALL_BREACH       -> GameStateFactory::wallBreachTerrain;
            case TANGLED_HIGHLANDS -> GameStateFactory::tangledHighlandsTerrain;
        };
    }
}