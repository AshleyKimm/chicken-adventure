/*
 * ChickenAdventure - HowToController.java
 * Purpose: displays the instructions to play the game
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */

package chickenadventure;

import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HowToController {
	
	@FXML
	Canvas howToCanvas;

	GraphicsContext gc;

	Scene howToScene;
	

	Image background = new Image("images/rainyBackground1.png");
	
	public void getScene(Stage primaryStage) {
		howToScene = primaryStage.getScene();
	}
	
	// A button click handler for 'BACK TO MENU' button
	public void backToMenuButtonHandler (ActionEvent evt) throws IOException {
		
		// loads the FXML file
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Start.fxml"));
		BorderPane sceneParent = (BorderPane) loader.load();
		
		// Creates a new scene
		Scene scene = new Scene(sceneParent);

		StartController controller = loader.getController();

		// Get stage information
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		// Runs (loops) the welcome() method in StartController
		stage.setScene(scene);
		controller.getScene(stage);
		controller.welcome();
		stage.show();
		}
	
	public void gameLoop() {
		gc = howToCanvas.getGraphicsContext2D();

		DisplayText displayText = new DisplayText(gc, howToCanvas);

		gc.clearRect(howToCanvas.getWidth(), howToCanvas.getHeight(), 0, 0);

		new AnimationTimer() {

			@Override
			public void handle(long currentNanoTime) {

				// draw background image
				gc.clearRect(howToCanvas.getWidth(), howToCanvas.getHeight(), 0, 0);

				gc.drawImage(background, 0, 0);

				// Displays the instructions
				displayText.howToPlay();

	

			}

		}.start();

	}
}