package com.monopoly.cards;

import com.monopoly.Game;
import com.monopoly.Player;
import com.monopoly.squares.PropertySquare;
import com.monopoly.squares.RailroadSquare;
import com.monopoly.squares.Square;
import com.monopoly.squares.UtilitySquare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A shuffled deck of Chance or Community Chest cards. Drawing pulls the
 * top card and recycles it to the bottom so the deck never runs out.
 *
 * The two factory methods own the full set of 16 + 16 official cards
 * as anonymous subclasses, keeping each card's text right next to its
 * behavior.
 */
public class CardDeck {

    private final String name;
    private final List<Card> cards;

    private CardDeck(String name, List<Card> cards) {
        this.name = name;
        this.cards = new ArrayList<>(cards);
        Collections.shuffle(this.cards);
    }

    public String getName() {
        return name;
    }

    /** Draw the top card and recycle it to the bottom. */
    public Card draw() {
        Card top = cards.remove(0);
        cards.add(top);
        return top;
    }

    public static CardDeck buildChanceDeck() {
        List<Card> deck = new ArrayList<>();

        deck.add(new Card("Advance to GO. Collect $200.") {
            @Override public void execute(Player p, Game g) {
                g.advancePlayerTo(p, 0);
            }
        });

        deck.add(new Card("Advance to Illinois Avenue. If you pass GO, collect $200.") {
            @Override public void execute(Player p, Game g) {
                g.advancePlayerTo(p, 24);
            }
        });

        deck.add(new Card("Advance to St. Charles Place. If you pass GO, collect $200.") {
            @Override public void execute(Player p, Game g) {
                g.advancePlayerTo(p, 11);
            }
        });

        deck.add(new Card("Advance to the nearest Utility. If unowned you may buy it; otherwise pay 10x dice.") {
            @Override public void execute(Player p, Game g) {
                int target = g.getBoard().nearestUtility(p.getPosition());
                g.movePlayerToNoLand(p, target);
                UtilitySquare u = (UtilitySquare) g.getBoard().getSquare(target);
                System.out.println(p.getName() + " arrives at " + u.getName() + ".");
                if (u.getOwner() == null) {
                    g.offerToBuyUtility(p, u);
                } else if (u.getOwner() != p && !u.isMortgaged()) {
                    int rent = g.rollExtraDice() * 10;
                    System.out.println("Special rent (10x dice): $" + rent);
                    g.charge(p, rent, u.getOwner());
                }
            }
        });

        deck.add(new Card("Advance to the nearest Railroad. Pay owner twice the normal rent if owned, or you may buy it.") {
            @Override public void execute(Player p, Game g) {
                int target = g.getBoard().nearestRailroad(p.getPosition());
                g.movePlayerToNoLand(p, target);
                RailroadSquare r = (RailroadSquare) g.getBoard().getSquare(target);
                System.out.println(p.getName() + " arrives at " + r.getName() + ".");
                if (r.getOwner() == null) {
                    g.offerToBuyRailroad(p, r);
                } else if (r.getOwner() != p && !r.isMortgaged()) {
                    int rent = r.calculateRent() * 2;
                    System.out.println("Special rent (double): $" + rent);
                    g.charge(p, rent, r.getOwner());
                }
            }
        });

        deck.add(new Card("Bank pays you dividend of $50.") {
            @Override public void execute(Player p, Game g) { p.addBalance(50); }
        });

        deck.add(new Card("Get Out of Jail Free. This card may be kept until needed.") {
            @Override public void execute(Player p, Game g) {
                p.addGetOutOfJailCard();
                System.out.println(p.getName() + " now holds " + p.getGetOutOfJailCards() + " Get Out of Jail Free card(s).");
            }
        });

        deck.add(new Card("Go back 3 spaces.") {
            @Override public void execute(Player p, Game g) {
                int newPos = (p.getPosition() - 3 + 40) % 40;
                p.setPosition(newPos);
                Square s = g.getBoard().getSquare(newPos);
                System.out.println(p.getName() + " moves back to " + s.getName() + ".");
                s.landOn(p, g);
            }
        });

        deck.add(new Card("Go directly to Jail. Do not pass GO, do not collect $200.") {
            @Override public void execute(Player p, Game g) { g.sendToJail(p); }
        });

        deck.add(new Card("Make general repairs on all your property: $25 per house, $100 per hotel.") {
            @Override public void execute(Player p, Game g) {
                int houses = 0, hotels = 0;
                for (PropertySquare prop : p.getOwnedProperties()) {
                    if (prop.isHotel()) hotels++;
                    else houses += prop.getHouseCount();
                }
                int bill = houses * 25 + hotels * 100;
                System.out.println("Repair bill: " + houses + " houses + " + hotels + " hotels = $" + bill);
                g.charge(p, bill, null);
            }
        });

        deck.add(new Card("Pay poor tax of $15.") {
            @Override public void execute(Player p, Game g) { g.charge(p, 15, null); }
        });

        deck.add(new Card("Take a trip to Reading Railroad. If you pass GO, collect $200.") {
            @Override public void execute(Player p, Game g) { g.advancePlayerTo(p, 5); }
        });

        deck.add(new Card("Take a walk on the Boardwalk. Advance to Boardwalk.") {
            @Override public void execute(Player p, Game g) { g.advancePlayerTo(p, 39); }
        });

        deck.add(new Card("You have been elected Chairman of the Board. Pay each player $50.") {
            @Override public void execute(Player p, Game g) { g.payAllOtherPlayers(p, 50); }
        });

        deck.add(new Card("Your building loan matures. Collect $150.") {
            @Override public void execute(Player p, Game g) { p.addBalance(150); }
        });

        deck.add(new Card("You have won a crossword competition. Collect $100.") {
            @Override public void execute(Player p, Game g) { p.addBalance(100); }
        });

        return new CardDeck("Chance", deck);
    }

    public static CardDeck buildCommunityChestDeck() {
        List<Card> deck = new ArrayList<>();

        deck.add(new Card("Advance to GO. Collect $200.") {
            @Override public void execute(Player p, Game g) { g.advancePlayerTo(p, 0); }
        });

        deck.add(new Card("Bank error in your favor. Collect $200.") {
            @Override public void execute(Player p, Game g) { p.addBalance(200); }
        });

        deck.add(new Card("Doctor's fees. Pay $50.") {
            @Override public void execute(Player p, Game g) { g.charge(p, 50, null); }
        });

        deck.add(new Card("From sale of stock you get $50.") {
            @Override public void execute(Player p, Game g) { p.addBalance(50); }
        });

        deck.add(new Card("Get Out of Jail Free. This card may be kept until needed.") {
            @Override public void execute(Player p, Game g) {
                p.addGetOutOfJailCard();
                System.out.println(p.getName() + " now holds " + p.getGetOutOfJailCards() + " Get Out of Jail Free card(s).");
            }
        });

        deck.add(new Card("Go directly to Jail. Do not pass GO, do not collect $200.") {
            @Override public void execute(Player p, Game g) { g.sendToJail(p); }
        });

        deck.add(new Card("Holiday fund matures. Receive $100.") {
            @Override public void execute(Player p, Game g) { p.addBalance(100); }
        });

        deck.add(new Card("Income tax refund. Collect $20.") {
            @Override public void execute(Player p, Game g) { p.addBalance(20); }
        });

        deck.add(new Card("It is your birthday. Collect $10 from every player.") {
            @Override public void execute(Player p, Game g) { g.collectFromAllOtherPlayers(p, 10); }
        });

        deck.add(new Card("Life insurance matures. Collect $100.") {
            @Override public void execute(Player p, Game g) { p.addBalance(100); }
        });

        deck.add(new Card("Pay hospital fees of $100.") {
            @Override public void execute(Player p, Game g) { g.charge(p, 100, null); }
        });

        deck.add(new Card("Pay school fees of $50.") {
            @Override public void execute(Player p, Game g) { g.charge(p, 50, null); }
        });

        deck.add(new Card("Receive $25 consultancy fee.") {
            @Override public void execute(Player p, Game g) { p.addBalance(25); }
        });

        deck.add(new Card("You are assessed for street repairs: $40 per house, $115 per hotel.") {
            @Override public void execute(Player p, Game g) {
                int houses = 0, hotels = 0;
                for (PropertySquare prop : p.getOwnedProperties()) {
                    if (prop.isHotel()) hotels++;
                    else houses += prop.getHouseCount();
                }
                int bill = houses * 40 + hotels * 115;
                System.out.println("Street repair bill: " + houses + " houses + " + hotels + " hotels = $" + bill);
                g.charge(p, bill, null);
            }
        });

        deck.add(new Card("You have won second prize in a beauty contest. Collect $10.") {
            @Override public void execute(Player p, Game g) { p.addBalance(10); }
        });

        deck.add(new Card("You inherit $100.") {
            @Override public void execute(Player p, Game g) { p.addBalance(100); }
        });

        return new CardDeck("Community Chest", deck);
    }
}
