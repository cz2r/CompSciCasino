import java.util.*;

public class Currency {

    private static int money = 1000; // Original amount of money
    private static String playerName;
    private static final String[] titles = { // Store titles + prices
        " the Brokey",
        " the Ace", 
        " the Jester", 
        " the All Knowing",  
        " the Lucky Duck",
        " the High Roller", 
        " the Royal Flush",
        " the Bum"
    };
    private static final int[] prices = {0, 1000, 2000, 3000, 5000, 10000, 100000, 1000000};

    private static final List<String> ownedTitles = new ArrayList<>(); // Titles that the player owns
    private static String equippedTitle = ""; // Title that the player has equipped

    // Money and player name
    public static void setMoney(int m) {
        Currency.money = Math.abs(m);
    }

    public static int getMoney() {
        return Math.abs(money);
    }

    public static void setName(String p) {
        Currency.playerName = p;
    }

    public static String getName() {
        if (!equippedTitle.isEmpty()) { // If there is an equipped title then return the name and title
            return playerName + equippedTitle;
        }
        return playerName;
    }

    // The title store

    public static void openStore(Scanner scanner) { // Accept the scanner from app.java
        while (true) {
            System.out.println("\nWelcome to the Title Store!");
            System.out.println("You currently have $" + money);
            System.out.println("Options:");
            System.out.println("1. View & buy titles");
            System.out.println("2. View owned titles / equip one");
            System.out.println("3. Exit store");
            System.out.println("Enter your choice: ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Invalid input. Please enter a number (1-3).");
                continue;
            }

            int menuChoice = scanner.nextInt();
            scanner.nextLine(); // consume the next line if invalid

            switch (menuChoice) {
                case 1 -> buyTitle(scanner);
                case 2 -> manageTitles(scanner);
                case 3 -> {
                    System.out.println("Leaving store, returning to main menu.\n");
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void buyTitle(Scanner scanner) { // If the player wants to buy a title
        System.out.println("\nAvailable Titles");
        for (int i = 0; i < titles.length; i++) {
            String status = ownedTitles.contains(titles[i]) ? " (Owned)" : ""; // Ternary operator to check if the title is owned 
            System.out.printf("%d. %-20s $%d%s%n", i + 1, titles[i], prices[i], status); // Format the output nicely (-20s for left align)
        }
        System.out.println("0. Back to Store Menu");

        while (true) {
            System.out.print("\nEnter the number of the title you want to buy: ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            if (choice == 0) {
                System.out.println("Returning to store menu");
                return;
            }

            if (choice < 1 || choice > titles.length) { 
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            int cost = prices[choice - 1];
            String title = titles[choice - 1];

            if (ownedTitles.contains(title)) {
                System.out.println("You already own that title!");
                continue;
            }

            if (money >= cost) {
                money -= cost;
                ownedTitles.add(title);
                System.out.printf("You bought%s for $%d!%nRemaining balance: $%d%n", title, cost, money); // Formatted output for better readability
            } else {
                System.out.println("You dont have enough money to buy that title.");
            }
        }
    }

    private static void manageTitles(Scanner scanner) { // If the player wants to manage their titles
        if (ownedTitles.isEmpty()) {
            System.out.println("\nYou dont own any titles yet. Buy some from the store first!");
            return;
        }

        System.out.println("\nYour Titles");
        for (int i = 0; i < ownedTitles.size(); i++) {
            String title = ownedTitles.get(i);
            String equippedMark = title.equals(equippedTitle) ? " (Equipped)" : "";
            System.out.printf("%d. %s%s%n", i + 1, title, equippedMark); // Format the output nicely
        }
        System.out.println("0. Unequip current title / Back to Store");

        while (true) {
            System.out.print("\nEnter the number of the title to equip (select equipped title to exit without unequipping): ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                equippedTitle = "";
                System.out.println("Title unequipped. Your name is now " + playerName + ".");
                return;
            }

            if (choice < 1 || choice > ownedTitles.size()) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            equippedTitle = ownedTitles.get(choice - 1);
            System.out.println("You are now known as: " + playerName + equippedTitle);
            return;
        }
    }

    public static void resetMoney(){
        Currency.setMoney(1000);
        System.out.println("Money set to $1000");
    }
}
