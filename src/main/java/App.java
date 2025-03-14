import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class App {
    public static Player me = null;

    public static void main(String[] args) {

        // MAIN THREAD
        String name = "/test2.txt";
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

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        String action;

        while (true) {
            try {
                action = inFromUser.readLine();
                outToServer.writeBytes(action + '\n');
                System.out.println("ACTION SENT: " + action);
            } catch (IOException e) {
                System.out.println("ACTION ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}