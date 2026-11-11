import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Ghost extends Entity {
    public Color color;
    public int type;
    public boolean frightened;
    private Random rand = new Random();

    public Ghost(GamePanel game, int tx, int ty, double speed, Color color, int type) {
        super(game, tx, ty, speed);
        this.color = color;
        this.type = type;
    }

    @Override
    public void update(double dt) {
        double base = speed;
        if (frightened) speed = base * 0.6;
        super.update(dt);
        speed = base;
    }

    @Override
    protected void onStopped() { decideNextMove(); }

    @Override
    protected void decideNextMove() {
        if (frightened) { pickRandom(); return; }

        Player pacman = game.getPacman();
        int tx = pacman.getTileX(), ty = pacman.getTileY();

        if (type == 1) { // Розовый - на 4 клетки вперед
            tx += pacman.getDx() * 4;
            ty += pacman.getDy() * 4;
        } else if (type == 2 && rand.nextDouble() < 0.15) { // Голубой - иногда случайно
            pickRandom(); return;
        } else if (type == 3) { // Оранжевый - убегает
            int d = Math.abs(tileX - pacman.getTileX()) + Math.abs(tileY - pacman.getTileY());
            if (d < 6 && rand.nextDouble() < 0.5) { pickRandom(); return; }
        }
        greedyMove(tx, ty);
    }

    private void greedyMove(int tx, int ty) {
    int[] dxs = {0, 1, 0, -1};
    int[] dys = {-1, 0, 1, 0};
    int bDx = 0, bDy = 0, best = Integer.MAX_VALUE;
    for (int i = 0; i < 4; i++) {
        int ndx = dxs[i], ndy = dys[i];
        if (ndx == -dx && ndy == -dy && (dx != 0 || dy != 0)) continue;
        if (!canMove(tileX + ndx, tileY + ndy)) continue;
        int d = Math.abs(tileX + ndx - tx) + Math.abs(tileY + ndy - ty);
        if (d < best) { best = d; bDx = ndx; bDy = ndy; }
    }
    if (best == Integer.MAX_VALUE) {
        for (int i = 0; i < 4; i++)
            if (canMove(tileX + dxs[i], tileY + dys[i])) { bDx = dxs[i]; bDy = dys[i]; break; }
    }
    dx = bDx; dy = bDy;
}

    private void pickRandom() {
        int[] dxs = {0, 1, 0, -1};
        int[] dys = {-1, 0, 1, 0};
        ArrayList<Integer> ok = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (dxs[i] == -dx && dys[i] == -dy && (dx != 0 || dy != 0)) continue;
            if (canMove(tileX + dxs[i], tileY + dys[i])) ok.add(i);
        }
        if (ok.isEmpty()) return;
        int p = ok.get(rand.nextInt(ok.size()));
        dx = dxs[p]; dy = dys[p];
    }
}
