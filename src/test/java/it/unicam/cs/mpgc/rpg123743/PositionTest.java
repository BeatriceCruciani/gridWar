package it.unicam.cs.mpgc.rpg123743;

import it.unicam.cs.mpgc.rpg123743.model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Position")
class PositionTest {

    @Test
    @DisplayName("Manhattan distance is calculated correctly")
    void manhattanDistance() {
        Position a = new Position(0, 0);
        Position b = new Position(3, 4);
        assertEquals(7, a.distanceTo(b));
    }

    @Test
    @DisplayName("Distance to self is zero")
    void distanceToSelfIsZero() {
        Position a = new Position(2, 5);
        assertEquals(0, a.distanceTo(a));
    }

    @Test
    @DisplayName("Two positions with same coordinates are equal")
    void equalPositions() {
        Position a = new Position(1, 2);
        Position b = new Position(1, 2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Negative coordinates throw IllegalArgumentException")
    void negativeCoordinatesThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Position(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Position(0, -1));
    }
}