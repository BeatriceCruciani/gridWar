package it.unicam.cs.mpgc.rpg123743;

import it.unicam.cs.mpgc.rpg123743.service.WeaponTriangle;
import it.unicam.cs.mpgc.rpg123743.model.WeaponType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponTriangle")
class WeaponTriangleTest {

    private WeaponTriangle triangle;

    @BeforeEach
    void setUp() {
        triangle = new WeaponTriangle();
    }

    @Test
    @DisplayName("Sword has advantage over Axe")
    void swordBeatsAxe() {
        assertEquals(1.2, triangle.getMultiplier(WeaponType.SWORD, WeaponType.AXE), 0.001);
    }

    @Test
    @DisplayName("Axe has advantage over Lance")
    void axeBeatsLance() {
        assertEquals(1.2, triangle.getMultiplier(WeaponType.AXE, WeaponType.LANCE), 0.001);
    }

    @Test
    @DisplayName("Lance has advantage over Sword")
    void lanceBeatsSwrd() {
        assertEquals(1.2, triangle.getMultiplier(WeaponType.LANCE, WeaponType.SWORD), 0.001);
    }

    @Test
    @DisplayName("Sword is at disadvantage against Lance")
    void swordLosesToLance() {
        assertEquals(0.8, triangle.getMultiplier(WeaponType.SWORD, WeaponType.LANCE), 0.001);
    }

    @Test
    @DisplayName("Bow is neutral against any weapon")
    void bowIsNeutral() {
        assertEquals(1.0, triangle.getMultiplier(WeaponType.BOW, WeaponType.SWORD), 0.001);
        assertEquals(1.0, triangle.getMultiplier(WeaponType.BOW, WeaponType.MAGIC), 0.001);
    }

    @Test
    @DisplayName("Magic is neutral against any weapon")
    void magicIsNeutral() {
        assertEquals(1.0, triangle.getMultiplier(WeaponType.MAGIC, WeaponType.LANCE), 0.001);
    }

    @Test
    @DisplayName("Null weapon types return neutral multiplier")
    void nullWeaponTypesReturnNeutral() {
        assertEquals(1.0, triangle.getMultiplier(null, WeaponType.SWORD), 0.001);
        assertEquals(1.0, triangle.getMultiplier(WeaponType.SWORD, null), 0.001);
    }

    @Test
    @DisplayName("hasAdvantage returns true only for triangle winners")
    void hasAdvantage() {
        assertTrue(triangle.hasAdvantage(WeaponType.SWORD, WeaponType.AXE));
        assertFalse(triangle.hasAdvantage(WeaponType.SWORD, WeaponType.LANCE));
        assertFalse(triangle.hasAdvantage(WeaponType.BOW, WeaponType.SWORD));
    }
}