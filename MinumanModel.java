import java.awt.*;
import java.util.*;

public class MinumanModel {
    public class Card {
        private String value;
        private String type;
        private Rectangle bounds;

        public Rectangle getBounds() {
            return bounds;
        }

        public void setBounds(Rectangle bounds) {
            this.bounds = bounds;
        }

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            switch (value) {
                case "A":
                    return 14;
                case "K":
                    return 13;
                case "Q":
                    return 12;
                case "J":
                    return 11;
                default:
                    return Integer.parseInt(value);
            }
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    private ArrayList<Card> deck;
    private ArrayList<Card> enemyHand;
    private Card hasilEnemy;
    private ArrayList<Card> playerHand;
    private boolean isPlayerTurn;
    private Card hasilPlayer;
    private int pageNow;
    private boolean isSkip;
    private ArrayList<Card> middle;
    private Rectangle posisiMinum;
    private String message;
    private Random random = new Random();

    public MinumanModel() {
        buildDeck();
        initGame();
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }

        Collections.shuffle(deck);
    }

    public void initGame() {
        enemyHand = new ArrayList<Card>();
        playerHand = new ArrayList<Card>();
        middle = new ArrayList<Card>();
        pageNow = 1;
        isPlayerTurn = true;

        for (int i = 0; i < 10; i++) {
            enemyHand.add(deck.remove(deck.size() - 1));
            playerHand.add(deck.remove(deck.size() - 1));
        }

        middle.add(deck.remove(deck.size() - 1));
    }

    public ArrayList<Card> getEnemyHand() {
        return enemyHand;
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public ArrayList<Card> getMiddle() {
        return middle;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }

    public void setHasilPlayer(Card hasilPlayer) {
        this.hasilPlayer = hasilPlayer;
    }

    public void setHasilEnemy(Card hasilEnemy) {
        this.hasilEnemy = hasilEnemy;
    }

    public Card getHasilPlayer() {
        return hasilPlayer;
    }

    public Card getHasilEnemy() {
        return hasilEnemy;
    }

    public int getPageNow() {
        return pageNow;
    }

    public void incrementPageNow() {
        pageNow++;
    }

    public void decrementPageNow() {
        pageNow--;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public boolean isSkip() { 
        return isSkip; 
    }

    public void setSkip(boolean skip) { 
        isSkip = skip; 
    }

    public boolean drawCardForPlayer() {
        if (!deck.isEmpty()) {
            playerHand.add(deck.remove(deck.size() - 1));
            return true;
        }
        return false;
    }
}
