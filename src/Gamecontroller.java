import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static final int PAC_SPAWN_X = 9, PAC_SPAWN_Y = 16;
    private static final int[][] GHOST_SPAWNS = {{8, 9}, {9, 9}, {10, 9}, {9, 10}};
    private static final Color[] GHOST_COLORS = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};

    private final GameState state = new GameState();
    private final LevelManager levelManager = new LevelManager(this);
    private final CollisionHandler collisions = new CollisionHandler(this);

    private char[][] grid;
    private Player pacman;
    private List<Ghost> ghosts;

    public void init() {
        state.reset();
        levelManager.reset();
        grid = Maze.createGrid();
        state.pelletsLeft = Maze.countPellets(grid);
        pacman = new Player(this, PAC_SPAWN_X, PAC_SPAWN_Y, levelManager.getPacSpeed());
        ghosts = new ArrayList<>();
        for (int i = 0; i < GHOST_SPAWNS.length; i++) {
            ghosts.add(new Ghost(this, GHOST_SPAWNS[i][0], GHOST_SPAWNS[i][1],
                levelManager.getGhostSpeed(), GHOST_COLORS[i], i));
        }
        collisions.reset();
    }

    public void update(double dt) {
        if (state.gameOver || state.win) return;

        if (state.frightTime > 0) state.frightTime = Math.max(0, state.frightTime - dt);
        if (state.speedBoostTime > 0) {
            state.speedBoostTime = Math.max(0, state.speedBoostTime - dt);
            if (state.speedBoostTime == 0) pacman.speed = levelManager.getPacSpeed();
        }
        if (state.frightTime == 0) collisions.setGhostCombo(0);

        collisions.updateBonus(dt);
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
        pacman.wantDx = 0;
        pacman.wantDy = 0;
        for (int i = 0; i < ghosts.size(); i++) {
            Ghost g = ghosts.get(i);
            g.reset(GHOST_SPAWNS[i][0], GHOST_SPAWNS[i][1]);
            g.speed = levelManager.getGhostSpeed();
        }
        state.frightTime = 0;
        state.speedBoostTime = 0;
        collisions.reset();
    }

    public void reloadMaze() {
        grid = Maze.createGrid();
        state.pelletsLeft = Maze.countPellets(grid);
    }

    public void clearEffects() {
        state.frightTime = 0;
        state.speedBoostTime = 0;
        pacman.speed = levelManager.getPacSpeed();
    }

    public void restart() { init(); }

    public boolean isGameOver() { return state.gameOver; }
    public boolean isWin() { return state.win; }

    public GameState getState() { return state; }
    public char[][] getGrid() { return grid; }
    public Player getPacman() { return pacman; }
    public List<Ghost> getGhosts() { return ghosts; }
    public LevelManager getLevelManager() { return levelManager; }
    public CollisionHandler getCollisions() { return collisions; }
}
