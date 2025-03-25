public class UpdateController {
    public static GameRenderer gameRenderer;
    public static PlayerCanvas playerCanvas0;
    public static PlayerCanvas playerCanvas1;
    public static ScoreBoard scoreBoard;

    public UpdateController() {
        gameRenderer = null;
        playerCanvas0 = null;
        playerCanvas1 = null;
        scoreBoard = null;
    }

    public static void setGameRenderer(GameRenderer gameRenderer) {
        UpdateController.gameRenderer = gameRenderer;
    }

    public static void setPlayerCanvas0(PlayerCanvas playerCanvas0) {
        UpdateController.playerCanvas0 = playerCanvas0;
    }

    public static void setPlayerCanvas1(PlayerCanvas playerCanvas1) {
        UpdateController.playerCanvas1 = playerCanvas1;
    }

    public static void setScoreBoard(ScoreBoard scoreBoard) {
        UpdateController.scoreBoard = scoreBoard;
    }

}
