import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            "####.#.......#.####",
            "####.#.#####.#.####",
            "#........#........#",
            "#.##.###.#.###.##.#",
            "#o.#.........#..o.#",
            "##.#.#.#####.#.#.##",
            "#....#...#...#....#",
            "#.####.#.#.#.####.#",
            "#.................#",
            "#........ ........#",
            "#.##.###.#.###.##.#",
            "#o.#.....#.....#.o#",
            "##.#.#######.#.#.##",
            "#....#.......#....#",
            "###################"
    };

    private char[][] grid;
    private Player pacman;
    private ArrayList<Ghost> ghosts;

    private int score, lives, pelletsLeft;
    private boolean gameOver, win;
    private double frightTime;
    private long lastTime;
    
    private double speedBoostTime = 0;
    private final double NORMAL_SPEED = 6.5;
    private final double BOOST_SPEED = 9.0;

    private Timer timer;
    private GameRenderer renderer;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        renderer = new GameRenderer();
        addKeyListener(new InputHandler(this)); // Подключаем обработчик ввода

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
        pacman = new Player(this, 9, 17, 6.5);
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(this, 1, 1, 5.0, Color.RED, 0));
        ghosts.add(new Ghost(this, 17, 1, 5.0, Color.PINK, 1));
        ghosts.add(new Ghost(this, 1, 21, 5.0, Color.CYAN, 2));
        ghosts.add(new Ghost(this, 17, 21, 5.0, Color.ORANGE, 3));

        score = 0; lives = 3;
        gameOver = false; win = false; frightTime = 0;
        lastTime = System.nanoTime();

        speedBoostTime = 0;
        if (pacman != null) pacman.speed = NORMAL_SPEED;
    }

    public void restart() {
        initGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;
        if (dt > 0.1) dt = 0.1;

        if (!gameOver && !win) {
            if (frightTime > 0) frightTime = Math.max(0, frightTime - dt);
            
            if (speedBoostTime > 0) {
    speedBoostTime = Math.max(0, speedBoostTime - dt);
    if (speedBoostTime == 0 && pacman != null) {
        pacman.speed = NORMAL_SPEED;
    }
}
            update(dt);
        }
        repaint();
    }

    private void update(double dt) {
        pacman.update(dt);
        if (pacman.justArrived) eatPellet(pacman.getTileX(), pacman.getTileY());
        for (Ghost g : ghosts) {
            g.frightened = frightTime > 0;
            g.update(dt);
        }
        checkCollisions();
        if (pelletsLeft == 0) win = true;
    }

    private void eatPellet(int tx, int ty) {
        char c = grid[ty][tx];
        if (c == '.') {
            grid[ty][tx] = ' '; score += 10; pelletsLeft--;
        } else if (c == 'o') {
            grid[ty][tx] = ' '; score += 50; pelletsLeft--; frightTime = 6.0; speedBoostTime = 5.0; pacman.speed = BOOST_SPEED;
        }
    }

    private void checkCollisions() {
        for (Ghost g : ghosts) {
            double dx = g.pixelX() - pacman.pixelX();
            double dy = g.pixelY() - pacman.pixelY();
            if (dx * dx + dy * dy < (TILE * 0.7) * (TILE * 0.7)) {
                if (g.frightened) {
                    score += 200;
                    g.reset(g.startX, g.startY);
                } else {
                    lives--;
                    if (lives <= 0) gameOver = true;
                    else resetPositions();
                    return;
                }
            }
        }
    }

    private void resetPositions() {
        pacman.reset(9, 17);
        pacman.wantDx = 0; pacman.wantDy = 0;
        ghosts.get(0).reset(1, 1);
        ghosts.get(1).reset(17, 1);
        ghosts.get(2).reset(1, 21);
        ghosts.get(3).reset(17, 21);
        frightTime = 0;
        
        speedBoostTime = 0;
        if (pacman != null) pacman.speed = NORMAL_SPEED;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, this); // Делегируем отрисовку рендереру
    }


    public char[][] getGrid() { return grid; }
    public Player getPacman() { return pacman; }
    public ArrayList<Ghost> getGhosts() { return ghosts; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }
    public boolean isWin() { return win; }
    public double getFrightTime() { return frightTime; }
    public double getSpeedBoostTime() { return speedBoostTime; }
}
