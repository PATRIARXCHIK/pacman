import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    public static final int TILE = 24;
    public static final int COLS = 19;
    public static final int ROWS = 23;
    public static final int WIDTH = COLS * TILE;
    public static final int HEIGHT = ROWS * TILE;

    private char[][] grid;
    private Player pacman;
    private ArrayList<Ghost> ghosts;
    private long lastTime;
    private Timer timer;
    private GameRenderer renderer;
    private CollisionHandler collisions;
    private GameState state;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        renderer = new GameRenderer();
        collisions = new CollisionHandler(this);
        state = new GameState();
        addKeyListener(new InputHandler(this));
        initGame();
        timer = new Timer(16, this);
        timer.start();
    }

    public void initGame() {
        grid = Maze.createGrid();
        state.pelletsLeft = Maze.countPellets(grid);
        pacman = new Player(this, 9, 17, 6.5);
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(this, 1, 1, 5.0, Color.RED, 0));
        ghosts.add(new Ghost(this, 17, 1, 5.0, Color.PINK, 1));
        ghosts.add(new Ghost(this, 1, 21, 5.0, Color.CYAN, 2));
        ghosts.add(new Ghost(this, 17, 21, 5.0, Color.ORANGE, 3));
        state.reset();
        collisions.reset();
        lastTime = System.nanoTime();
    }

    public void restart() { initGame(); }

    @id86240433 (@Override)
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = Math.min((now - lastTime) / 1e9, 0.1);
        lastTime = now;

        if (!state.gameOver && !state.win) {
            if (state.frightTime > 0) state.frightTime = Math.max(0, state.frightTime - dt);
            if (state.speedBoostTime > 0) {
                state.speedBoostTime = Math.max(0, state.speedBoostTime - dt);
                if (state.speedBoostTime == 0) pacman.speed = 6.5;
            }
            if (state.frightTime == 0) collisions.setGhostCombo(0);
            collisions.updateBonus(dt);
            update(dt);
        }
        repaint();
    }

    private void update(double dt) {
        pacman.update(dt);
        if (pacman.justArrived) collisions.eatPellet(pacman.getTileX(), pacman.getTileY());
        for (Ghost g : ghosts) {
            g.frightened = state.frightTime > 0;
            g.update(dt);
        }
        collisions.checkCollisions();
        collisions.checkBonus();
        if (state.pelletsLeft == 0) state.win = true;
    }

    public void resetPositions() {
        pacman.reset(9, 17);
        pacman.wantDx = 0;
        pacman.wantDy = 0;
        ghosts.get(0).reset(1, 1);
        ghosts.get(1).reset(17, 1);
        ghosts.get(2).reset(1, 21);
        ghosts.get(3).reset(17, 21);
        state.frightTime = 0;
        state.speedBoostTime = 0;
        collisions.reset();
    }

    @id86240433 (@Override)
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, this);
    }

    public char[][] getGrid() { return grid; }
    public Player getPacman() { return pacman; }
    public ArrayList<Ghost> getGhosts() { return ghosts; }
    public GameState getState() { return state; }
    public CollisionHandler getCollisions() { return collisions; }
}
