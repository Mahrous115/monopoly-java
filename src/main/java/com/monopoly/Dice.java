package com.monopoly;

import java.util.Random;

/**
 * Pair of six-sided dice. Tracks consecutive doubles so the Game can
 * apply the "three doubles in a row = go to jail" rule.
 */
public class Dice {

    private final Random random = new Random();
    private int die1;
    private int die2;
    private int consecutiveDoubles;

    public void roll() {
        die1 = random.nextInt(6) + 1;
        die2 = random.nextInt(6) + 1;
        if (isDoubles()) {
            consecutiveDoubles++;
        } else {
            consecutiveDoubles = 0;
        }
    }

    public int getDie1() { return die1; }
    public int getDie2() { return die2; }

    public int getTotal() { return die1 + die2; }

    public boolean isDoubles() { return die1 == die2 && die1 != 0; }

    public int getConsecutiveDoubles() { return consecutiveDoubles; }

    public void resetConsecutiveDoubles() { consecutiveDoubles = 0; }
}
