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

    private static final String[] MAZE = {
        "###################",
        "#........#........#",
        "#.##.###.#.###.##.#",
        "#o##.###.#.###.##o#",
        "#.................#",
        "#.##.#.#####.#.##.#",
        "#....#...#...#....#",
        "####.###.#.###.####",
        "   #.#.......#.#   ",
        "####.#.## ##.#.####",
        "    ...#   #...    ",
        "####.#.#####.#.####",
        "   #.#.......#.#   ",
        "####.###.#.###.####",
        "#....#...#...#....#",
        "#.##.#.#####.#.##.#",
        "#o................o#",
        "#.##.###.#.###.##.#",
        "#........#........#",
        "###################"
        // добавь остальные строки лабиринта из своего оригинала
    };

    private char[][] grid;
    private Player pacman;
    private ArrayList<Ghost> ghosts;
    private int score, lives, pelletsLeft;
    private boolean gameOver, win;
    private double frightTime;
    private double speedBoostTime = 0;
    private final double NORMAL_SPEED = 6.5;
    private final double BOOST_SPEED = 9.0;
    private long lastTime;
    private Timer timer;
    private GameRenderer renderer;
    private LevelManager levelManager;
    private CollisionHandler collisions;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        renderer = new GameRenderer();
        levelManager = new LevelManager(this);
        collisions = new CollisionHandler(this);
        addKeyListener(new InputHandler(this));
        initGame();
        timer = new Timer(16, this);
        timer.start();
    }

    public void initGame() {
        grid = new char[ROWS][];
        pelletsLeft = 0;
        for (int r = 0; r < ROWS; r++) {
            grid[r] = MAZE[r].toCharArray();
            for (int c = 0; c < COLS; c++)
                if (grid[r][c] == '.' || grid[r][c] == 'o') pelletsLeft++;
        }
        pacman = new Player(this, 9, 17, NORMAL_SPEED);
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(this, 1, 1, 5.0, Color.RED, 0));
        ghosts.add(new Ghost(this, 17, 1, 5.0, Color.PINK, 1));
        ghosts.add(new Ghost(this, 1, 21, 5.0, Color.CYAN, 2));
        ghosts.add(new Ghost(this, 17, 21, 5.0, Color.ORANGE, 3));
        score = 0; lives = 3;
        gameOver = false; win = false; frightTime = 0;
        speedBoostTime = 0;
        levelManager.reset();
        collisions.reset();
        lastTime = System.nanoTime();
    }

    public void restart() { initGame(); }

    @id86240433 (@Override)
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;
        if (dt > 0.1) dt = 0.1;

        if (!gameOver && !win) {
            if (frightTime > 0) frightTime = Math.max(0, frightTime - dt);
            if (speedBoostTime > 0) {
                speedBoostTime = Math.max(0, speedBoostTime - dt);
                if (speedBoostTime == 0) pacman.speed = NORMAL_SPEED;
            }
            if (frightTime == 0) collisions.setGhostCombo(0);
            collisions.updateBonus(dt);
            update(dt);
        }
        repaint();
    }

    private void update(double dt) {
        pacman.update(dt);
        if (pacman.justArrived) collisions.eatPellet(pacman.getTileX(), pacman.getTileY());
        for (Ghost g : ghosts) {
            g.frightened = frightTime > 0;
            g.update(dt);
        }
        collisions.checkCollisions();
        collisions.checkBonus();
        if (pelletsLeft == 0) {
            if (levelManager.isMaxLevel()) win = true;
            else
