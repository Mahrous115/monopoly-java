package com.monopoly;

import com.monopoly.squares.TaxSquare;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxSquareTest {

    @Test
    void landingOnIncomeTaxCharges200() {
        Game game = new Game();
        Player alice = new Player("Alice");
        TaxSquare incomeTax = (TaxSquare) game.getBoard().getSquare(4);
        incomeTax.landOn(alice, game);
        assertEquals(1500 - 200, alice.getBalance());
    }

    @Test
    void landingOnLuxuryTaxCharges100() {
        Game game = new Game();
        Player alice = new Player("Alice");
        TaxSquare luxury = (TaxSquare) game.getBoard().getSquare(38);
        luxury.landOn(alice, game);
        assertEquals(1500 - 100, alice.getBalance());
    }
}
