package com.monopoly;

/**
 * Entry point. Constructs a single {@link Game} and starts the turn loop.
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        new Game().start();
    }
}
