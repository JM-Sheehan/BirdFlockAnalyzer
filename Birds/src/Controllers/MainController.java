package Controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import Controllers.BlackWhiteController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {

	@FXML
	private MenuItem openMenu;
	@FXML
	private MenuItem closeMenu;
	@FXML
	private MenuItem settingsMenu;
	@FXML
	private MenuItem blackWhiteMenu;
	@FXML
	private ImageView colourImage;
	private File imageFile;
	private Pane pane;
	
	//////////////////////////////
	/// Image Chooser method   ///
	//////////////////////////////
	@FXML
	public void open() {
		FileChooser fc = new FileChooser();
		
		/*
		 * Ensures that only valid files can be chosen
		 */
		fc.getExtensionFilters().addAll(new ExtensionFilter
				("Bird Image Chooser", "*.jpg", "*.png"));
		File file = fc.showOpenDialog(null);
		if(file != null) {
			imageFile = file;
			Image image;
			try {
				/*
				 * Converts file choosen to image format.
				 */
				image = new Image(file.toURI().toURL().toString());
				colourImage.setImage(image);
			}
			/*
			 * Catch in case of invalid file selection.
			 */
			catch(MalformedURLException e){
				System.out.println("Invalid file selected");
			}
		}
		else {
			System.out.println("Invalid file selected.");
		}
	}
	
	@FXML
	public void exit() {
		Platform.exit();
		System.exit(0);
	}
	
	///////////////////////////////////////////////
	/// Opens Window that allows user to define ///
	/// the limits of rgb values for the black  ///
	/// & white conversion.                     ///
	///////////////////////////////////////////////
	@FXML
	private void luminanceWindow() {
		try {
			FXMLLoader loader;
			loader = new FXMLLoader(getClass().getResource("/Controllers/LuminanceSettings.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root, 383, 401);
			scene.getStylesheets().add(getClass().getResource(
					"/application/application.css").toExternalForm());
			Stage settingsStage = new Stage();
			settingsStage.setScene(scene);
			settingsStage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////
	/// Performs black & white image conversion ///
	/// ,then loads black & white fxml window   ///
	///////////////////////////////////////////////
	@FXML
	private void blackWhiteWindow() throws IOException {
		if(imageFile != null) {
			pane = new Pane();
			/*
			 * Constructor for black & White controller, passes pane used to 
			 * draw rectangles on and the original colour image.
			 */
			BlackWhiteController black = new BlackWhiteController(pane, colourImage);
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/Controllers/BlackWhite.fxml"));
			/*
			 * Sets the controller for the fxml window here rather than in the fxml
			 * file itself, to allow for the constructor to be used first.
			 */
			loader.setController(black);
			
			Parent parent = loader.load();
			Scene scene = new Scene(parent, 1300, 800);
			scene.getStylesheets().add(getClass().getResource(
					"/application/application.css").toExternalForm());
			Stage blackStage = new Stage();
			blackStage.setScene(scene);
			blackStage.show();
			}
			/*
			 * Ensures that an image must be selected before conversion is performed.
			 */
			else {
				System.out.println("Please choose an image");
			}
		}
}
