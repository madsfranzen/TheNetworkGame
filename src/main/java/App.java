import javafx.application.Application;

public class App {

    static String username;

    public static void main(String[] args) {
        // Hardware acceleration settings
        System.setProperty("prism.order", "d3d,metal,es2,sw"); // Try hardware acceleration first
        System.setProperty("prism.forceGPU", "true"); // Force GPU usage
        System.setProperty("prism.maxvram", "4g"); // Increased texture memory

        // Performance optimizations
        System.setProperty("prism.dirtyopts", "false"); // Disable dirty region optimizations
        System.setProperty("quantum.multithreaded", "true"); // Enable multithreaded rendering

        // Opening connection window
        Application.launch(StartMenu.class);
    }

    public static void setUsername(String un) {
        username = un;
    }
}