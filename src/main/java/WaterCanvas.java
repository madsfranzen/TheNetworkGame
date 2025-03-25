import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class WaterCanvas extends Canvas {

    private final GraphicsContext gc;
    private int WORLD_WIDTH;
    private int WORLD_HEIGHT;
    private int TILE_SIZE = 64;
    private int layerIndex;
    private String[][][] worldTileMap;

    // ALL TILESHEEETS

    public WaterCanvas(int width, int height) {
        super(width * 64, height * 64);
        this.WORLD_WIDTH = width;
        this.WORLD_HEIGHT = height;
        this.gc = this.getGraphicsContext2D();
        this.foamAnimator = createFoamAnimator();
        this.activeRocks = Collections.synchronizedSet(new HashSet<>());
        this.activeFoams = Collections.synchronizedSet(new HashSet<>());
        this.foamSpritesheet = SpriteLoader.getFoamSpritesheet();
        this.foamAnimator.start();
        this.rocksTileMap = new RocksTile[WORLD_WIDTH][WORLD_HEIGHT];
    }

    public void initialize(int layerIndex, String[][][] worldTileMap) {
        this.layerIndex = layerIndex;
        this.worldTileMap = worldTileMap;
        drawWorldMap();
    }

    private void drawWorldMap() {

        System.out.println("Width: " + this.getWidth() + " Height: " + this.getHeight());

        for (int layer = 0; layer < worldTileMap[0][0].length; layer++) {
            for (int x = 0; x < WORLD_WIDTH; x++) {
                for (int y = 0; y < WORLD_HEIGHT; y++) {
                    String tile = worldTileMap[x][y][layer];
                    if (tile != null) {
                        switch (tile) {
                            case "FOAM" -> {
                                drawFoam(x, y);
                            }
                            case "ROCKS1" -> {
                                drawRocks(x, y, 0);
                            }
                            case "ROCKS2" -> {
                                drawRocks(x, y, 1);
                            }
                            case "ROCKS3" -> {
                                drawRocks(x, y, 2);
                            }
                            case "ROCKS4" -> {
                                drawRocks(x, y, 3);
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

    // ================= FOAM ==============

    private static final long FOAM_FRAME_DURATION_NS = 100_000_000; // 100ms in nanoseconds
    private static final int FOAM_FRAME_COUNT = 8;

    private final Image foamSpritesheet;
    private final Set<FoamPosition> activeFoams;
    private final AnimationTimer foamAnimator;
    private static final int FRAME_WIDTH = 64 * 3;
    private static final int FRAME_HEIGHT = 64 * 3;

    private int foamCurrentFrame;
    private long foamLastFrameTime;

    private static record FoamPosition(int centerX, int centerY) {
    }

    private AnimationTimer createFoamAnimator() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - foamLastFrameTime >= FOAM_FRAME_DURATION_NS) {
                    updateAnimation();
                    foamLastFrameTime = now;
                }
            }
        };
    }

    private void updateAnimation() {
        foamCurrentFrame = (foamCurrentFrame + 1) % FOAM_FRAME_COUNT;
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Use synchronized set to avoid concurrent modification
        synchronized (activeFoams) {
            activeFoams.forEach(pos -> draw3x3Foam(pos.centerX(), pos.centerY()));
        }

        synchronized (activeRocks) {
            activeRocks.forEach(pos -> draw2x2Rocks(pos.centerX(), pos.centerY()));
        }

    }

    /**
     * Adds a new foam animation at the specified center position.
     *
     * @param centerX center X coordinate in tile units
     * @param centerY center Y coordinate in tile units
     */
    public void drawFoam(int centerX, int centerY) {
        activeFoams.add(new FoamPosition(centerX, centerY));
    }

    private void draw3x3Foam(int centerX, int centerY) {
        getGraphicsContext2D().drawImage(
                foamSpritesheet,
                foamCurrentFrame * FRAME_WIDTH, 0,
                FRAME_WIDTH, FRAME_HEIGHT,
                (centerX - 1) * TILE_SIZE,
                (centerY - 1) * TILE_SIZE,
                FRAME_WIDTH, FRAME_HEIGHT);
    }

    /**
     * Stops the animation and releases resources.
     * Should be called when the canvas is no longer needed.
     */
    public void dispose() {
        foamAnimator.stop();
        activeFoams.clear();
    }

    // ============= ROCKS ===============

    private final Set<RocksPosition> activeRocks;
    private static final Image[] ROCKS_IMAGES = SpriteLoader.getAllRocksImages();
    private final RocksTile[][] rocksTileMap;

    private static record RocksPosition(int centerX, int centerY) {
    }

    public static class RocksTile {
        final int rockType;

        RocksTile(int rockType) {
            this.rockType = rockType;
        }

        public int getRockType() {
            return rockType;
        }
    }

    /**
     * Adds a new foam animation at the specified center position.
     *
     * @param x center X coordinate in tile units
     * @param y center Y coordinate in tile units
     */
    public void drawRocks(int x, int y, int rockType) {
        rocksTileMap[x][y] = new RocksTile(rockType);
        activeRocks.add(new RocksPosition(x, y));
    }

    // This centers the image on the tile
    private void draw2x2Rocks(int x, int y) {
        getGraphicsContext2D().drawImage(
                ROCKS_IMAGES[rocksTileMap[x][y].rockType],
                foamCurrentFrame * TILE_SIZE * 2, 0,
                TILE_SIZE * 2, TILE_SIZE * 2,
                (x - 1) * TILE_SIZE + TILE_SIZE / 2,
                (y - 1) * TILE_SIZE + TILE_SIZE / 2,
                TILE_SIZE * 2, TILE_SIZE * 2);
    }
}
