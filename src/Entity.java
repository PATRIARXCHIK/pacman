public abstract class Entity {
    protected int tileX, tileY, startX, startY;
    protected int dx, dy;
    protected double progress, speed;
    public boolean justArrived;
    protected GamePanel game; // Ссылка на игру для проверки стенок

    public Entity(GamePanel game, int tx, int ty, double speed) {
        this.game = game;
        this.tileX = this.startX = tx;
        this.tileY = this.startY = ty;
        this.speed = speed;
    }

    public void reset(int tx, int ty) {
        this.tileX = this.startX = tx;
        this.tileY = this.startY = ty;
        this.dx = this.dy = 0;
        this.progress = 0;
        this.justArrived = false;
    }

    protected boolean canMove(int tx, int ty) {
        if (tx < 0 || tx >= GamePanel.COLS || ty < 0 || ty >= GamePanel.ROWS) return false;
        return game.getGrid()[ty][tx] != '#';
    }

    public double pixelX() { return (tileX + dx * progress + 0.5) * GamePanel.TILE; }
    public double pixelY() { return (tileY + dy * progress + 0.5) * GamePanel.TILE; }

    public void update(double dt) {
        justArrived = false;
        if (dx == 0 && dy == 0) {
            onStopped();
            if (dx == 0 && dy == 0) return;
        }
        progress += dt * speed;
        if (progress >= 1) {
            tileX += dx; tileY += dy;
            progress = 0;
            justArrived = true;
            decideNextMove();
        }
    }

    protected void onStopped() {}
    protected abstract void decideNextMove();

    // Геттеры для координат и направления
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
}