import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinumanModel model = new MinumanModel();
            MinumanView view = new MinumanView(model);
            new MinumanController(model, view);
        });
    }
}
