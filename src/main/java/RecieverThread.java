import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecieverThread extends Thread {
    private BufferedReader in;

    public RecieverThread(Socket socket) {
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void run() {
        String messageFromServer;
        System.out.println("RecieverThread started");
        try {
            while ((messageFromServer = in.readLine()) != null) {
                System.out.println(messageFromServer);
            }
        } catch (IOException e) {
            System.out.println("RecieverThread error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
