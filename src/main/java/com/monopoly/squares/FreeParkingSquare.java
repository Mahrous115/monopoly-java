package com.monopoly.squares;

import com.monopoly.Game;
import com.monopoly.Player;

/**
 * Free Parking. In the classic rules nothing happens here.
 */
public class FreeParkingSquare extends Square {

    public FreeParkingSquare(int position) {
        super("Free Parking", position);
    }

    @Override
    public void landOn(Player player, Game game) {
        System.out.println(player.getName() + " is enjoying Free Parking.");
    }
}
