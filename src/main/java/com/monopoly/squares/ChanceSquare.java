package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;
import com.monopoly.cards.Card;

/**
 * Draws the top card of the Chance deck and executes it.
 */
public class ChanceSquare extends Square {

    public ChanceSquare(int position) {
        super("Chance", position);
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " landed on Chance.");
        Card card = game.getChanceDeck().draw();
        System.out.println("Chance: " + card.getDescription());
        card.execute(player, game);
    }
}
