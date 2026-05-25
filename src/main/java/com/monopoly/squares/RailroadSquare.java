package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * One of the four railroads. Rent scales with the number of railroads
 * owned by the same player: $25 / $50 / $100 / $200.
 */
public class RailroadSquare extends Square {

    public static final int PRICE = 200;
    private static final int[] RENT_BY_COUNT = {0, 25, 50, 100, 200};

    private Player owner;
    private boolean mortgaged;

    public RailroadSquare(String name, int position) {
        super(name, position);
    }

    public int getPrice() { return PRICE; }
    public int getMortgageValue() { return PRICE / 2; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
    public boolean isMortgaged() { return mortgaged; }
    public void setMortgaged(boolean mortgaged) { this.mortgaged = mortgaged; }

    public int calculateRent() {
        if (owner == null || mortgaged) return 0;
        int count = owner.getOwnedRailroads().size();
        return RENT_BY_COUNT[Math.min(count, 4)];
    }

    public void buy(Player buyer) {
        this.owner = buyer;
        buyer.deductBalance(PRICE);
        buyer.addRailroad(this);
        System.out.println(buyer.getName() + " bought " + getName() + " for $" + PRICE + ".");
    }

    public void resetToBank() {
        this.owner = null;
        this.mortgaged = false;
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " landed on " + getName() + " (Railroad, $" + PRICE + ").");
        if (owner == null) {
            game.offerToBuyRailroad(player, this);
        } else if (owner == player) {
            System.out.println("You own this railroad.");
        } else if (mortgaged) {
            System.out.println(getName() + " is mortgaged; no rent due.");
        } else {
            int rent = calculateRent();
            System.out.println(player.getName() + " owes $" + rent + " rent to " + owner.getName() + ".");
            game.charge(player, rent, owner);
        }
    }
}
