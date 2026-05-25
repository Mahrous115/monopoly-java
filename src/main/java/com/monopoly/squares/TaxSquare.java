package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Income Tax or Luxury Tax. Charges a fixed amount to the bank.
 */
public class TaxSquare extends Square {

    private final int amount;

    public TaxSquare(String name, int position, int amount) {
        super(name, position);
        this.amount = amount;
    }

    public int getAmount() { return amount; }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " landed on " + getName() + " and must pay $" + amount + ".");
        game.charge(player, amount, null);
    }
}
