import java.util.Random;

public class CollisionHandler {
    private final GamePanel game;
    private int bonusX = -1, bonusY = -1;
    private double bonusTime = 0;
    private int pelletsEatenSinceBonus = 0;
    private int ghostCombo = 0;

    public CollisionHandler(GamePanel game) {
        this.game = game;
    }

    public void eatPellet(int tx, int ty) {
        char c = game.getGrid()  ;
        if (c == '.') {
            game.getGrid()  = ' ';
            game.setScore(game.getScore() + 10);
            game.setPelletsLeft(game.getPelletsLeft() - 1);
            pelletsEatenSinceBonus++;
        } else if (c == 'o') {
            game.getGrid()   = ' ';
            game.setScore(game.getScore() + 50);
            game.setPelletsLeft(game.getPelletsLeft() - 1);
            game.setFrightTime(6.0);
            game.setSpeedBoostTime(5.0);
            game.getPacman().speed = 9.0;
            pelletsEatenSinceBonus++;
        }
    }

    public void checkCollisions() {
        for (Ghost g : game.getGhosts()) {
            double dx = g.pixelX() - game.getPacman().pixelX();
            double dy = g.pixelY() - game.getPacman().pixelY();
            if (dx * dx + dy * dy < (GamePanel.TILE * 0.7) * (GamePanel.TILE * 0.7)) {
                if (g.frightened) {
                    ghostCombo++;
                    int points = 200 * (1 << (ghostCombo - 1));
                    if (points > 1600) points = 1600;
                    game.setScore(game.getScore() + points);
                    g.reset(g.startX, g.startY);
                } else {
                    game.setLives(game.getLives() - 1);
                    if (game.getLives() <= 0) game.setGameOver(true);
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
            game.setScore(game.getScore() + 100);
            bonusX = -1;
            bonusY = -1;
        }
    }

    public void updateBonus(double dt) {
        if (bonusTime > 0) {
            bonusTime = Math.max(0, bonusTime - dt);
            if (bonusTime == 0) {
                bonusX = -1;
                bonusY = -1;
            }
        }
        if (pelletsEatenSinceBonus >= 25 && bonusX == -1 && game.getPelletsLeft() > 10) {
            spawnBonus();
        }
    }

    private void spawnBonus() {
        Random r = new Random();
        for (int i = 0; i < 80; i++) {
            int x = r.nextInt(GamePanel.COLS);
            int y = r.nextInt(GamePanel.ROWS);
            if (game.getGrid()  == ' ' &&
                Math.abs(x - game.getPacman().getTileX()) +
                Math.abs(y - game.getPacman().getTileY()) > 4) {
                bonusX = x;
                bonusY = y;
                bonusTime = 8.0;
                pelletsEatenSinceBonus = 0;
                return;
            }
        }
    }

    public void reset() {
        ghostCombo = 0;
        bonusX = -1;
        bonusY = -1;
        bonusTime = 0;
        pelletsEatenSinceBonus = 0;
    }

    public int getBonusX() { return bonusX; }
    public int getBonusY() { return bonusY; }
    public double getBonusTime() { return bonusTime; }
    public int getGhostCombo() { return ghostCombo; }

    public void setGhostCombo(int ghostCombo) {
        this.ghostCombo = ghostCombo;
    }
}
