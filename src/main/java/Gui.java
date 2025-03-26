import java.io.DataOutputStream;
import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Gui {

    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private final int WINDOW_WIDTH = (int) screenBounds.getWidth();
    private final int WINDOW_HEIGHT = (int) screenBounds.getHeight();
    private static final int SCOREBOARD_WIDTH = 180;
    private static final int SCOREBOARD_HEIGHT = 300;

    private final int MOVE_DELAY_MILIS = 100;
    private boolean isMoving = false;

    private final GameRenderer gameRenderer = new GameRenderer(WINDOW_WIDTH, WINDOW_HEIGHT);
    public static final ScoreBoard scoreBoard = new ScoreBoard(SCOREBOARD_WIDTH, SCOREBOARD_HEIGHT);
    private static DataOutputStream outToServer = null;
    private final NameOverlay nameOverlay = new NameOverlay(WINDOW_WIDTH, WINDOW_HEIGHT);

    public void start(BorderPane root, Scene scene) throws Exception {
        // Create a StackPane to overlay the ScoreBoard on top of GameRenderer
        StackPane mainPane = new StackPane();

        // Add GameRenderer as the base layer
        mainPane.getChildren().add(gameRenderer);
        mainPane.getChildren().add(nameOverlay);

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
        //BorderPane root = new BorderPane();
        root.setCenter(mainPane);

        root.setStyle("-fx-background-color: #f0f0f0;");

        //Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set background color for the scene
        //scene.setFill(javafx.scene.paint.Color.LIGHTGRAY);

        //primaryStage.setScene(scene);
        //primaryStage.setResizable(false);
        //primaryStage.setTitle("THE NETWORK GAME CLIENT");

        //toggleWindowedFullscreen(primaryStage);

        UpdateController.setGameRenderer(gameRenderer);
        UpdateController.setPlayerCanvas0(gameRenderer.getPlayerCanvas0());
        UpdateController.setPlayerCanvas1(gameRenderer.getPlayerCanvas1());
        UpdateController.setScoreBoard(scoreBoard);
        UpdateController.setNameOverlay(nameOverlay);

        //primaryStage.show();
        //primaryStage.requestFocus();

        setDataOutputStream();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (isMoving) {
                event.consume();
                return;
            }
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    try {
                        isMoving = true;
                        String action = event.getCode() == KeyCode.UP ? Action.MOVEUP.toString()
                                : event.getCode() == KeyCode.DOWN ? Action.MOVEDOWN.toString()
                                        : event.getCode() == KeyCode.LEFT ? Action.MOVELEFT.toString()
                                                : Action.MOVERIGHT.toString();
                        if (action != null) {
                            outToServer.writeBytes(action + "\n");
                            System.out.println(action);
                        } else {
                            System.out.println("\n !!! ACTION IS NULL !!! \n");
                        }

                        PauseTransition pause = new PauseTransition(Duration.millis(MOVE_DELAY_MILIS));
                        pause.setOnFinished(e -> isMoving = false);
                        pause.play();
                    } catch (IOException e) {
                        System.out.println("Error moving: " + e);
                        isMoving = false;
                    }
                    break;
                case ESCAPE:
                    System.out.println("Closing application...\n");
                    try {
                        StartMenu.recieverThread.stop();
                        System.out.println("RecieverThread closed");
                        StartMenu.clientSocket.close();
                        System.out.println("Socket closed");
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println(
                                "Using deprecated methods to close socket. \nWe are aware of this issue and will fix it in the next update.");
                    }
                    System.exit(0);
                case SPACE:
                    gameRenderer.getPlayerCanvas0().drawHit(10, 10, "BLUE", 'r');
                    break;
                default:
                    break;
            }
        });
    }

    public static void setDataOutputStream() {
        outToServer = StartMenu.outToServer;
    }


}
