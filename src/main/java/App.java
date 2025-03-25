import java.io.DataOutputStream;
import java.net.Socket;

import javafx.application.Application;

public class App {

    public static DataOutputStream outToServer = null;

    public static void main(String[] args) {

        // MAIN THREAD
        String name = "Jensemanden";
        Socket clientSocket;
        RecieverThread recieverThread;

        try {
            clientSocket = new Socket("95.138.218.31", 30000);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            recieverThread = new RecieverThread(clientSocket);

            SpriteLoader.loadSprites(recieverThread);

            outToServer.writeBytes(name + '\n');
            recieverThread.start();


        } catch (Exception e) {
            System.out.println("CONNECTION INITIALIZATION ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        Application.launch(Gui.class);

    }


}