import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.*;

public class Minuman {
    private class Card {
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

    ArrayList<Card> deck;
    Random random = new Random(); //shuffle deck

    //enemy
    ArrayList<Card> enemyHand;
    Card hasilEnemy;

    //player
    ArrayList<Card> playerHand;
    boolean isPlayerTurn;
    Card hasilPlayer;
    int pageNow;
    boolean playerSkip;

    JPanel buttonPanel = new JPanel();
    JButton nextButton = new JButton("Next");
    JButton prevButton = new JButton("Prev");
    JButton restartButton = new JButton("Restart");

    // tengah
    ArrayList<Card> middle;
    Rectangle posisiMinum;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth + 50;

    int cardWidth = 90; //ratio should 1/1.4
    int cardHeight = 126;

    String message;

    JFrame frame = new JFrame("Minuman");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            try {
                // Untuk "minum" kartu
                if (!deck.isEmpty()){
                    Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                    posisiMinum = new Rectangle(470, 170, cardWidth, cardHeight);
                    g.drawImage(hiddenCardImg, 470, 170, cardWidth, cardHeight, null);
                }

                //draw enemy's hand
                for (int i = 0; i < Math.min(enemyHand.size(), 6); i++) {
                    Image cardImg = new ImageIcon("./cards/BACK.png").getImage();
                    g.drawImage(cardImg, 10 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                }

                //draw player's hand
                int index = 6*(pageNow-1);
                for (int i = index; i < Math.min(playerHand.size(), index+6); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    int x = 10 + (cardWidth + 5) * (i - 6*(pageNow-1));
                    int y = 370;
                    card.setBounds(new Rectangle(x, y, cardWidth, cardHeight));
                    g.drawImage(cardImg, x, y, cardWidth, cardHeight, null);
                }

                //draw middle
                for (int i = 0; i < middle.size(); i++) {
                    Card card = middle.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    int x = 20 + (cardWidth + 5) * (i + 2) - 50 * (middle.size()-1);
                    int y = 200;
                    g.drawImage(cardImg, x, y, cardWidth, cardHeight, null);
                }

                g.setFont(new Font("Arial", Font.PLAIN, 30));
                g.setColor(Color.white);
                g.drawString(message, 200, 550);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void setup(){
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        prevButton.setFocusable(false);
        buttonPanel.add(prevButton);
        nextButton.setFocusable(false);
        buttonPanel.add(nextButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isPlayerTurn) return;
                if (!deck.isEmpty() && posisiMinum.contains(e.getPoint())){
                    if (middle.isEmpty()) return;
                    if (playerHand.stream().anyMatch(card -> card.getType().equals(middle.get(0).getType()))) return;
                    playerHand.add(deck.remove(deck.size()-1));
                    gamePanel.repaint();
                    if (pageNow == playerHand.size()/6+Math.min(1, playerHand.size()%6))
                        nextButton.setEnabled(false);
                    else 
                        nextButton.setEnabled(true);
                    return;
                }
                int index = 6*(pageNow-1);
                for (int i = index; i < Math.min(playerHand.size(), index+6); i++) {
                    Card card = playerHand.get(i);
                    Rectangle cardBounds = card.getBounds();
                    if (cardBounds.contains(e.getPoint())) {
                        if (!middle.isEmpty() && middle.get(0).getType() != card.getType()) continue;
                        middle.add(card);
                        if (playerHand.size() % 6 == 1 && pageNow > 1) pageNow--;
                        playerHand.remove(card);
                        gamePanel.repaint();
                        isPlayerTurn = false;
                        hasilPlayer = card;
                        break;
                    }
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pageNow++;
                if (pageNow == playerHand.size()/6+Math.min(1, playerHand.size()%6))
                    nextButton.setEnabled(false);
                prevButton.setEnabled(true);
                gamePanel.repaint();
            }
        });

        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pageNow--;
                if (pageNow == 1)
                    prevButton.setEnabled(false);
                nextButton.setEnabled(true);
                gamePanel.repaint();
            }
        });

        gamePanel.repaint();
    }

    Minuman() {
        setup();
        startGame();
    }

    public void startGame() {
        //deck
        buildDeck();
        shuffleDeck();

        //enemy
        enemyHand = new ArrayList<Card>();
        pageNow = 1;

        // nextButton.setEnabled(false);
        prevButton.setEnabled(false);

        for (int i = 0; i < 10; i++){
            Card take = deck.remove(deck.size()-1);
            enemyHand.add(take);
        }

        //player
        playerHand = new ArrayList<Card>();

        middle = new ArrayList<Card>();

        for (int i = 0; i < 10; i++){
            Card take = deck.remove(deck.size()-1);
            playerHand.add(take);
        }

        middle.add(deck.remove(deck.size()-1));
        isPlayerTurn = true;

        while (!playerHand.isEmpty() && !enemyHand.isEmpty()){
            if (isPlayerTurn){
                waitPlayer();
                if (playerHand.isEmpty()) break;
                message = "Giliran Musuh";
                gamePanel.repaint();
                enemyDecide();
                gamePanel.repaint();
                if (enemyHand.isEmpty()) break;
            } else {
                message = "Giliran Musuh";
                gamePanel.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hasilEnemy = enemyHand.remove(random.nextInt(enemyHand.size()));
                middle.add(hasilEnemy);
                if (enemyHand.isEmpty()) break;
                isPlayerTurn = true;
                waitPlayer();
            }
            if (playerHand.size() % 6 == 0 && pageNow == playerHand.size()/6+1){
                pageNow--;
            }
            if (playerHand.size() <= 6){
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isPlayerTurn = hasilPlayer.getValue() > hasilEnemy.getValue();
            message = (isPlayerTurn ? "Anda Menang" : "Anda Kalah");
            gamePanel.repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            middle.clear();
            gamePanel.repaint();
        }
        message = (playerHand.isEmpty() ? "Yeay Kamu Menang xD" : "Yah Kamu Kalah :(");
        gamePanel.repaint();
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void enemyDecide() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Card smallest = null, greatest = null;
        for (Card card: enemyHand){
            if (card.getType() == hasilPlayer.getType()){
                if (greatest != null && smallest != null) {
                    if (greatest.getValue() > card.getValue()){
                        if (hasilPlayer.getValue() < card.getValue())
                            greatest = card;
                    }
                    else if (smallest.getValue() > card.getValue()) {
                        smallest = card;
                    }
                }
                if (smallest == null){
                    if (hasilPlayer.getValue() > card.getValue()) 
                        smallest = card;
                }
                if (greatest == null){
                    if (hasilPlayer.getValue() < card.getValue())
                        greatest = card;
                }
            }
        }
        if (greatest != null)
            hasilEnemy = greatest;
        else if (smallest != null)
            hasilEnemy = smallest;
        else {
            hasilEnemy = null;
            while (!deck.isEmpty() && enemyHand.get(enemyHand.size()-1).getType() != hasilPlayer.getType()){
                greatest = deck.remove(deck.size()-1);
                hasilEnemy = greatest;
                enemyHand.add(greatest);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gamePanel.repaint();
            }
        }
        enemyHand.remove(hasilEnemy);
        middle.add(hasilEnemy);
        gamePanel.repaint();
    }

    public void waitPlayer(){
        while (isPlayerTurn) {
            message = "Giliran Anda";
            gamePanel.repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
