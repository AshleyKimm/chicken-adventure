/*
 * ChickenAdventure - Skateboard.java
 * Purpose: makes the skateboard move to the same position as chicken
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

public class Skateboard {

	// The starting position
	double x = 830;
	double y = 770;
	double dx = 0;
	double dy = 0;
	String imageName = "images/skateboard1.png";
	Image image = new Image(imageName);

	String left = "LEFT";
	String right = "RIGHT";

	GraphicsContext gc;
	@FXML
	Canvas gameCanvas;

	// Arraylist of user's key input
	ArrayList<String> input;

	public Skateboard(GraphicsContext gc, Canvas gameCanvas, ArrayList<String> input) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.input = input;
	}

	public Skateboard(String imageName, GraphicsContext gc, Canvas gameCanvas, ArrayList<String> input) {
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
		this.y += this.dy;

		// If this.x value is bigger than the width or height
		// of the image, then it is kept inside the width of canvas
		if (this.x < 0 || this.x > gameCanvas.getWidth() - this.image.getWidth()) {
			this.x = x;
		}
		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other
	// images
	public Rectangle2D getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.getWidth(), this.image.getHeight());
	}
}
