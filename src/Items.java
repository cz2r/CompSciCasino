import java.util.*;

public class Items { // Item class used to store item functionality separate from code.
    private static final int MAX_LIVES = 7;
    public static boolean useItem(String item, Map<String, Integer> inventory, boolean isPlayer, BuckshotRoulette state) {
        String actor = isPlayer ? state.playerName : "Dealer";

        if (!inventory.containsKey(item) || inventory.get(item) <= 0) {
            System.out.println(actor + " does not have that item.");
            return false;
        }

        // Prevent overusing same item type per turn
        if (item.equals("Glass")) {
            if (isPlayer && state.playerGlassUsedTsTurn) {
                System.out.println("You have already used a magnifying glass this turn.");
                return false;
            }
            if (!isPlayer && state.dealerGlassUsedTsTurn) return false;
        }
        if (item.equals("Saw")) {
            if (isPlayer && state.playerSawUsedTsTurn) return false;
            if (!isPlayer && state.dealerSawUsedTsTurn) return false;
        }

        // Consume 1 item
        inventory.put(item, inventory.get(item) - 1);
        if (inventory.get(item) == 0) inventory.remove(item);

        switch (item) {
            case "Cig" -> {
                if (isPlayer) {
                    if (state.playerLives < MAX_LIVES) {
                        state.playerLives++;
                        System.out.println(actor + " smokes a cigarette and heals 1 life.");
                    } else {
                        System.out.println(actor + " is at full health; cigarette wasted.");
                    }
                } else {
                    if (state.dealerLives < MAX_LIVES) {
                        state.dealerLives++;
                        System.out.println("Dealer smokes a cigarette and heals 1 life.");
                    } else {
                        System.out.println("Dealer is at full health; cigarette wasted.");
                    }
                }
                return true;
            }

            case "Glass" -> {
                System.out.println(actor + " peers through the magnifying glass... \"Interesting...\"");
                if (isPlayer) state.playerGlassUsedTsTurn = true;
                else state.dealerGlassUsedTsTurn = true;
                return true;
            }

            case "Saw" -> {
                if (isPlayer) {
                    state.sawUsedPlayer = true;
                    state.playerSawUsedTsTurn = true;
                    System.out.println("You attach a saw. The next live shot you fire will deal DOUBLE damage.");
                } else {
                    state.sawUsedDealer = true;
                    state.dealerSawUsedTsTurn = true;
                    System.out.println("Dealer attaches a saw. The next live shot dealer fires will deal DOUBLE damage.");
                }
                return true;
            }
        }
        return false;
    }
}