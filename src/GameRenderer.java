
import java.awt.*;

public class GameRenderer {
    private static final Font HUD_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font BIG_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 18);

    public void render(Graphics2D g2, GamePanel game) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawMaze(g2, game);
        drawPacman(g2, (int) game.getPacman().pixelX(), (int) game.getPacman().pixelY(), game.getPacman());

        for (Ghost gh : game.getGhosts()) {
            drawGhost(g2, (int) gh.pixelX(), (int) gh.pixelY(), gh, game.getFrightTime());
        }

        drawHUD(g2, game);

        if (game.isGameOver()) drawOverlay(g2, "GAME OVER", Color.RED);
        else if (game.isWin()) drawOverlay(g2, "YOU WIN!", Color.YELLOW);
    }

    private void drawMaze(Graphics2D g2, GamePanel game) {
        char[][] grid = game.getGrid();
        for (int r = 0; r < GamePanel.ROWS; r++) {
            for (int c = 0; c < GamePanel.COLS; c++) {
                char ch = grid[r][c];
                int x = c * GamePanel.TILE, y = r * GamePanel.TILE;
                if (ch == '#') {
                    g2.setColor(new Color(30, 30, 180));
                    g2.fillRect(x, y, GamePanel.TILE, GamePanel.TILE);
                    g2.setColor(new Color(80, 80, 220));
                    g2.drawRect(x, y, GamePanel.TILE - 1, GamePanel.TILE - 1);
                } else if (ch == '.') {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x + GamePanel.TILE / 2 - 2, y + GamePanel.TILE / 2 - 2, 4, 4);
                } else if (ch == 'o') {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x + GamePanel.TILE / 2 - 5, y + GamePanel.TILE / 2 - 5, 10, 10);
                }
            }
        }
    }

    private void drawHUD(Graphics2D g2, GamePanel game) {
        g2.setColor(Color.WHITE);
        g2.setFont(HUD_FONT);
        g2.drawString("Score: " + game.getScore(), 10, 20);
        g2.drawString("Lives: " + game.getLives(), GamePanel.WIDTH - 90, 20);
    }

    private void drawOverlay(Graphics2D g2, String text, Color color) {
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        g2.setColor(color);
        g2.setFont(BIG_FONT);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (GamePanel.WIDTH - fm.stringWidth(text)) / 2, GamePanel.HEIGHT / 2);
        g2.setColor(Color.WHITE);
        g2.setFont(SMALL_FONT);
        String sub = "Press SPACE to restart";
        fm = g2.getFontMetrics();
        g2.drawString(sub, (GamePanel.WIDTH - fm.stringWidth(sub)) / 2, GamePanel.HEIGHT / 2 + 40);
    }

    private void drawPacman(Graphics2D g2, int px, int py, Player pacman) {
        int r = GamePanel.TILE / 2 - 2;
        int mouth = (int) (25 * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 100.0)));
        int start = 0;
        if (pacman.getDx() == 1) start = 0;
        else if (pacman.getDx() == -1) start = 180;
        else if (pacman.getDy() == -1) start = 90;
        else if (pacman.getDy() == 1) start = 270;
        else if (pacman.wantDx == 1) start = 0;
        else if (pacman.wantDx == -1) start = 180;
        else if (pacman.wantDy == -1) start = 90;
        else if (pacman.wantDy == 1) start = 270;

        g2.setColor(Color.YELLOW);
        g2.fillArc(px - r, py - r, r * 2, r * 2, start + mouth, 360 - 2 * mouth);
    }

    private void drawGhost(Graphics2D g2, int px, int py, Ghost gh, double frightTime) {
        Color base;
        if (gh.frightened) {
            boolean flash = frightTime < 2 && ((int) (frightTime * 5) % 2 == 0);
            base = flash ? Color.WHITE : new Color(60, 60, 200);
        } else base = gh.color;

        g2.setColor(base);
        int r = GamePanel.TILE / 2 - 2;
        g2.fillArc(px - r, py - r, r * 2, r * 2, 0, 180);
        int bodyBottom = py + r - (int) (r * 0.4);
        g2.fillRect(px - r, py, r * 2, bodyBottom - py);
        int n = 3, w = (r * 2) / n;
        for (int i = 0; i < n; i++) {
            int x = px - r + i * w;
            int[] xp = {x, x + w / 2, x + w};
            int[] yp = {bodyBottom, py + r, bodyBottom};
            g2.fillPolygon(xp, yp, 3);
        }

        if (gh.frightened) {
            g2.setColor(Color.WHITE);
            g2.fillRect(px - 4, py - 2, 3, 3);
            g2.fillRect(px + 2, py - 2, 3, 3);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(px - r / 2 - 4, py - r / 2 - 2, 8, 9);
            g2.fillOval(px + r / 2 - 4, py - r / 2 - 2, 8, 9);
            g2.setColor(Color.BLUE);
            int lx = gh.getDx() * 2, ly = gh.getDy() * 2;
            g2.fillOval(px - r / 2 - 2 + lx, py - r / 2 + ly, 4, 5);
            g2.fillOval(px + r / 2 - 2 + lx, py - r / 2 + ly, 4, 5);
        }
    }
}