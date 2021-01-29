/*
 * ChickenAdventure - Chicken.java
 * Purpose: to display the chicken images at corresponding positions
 * 			to limit the area that the chicken can move (only horizontal)
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */

package chickenadventure;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Chicken {

	// The chicken is at the center when the game begins
	double x = 850;
	double y = 700;

	double dx = 0;
	double dy = 0;

	static double speed = 3.0;
	static int lives = 3;

	String imageName = "images/chickenRight.png";
	Image image = new Image(imageName);

	String left = "LEFT";
	String right = "RIGHT";

	GraphicsContext gc;
	@FXML
	Canvas gameCanvas;

	// Arraylist of user's key input
	ArrayList<String> input;

	public Chicken(GraphicsContext gc, Canvas gameCanvas, ArrayList<String> input) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.input = input;
	}

	public Chicken(String imageName, GraphicsContext gc, Canvas gameCanvas, ArrayList<String> input) {
		this.imageName = imageName;
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.input = input;
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

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
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

		// left & right button
		if (this.input.contains(this.left)) {
			this.dx = -Chicken.speed;
		} else if (this.input.contains(this.right)) {
			this.dx = Chicken.speed;
		} else {
			this.dx = 0;
		}
		// save old position
		double x = this.x;

		// Adds the new dx and dy value to current position to make it move
		this.x += this.dx;

		// If this.x is bigger than the width of the canvas, it is kept inside the window
		if (this.x < 0 || this.x > gameCanvas.getWidth() - this.image.getWidth()) {
			this.x = x;
		}

		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other images
	public Rectangle2D getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.getWidth(), this.image.getHeight());
	}

	// Checks for the collision with other images, and return true
	public boolean collisionRain(Rain rain) {
		boolean collide = this.getBoundary().intersects(rain.getBoundary());
		return collide;
	}

	public boolean collisionBean(Bean bean) {
		boolean collide = this.getBoundary().intersects(bean.getBoundary());
		return collide;
	}

	public boolean collisionSeed(Seed seed) {
		boolean collide = this.getBoundary().intersects(seed.getBoundary());
		return collide;
	}
}
