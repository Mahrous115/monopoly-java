package com.monopoly.cards;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Abstract base for Chance and Community Chest cards. Concrete cards
 * are implemented as anonymous subclasses inside {@link CardDeck} so
 * each card's effect lives next to its description.
 */
public abstract class Card {

    private final String description;

    protected Card(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /** Apply this card's effect to {@code player}. */
    public abstract void execute(Player player, Game game);
}
