import java.util.*;

public class BuckshotRoulette {
    static Random rand = new Random();
    static Scanner sc = new Scanner(System.in);

    static String playerName;
    static int playerLives = 7;
    static int dealerLives = 7;
    static final int MAX_LIVES = 7;
    static final int MAX_ITEMS = 8;
    static Map<String, Integer> playerItems = new HashMap<>();
    static Map<String, Integer> dealerItems = new HashMap<>();
    // Global static maps to store dealer and player information

    static final int TOTAL_SHELLS = 8; // changed to 8 shells
    static LinkedList<Boolean> chamber = new LinkedList<>();

    // persistent "next-shot double" flags
    static boolean sawActivePlayer = false;
    static boolean sawActiveDealer = false;

    // per-turn usage-block flags (prevents re-using Glass or Saw in same turn)
    static boolean playerGlassUsedThisTurn = false;
    static boolean playerSawUsedThisTurn = false;
    static boolean dealerGlassUsedThisTurn = false;
    static boolean dealerSawUsedThisTurn = false;

    public static void game() {
        playerName = Currency.getName();
        resetGame();

        System.out.println("\n=== Buckshot Roulette ===\n");

        boolean playerTurn = true;

        while (playerLives > 0 && dealerLives > 0) {

            // If chamber empty, reload (and display live/blank counts only on reload)
            if (chamber.isEmpty()) {
                pause("--- Chamber empty. Reloading in any order... ---", 1500);
                setupGun();
            }

            // Reset per-turn usage flags at start of each turn
            resetPerTurnUsageFlags();

            // Give one random item each round (if under max of 8)
            assignRandomItem(playerItems);
            assignRandomItem(dealerItems);

            showStatus(); //shows lives and items of both dealer and player

            if (playerTurn) {
                playerTurn = playerAction();
            } else {
                playerTurn = !dealerAction();
            }
        }

        pause("\n=== Game Over ===", 1500);
        if (playerLives <= 0 && dealerLives <= 0) {
            System.out.println("It's a draw!");
        } else if (playerLives <= 0) {
            System.out.println("Dealer wins!");
        } else {
            System.out.println(playerName + " wins!");
        }
    }

    static void resetGame() {
        playerLives = MAX_LIVES;
        dealerLives = MAX_LIVES;
        playerItems.clear();
        dealerItems.clear();
        sawActivePlayer = false;
        sawActiveDealer = false;
        setupGun();
    }

    // Randomise live blanks with at least 3 live rounds. Print counts only on reload.
    static void setupGun() {
        chamber.clear();
        // random live between 3 and 8 inclusive (ensures at least 3 live)
        int liveShells = rand.nextInt(4) + 3; // 0..5 + 3 => 3..8
        int blankShells = TOTAL_SHELLS - liveShells;

        for (int i = 0; i < liveShells; i++) chamber.add(true);
        for (int i = 0; i < blankShells; i++) chamber.add(false);

        Collections.shuffle(chamber);

        System.out.println("================================================================");
        System.out.println("Shotgun reloaded: " + liveShells + " live, " + blankShells + " blank.");
    }

    // Assign exactly one random item to the given inventory if under MAX_ITEMS
    static void assignRandomItem(Map<String, Integer> items) {
        if (items.values().stream().mapToInt(Integer::intValue).sum() >= MAX_ITEMS) return;
        String[] pool = {"Glass", "Cig", "Saw"};
        String item = pool[rand.nextInt(pool.length)];
        items.put(item, items.getOrDefault(item, 0) + 1);
    }

    // Show status with '+' repeated for lives
    static void showStatus() {
    int maxLen = MAX_LIVES;

    String playerHearts = "+".repeat(playerLives);
    String dealerHearts = "+".repeat(dealerLives);

    String playerPadding = " ".repeat(maxLen - playerLives);
    String dealerPadding = " ".repeat(maxLen - dealerLives);

    System.out.println("Lives:");
    System.out.println("\n------------------------------");
    System.out.printf("%-12s | %s%s (%d/%d)\n", playerName, playerHearts, playerPadding, playerLives, MAX_LIVES);
    System.out.printf("%-12s | %s%s (%d/%d)\n", "Dealer", dealerHearts, dealerPadding, dealerLives, MAX_LIVES);
    System.out.println("------------------------------");

    System.out.println("Items:");
    System.out.printf("%-12s | %s\n", playerName, playerItems.isEmpty() ? "None" : playerItems);
    System.out.printf("%-12s | %s\n", "Dealer", dealerItems.isEmpty() ? "None" : dealerItems);
    System.out.println("------------------------------");
    System.out.println("Chamber shells left: " + chamber.size());
    System.out.println();
}


    // Reset per-turn flags so Glass/Saw can be used again next turn
    static void resetPerTurnUsageFlags() {
        playerGlassUsedThisTurn = false;
        playerSawUsedThisTurn = false;
        dealerGlassUsedThisTurn = false;
        dealerSawUsedThisTurn = false;
    }

    // Player turn; returns true if player gets an extra turn (shoot self blank), false otherwise
    static boolean playerAction() {
        System.out.println("\nYour turn, " + playerName + "!");
        boolean extraTurn = false;
        boolean turnEnded = false;

        while (!turnEnded) {
            // Allow the player to use items first — can use multiple items,
            // but Glass and Saw cannot be used more than once each in the same turn.
            if (!playerItems.isEmpty()) {
                System.out.println("Do you want to use an item? (y/n)");
                String input = getYesNo();
                if (input.equals("y")) {
                    // Present only items that exist AND are allowed this turn
                    List<String> allowed = getAllowedItemsForPlayer();
                    if (allowed.isEmpty()) {
                        System.out.println("No usable items available this turn (Glass/Saw already used). You can still use Cigarettes.");
                    } else {
                        String chosen = pickItemFlexible(allowed);
                        useItem(chosen, playerItems, true);
                        showStatus();
                        pause("", 900);
                        continue; // allow further item usage or decision to fire
                    }
                }
            }

            // Fire choice
            System.out.println("Choose target: 1 = Dealer, 2 = Yourself");
            int targetChoice = getInt(1, 2);

            if (targetChoice == 1) {
                System.out.println("You aim at the dealer...");
                extraTurn = fire("dealer", playerName, sawActivePlayer);
                // saw effect consumes only on actual shot (we reset regardless)
                sawActivePlayer = false;
            } else {
                System.out.println("You aim at yourself...");
                extraTurn = fire("player", playerName, sawActivePlayer);
                sawActivePlayer = false;
            }
            // firing ends the player's decision loop; extraTurn decides if player keeps turn
            turnEnded = true;
        }
        return extraTurn;
    }

    // Build a list of allowed items for the player to select (accounts for per-turn usage blocks)
    static List<String> getAllowedItemsForPlayer() {
        List<String> allowed = new ArrayList<>();
        for (String k : playerItems.keySet()) {
            switch (k) {
                case "Cig" -> {
                    // Cig allowed always if present
                    if (playerItems.get(k) > 0) allowed.add(k);
                }
                case "Glass" -> {
                    if (playerItems.get(k) > 0 && !playerGlassUsedThisTurn) allowed.add(k);
                }
                case "Saw" -> {
                    if (playerItems.get(k) > 0 && !playerSawUsedThisTurn) allowed.add(k);
                }
                default -> {
                }
            }
        }
        return allowed;
    }

    // Dealer turn: smarter decisions; returns true if dealer gets an extra turn
    static boolean dealerAction() {
        System.out.println("\nDealer's turn...");
        boolean extraTurn;

        // Dealer checks next shell chance (but doesn't reveal it unless uses Glass)
        boolean nextShellLive = chamber.peek() != null && chamber.peek();

        // Dealer evaluates items and uses at most one item per turn (and respects per-turn usage rules)
        if (!dealerItems.isEmpty()) {
            // If dealer is injured, prefer Cig
            if (dealerItems.containsKey("Cig") && dealerLives < MAX_LIVES) {
                // use cigarette
                useItem("Cig", dealerItems, false);
                pause("", 900);
            } else {
                // If next shell likely live, try to use saw (if not used this turn) to increase damage
                if (nextShellLive && dealerItems.containsKey("Saw") && !dealerSawUsedThisTurn) {
                    useItem("Saw", dealerItems, false);
                    pause("", 900);
                } else if (dealerItems.containsKey("Glass") && !dealerGlassUsedThisTurn) {
                    // If dealer has glass and will benefit, use it
                    useItem("Glass", dealerItems, false);
                    pause("", 900);
                }
            }
        }

        // Dealer decision: if next shell is live, favor shooting the player; if blank is likely,
        // sometimes risk shooting himself for an extra turn (but avoid saw+self)
        boolean shootSelf = false;
        // If dealer used saw this turn, avoid shooting self
        if (sawActiveDealer) {
            shootSelf = false;
        } else {
            double liveRatio = (double) chamber.stream().filter(b -> b).count() / chamber.size();
            // If probability of live is low, sometimes choose self to gain extra turn
            if (liveRatio < 0.5) {
                // 60% chance to risk self-shot if blanks are more likely
                shootSelf = rand.nextInt(100) < 80;
            }
        }

        if (shootSelf) {
            System.out.println("Dealer aims at himself...");
            extraTurn = fire("dealer", "Dealer", sawActiveDealer);
            sawActiveDealer = false;
        } else {
            System.out.println("Dealer aims at you...");
            extraTurn = fire("player", "Dealer", sawActiveDealer);
            sawActiveDealer = false;
        }
        return extraTurn;
    }

    // Unified item use: returns true if applied successfully
    // If isPlayer==true, actor is player; else dealer
    static boolean useItem(String item, Map<String, Integer> inventory, boolean isPlayer) {
        String actor = isPlayer ? playerName : "Dealer";

        // Validate presence
        if (!inventory.containsKey(item) || inventory.get(item) <= 0) {
            System.out.println(actor + " does not have that item.");
            return false;
        }

        // Block repeated usage of Glass/Saw in same turn (Cigarette allowed multiple times)
        if (item.equals("Glass")) {
            if (isPlayer && playerGlassUsedThisTurn) {
                System.out.println("You have already used a magnifying glass this turn.");
                return false;
            }
            if (!isPlayer && dealerGlassUsedThisTurn) {
                System.out.println("Dealer has already used a magnifying glass this turn.");
                return false;
            }
        }
        if (item.equals("Saw")) {
            if (isPlayer && playerSawUsedThisTurn) {
                System.out.println("You have already used a saw this turn.");
                return false;
            }
            if (!isPlayer && dealerSawUsedThisTurn) {
                System.out.println("Dealer has already used a saw this turn.");
                return false;
            }
        }

        // Consume item from inventory (for Cigarettes, we still consume but allow further uses)
        inventory.put(item, inventory.get(item) - 1);
        if (inventory.get(item) == 0) inventory.remove(item);

        switch (item) {
            case "Cig" -> {
                if (isPlayer) {
                    if (playerLives < MAX_LIVES) {
                        playerLives++;
                        System.out.println(playerName + " smokes a cigarette and heals 1 life.");
                    } else {
                        System.out.println(playerName + " is at full health; cigarette wasted.");
                    }
                } else {
                    if (dealerLives < MAX_LIVES) {
                        dealerLives++;
                        System.out.println("Dealer smokes a cigarette and heals 1 life.");
                    } else {
                        System.out.println("Dealer is at full health; cigarette wasted.");
                    }
                }
                // Cigarettes do not set per-turn block
                return true;
            }
            case "Glass" -> {
                // reveal next shell
                boolean next = chamber.peek() != null && chamber.peek();
                System.out.println(actor + " uses a magnifying glass. The next shell is " + (next ? "LIVE." : "BLANK."));
                if (isPlayer) playerGlassUsedThisTurn = true;
                else dealerGlassUsedThisTurn = true;
                return true;
            }
            case "Saw" -> {
                if (isPlayer) {
                    sawActivePlayer = true;
                    playerSawUsedThisTurn = true;
                    System.out.println("You attach a saw. The next live shot you fire will deal DOUBLE damage.");
                } else {
                    sawActiveDealer = true;
                    dealerSawUsedThisTurn = true;
                    System.out.println("Dealer attaches a saw. The next live shot dealer fires will deal DOUBLE damage.");
                }
                return true;
            }
            default -> {
                System.out.println("Unknown item.");
                return false;
            }
        }
    }

    // uses nextLine() to consume user input robustly
    static String pickItemFlexible(List<String> available) {
        System.out.println("Pick an item: " + available + " (case-insensitive)");
        while (true) {
            String input = sc.nextLine().trim().toLowerCase();
            for (String item : available) {
                if (input.equals(item.toLowerCase()) || input.equals(item.substring(0, 1).toLowerCase())) {
                    return item;
                }
            }
            System.out.println("Invalid choice. Try again.");
        }
    }

    // fire: returns true if shooter gets extra turn (self-blank), false otherwise
    static boolean fire(String target, String shooter, boolean doubled) {

        boolean shell = chamber.poll();

        // pauses for dramatic effect
        pause("", 1300);

        if (shell) {
            int damage = doubled ? 2 : 1;
            if (target.equals("player")) {
                playerLives -= damage;
                System.out.println("BANG! You lost " + damage + (damage == 1 ? " life." : " lives."));
            } else {
                dealerLives -= damage;
                System.out.println("BANG! Dealer lost " + damage + (damage == 1 ? " life." : " lives."));
            }
            pause("Turn ends.", 1200);
            return false; // hit ends the turn
        } else {
            System.out.println("Click... blank round.");
            // If shooter shot themself and it was blank, they gain extra turn
            if ((target.equals("player") && shooter.equals(playerName)) ||
                (target.equals("dealer") && shooter.equals("Dealer"))) {
                System.out.println((shooter.equals(playerName) ? "You" : "Dealer") + " gain an extra turn.");
                pause("", 1200);
                return true;
            }
            pause("Turn ends.", 1200);
            return false;
        }
    }

    // get y/n validated input (consumes the rest of line)
    static String getYesNo() {
        while (true) {
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("n")) return input;
            System.out.println("Please enter 'y' or 'n'.");
        }
    }

    // getInt validated input (consumes newline)
    static int getInt(int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a valid option (" + min + "-" + max + ").");
        }
    }

    static void pause(String msg, int ms) {
        try {
            if (!msg.isEmpty()) System.out.println(msg);
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
