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
                while (true) { // Input validation
                    System.out.println("Type 1 for Blackjack, 2 for Slots, and 3 for Buckshot Roulette.");
                    System.out.println("You currently have " + Currency.getMoney() + " dollars available.");
                    if (scanner.hasNextInt()) {
                        selectedGame = scanner.nextInt();
                        if (selectedGame >= 1 && selectedGame <= 3) {
                            break; // Breaks if it is a valid input
                        } else {
                            System.out.println("Invalid input! Please enter 1, 2, or 3."); // Catches if the integer is out of range
                        }
                    } else {
                        System.out.println("Invalid input! Please enter a number (1, 2, or 3)."); // Catches if it is not an integer
                        scanner.next(); // Ensures it doesn't get stuck on invalid input
                    }
                }
                switch (selectedGame) {
                    case 1 -> {
                        if (!greetedBlackjack){
                            System.out.println("Welcome to Blackjack!");
                            System.out.println("\n\tBLACKJACK RULES: "); // Rules are from https://gist.github.com/MelissaBruno/cabf119e2f6573de4fe5 (slight modifications)
                            System.out.println("\t-Each player is dealt 2 cards. The dealer is dealt 2 cards with one face-up and one face-down.");
                            System.out.println("\t-Cards are equal to their value with face cards being 10 and an Ace being 1 or 11.");
                            System.out.println("\t-The players cards are added up for their total.");
                            System.out.println("\t-Players Hit to gain another card from the deck. Players Stand to keep their current card total.");
                            System.out.println("\t-Dealer Hits until they equal or exceed 17.");
                            System.out.println("\t-The goal is to have a higher card total than the dealer without going over 21.");
                            System.out.println("\t-If the hand total equals the dealer total, it is a Push and the hand ends.");
                            System.out.println("\t-Players win their bet if they beat the dealer.");
                            System.out.println("\t-Players win 1.5x their bet if they get Blackjack which is 21 (Only for natural blackjacks).");
                            System.out.println("\t-Players can split their hand if they show a pair, and it will create a new hand with that card and the same bet as the first.");
                            System.out.println("\t-Players can also choose to double down to double their bet on a hand, hit one card, then stand.");
                            System.out.println();
                            greetedBlackjack = true;
                        }
                        Blackjack.game();
                    }
                    case 2 -> {
                        if (!greetedSlots){
                            System.out.println("Welcome to Slots!");
                            greetedSlots = true;
                        }
                        Slots.game();
                    }
                    case 3 -> {
                        if (!greetedRoulette){
                            System.out.println("Welcome to Buckshot Roulette!");
                            greetedRoulette = true;
                        }
                        BuckshotRoulette.game();
                    }
                    default -> {
                        System.out.println("Invalid input! Please enter 1, 2, or 3."); // If the integer is not 1 2 or 3
                    }
                    
                }
                
                int playInput = 0; // Checks if the input is valid
                while (true) {
                    System.out.println("Would you like to play again? Please enter 1 for yes and 2 for no.");
                    if (scanner.hasNextInt()) {
                        playInput = scanner.nextInt();
                        if (playInput == 1 || playInput == 2) { // If the input is valid (1 or 2)
                            break;
                        } else {
                            System.out.println("Not a valid option. Please enter 1 or 2."); // Catches if the integer is out of range
                        }
                    } else {
                        System.out.println("Invalid input! Please enter a number (1 or 2)."); // Catches if the input is not an integer
                        scanner.next(); // Ensure it doesnt get stuck on invalid input
                    }
                }
                
                playAgain = (playInput == 1);
            } while (playAgain); // Runs the game selection again if they want to play again or play a different game
        }
    }
}




