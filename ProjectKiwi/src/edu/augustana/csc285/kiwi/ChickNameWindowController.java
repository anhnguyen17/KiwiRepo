package edu.augustana.csc285.kiwi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.videoio.Videoio;

import autotracking.AutoTracker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import project.ProjectData;
import project.Video;
import utils.UtilsForOpenCV;

public class ChickNameWindowController {
	@FXML
	private Button submitButton;
	@FXML
	private Button undoButton;
	@FXML
	private Button browseButton;
	@FXML
	private ImageView videoView;
	@FXML
	private TextField chickNum;
	@FXML
	private TextField calibrationNum;
	@FXML
	private BorderPane videoPane;
	@FXML
	private GridPane gridChickNames;

	private List<TextField> chickIDTextFields = new ArrayList<>();
	private List<Label> chickIDLables = new ArrayList<>();
	private List<Circle> currentDots = new ArrayList<>();

	private Video vid;
	private Window stage;

	public void initialize() {

	}

	@FXML
	public void handleUpdateNumChicks() {

		for (Label lb : chickIDLables) {
			gridChickNames.getChildren().remove(lb);
		}
		for (TextField tf : chickIDTextFields) {
			gridChickNames.getChildren().remove(tf);

		}
		chickIDTextFields.clear();

		try {
		int numChicks = Integer.parseInt(chickNum.getText());
		for (int i = 0; i < numChicks; i++) {
			TextField tf = new TextField();
			Label lb= new Label();
		
			chickIDTextFields.add(tf);
			chickIDLables.add(lb);
			gridChickNames.add(lb, 0, i);
			lb.setText("CHICK ID " + (i+1) + ": ");
			gridChickNames.add(tf, 1, i);			
		}
		} 
		catch (NumberFormatException e){
			new Alert(AlertType.WARNING, "Enter a number").showAndWait();
		}
	}
	
	@FXML
	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		File chosenFile = fileChooser.showOpenDialog(stage);
		if (chosenFile != null) {
			vid = new Video(chosenFile.getAbsolutePath());
			vid.setCurrentFrameNum(0);
			Image curFrame = UtilsForOpenCV.matToJavaFXImage(vid.readFrame());
			videoView.setImage(curFrame);
		}
	}

	@FXML
	public void handleUndo() {
		videoPane.getChildren().removeAll(currentDots);
		currentDots.clear();
	}

	public void drawDot(MouseEvent event) {
		Circle dot = new Circle();
		dot.setCenterX(event.getX() + videoView.getLayoutX());
		dot.setCenterY(event.getY() + videoView.getLayoutY());
		dot.setRadius(3);
		dot.setFill(Color.RED);
		currentDots.add(dot);
		videoPane.getChildren().add(dot);
	}
	

	@FXML 
	public void handleSubmit(ActionEvent event) throws IOException  {
		try { 
		FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackScreen.fxml"));

		BorderPane root = (BorderPane) loader.load();
		TrackScreenController nextController = loader.getController();

		ArrayList<String> chickNames = new ArrayList<String>();

		for (TextField tf : chickIDTextFields) {
			
			chickNames.add(tf.getText());
		}

		nextController.setChickNames(chickNames);
		nextController.setFilePath(vid.getFilePath());

		Scene nextScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Stage primary = (Stage) submitButton.getScene().getWindow();
		primary.setScene(nextScene);
	} catch (NullPointerException e) {
		new Alert(AlertType.WARNING, "You must CHOOSE a file first").showAndWait();;
	}
		
	}
}
