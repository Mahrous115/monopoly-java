package com.monopoly;

import com.monopoly.squares.PropertySquare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertySquareTest {

    private Game game;
    private PropertySquare med;
    private PropertySquare bal;
    private Player alice;
    private Player bob;

    @BeforeEach
    void setUp() {
        game = new Game();
        med = (PropertySquare) game.getBoard().getSquare(1);   // Mediterranean
        bal = (PropertySquare) game.getBoard().getSquare(3);   // Baltic
        alice = new Player("Alice");
        bob = new Player("Bob");
    }

    @Test
    void baseRentChargedWithoutMonopoly() {
        med.setOwner(alice);
        alice.addProperty(med);
        assertEquals(2, med.calculateRent(game));
    }

    @Test
    void rentDoublesWithMonopoly() {
        med.setOwner(alice);
        bal.setOwner(alice);
        alice.addProperty(med);
        alice.addProperty(bal);
        assertEquals(4, med.calculateRent(game));
        assertEquals(8, bal.calculateRent(game));
    }

    @Test
    void rentScalesWithHouses() {
        med.setOwner(alice);
        bal.setOwner(alice);
        alice.addProperty(med);
        alice.addProperty(bal);
        med.buildHouse();
        assertEquals(10, med.calculateRent(game));
        med.buildHouse();
        assertEquals(30, med.calculateRent(game));
        med.buildHouse();
        med.buildHouse();
        assertEquals(160, med.calculateRent(game));
    }

    @Test
    void hotelChargesTopRent() {
        med.setOwner(alice);
        bal.setOwner(alice);
        alice.addProperty(med);
        alice.addProperty(bal);
        med.buildHouse(); med.buildHouse(); med.buildHouse(); med.buildHouse();
        med.buildHotel();
        assertTrue(med.isHotel());
        assertEquals(0, med.getHouseCount());
        assertEquals(250, med.calculateRent(game));
    }

    @Test
    void mortgagedPropertyChargesNoRent() {
        med.setOwner(alice);
        alice.addProperty(med);
        med.setMortgaged(true);
        assertEquals(0, med.calculateRent(game));
    }

    @Test
    void canBuildHouseOnlyWithFullColorGroup() {
        med.setOwner(alice);
        alice.addProperty(med);
        assertFalse(med.canBuildHouse(game));
        bal.setOwner(alice);
        alice.addProperty(bal);
        assertTrue(med.canBuildHouse(game));
    }

    @Test
    void evenBuildingRule() {
        med.setOwner(alice);
        bal.setOwner(alice);
        alice.addProperty(med);
        alice.addProperty(bal);
        med.buildHouse(); // med has 1, bal has 0
        // Can't add another to med (would be 2 vs 0)
        assertFalse(med.canBuildHouse(game));
        // Can build on bal (catch up)
        assertTrue(bal.canBuildHouse(game));
    }

    @Test
    void buyTransfersOwnershipAndDeductsCash() {
        med.buy(alice);
        assertSame(alice, med.getOwner());
        assertEquals(1500 - 60, alice.getBalance());
        assertTrue(alice.getOwnedProperties().contains(med));
    }

    @Test
    void resetToBankClearsAllState() {
        med.setOwner(alice);
        med.buildHouse();
        med.setMortgaged(true);
        med.resetToBank();
        assertNull(med.getOwner());
        assertEquals(0, med.getHouseCount());
        assertFalse(med.isHotel());
        assertFalse(med.isMortgaged());
    }
}
