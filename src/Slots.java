import java.util.Random;
import java.util.Scanner;


public class Slots {

    // slot symbols
    private static final String[] SyMbOlS = {"7", "Pikachu", "Rattata", "Bellsprout", "Pokeball"}; //zhe symbols from the og pokemon red game
    private static final Random random = new Random(); // randomsigs it 

    public static void game() {
        try (Scanner scanner = new Scanner(System.in)) {
            int coins = Currency.getMoney(); // starting money
            System.out.println("welcome to erin's slot machine "); // greet
            System.out.println("you start with " + coins + " dollars."); // show ur balance
            
            while (coins > 0) { //makes sure they aint broke yns
                System.out.println("\nyou have " + coins + " dollars."); //shows balance
                System.out.print("how much do you want to gamble (1-3, or 0 to quit): "); //the prompt for player to gamble how much
                int bet = scanner.nextInt();
                
                if (bet == 0) {
                    System.out.println("thanks for your money fool, you finished with " + coins + " dollars."); // quit the game like a noob
                    break;
                }
                if (bet < 1 || bet > 3 || bet > coins) {
                    System.out.println("bro dont fat finger"); // if they dont put the right digits, or they too broke to bet that amount, it restarts the sequence
                    continue;
                }
                
                coins -= bet;
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
                coins += winnings;
                
                if (winnings > 0) {
                    System.out.println("you won " + winnings + " coins"); // ya win
                } else {
                    System.out.println("loss, 99% of gamblers quit before winning big"); // ya lost
                }
            }
            
            if (coins <= 0) {
                System.out.println("your broke, game over."); // literally game over, cuz they vroke
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
                    case "Pikachu" -> payout += 50 * bet; // second best outcome
                    case "Rattata" -> payout += 20 * bet; // third best outcome
                    case "Bellsprout" -> payout += 15 * bet; // fourth outcome
                    case "Pokeball" -> payout += 10 * bet; // least money
                }
            }
        }

        return payout;
    }
}