import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class NameOverlay extends Canvas {

    private final int TILE_SIZE;

    public NameOverlay(int width, int height) {
        super(width, height);
        this.TILE_SIZE = 64;
    }

    private GraphicsContext gcPlayer = getGraphicsContext2D();

    public void drawName(int x, int y, String playerName) {
        gcPlayer.setFill(Color.WHITE);
        gcPlayer.setFont(Font.font("Arial", 12));
        gcPlayer.setStroke(Color.BLACK);
        gcPlayer.setTextAlign(TextAlignment.CENTER);
        gcPlayer.fillText(playerName, x * TILE_SIZE + TILE_SIZE / 2, y * TILE_SIZE - 50);
    }

    public void removeName(int x, int y) {
        // Clear area centered on the text position
        // Width: 2 * TILE_SIZE to cover potential text width
        // Height: 30 pixels to cover text height plus padding
        // Start position: center of tile (x * TILE_SIZE + TILE_SIZE/2) minus half the clearing width
        gcPlayer.clearRect(x * TILE_SIZE + TILE_SIZE/2 - TILE_SIZE, y * TILE_SIZE - 60, 2 * TILE_SIZE, 30);
    }

}   
