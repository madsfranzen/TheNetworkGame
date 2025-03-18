import java.io.DataOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private final int WINDOW_WIDTH = (int) screenBounds.getWidth();
    private final int WINDOW_HEIGHT = (int) screenBounds.getHeight();

    private final GameRenderer gameRenderer = new GameRenderer(WINDOW_WIDTH, WINDOW_HEIGHT);

    public static void main(String[] args) {

        // MAIN THREAD
        String name = "Mads";
        Socket clientSocket;
        DataOutputStream outToServer = null;
        RecieverThread recieverThread;

        try {
            clientSocket = new Socket("95.138.218.31", 30000);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            recieverThread = new RecieverThread(clientSocket);

            outToServer.writeBytes(name + '\n');
            recieverThread.start();

            System.out.println("Connection initialized");

        } catch (Exception e) {
            System.out.println("CONNECTION INITIALIZATION ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        // Stack the debug overlay on top of main application
        StackPane mainPane = new StackPane(gameRenderer);

        mainPane.setMaxSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainPane.setMinSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        root.setCenter(mainPane);

        root.setStyle("-fx-background-color: #f0f0f0;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set background color for the scene
        scene.setFill(javafx.scene.paint.Color.LIGHTGRAY);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("THE NETWORK GAME CLIENT");

        toggleWindowedFullscreen(primaryStage);

        primaryStage.show();
    }

    private void toggleWindowedFullscreen(Stage primaryStage) {
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }
}