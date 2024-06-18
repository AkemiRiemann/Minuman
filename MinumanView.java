import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinumanView {
    private JFrame frame;
    private JPanel gamePanel;
    private JPanel buttonPanel;
    private JButton nextButton;
    private JButton prevButton;
    private JButton skipButton;
    private JButton restartButton;
    private Rectangle posisiMinum;
    private String message;
    private int boardWidth = 600;
    private int boardHeight = 650;
    private int cardWidth = 90;
    private int cardHeight = 126;
    private MinumanModel model;

    public MinumanView(MinumanModel model) {
        frame = new JFrame("Minuman");
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Untuk "minum" kartu
                    if (!model.getDeck().isEmpty()){
                        Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                        posisiMinum = new Rectangle(470, 170, cardWidth, cardHeight);
                        g.drawImage(hiddenCardImg, 470, 170, cardWidth, cardHeight, null);
                    }

                    //draw enemy's hand
                    for (int i = 0; i < Math.min(model.getEnemyHand().size(), 6); i++) {
                        Image cardImg = new ImageIcon("./cards/BACK.png").getImage();
                        g.drawImage(cardImg, 10 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                    }

                    // //draw player's hand
                    int index = 6*(model.getPageNow()-1);
                    for (int i = index; i < Math.min(model.getPlayerHand().size(), index+6); i++) {
                        MinumanModel.Card card = model.getPlayerHand().get(i);
                        Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                        int x = 10 + (cardWidth + 5) * (i - 6*(model.getPageNow()-1));
                        int y = 370;
                        card.setBounds(new Rectangle(x, y, cardWidth, cardHeight));
                        g.drawImage(cardImg, x, y, cardWidth, cardHeight, null);
                    }

                    //draw middle
                    for (int i = 0; i < model.getMiddle().size(); i++) {
                        MinumanModel.Card card = model.getMiddle().get(i);
                        Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                        int x = 20 + (cardWidth + 5) * (i + 2) - 50 * (model.getMiddle().size()-1);
                        int y = 200;
                        g.drawImage(cardImg, x, y, cardWidth, cardHeight, null);
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(model.getMessage(), 200, 550);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        buttonPanel = new JPanel();
        nextButton = new JButton("Next");
        prevButton = new JButton("Prev");
        skipButton = new JButton("Skip");
        this.model = model;

        setupUI();
    }

    private void setupUI() {
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
        skipButton.setFocusable(false);
        buttonPanel.add(skipButton);
        nextButton.setFocusable(false);
        buttonPanel.add(nextButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addGamePanelMouseListener(MouseAdapter mouseAdapter) {
        gamePanel.addMouseListener(mouseAdapter);
    }

    public void addNextButtonActionListener(ActionListener actionListener) {
        nextButton.addActionListener(actionListener);
    }

    public void addPrevButtonActionListener(ActionListener actionListener) {
        prevButton.addActionListener(actionListener);
    }

    public void addSkipButtonActionListener(ActionListener actionListener) {
        skipButton.addActionListener(actionListener);
    }

    public void setMessage(String message) {
        this.message = message;
        gamePanel.repaint();
    }

    public void render(Graphics g) {
        gamePanel.repaint();
    }

    public void repaint() {
        if (model.getPageNow() == model.getPlayerHand().size()/6+Math.min(1, model.getPlayerHand().size()%6))
            setNextButtonEnabled(false);
        else 
            setNextButtonEnabled(true);
        setPrevButtonEnabled(model.getPageNow() > 1);
        gamePanel.repaint();
    }

    public void setNextButtonEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
    }

    public void setPrevButtonEnabled(boolean enabled) {
        prevButton.setEnabled(enabled);
    }

    public void setSkipButtonEnabled(boolean enabled) {
        skipButton.setEnabled(enabled);
    }
}
