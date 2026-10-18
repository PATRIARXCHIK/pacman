import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Pac-Man");
            GamePanel game = new GamePanel();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(game);
            f.pack();
            f.setResizable(false);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}