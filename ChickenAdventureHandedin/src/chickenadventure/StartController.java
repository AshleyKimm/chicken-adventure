/*
 * ChickenAdventure - StartController.java
 * Purpose: displays the menu, changes the scenes when buttons are clicked
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StartController {
	@FXML
	Canvas startCanvas;

	Label changeScene;
	GraphicsContext gc;

	Scene gameScene;
	Stage secondaryStage;
	Image background = new Image("images/rainyBackground1.png");

	// A Button click handler for 'START' button
	// Runs the game, changes the scene to GameController.java , Game.fxml
	public void startButtonHandler(ActionEvent evt) throws IOException {

		try {
			// loads the fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Game.fxml"));
			BorderPane sceneParent = (BorderPane) loader.load();

			// Creates a new scene
			Scene scene = new Scene(sceneParent);

			GameController controller = loader.getController();

			// Get stage information
			Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
			stage.setScene(scene);
			controller.getScene(stage);
			controller.gameLoop();
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// Button click handler for 'HOW TO PLAY' button
	// Changes the scene to HowToController.java, HowToPlay.fxml
	public void howToPlayButtonHandler(ActionEvent evt) throws IOException {

		try {
			// Loads the FXML file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("HowToPlay.fxml"));
			BorderPane sceneParent = (BorderPane) loader.load();

			// creates a new scene
			Scene scene = new Scene(sceneParent);

			HowToController controller = loader.getController();

			// Gets stage information
			Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
			stage.setScene(scene);
			controller.getScene(stage);
			controller.gameLoop();
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void getScene(Stage primaryStage) {
		gameScene = primaryStage.getScene();
	}

	public void welcome() {
		gc = startCanvas.getGraphicsContext2D();

		DisplayText displayText = new DisplayText(gc, startCanvas);

		new AnimationTimer() {

			@Override
			public void handle(long currentNanoTime) {

				// draw background image
				gc.clearRect(startCanvas.getWidth(), startCanvas.getHeight(), 0, 0);

				gc.drawImage(background, 0, 0);

				// Displays the game title
				displayText.gameOpening();

			}

		}.start();

	}
}