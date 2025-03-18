import javafx.scene.control.ScrollPane;

public class GameRenderer extends ScrollPane {
    public GameRenderer(int width, int height) {
        super();
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);

        setupScrollPane(width, height);
    }

    private void setupScrollPane(int width, int height) {
        setPannable(false); // Disable panning to prevent view movement when painting
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);

        // Don't use fitToWidth/Height as they can cause performance issues
        setFitToWidth(false);
        setFitToHeight(false);

        // Set viewport size - this prevents re-layout calculations
        setPrefViewportWidth(width);
        setPrefViewportHeight(height);

        // Use hardware acceleration for the scroll pane
        setCache(true);
        setCacheHint(javafx.scene.CacheHint.SPEED);

        // Optimize scrolling speed based on size
        double scrollFactor = 0.001 * Math.min(width, height);

        // Handle scroll events with throttling
        setOnScroll(event -> {
            event.consume();
            double deltaX = event.getDeltaX() * scrollFactor;
            double deltaY = event.getDeltaY() * scrollFactor;

            // Apply scroll changes
            setHvalue(getHvalue() - deltaX);
            setVvalue(getVvalue() - deltaY);
        });
    }
}
