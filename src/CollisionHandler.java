import java.util.Random;

public class CollisionHandler {
    private final GameController game;
    private int bonusX = -1, bonusY = -1;
    private double bonusTime = 0;
    private int pelletsEatenSinceBonus = 0;
    private int ghostCombo = 0;

    public CollisionHandler(GameController game) { this.game = game; }

    public void eatPellet(int tx, int ty) {
        char c = game.getGrid()[ty][tx];
        if (c == '.') {
            game.getGrid()[ty][tx] = ' ';
            game.getState().score += 10;
            game.getState().pelletsLeft--;
            pelletsEatenSinceBonus++;
        } else if (c == 'o') {
            game.getGrid()[ty][tx] = ' ';
            game.getState().score += 50;
            game.getState().pelletsLeft--;
            game.getState().frightTime = 6.0;
            game.getState().speedBoostTime = 5.0;
            game.getPacman().speed = 9.0;
            pelletsEatenSinceBonus++;
        }
    }

    public void checkCollisions() {
        for (Ghost g : game.getGhosts()) {
            double dx = g.pixelX() - game.getPacman().pixelX();
            double dy = g.pixelY() - game.getPacman().pixelY();
            if (dx * dx + dy * dy < (GameConfig.TILE * 0.7) * (GameConfig.TILE * 0.7)) {
                if (g.frightened) {
                    ghostCombo++;
                    int points = 200 * (1 << (ghostCombo - 1));
                    if (points > 1600) points = 1600;
                    game.getState().score += points;
                    g.reset(g.startX, g.startY);
                } else {
                    game.getState().lives--;
                    if (game.getState().lives <= 0) game.getState().gameOver = true;
                    else game.resetPositions();
                    return;
                }
            }
        }
    }

    public void checkBonus() {
        if (bonusX != -1 && game.getPacman().justArrived &&
            game.getPacman().getTileX() == bonusX &&
            game.getPacman().getTileY() == bonusY) {
            game.getState().score += 100;
            bonusX = -1; bonusY = -1;
        }
    }

    public void updateBonus(double dt) {
        if (bonusTime > 0) {
            bonusTime = Math.max(0, bonusTime - dt);
            if (bonusTime == 0) { bonusX = -1; bonusY = -1; }
        }
        if (pelletsEatenSinceBonus >= 25 && bonusX == -1 && game.getState().pelletsLeft > 10) spawnBonus();
    }

    private void spawnBonus() {
        Random r = new Random();
        for (int i = 0; i < 80; i++) {
            int x = r.nextInt(GameConfig.COLS);
            int y = r.nextInt(GameConfig.ROWS);
            if (game.getGrid()[y][x] == ' ' &&
                Math.abs(x - game.getPacman().getTileX()) +
                Math.abs(y - game.getPacman().getTileY()) > 4) {
                bonusX = x; bonusY = y; bonusTime = 8.0;
                pelletsEatenSinceBonus = 0;
                return;
            }
        }
    }

    public void reset() {
        ghostCombo = 0; bonusX = -1; bonusY = -1;
        bonusTime = 0; pelletsEatenSinceBonus = 0;
    }

    public int getBonusX() { return bonusX; }
    public int getBonusY() { return bonusY; }
    public double getBonusTime() { return bonusTime; }
    public int getGhostCombo() { return ghostCombo; }
    public void setGhostCombo(int ghostCombo) { this.ghostCombo = ghostCombo; }
}
