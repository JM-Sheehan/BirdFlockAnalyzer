package Controllers;

import Controllers.BlackWhiteController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AdvancedSettingsController {
//	private double minInput;
//	private double maxInput;
//	
	@FXML
	private TextField minimum;
	@FXML
	private TextField maximum;
	@FXML
	private Button button;
	
	///////////////////////////////////////////////////////
	/// Method that handles the users input for maximum ///
	/// and minimum within the settings windows.        ///
	///////////////////////////////////////////////////////
	@FXML
	private void handleSaveButton(ActionEvent event) {
		double minInput = 1.0;
		double maxInput = 1.0;
		if(!(minimum.getText().equals(""))) {
			try{
				minInput = Double.parseDouble(minimum.getText());
			}catch(NumberFormatException e){
				System.out.println("Invalid Input Provided");
				return;
			}
		}
		
		if(!(maximum.getText().equals(""))) {
			try{
				maxInput = Double.parseDouble(maximum.getText());
			}catch(NumberFormatException e){
				System.out.println("Invalid Input Provided");
				return;
			}
		}
		
		
		BlackWhiteController.setMinLimit(minInput);
		BlackWhiteController.setMaxLimit(maxInput);
		}
}
