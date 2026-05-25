package com.monopoly;

import com.monopoly.squares.PropertySquare;
import com.monopoly.squares.RailroadSquare;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void chargeTransfersFromDebtorToCreditor() {
        Game game = new Game();
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        game.charge(alice, 200, bob);
        assertEquals(1300, alice.getBalance());
        assertEquals(1700, bob.getBalance());
    }

    @Test
    void chargeWithNullCreditorJustDeducts() {
        Game game = new Game();
        Player alice = new Player("Alice");
        game.charge(alice, 200, null);
        assertEquals(1300, alice.getBalance());
    }

    @Test
    void bankruptcyReturnsPropertiesToBankAndZerosBalance() {
        Game game = new Game();
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");

        PropertySquare med = (PropertySquare) game.getBoard().getSquare(1);
        RailroadSquare reading = (RailroadSquare) game.getBoard().getSquare(5);
        med.setOwner(alice);
        reading.setOwner(alice);
        alice.addProperty(med);
        alice.addRailroad(reading);
        alice.setBalance(50);

        game.charge(alice, 1000, bob);

        assertTrue(alice.isBankrupt());
        assertEquals(0, alice.getBalance());
        assertEquals(1550, bob.getBalance());
        assertNull(med.getOwner(), "property should return to bank");
        assertNull(reading.getOwner(), "railroad should return to bank");
        assertTrue(alice.getOwnedProperties().isEmpty());
        assertTrue(alice.getOwnedRailroads().isEmpty());
    }

    @Test
    void sendToJailMovesToPosition10AndFlipsFlag() {
        Game game = new Game();
        Player alice = new Player("Alice");
        alice.setPosition(25);
        game.sendToJail(alice);
        assertEquals(10, alice.getPosition());
        assertTrue(alice.isInJail());
        assertEquals(0, alice.getJailTurns());
    }

    @Test
    void advancePlayerByWrappingGoPaysSalaryAndAppliesLanding() {
        Game game = new Game();
        Player alice = new Player("Alice");
        alice.setPosition(38);
        // 38 + 6 = 44 → wraps to 4 (Income Tax). Pass GO (+200), then pay tax (-200) = $1500.
        game.advancePlayerBy(alice, 6);
        assertEquals(4, alice.getPosition());
        assertEquals(1500, alice.getBalance());
    }

    @Test
    void advancePlayerByWithoutWrappingDoesNotPayGo() {
        Game game = new Game();
        Player alice = new Player("Alice");
        alice.setPosition(0);
        // 0 + 4 = 4 → Income Tax. No GO bonus. Tax -200 → $1300.
        game.advancePlayerBy(alice, 4);
        assertEquals(4, alice.getPosition());
        assertEquals(1300, alice.getBalance());
    }

    @Test
    void payAllOtherPlayersDistributesToEveryoneElse() {
        Game game = new Game();
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player carol = new Player("Carol");
        game.getPlayers().add(alice);
        game.getPlayers().add(bob);
        game.getPlayers().add(carol);
        game.payAllOtherPlayers(alice, 50);
        assertEquals(1500 - 100, alice.getBalance());
        assertEquals(1550, bob.getBalance());
        assertEquals(1550, carol.getBalance());
    }

    @Test
    void collectFromAllOtherPlayersDrainsOthers() {
        Game game = new Game();
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player carol = new Player("Carol");
        game.getPlayers().add(alice);
        game.getPlayers().add(bob);
        game.getPlayers().add(carol);
        game.collectFromAllOtherPlayers(alice, 25);
        assertEquals(1550, alice.getBalance());
        assertEquals(1475, bob.getBalance());
        assertEquals(1475, carol.getBalance());
    }
}
