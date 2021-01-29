/*
 * ChickenAdventure - Bean.java
 * Purpose: to display the bean images at certain positions on window
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */
package chickenadventure;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bean {

	// static variable of the numbers of beans collected
	static int score = 0;
	double x;
	// Allows the bean to appear on same y axis (ground)
	double y = 700;

	String imageName = "images/bean1.png";
	Image image = new Image(imageName);

	GraphicsContext gc;
	@FXML
	Canvas gameCanvas;

	public Bean(GraphicsContext gc, Canvas gameCanvas) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		randomBean();
	}

	public Bean(String imageName, GraphicsContext gc, Canvas gameCanvas) {
		this.imageName = imageName;
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		randomBean();
	}

	// Sets the random position of bean in the game canvas (same x axis)
	public void randomBean() {
		this.x = (int) (Math.random() * (this.gameCanvas.getWidth() - this.image.getWidth()));
		this.y = 700;
	}

	// Displays the bean image at corresponding random position
	public void drawBean() {
		gc.drawImage(this.image, this.x, this.y);
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

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	// gets the x, y, width and height of image to set collision with other images
	public Rectangle2D getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.getWidth(), this.image.getHeight());
	}

}
