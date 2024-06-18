import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class MinumanController {
    private MinumanView view;
    private MinumanModel model;
    private Random random = new Random();


    public MinumanController(MinumanModel model, MinumanView view) {
        this.view = view;
        this.model = model;

        initController();
    }

    private void initController() {
        view.addGamePanelMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePlayerClick(e);
            }
        });

        view.addNextButtonActionListener(e -> handleNextButton());
        view.addPrevButtonActionListener(e -> handlePrevButton());
        view.addSkipButtonActionListener(e -> handleSkipButton());

        view.repaint();
        new Thread(() -> {
            startGame();
        }).start();
    }

    private void startGame() {
        while (!model.getPlayerHand().isEmpty() && !model.getEnemyHand().isEmpty()){
            model.setSkip(false);
            // Menunggu semua pemain mengeluarkan kartu
            if (model.isPlayerTurn()){
                view.setSkipButtonEnabled(false);
                waitPlayer();
                if (model.getPlayerHand().isEmpty()) break;
                model.setMessage("Giliran Musuh");
                view.repaint();
                enemyPlay();
                view.repaint();
                if (model.getEnemyHand().isEmpty()) break;
            } else {
                model.setMessage("Giliran Musuh");
                view.setSkipButtonEnabled(false);
                if (model.isSkip()) continue;
                view.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                model.setHasilEnemy(model.getEnemyHand().remove(random.nextInt(model.getEnemyHand().size())));
                model.getMiddle().add(model.getHasilEnemy());
                view.repaint();
                if (model.getEnemyHand().isEmpty()) break;
                model.setPlayerTurn(true);
                view.setSkipButtonEnabled(true);
                waitPlayer();
            }

            // Evaluasi setelah semua pemain mengeluarkan kartu
            if (model.getPlayerHand().size() % 6 == 0 && model.getPageNow() == model.getPlayerHand().size()/6+1){
                model.decrementPageNow();
            }
            view.setSkipButtonEnabled(false);
            view.repaint();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (model.isSkip()){
                model.setPlayerTurn(!model.isPlayerTurn());
            }
            else {
                model.setPlayerTurn(model.getHasilPlayer().getValue() > model.getHasilEnemy().getValue());
            }

            model.setMessage(model.isPlayerTurn() ? "Anda Menang" : "Anda Kalah");
            view.repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            model.getMiddle().clear();
            view.repaint();
        }
        model.setMessage(model.getPlayerHand().isEmpty() ? "Yeay Kamu Menang xD" : "Yah Kamu Kalah :(");
        view.repaint();
    }

    private void handlePlayerClick(MouseEvent e) {
        if (!model.isPlayerTurn()) return;

        Rectangle posisiMinum = new Rectangle(470, 170, 90, 126);
        if (!model.getDeck().isEmpty() && posisiMinum.contains(e.getPoint())){
            if (model.getMiddle().isEmpty()) return;
            if (model.getPlayerHand().stream().anyMatch(card -> card.getType().equals(model.getMiddle().get(0).getType()))) return;
            model.getPlayerHand().add(model.getDeck().remove(model.getDeck().size()-1));
            view.repaint();
            return;
        }
        int index = 6*(model.getPageNow()-1);
        for (int i = index; i < Math.min(model.getPlayerHand().size(), index+6); i++) {
            MinumanModel.Card card = model.getPlayerHand().get(i);
            Rectangle cardBounds = card.getBounds();
            if (cardBounds.contains(e.getPoint())) {
                if (!model.getMiddle().isEmpty() && model.getMiddle().get(0).getType() != card.getType()) continue;
                model.getMiddle().add(card);
                if (model.getPlayerHand().size() % 6 == 1 && model.getPageNow() > 1) model.decrementPageNow();
                model.getPlayerHand().remove(card);
                view.repaint();
                model.setPlayerTurn(false);
                model.setHasilPlayer(card);
                break;
            }
        }
    }

    private void handleNextButton() {
        model.incrementPageNow();
        view.repaint();
    }

    private void handlePrevButton() {
        model.decrementPageNow();
        view.repaint();
    }

    private void handleSkipButton() {
        model.setSkip(true);
        view.repaint();
    }

    public void waitPlayer(){
        while (model.isPlayerTurn()) {
            model.setMessage("Giliran Anda");
            if (model.isSkip()) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void enemyPlay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MinumanModel.Card smallest = null, greatest = null;
        for (MinumanModel.Card card : model.getEnemyHand()) {
            if (card.getType().equals(model.getHasilPlayer().getType())) {
                if (greatest != null && smallest != null) {
                    if (greatest.getValue() > card.getValue()) {
                        if (model.getHasilPlayer().getValue() < card.getValue())
                            greatest = card;
                    } else if (smallest.getValue() > card.getValue()) {
                        smallest = card;
                    }
                }
                if (smallest == null) {
                    if (model.getHasilPlayer().getValue() > card.getValue())
                        smallest = card;
                }
                if (greatest == null) {
                    if (model.getHasilPlayer().getValue() < card.getValue())
                        greatest = card;
                }
            }
        }
        if (greatest != null)
            model.setHasilEnemy(greatest);
        else if (smallest != null)
            model.setHasilEnemy(smallest);
        else {
            model.setHasilEnemy(null);
            while (!model.getDeck().isEmpty() && model.getEnemyHand().get(model.getEnemyHand().size() - 1).getType() != model.getHasilPlayer().getType()) {
                greatest = model.getDeck().remove(model.getDeck().size() - 1);
                model.setHasilEnemy(greatest);
                model.getEnemyHand().add(greatest);
                view.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (model.getEnemyHand().isEmpty()){
            model.setSkip(true);
            return;
        }
        model.getEnemyHand().remove(model.getHasilEnemy());
        model.getMiddle().add(model.getHasilEnemy());
    }
}
