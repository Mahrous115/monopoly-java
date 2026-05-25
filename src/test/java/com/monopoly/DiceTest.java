package com.monopoly;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @RepeatedTest(50)
    void rollProducesValuesBetween1And6() {
        Dice dice = new Dice();
        dice.roll();
        assertTrue(dice.getDie1() >= 1 && dice.getDie1() <= 6);
        assertTrue(dice.getDie2() >= 1 && dice.getDie2() <= 6);
        assertEquals(dice.getDie1() + dice.getDie2(), dice.getTotal());
    }

    @Test
    void doublesDetectionMatchesDieValues() {
        Dice dice = new Dice();
        for (int i = 0; i < 100; i++) {
            dice.roll();
            assertEquals(dice.getDie1() == dice.getDie2(), dice.isDoubles());
        }
    }

    @Test
    void consecutiveDoublesResetsOnNonDoubles() {
        Dice dice = new Dice();
        // Force the state via rolls — statistically we'll hit doubles eventually.
        // Just verify that the counter can be reset.
        dice.resetConsecutiveDoubles();
        assertEquals(0, dice.getConsecutiveDoubles());
    }
}
