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
import javafx.scene.control.ChoiceBox;
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

import java.awt.Rectangle;

public class ChickNameWindowController {
	@FXML
	private Button submitButton;
	@FXML
	private Button importBtn;
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
	@FXML
	private ChoiceBox<String> calibrationChoice;
	@FXML
	private Button saveBtn;

	private List<TextField> chickIDTextFields = new ArrayList<>();
	private List<Label> chickIDLables = new ArrayList<>();
	private List<Circle> currentDots = new ArrayList<>();
	private ProjectData project;

	private Video vid;
	private Window stage;
	private TrackScreenController trackScreen;
	

	public void initialize() {
		addToCalibrationBox();
		giveCalibrationInstructions();
		importBtn.setDisable(true);
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
				Label lb = new Label();

				chickIDTextFields.add(tf);
				chickIDLables.add(lb);
				gridChickNames.add(lb, 0, i);
				lb.setText("CHICK ID " + (i + 1) + ": ");
				gridChickNames.add(tf, 1, i);
			}
		} catch (NumberFormatException e) {
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

	//mouseTrackEvent
	
	public void drawDot(MouseEvent event) {
		Circle dot = new Circle();
		dot.setCenterX(event.getX() + videoView.getLayoutX());
		dot.setCenterY(event.getY() + videoView.getLayoutY());
		dot.setRadius(3);
		dot.setFill(Color.RED);
		currentDots.add(dot);
		videoPane.getChildren().add(dot);
	}

	public void addToCalibrationBox() {
		calibrationChoice.getItems().add("Arena Rectangle");
		calibrationChoice.getItems().add("Origin: (0,0)");
		calibrationChoice.getItems().add("Vertical");
		calibrationChoice.getItems().add("Horizontal");
	}
	
	@FXML 
	public void handleImport (ActionEvent event) throws IOException  {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		File chosenFile = fileChooser.showOpenDialog(stage);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackScreen.fxml"));

		BorderPane root = (BorderPane) loader.load();
		TrackScreenController nextController = loader.getController();

		Scene nextScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Stage primary = (Stage) importBtn.getScene().getWindow();
		primary.setScene(nextScene);
		
		nextController.initializeAfterSceneCreated(chosenFile);
	}

	public void giveCalibrationInstructions() {
		calibrationChoice.setOnAction(e ->

		{
			if (calibrationChoice.getSelectionModel().getSelectedIndex() == 0) {
				new Alert(AlertType.INFORMATION, "Arena Rect Info").showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 1) {
				new Alert(AlertType.INFORMATION, "Origin Info").showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 2) {
				new Alert(AlertType.INFORMATION, "Vertical Info").showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 3) {
				new Alert(AlertType.INFORMATION, "Horizontal Info").showAndWait();
			}
		});
	}
	
	//coordinate with the smallest x and the smallest y 
	public Circle findUpperLeftCircle() {
		
	Circle upperLeft = null;
	double minX = Integer.MAX_VALUE;
	
	for(int i = 0; i < currentDots.size(); i++) {
		
		if(currentDots.get(i).getCenterX() < minX) {
			minX = currentDots.get(i).getCenterX();
		}
		
		if(currentDots.get(i).getCenterX() == minX) {
			upperLeft = currentDots.get(i);
		}
	}	
	return upperLeft;
	}
	
	@FXML
	public void handleSaveBtn() {
		Rectangle arenaRect = new Rectangle();
		Circle upperLeft = findUpperLeftCircle();
		double upperLeftX = upperLeft.getCenterX();
		double upperLeftY = upperLeft.getCenterY();
		
//		arenaRect.setBounds(upperLeftX, upperLeftY, width, height); 
		
		if (calibrationChoice.getSelectionModel().getSelectedIndex() == 0) {
			System.out.println(currentDots.toString());
			

		} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 1) {
			
			
		} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 2) {
			
			
		} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 3) {
			
			
		}
	}

	@FXML
	public void handleSubmit(ActionEvent event) throws IOException {
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

			nextController.initializeAfterSceneCreated();

		} catch (NullPointerException e) {
			new Alert(AlertType.WARNING, "You must CHOOSE a file first").showAndWait();
			;
		}

	}
}
