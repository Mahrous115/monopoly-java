package com.monopoly;

import com.monopoly.cards.Card;
import com.monopoly.cards.CardDeck;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CardDeckTest {

    @Test
    void chanceDeckHas16UniqueCards() {
        CardDeck deck = CardDeck.buildChanceDeck();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 16; i++) {
            seen.add(deck.draw().getDescription());
        }
        assertEquals(16, seen.size(), "all 16 chance cards should be unique");
    }

    @Test
    void communityChestDeckHas16UniqueCards() {
        CardDeck deck = CardDeck.buildCommunityChestDeck();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 16; i++) {
            seen.add(deck.draw().getDescription());
        }
        assertEquals(16, seen.size(), "all 16 community chest cards should be unique");
    }

    @Test
    void deckRecyclesCardsToBottom() {
        CardDeck deck = CardDeck.buildChanceDeck();
        Card first = deck.draw();
        // Draw 15 more — none should equal `first` since each card is unique
        for (int i = 0; i < 15; i++) {
            assertNotSame(first, deck.draw());
        }
        // Now the 17th draw should be `first` again (full rotation)
        assertSame(first, deck.draw());
    }
}
