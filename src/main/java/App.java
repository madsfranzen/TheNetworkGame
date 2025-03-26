import javafx.application.Application;

public class App {

    static String username;

    public static void main(String[] args) {

        // Opening connection window
        Application.launch(StartMenu.class);
    }

    public static void setUsername(String un) {
        username = un;
    }
}