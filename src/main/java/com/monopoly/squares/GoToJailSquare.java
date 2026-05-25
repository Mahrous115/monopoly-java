package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * "Go to Jail" — sends the player directly to position 10 and flips
 * their inJail flag. No $200 salary, regardless of board layout.
 */
public class GoToJailSquare extends Square {

    public GoToJailSquare(int position) {
        super("Go To Jail", position);
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " was sent to Jail!");
        game.sendToJail(player);
    }
}
