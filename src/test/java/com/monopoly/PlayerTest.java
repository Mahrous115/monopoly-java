package com.monopoly;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void newPlayerStartsWith1500AtGoNotInJail() {
        Player p = new Player("Alice");
        assertEquals("Alice", p.getName());
        assertEquals(1500, p.getBalance());
        assertEquals(0, p.getPosition());
        assertFalse(p.isInJail());
        assertEquals(0, p.getJailTurns());
        assertEquals(0, p.getGetOutOfJailCards());
        assertFalse(p.isBankrupt());
        assertTrue(p.getOwnedProperties().isEmpty());
        assertTrue(p.getOwnedRailroads().isEmpty());
        assertTrue(p.getOwnedUtilities().isEmpty());
    }

    @Test
    void balanceAddAndDeductWork() {
        Player p = new Player("Bob");
        p.addBalance(500);
        assertEquals(2000, p.getBalance());
        p.deductBalance(300);
        assertEquals(1700, p.getBalance());
        p.setBalance(0);
        assertEquals(0, p.getBalance());
    }

    @Test
    void getOutOfJailCardsAccumulateAndDecrement() {
        Player p = new Player("Eve");
        p.addGetOutOfJailCard();
        p.addGetOutOfJailCard();
        assertEquals(2, p.getGetOutOfJailCards());
        p.useGetOutOfJailCard();
        assertEquals(1, p.getGetOutOfJailCards());
    }

    @Test
    void jailTurnsIncrementAndReset() {
        Player p = new Player("Dan");
        p.setInJail(true);
        p.incrementJailTurns();
        p.incrementJailTurns();
        assertEquals(2, p.getJailTurns());
        p.setJailTurns(0);
        assertEquals(0, p.getJailTurns());
    }
}
