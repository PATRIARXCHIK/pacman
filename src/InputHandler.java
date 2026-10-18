import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private final GamePanel game;

    public InputHandler(GamePanel game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (game.isGameOver() || game.isWin()) {
            if (k == KeyEvent.VK_SPACE) game.restart();
            return;
        }

        if (k == KeyEvent.VK_UP) game.getPacman().setDirection(0, -1);
        else if (k == KeyEvent.VK_DOWN) game.getPacman().setDirection(0, 1);
        else if (k == KeyEvent.VK_LEFT) game.getPacman().setDirection(-1, 0);
        else if (k == KeyEvent.VK_RIGHT) game.getPacman().setDirection(1, 0);
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}