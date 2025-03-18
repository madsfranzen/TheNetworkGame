import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameRenderer extends ScrollPane {

    private final StackPane canvasContainer;
    private final int WORLD_WIDTH = 32;
    private final int WORLD_HEIGHT = 32;
    private final int TILE_SIZE = 64;

    private final Canvas gridCanvas = new Canvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE);
    private final GraphicsContext gc = gridCanvas.getGraphicsContext2D();

    private final Canvas waterCanvas = new Canvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE);
    private final GraphicsContext gcWater = waterCanvas.getGraphicsContext2D();

    private final Canvas groundCanvas = new Canvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE);
    private final GraphicsContext gcGround = groundCanvas.getGraphicsContext2D();

    private final PlayerCanvas playerCanvas = new PlayerCanvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE,
            WORLD_WIDTH,
            WORLD_HEIGHT, TILE_SIZE);

    public GameRenderer(int width, int height) {
        super();
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);

        // Setup canvas container
        canvasContainer = new StackPane();

        drawGrid();
        drawGround();

        gridCanvas.setMouseTransparent(false);
        canvasContainer.getChildren().add(gridCanvas);

        waterCanvas.setMouseTransparent(true);
        groundCanvas.setMouseTransparent(true);
        playerCanvas.setMouseTransparent(true);

        canvasContainer.getChildren().add(waterCanvas);
        canvasContainer.getChildren().add(groundCanvas);
        canvasContainer.getChildren().add(playerCanvas);

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

        // Disable arrow key scrolling
        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT: 
                case RIGHT:
                    event.consume();
                    break;
                default:
                    break;
            }
        });
        
        // Additional key event handler to ensure arrow keys are blocked
        addEventFilter(javafx.scene.input.KeyEvent.ANY, event -> {
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    event.consume();
                    break;
                default:
                    break;
            }
        });

        // Completely disable scroll events
        setOnScroll(event -> {
            event.consume();
        });
        
        // Add scroll event filter to catch all scroll events at the capture phase
        addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
            event.consume();
        });
        
        // Disable mouse drag scrolling
        setPannable(false);
        
        // Lock scroll values
        hvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() != 0.5) {
                setHvalue(0.5);
            }
        });
        
        vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() != 0.5) {
                setVvalue(0.5);
            }
        });
        
        // Set initial scroll position
        setHvalue(0.5);
        setVvalue(0.5);
        
        // Disable focus traversal to prevent keyboard navigation
        setFocusTraversable(false);
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

    // TODO: Not for use in production, this is just for generating placeholder map
    // for testing
    public void drawGround() {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                drawTile(x, y, "GRASS_CENTER");
            }
        }
    }

    public void drawTile(int x, int y, String sprite) {
        String spriteName = sprite.toUpperCase().split("_")[0];
        Image spriteImage = SpriteLoader.getSprite(sprite);
        int[] sourceXY = TileVariant.getVariant(sprite);

        switch (spriteName) {
            case "GRASS" -> {
                drawImage1x1(gcGround, spriteImage, sourceXY, x, y);
            }
            case "WATER" -> {
                drawImage1x1(gcWater, spriteImage, sourceXY, x, y);
            }
            case "SAND" -> {
                drawImage1x1(gcGround, spriteImage, sourceXY, x, y);
            }
            default -> {
            }
        }
    }

    // COPY PASTE THIS TO ANY INDIVIDUAL CANVASES THAT MIGHT NEED IT
    public void drawImage1x1(GraphicsContext GC, Image spriteImage, int[] sourceXY, int x, int y) {
        GC.drawImage(
                spriteImage,
                sourceXY[0] * TILE_SIZE,
                sourceXY[1] * TILE_SIZE,
                TILE_SIZE,
                TILE_SIZE,
                x * TILE_SIZE,
                y * TILE_SIZE,
                TILE_SIZE,
                TILE_SIZE);
    }

    // COPY PASTE THIS TO ANY INDIVIDUAL CANVASES THAT MIGHT NEED IT
    public void drawImage3x3(GraphicsContext GC, Image spriteImage, int[] sourceXY, int x, int y) {
        GC.drawImage(
                spriteImage,
                sourceXY[0] * TILE_SIZE,
                sourceXY[1] * TILE_SIZE,
                TILE_SIZE * 3,
                TILE_SIZE * 3,
                x * TILE_SIZE - TILE_SIZE,
                y * TILE_SIZE - TILE_SIZE,
                TILE_SIZE * 3,
                TILE_SIZE * 3);
    }

    public PlayerCanvas getPlayerCanvas() {
        return playerCanvas;
    }
}
