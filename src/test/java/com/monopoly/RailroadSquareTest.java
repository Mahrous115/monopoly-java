package com.monopoly;

import com.monopoly.squares.RailroadSquare;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RailroadSquareTest {

    @Test
    void rentIs25With1Railroad() {
        Player alice = new Player("Alice");
        Board board = new Board();
        RailroadSquare reading = (RailroadSquare) board.getSquare(5);
        reading.setOwner(alice);
        alice.addRailroad(reading);
        assertEquals(25, reading.calculateRent());
    }

    @Test
    void rentDoublesPerRailroad() {
        Player alice = new Player("Alice");
        Board board = new Board();
        RailroadSquare r1 = (RailroadSquare) board.getSquare(5);
        RailroadSquare r2 = (RailroadSquare) board.getSquare(15);
        RailroadSquare r3 = (RailroadSquare) board.getSquare(25);
        RailroadSquare r4 = (RailroadSquare) board.getSquare(35);
        for (RailroadSquare r : new RailroadSquare[]{r1, r2, r3, r4}) {
            r.setOwner(alice);
            alice.addRailroad(r);
        }
        assertEquals(200, r1.calculateRent());
    }

    @Test
    void mortgagedRailroadChargesNoRent() {
        Player alice = new Player("Alice");
        Board board = new Board();
        RailroadSquare reading = (RailroadSquare) board.getSquare(5);
        reading.setOwner(alice);
        alice.addRailroad(reading);
        reading.setMortgaged(true);
        assertEquals(0, reading.calculateRent());
    }

    @Test
    void buyDeductsPriceAndAssignsOwner() {
        Player alice = new Player("Alice");
        Board board = new Board();
        RailroadSquare reading = (RailroadSquare) board.getSquare(5);
        reading.buy(alice);
        assertSame(alice, reading.getOwner());
        assertEquals(1500 - 200, alice.getBalance());
        assertTrue(alice.getOwnedRailroads().contains(reading));
    }
}
