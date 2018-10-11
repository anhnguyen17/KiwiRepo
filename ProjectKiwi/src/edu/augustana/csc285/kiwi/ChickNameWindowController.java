package edu.augustana.csc285.kiwi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChickNameWindowController {
	@FXML private Button submitButton;
	@FXML private TextField chickNum;

	@FXML private GridPane gridChickNames;

	private List<TextField> chickIDTextFields = new ArrayList<>();
	private List<Label> chickIDLables = new ArrayList<>();
	

	public void initialize() {

	}

	
	@FXML 
	
	public void handleUpdateNumChicks(ActionEvent event)   {
		
		for ( Label lb : chickIDLables) {
			gridChickNames.getChildren().remove(lb);				
		}
		for (TextField tf : chickIDTextFields) {
			gridChickNames.getChildren().remove(tf);			
			
		}
		chickIDTextFields.clear();
		int numChicks = Integer.parseInt(chickNum.getText());
		for (int i = 0; i < numChicks; i++) {
			TextField tf = new TextField();
			Label lb= new Label();
			chickIDTextFields.add(tf);
			chickIDLables.add(lb);
			gridChickNames.add(lb, 0, i);
			lb.setText("ChickID " + (i+1));
			gridChickNames.add(tf, 1, i);			
		}

	}
	@FXML 
	public void handleSubmit(ActionEvent event) throws IOException  {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackScreen.fxml"));

		BorderPane root = (BorderPane)loader.load();
		TrackScreenController nextController = loader.getController();

		ArrayList<String> chickNames = new ArrayList<String>();

		for (TextField tf :chickIDTextFields ) {
			chickNames.add(tf.getText());
		}

		nextController.setChickNames(chickNames);


		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Stage primary = (Stage) submitButton.getScene().getWindow();
		primary.setScene(nextScene);
	}

}
