import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataOutputStream;
import java.net.Socket;

public class StartMenu extends Application {

    BorderPane borderPane;
    Scene scene;
    Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        GridPane pane = new GridPane();

        borderPane = new BorderPane();
        borderPane.setCenter(pane);

        scene = new Scene(borderPane);

        initContent(pane);

        setBackgroundImage();

        toggleWindowedFullscreen(window);
        window.setScene(scene);
        window.show();
        window.requestFocus();
    }

    // Fields for clearing
    private TextField txfNavn = new TextField();
    private TextField txfIp = new TextField();
    private TextField txfPort = new TextField();
    private Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

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

    private void setBackgroundImage() {
        Image backgroundImage = new Image("/assets/UI/Backgrounds/mainBG.png");

        BackgroundImage backgroundImg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false)
        );

        borderPane.setBackground(new Background(backgroundImg));
    }

    private void addSceneEventListener() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ESCAPE -> System.exit(0);
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

    private void toggleWindowedFullscreen(Stage primaryStage) {
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setFullScreen(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }
}
