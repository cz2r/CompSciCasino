import java.util.Random;

/*
 * Changes made to deck code made by Mr Freeman
 * Increased shoe size to 8 decks
 * Created a "trueValue" array to assist in blackjack calculations
 * Cleaned up for loops
 * Changed shuffle method
 * Made some variables final
 * Replaced documentation with my own
 * Tanay K, CS10 Group Project (Blackjack)
 */

class Deck {
    private final Card[] deck; // Array of Card objects representing the deck
    private int top; // Index of the top card in the deck
    private final int numDeck = 8; // Number of decks in the shoe
    
    public Deck(){
        String[] values = {"Ace","2","3","4","5","6","7","8","9","10","Jack","Queen","King"}; // Create value array
        int[] trueValues = {11,2,3,4,5,6,7,8,9,10,10,10,10}; // Create array for the true values of each card (in terms of blackjack)
        String[] suits = {"Spades","Clubs","Diamonds","Hearts"}; // Create suits array
        deck = new Card[52 * numDeck]; // Creates a deck array with how many cards are in the shoe
        int counter = 0;
        for (int d = 0; d < numDeck; d++) {        
            for (int i = 0; i < values.length; i++) {   
                for (String suit : suits) {
                    deck[counter++] = new Card(values[i], suit, trueValues[i]);
                }
            }
        } // Creation of cards
        top = 0; // Sets the "top" of the deck (card to be dealt)
    }

    public Card deal(){
        Card toDeal = deck[top]; // Selects the top card of the deck to be dealt
        top++; // Adds one to the top card (essentially removes the card)
        if(top >= deck.length){
            shuffle(); // If the top card is out of bounds then shuffles again and sets the top to 0 (debugging purposes)
            top = 0;
        } else if (top >= deck.length * 0.75) {
            System.out.println("Reshuffling the shoe..."); // Reshuffles shoe when there is 25% left to help prevent card counting
            shuffle();
            top = 0;
            toDeal = deck[top]; // Resets the top card and sets the new toDeal card
        }
        
        return toDeal;
    }
    
    public void shuffle() { // Using Fisher-Yates shuffling algorithm
        Random rand = new Random();
        for (int i = deck.length - 1; i > 0; i--) { // Randomly swaps each element with another element at a random position that hasnt been shuffled yet
            int j = rand.nextInt(i + 1); 
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
    }

    public void checkDeck(){ // Debugging purposes
        for (Card deck1 : deck) {
            System.out.println(deck1.getValue() + " of " + deck1.getSuit()); // Prints every card in the shoe
        }
    }

    public void checkTrueValues(){ // Debugging purposes
        for (Card deck1 : deck) {
            System.out.println(deck1.getTrueValue()); // Prints only the true value of every card
        }
    }

    public int getTopCard(){
        return top;
    }
    
    public int getCardsInDeck(){
        return numDeck * 52;
    }
}

class Card{ // Create the card class
    final private String value; // Attributes
    final private String suit;
    final private int trueValue;

    public Card (String v, String s, int tv){ // Sets the value and suit
        this.value = v;
        this.suit = s;
        this.trueValue = tv;
    }

    public String getValue() { // Gets card value
        return value;
    }

    public String getSuit() { // Gets card suit
        return suit;
    }
    
    public int getTrueValue() { // Return the true value of the card in blackjack
        return trueValue;
    }

    @Override
    public String toString() { // Makes it into a string that can be printed (from the ArrayList directly)
        return value + " of " + suit;
    }
}
