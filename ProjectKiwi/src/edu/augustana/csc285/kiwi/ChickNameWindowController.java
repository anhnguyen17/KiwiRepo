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
import javafx.geometry.Rectangle2D;
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
import javafx.scene.shape.Rectangle;
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
	private Rectangle2D arenaRect;

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
				removeDots();
				Alert alert = new Alert(AlertType.INFORMATION, "The arena rectangle is the area where tracking will occur. "
						+ "Click on the image where you want the upper left corner and the lower right corner of the rectangle to be. "
						+ "Click the Save button when you are done.");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 1) {
				removeDots();
				Alert alert = new Alert(AlertType.INFORMATION, "Click on the image where you want the origin point to be. Click the Save "
						+ "button when you are done.");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 2) {
				removeDots();
				Alert alert = new Alert(AlertType.INFORMATION, "");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();

			} else if (calibrationChoice.getSelectionModel().getSelectedIndex() == 3) {
				removeDots();
				Alert alert = new Alert(AlertType.INFORMATION, "");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();
			}
		});
	}
	
	//coordinate with the smallest x
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
	
	public int calculateRectWidth() {
		int rectWidth = (int) Math.round(Math.abs(currentDots.get(0).getCenterX() - currentDots.get(1).getCenterX()));

		return rectWidth;
	}
	
	public int calculateRectHeight() {
		int rectHeight =(int) Math.round(Math.abs(currentDots.get(0).getCenterY() - currentDots.get(1).getCenterY()));
		return rectHeight;
	}
	
	public void createArenaRect() {	
		Circle upperLeft = findUpperLeftCircle();
		int upperLeftX = (int) Math.round(upperLeft.getCenterX());
		int upperLeftY = (int) Math.round(upperLeft.getCenterY());
		int rectWidth = calculateRectWidth();
		int rectHeight = calculateRectHeight();
		arenaRect = new Rectangle2D(upperLeftX, upperLeftY, rectWidth, rectHeight);
	//	arenaRect.setFill(Color.GREEN); 
	} 
	
	//to test if code for creating arena rect is correct
//	public void drawArenaRect() {
//		currentRectangles.add(arenaRect);
//		videoPane.getChildren().add(arenaRect);
//	}
	
	@FXML
	public void handleSaveBtn() {
		if (calibrationChoice.getSelectionModel().getSelectedIndex() == 0) {
			//System.out.println(currentDots.toString());
			createArenaRect();
			new Alert(AlertType.INFORMATION, "Successfully set the Arena Rectangle").showAndWait();
	//		drawArenaRect();
			project.getVideo().setArenaBounds(arenaRect);
			
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
