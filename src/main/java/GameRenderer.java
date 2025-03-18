import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameRenderer extends ScrollPane {

    private StackPane canvasContainer;
    private Canvas groundCanvas;
    private final int WORLD_WIDTH = 32;
    private final int WORLD_HEIGHT = 32;
    private final int TILE_SIZE = 64;
    private Canvas gridCanvas = new Canvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE);
    private GraphicsContext gc = gridCanvas.getGraphicsContext2D();

    public GameRenderer(int width, int height) {
        super();
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);

        // Setup canvas container
        canvasContainer = new StackPane();

        groundCanvas = new Canvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE);

        drawGrid();
        drawGround();

        gridCanvas.setMouseTransparent(false);
        canvasContainer.getChildren().add(gridCanvas);

        groundCanvas.setMouseTransparent(true);
        canvasContainer.getChildren().add(groundCanvas);

        setupScrollPane(width, height);

        // Add the canvas container to the scroll pane
        this.setContent(canvasContainer);
    }

    private void setupScrollPane(int width, int height) {
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

            // Use the ScrollPane's built-in scrolling mechanism
            setHvalue(getHvalue() - event.getDeltaX() * scrollFactor);
            setVvalue(getVvalue() - event.getDeltaY() * scrollFactor);
        });
    }

    public void drawGrid() {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);

        // Draw vertical lines
        for (int x = 0; x <= WORLD_WIDTH; x++) {
            gc.strokeLine(x * TILE_SIZE, 0, x * TILE_SIZE, gridCanvas.getHeight());
        }

        // Draw horizontal lines
        for (int y = 0; y <= WORLD_HEIGHT; y++) {
            gc.strokeLine(0, y * TILE_SIZE, gridCanvas.getWidth(), y * TILE_SIZE);
        }
    }

    public void drawGround() {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                drawTile(x, y, "GRASS_SOLO");
            }
        }
    }

    public void drawTile(int x, int y, String sprite) {
        Image spriteImage = SpriteLoader.getSprite(sprite);

        int[] sourceXY = TileVariant.getVariant(sprite);
        System.out.println("SOURCE XY: " + sourceXY[0] + ", " + sourceXY[1]);
        gc.drawImage(
                spriteImage,
                sourceXY[0] * TILE_SIZE, sourceXY[1] * TILE_SIZE, TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
}
