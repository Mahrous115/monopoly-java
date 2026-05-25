package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Electric Company or Water Works. Rent is dice total × 4 if the owner
 * has one utility, × 10 if they own both.
 */
public class UtilitySquare extends Square {

    public static final int PRICE = 150;

    private Player owner;
    private boolean mortgaged;

    public UtilitySquare(String name, int position) {
        super(name, position);
    }

    public int getPrice() { return PRICE; }
    public int getMortgageValue() { return PRICE / 2; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
    public boolean isMortgaged() { return mortgaged; }
    public void setMortgaged(boolean mortgaged) { this.mortgaged = mortgaged; }

    public int calculateRent(int diceTotal) {
        if (owner == null || mortgaged) return 0;
        int count = owner.getOwnedUtilities().size();
        int multiplier = (count >= 2) ? 10 : 4;
        return diceTotal * multiplier;
    }

    public void buy(Player buyer) {
        this.owner = buyer;
        buyer.deductBalance(PRICE);
        buyer.addUtility(this);
        System.out.println(buyer.getName() + " bought " + getName() + " for $" + PRICE + ".");
    }

    public void resetToBank() {
        this.owner = null;
        this.mortgaged = false;
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " landed on " + getName() + " (Utility, $" + PRICE + ").");
        if (owner == null) {
            game.offerToBuyUtility(player, this);
        } else if (owner == player) {
            System.out.println("You own this utility.");
        } else if (mortgaged) {
            System.out.println(getName() + " is mortgaged; no rent due.");
        } else {
            int rent = calculateRent(game.getDice().getTotal());
            System.out.println(player.getName() + " owes $" + rent + " rent to " + owner.getName() + ".");
            game.charge(player, rent, owner);
        }
    }
}
