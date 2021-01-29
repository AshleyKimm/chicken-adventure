/*
 * ChickenAdventure - Seed.java
 * Purpose: to display and allow the seeds to be placed
 * at corresponding positions
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */

package chickenadventure;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Seed {
	
	double x = 0;
	double y = 0;

	// Same speed as raindrops
	double dy = -Rain.speed;
	
	String imageName = "images/seed1.png";
	Image image = new Image(imageName);

	GraphicsContext gc;
	@FXML
	Canvas gameCanvas;

	public Seed(GraphicsContext gc, Canvas gameCanvas) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		randomStart();
	}

	public Seed(String imageName, GraphicsContext gc, Canvas gameCanvas) {
		this.imageName = imageName;
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		randomStart();
	}

	// Displays the seed at random position
	public void randomStart() {

		this.dy = Rain.speed;
		
		// Generates random x value within the size of gameCanvas
		this.x = (int) (Math.random() * (this.gameCanvas.getWidth() - this.image.getWidth()));
		this.y = 0;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}


	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void move() {
		
		// Add dy value (speed) to the current y position
		this.y += this.dy;
		
		// If this.y value is bigger than the width or height
		// of the image, then it is sent back to the top
		if (this.y <= 0 || this.y >= this.gameCanvas.getHeight() - this.image.getHeight()) {
			this.x = (Math.random() * (this.gameCanvas.getWidth() - this.image.getWidth()));
			this.y = 0;
		}
		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}
	
	// gets the x, y, width and height of image to set collision with other images
	public Rectangle2D getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.getWidth(), this.image.getHeight());
	}

}

