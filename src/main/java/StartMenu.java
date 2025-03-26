import java.io.DataOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StartMenu extends Application {

    private static BorderPane borderPane;
    private Scene scene;
    private static Stage window;
    private static GridPane pane;
    private Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        pane = new GridPane();

        borderPane = new BorderPane();
        borderPane.setCenter(pane);

        scene = new Scene(borderPane);
        
        initContent(pane);

        setBackgroundImage();

        configureWindow(window);
        window.setScene(scene);
        window.show();
        window.requestFocus();
    }

    // Fields for clearing
    private TextField txfNavn = new TextField();
    private TextField txfIp = new TextField();
    private TextField txfPort = new TextField();

    private void initContent(GridPane pane) {
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setAlignment(Pos.CENTER);

        // ========== Input fields ==========
        Label lblNavn = new Label("Navn:");
        pane.add(lblNavn, 0, 0);

        txfNavn.setPrefWidth(300);
        pane.add(txfNavn, 0, 1);

        Label lblIp = new Label("Ip:");
        pane.add(lblIp, 0, 2);

        txfIp.setPrefWidth(300);
        txfIp.setText("95.138.218.31");
        pane.add(txfIp, 0, 3);

        Label lblPort = new Label("Port:");
        pane.add(lblPort, 0, 4);

        txfPort.setPrefWidth(300);
        txfPort.setText("30000");
        pane.add(txfPort, 0, 5);

        // ========== Buttons ==========
        HBox btnHBox = new HBox();

        btnHBox.setSpacing(10);

        Button btnClear = new Button("Clear");
        GridPane.setHalignment(btnClear, HPos.CENTER);
        btnClear.setOnAction(event -> clearAction());

        Button btnConnect = new Button("Connect");
        GridPane.setHalignment(btnConnect, HPos.CENTER);
        btnConnect.setOnAction(event -> connectAction());

        btnHBox.getChildren().addAll(btnClear, btnConnect);

        pane.add(btnHBox, 0, 6, 2, 1);

        addSceneEventListener();
    }

    private static void setBackgroundImage() {
        try {
            // Load the image
            String imagePath = "/assets/UI/Backgrounds/mainBG.png";
            Image backgroundImage = new Image(imagePath, true);
            
            // Verify image loaded successfully
            if (backgroundImage.isError()) {
                System.err.println("Error loading background image: " + backgroundImage.getException());
                return;
            }
            
            // Calculate aspect ratios for adaptive stretching
            double imageAspectRatio = backgroundImage.getWidth() / backgroundImage.getHeight();
            Rectangle2D bounds = getScreenBounds();
            double screenAspectRatio = bounds.getWidth() / bounds.getHeight();
            
            // Determine optimal stretch factors based on aspect ratios
            double widthStretch = 120;  // Default stretch percentage
            double heightStretch = 120; // Default stretch percentage
            
            // Adapt stretching based on aspect ratio comparison
            if (imageAspectRatio > screenAspectRatio) {
                heightStretch = 150; // More aggressive height stretch
            } 
            else if (imageAspectRatio < screenAspectRatio) {
                widthStretch = 150; // More aggressive width stretch
            }
            
            // Get image URL for CSS
            String imageUrl = StartMenu.class.getResource(imagePath).toExternalForm();
            
            // CSS with optimized stretching
            String css = String.format(
                "-fx-background-image: url('%s'); " +
                "-fx-background-size: %d%% %d%%; " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-padding: 0; " +
                "-fx-background-insets: 0;",
                imageUrl, (int)widthStretch, (int)heightStretch);
            
            // Apply the CSS to the BorderPane
            borderPane.setStyle(css);
            
            // Make BorderPane larger than the screen to ensure full coverage
            double extraSize = 50; // Add 50 pixels in each direction
            borderPane.setPrefWidth(bounds.getWidth() + extraSize);
            borderPane.setPrefHeight(bounds.getHeight() + extraSize);
            borderPane.setMinWidth(bounds.getWidth() + extraSize);
            borderPane.setMinHeight(bounds.getHeight() + extraSize);
            
            // Center the oversized BorderPane
            borderPane.setLayoutX(-extraSize/2);
            borderPane.setLayoutY(-extraSize/2);
            
            // Set black Scene background as fallback
            if (window != null && window.getScene() != null) {
                window.getScene().setFill(javafx.scene.paint.Color.BLACK);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to set background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addSceneEventListener() {
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (borderPane.getCenter() == pane) {
                switch (event.getCode()) {
                    case ESCAPE:
                        System.exit(0);
                        break;
                }
            }
        });
    }

    private void clearAction() {
        txfNavn.clear();
        txfIp.clear();
        txfPort.clear();
    }

    public static DataOutputStream outToServer = null;
    public static RecieverThread recieverThread;
    public static Socket clientSocket;

    private void connectAction() {
        // MAIN THREAD
        String name = txfNavn.getText().trim();
        String ip = txfIp.getText().trim();
        int port = Integer.parseInt(txfPort.getText().trim());

        try {
            clientSocket = new Socket(ip, port);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());

            recieverThread = new RecieverThread(clientSocket);

            SpriteLoader.loadSprites(recieverThread);

            outToServer.writeBytes(name + '\n');
            recieverThread.start();

            App.setUsername(name);
            Gui gui = new Gui();
            gui.start(borderPane, scene);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Connecton failed!");
            alert.setContentText(e.getMessage());
            alert.show();

            System.out.println("CONNECTION INITIALIZATION ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureWindow(Stage primaryStage) {
        // Configure window to fill screen without decorations
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setResizable(false);
    }

    public static void goToMainMenu() {
        Platform.runLater(() -> {
            try {
                // Set the pane back as the center content
                borderPane.setCenter(pane);
                
                // Re-apply the background image
                setBackgroundImage();
            } catch (Exception e) {
                System.err.println("Error returning to main menu: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Static method to get screen bounds from anywhere
    private static Rectangle2D getScreenBounds() {
        return Screen.getPrimary().getVisualBounds();
    }
}
