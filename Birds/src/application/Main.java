package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	/*
	 * Loads the main fxml file to start the application.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader;
			loader = new FXMLLoader(getClass().getResource("/Controllers/Main.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root,1300, 800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
