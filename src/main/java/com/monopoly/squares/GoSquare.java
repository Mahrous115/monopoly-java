package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * The GO square. The $200 salary is paid by the Game when a player
 * passes or lands on this position, so landing here is a no-op beyond
 * a friendly message.
 */
public class GoSquare extends Square {

    public GoSquare(int position) {
        super("GO", position);
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " is on GO.");
    }
}
