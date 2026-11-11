import java.util.ArrayList;

public class LevelManager {
    private int level = 1;
    private final int maxLevel = 5;
    private final GamePanel game;

    public LevelManager(GamePanel game) {
        this.game = game;
    }

    public int getLevel() {
        return level;
    }

    public boolean isMaxLevel() {
        return level >= maxLevel;
    }

    public void nextLevel() {
        if (level >= maxLevel) return;
        level++;

        double pacSpeed = 6.5 + (level - 1) * 0.6;
        double ghostSpeed = 5.0 + (level - 1) * 0.35;

        game.getPacman().speed = pacSpeed;
        for (Ghost g : game.getGhosts()) {
            g.speed = ghostSpeed;
        }

        game.reloadMaze();
        game.resetPositions();
        game.clearEffects();
    }

    public void reset() {
        level = 1;
    }
}
