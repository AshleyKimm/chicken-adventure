/*
 * ChickenAdventure - DisplayText.java
 * Purpose: to display texts and images to the window
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */

package chickenadventure;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DisplayText {
	GraphicsContext gc;
	@FXML
	Canvas gameCanvas;

	static int higherScore = 0;
	// Assign the same font with different sizes
	Font font = Font.loadFont("file:resources/fonts/ARCADECLASSIC.TTF", 100);
	Font fontGameOver = Font.loadFont("file:resources/fonts/ARCADECLASSIC.TTF", 200);
	Font fontOpening = Font.loadFont("file:resources/fonts/ARCADECLASSIC.TTF", 150);
	Font fontHowTo = Font.loadFont("file:resources/fonts/ARCADECLASSIC.TTF", 80);

	// Assign the images
	String heartName = "images/heart3.png";
	Image heart = new Image(heartName);

	String chickenRightName = "images/chickenRight.png";
	Image chickenRight = new Image(chickenRightName);

	String chickenLeftName = "images/chickenLeft1.png";
	Image chickenLeft = new Image(chickenLeftName);

	String beanName = "images/bean1.png";
	Image bean = new Image(beanName);

	String seedName = "images/seed1.png";
	Image seed = new Image(seedName);

	public DisplayText(GraphicsContext gc, Canvas gameCanvas) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
	}

	// Displays the lives and the score (number of beans collected)
	public void display(Chicken chicken) {
		
		this.gc.drawImage(this.heart, 1100, 30);

		String lives = "X " + Chicken.lives;
		gc.setFont(font);
		gc.setFill(Color.WHITE);
		gc.fillText(lives, 1200, 100);

		this.gc.drawImage(this.bean, 1350, 30);

		String score = "X " + Bean.score;
		gc.setFont(font);
		gc.setFill(Color.WHITE);
		gc.fillText(score, 1450, 100);

	}

	// Displays the game title
	public void gameOpening() {
	
		String chicken = "CHICKEN";
		String adventure = "ADVENTURE";
		gc.setFont(fontGameOver);
		gc.setFill(Color.WHITE);
		gc.fillText(chicken, 450, 190);
		gc.fillText(adventure, 380, 350);
	}

	// Displays the 'How To Play' instructions
	public void howToPlay() {
		String title = "How To Play";
		gc.setFont(fontOpening);
		gc.setFill(Color.WHITE);
		gc.fillText(title, 450, 120);

		String moveChicken = "Move the chicken with arrow keys";
		gc.setFont(fontHowTo);
		gc.setFill(Color.WHITE);
		gc.fillText(moveChicken, 100, 300);

		// Chicken images
		gc.drawImage(chickenRight, 1400, 220);
		gc.drawImage(chickenLeft, 1500, 220);

		String collectBeans = "Collect as many beans as possible";
		gc.setFont(fontHowTo);
		gc.setFill(Color.WHITE);
		gc.fillText(collectBeans, 100, 400);

		// Bean image
		gc.drawImage(bean, 1450, 330);

		String getSeeds = "Collect seeds to speed up";
		gc.setFont(fontHowTo);
		gc.setFill(Color.WHITE);
		gc.fillText(getSeeds, 100, 500);

		// Seed image
		gc.drawImage(seed, 1110, 440);

		String lives = "You get three lives";
		gc.setFont(fontHowTo);
		gc.setFill(Color.WHITE);
		gc.fillText(lives, 100, 600);

		// Heart (lives) image
		gc.drawImage(heart, 880, 540);

		String goodLuck = "Good luck";
		gc.setFont(fontHowTo);
		gc.setFill(Color.WHITE);
		gc.fillText(goodLuck, 100, 800);
	}

	// Displays the ending text (score) when game ends (when lives = 0)
	public void displayEnding() {
		
		if (GameController.newScore <= GameController.oldScore)
			higherScore = GameController.oldScore;
		else higherScore = GameController.newScore;
		
			
		

		String gameOver = "GAME OVER";
		gc.setFont(fontGameOver);
		gc.setFill(Color.RED);
		gc.fillText(gameOver, 400, 400);

		String yourScore = "YOUR SCORE ";
		gc.setFont(font);
		gc.setFill(Color.WHITE);
		gc.fillText(yourScore, 580, 570);

		String score = "" + Bean.score;
		gc.setFont(font);
		gc.setFill(Color.WHITE);
		gc.fillText(score, 820, 650);
		
		String highScore = "HIGH SCORE ";
		gc.setFont(font);
		gc.setFill(Color.WHITE);
		gc.fillText(highScore, 580, 720);
		
		String scoreInt = "" + higherScore;
		gc.setFont(font);
		gc.setFill(Color.WHITE);
		gc.fillText(scoreInt, 820, 800);
		
	}
}
