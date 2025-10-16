/*
 * CS 10B Group Project
 * Semester 2 Term 3/4
 * James Q (Buckshot Roulette), Erin K (Slots), Tanay K (Blackjack)
 * Mr Atzeni 
 */

import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        menu();
    }

    private static void menu(){
        boolean playAgain; // If the user wants to play again
        boolean greetedBlackjack = false; // Checks if the greeting for the selected game has already been said
        boolean greetedSlots = false;
        boolean greetedRoulette = false;
        boolean enteredStore = false;
        int selectedGame;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to Casino!");
            while (true) {
                System.out.println("What is your name?");
                if (scanner.hasNextLine()) {
                    Currency.setName(scanner.nextLine().trim());
                    System.out.println("Your name has been inputted as: " + Currency.getName());
                    break;
                }
            }

            do {

                while (true) {
                    System.out.println("Type 1 for Blackjack, 2 for Slots, 3 for Buckshot Roulette, or 4 to open the Title Store. Type 5 to reset your money to 1000.");
                    System.out.println("You currently have " + Currency.getMoney() + " dollars available.");

                    if (scanner.hasNextInt()) {
                        selectedGame = scanner.nextInt();
                        if (selectedGame >= 1 && selectedGame <= 5) {
                            break; // If valid input then break the loop
                        } else {
                            System.out.println("Invalid input! Please enter 1, 2, 3, 4 or 5"); // If invalid input request again
                        }
                    } else {
                        System.out.println("Invalid input! Please enter a number (1, 2, 3, 4 or 5).");
                        scanner.next(); // Consume the value so it doesnt crash on invalid input
                    }
                }

                switch (selectedGame) {
                    case 1 -> {
                        if (!greetedBlackjack) {
                            System.out.println("Welcome to Blackjack!");
                            System.out.println("Refer to the README for rules and features!");
                            greetedBlackjack = true; // Greet only if they havent played before
                        }
                        Blackjack.game();
                    }
                    case 2 -> {
                        if (!greetedSlots) {
                            System.out.println("Welcome to Slots!");
                            System.out.println("Refer to the README for rules and features!");
                            greetedSlots = true;
                        }
                        Slots.game();
                    }
                    case 3 -> {
                        if (!greetedRoulette) {
                            System.out.println("Welcome to Buckshot Roulette!");
                            System.out.println("Refer to the README for rules and features!");
                            greetedRoulette = true;
                        }
                        BuckshotRoulette.game();
                    }
                    case 4 -> {
                        Currency.openStore(scanner); // Pass the scanner in app.java to the store
                        enteredStore = true;
                    }
                    case 5 -> Currency.resetMoney();
                    default -> System.out.println("Invalid input! Please enter 1, 2, 3, 4 or 5");
                }

                int playInput = 0;
                while (true) {
                    if (enteredStore){
                        playInput = 1;
                        enteredStore = false; // Reset entered store (so it only auto plays if the store was entered)
                        break;
                    } else {     
                        System.out.println("Would you like to play again/play a different game? Please enter 1 for yes and 2 for no.");
                        if (scanner.hasNextInt()) {
                            playInput = scanner.nextInt();
                            if (playInput == 1 || playInput == 2) {
                                break; // Break the loop if valid input
                            } else {
                                System.out.println("Not a valid option. Please enter 1 or 2.");
                            }
                        } else {
                            System.out.println("Invalid input! Please enter a number (1 or 2).");
                            scanner.next(); // Consume invalid input
                        }
                    }
                }
                playAgain = (playInput == 1); // If the user wants to play again
            } while (playAgain);
        }
    }
}
