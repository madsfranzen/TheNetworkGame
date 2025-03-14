import java.util.*;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class Gui extends Application {

	public static final int size = 30; 
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	

	private static Label[][] fields;
	private TextArea scoreList;
	


	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			
			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (Generel.board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+Generel.board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:    playerMoved(0,-1,"up");    break;
				case DOWN:  playerMoved(0,+1,"down");  break;
				case LEFT:  playerMoved(-1,0,"left");  break;
				case RIGHT: playerMoved(+1,0,"right"); break;
				case ESCAPE:System.exit(0); 
				default: break;
				}
			});
			
            // Putting default players on screen
			for (int i=0;i<GameLogic.players.size();i++) {
			  fields[GameLogic.players.get(i).getXpos()][GameLogic.players.get(i).getYpos()].setGraphic(new ImageView(hero_up));
			}
			scoreList.setText(getScoreList());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removePlayerOnScreen(pair oldpos) {
		Platform.runLater(() -> {
			fields[oldpos.getX()][oldpos.getY()].setGraphic(new ImageView(image_floor));
			});
	}
	
	public static void placePlayerOnScreen(pair newpos,String direction) {
		Platform.runLater(() -> {
			int newx = newpos.getX();
			int newy = newpos.getY();
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			};
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			};
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			};
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			};
			});
	}
	
	public static void movePlayerOnScreen(pair oldpos,pair newpos,String direction)
	{
		removePlayerOnScreen(oldpos);
		placePlayerOnScreen(newpos,direction);
	}
	

	
	public void updateScoreTable()
	{
		Platform.runLater(() -> {
			scoreList.setText(getScoreList());
			});
	}
	public void playerMoved(int delta_x, int delta_y, String direction) {
		GameLogic.updatePlayer(App.me,delta_x,delta_y,direction);
		updateScoreTable();
	}
	
	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : GameLogic.players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}



	
}

