import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecieverThread extends Thread {
    private BufferedReader in;
    private boolean spritesLoaded = false;
    private boolean GUIloaded = false;

    public RecieverThread(Socket socket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String messageFromServer;
        System.out.println("RecieverThread started (LOADING)");

        while (!spritesLoaded || !GUIloaded) {
            if (UpdateController.playerCanvas != null && UpdateController.scoreBoard != null
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
                System.out.println("RecieverThread: " + messageFromServer);
                // Parse JSON received
                JSONObject jsonObject = new JSONObject(messageFromServer);

                // Parse scoreBoard
                if (jsonObject.has("scoreBoard")) {
                    JSONObject scoreBoard = jsonObject.getJSONObject("scoreBoard");
                    UpdateController.scoreBoard.updateScoreBoard(scoreBoard);
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
                        String sprite = fieldState.getString("sprite");
                        String contentType = fieldState.getString("contentType");
                        int zIndex = fieldState.getInt("zIndex");

                        // nullable player field
                        String player = fieldState.has("player") ? fieldState.getString("player") : null;

                        System.out.println("Update at (" + x + "," + y + "): " +
                                "Sprite=" + sprite + ", Type=" + contentType +
                                (player != null ? ", Player=" + player : ""));

                        if (player != null) {
                            UpdateController.playerCanvas.drawPlayer(x, y, "red");
                        } else {
                            UpdateController.playerCanvas.removePlayer(x, y);
                        }

                    }
                }
                // System.out.println(messageFromServer);
            }
        } catch (IOException e) {
            System.out.println("RecieverThread error: " + e.getMessage());
            e.printStackTrace();
        }
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
