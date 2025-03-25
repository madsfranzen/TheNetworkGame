import java.io.DataOutputStream;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Gui extends Application {

    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private final int WINDOW_WIDTH = (int) screenBounds.getWidth();
    private final int WINDOW_HEIGHT = (int) screenBounds.getHeight();
    private static final int SCOREBOARD_WIDTH = 180;
    private static final int SCOREBOARD_HEIGHT = 300;

    private final GameRenderer gameRenderer = new GameRenderer(WINDOW_WIDTH, WINDOW_HEIGHT);
    public static final ScoreBoard scoreBoard = new ScoreBoard(SCOREBOARD_WIDTH, SCOREBOARD_HEIGHT);
    private static DataOutputStream outToServer = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create a StackPane to overlay the ScoreBoard on top of GameRenderer
        StackPane mainPane = new StackPane();
        
        // Add GameRenderer as the base layer
        mainPane.getChildren().add(gameRenderer);
        
        // Style the ScoreBoard to make it visible
        scoreBoard.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-border-color: white; -fx-border-width: 1;");
        
        // Add ScoreBoard as the overlay layer
        mainPane.getChildren().add(scoreBoard);
        
        // Position ScoreBoard to float in the top-right corner with margin
        StackPane.setAlignment(scoreBoard, Pos.TOP_RIGHT);
        StackPane.setMargin(scoreBoard, new Insets(20, 20, 0, 0));
        
        // Ensure ScoreBoard stays on top
        scoreBoard.setViewOrder(-1.0);
        
        // Set the main pane to fill the entire window
        mainPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create the root layout and add the main pane
        BorderPane root = new BorderPane();
        root.setCenter(mainPane);

        root.setStyle("-fx-background-color: #f0f0f0;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set background color for the scene
        scene.setFill(javafx.scene.paint.Color.LIGHTGRAY);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("THE NETWORK GAME CLIENT");

        toggleWindowedFullscreen(primaryStage);

        UpdateController.setGameRenderer(gameRenderer);
        UpdateController.setPlayerCanvas0(gameRenderer.getPlayerCanvas0());
        UpdateController.setPlayerCanvas1(gameRenderer.getPlayerCanvas1());
        UpdateController.setScoreBoard(scoreBoard);

        primaryStage.show();
        primaryStage.requestFocus();

        setDataOutputStream();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case UP:
                    try {
                        outToServer.writeBytes(Action.MOVEUP + "\n");
                        System.out.println(Action.MOVEUP);
                    } catch (IOException e) {
                        System.out.println("Error moving up: " + e);
                    }
                    break;
                case DOWN:
                    try {
                        outToServer.writeBytes(Action.MOVEDOWN + "\n");
                        System.out.println(Action.MOVEDOWN);
                    } catch (IOException e) {
                        System.out.println("Error moving down: " + e);
                    }
                    break;
                case LEFT:
                    try {
                        outToServer.writeBytes(Action.MOVELEFT + "\n");
                        System.out.println(Action.MOVELEFT);
                    } catch (IOException e) {
                        System.out.println("Error moving left: " + e);
                    }
                    break;
                case RIGHT:
                    try {
                        outToServer.writeBytes(Action.MOVERIGHT + "\n");
                        System.out.println(Action.MOVERIGHT);
                    } catch (IOException e) {
                        System.out.println("Error moving right: " + e);
                    }
                    break;
                case ESCAPE: System.exit(0);
                default: break;
            }
        });

    }

    public static void setDataOutputStream() {
        outToServer = App.outToServer;
    }

    private void toggleWindowedFullscreen(Stage primaryStage) {
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setFullScreen(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }
}
