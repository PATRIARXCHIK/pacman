public class LevelManager {
    public static final double BASE_PAC_SPEED = 6.5;
    public static final double BASE_GHOST_SPEED = 5.0;
    public static final double PAC_SPEED_PER_LEVEL = 0.6;
    public static final double GHOST_SPEED_PER_LEVEL = 0.35;

    private int level = 1;
    private final int maxLevel = 5;
    private final GameController game;

    public LevelManager(GameController game) { this.game = game; }

    public int getLevel() { return level; }
    public boolean isMaxLevel() { return level >= maxLevel; }
    public double getPacSpeed() { return BASE_PAC_SPEED + (level - 1) * PAC_SPEED_PER_LEVEL; }
    public double getGhostSpeed() { return BASE_GHOST_SPEED + (level - 1) * GHOST_SPEED_PER_LEVEL; }

    public void nextLevel() {
        if (level >= maxLevel) return;
        level++;
        game.getPacman().speed = getPacSpeed();
        for (Ghost g : game.getGhosts()) g.speed = getGhostSpeed();
        game.reloadMaze();
        game.resetPositions();
        game.clearEffects();
    }

    public void reset() { level = 1; }
}
