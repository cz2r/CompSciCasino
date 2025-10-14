import java.util.Scanner;

public class Currency {

    private static int money = 1000; // Original amount of money
    private static String playerName;
    private static final String[] titles = { // Store titles + prices
        " the Ace", 
        " the Jester", 
        " the All Knowing",  
        " the Lucky Duck",
        " the High Roller", 
        " the Royal Flush",
        " the Bum"
    };
    private static final int[] prices = {1000, 2000, 3000, 5000, 10000, 100000, 1000000};

    public static void setMoney(int m){ // Getter and setter for the money value
        Currency.money = Math.abs(m); // Ensure that money is never negative
    }

    public static int getMoney(){
        return Math.abs(money); // Ensure that money is never negative
    }

    public static void setName(String p){
        Currency.playerName = p;
    }
    
    public static String getName(){
        return playerName;
    }


    public static void openStore(Scanner scanner){
        System.out.println("\nWelcome to the Title Store!");
        System.out.println("Available Titles:");
            for (int i = 0; i < titles.length; i++) {
                System.out.printf("%d. %s - $%d%n", i + 1, titles[i], prices[i]);
            }
            System.out.println("0. Exit Store");

            while (true) {
                System.out.print("\nEnter the number of the title you want to buy: ");

                if (!scanner.hasNextInt()) {
                    scanner.next();
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }

                int choice = scanner.nextInt();
                if (choice == 0) {
                    System.out.println("Leaving store...");
                    break;
                }

                if (choice < 1 || choice > titles.length) {
                    System.out.println("Invalid choice. Try again.");
                    continue;
                }

                int cost = prices[choice - 1];
                String title = titles[choice - 1];

                if (money >= cost) {
                    money -= cost;
                    playerName += title;
                    System.out.printf("You bought%s for $%d!%nYour new name: %s%nBalance: $%d%n", title, cost, playerName, money);
                    break;
                } else {
                    System.out.println("You can't afford that! Try something cheaper.");
                }
            }
        }
    }



