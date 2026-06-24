package it.unicam.cs.mpgc.rpg123743;

import it.unicam.cs.mpgc.rpg123743.model.WeaponType;
import it.unicam.cs.mpgc.rpg123743.model.WeaponType.TriangleRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponType")
class WeaponTypeTest {

    @Test
    @DisplayName("Sword has advantage over Axe")
    void swordBeatsAxe() {
        assertEquals(TriangleRelation.ADVANTAGE, WeaponType.SWORD.getRelationAgainst(WeaponType.AXE));
        assertEquals(1.2, WeaponType.SWORD.getRelationAgainst(WeaponType.AXE).getMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Axe has advantage over Lance")
    void axeBeatsLance() {
        assertEquals(TriangleRelation.ADVANTAGE, WeaponType.AXE.getRelationAgainst(WeaponType.LANCE));
        assertEquals(1.2, WeaponType.AXE.getRelationAgainst(WeaponType.LANCE).getMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Lance has advantage over Sword")
    void lanceBeatsSword() {
        assertEquals(TriangleRelation.ADVANTAGE, WeaponType.LANCE.getRelationAgainst(WeaponType.SWORD));
        assertEquals(1.2, WeaponType.LANCE.getRelationAgainst(WeaponType.SWORD).getMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Sword is at disadvantage against Lance")
    void swordLosesToLance() {
        assertEquals(TriangleRelation.DISADVANTAGE, WeaponType.SWORD.getRelationAgainst(WeaponType.LANCE));
        assertEquals(0.8, WeaponType.SWORD.getRelationAgainst(WeaponType.LANCE).getMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Bow is neutral against any weapon")
    void bowIsNeutral() {
        assertEquals(TriangleRelation.NEUTRAL, WeaponType.BOW.getRelationAgainst(WeaponType.SWORD));
        assertEquals(TriangleRelation.NEUTRAL, WeaponType.BOW.getRelationAgainst(WeaponType.MAGIC));
        assertEquals(1.0, WeaponType.BOW.getRelationAgainst(WeaponType.SWORD).getMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Magic is neutral against any weapon")
    void magicIsNeutral() {
        assertEquals(TriangleRelation.NEUTRAL, WeaponType.MAGIC.getRelationAgainst(WeaponType.LANCE));
        assertEquals(1.0, WeaponType.MAGIC.getRelationAgainst(WeaponType.LANCE).getMultiplier(), 0.001);
    }

    @Test
    @DisplayName("Staff is neutral against any weapon")
    void staffIsNeutral() {
        assertEquals(TriangleRelation.NEUTRAL, WeaponType.STAFF.getRelationAgainst(WeaponType.SWORD));
    }

    @Test
    @DisplayName("Null opponent weapon type returns neutral relation")
    void nullOpponentReturnsNeutral() {
        assertEquals(TriangleRelation.NEUTRAL, WeaponType.SWORD.getRelationAgainst(null));
        assertEquals(1.0, WeaponType.SWORD.getRelationAgainst(null).getMultiplier(), 0.001);
    }
}