import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
public class GameRenderer extends ScrollPane {

    private final StackPane canvasContainer;
    private final int WORLD_WIDTH = 32;
    private final int WORLD_HEIGHT = 32;
    private final int TILE_SIZE = 64;

    private String[][][] worldTileMap;

    private final Canvas gridCanvas = new Canvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE);

    private final WaterCanvas waterCanvas = new WaterCanvas(WORLD_WIDTH, WORLD_HEIGHT);
    private final GroundCanvas groundCanvas0 = new GroundCanvas(WORLD_WIDTH, WORLD_HEIGHT, 0);
    private final GroundCanvas groundCanvas1 = new GroundCanvas(WORLD_WIDTH, WORLD_HEIGHT, 1);
    private final GroundCanvas groundCanvas2 = new GroundCanvas(WORLD_WIDTH, WORLD_HEIGHT, 2);
    private final PlateauCanvas plateauCanvas0 = new PlateauCanvas(WORLD_WIDTH, WORLD_HEIGHT, 1);
    private final PlateauCanvas plateauCanvas1 = new PlateauCanvas(WORLD_WIDTH, WORLD_HEIGHT, 2);
    private final PlayerCanvas playerCanvas0 = new PlayerCanvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE,
            WORLD_WIDTH,
            WORLD_HEIGHT, TILE_SIZE);
    private final PlayerCanvas playerCanvas1 = new PlayerCanvas(WORLD_WIDTH * TILE_SIZE, WORLD_HEIGHT * TILE_SIZE,
            WORLD_WIDTH,
            WORLD_HEIGHT, TILE_SIZE);

    public GameRenderer(int width, int height) {
        super();
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);

        // Setup canvas container
        canvasContainer = new StackPane();

        canvasContainer.setStyle("-fx-background-color: rgb(99, 165, 164);"); // Using CSS for background color 😎

        gridCanvas.setMouseTransparent(false);
        canvasContainer.getChildren().add(gridCanvas);

        waterCanvas.setMouseTransparent(true);
        groundCanvas0.setMouseTransparent(true);
        groundCanvas1.setMouseTransparent(true);
        plateauCanvas0.setMouseTransparent(true);
        plateauCanvas1.setMouseTransparent(true);
        playerCanvas0.setMouseTransparent(true);
        playerCanvas1.setMouseTransparent(true);
        groundCanvas2.setMouseTransparent(true);

        canvasContainer.getChildren().add(waterCanvas);
        canvasContainer.getChildren().add(groundCanvas0);
        canvasContainer.getChildren().add(playerCanvas0);
        canvasContainer.getChildren().add(plateauCanvas0);
        // canvasContainer.getChildren().add(groundCanvas1);
        // canvasContainer.getChildren().add(playerCanvas1);
        // canvasContainer.getChildren().add(plateauCanvas1);
        // canvasContainer.getChildren().add(groundCanvas2);
        setupScrollPane(width, height);

        // Add the canvas container to the scroll pane
        this.setContent(canvasContainer);

            System.out.println("Loading world map");
        loadWorldMap();

    }

    private void loadWorldMap() {
        try {
            // Read the JSON file using ClassLoader
            var classLoader = getClass().getClassLoader();
            var resource = classLoader.getResourceAsStream("World.json");
            if (resource == null) {
                throw new RuntimeException("Could not find World.json in resources");
            }
            
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(resource))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }
            
            JSONArray worldMapFile = new JSONArray(content.toString());

            // Get the dimensions from the JSON array
            int worldWidth = worldMapFile.length();
            int worldHeight = worldMapFile.getJSONArray(0).length();
            int layerCount = worldMapFile.getJSONArray(0).getJSONArray(0).length();
            
            // Create the world tile map with the correct dimensions
            worldTileMap = new String[worldWidth][worldHeight][layerCount];

            for (int x = 0; x < worldWidth; x++) {
                for (int y = 0; y < worldHeight; y++) {
                    for (int layer = 0; layer < layerCount; layer++) {
                        var value = worldMapFile.getJSONArray(x).getJSONArray(y).get(layer);
                        worldTileMap[x][y][layer] = value == JSONObject.NULL ? null : value.toString();
                    }
                }
            }

            this.waterCanvas.initialize(0, worldTileMap);
            this.groundCanvas0.initialize(0, worldTileMap);

            this.plateauCanvas0.initialize(1, worldTileMap);
            this.groundCanvas1.initialize(1, worldTileMap);
            
            this.plateauCanvas1.initialize(1, worldTileMap);
            this.groundCanvas2.initialize(2, worldTileMap);


        } catch (Exception e) {
            System.err.println("❌ Error loading world map: " + e.getMessage());
            e.printStackTrace();
        }
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
            // event.consume();
        });

        // Add scroll event filter to catch all scroll events at the capture phase
        addEventFilter(javafx.scene.input.ScrollEvent.ANY, event -> {
            // event.consume();
        });

        // Disable mouse drag scrolling
        setPannable(true);

        // Lock scroll values
        hvalueProperty().addListener((obs, oldVal, newVal) -> {
            // if (newVal.doubleValue() != 0.5) {
            //     setHvalue(0.5);
            // }
        });

        vvalueProperty().addListener((obs, oldVal, newVal) -> {
            // if (newVal.doubleValue() != 0.5) {
            //     setVvalue(0.5);
            // }
        });

        // Set initial scroll position
        setHvalue(0);
        setVvalue(0);

        // Disable focus traversal to prevent keyboard navigation
        setFocusTraversable(false);
    }

    public PlayerCanvas getPlayerCanvas0() {
        return playerCanvas0;
    }

    public PlayerCanvas getPlayerCanvas1() {
        return playerCanvas1;
    }
}
