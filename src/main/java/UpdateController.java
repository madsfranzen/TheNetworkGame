public class UpdateController {
    public static GameRenderer gameRenderer;
    public static PlayerCanvas playerCanvas;
    public static ScoreBoard scoreBoard;

    public UpdateController() {
        gameRenderer = null;
        playerCanvas = null;
        scoreBoard = null;
    }

    public static void setGameRenderer(GameRenderer gameRenderer) {
        UpdateController.gameRenderer = gameRenderer;
    }

    public static void setPlayerCanvas(PlayerCanvas playerCanvas) {
        UpdateController.playerCanvas = playerCanvas;
    }

    public static void setScoreBoard(ScoreBoard scoreBoard) {
        UpdateController.scoreBoard = scoreBoard;
    }

}
