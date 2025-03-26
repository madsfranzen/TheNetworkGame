import java.io.DataOutputStream;
import java.net.Socket;

import javafx.application.Application;

public class App {

    public static void main(String[] args) {

        // Opening connection window
        Application.launch(StartMenu.class);

        String name = "Philip";
        Socket clientSocket;
        RecieverThread recieverThread;

        //clientSocket = new Socket("95.138.218.31", 30000);
    }
}