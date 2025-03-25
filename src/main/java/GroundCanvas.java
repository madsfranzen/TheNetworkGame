import java.io.File;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GroundCanvas extends Canvas {

    private final GraphicsContext gc;
    private int WORLD_WIDTH;
    private int WORLD_HEIGHT;
    private int TILE_SIZE = 64;
    private int layerIndex;
    private String[][][] worldTileMap;

    // ALL TILESHEEETS
    private final Image waterTile = SpriteLoader.getWaterTile();
    private final Image elevationTileSet = SpriteLoader.getElevationTileset();

    public GroundCanvas(int width, int height) {
        super(width * 64, height * 64);
        this.WORLD_WIDTH = width;
        this.WORLD_HEIGHT = height;
        this.gc = this.getGraphicsContext2D();
    }

    public void initialize(int layerIndex, String[][][] worldTileMap) {
        this.layerIndex = layerIndex;
        this.worldTileMap = worldTileMap;
        drawWorldMap();
    }

    private void drawWorldMap() {
        for (int layer = 0; layer < worldTileMap[0][0].length; layer++) {
            for (int x = 0; x < WORLD_WIDTH; x++) {
                for (int y = 0; y < WORLD_HEIGHT; y++) {
                    String tile = worldTileMap[x][y][layer];
                    if (tile != null) {
                        switch (tile) {
                            case "GRASS" -> {
                                // drawGrass(x, y, true);
                            }
                            case "WATER" -> {
                                // drawWater(x, y);
                            }
                            case "FOAM" -> {
                                // drawFoam(x, y);
                            }
                            case "SAND" -> {
                                // drawSand(x, y, true);
                            }
                            case "ROCKS1" -> {
                                // drawRocks(x, y, 0);
                            }
                            case "ROCKS2" -> {
                                // drawRocks(x, y, 1);
                            }
                            case "ROCKS3" -> {
                                // drawRocks(x, y, 2);
                            }
                            case "ROCKS4" -> {
                                // drawRocks(x, y, 3);
                            }
                            case "SHADOW" -> {
                                // drawShadow(x, y);
                            }
                            case "WALL" -> {
                                drawWall(x, y, true);
                            }
                            case "PLATEAU" -> {
                                // drawPlateau(x, y, true);
                            }
                            case "STAIRS" -> {
                                // drawStairs(x, y, true);
                            }
                            case "BRIDGE" -> {
                                // drawBridge(x, y, true);
                            }
                            case "BRIDGESHADOW" -> {
                                // drawBridgeShadow(x, y);
                            }
                            case "GRASSFILL" -> {
                                // drawGrassFill(x, y);
                            }
                            case "SANDFILL" -> {
                                // drawSandFill(x, y);
                            }
                            default -> {
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================== WATER
    // =========================================

    public void drawWater(int x, int y) {
        gc.drawImage(waterTile, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // ================================== WALL
    // =========================================

    private static record TileVariant(String name, int x, int y) {
    }

    private static final TileVariant[] WALL_VARIANTS = {
            new TileVariant("LEFT", 0, 5),
            new TileVariant("CENTER", 1, 5),
            new TileVariant("RIGHT", 2, 5),
            new TileVariant("SOLO", 3, 5)
    };

    public void drawWall(int x, int y, boolean updateNeighbors) {
        TileVariant variant = wallDetermineVariant(x, y);
        System.out.println("Drawing wall at: " + x + ", " + y);
        System.out.println("Variant X: " + variant.x() + " Variant Y: " + variant.y());
        System.out.println("Variant: " + variant.name());

        gc.drawImage(elevationTileSet,
                variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }
    }

    private TileVariant wallDetermineVariant(int x, int y) {
        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layerIndex] == "WALL";
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layerIndex] == "WALL";

        if (hasLeft && hasRight)
            return WALL_VARIANTS[1]; // CENTER
        else if (!hasLeft && hasRight)
            return WALL_VARIANTS[0]; // LEFT
        else if (hasLeft && !hasRight)
            return WALL_VARIANTS[2]; // RIGHT

        return WALL_VARIANTS[3]; // SOLO
    }

    public void updateNeighbors(int x, int y) {
        if (x > 0 && worldTileMap[x - 1][y][layerIndex] == "WALL")
            drawWall(x - 1, y, false);
        if (x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layerIndex] == "WALL")
            drawWall(x + 1, y, false);
        if (y > 0 && worldTileMap[x][y - 1][layerIndex] == "WALL")
            drawWall(x, y - 1, false);
        if (y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layerIndex] == "WALL")
            drawWall(x, y + 1, false);
    }

    // ================================== PLATEAU
    // =========================================

}
