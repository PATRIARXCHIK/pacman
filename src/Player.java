public class Player extends Entity {
    public int wantDx, wantDy;

    public Player(GameController game, int tx, int ty, double speed) {
        super(game, tx, ty, speed);
    }

    public void setDirection(int dx, int dy) {
        wantDx = dx; wantDy = dy;
        if (this.dx == 0 && this.dy == 0 && canMove(tileX + dx, tileY + dy)) {
            this.dx = dx; this.dy = dy;
        }
    }

    @Override
    protected void onStopped() {
        if ((wantDx != 0 || wantDy != 0) && canMove(tileX + wantDx, tileY + wantDy)) {
            dx = wantDx; dy = wantDy;
        }
    }

    @Override
    protected void decideNextMove() {
        if ((wantDx != 0 || wantDy != 0) && canMove(tileX + wantDx, tileY + wantDy)) {
            dx = wantDx; dy = wantDy; return;
        }
        if (!canMove(tileX + dx, tileY + dy)) { dx = 0; dy = 0; }
    }
}
