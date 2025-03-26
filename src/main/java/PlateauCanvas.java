import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class PlateauCanvas extends Canvas {

    private final GraphicsContext gc;
    private int WORLD_WIDTH;
    private int WORLD_HEIGHT;
    private int TILE_SIZE = 64;
    private int zIndex;
    private int layer;
    private String[][][] worldTileMap;

    // ALL TILESHEETS

    public PlateauCanvas(int width, int height, int zIndex) {
        super(width * 64, height * 64);
        this.WORLD_WIDTH = width;
        this.WORLD_HEIGHT = height;
        this.zIndex = zIndex;
        this.gc = this.getGraphicsContext2D();

    }

    public void initialize(int layer, String[][][] worldTileMap) {
        this.layer = layer;
        this.worldTileMap = worldTileMap;
        drawWorldMap();
    }

    private void drawWorldMap() {

        for (layer = 0; layer < worldTileMap[0][0].length; layer++) {
            for (int x = 0; x < WORLD_WIDTH; x++) {
                for (int y = 0; y < WORLD_HEIGHT; y++) {
                    String tile = worldTileMap[x][y][layer];
                    if (tile != null) {
                        switch (tile) {
                            case "PLATEAU" -> {
                                drawPlateau(x, y, true);
                            }
                            case "BRIDGE" -> {
                                drawBridge(x, y, true);
                            }
                            default -> {
                            }
                        }
                    }
                }
            }
        }
    }

    private static record TileVariant(String name, int x, int y) {
    }

    // ================================== WALL ====================================

    private static final TileVariant[] WALL_VARIANTS = {
            new TileVariant("LEFT", 0, 5),
            new TileVariant("CENTER", 1, 5),
            new TileVariant("RIGHT", 2, 5),
            new TileVariant("SOLO", 3, 5)
    };

    public void drawWall(int x, int y, boolean updateNeighbors, int layer) {
        TileVariant variant = wallDetermineVariant(x, y);

        gc.drawImage(SpriteLoader.getElevationTileset(),
                variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }
    }

    private TileVariant wallDetermineVariant(int x, int y) {

        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layer] != null
                && worldTileMap[x - 1][y][layer].equals("WALL");
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null
                && worldTileMap[x + 1][y][layer].equals("WALL");

        if (hasLeft && hasRight)
            return WALL_VARIANTS[1]; // CENTER
        else if (!hasLeft && hasRight)
            return WALL_VARIANTS[0]; // LEFT
        else if (hasLeft && !hasRight)
            return WALL_VARIANTS[2]; // RIGHT

        return WALL_VARIANTS[3]; // SOLO
    }

    public void updateNeighbors(int x, int y) {
        if (x > 0 && worldTileMap[x - 1][y][layer] != null && worldTileMap[x - 1][y][layer].equals("WALL"))
            drawWall(x - 1, y, false, layer);
        if (x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null
                && worldTileMap[x + 1][y][layer].equals("WALL"))
            drawWall(x + 1, y, false, layer);
        if (y > 0 && worldTileMap[x][y - 1][layer] != null && worldTileMap[x][y - 1][layer].equals("WALL"))
            drawWall(x, y - 1, false, layer);
        if (y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null
                && worldTileMap[x][y + 1][layer].equals("WALL"))
            drawWall(x, y + 1, false, layer);
    }

    // ================================== PLATEAU ==================================

    private static final TileVariant[] PLATEAU_VARIANTS = {
            new TileVariant("TOP_LEFT", 0, 0),
            new TileVariant("TOP_CENTER", 1, 0),
            new TileVariant("TOP_RIGHT", 2, 0),

            new TileVariant("CENTER_LEFT", 0, 1),
            new TileVariant("CENTER", 1, 1),
            new TileVariant("CENTER_RIGHT", 2, 1),

            new TileVariant("BOTTOM_LEFT", 0, 2),
            new TileVariant("BOTTOM_CENTER", 1, 2),
            new TileVariant("BOTTOM_RIGHT", 2, 2),

            new TileVariant("LONG_TOP", 3, 0),
            new TileVariant("LONG_CENTER", 3, 1),
            new TileVariant("LONG_BOTTOM", 3, 2),

            new TileVariant("WIDE_LEFT", 0, 4),
            new TileVariant("WIDE_CENTER", 1, 4),
            new TileVariant("WIDE_RIGHT", 2, 4),

            new TileVariant("SOLO", 3, 4),

    };

    public void drawPlateau(int x, int y, boolean updateNeighbors) {
        TileVariant variant = plateauDetermineVariant(x, y);

        gc.drawImage(SpriteLoader.getElevationTileset(),
                variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }

    }

    private TileVariant plateauDetermineVariant(int x, int y) {
        boolean hasTop = y > 0 && worldTileMap[x][y - 1][layer] != null;
        boolean hasBottom = y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null;
        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layer] != null;
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null;

        if (!hasTop && hasBottom && !hasLeft && hasRight)
            return PLATEAU_VARIANTS[0]; // TOP LEFT
        else if (!hasTop && hasBottom && hasLeft && hasRight)
            return PLATEAU_VARIANTS[1]; // TOP CENTER
        else if (!hasTop && hasBottom && hasLeft && !hasRight)
            return PLATEAU_VARIANTS[2]; // TOP RIGHT

        else if (hasTop && hasBottom && !hasLeft && hasRight)
            return PLATEAU_VARIANTS[3]; // CENTER LEFT
        else if (hasTop && hasBottom && hasLeft && hasRight)
            return PLATEAU_VARIANTS[4]; // CENTER
        else if (hasTop && hasBottom && hasLeft && !hasRight)
            return PLATEAU_VARIANTS[5]; // CENTER RIGHT

        else if (hasTop && !hasBottom && !hasLeft && hasRight)
            return PLATEAU_VARIANTS[6]; // BOTTOM LEFT
        else if (hasTop && !hasBottom && hasLeft && hasRight)
            return PLATEAU_VARIANTS[7]; // BOTTOM CENTER
        else if (hasTop && !hasBottom && hasLeft && !hasRight)
            return PLATEAU_VARIANTS[8]; // BOTTOM RIGHT

        else if (!hasTop && hasBottom && !hasLeft && !hasRight)
            return PLATEAU_VARIANTS[9]; // LONG_TOP
        else if (hasTop && hasBottom && !hasLeft && !hasRight)
            return PLATEAU_VARIANTS[10]; // LONG_CENTER
        else if (hasTop && !hasBottom && !hasLeft && !hasRight)
            return PLATEAU_VARIANTS[11]; // LONG_BOTTOM

        else if (!hasTop && !hasBottom && !hasLeft && hasRight)
            return PLATEAU_VARIANTS[12]; // WIDE_LEFT
        else if (!hasTop && !hasBottom && hasLeft && hasRight)
            return PLATEAU_VARIANTS[13]; // WIDE_CENTER
        else if (!hasTop && !hasBottom && hasLeft && !hasRight)
            return PLATEAU_VARIANTS[14]; // WIDE_RIGHT
        else if (!hasTop && !hasBottom && !hasLeft && !hasRight)
            return PLATEAU_VARIANTS[15]; // SOLO
        else
            return PLATEAU_VARIANTS[15]; // SOLO
    }

    // ================================== STAIRS ==================================

    private static final TileVariant[] STAIRS_VARIANTS = {
            new TileVariant("LEFT", 0, 7),
            new TileVariant("CENTER", 1, 7),
            new TileVariant("RIGHT", 2, 7),
            new TileVariant("SOLO", 3, 7)
    };

    public void drawStairs(int x, int y, boolean updateNeighbors) {
        TileVariant variant = stairsDetermineVariant(x, y);

        gc.drawImage(SpriteLoader.getElevationTileset(),
                variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }

    }

    private TileVariant stairsDetermineVariant(int x, int y) {
        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layer] != null;
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null;

        if (hasLeft && hasRight)
            return STAIRS_VARIANTS[1]; // CENTER
        else if (hasLeft)
            return STAIRS_VARIANTS[2]; // RIGHT
        else if (hasRight)
            return STAIRS_VARIANTS[0]; // LEFT

        return STAIRS_VARIANTS[3]; // SOLO
    }

    // =================================== GRASS ===================================

    protected TileVariant grassDetermineVariant(int x, int y) {

        boolean hasTop = y > 0 && worldTileMap[x][y - 1][layer] != null;
        boolean hasBottom = y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null;
        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layer] != null;
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null;

        if (hasTop && hasBottom && hasLeft && hasRight)
            return GRASS_VARIANTS[4]; // CENTER
        if (hasLeft && hasRight && !hasTop && !hasBottom)
            return GRASS_VARIANTS[10]; // HOR_CENTER
        if (hasTop && hasBottom && !hasLeft && !hasRight)
            return GRASS_VARIANTS[13]; // VER_CENTER
        if (hasRight && hasBottom && !hasLeft && !hasTop)
            return GRASS_VARIANTS[0]; // TOP_LEFT
        if (hasLeft && hasBottom && !hasRight && !hasTop)
            return GRASS_VARIANTS[2]; // TOP_RIGHT
        if (hasRight && hasTop && !hasLeft && !hasBottom)
            return GRASS_VARIANTS[6]; // BOTTOM_LEFT
        if (hasLeft && hasTop && !hasRight && !hasBottom)
            return GRASS_VARIANTS[8]; // BOTTOM_RIGHT
        if (hasBottom && !hasTop && hasLeft && hasRight)
            return GRASS_VARIANTS[1]; // TOP
        if (hasTop && !hasBottom && hasLeft && hasRight)
            return GRASS_VARIANTS[7]; // BOTTOM
        if (hasRight && !hasLeft && hasTop && hasBottom)
            return GRASS_VARIANTS[3]; // LEFT
        if (hasLeft && !hasRight && hasTop && hasBottom)
            return GRASS_VARIANTS[5]; // RIGHT
        if (hasRight && !hasLeft && !hasTop && !hasBottom)
            return GRASS_VARIANTS[9]; // HOR_LEFT
        if (hasLeft && !hasRight && !hasTop && !hasBottom)
            return GRASS_VARIANTS[11];// HOR_RIGHT
        if (!hasRight && !hasLeft && !hasTop && hasBottom)
            return GRASS_VARIANTS[12];// VER_TOP
        if (!hasRight && !hasLeft && !hasBottom && hasTop)
            return GRASS_VARIANTS[14];// VER_BOTTOM
        return GRASS_VARIANTS[15]; // SOLO
    }

    /**
     * Draws a terrain tile at the specified coordinates.
     */
    public void drawGrass(int x, int y, boolean updateNeighbors) {
        TileVariant variant = grassDetermineVariant(x, y);

        gc.drawImage(SpriteLoader.getGroundTileset(),
                variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }

    }

    private static final TileVariant[] GRASS_VARIANTS = {
            new TileVariant("TOP_LEFT", 0, 0),
            new TileVariant("TOP", 1, 0),
            new TileVariant("TOP_RIGHT", 2, 0),
            new TileVariant("LEFT", 0, 1),
            new TileVariant("CENTER", 1, 1),
            new TileVariant("RIGHT", 2, 1),
            new TileVariant("BOTTOM_LEFT", 0, 2),
            new TileVariant("BOTTOM", 1, 2),
            new TileVariant("BOTTOM_RIGHT", 2, 2),
            new TileVariant("HOR_LEFT", 0, 3),
            new TileVariant("HOR_CENTER", 1, 3),
            new TileVariant("HOR_RIGHT", 2, 3),
            new TileVariant("VER_TOP", 3, 0),
            new TileVariant("VER_CENTER", 3, 1),
            new TileVariant("VER_BOTTOM", 3, 2),
            new TileVariant("SOLO", 3, 3)
    };

    /**
     * Updates neighboring tiles after a terrain modification.
     */
    protected void grassUpdateNeighbors(int x, int y) {
        if (x > 0 && worldTileMap[x - 1][y][layer] != null)
            drawGrass(x - 1, y, false);
        if (x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null)
            drawGrass(x + 1, y, false);
        if (y > 0 && worldTileMap[x][y - 1][layer] != null)
            drawGrass(x, y - 1, false);
        if (y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null)
            drawGrass(x, y + 1, false);
    }

    // ================================== SAND ==================================

    protected TileVariant sandDetermineVariant(int x, int y) {

        boolean hasTop = y > 0 && worldTileMap[x][y - 1][layer] != null;
        boolean hasBottom = y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null;
        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layer] != null;
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null;

        if (hasTop && hasBottom && hasLeft && hasRight)
            return SAND_VARIANTS[4]; // CENTER
        if (hasLeft && hasRight && !hasTop && !hasBottom)
            return SAND_VARIANTS[10]; // HOR_CENTER
        if (hasTop && hasBottom && !hasLeft && !hasRight)
            return SAND_VARIANTS[13]; // VER_CENTER
        if (hasRight && hasBottom && !hasLeft && !hasTop)
            return SAND_VARIANTS[0]; // TOP_LEFT
        if (hasLeft && hasBottom && !hasRight && !hasTop)
            return SAND_VARIANTS[2]; // TOP_RIGHT
        if (hasRight && hasTop && !hasLeft && !hasBottom)
            return SAND_VARIANTS[6]; // BOTTOM_LEFT
        if (hasLeft && hasTop && !hasRight && !hasBottom)
            return SAND_VARIANTS[8]; // BOTTOM_RIGHT
        if (hasBottom && !hasTop && hasLeft && hasRight)
            return SAND_VARIANTS[1]; // TOP
        if (hasTop && !hasBottom && hasLeft && hasRight)
            return SAND_VARIANTS[7]; // BOTTOM
        if (hasRight && !hasLeft && hasTop && hasBottom)
            return SAND_VARIANTS[3]; // LEFT
        if (hasLeft && !hasRight && hasTop && hasBottom)
            return SAND_VARIANTS[5]; // RIGHT
        if (hasRight && !hasLeft && !hasTop && !hasBottom)
            return SAND_VARIANTS[9]; // HOR_LEFT
        if (hasLeft && !hasRight && !hasTop && !hasBottom)
            return SAND_VARIANTS[11];// HOR_RIGHT
        if (!hasRight && !hasLeft && !hasTop && hasBottom)
            return SAND_VARIANTS[12];// VER_TOP
        if (!hasRight && !hasLeft && !hasBottom && hasTop)
            return SAND_VARIANTS[14];// VER_BOTTOM
        return SAND_VARIANTS[15]; // SOLO
    }

    /**
     * Draws a terrain tile at the specified coordinates.
     */
    public void drawSand(int x, int y, boolean updateNeighbors) {
        TileVariant variant = sandDetermineVariant(x, y);

        gc.drawImage(SpriteLoader.getGroundTileset(),
                variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE,
                x * TILE_SIZE, y * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }

    }

    private static final TileVariant[] SAND_VARIANTS = {
            new TileVariant("TOP_LEFT", 5, 0),
            new TileVariant("TOP", 6, 0),
            new TileVariant("TOP_RIGHT", 7, 0),
            new TileVariant("LEFT", 5, 1),
            new TileVariant("CENTER", 6, 1),
            new TileVariant("RIGHT", 7, 1),
            new TileVariant("BOTTOM_LEFT", 5, 2),
            new TileVariant("BOTTOM", 6, 2),
            new TileVariant("BOTTOM_RIGHT", 7, 2),
            new TileVariant("HOR_LEFT", 5, 3),
            new TileVariant("HOR_CENTER", 6, 3),
            new TileVariant("HOR_RIGHT", 7, 3),
            new TileVariant("VER_TOP", 8, 0),
            new TileVariant("VER_CENTER", 8, 1),
            new TileVariant("VER_BOTTOM", 8, 2),
            new TileVariant("SOLO", 8, 3)
    };

    /**
     * Updates neighboring tiles after a terrain modification.
     */
    protected void sandUpdateNeighbors(int x, int y) {
        if (x > 0 && worldTileMap[x - 1][y][layer] != null)
            drawSand(x - 1, y, false);
        if (x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null)
            drawSand(x + 1, y, false);
        if (y > 0 && worldTileMap[x][y - 1][layer] != null)
            drawSand(x, y - 1, false);
        if (y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null)
            drawSand(x, y + 1, false);
    }

    // ================================== BRIDGE ==================================

    private static final TileVariant[] BRIDGE_VARIANTS = {
            new TileVariant("HOR_LEFT", 0, 0),
            new TileVariant("HOR_CENTER", 1, 0),
            new TileVariant("HOR_RIGHT", 2, 0),

            new TileVariant("VER_TOP", 0, 1),
            new TileVariant("VER_CENTER", 0, 2),
            new TileVariant("VER_BOTTOM", 0, 3),

            new TileVariant("BROKEN1", 1, 1),
            new TileVariant("BROKEN2", 1, 2),
            new TileVariant("BROKEN3", 2, 1),

    };

    public void drawBridge(int x, int y, boolean updateNeighbors) {

        TileVariant variant = bridgeDetermineVariant(x, y);

        if (variant.name().equals("HOR_CENTER")) {
            gc.drawImage(SpriteLoader.getBridgeTileset(),
                    variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE,
                    x * TILE_SIZE - 2, y * TILE_SIZE,
                    TILE_SIZE + 2, TILE_SIZE);
        } else if (variant.name().equals("VER_CENTER")) {
            gc.drawImage(SpriteLoader.getBridgeTileset(),
                    variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE,
                    x * TILE_SIZE, y * TILE_SIZE - 2,
                    TILE_SIZE, TILE_SIZE + 2);
        } else {
            gc.drawImage(SpriteLoader.getBridgeTileset(),
                    variant.x() * TILE_SIZE, variant.y() * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE,
                    x * TILE_SIZE, y * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE);
        }

        if (updateNeighbors) {
            updateNeighbors(x, y);
        }

    }

    private TileVariant bridgeDetermineVariant(int x, int y) {
        boolean hasTop = y > 0 && worldTileMap[x][y - 1][layer] != null;
        boolean hasBottom = y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null;
        boolean hasLeft = x > 0 && worldTileMap[x - 1][y][layer] != null;
        boolean hasRight = x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null;

        if (!hasTop && !hasBottom && !hasLeft && hasRight)
            return BRIDGE_VARIANTS[0]; // HOR_LEFT
        if (!hasTop && !hasBottom && hasLeft && hasRight)
            return BRIDGE_VARIANTS[1]; // HOR_CENTER
        if (!hasTop && !hasBottom && hasLeft && !hasRight)
            return BRIDGE_VARIANTS[2]; // HOR_RIGHT

        if (!hasTop && hasBottom && !hasLeft && !hasRight)
            return BRIDGE_VARIANTS[3]; // VER_TOP
        if (hasTop && hasBottom && !hasLeft && !hasRight)
            return BRIDGE_VARIANTS[4]; // VER_CENTER
        if (hasTop && !hasBottom && !hasLeft && !hasRight)
            return BRIDGE_VARIANTS[5]; // VER_BOTTOM
        // TODO: Remove this hardcoded value and replace with random
        return BRIDGE_VARIANTS[1];
        // return BRIDGE_VARIANTS[(int) (Math.random() * 3) + 6]; // Returns BROKEN1,
        // BROKEN2
        // or BROKEN3
    }

    public void bridgeUpdateNeighbors(int x, int y) {
        if (x > 0 && worldTileMap[x - 1][y][layer] != null) {
            drawBridge(x - 1, y, false);
        }
        if (x < worldTileMap.length - 1 && worldTileMap[x + 1][y][layer] != null) {
            drawBridge(x + 1, y, false);
        }
        if (y > 0 && worldTileMap[x][y - 1][layer] != null) {
            drawBridge(x, y - 1, false);
        }
        if (y < worldTileMap[0].length - 1 && worldTileMap[x][y + 1][layer] != null) {
            drawBridge(x, y + 1, false);
        }
    }

    // ================================== BRIDGE SHADOW ==================================

    public void drawBridgeShadow(int x, int y) {
        gc.drawImage(SpriteLoader.getBridgeTileset(), 2 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, x * TILE_SIZE,
                y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // GRASS FILL + SAND FILL

    public void drawGrassFill(int x, int y) {
        gc.drawImage(SpriteLoader.getGroundTileset(), 4 * TILE_SIZE, 0 * TILE_SIZE, TILE_SIZE, TILE_SIZE, x * TILE_SIZE,
                y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    public void drawSandFill(int x, int y) {
        gc.drawImage(SpriteLoader.getGroundTileset(), 9 * TILE_SIZE, 0 * TILE_SIZE, TILE_SIZE, TILE_SIZE, x * TILE_SIZE,
                y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // ================================ SHADOWS =================================

    public void drawShadow(int x, int y) {
        gc.drawImage(SpriteLoader.getShadowsImage(),
                x * TILE_SIZE - TILE_SIZE,
                y * TILE_SIZE - TILE_SIZE,
                TILE_SIZE * 3,
                TILE_SIZE * 3);
    }
}
