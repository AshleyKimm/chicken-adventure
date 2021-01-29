/*
 * ChickenAdventure - GameController.java
 * Purpose: to call methods and variable from other
 * classes to run the game
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */
package chickenadventure;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameController {
	@FXML
	Canvas gameCanvas;

	GraphicsContext gc;

	Scene gameScene;

	// Boolean variables to check for collision
	boolean collidedRain = false;
	boolean collidedBean = false;
	boolean collidedSeed = false;
	static int newScore = 0;
	static int oldScore = 0;

	Image background = new Image("images/rainyBackground1.png");

	// A Button Click Handler for 'MENU' button on game scene
	public void menuButtonHandler(ActionEvent evt) throws IOException {
		
		// loads the fxml file
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Start.fxml"));
		BorderPane sceneParent = (BorderPane) loader.load();
		
		// creates a new scene
		Scene scene = new Scene(sceneParent);

		StartController controller = loader.getController();

		// Get stage information
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
		stage.setScene(scene);
		
		// Runs (loops) the welcome() method in StartController
		controller.getScene(stage);
		controller.welcome();
		stage.show();
	}

	// Resets all the variables and restart the game when 'RESTART' button is clicked
	public void restartButtonClickHandler(ActionEvent evt) throws IOException {
		Chicken.lives = 3;
		Bean.score = 0;
		Chicken.speed = 3;
		Rain.speed = 3;
		Rain.numRain = 5;
		// Runs the gameLoop to start the game
		gameLoop();
	}

	public void getScene(Stage primaryStage) {
		gameScene = primaryStage.getScene();
	}

	public void gameLoop() {
		
		// Resets the variables so that whenever gameLoop() is exited,
		// the game automatically restarts
		Chicken.lives = 3;
		Bean.score = 0;
		Chicken.speed = 3;
		Rain.speed = 3;

		gc = gameCanvas.getGraphicsContext2D();

		// Arraylist that holds the raindrops
		ArrayList<Rain> rainList = new ArrayList<Rain>();

		DisplayText displayText = new DisplayText(gc, gameCanvas);

		gc.clearRect(gameCanvas.getWidth(), gameCanvas.getHeight(), 0, 0);

		// Adds the key input to the ArrayList input when the key is pressed
		ArrayList<String> input = new ArrayList<String>();

		gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				// Converts the keyCode to string, and adds it to the ArrayList
				String code = e.getCode().toString();
				if (!input.contains(code)) {
					input.add(code);
//					 if (code == "LEFT")
//					 chicken.imageName = "images/chickenLeft1.png";
//					 else if (code == "RIGHT")
//					 chicken.imageName = "images/chickenRight.png";
				}
			}
		});

		// Removes the key input from the ArrayList input when the key is released
		gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				// Converts the keyCode to string, and removes it from the ArrayList
				String code = e.getCode().toString();
				if (input.contains(code))
					input.remove(code);
			}
		});
		// Create the raindrops; adds the starting number of raindrops to the array
		for (int i = 0; i < Rain.numRain; i++) {
			rainList.add(new Rain(gc, gameCanvas));
		}
		
		Chicken chicken = new Chicken(gc, gameCanvas, input);
		Skateboard skateboard = new Skateboard(gc, gameCanvas, input);
		Bean bean = new Bean(gc, gameCanvas);
		Seed seed = new Seed(gc, gameCanvas);

		new AnimationTimer() {

			@Override
			public void handle(long currentNanoTime) {

				// draw background image
				gc.clearRect(gameCanvas.getWidth(), gameCanvas.getHeight(), 0, 0);

				gc.drawImage(background, 0, 0);

				skateboard.move();
				chicken.move();
				bean.drawBean();
				seed.move();

				// Displays lives and score
				displayText.display(chicken);

				// Moves the individual raindrops
				for (int i = 0; i < Rain.numRain; i++) {
					rainList.get(i).move();
				}

				collidedBean = chicken.collisionBean(bean);

				// Increase score when bean is collected
				if (collidedBean) {
					
					Bean.score++;
					bean.randomBean();

					// Raindrops' speed increases for every 5 beans collected
					if (Bean.score % 5 == 0 && Bean.score != 0)
						Rain.speed += 0.5;

					// Number of raindrops increase for every 6 beans collected
					if (Bean.score % 6 == 0 && Bean.score != 0) {
						Rain.numRain++;
					rainList.add(new Rain(gc, gameCanvas));
					}
				}

				collidedSeed = chicken.collisionSeed(seed);

				// Increase the chicken's speed when seed collides
				if (collidedSeed) {
					seed.randomStart();
					Chicken.speed += 0.5;
				}

				// Check collision for each raindrop in the arraylist
				for (int i = 0; i < Rain.numRain; i++) {
					Rain r = rainList.get(i);

					collidedRain = chicken.collisionRain(r);

					// When collided with raindrop, lives decrease and the raindrop
					// is randomly placed
					if (collidedRain) {
						Chicken.lives -= 1;
						rainList.get(i).randomStart();
					}
				}
				// When the chicken dies, displays the ending string and final score
				// Stops everything by making the speed 0
				if (Chicken.lives == 0) {
					// stores the current score
					newScore = Bean.score;
					displayText.displayEnding();
					Chicken.speed = 0;
					Rain.speed = 0;
					seed.dy = 0;
					
					// Sets the speed to 0 for every raindrop
					for (int i = 0; i < Rain.numRain; i++) {
						rainList.get(i).setDy(Rain.speed);
					}
					// stores the higher score for the next game's comparison
					oldScore = DisplayText.higherScore;
				}

			}

		}.start();

	}
}
