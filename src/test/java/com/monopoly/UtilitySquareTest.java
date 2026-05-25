package com.monopoly;

import com.monopoly.squares.UtilitySquare;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilitySquareTest {

    @Test
    void rentIsDiceTimes4WithOneUtility() {
        Player alice = new Player("Alice");
        Board board = new Board();
        UtilitySquare electric = (UtilitySquare) board.getSquare(12);
        electric.setOwner(alice);
        alice.addUtility(electric);
        assertEquals(28, electric.calculateRent(7));
    }

    @Test
    void rentIsDiceTimes10WithBothUtilities() {
        Player alice = new Player("Alice");
        Board board = new Board();
        UtilitySquare electric = (UtilitySquare) board.getSquare(12);
        UtilitySquare water = (UtilitySquare) board.getSquare(28);
        electric.setOwner(alice);
        water.setOwner(alice);
        alice.addUtility(electric);
        alice.addUtility(water);
        assertEquals(70, electric.calculateRent(7));
    }

    @Test
    void mortgagedUtilityChargesNoRent() {
        Player alice = new Player("Alice");
        Board board = new Board();
        UtilitySquare electric = (UtilitySquare) board.getSquare(12);
        electric.setOwner(alice);
        alice.addUtility(electric);
        electric.setMortgaged(true);
        assertEquals(0, electric.calculateRent(7));
    }
}
