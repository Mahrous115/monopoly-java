package com.monopoly;

import com.monopoly.cards.CardDeck;
import com.monopoly.squares.PropertySquare;
import com.monopoly.squares.RailroadSquare;
import com.monopoly.squares.Square;
import com.monopoly.squares.UtilitySquare;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Top-level controller. Owns the Board, the player list, the dice, the
 * two card decks, and the Scanner used for all console interaction.
 *
 * Game state mutations that span multiple classes (movement with GO
 * salary, jail entry, charge with bankruptcy fallback, bulk transfers
 * triggered by cards) live here so the Square / Card classes can stay
 * focused on their own rules.
 */
public class Game {

    private final Board board = new Board();
    private final List<Player> players = new ArrayList<>();
    private final Dice dice = new Dice();
    private final CardDeck chanceDeck = CardDeck.buildChanceDeck();
    private final CardDeck communityChestDeck = CardDeck.buildCommunityChestDeck();
    private final Scanner scanner = new Scanner(System.in);
    private final Random extraDiceRandom = new Random();

    public Board getBoard() { return board; }
    public Dice getDice() { return dice; }
    public CardDeck getChanceDeck() { return chanceDeck; }
    public CardDeck getCommunityChestDeck() { return communityChestDeck; }
    public List<Player> getPlayers() { return players; }

    // ---------------------------------------------------------------- lifecycle

    public void start() {
        System.out.println("=================================");
        System.out.println("        MONOPOLY (Java)");
        System.out.println("=================================");
        setupPlayers();

        outer:
        while (activePlayerCount() > 1) {
            for (Player p : players) {
                if (p.isBankrupt()) continue;
                takeTurn(p);
                if (activePlayerCount() <= 1) break outer;
            }
        }
        announceWinner();
    }

    private void setupPlayers() {
        int count = readInt("How many players? (2-4): ", 2, 4);
        for (int i = 1; i <= count; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Player " + i;
            players.add(new Player(name));
        }
        System.out.println();
    }

    private int activePlayerCount() {
        int n = 0;
        for (Player p : players) if (!p.isBankrupt()) n++;
        return n;
    }

    private void announceWinner() {
        Player winner = null;
        for (Player p : players) if (!p.isBankrupt()) winner = p;
        System.out.println();
        System.out.println("================================");
        if (winner != null) {
            System.out.println("  WINNER: " + winner.getName()
                    + " (balance $" + winner.getBalance() + ")");
        } else {
            System.out.println("  Game ended with no survivors.");
        }
        System.out.println("================================");
    }

    // ---------------------------------------------------------------- turn loop

    private void takeTurn(Player p) {
        printBoardState();
        System.out.println();
        System.out.println(">>> " + p.getName() + "'s turn (balance $" + p.getBalance() + ")");
        pause();

        if (p.isInJail()) {
            boolean releasedAndMoving = handleJailTurn(p);
            if (!releasedAndMoving) {
                postTurnPhase(p);
                return;
            }
        }

        dice.resetConsecutiveDoubles();
        boolean rollAgain;
        do {
            rollAgain = false;
            dice.roll();
            System.out.println(p.getName() + " rolled " + dice.getDie1() + " + " + dice.getDie2()
                    + " = " + dice.getTotal() + (dice.isDoubles() ? " (doubles!)" : ""));

            if (dice.isDoubles() && dice.getConsecutiveDoubles() >= 3) {
                System.out.println("Three doubles in a row — straight to Jail!");
                sendToJail(p);
                break;
            }

            advancePlayerBy(p, dice.getTotal());
            if (p.isBankrupt() || p.isInJail()) break;

            if (dice.isDoubles()) {
                System.out.println("Doubles! " + p.getName() + " rolls again.");
                rollAgain = true;
                pause();
            }
        } while (rollAgain);

        if (!p.isBankrupt()) {
            postTurnPhase(p);
        }
    }

    private void postTurnPhase(Player p) {
        if (p.isBankrupt()) return;
        offerBuildMenu(p);
        printTurnSummary(p);
        System.out.println();
    }

    private boolean handleJailTurn(Player p) {
        System.out.println(p.getName() + " is in Jail (turn " + (p.getJailTurns() + 1) + " of 3).");
        System.out.println("Options:");
        System.out.println("  1) Pay $50 bail and roll");
        if (p.getGetOutOfJailCards() > 0) {
            System.out.println("  2) Use Get Out of Jail Free card and roll");
        }
        System.out.println("  3) Try to roll doubles");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            if (p.getBalance() < 50) {
                System.out.println("Can't afford bail — must try rolling doubles instead.");
            } else {
                p.deductBalance(50);
                p.setInJail(false);
                p.setJailTurns(0);
                System.out.println(p.getName() + " paid $50 bail.");
                dice.roll();
                System.out.println("Rolled " + dice.getDie1() + " + " + dice.getDie2() + " = " + dice.getTotal());
                advancePlayerBy(p, dice.getTotal());
                return true;
            }
        } else if (choice.equals("2") && p.getGetOutOfJailCards() > 0) {
            p.useGetOutOfJailCard();
            p.setInJail(false);
            p.setJailTurns(0);
            System.out.println(p.getName() + " used a Get Out of Jail Free card.");
            dice.roll();
            System.out.println("Rolled " + dice.getDie1() + " + " + dice.getDie2() + " = " + dice.getTotal());
            advancePlayerBy(p, dice.getTotal());
            return true;
        }

        // Try to roll doubles
        dice.roll();
        System.out.println("Rolled " + dice.getDie1() + " + " + dice.getDie2() + " = " + dice.getTotal()
                + (dice.isDoubles() ? " (doubles!)" : ""));
        if (dice.isDoubles()) {
            p.setInJail(false);
            p.setJailTurns(0);
            System.out.println(p.getName() + " escapes Jail with doubles!");
            dice.resetConsecutiveDoubles();
            advancePlayerBy(p, dice.getTotal());
            return true;
        }
        p.incrementJailTurns();
        if (p.getJailTurns() >= 3) {
            System.out.println("Third failed attempt — must pay $50 bail.");
            charge(p, 50, null);
            if (p.isBankrupt()) return false;
            p.setInJail(false);
            p.setJailTurns(0);
            advancePlayerBy(p, dice.getTotal());
            return true;
        }
        System.out.println(p.getName() + " stays in Jail.");
        return false;
    }

    private void offerBuildMenu(Player p) {
        List<PropertySquare> buildable = new ArrayList<>();
        for (PropertySquare prop : p.getOwnedProperties()) {
            if (prop.canBuildHouse(this) || prop.canBuildHotel(this)) {
                buildable.add(prop);
            }
        }
        if (buildable.isEmpty()) return;
        System.out.println();
        System.out.println("Build menu — you can build on:");
        for (int i = 0; i < buildable.size(); i++) {
            PropertySquare ps = buildable.get(i);
            String what = ps.canBuildHotel(this) ? "HOTEL (4 houses already)" : "house";
            System.out.println("  " + (i + 1) + ") " + ps.getName()
                    + " [" + ps.getColorGroup() + "] houses=" + ps.getHouseCount()
                    + " cost=$" + ps.getHouseCost() + " — " + what);
        }
        System.out.print("Build on which? (number, or Enter to skip): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;
        int idx;
        try { idx = Integer.parseInt(input) - 1; } catch (NumberFormatException e) { return; }
        if (idx < 0 || idx >= buildable.size()) return;
        PropertySquare target = buildable.get(idx);
        if (p.getBalance() < target.getHouseCost()) {
            System.out.println("Not enough cash to build (need $" + target.getHouseCost() + ").");
            return;
        }
        p.deductBalance(target.getHouseCost());
        if (target.canBuildHotel(this)) {
            target.buildHotel();
            System.out.println("Built a HOTEL on " + target.getName() + ".");
        } else {
            target.buildHouse();
            System.out.println("Built a house on " + target.getName() + " (now " + target.getHouseCount() + ").");
        }
    }

    private void printTurnSummary(Player p) {
        System.out.println("--- End of " + p.getName() + "'s turn: balance $" + p.getBalance()
                + ", position " + p.getPosition() + " (" + board.getSquare(p.getPosition()).getName() + ") ---");
    }

    private void printBoardState() {
        System.out.println();
        System.out.println("Board state:");
        for (Player p : players) {
            String status = p.isBankrupt() ? "BANKRUPT" :
                    "pos " + p.getPosition() + " (" + board.getSquare(p.getPosition()).getName() + ")"
                    + ", $" + p.getBalance()
                    + (p.isInJail() ? ", IN JAIL" : "");
            System.out.println("  " + p.getName() + ": " + status);
        }
    }

    // ---------------------------------------------------------------- movement

    /** Move forward {@code spaces} squares, paying $200 for any GO wrap, then land. */
    public void advancePlayerBy(Player p, int spaces) {
        int newPos = (p.getPosition() + spaces) % Board.SIZE;
        if (newPos < p.getPosition()) {
            p.addBalance(Board.GO_SALARY);
            System.out.println(p.getName() + " passed GO and collected $" + Board.GO_SALARY + ".");
        }
        p.setPosition(newPos);
        Square landed = board.getSquare(newPos);
        System.out.println(p.getName() + " moves to " + landed.getName() + ".");
        landed.landOn(p, this);
    }

    /** Jump to a specific square. Pays GO if wrapping forward. Lands on arrival. */
    public void advancePlayerTo(Player p, int targetPosition) {
        int oldPos = p.getPosition();
        if (targetPosition <= oldPos) {
            p.addBalance(Board.GO_SALARY);
            System.out.println(p.getName() + " passed GO and collected $" + Board.GO_SALARY + ".");
        }
        p.setPosition(targetPosition);
        Square landed = board.getSquare(targetPosition);
        System.out.println(p.getName() + " moves to " + landed.getName() + ".");
        landed.landOn(p, this);
    }

    /** Used by cards that handle their own rent logic (railroad x2, utility x10). */
    public void movePlayerToNoLand(Player p, int targetPosition) {
        int oldPos = p.getPosition();
        if (targetPosition <= oldPos) {
            p.addBalance(Board.GO_SALARY);
            System.out.println(p.getName() + " passed GO and collected $" + Board.GO_SALARY + ".");
        }
        p.setPosition(targetPosition);
    }

    public void sendToJail(Player p) {
        p.setPosition(Board.JAIL_POSITION);
        p.setInJail(true);
        p.setJailTurns(0);
        dice.resetConsecutiveDoubles();
        System.out.println(p.getName() + " is now in Jail.");
    }

    /** Independent roll used by the "pay 10x dice" Chance card. */
    public int rollExtraDice() {
        int d1 = extraDiceRandom.nextInt(6) + 1;
        int d2 = extraDiceRandom.nextInt(6) + 1;
        System.out.println("Special dice roll: " + d1 + " + " + d2 + " = " + (d1 + d2));
        return d1 + d2;
    }

    // ---------------------------------------------------------------- money

    /** Pay {@code amount} from debtor to creditor (or bank if creditor is null). */
    public void charge(Player debtor, int amount, Player creditor) {
        if (amount <= 0) return;
        if (debtor.getBalance() >= amount) {
            debtor.deductBalance(amount);
            if (creditor != null) creditor.addBalance(amount);
            return;
        }
        int remaining = debtor.getBalance();
        if (creditor != null) creditor.addBalance(remaining);
        debtor.setBalance(0);
        System.out.println(debtor.getName() + " could not pay $" + amount + " — BANKRUPT.");
        handleBankruptcy(debtor);
    }

    private void handleBankruptcy(Player p) {
        p.setBankrupt(true);
        for (PropertySquare ps : new ArrayList<>(p.getOwnedProperties())) ps.resetToBank();
        for (RailroadSquare rr : new ArrayList<>(p.getOwnedRailroads())) rr.resetToBank();
        for (UtilitySquare u : new ArrayList<>(p.getOwnedUtilities())) u.resetToBank();
        p.getOwnedProperties().clear();
        p.getOwnedRailroads().clear();
        p.getOwnedUtilities().clear();
        System.out.println("All of " + p.getName() + "'s properties return to the bank.");
    }

    public void payAllOtherPlayers(Player payer, int amount) {
        for (Player other : players) {
            if (other == payer || other.isBankrupt()) continue;
            charge(payer, amount, other);
            if (payer.isBankrupt()) return;
        }
    }

    public void collectFromAllOtherPlayers(Player receiver, int amount) {
        for (Player other : players) {
            if (other == receiver || other.isBankrupt()) continue;
            charge(other, amount, receiver);
        }
    }

    // ---------------------------------------------------------------- buy offers

    public void offerToBuy(Player p, PropertySquare prop) {
        if (p.getBalance() < prop.getPrice()) {
            System.out.println("You can't afford " + prop.getName() + " ($" + prop.getPrice() + ").");
            return;
        }
        System.out.print("Buy " + prop.getName() + " for $" + prop.getPrice() + "? (y/n): ");
        String in = scanner.nextLine().trim().toLowerCase();
        if (in.startsWith("y")) prop.buy(p);
        else System.out.println(p.getName() + " passes on " + prop.getName() + ".");
    }

    public void offerToBuyRailroad(Player p, RailroadSquare rr) {
        if (p.getBalance() < RailroadSquare.PRICE) {
            System.out.println("You can't afford " + rr.getName() + ".");
            return;
        }
        System.out.print("Buy " + rr.getName() + " for $" + RailroadSquare.PRICE + "? (y/n): ");
        String in = scanner.nextLine().trim().toLowerCase();
        if (in.startsWith("y")) rr.buy(p);
        else System.out.println(p.getName() + " passes on " + rr.getName() + ".");
    }

    public void offerToBuyUtility(Player p, UtilitySquare u) {
        if (p.getBalance() < UtilitySquare.PRICE) {
            System.out.println("You can't afford " + u.getName() + ".");
            return;
        }
        System.out.print("Buy " + u.getName() + " for $" + UtilitySquare.PRICE + "? (y/n): ");
        String in = scanner.nextLine().trim().toLowerCase();
        if (in.startsWith("y")) u.buy(p);
        else System.out.println(p.getName() + " passes on " + u.getName() + ".");
    }

    // ---------------------------------------------------------------- io helpers

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
