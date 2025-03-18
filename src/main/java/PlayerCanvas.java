import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PlayerCanvas extends Canvas {

    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;
    private final int TILE_SIZE;

    private final PlayerTile[][] playerTiles;
    private final Set<PlayerPosition> activePlayerPositions;

    private GraphicsContext gcPlayer = getGraphicsContext2D();
    private static final int FRAME_COUNT = 6;
    private static final long FRAME_DURATION_NS = 100_000_000; // 100ms in nanoseconds
    private final AnimationTimer animator;

    private int currentFrame;
    private long lastFrameTime;

    /**
     * @param width       The width of the canvas (pixels)
     * @param height      The height of the canvas (pixels)
     * @param worldWidth  The width of the world (tiles)
     * @param worldHeight The height of the world (tiles)
     * @param tileSize    The size of the tiles in the world (pixels)
     */
    public PlayerCanvas(int width, int height, int worldWidth, int worldHeight, int tileSize) {
        super(width, height);
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.TILE_SIZE = tileSize;
        this.activePlayerPositions = Collections.synchronizedSet(new HashSet<>());
        this.animator = createAnimator();
        this.animator.start();
        playerTiles = new PlayerTile[WORLD_WIDTH][WORLD_HEIGHT];

    }

    private AnimationTimer createAnimator() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrameTime >= FRAME_DURATION_NS) {
                    updateAnimation();
                    lastFrameTime = now;
                }
            }
        };
    }

    private void updateAnimation() {
        currentFrame = (currentFrame + 1) % FRAME_COUNT;

        gcPlayer.clearRect(0, 0, getWidth(), getHeight());

        String sprite = "PLAYER_IDLE" + (currentFrame + 1);
        int[] sourceXY = TileVariant.getVariant(sprite);

        // Use synchronized set to avoid concurrent modification
        synchronized (activePlayerPositions) {
            for (PlayerPosition pos : activePlayerPositions) {
                drawPlayerTile(gcPlayer, SpriteLoader.getSprite(sprite + "_" + pos.faction()), sourceXY, pos.centerX(),
                        pos.centerY());
            }
        }
    }

    public void drawPlayerTile(GraphicsContext gc, Image sprite, int[] sourceXY, int x, int y) {
        gc.drawImage(sprite, sourceXY[0] * TILE_SIZE,
                sourceXY[1] * TILE_SIZE,
                TILE_SIZE * 3,
                TILE_SIZE * 3,
                x * TILE_SIZE - TILE_SIZE,
                y * TILE_SIZE - TILE_SIZE,
                TILE_SIZE * 3,
                TILE_SIZE * 3);
    }

    // TODO: Not for use in production, this is just for generating placeholder map
    // for testing
    public void drawPlayer() {
        playerTiles[12][12] = new PlayerTile();
        playerTiles[5][5] = new PlayerTile();
        activePlayerPositions.add(new PlayerPosition(12, 12, "blue"));
        activePlayerPositions.add(new PlayerPosition(5, 5, "red"));
        activePlayerPositions.add(new PlayerPosition(8, 20, "yellow"));
        activePlayerPositions.add(new PlayerPosition(15, 3, "purple"));
    }

    public class PlayerTile {
    }

    public record PlayerPosition(int centerX, int centerY, String faction) {
    }

    /**
     * Stops the animation and releases resources.
     * Should be called when the canvas is no longer needed.
     */
    public void dispose() {
        animator.stop();
        activePlayerPositions.clear();
    }

}
