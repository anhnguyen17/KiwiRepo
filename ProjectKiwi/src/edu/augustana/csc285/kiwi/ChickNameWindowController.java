package edu.augustana.csc285.kiwi;


import java.io.IOException;

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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LaunchScreen.fxml"));
		
		BorderPane root = (BorderPane)loader.load();
		TrackScreenController nextController = loader.getController();
		//nextController.loadVideo("/S:/CLASS/CS/285/sample_videos/sample1.mp4"); 

		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) submitButton.getScene().getWindow();
		primary.setScene(nextScene);
	}

}
