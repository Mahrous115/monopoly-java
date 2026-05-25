package com.monopoly;

import com.monopoly.squares.PropertySquare;
import com.monopoly.squares.RailroadSquare;
import com.monopoly.squares.UtilitySquare;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single Monopoly player: identity, cash, board position,
 * jail state, owned assets, and Get-Out-of-Jail cards. Mutators are
 * narrowly scoped so the Game and Square classes can coordinate state
 * without each other reaching into raw fields.
 */
public class Player {

    private static final int STARTING_BALANCE = 1500;

    private final String name;
    private int balance;
    private int position;
    private boolean inJail;
    private int jailTurns;
    private int getOutOfJailCards;
    private boolean bankrupt;

    private final List<PropertySquare> ownedProperties = new ArrayList<>();
    private final List<RailroadSquare> ownedRailroads = new ArrayList<>();
    private final List<UtilitySquare> ownedUtilities = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.balance = STARTING_BALANCE;
        this.position = 0;
    }

    public String getName() { return name; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }
    public void addBalance(int amount) { this.balance += amount; }
    public void deductBalance(int amount) { this.balance -= amount; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public boolean isInJail() { return inJail; }
    public void setInJail(boolean inJail) { this.inJail = inJail; }

    public int getJailTurns() { return jailTurns; }
    public void setJailTurns(int jailTurns) { this.jailTurns = jailTurns; }
    public void incrementJailTurns() { this.jailTurns++; }

    public int getGetOutOfJailCards() { return getOutOfJailCards; }
    public void addGetOutOfJailCard() { this.getOutOfJailCards++; }
    public void useGetOutOfJailCard() { this.getOutOfJailCards--; }

    public boolean isBankrupt() { return bankrupt; }
    public void setBankrupt(boolean bankrupt) { this.bankrupt = bankrupt; }

    public List<PropertySquare> getOwnedProperties() { return ownedProperties; }
    public List<RailroadSquare> getOwnedRailroads() { return ownedRailroads; }
    public List<UtilitySquare> getOwnedUtilities() { return ownedUtilities; }

    public void addProperty(PropertySquare property) { ownedProperties.add(property); }
    public void addRailroad(RailroadSquare railroad) { ownedRailroads.add(railroad); }
    public void addUtility(UtilitySquare utility) { ownedUtilities.add(utility); }

    public int totalAssets() {
        return balance;
    }

    @Override
    public String toString() {
        return name + " (balance $" + balance + ", position " + position + ")";
    }
}
