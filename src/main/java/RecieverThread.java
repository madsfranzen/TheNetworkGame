import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecieverThread extends Thread {
    private BufferedReader in;
    private boolean spritesLoaded = false;
    private boolean GUIloaded = false;

    private HashMap<String, String> players = new HashMap<>();
    private ArrayList<String> playerColors = new ArrayList<>();

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
        String messageFromServer;
        System.out.println("RecieverThread started (LOADING)");

        while (!spritesLoaded || !GUIloaded) {
            if (UpdateController.playerCanvas0 != null && UpdateController.playerCanvas1 != null
                    && UpdateController.scoreBoard != null
                    && UpdateController.gameRenderer != null) {
                setGUIloaded(true);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("RecieverThread started (READY)");

        try {

            String welcomeMessage = in.readLine();
            System.out.println(welcomeMessage);

            while (true) {
                messageFromServer = in.readLine();

                if (messageFromServer == "Unknown actions") {
                    System.out.println("\n === YOU HAVE BEEN KICKED === \n");
                    // TODO: PHILIP CLOSE GAMEWINDOW AND GO TO MAIN MENU
                    break;
                }

                System.out.println("RecieverThread: " + messageFromServer);
                // Parse JSON received
                JSONObject jsonObject = new JSONObject(messageFromServer);

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
            }
        } catch (IOException e) {
            System.out.println("RecieverThread error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("RecieverThread closed");
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
