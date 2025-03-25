import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ScoreBoard extends StackPane {
    private final Text scoreBoardText;

    public ScoreBoard(int width, int height) {
        super();
        
        // Initialize text node first
        scoreBoardText = new Text();
        
        // Set initial text to avoid null layout issues
        scoreBoardText.setText("Loading...");
        
        // Now set properties
        scoreBoardText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreBoardText.setFill(Color.WHITE);
        scoreBoardText.setStyle("-fx-background-color: #f0f0f0;");
        scoreBoardText.setFocusTraversable(false);
        scoreBoardText.setWrappingWidth(width - 20); // Set max width with 10px padding on each side
        scoreBoardText.setLineSpacing(10);
        scoreBoardText.setTextAlignment(TextAlignment.LEFT);
        
        // Force layout computation
        scoreBoardText.setManaged(true);
        scoreBoardText.setVisible(true);
        
        // Add to pane
        this.getChildren().add(scoreBoardText);
        
        // Align text to the top of the pane
        StackPane.setAlignment(scoreBoardText, Pos.TOP_LEFT);
        
        // Add margin around the text instead of padding
        StackPane.setMargin(scoreBoardText, new Insets(10, 10, 10, 10));
        
        // Set pane size
        this.setPrefSize(width, height);
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        
        // Force layout pass
        this.layout();
    }

    public void updateScoreBoard(JSONObject scoreBoard) {
        StringBuilder scoreBoardString = new StringBuilder();

        JSONArray players = scoreBoard.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject player = players.getJSONObject(i);
            String playerName = player.getString("playerName");
            int playerScore = player.getInt("playerScore");
            scoreBoardString.append(playerName);
            scoreBoardString.append(" : ");
            scoreBoardString.append(playerScore);
            scoreBoardString.append("\n");
        }
        
        // Update text on JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            scoreBoardText.setText(scoreBoardString.toString());
            // Force layout update
            this.layout();
        });
    }
}
