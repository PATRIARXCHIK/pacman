import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
   
    private final GameController controller;
    private final GameRenderer renderer;
    private final Timer timer;
    private long lastTime;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        controller = new GameController();
        renderer = new GameRenderer();
        addKeyListener(new InputHandler(controller));
        controller.init();

        lastTime = System.nanoTime();
        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = Math.min((now - lastTime) / 1e9, 0.1);
        lastTime = now;
        controller.update(dt);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, controller);
    }
}
