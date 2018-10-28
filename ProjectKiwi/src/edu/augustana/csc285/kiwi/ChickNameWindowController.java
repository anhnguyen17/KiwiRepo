package edu.augustana.csc285.kiwi;

import java.awt.Rectangle;
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
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import project.ProjectData;
import project.TimePoint;
import project.Video;
import utils.UtilsForOpenCV;

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
	private ChoiceBox<String> calibrationChoice;
	@FXML
	private Button saveBtn;

	private List<Circle> currentDots = new ArrayList<>();
	private List<Rectangle> currentRectangles = new ArrayList<>();

	private Video vid;
	private ProjectData project;
	private Window stage;
	private TrackScreenController trackScreen;
	private Rectangle arenaBounds;
	private TimePoint origin;

	public void initialize() {
		addToCalibrationBox();
		giveCalibrationInstructions();
		importBtn.setDisable(true);
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
		removeDots();
	}
	
	public void removeDots() {
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
		if (currentDots.size() == 3) {
			videoPane.getChildren().remove(currentDots.get(0));
			currentDots.remove(0);
		}
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
			removeDots();
			if (calibrationChoice.getSelectionModel().getSelectedIndex() == 0) {
				Alert alert = new Alert(AlertType.INFORMATION, "The arena rectangle is the area where tracking will occur. "
						+ "\nClick on the image where you want the upper left corner and the lower right corner of the rectangle to be. "
						+ "\n\tClick the Save button when you are done.");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 1) {
				Alert alert = new Alert(AlertType.INFORMATION, "Click on the image where you want the origin point to be. Click the Save "
						+ "button when you are done.");
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);	
						alert.showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 2) {
				Alert alert = new Alert(AlertType.INFORMATION, "");
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 3) {
				Alert alert = new Alert(AlertType.INFORMATION, "");
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
			}
		});
	}
	
	public Rectangle createArenaRect() {	
		int rectWidth = (int) Math.round(Math.abs(currentDots.get(0).getCenterX() - currentDots.get(1).getCenterX()));
		int rectHeight = (int) Math.round(Math.abs(currentDots.get(0).getCenterY() - currentDots.get(1).getCenterY()));
		if (currentDots.get(0).getCenterX() < currentDots.get(1).getCenterX() ) {
			return new Rectangle((int) currentDots.get(0).getCenterX(),(int) currentDots.get(0).getCenterY(), rectWidth, rectHeight);
		} else {
			return new Rectangle((int)currentDots.get(1).getCenterX(), (int)currentDots.get(1).getCenterY(), rectWidth, rectHeight);
		}
	} 
	
	@FXML
	public void handleSaveBtn() {
		if (calibrationChoice.getSelectionModel().getSelectedIndex() == 0) {
			arenaBounds = createArenaRect();
			new Alert(AlertType.INFORMATION, "Successfully set the Arena Rectangle").showAndWait();
			
		} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 1) {
			if (currentDots.size() ==1) {
				new Alert(AlertType.INFORMATION, "Successfully set Origin").showAndWait();
				origin = new TimePoint(currentDots.get(0).getCenterX(), currentDots.get(0).getCenterY(), 0);
			} else {
				removeDots();
				new Alert(AlertType.WARNING, "Please choose ONE point only").showAndWait();
			}

		} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 2) {
			new Alert(AlertType.INFORMATION, "Successfully set ").showAndWait();
			
		} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 3) {
			new Alert(AlertType.INFORMATION, "Successfully set ").showAndWait();
			
		}
		currentDots.clear();
	}

	
	@FXML
	public void handleSubmit(ActionEvent event) throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackScreen.fxml"));

			BorderPane root = (BorderPane) loader.load();
			TrackScreenController nextController = loader.getController();


			nextController.setFilePath(vid.getFilePath()); 
	
			Scene nextScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			Stage primary = (Stage) submitButton.getScene().getWindow();
			primary.setScene(nextScene);
			primary.setResizable(false);

			nextController.initializeAfterSceneCreated(arenaBounds, origin);

		} catch (NullPointerException e) {
			new Alert(AlertType.WARNING, "You must CHOOSE a file first").showAndWait();
		}

	}
}
