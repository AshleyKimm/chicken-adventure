/*
 * ChickenAdventure - Main.java
 * Purpose: Runs the Start window in the beginning when played
 * Author: Ashley Kim
 * Date: November 11th, 2020
 */
package chickenadventure;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Start.fxml"));
			BorderPane root = (BorderPane) loader.load();
			Scene scene = new Scene(root,1700,956);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			StartController controller = loader.getController();
			primaryStage.setScene(scene);
			controller.getScene(primaryStage);
			controller.welcome();

			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
