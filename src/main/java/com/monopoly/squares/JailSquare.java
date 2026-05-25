package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Position 10. If the player is "just visiting" nothing happens; if they
 * are in jail the actual release logic is handled at the start of the
 * next turn inside {@link Game}.
 */
public class JailSquare extends Square {

    public JailSquare(int position) {
        super("Jail / Just Visiting", position);
    }

    @Override
    public void landOn(Player player, Game game) {
        if (player.isInJail()) {
            System.out.println(player.getName() + " is in Jail.");
        } else {
            System.out.println(player.getName() + " is Just Visiting Jail.");
        }
    }
}
