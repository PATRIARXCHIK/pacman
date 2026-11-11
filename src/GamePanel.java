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
    private static final int PAC_SPAWN_X = 9, PAC_SPAWN_Y = 16;
    private static final int[][] GHOST_SPAWNS = {{8, 9}, {9, 9}, {10, 9}, {9, 10}};

    private char[][] grid;
    private Player pacman;
    private ArrayList<Ghost> ghosts;
    private long lastTime;
    private Timer timer;
    private GameRenderer renderer;
    private CollisionHandler collisions;
    private GameState state;
    private LevelManager levelManager;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        renderer = new GameRenderer();
        collisions = new CollisionHandler(this);
        state = new GameState();
        levelManager = new LevelManager(this);
        addKeyListener(new InputHandler(this));
        initGame();
        timer = new Timer(16, this);
        timer.start();
    }

    public void initGame() {
        state.reset();
        levelManager.reset();
        grid = Maze.createGrid();
        state.pelletsLeft = Maze.countPellets(grid);
        pacman = new Player(this, PAC_SPAWN_X, PAC_SPAWN_Y, levelManager.getPacSpeed());
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(this, GHOST_SPAWNS[0][0], GHOST_SPAWNS[0][1], levelManager.getGhostSpeed(), Color.RED, 0));
        ghosts.add(new Ghost(this, GHOST_SPAWNS[1][0], GHOST_SPAWNS[1][1], levelManager.getGhostSpeed(), Color.PINK, 1));
        ghosts.add(new Ghost(this, GHOST_SPAWNS[2][0], GHOST_SPAWNS[2][1], levelManager.getGhostSpeed(), Color.CYAN, 2));
        ghosts.add(new Ghost(this, GHOST_SPAWNS[3][0], GHOST_SPAWNS[3][1], levelManager.getGhostSpeed(), Color.ORANGE, 3));
        collisions.reset();
        lastTime = System.nanoTime();
    }

    public void restart() { initGame(); }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = Math.min((now - lastTime) / 1e9, 0.1);
        lastTime = now;
        if (!state.gameOver && !state.win) {
            if (state.frightTime > 0) state.frightTime = Math.max(0, state.frightTime - dt);
            if (state.speedBoostTime > 0) {
                state.speedBoostTime = Math.max(0, state.speedBoostTime - dt);
                if (state.speedBoostTime == 0) pacman.speed = levelManager.getPacSpeed();
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
        if (state.pelletsLeft == 0) {
            if (levelManager.isMaxLevel()) state.win = true;
            else levelManager.nextLevel();
        }
    }

    public void resetPositions() {
        pacman.reset(PAC_SPAWN_X, PAC_SPAWN_Y);
        pacman.speed = levelManager.getPacSpeed();
        pacman.wantDx = 0; pacman.wantDy = 0;
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).reset(GHOST_SPAWNS[i][0], GHOST_SPAWNS[i][1]);
            ghosts.get(i).speed = levelManager.getGhostSpeed();
        }
        state.frightTime = 0; state.speedBoostTime = 0;
        collisions.reset();
    }

    public void reloadMaze() {
        grid = Maze.createGrid();
        state.pelletsLeft = Maze.countPellets(grid);
    }

    public void clearEffects() {
        state.frightTime = 0; state.speedBoostTime = 0;
        pacman.speed = levelManager.getPacSpeed();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, this);
    }

    public char[][] getGrid() { return grid; }
    public Player getPacman() { return pacman; }
    public ArrayList<Ghost> getGhosts() { return ghosts; }
    public GameState getState() { return state; }
    public CollisionHandler getCollisions() { return collisions; }
    public LevelManager getLevelManager() { return levelManager; }
    public int getScore() { return state.score; }
    public void setScore(int s) { state.score = s; }
    public int getLives() { return state.lives; }
    public void setLives(int l) { state.lives = l; }
    public int getPelletsLeft() { return state.pelletsLeft; }
    public void setPelletsLeft(int p) { state.pelletsLeft = p; }
    public double getFrightTime() { return state.frightTime; }
    public void setFrightTime(double t) { state.frightTime = t; }
    public double getSpeedBoostTime() { return state.speedBoostTime; }
    public void setSpeedBoostTime(double t) { state.speedBoostTime = t; }
    public boolean isGameOver() { return state.gameOver; }
    public void setGameOver(boolean b) { state.gameOver = b; }
    public boolean isWin() { return state.win; }
}
