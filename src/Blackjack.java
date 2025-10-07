import java.util.*;

/*
 * Original code by Tanay K, CS10 Group Project
 * Assistance from W3Schools, Stack Overflow
 */

public class Blackjack {

    private static final Deck shoe = new Deck(); // Instantiate the shoe
    private static final Scanner scanner = new Scanner(System.in); // Instantiate a scanner
    private static final ArrayList<Integer> bet = new ArrayList<>(); // Create an arrayList for the bets on each hand
    private static final ArrayList<Card> dealerHand = new ArrayList<>(); // Create the dealer hand
    private static final ArrayList<ArrayList<Card>> playerHand = new ArrayList<>(); // Arraylist with nested arraylist of player hands - simulated 2d arraylist
    private static final ArrayList<Boolean> playerBust = new ArrayList<>(); // Checks if the player went over 21
    private static final ArrayList<Boolean> playerBlackjack = new ArrayList<>(); // Checks if the player hit a blackjack
    private static boolean dealerBust = false; // Checks if the dealer went over 21
    private static boolean isShuffled = false; // Checks if the deck has been shuffled once
    private static int hands; // Create an integer to store the number of hands to be played

    public static void game(){
        System.out.println("How many hands would you like to play? (Please input an integer thats less than 20)");
        
        // Clear bets and hands for the round (ensures no leftover data from previous round)
        bet.clear();
        playerHand.clear();
        playerBust.clear();
        playerBlackjack.clear();
        dealerHand.clear();
        dealerBust = false;

        // Input validation for hand count
        while (true) {
            if (scanner.hasNextInt()) {
                hands = scanner.nextInt();
                if (hands >= 1 && hands <= 20) {
                    break;
                } else {
                    System.out.println("Invalid input! Enter an integer between 1 and 20."); // In input integer is out of bounds
                }
            } else {
                System.out.println("Invalid input! Please enter an integer."); // If input is not an integer
                scanner.next(); // Ensure that it doesnt crash on invalid input
            }
        }

        for (int i = 0; i < hands; i++){ // Iterates over all hands to get every bet
            int j = i + 1; // True hand count (instead of using array index)
            System.out.println("How much would you like to bet on hand " + j + "?");
            System.out.println("You currently have: $" + Currency.getMoney());
            System.out.println("You are able to bet 0 if you run out of money or do not want to bet");

            // Input validation for bet amount
            do {
                if (scanner.hasNextInt()) {
                    bet.add(Math.abs(scanner.nextInt())); // Adds the bet value to each hand
                    Currency.setMoney(Currency.getMoney() - bet.get(i)); // Removes the money that was bet from the players account
                    if (Currency.getMoney() < 0) { // If the bet results in the balance going negative
                        Currency.setMoney(Currency.getMoney() + bet.get(i)); // Readd the removed money
                        System.out.println("You do not have enough money to do that! You have $" + Currency.getMoney());
                        bet.remove(i); // Remove the bet from that hand, and reject the value
                    } else {
                        break;
                    }
                } else {
                    System.out.println("Invalid input! Please enter a positive integer.");
                    scanner.next(); // Ensure that it doesn't get stuck on invalid input
                }
            } while (true); // Catches negative currency values - doesnt let the user add a bet
        }

        System.out.println("Your final bets are: ");
        for (int i = 0; i < hands; i++){
            int j = i + 1;
            System.out.println(bet.get(i) + " for hand " + j); // Prints every hand and the bets placed on them
        }

        if (!isShuffled){
            shoe.shuffle(); // Initial deck shuffle, it is shuffled in Deck afterwards
            System.out.println("Shuffling the deck");
            isShuffled = true;
        }

        // Initial card dealing
        dealerHand.clear(); // Clears any remaining cards from the previous game
        dealerHand.add(shoe.deal()); // Face up card
        dealerHand.add(shoe.deal()); // Face down card

        playerHand.clear(); // Clears any remaining cards from the previous game
        for (int i = 0; i < hands; i++) {
            playerHand.add(new ArrayList<>()); // Makes a sub-arraylist for each hand (2nd dimension)
            playerHand.get(i).add(shoe.deal());
            playerHand.get(i).add(shoe.deal());
        }

        playerBust.clear(); // Initialize the playerBust and playerBlackjack states
        playerBlackjack.clear();
        for (int i = 0; i < playerHand.size(); i++) {
            playerBust.add(false); // Adds playerBust and playerBlackjack for each hand
            playerBlackjack.add(false);

            if (calculateHandValue(playerHand.get(i)) == 21) {
                playerBlackjack.set(i, true); // Checks for a natural blackjack
            }
        }

        for (int i = 0; i < hands; i++) {
            int handValue = calculateHandValue(playerHand.get(i)); // Calculates the value of the players hand
            System.out.println("Player hand " + (i + 1) + ": " + playerHand.get(i));
            System.out.println(" -> Total: " + handValue); // Prints all the hands that the player has and their respective value
        }

        System.out.println("Dealer shows a " + dealerHand.get(0)); // Shows the dealers top card
        System.out.println("Cards in shoe: " + (shoe.getCardsInDeck() - shoe.getTopCard()) + " / " + shoe.getCardsInDeck());
        System.out.println("Dealer peeks at their other card...");

        int dealerValue = calculateHandValue(dealerHand); // Check if dealer has a natural blackjack
        boolean dealerHasBlackjack = dealerValue == 21 && dealerHand.size() == 2; // If both values are true then it sets as true
        if (dealerHasBlackjack) {
            System.out.println("Dealer has a natural Blackjack! Round ends immediately.");
            payouts(); // Handle payouts (players without blackjack lose, players with blackjack push)
            return; // Skip the rest - cant play anything else
        } else {
            System.out.println("Dealer does not have a blackjack! Round continues...");
        }

        playHands();
        dealerTurn();
        payouts();
    }

    // Methods

    private static int calculateHandValue(ArrayList<Card> hand) { // Calculate the value of the hand (with account to aces and ace logic)
        int total = 0; // Total of each hand
        int aces = 0; // Number of aces in the hand

        for (Card c : hand) {
            total += c.getTrueValue(); // Gets the values for each card
            if (c.getValue().equals("Ace")) {
                aces++; // Adds to the aces value of the hand
            }
        }

        while (total > 21 && aces > 0) { // Checks if the hand is a bust (if aces are available count an ace as 1 instead)
            total -= 10; // Count one Ace as 1 instead of 11
            aces--; // Removes the ace counted as a 1 from the available aces
        }

        return total;
    }

    private static void playHands() { // Logic for after the initial dealing
        for (int i = 0; i < playerHand.size(); i++) {

            if (playerBlackjack.get(i)) { // Skip natural blackjack hands (They cannot be hit or split)
                System.out.println("Hand " + (i + 1) + " hit a Blackjack! Skipping...");
                continue; // Move to next hand (interates hand)
            }
            boolean playing = true; // Keeps track if the player can still play or not
            while (playing) { 
                int value = calculateHandValue(playerHand.get(i)); // Calculates the value of the current hand
                System.out.println("\nHand " + (i + 1) + ": " + playerHand.get(i) + " -> " + value); // Displays the hand currently in play

                if (value > 21) { // If the player hand is over 21 then bust
                    System.out.println("Bust!");
                    playerBust.set(i, true);
                    break; // Stops the player from playing this hand
                } else if (value == 21) { // If the player hits 21 then cant play it anymore
                    System.out.println("You hit 21!");
                    break; // Stops the player from playing this hand
                }

                System.out.println("Choose action: (H)it, (S)tand, (D)ouble, S(P)lit"); // Lets the player choose what they want to do
                
                String choice = "";
                while (true) { // input validation for player choice
                    choice = scanner.next().toLowerCase();
                    if (choice.equals("h") || choice.equals("s") || choice.equals("d") || choice.equals("p")) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter H, S, D, or P.");
                    }
                }

                switch (choice) {
                    case "h" -> // Hit
                        playerHand.get(i).add(shoe.deal());

                    case "s" -> // Stand
                        playing = false; // If the player stands on that hand then move onto the next hand

                    case "d" -> { // Double Down
                        if (bet.get(i) <= Currency.getMoney()) {
                            Currency.setMoney(Currency.getMoney() - bet.get(i)); // Remove extra bet from the players account
                            bet.set(i, bet.get(i) * 2); // Doubles the bet on that hand
                            playerHand.get(i).add(shoe.deal()); // One more card
                            System.out.println("Doubled down. New bet: " + bet.get(i)); // Display the new bet
                            playing = false; // Must stand after double
                        } else {
                            System.out.println("Not enough money to double down."); 
                        } // Refuse double down if not possible, then returns back to the switch to allow user to pick another action
                    }

                    case "p" -> { // Split
                        ArrayList<Card> hand = playerHand.get(i);    
                        if (hand.size() == 2 && hand.get(0).getValue().equals(hand.get(1).getValue())) { // Only allow split if the player has a pair
                            // Create new hand for the second card
                            ArrayList<Card> newHand = new ArrayList<>();
                            newHand.add(hand.remove(1)); // move second card to new hand        
                            hand.add(shoe.deal()); // Deal one new card to each hand
                            newHand.add(shoe.deal());
                            playerHand.add(newHand); // Add the new hand to playerHand
                            // Add new entries for bust and blackjack, ensuring both hands are correctly initialized
                            playerBust.add(false); // New hand is not busted
                            playerBlackjack.add(false); // New hand does not start with blackjack
                            if (calculateHandValue(hand) == 21 && hand.size() == 2) { // Make sure the original hand remains correctly marked
                                playerBlackjack.set(i, true);
                            } else {
                                playerBlackjack.set(i, false);
                            }
                            playerBust.set(i, false); // original hand cannot be busted immediately after split
                            bet.add(bet.get(i)); // Duplicate bet for the new hand
                            System.out.println("You have split. You now have " + playerHand.size() + " hands.");
                        } else {
                            System.out.println("You can only split pairs of equal value.");
                        }
                        if (bet.size() != playerHand.size()) { // Debugging catch for desync bet and hands
                            System.out.println("WARNING: Bets and hands desynced! Hands=" + playerHand.size() + " Bets=" + bet.size());
                        }
                    }
                }
            }
        }
    }

    private static void dealerTurn() {
        System.out.println("\n\n\n\nDealer is now playing...");

        System.out.println("Dealer's hand: " + dealerHand);
        System.out.println(" -> Total: " + calculateHandValue(dealerHand));

        while (true) { // Infinite loop until dealer busts or stands
            int dealerValue = calculateHandValue(dealerHand);
            if (dealerValue >= 17) break; // Dealer stands on 17 or higher
            Card newCard = shoe.deal(); // Hits if the dealer value is not 17 or higher
            dealerHand.add(newCard); // Adds the new card
            System.out.println("Dealer hits: " + newCard); // Displays the card the dealer pulls

            dealerValue = calculateHandValue(dealerHand); // Calculate value of dealer hand
            System.out.println("Dealer's hand: " + dealerHand + " -> Total: " + dealerValue); // Display dealer hand

            if (dealerValue > 21) { // Checks if the dealer busted
                System.out.println("Dealer busts!"); 
                dealerBust = true;
                break; // Breaks
            }
        }

        if (!dealerBust) {
            System.out.println("Dealer stands with total: " + calculateHandValue(dealerHand)); // Displays the total of the dealer hand if it did not bust
        }
    }

    private static void payouts() { // Payout Logic
        int dealerTotal = calculateHandValue(dealerHand);
        boolean dealerHasBlackjack = dealerTotal == 21 && dealerHand.size() == 2; // Check if dealer has natural blackjack
        System.out.println("\nRound Results");
        if (dealerHasBlackjack) {
            System.out.println("Dealer reveals: " + dealerHand + " -> Blackjack!");
        }
        System.out.println("Dealer total: " + dealerTotal);
        for (int i = 0; i < playerHand.size(); i++) {
            int playerTotal = calculateHandValue(playerHand.get(i)); // Calculates the value of the hands
            int handBet = bet.get(i); // Sets the bet that each hand has
            System.out.print("Hand " + (i + 1) + " (" + playerHand.get(i) + "): " + playerTotal + " -> "); // Prints the hand
            if (dealerHasBlackjack) { // If the dealer has natural blackjack      
                if (playerBlackjack.get(i)) { // If the player also has a blackjack
                    Currency.setMoney(Currency.getMoney() + handBet);
                    System.out.println("Push (Both the dealer and player have Blackjack). Bet returned.");
                } else {
                    System.out.println("Dealer has Blackjack! You lose $" + handBet); // Player loss
                }
            } else if (playerBust.get(i)) {
                System.out.println("Bust! You lose $" + handBet); // Money lost
            } else if (playerBlackjack.get(i) && dealerTotal != 21) { // If the player got a Blackjack
                int payout = (int) (handBet * 1.5); // Calculates the payout (Money was removed at the start, so payout needs to be calc'd seperately)
                Currency.setMoney(Currency.getMoney() + handBet + payout); // Money gained
                System.out.println("Blackjack! You win $" + payout); 
            } else if (dealerBust) { // If the dealer busted
                Currency.setMoney(Currency.getMoney() + handBet * 2);
                System.out.println("Dealer busts! You win $" + handBet);
            } else if (playerTotal > dealerTotal) { // If the player stood with a higher score than the dealer
                Currency.setMoney(Currency.getMoney() + handBet * 2);
                System.out.println("You stood higher than the dealer! You win $" + handBet);
            } else if (playerTotal < dealerTotal) { // If the player stood with a lower score than the dealer
                System.out.println("You stood lower than the dealer! You lose $" + handBet);
            } else {
                Currency.setMoney(Currency.getMoney() + handBet);
                System.out.println("Push. Your bet is returned."); // If the dealer and player have the same amount
            }
            if (bet.size() != playerHand.size()) { // Debugging catch for desync bet and hands
                System.out.println("WARNING: Bets and hands desynced! Hands=" + playerHand.size() + " Bets=" + bet.size());
            }
        }
        System.out.println("You now have: $" + Currency.getMoney());
    }
}
