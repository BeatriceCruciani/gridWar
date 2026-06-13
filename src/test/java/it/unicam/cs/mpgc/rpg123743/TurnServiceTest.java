package it.unicam.cs.mpgc.rpg123743;

import it.unicam.cs.mpgc.rpg123743.model.*;
import it.unicam.cs.mpgc.rpg123743.service.TurnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TurnService")
class TurnServiceTest {

    private TurnService turnService;
    private BattleMap map;
    private GameState state;

    @BeforeEach
    void setUp() {
        turnService = new TurnService();
        map = buildTestMap();
        state = new GameState("test-save", map);
    }

    @Test
    @DisplayName("Initial phase is PLAYER_TURN")
    void initialPhaseIsPlayerTurn() {
        assertEquals(GameState.Phase.PLAYER_TURN, state.getCurrentPhase());
    }

    @Test
    @DisplayName("End player turn advances to ENEMY_TURN")
    void endPlayerTurnAdvancesToEnemyTurn() {
        turnService.endPlayerTurn(state);
        assertEquals(GameState.Phase.ENEMY_TURN, state.getCurrentPhase());
    }

    @Test
    @DisplayName("End enemy turn advances to PLAYER_TURN and increments turn number")
    void endEnemyTurnAdvancesAndIncrementsTurn() {
        turnService.endPlayerTurn(state);
        turnService.endEnemyTurn(state);
        assertEquals(GameState.Phase.PLAYER_TURN, state.getCurrentPhase());
        assertEquals(2, state.getTurnNumber());
    }

    @Test
    @DisplayName("Victory detected when all enemies are defeated")
    void victoryWhenAllEnemiesDefeated() {
        Unit enemy = buildUnit("Enemy", Faction.ENEMY, new Position(0, 1));
        map.placeUnit(enemy);
        enemy.getStats().applyDamage(999);

        assertTrue(turnService.checkVictory(state));
        assertEquals(GameState.Phase.VICTORY, state.getCurrentPhase());
    }

    @Test
    @DisplayName("Defeat detected when all player units are defeated")
    void defeatWhenAllPlayersDefeated() {
        Unit player = buildUnit("Player", Faction.PLAYER, new Position(0, 0));
        map.placeUnit(player);
        player.getStats().applyDamage(999);

        assertTrue(turnService.checkDefeat(state));
        assertEquals(GameState.Phase.DEFEAT, state.getCurrentPhase());
    }

    @Test
    @DisplayName("allPlayerUnitsFinished returns true when all have acted")
    void allPlayerUnitsFinished() {
        Unit p1 = buildUnit("P1", Faction.PLAYER, new Position(0, 0));
        Unit p2 = buildUnit("P2", Faction.PLAYER, new Position(0, 1));
        map.placeUnit(p1);
        map.placeUnit(p2);
        p1.markAsMoved(); p1.markAsActed();
        p2.markAsMoved(); p2.markAsActed();

        assertTrue(turnService.allPlayerUnitsFinished(state));
    }

    @Test
    @DisplayName("removeDefeatedUnits clears defeated units from the map")
    void removeDefeatedUnits() {
        Unit unit = buildUnit("Dead", Faction.ENEMY, new Position(0, 0));
        map.placeUnit(unit);
        unit.getStats().applyDamage(999);

        assertTrue(map.getCell(new Position(0, 0)).isOccupied());
        turnService.removeDefeatedUnits(state);
        assertFalse(map.getCell(new Position(0, 0)).isOccupied());
    }

    // --- Helpers ---

    private BattleMap buildTestMap() {
        BattleMap m = new BattleMap("Test", 3, 3);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                m.setCell(new Cell(new Position(r, c), TerrainType.PLAIN));
        return m;
    }

    private Unit buildUnit(String name, Faction faction, Position pos) {
        return new Unit(name, faction, UnitClass.WARRIOR,
                new Stats(20, 8, 4, 2, 5, 4), pos);
    }
}