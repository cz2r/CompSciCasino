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
    private static final ArrayList<Integer> insuranceBet = new ArrayList<>(); // Create an arrayList for insurance bets on each hand
    private static final ArrayList<Boolean> playerSurrender = new ArrayList<>(); // Create an arrayList to track if the player surrendered on each hand

    public static void game(){
        System.out.println("How many hands would you like to play? (Please input an integer thats less than or equal to 20)");
        
        // Clear bets and hands for the round (ensures no leftover data from previous round)
        resetRound();

        // Input validation for hand count
        while (true) {
            if (scanner.hasNextInt()) {
                hands = scanner.nextInt();
                if (hands >= 1 && hands <= 20) {
                    break;
                } else {
                    System.out.println("Invalid input! Enter an integer between 1 and 20."); // If input integer is out of bounds
                }
            } else {
                System.out.println("Invalid input! Please enter an integer."); // If input is not an integer
                scanner.next(); // Ensure that it doesnt crash on invalid input
            }
        }

        for (int i = 0; i < hands; i++){ // Iterates over all hands to get every bet
            System.out.println("How much would you like to bet on hand " + (i+1) + "?"); // i+1 to make it more user friendly (as arrays and iteration start at 0)
            System.out.println("You currently have: $" + Currency.getMoney());
            System.out.println("You are able to bet 0 if you run out of money or do not want to bet");

            // Input validation for bet amount
            while (true) {
                if (scanner.hasNextInt()) {
                    int betAmount = scanner.nextInt();
                    if (betAmount > Currency.getMoney()) { // If the bet results in the balance going negative or negative value
                        System.out.println("You do not have enough money to do that! You have $" + Currency.getMoney());
                    } else if (betAmount < 0) {
                        System.out.println("You cannot bet a negative amount!");
                    } else {
                        bet.add(betAmount); // Adds the bet value to each hand
                        Currency.setMoney(Currency.getMoney() - betAmount); // Removes the money that was bet from the players account
                        break;
                    }
                } else {
                    System.out.println("Invalid input! Please enter a positive integer.");
                    scanner.next(); // Ensure that it doesn't get stuck on invalid input
                }
            }
        }

        System.out.println("Your final bets are: ");
        for (int i = 0; i < hands; i++){
            System.out.println("$" + bet.get(i) + " for hand " + (i+1)); // Prints every hand and the bets placed on them
            pause(500); // Small pause for readability
        }

        if (!isShuffled){
            shoe.shuffle(); // Initial deck shuffle, it is shuffled in Deck afterwards (if 75% of the deck is used it reshuffles)
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
            playerHand.get(i).add(shoe.deal()); // Deals the first card to each hand
            playerHand.get(i).add(shoe.deal()); // Deals the second card to each hand
        }

        playerBust.clear(); // Initialize the playerBust and playerBlackjack states
        playerBlackjack.clear();
        playerSurrender.clear();

        for (ArrayList<Card> _ : playerHand) { // Initialize playerSurrender for each hand
            playerSurrender.add(false);
        }


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
            pause(500); // Small pause for readability
        }

        System.out.println("Dealer shows a " + dealerHand.get(0)); // Shows the dealers top card
        pause(500); // Small pause for readability
        handleSurrender();
        handleInsurance(); // Offer insurance if dealer's face-up card is an Ace
        pause(500);

        System.out.println("Dealer peeks at their other card...");
        pause(500);

        int dealerValue = calculateHandValue(dealerHand); // Check if dealer has a natural blackjack
        boolean dealerHasBlackjack = dealerValue == 21 && dealerHand.size() == 2; // If both values are true then it sets as true
        if (dealerHasBlackjack) {
            System.out.println("Dealer has a natural Blackjack! Round ends immediately.");
            payouts(); // Handle payouts (players without blackjack lose, players with blackjack push)
            return; // Skip the rest - cant play anything else
        } else {
            System.out.println("Dealer does not have a blackjack! Round continues...");
            System.out.println("Cards in shoe: " + (shoe.getCardsInDeck() - shoe.getTopCard()) + " / " + shoe.getCardsInDeck()); // Display how many cards are left in the shoe
        }

        playHands();
        dealerTurn();
        payouts();
    }

    // Methods

    private static void pause(int i) {
        try {
            Thread.sleep(i); // Pauses the program for i milliseconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

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

        // Safety: ensure all lists are synced to playerHand size (should always be, but just in case)
        while (playerSurrender.size() < playerHand.size()) playerSurrender.add(false);
        while (playerBust.size() < playerHand.size()) playerBust.add(false);
        while (playerBlackjack.size() < playerHand.size()) playerBlackjack.add(false);
        while (bet.size() < playerHand.size()) bet.add(0);
        while (insuranceBet.size() < playerHand.size()) insuranceBet.add(0);

        for (int i = 0; i < playerHand.size(); i++) {

            if (playerBlackjack.get(i)) { // Skip natural blackjack hands (They cannot be hit or split)
                System.out.println("Hand " + (i + 1) + " hit a Blackjack! Skipping...");
                continue; // Move to next hand (iterates hand)
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

                System.out.println("Choose action: (H)it, (S)tand, (D)ouble, S(P)lit, Su(R)render (Case insensitive)"); // Lets the player choose what they want to do
                
                String choice = "";
                while (true) { // input validation for player choice
                    choice = scanner.next().toLowerCase(); // Convert to lowercase to allow for case insensitive input
                    if (choice.equals("h") || choice.equals("s") || choice.equals("d") || choice.equals("p") || choice.equals("r")) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter H, S, D, P, or R.");
                    }
                }

                switch (choice) {
                    case "h" ->  // Hit
                        playerHand.get(i).add(shoe.deal());
                    
                    case "s" -> // Stand
                        playing = false; // If the player stands on that hand then move onto the next hand

                    case "d" -> { // Double Down
                        if (bet.get(i) <= Currency.getMoney() && playerHand.get(i).size() == 2) { // Only allow double down if the player has enough money and has only 2 cards
                            Currency.setMoney(Currency.getMoney() - bet.get(i)); // Remove extra bet from the players account
                            bet.set(i, bet.get(i) * 2); // Doubles the bet on that hand
                            playerHand.get(i).add(shoe.deal()); // One more card
                            System.out.println("Doubled down. New bet: " + bet.get(i)); // Display the new bet
                            playing = false; // Must stand after double
                        } else if (bet.get(i) > Currency.getMoney()) { // If the player does not have enough money to double down
                            System.out.println("Not enough money to double down."); 
                        } else { // If the player has more than 2 cards already (hit once already)
                            System.out.println("You can only double down on your first two cards.");
                        }
                        // Refuse double down if not possible, then returns back to the switch to allow user to pick another action
                    }

                    case "p" -> { // Split
                        ArrayList<Card> hand = playerHand.get(i);    
                        if (hand.size() == 2 && hand.get(0).getValue().equals(hand.get(1).getValue())) { // Only allow split if the player has a pair
                            if (bet.get(i) <= Currency.getMoney()) {
                                Currency.setMoney(Currency.getMoney() - bet.get(i));
                                // Create new hand for the second card
                                ArrayList<Card> newHand = new ArrayList<>();
                                newHand.add(hand.remove(1)); // move second card to new hand        
                                hand.add(shoe.deal()); // Deal one new card to each hand
                                newHand.add(shoe.deal());
                                playerHand.add(newHand); // Add the new hand to playerHand
                                // Add new entries for bust and blackjack, ensuring both hands are correctly initialized
                                playerBust.add(false); // New hand is not busted
                                playerBlackjack.add(false); // New hand does not start with blackjack
                                playerBlackjack.set(i, false); // Original hand cannot have blackjack after split
                                playerBust.set(i, false); // original hand cannot be busted immediately after split
                                insuranceBet.add(0); // No insurance on the new hand initially
                                playerSurrender.add(false); // No surrender on the new hand initially
                                bet.add(bet.get(i)); // Duplicate bet for the new hand
                                System.out.println("You have split. You now have " + playerHand.size() + " hands.");
                                System.out.println("Hand " + (i + 1) + " is now: " + hand);
                                System.out.println("New hand " + playerHand.size() + " is: " + newHand);
                            } else {
                                System.out.println("Not enough money to split.");
                            }
                        } else {
                            System.out.println("You can only split pairs of equal value.");
                        } // Refuse split if not possible, then returns back to the switch to allow user to pick another action
                    }

                    case "r" -> { // Surrender
                        if (playerHand.get(i).size() == 2) { // Only allowed on first two cards
                            int refund = bet.get(i) / 2; // Calculate half the bet
                            Currency.setMoney(Currency.getMoney() + refund); // Returns half the bet to the player
                            System.out.println("You surrendered hand " + (i + 1) + ". You get back $" + refund + ".");
                            playerSurrender.set(i, true); // Mark this hand as surrendered
                            playing = false; // End this hand
                        } else {
                            System.out.println("You can only surrender on your first two cards."); // Refuse surrender if not possible, then returns back to the switch to allow user to pick another action
                        }
                    }
                }
            }
        }
    }

    private static void dealerTurn() {
        System.out.println("\n\n\n\nDealer is now playing...");
        pause(500);

        System.out.println("Dealer's hand: " + dealerHand);
        System.out.println(" -> Total: " + calculateHandValue(dealerHand)); // Displays the dealers hand and value
        pause(500);

        while (true) { // Infinite loop until dealer busts or stands
            int dealerValue = calculateHandValue(dealerHand);
            if (dealerValue >= 17) break; // Dealer stands on soft 17 or higher
            Card newCard = shoe.deal(); // Hits if the dealer value is not 17 or higher
            dealerHand.add(newCard); // Adds the new card
            System.out.println("Dealer hits: " + newCard); // Displays the card the dealer pulls
            pause(500);
            dealerValue = calculateHandValue(dealerHand); // Calculate value of dealer hand
            System.out.println("Dealer's hand: " + dealerHand + " -> Total: " + dealerValue); // Display dealer hand
            pause(500);

            if (dealerValue > 21) { // Checks if the dealer busted
                System.out.println("Dealer busts!"); 
                dealerBust = true;
                break; // Breaks
            }
        }

        if (!dealerBust) {
            System.out.println("Dealer stands with total: " + calculateHandValue(dealerHand)); // Displays the total of the dealer hand if it did not bust
            pause(1000);
        }
    }

    private static void payouts() { // Payout Logic
        int dealerTotal = calculateHandValue(dealerHand);
        int totalWinnings = 0;
        boolean dealerHasBlackjack = dealerTotal == 21 && dealerHand.size() == 2; // Check if dealer has natural blackjack
        System.out.println("\nRound Results");
        pause(500);
        if (dealerHasBlackjack) {
            System.out.println("Dealer reveals: " + dealerHand + " -> Blackjack!"); // Displays dealer hand if it has a natural blackjack
            pause(500);
        }
        System.out.println("Dealer total: " + dealerTotal); // Displays the total of the dealer hand
        pause(500);
        
        handleInsurancePayouts(dealerHasBlackjack); // Handle insurance payouts first if taken

        for (int i = 0; i < playerHand.size(); i++) {

            if (playerSurrender.get(i)) { // If the player surrendered on this hand; skip further checks and apply surrender loss
                System.out.println("Hand " + (i + 1) + " was surrendered. You lose half your bet ($" + (bet.get(i) / 2) + ").");
                totalWinnings -= (bet.get(i) / 2); // Track the loss from surrender
                pause(500);
                continue; // Move to next hand
            }

            int playerTotal = calculateHandValue(playerHand.get(i)); // Calculates the value of the hands
            int handBet = bet.get(i); // Sets the bet that each hand has
            System.out.print("Hand " + (i + 1) + " (" + playerHand.get(i) + "): " + playerTotal + " -> "); // Prints the hand
            if (dealerHasBlackjack) { // If the dealer has natural blackjack      
                if (playerBlackjack.get(i)) { // If the player also has a blackjack
                    Currency.setMoney(Currency.getMoney() + handBet);
                    System.out.println("Push (Both the dealer and player have Blackjack). Bet returned.");
                    pause(500);
                } else {
                    System.out.println("Dealer has Blackjack! You lose $" + handBet); // Player loss
                    pause(500);
                    totalWinnings -= handBet;
                }
            } else if (playerBust.get(i)) {
                System.out.println("Bust! You lose $" + handBet); // Money lost
                totalWinnings -= handBet;
                pause(500);
            } else if (playerBlackjack.get(i)) { // If the player got a Blackjack
                int payout = (int) (handBet * 1.5); // Calculates the payout (Money was removed at the start, so payout needs to be calc'd seperately)
                Currency.setMoney(Currency.getMoney() + handBet + payout); // Money gained
                System.out.println("Blackjack! You win $" + (payout)); // Displays the payout (not including original bet - profit only)
                totalWinnings += (payout);
                pause(500);
            } else if (dealerBust) { // If the dealer busted
                Currency.setMoney(Currency.getMoney() + handBet * 2); // Returns double the bet (original bet + winnings)
                System.out.println("Dealer busts! You win $" + handBet);
                totalWinnings += handBet;
                pause(500);
            } else if (playerTotal > dealerTotal) { // If the player stood with a higher score than the dealer
                Currency.setMoney(Currency.getMoney() + handBet * 2);
                System.out.println("You stood higher than the dealer! You win $" + handBet);
                totalWinnings += handBet;
                pause(500);
            } else if (playerTotal < dealerTotal) { // If the player stood with a lower score than the dealer
                System.out.println("You stood lower than the dealer! You lose $" + handBet);
                totalWinnings -= handBet;
                pause(500);
            } else {
                Currency.setMoney(Currency.getMoney() + handBet);
                System.out.println("Push. Your bet is returned."); // If the dealer and player have the same amount
                pause(500);
            }
            if (bet.size() != playerHand.size()) { // Debugging catch for desync bet and hands
                throw new IllegalStateException("Bets and hands desynced! Hands=" + playerHand.size() + " Bets=" + bet.size());
            }
        }

        if (totalWinnings > 0) { // Displays total winnings/losses for the round
            System.out.println("Total winnings this round: $" + totalWinnings);
        } else if (totalWinnings < 0) {
            System.out.println("Total losses this round: $" + (-totalWinnings)); // -totalWinnings to make it positive
        } else {
            System.out.println("You broke even this round.");
        }

        System.out.println("You now have: $" + Currency.getMoney());

    }

    private static void handleSurrender() {
        if (dealerHand.get(0).getTrueValue() == 11 || dealerHand.get(0).getTrueValue() == 10) {
            surrenderLogic();
        } else {
            for (int i = 0; i < playerHand.size(); i++) {
                surrenderList.add(i, 0);
            }
        }
    }

    private static void surrenderLogic() {
        int totalSurrenderHands = 0;
        int totalReturnMoney = 0;
        System.out.println("Surrender is available if your hand is not good. Would you like to surrender? (Y/N)");
        System.out.println("Surrendering will return half your bet and render the hand as unplayable.");
        System.out.println("You can select which specific hands to surrender.");
        System.out.println("You currently have: $" + Currency.getMoney());
        pause(500);

        OUTER:
        while (true) {
            String choice1 = scanner.next().toLowerCase(); // Convert to lowercase to allow for case insensitive input
            switch (choice1) {
                case "y" -> {
                    break OUTER; // Return to offer surrenders for each hand
                }
                case "n" -> {
                    System.out.println("Surrender refused.");
                    return; // No surrenders
                }
                default -> System.out.println("Invalid choice. Please enter Y or N.");
            }
        }

        for (int i = 0; i < hands; i++) {
            int surrenderReturn = bet.get(i) / 2; // Surrender will return half money
            System.out.println("\nHand " + (i + 1) + ": " + playerHand.get(i) + " with bet $" + bet.get(i));
            System.out.println("Would you like to surrender this hand? (Y/N)");
            System.out.println("You will recieve: $" + surrenderReturn);

            String choice = "";
            while (true) { // Input validation for insurance choice
                choice = scanner.next().toLowerCase(); // Convert to lowercase to allow for case insensitive input
                if (choice.equals("y") || choice.equals("n")) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter Y or N.");
                }
            }

            if (choice.equals("y")) {
                System.out.println("You have surrendered hand " + (i+1));
                Currency.setMoney(Currency.getMoney() + surrenderReturn); // Return the surrendered money immediately
                surrenderList.add(i, 1);
                totalReturnMoney = totalReturnMoney + surrenderReturn; // Add count to surrendered hands and total returned
                totalSurrenderHands++;
            } else {
                System.out.println("Hand " + (i+1) + " still in play");
                surrenderList.add(i, 0);
            }
        }

        if (totalSurrenderHands > 0) {
            System.out.println("Surrender summary:");
            System.out.println("Total hands surrendered: " + totalSurrenderHands);
            System.out.println("Total money returned: " + totalReturnMoney);
        } else {
            System.out.println("All hands still in play");
        }
    }

    private static void handleInsurance() {
        if (!dealerHand.get(0).getValue().equals("Ace")) {
            return; // Only offer insurance if the dealer's face-up card is an Ace
        } 

        System.out.println("Dealer's face-up card is an Ace. Would you like to take insurance? (Y/N)");
        System.out.println("Insurance costs half your original bet and pays 2:1 if the dealer has a blackjack.");
        System.out.println("You currently have: $" + Currency.getMoney());
        pause(500);

        OUTER:
        while (true) {
            String choice1 = scanner.next().toLowerCase(); // Convert to lowercase to allow for case insensitive input
            switch (choice1) {
                case "y" -> {
                    break OUTER; // Return to offer insurance for each hand
                }
                case "n" -> {
                    System.out.println("No insurance taken for any hands.");
                    return; // No insurance taken
                }
                default -> System.out.println("Invalid choice. Please enter Y or N.");
            }
        }

        for (int i = 0; i < hands; i++) {
            int insuranceCost = bet.get(i) / 2; // Insurance costs half the original bet
            System.out.println("\nHand " + (i + 1) + ": " + playerHand.get(i) + " with bet $" + bet.get(i));
            System.out.println("Would you like to take insurance for this hand? (Y/N)");
            System.out.println("Insurance cost: $" + insuranceCost);

            String choice = "";
            while (true) { // Input validation for insurance choice
                choice = scanner.next().toLowerCase(); // Convert to lowercase to allow for case insensitive input
                if (choice.equals("y") || choice.equals("n")) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter Y or N.");
                }
            }

            if (choice.equals("y")) {
                if (insuranceCost > Currency.getMoney()) {
                    System.out.println("Not enough money to take insurance for this hand.");
                    insuranceBet.add(0); // No insurance taken for this hand
                } else {
                    Currency.setMoney(Currency.getMoney() - insuranceCost); // Deduct insurance cost from player's money
                    insuranceBet.add(insuranceCost); // Record the insurance bet
                    System.out.println("Insurance taken for hand " + (i + 1) + ". You paid $" + insuranceCost);
                }
            } else {
                insuranceBet.add(0); // No insurance taken for this hand
                System.out.println("No insurance taken for hand " + (i + 1) + ".");
            }
            pause(500);
        }

        int totalInsuranceCost = 0;
        int insuredHands = 0;
        for (int amount : insuranceBet) {
            if (amount > 0) {
                totalInsuranceCost += amount; // Calculate total insurance cost
                insuredHands++; // Count number of hands with insurance
            }
        }

        if (insuredHands > 0) {
            System.out.println("\nInsurance summary:");
            System.out.println("\nTotal insurance cost for this round: $" + totalInsuranceCost + " across " + insuredHands + " hands.");
            System.out.println("You currently have: $" + Currency.getMoney()); // Display remaining money after insurance
            pause(500); // Summary of insurance taken
        } else {
            System.out.println("\nNo insurance taken for any hands."); // Summary if no insurance taken
            pause(500);
        }
    }

    private static void handleInsurancePayouts(boolean dealerHasBlackjack) {
        if (insuranceBet.isEmpty()) {
            return; // No insurance bets were placed, ignore the payout logic
        }

        boolean hasInsurance = false;
        for (int amount : insuranceBet) {
            if (amount > 0) {
                hasInsurance = true; // Check if any insurance was taken
                break;
            }
        }
        
        if (!hasInsurance) {
            return; // No insurance taken, nothing to process
        }
        
        System.out.println("\nInsurance Results");
        pause(500);
        
        int totalInsurancePayout = 0;
        int totalInsuranceLoss = 0;
        
        for (int i = 0; i < insuranceBet.size(); i++) {
            if (insuranceBet.get(i) > 0) { // Only process hands with insurance
                if (dealerHasBlackjack) {
                    int payout = insuranceBet.get(i) * 2; // Insurance pays 2:1
                    Currency.setMoney(Currency.getMoney() + payout + insuranceBet.get(i)); // Return bet + winnings
                    System.out.println("Hand " + (i + 1) + " insurance wins! Payout: $" + (payout + insuranceBet.get(i)));
                    totalInsurancePayout += (payout + insuranceBet.get(i)); // Track total payout
                } else {
                    System.out.println("Hand " + (i + 1) + " insurance loses: $" + insuranceBet.get(i));
                    totalInsuranceLoss += insuranceBet.get(i);
                }
                pause(500);
            }
        }
        
        if (dealerHasBlackjack && totalInsurancePayout > 0) {
            System.out.println("Total insurance payout: $" + totalInsurancePayout); 
        } else if (!dealerHasBlackjack && totalInsuranceLoss > 0) {
            System.out.println("Total insurance lost: $" + totalInsuranceLoss); // Summary of insurance results
        }
    }

    private static void resetRound() { // Resets all the variables for a new round
        bet.clear();
        playerHand.clear();
        playerBust.clear();
        playerBlackjack.clear();
        dealerHand.clear();
        dealerBust = false;
        insuranceBet.clear();
        playerSurrender.clear();
    }
}
