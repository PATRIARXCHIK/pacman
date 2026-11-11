public class GameState {
    public int score, lives, pelletsLeft;
    public double frightTime, speedBoostTime;
    public boolean gameOver, win;
    public void reset() {
        score = 0; lives = 3; pelletsLeft = 0;
        frightTime = 0; speedBoostTime = 0;
        gameOver = false; win = false;
    }
}
