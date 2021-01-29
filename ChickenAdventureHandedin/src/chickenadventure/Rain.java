/*
 * ChickenAdventure - Rain.java
 * Purpose: to display and allow the raindrop images to move in
 * 			right direction
 * Author: Ashley Kim
 * Date: November 11th
 * Course: ICS4U1
 */

package chickenadventure;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Rain {

	static int numRain = 5;

	// Allows the raindrops to only move vertically
	double x = 0;
	// Places the raindrops at random position of x axis
	double y = (double) (Math.random() * 200);
	
	static double speed = 3.0;

	double dy = -speed;

	// Chooses raindrop images with random shades from the array
	String[] imageName = { "images/raindrop1.png", "images/raindrop1 (2).png", "images/raindrop1 (3).png" };
	int randomRain = (int) (Math.random() * imageName.length);
	Image image = new Image(imageName[randomRain]);

	GraphicsContext gc;
	@FXML
	Canvas gameCanvas;

	public Rain(GraphicsContext gc, Canvas gameCanvas) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		randomStart();
	}

	public Rain(String[] imageName, GraphicsContext gc, Canvas gameCanvas) {
		this.imageName = imageName;
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		randomStart();
	}

	// Makes the raindrops appear in random position
	public void randomStart() {
		
		this.dy = speed;

		// Generates random x value within the size of gameCanvas
		// Generates random y position within 300 y value
		this.x = (int) (Math.random() * (this.gameCanvas.getWidth() - this.image.getWidth()));
		this.y = (int) (Math.random() * 300);
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

	public String[] getImageName() {
		return imageName;
	}

	public void setImageName(String[] imageName) {
		this.imageName = imageName;
	}

	public void move() {

		// Add dy value (speed) to the current y position
		this.y += this.dy;

		// If this.y value is equal or bigger than the width or height
		// of the image, then they are placed back to the top at random x value.
		if (this.y <= 0 || this.y >= this.gameCanvas.getHeight() - this.image.getHeight()) {
			this.x = (Math.random() * (this.gameCanvas.getWidth() - this.image.getWidth()));
			this.y = (int) (Math.random() * 300);
		}
		
		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other images
	public Rectangle2D getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.getWidth(), this.image.getHeight());
	}

}
