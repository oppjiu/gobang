package restful.bean;

/**
 * @author lwz
 * @create 2021-12-05 20:13
 * @description:
 */
public class GameData {
    private boolean isStartGame;
    private boolean isPauseGame;
    private boolean isGiveUp;
    private boolean isDraw;
    private boolean isRegretStep;
    private int stepX;
    private int stepY;
    private String winner;

    public boolean isStartGame() {
        return isStartGame;
    }

    public void setStartGame(boolean startGame) {
        isStartGame = startGame;
    }

    public boolean isPauseGame() {
        return isPauseGame;
    }

    public void setPauseGame(boolean pauseGame) {
        isPauseGame = pauseGame;
    }

    public boolean isGiveUp() {
        return isGiveUp;
    }

    public void setGiveUp(boolean giveUp) {
        isGiveUp = giveUp;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean draw) {
        isDraw = draw;
    }

    public boolean isRegretStep() {
        return isRegretStep;
    }

    public void setRegretStep(boolean regretStep) {
        isRegretStep = regretStep;
    }

    public int getStepX() {
        return stepX;
    }

    public void setStepX(int stepX) {
        this.stepX = stepX;
    }

    public int getStepY() {
        return stepY;
    }

    public void setStepY(int stepY) {
        this.stepY = stepY;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
