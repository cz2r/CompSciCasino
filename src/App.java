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
                            System.out.println("Refer to the README for rules and features!"); // Only greets the first time they play blackjack
                            greetedBlackjack = true;
                        }
                        Blackjack.game();
                    }
                    case 2 -> {
                        if (!greetedSlots){
                            System.out.println("Welcome to Slots!");
                            System.out.println("Refer to the README for rules and features!"); // Only greets the first time they play slots
                            greetedSlots = true;
                        }
                        Slots.game();
                    }
                    case 3 -> {
                        if (!greetedRoulette){
                            System.out.println("Welcome to Buckshot Roulette!");
                            System.out.println("Refer to the README for rules and features!"); // Only greets the first time they play buckshot roulette
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
                    System.out.println("Would you like to play again/play a different game? Please enter 1 for yes and 2 for no.");
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




