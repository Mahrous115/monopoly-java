package com.monopoly;

import com.monopoly.squares.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void boardHas40SquaresAtCanonicalPositions() {
        Board board = new Board();
        assertEquals(40, board.getSquares().size());
        assertInstanceOf(GoSquare.class, board.getSquare(0));
        assertInstanceOf(JailSquare.class, board.getSquare(10));
        assertInstanceOf(FreeParkingSquare.class, board.getSquare(20));
        assertInstanceOf(GoToJailSquare.class, board.getSquare(30));
        assertEquals("Boardwalk", board.getSquare(39).getName());
        assertEquals("Mediterranean Avenue", board.getSquare(1).getName());
    }

    @Test
    void railroadsLiveAt5_15_25_35() {
        Board board = new Board();
        for (int pos : new int[]{5, 15, 25, 35}) {
            assertInstanceOf(RailroadSquare.class, board.getSquare(pos));
        }
    }

    @Test
    void utilitiesLiveAt12And28() {
        Board board = new Board();
        assertInstanceOf(UtilitySquare.class, board.getSquare(12));
        assertInstanceOf(UtilitySquare.class, board.getSquare(28));
    }

    @Test
    void brownGroupHasTwoProperties() {
        Board board = new Board();
        assertEquals(2, board.propertiesInGroup("Brown").size());
        assertEquals(3, board.propertiesInGroup("Orange").size());
        assertEquals(2, board.propertiesInGroup("Dark Blue").size());
    }

    @Test
    void monopolyDetectedWhenOnePlayerOwnsAllInGroup() {
        Board board = new Board();
        Player alice = new Player("Alice");
        PropertySquare med = (PropertySquare) board.getSquare(1);
        PropertySquare bal = (PropertySquare) board.getSquare(3);
        med.setOwner(alice);
        assertFalse(board.playerOwnsColorGroup(alice, "Brown"));
        bal.setOwner(alice);
        assertTrue(board.playerOwnsColorGroup(alice, "Brown"));
    }

    @Test
    void nearestRailroadWrapsAround() {
        Board board = new Board();
        assertEquals(5, board.nearestRailroad(0));
        assertEquals(15, board.nearestRailroad(7));
        assertEquals(25, board.nearestRailroad(22));
        assertEquals(35, board.nearestRailroad(33));
        assertEquals(5, board.nearestRailroad(36));
    }

    @Test
    void nearestUtilityWrapsAround() {
        Board board = new Board();
        assertEquals(12, board.nearestUtility(7));
        assertEquals(28, board.nearestUtility(22));
        assertEquals(12, board.nearestUtility(30));
    }
}
