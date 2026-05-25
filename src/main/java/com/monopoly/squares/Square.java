package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Abstract base for every space on the board.
 *
 * Each concrete subclass decides what happens when a player lands on it
 * via the polymorphic {@link #landOn(Player, Game)} hook. Common state
 * (display name, board position) lives here.
 */
public abstract class Square {

    private final String name;
    private final int position;

    protected Square(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    /** Polymorphic behavior when {@code player} lands on this square. */
    public abstract void landOn(Player player, Game game);

    @Override
    public String toString() {
        return name;
    }
}
