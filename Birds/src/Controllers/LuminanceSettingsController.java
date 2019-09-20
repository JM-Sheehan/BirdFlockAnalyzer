package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class LuminanceSettingsController {
	
	@FXML
	private TextField redValue;
	@FXML
	private TextField greenValue;
	@FXML
	private TextField blueValue;
	@FXML
	private Button save;
	
	@FXML
	private void handleSaveButton(ActionEvent event) {
		double redValueInput = 0.5;
		double greenValueInput = 0.5;
		double blueValueInput = 0.5;
		
		if(!(redValue.getText().equals(""))) {
			try{
				redValueInput = Double.parseDouble(redValue.getText());
			}catch(NumberFormatException e){
				System.out.println("Invalid Input Provided");
				return;
			}
		}
		if(!(greenValue.getText().equals(""))) {
			try{
				greenValueInput = Double.parseDouble(greenValue.getText());
			}catch(NumberFormatException e){
				System.out.println("Invalid Input Provided");
				return;
			}
		}
		if(!(blueValue.getText().equals(""))) {
			try{
				blueValueInput = Double.parseDouble(blueValue.getText());
			}catch(NumberFormatException e){
				System.out.println("Invalid Input Provided");
				return;
			}
		}
		
		if((redValueInput <= 1) && (redValueInput >= 0)) {
			BlackWhiteController.setRedValue(redValueInput);
		}
		if((greenValueInput <= 1) && (greenValueInput >= 0)) {
			BlackWhiteController.setGreenValue(greenValueInput);
		}
		if((blueValueInput <= 1) && (blueValueInput >= 0)) {
			BlackWhiteController.setBlueValue(blueValueInput);
		}
	}

}
