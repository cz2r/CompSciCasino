import java.util.Random;
import java.util.Scanner;

public class Slots {

    // slot symbols
    private static final String[] SyMbOlS = {"7", "@", "#", "$", "%", "T", "J", "C", "E", "K", "L"}; //symbols, because looking at the words was an eyesore
    private static final Random random = new Random(); // randomsigs it 

    public static void game() {
        try (Scanner scanner = new Scanner(System.in)) {
            int money = Currency.getMoney(); // starting money
            System.out.println("You start with " + money + " dollars."); // show ur balance
            
            while (money > 0) { //Makes sure they have enough money to bet, and starts the game
                System.out.println("\nYou have " + money + " dollars."); //shows balance
                System.out.println("                                                                      ");
                System.out.print("How much do you want to bet (1 to " + money + ", or 0 to quit): "); //the prompt for player to gamble how much
                int bet = scanner.nextInt();
                
                if (bet == 0) {
                    System.out.println("Thank you playing, you finished with " + money + " dollars."); // quit the game like a noob
                    break;
                }
                if (bet < 1 || bet > 1000000 || bet > money) {
                    System.out.println("Invalid input"); // if they dont put the right digits, or they too broke to bet that amount, it restarts the sequence
                    continue;
                }
                
                money -= bet;
                String[][] reels = spinReels(); // starts the game
                
                // display reels
                for (int row = 0; row < 3; row++) { // shows the lines and rows
                    for (int col = 0; col < 3; col++) { // structure
                        System.out.printf("%-15s", reels[row][col]); // structure, and makes the rows/columns not so squished
                    }
                    System.out.println(); //show the results
                }
                
                // checks winnings
                int winnings = calculateWinnings(reels, bet);
                money += winnings;
                Currency.setMoney(money);
                
                if (winnings > 0) {
                    System.out.println("You win " + winnings + " dollars"); // You win
                } else {
                    System.out.println("                                    ");
                    System.out.println("You lose " + bet + " dollars"); // You lost
                }
            }
            
            if (money <= 0  ) {
                System.out.println("You are out of money, game over."); // Game over, because they are broke
            }
        } // starting money
    }

    // spins reels (3x3 grid, random symbols)
    private static String[][] spinReels() {
        String[][] reels = new String[3][3]; //this randomizes it 
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                reels[row][col] = SyMbOlS[random.nextInt(SyMbOlS.length)]; // this one as well
            }
        }
        return reels;
    }

    // calculate win money based on rows
    private static int calculateWinnings(String[][] reels, int bet) {
        int payout = 0;

        // check each row (like the og pokemon slot machine)
        for (int row = 0; row < 3; row++) {
            if (reels[row][0].equals(reels[row][1]) && reels[row][1].equals(reels[row][2])) {
                String symbol = reels[row][0];
                switch (symbol) {
                    case "7" -> payout += 300 * bet; // jackpot
                    case "@" -> payout += 4 * bet; // second best outcome
                    case "#" -> payout += 3 * bet; // third best outcome
                    case "$" -> payout += 2 * bet; // fourth outcome
                    case "%" -> payout += 1.5 * bet; // least money
                    case "T" -> payout += 1.5 * bet; // fodders to stop players from winning too much money
                    case "J" -> payout += 1.5 * bet; // fodder
                    case "C" -> payout += 1.5 * bet; // fodder
                    case "E" -> payout += 1.5 * bet; // fodder
                    case "K" -> payout += 1.5 * bet; // fodder
                    case "L" -> payout += 1.5 * bet; // fodder
                }
            }
        }

        return payout;
    
    }
}