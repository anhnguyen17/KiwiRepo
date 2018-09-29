package edu.augustana.csc285.kiwi;


import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChickNameWindowController {
	@FXML private Button submitButton;
	@FXML private TextField chick1ID;
	@FXML private TextField chick2ID;
	@FXML private TextField chick3ID;
	@FXML private TextField chickNum;
	
	public void initialize() {
		
	}
	
	 @FXML 
	public void handleSubmit(ActionEvent event) throws IOException  {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackScreen.fxml"));
		
		BorderPane root = (BorderPane)loader.load();
		TrackScreenController nextController = loader.getController();
		
		ArrayList<String> chickName = new ArrayList<String>();
		chickName.add(chick1ID.getText()); 
		chickName.add(chick2ID.getText()); 
		chickName.add(chick3ID.getText()); 
		
		nextController.setChickNames(chickName);
		

		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) submitButton.getScene().getWindow();
		primary.setScene(nextScene);
	}

}
