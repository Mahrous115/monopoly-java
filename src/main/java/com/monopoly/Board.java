package com.monopoly;

import com.monopoly.squares.ChanceSquare;
import com.monopoly.squares.CommunityChestSquare;
import com.monopoly.squares.FreeParkingSquare;
import com.monopoly.squares.GoSquare;
import com.monopoly.squares.GoToJailSquare;
import com.monopoly.squares.JailSquare;
import com.monopoly.squares.PropertySquare;
import com.monopoly.squares.RailroadSquare;
import com.monopoly.squares.Square;
import com.monopoly.squares.TaxSquare;
import com.monopoly.squares.UtilitySquare;

import java.util.ArrayList;
import java.util.List;

/**
 * The 40-space Monopoly board. Built once at game start with the
 * canonical layout (US edition). Also answers ownership queries the
 * other classes need: "does this player own the whole color group?"
 * and "where is the nearest railroad/utility?".
 */
public class Board {

    public static final int SIZE = 40;
    public static final int JAIL_POSITION = 10;
    public static final int GO_SALARY = 200;

    private final List<Square> squares = new ArrayList<>(SIZE);

    public Board() {
        squares.add(new GoSquare(0));
        squares.add(new PropertySquare("Mediterranean Avenue", 1, "Brown", 60,
                new int[]{2, 10, 30, 90, 160, 250}, 50));
        squares.add(new CommunityChestSquare(2));
        squares.add(new PropertySquare("Baltic Avenue", 3, "Brown", 60,
                new int[]{4, 20, 60, 180, 320, 450}, 50));
        squares.add(new TaxSquare("Income Tax", 4, 200));
        squares.add(new RailroadSquare("Reading Railroad", 5));
        squares.add(new PropertySquare("Oriental Avenue", 6, "Light Blue", 100,
                new int[]{6, 30, 90, 270, 400, 550}, 50));
        squares.add(new ChanceSquare(7));
        squares.add(new PropertySquare("Vermont Avenue", 8, "Light Blue", 100,
                new int[]{6, 30, 90, 270, 400, 550}, 50));
        squares.add(new PropertySquare("Connecticut Avenue", 9, "Light Blue", 120,
                new int[]{8, 40, 100, 300, 450, 600}, 50));
        squares.add(new JailSquare(10));
        squares.add(new PropertySquare("St. Charles Place", 11, "Pink", 140,
                new int[]{10, 50, 150, 450, 625, 750}, 100));
        squares.add(new UtilitySquare("Electric Company", 12));
        squares.add(new PropertySquare("States Avenue", 13, "Pink", 140,
                new int[]{10, 50, 150, 450, 625, 750}, 100));
        squares.add(new PropertySquare("Virginia Avenue", 14, "Pink", 160,
                new int[]{12, 60, 180, 500, 700, 900}, 100));
        squares.add(new RailroadSquare("Pennsylvania Railroad", 15));
        squares.add(new PropertySquare("St. James Place", 16, "Orange", 180,
                new int[]{14, 70, 200, 550, 750, 950}, 100));
        squares.add(new CommunityChestSquare(17));
        squares.add(new PropertySquare("Tennessee Avenue", 18, "Orange", 180,
                new int[]{14, 70, 200, 550, 750, 950}, 100));
        squares.add(new PropertySquare("New York Avenue", 19, "Orange", 200,
                new int[]{16, 80, 220, 600, 800, 1000}, 100));
        squares.add(new FreeParkingSquare(20));
        squares.add(new PropertySquare("Kentucky Avenue", 21, "Red", 220,
                new int[]{18, 90, 250, 700, 875, 1050}, 150));
        squares.add(new ChanceSquare(22));
        squares.add(new PropertySquare("Indiana Avenue", 23, "Red", 220,
                new int[]{18, 90, 250, 700, 875, 1050}, 150));
        squares.add(new PropertySquare("Illinois Avenue", 24, "Red", 240,
                new int[]{20, 100, 300, 750, 925, 1100}, 150));
        squares.add(new RailroadSquare("B&O Railroad", 25));
        squares.add(new PropertySquare("Atlantic Avenue", 26, "Yellow", 260,
                new int[]{22, 110, 330, 800, 975, 1150}, 150));
        squares.add(new PropertySquare("Ventnor Avenue", 27, "Yellow", 260,
                new int[]{22, 110, 330, 800, 975, 1150}, 150));
        squares.add(new UtilitySquare("Water Works", 28));
        squares.add(new PropertySquare("Marvin Gardens", 29, "Yellow", 280,
                new int[]{24, 120, 360, 850, 1025, 1200}, 150));
        squares.add(new GoToJailSquare(30));
        squares.add(new PropertySquare("Pacific Avenue", 31, "Green", 300,
                new int[]{26, 130, 390, 900, 1100, 1275}, 200));
        squares.add(new PropertySquare("North Carolina Avenue", 32, "Green", 300,
                new int[]{26, 130, 390, 900, 1100, 1275}, 200));
        squares.add(new CommunityChestSquare(33));
        squares.add(new PropertySquare("Pennsylvania Avenue", 34, "Green", 320,
                new int[]{28, 150, 450, 1000, 1200, 1400}, 200));
        squares.add(new RailroadSquare("Short Line", 35));
        squares.add(new ChanceSquare(36));
        squares.add(new PropertySquare("Park Place", 37, "Dark Blue", 350,
                new int[]{35, 175, 500, 1100, 1300, 1500}, 200));
        squares.add(new TaxSquare("Luxury Tax", 38, 100));
        squares.add(new PropertySquare("Boardwalk", 39, "Dark Blue", 400,
                new int[]{50, 200, 600, 1400, 1700, 2000}, 200));
    }

    public Square getSquare(int position) {
        return squares.get(position);
    }

    public List<Square> getSquares() {
        return squares;
    }

    public List<PropertySquare> propertiesInGroup(String colorGroup) {
        List<PropertySquare> out = new ArrayList<>();
        for (Square s : squares) {
            if (s instanceof PropertySquare ps && ps.getColorGroup().equals(colorGroup)) {
                out.add(ps);
            }
        }
        return out;
    }

    public boolean playerOwnsColorGroup(Player player, String colorGroup) {
        List<PropertySquare> group = propertiesInGroup(colorGroup);
        if (group.isEmpty()) return false;
        for (PropertySquare ps : group) {
            if (ps.getOwner() != player) return false;
        }
        return true;
    }

    public int minHousesInGroup(String colorGroup) {
        int min = Integer.MAX_VALUE;
        for (PropertySquare ps : propertiesInGroup(colorGroup)) {
            int count = ps.isHotel() ? 5 : ps.getHouseCount();
            if (count < min) min = count;
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    public int nearestRailroad(int fromPosition) {
        int[] railroadPositions = {5, 15, 25, 35};
        for (int pos : railroadPositions) {
            if (pos > fromPosition) return pos;
        }
        return railroadPositions[0];
    }

    public int nearestUtility(int fromPosition) {
        int[] utilityPositions = {12, 28};
        for (int pos : utilityPositions) {
            if (pos > fromPosition) return pos;
        }
        return utilityPositions[0];
    }
}
