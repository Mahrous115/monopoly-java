package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;
import com.monopoly.cards.Card;

/**
 * Draws the top card of the Community Chest deck and executes it.
 */
public class CommunityChestSquare extends Square {

    public CommunityChestSquare(int position) {
        super("Community Chest", position);
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " landed on Community Chest.");
        Card card = game.getCommunityChestDeck().draw();
        System.out.println("Community Chest: " + card.getDescription());
        card.execute(player, game);
    }
}
