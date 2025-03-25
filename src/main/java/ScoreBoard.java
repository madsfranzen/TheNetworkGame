import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ScoreBoard extends VBox {
    private final VBox contentBox;

    public ScoreBoard(int width, int height) {
        super();
        
        // Initialize content box
        contentBox = new VBox(10); // 10px spacing between entries
        contentBox.setAlignment(Pos.TOP_LEFT);
        
        // Set pane size
        this.setPrefSize(width, height);
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        
        // Add content box to main container
        this.getChildren().add(contentBox);
        
        // Add margin around the content
        this.setPadding(new Insets(10, 10, 10, 10));
        
        // Force layout pass
        this.layout();
    }

    private Text createPlayerEntry(String playerName, int playerScore, String playerColor) {
        Text entry = new Text(playerName + " : " + playerScore);
        entry.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        entry.setFill(Color.web(playerColor)); // Use the player's color
        entry.setFocusTraversable(false);
        entry.setWrappingWidth(this.getPrefWidth() - 20); // Account for padding
        entry.setTextAlignment(TextAlignment.LEFT);
        return entry;
    }

    public void updateScoreBoard(JSONObject scoreBoard, HashMap<String, String> players) {
        // Wrap UI modifications in Platform.runLater
        javafx.application.Platform.runLater(() -> {
            // Clear existing content
            contentBox.getChildren().clear();
            
            JSONArray playersConnected = scoreBoard.getJSONArray("players");
            for (int i = 0; i < playersConnected.length(); i++) {
                JSONObject player = playersConnected.getJSONObject(i);
                String playerName = player.getString("playerName");
                int playerScore = player.getInt("playerScore");
                String playerColor = players.get(playerName);
                
                // Create and add colored entry
                Text entry = createPlayerEntry(playerName, playerScore, playerColor);
                contentBox.getChildren().add(entry);
            }
            
            // Force layout update
            this.layout();
        });
    }
}
