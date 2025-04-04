import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class RecieverThread extends Thread {
    private BufferedReader in;
    private boolean spritesLoaded = false;
    private boolean GUIloaded = false;

    private HashMap<String, String> players = new HashMap<>();
    private ArrayList<String> playerColors = new ArrayList<>();

    private GameRenderer gameRenderer;

    private boolean running = true;

    public RecieverThread(Socket socket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        playerColors.add("red");
        playerColors.add("blue");
        playerColors.add("yellow");
        playerColors.add("purple");
    }

    public void run() {
        gameRenderer = Gui.getGameRenderer();
        String messageFromServer;
        System.out.println("RecieverThread started (LOADING)");

        while (!spritesLoaded || !GUIloaded) {
            if (UpdateController.playerCanvas0 != null && UpdateController.playerCanvas1 != null
                    && UpdateController.scoreBoard != null
                    && UpdateController.gameRenderer != null) {
                setGUIloaded(true);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("RecieverThread started (READY)");

        try {

            String welcomeMessage = in.readLine();

            while (running) {
                messageFromServer = in.readLine();

                if (messageFromServer.equals("EXIT")) {
                    System.out.println("\n === YOU HAVE BEEN KICKED === \n");
                    running = false;
                    StartMenu.clientSocket.close();
                    StartMenu.goToMainMenu();

                    break;
                } else {

                    System.out.println("RecieverThread: " + messageFromServer);
                    // Parse JSON received
                    JSONObject jsonObject = new JSONObject(messageFromServer);

                    // Parse gameState
                    if (jsonObject.has("gameState")) {
                        JSONObject gameState = jsonObject.getJSONObject("gameState");
                        JSONArray updatePackages = gameState.getJSONArray("updatePackages");

                        for (int i = 0; i < updatePackages.length(); i++) {
                            JSONObject update = updatePackages.getJSONObject(i);
                            JSONObject pair = update.getJSONObject("pair");
                            int x = pair.getInt("x");
                            int y = pair.getInt("y");

                            JSONObject fieldState = update.getJSONObject("fieldState");
                            String contentType = fieldState.getString("contentType");
                            int zIndex = fieldState.getInt("zIndex");

                            // nullable player field
                            String player = fieldState.has("player") ? fieldState.getString("player") : null;
                            String direction = fieldState.has("PlayerDirection") ? fieldState.getString("PlayerDirection") : null;

                            System.out.println("Update at (" + x + "," + y + "): " +
                                    "Type=" + contentType +
                                    (player != null ? ", Player=" + player : ""));

                            if (player != null) {
                                if (player.equals(App.username)) {
                                    smoothScroll(x, y);
                                }
                                if (zIndex == 0 && !contentType.equals("STAIRS")) {
                                    UpdateController.playerCanvas0.drawPlayer(x, y, players.get(player), direction);
                                    UpdateController.nameOverlay.drawName(x, y, player);
                                }
                                if (zIndex == 1 || contentType.equals("STAIRS")) {
                                    UpdateController.playerCanvas1.drawPlayer(x, y, players.get(player), direction);
                                    UpdateController.nameOverlay.drawName(x, y, player);
                                }
                            } else {
                                UpdateController.playerCanvas0.removePlayer(x, y);
                                UpdateController.playerCanvas1.removePlayer(x, y);
                                UpdateController.nameOverlay.removeName(x, y);
                            }
                        }
                    }

                    // Parse scoreBoard
                    if (jsonObject.has("scoreBoard")) {
                        JSONObject scoreBoard = jsonObject.getJSONObject("scoreBoard");
                        UpdateController.scoreBoard.updateScoreBoard(scoreBoard, players);
                        JSONArray playersConnected = scoreBoard.getJSONArray("players");
                        for (int i = 0; i < playersConnected.length(); i++) {
                            JSONObject player = playersConnected.getJSONObject(i);
                            players.put(player.getString("playerName"), playerColors.get(i % playerColors.size()));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void smoothScroll(double deltaX, double deltaY) {
        double maxSteps = 31; // Normalize deltaX and deltaY within 0.0 - 1.0 range
        double stepSize = 1.0 / maxSteps;

        double startH = gameRenderer.getHvalue();
        double startV = gameRenderer.getVvalue();

        double endH = deltaX * stepSize;
        double endV = deltaY * stepSize;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(gameRenderer.hvalueProperty(), startH), new KeyValue(gameRenderer.vvalueProperty(), startV)),
                new KeyFrame(Duration.millis(500), new KeyValue(gameRenderer.hvalueProperty(), endH), new KeyValue(gameRenderer.vvalueProperty(), endV))
        );

        timeline.setCycleCount(1);
        timeline.play();
    }




    public void setSpritesLoaded(boolean spritesLoaded) {
        this.spritesLoaded = spritesLoaded;
        System.out.println("RECIEVERTHREAD: Sprites loaded");
    }

    public void setGUIloaded(boolean GUIloaded) {
        System.out.println("RECIEVERTHREAD: GUI loaded");
        this.GUIloaded = GUIloaded;
    }
}
