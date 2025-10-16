import java.util.*;

public class BuckshotRoulette {
    static Random rand = new Random();
    static Scanner sc = new Scanner(System.in);

    // Player state
    String playerName;
    int playerLives = 7;
    int dealerLives = 7;
    static final int MAX_LIVES = 7;
    static final int MAX_ITEMS = 8;
    Map<String, Integer> playerItems = new HashMap<>();
    Map<String, Integer> dealerItems = new HashMap<>();

    // Game state
    static final int TOTAL_SHELLS = 8;
    LinkedList<Boolean> chamber = new LinkedList<>();
    boolean sawUsedPlayer = false;
    boolean sawUsedDealer = false;
    boolean playerGlassUsedTsTurn = false;
    boolean playerSawUsedTsTurn = false;
    boolean dealerGlassUsedTsTurn = false;
    boolean dealerSawUsedTsTurn = false;

    public static void main(String[] args) {
        BuckshotRoulette game = new BuckshotRoulette();
        game.startGame();
    }

    public void startGame() {
        playerName = Currency.getName();

        setupGun();

        boolean playerTurn = true;
        while (playerLives > 0 && dealerLives > 0) {
            if (chamber.isEmpty()) setupGun();
            perTurnUsageReset();
            randomItem(playerItems);
            randomItem(dealerItems);
            showStatus();

            if (playerTurn)
                playerTurn = playerAction();
            else
                playerTurn = !dealerAction();
        }
        System.out.println("\n=== Game Over ===");
        System.out.println(playerLives > 0 ? playerName + " wins!" : "Dealer wins!");
    }

    void setupGun() {
        chamber.clear();
        int liveShells = rand.nextInt(4) + 3; // 3–7 lives
        int blankShells = TOTAL_SHELLS - liveShells;
        for (int i = 0; i < liveShells; i++) chamber.add(true);
        for (int i = 0; i < blankShells; i++) chamber.add(false);
        Collections.shuffle(chamber);
        System.out.println("\nShotgun reloaded: " + liveShells + " live, " + blankShells + " blank.");
        pause("", 500);
    }

    void showStatus() {
        System.out.println("\n----------------------------");
        System.out.printf("%-12s | %s (%d/%d)\n", playerName, "+".repeat(playerLives), playerLives, MAX_LIVES);
        System.out.printf("%-12s | %s (%d/%d)\n", "Dealer", "+".repeat(dealerLives), dealerLives, MAX_LIVES);
        System.out.println("----------------------------");
        System.out.printf("%-12s | %s\n", playerName, playerItems);
        System.out.printf("%-12s | %s\n", "Dealer", dealerItems);
        System.out.println("----------------------------");
    }

    void perTurnUsageReset() {
        playerGlassUsedTsTurn = false;
        playerSawUsedTsTurn = false;
        dealerGlassUsedTsTurn = false;
        dealerSawUsedTsTurn = false;
    }

    void randomItem(Map<String, Integer> items) {
        if (items.values().stream().mapToInt(Integer::intValue).sum() >= MAX_ITEMS) return;
        String[] pool = {"Glass", "Cig", "Saw"};
        String item = pool[rand.nextInt(pool.length)];
        items.put(item, items.getOrDefault(item, 0) + 1);
    }

    boolean playerAction() {
        System.out.println("\nYour turn, " + playerName + "!");
        System.out.println("Use item? (y/n)");
        String input = sc.nextLine().toLowerCase();
        if (input.equals("y") && !playerItems.isEmpty()) {
            String item = inputControl(playerItems);
            Items.useItem(item, playerItems, true, this);
        }

        System.out.println("Shoot 1 = Dealer, 2 = Yourself");
        int target = Integer.parseInt(sc.nextLine());
        boolean extraTurn = fire(target == 1 ? "dealer" : "player", playerName, sawUsedPlayer);
        sawUsedPlayer = false;
        return extraTurn;
    }

    boolean dealerAction() {
        System.out.println("\nDealer’s turn...");
        boolean nextShellLive = chamber.peek();
        boolean usedGlass;

        if (dealerItems.containsKey("Glass")) {
            System.out.println("Dealer peers through the magnifying glass... \"Interesting...\"");
            usedGlass = true;
            boolean glassSawRound = chamber.peek();

            // If live - attach saw and shoot player
            if (glassSawRound) {
                if (dealerItems.containsKey("Saw") && !dealerSawUsedTsTurn)
                    Items.useItem("Saw", dealerItems, false, this);
                System.out.println("Dealer aims at you...");
                return fire("player", "Dealer", sawUsedDealer);
            } else {
                // Blank - shoot self
                System.out.println("Dealer aims at himself...");
                return fire("dealer", "Dealer", sawUsedDealer);
            }
        }

        // Default random logic if no glass
        if (nextShellLive) {
            System.out.println("Dealer aims at you...");
            return fire("player", "Dealer", sawUsedDealer);
        } else {
            System.out.println("Dealer aims at himself...");
            return fire("dealer", "Dealer", sawUsedDealer);
        }
    }

    boolean fire(String target, String shooter, boolean doubled) {
        boolean shell = chamber.poll();
        pause("", 1000);
        if (shell) {
            int dmg = doubled ? 2 : 1;
            if (target.equals("player")) {
                playerLives -= dmg;
                System.out.println("BANG! You lost " + dmg + " life" + (dmg > 1 ? "s!" : "!"));
            } else {
                dealerLives -= dmg;
                System.out.println("BANG! Dealer lost " + dmg + " life" + (dmg > 1 ? "s!" : "!"));
            }
            pause("Turn ends.", 1000);
            return false;
        } else {
            System.out.println("Click... blank round.");
            if ((target.equals("player") && shooter.equals(playerName)) ||
                (target.equals("dealer") && shooter.equals("Dealer"))) {
                System.out.println(shooter + " gains an extra turn.");
                return true;
            }
            pause("Turn ends.", 1000);
            return false;
        }
    }

    String inputControl(Map<String, Integer> items) {
        System.out.println("Available: " + items.keySet());
        String item = sc.nextLine().trim();
        return items.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(item) || k.toLowerCase().startsWith(item.toLowerCase()))
                .findFirst().orElse("Cig");
    }

    void pause(String msg, int ms) {
        try {
            if (!msg.isEmpty()) System.out.println(msg);
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}