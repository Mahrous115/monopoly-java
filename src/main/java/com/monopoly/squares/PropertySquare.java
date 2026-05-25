package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * A street property: buyable, charges rent, supports houses and a hotel.
 *
 * Rent tiers are indexed by build state:
 *   [0] = base (doubled if owner has the full color monopoly)
 *   [1..4] = with that many houses
 *   [5] = with a hotel
 *
 * Mortgaged properties charge no rent.
 */
public class PropertySquare extends Square {

    private final String colorGroup;
    private final int price;
    private final int[] rentTiers;
    private final int houseCost;
    private final int mortgageValue;

    private Player owner;
    private int houseCount;
    private boolean isHotel;
    private boolean mortgaged;

    public PropertySquare(String name, int position, String colorGroup, int price,
                          int[] rentTiers, int houseCost) {
        super(name, position);
        this.colorGroup = colorGroup;
        this.price = price;
        this.rentTiers = rentTiers;
        this.houseCost = houseCost;
        this.mortgageValue = price / 2;
    }

    public String getColorGroup() { return colorGroup; }
    public int getPrice() { return price; }
    public int getHouseCost() { return houseCost; }
    public int getMortgageValue() { return mortgageValue; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
    public int getHouseCount() { return houseCount; }
    public boolean isHotel() { return isHotel; }
    public boolean isMortgaged() { return mortgaged; }
    public void setMortgaged(boolean mortgaged) { this.mortgaged = mortgaged; }

    public int calculateRent(Game game) {
        if (mortgaged || owner == null) return 0;
        if (isHotel) return rentTiers[5];
        if (houseCount > 0) return rentTiers[houseCount];
        if (game.getBoard().playerOwnsColorGroup(owner, colorGroup)) {
            return rentTiers[0] * 2;
        }
        return rentTiers[0];
    }

    public void buy(Player buyer) {
        this.owner = buyer;
        buyer.deductBalance(price);
        buyer.addProperty(this);
        System.out.println(buyer.getName() + " bought " + getName() + " for $" + price + ".");
    }

    /** True if owner holds the full color group and even-build rule allows adding a house here. */
    public boolean canBuildHouse(Game game) {
        if (owner == null || isHotel || mortgaged) return false;
        if (houseCount >= 4) return false;
        if (!game.getBoard().playerOwnsColorGroup(owner, colorGroup)) return false;
        int minInGroup = game.getBoard().minHousesInGroup(colorGroup);
        return houseCount <= minInGroup;
    }

    public boolean canBuildHotel(Game game) {
        if (owner == null || isHotel || mortgaged) return false;
        if (houseCount != 4) return false;
        return game.getBoard().playerOwnsColorGroup(owner, colorGroup);
    }

    public void buildHouse() {
        houseCount++;
    }

    public void buildHotel() {
        houseCount = 0;
        isHotel = true;
    }

    /** Reset to bank-owned, no buildings, not mortgaged. Used on bankruptcy. */
    public void resetToBank() {
        this.owner = null;
        this.houseCount = 0;
        this.isHotel = false;
        this.mortgaged = false;
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " landed on " + getName()
                + " (" + colorGroup + ", price $" + price + ").");
        if (owner == null) {
            game.offerToBuy(player, this);
        } else if (owner == player) {
            System.out.println("You own this property.");
        } else if (mortgaged) {
            System.out.println(getName() + " is mortgaged; no rent due.");
        } else {
            int rent = calculateRent(game);
            System.out.println(player.getName() + " owes $" + rent + " rent to " + owner.getName() + ".");
            game.charge(player, rent, owner);
        }
    }
}
