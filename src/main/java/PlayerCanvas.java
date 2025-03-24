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

    public void drawPlayer(int x, int y, String faction) {
        System.out.println("Drawing player at (" + x + ", " + y + ")");
        activePlayerPositions.add(new PlayerPosition(x, y, faction));
        this.animator.start();
    }

    public void removePlayer(int x, int y) {
        System.out.println("Removing player at (" + x + ", " + y + ")");
        activePlayerPositions.removeIf(pos -> pos.centerX() == x && pos.centerY() == y);
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
