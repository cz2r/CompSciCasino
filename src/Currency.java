public class Currency {

    private static int money = 1000; // Original amount of money
    private static String playerName;

    public static void setMoney(int m){ // Getter and setter for the money value
        Currency.money = Math.abs(m);
    }

    public static int getMoney(){
        return money;
    }

    public static void setName(String p){
        Currency.playerName = p;
    }
    
    public static String getName(){
        return playerName;
    }

}
