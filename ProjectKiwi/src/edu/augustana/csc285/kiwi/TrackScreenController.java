package edu.augustana.csc285.kiwi;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import project.AnimalTrack;
import project.ProjectData;
import project.Video;
import project.TimePoint;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utils.UtilsForOpenCV;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;

public class TrackScreenController implements AutoTrackListener {
	@FXML
	private Label blankFrameLabel;
	@FXML
	private TextField blankFrameTextField;
	@FXML
	private ImageView videoView;
	@FXML
	private Slider sliderSeekBar;
	@FXML
	private Button submitButton;
	@FXML
	private Button backwardBtn;
	@FXML
	private Button forwardBtn;
	@FXML
	private Button frameBtn;
	@FXML
	private BorderPane videoPane;
	@FXML
	private ChoiceBox<String> chickChoice;
	@FXML
	private AnchorPane trackPane;
	@FXML
	private Label timeLabel;
	@FXML
	private Label instructionLabel;
	@FXML
	private ChoiceBox<Double> timeStepCb;
	@FXML
	private Label availableAuto;
	@FXML
	private ChoiceBox<AnimalTrack> availAutoChoiceBox;
	@FXML
	private AnchorPane sideBarPane;
	@FXML
	private AnchorPane topBarPane;
	@FXML
	private ColorPicker chickColor;

	private List<Circle> currentDots = new ArrayList<>();
	// add up to 10 colors
	private Color[] chickColors = new Color[] { Color.PURPLE, Color.AQUA, Color.YELLOW, Color.RED, Color.GREEN,
			Color.PINK, Color.WHITE, Color.GRAY };
	private double[] timeStep = new double[] { 0.5, 1, 2, 3, 5 };
	private double time = 1;
	private String filePath;
	private AutoTracker autotracker;
	private AnimalTrack animalTrack;
	private ProjectData project;
	private ChickNameWindowController firstWindow;
	public ArrayList<String> chickNames = new ArrayList<String>();
	private Window stage;

	@FXML
	public void initialize() {
		
		for (int i = 0; i < timeStep.length; i++) {
			timeStepCb.getItems().add(timeStep[i]);
		}
		timeStepCb.getSelectionModel().selectFirst();
		availAutoChoiceBox.setOnAction(e -> showSelectedAutoTrack(availAutoChoiceBox.getSelectionModel().getSelectedItem()));
		
	}
	
	
	/** This method display the About screen*/
	@FXML
	public void handleAbout() {
		String about ="Chicken Tracking Software - By Kiwi Team";
		about += "\n\tTeam members: \n\t\tAnh Nguyen \n\t\tAJ Housholder"
			  +  "\n\t\tGenesis Sarmiento \n\t\tThomas Ayele"
		      +  "\n\tProject Supervisor: Dr. Forrest Stondedahl"
		      +  "\nCSC 285 - Augustana College";
		new Alert(AlertType.INFORMATION, about).showAndWait();
	}

	public void updateColor() {
		if(chickChoice.getSelectionModel().isEmpty()) {
			System.out.println("no chick selected");
		} else {
			chickColor.setValue(project.getCurrentProject().getTracks().get(chickChoice.getSelectionModel().getSelectedIndex()).getColor());
		}
	}
	
	/**
	 * this method draw the user selected AutoTrack on top of the video pane
	 * @param tracks the AnimalTrack to be drawn
	 */
	public void showSelectedAutoTrack(AnimalTrack track) {
		if(!availAutoChoiceBox.getItems().isEmpty()) {
		videoPane.getChildren().removeAll(currentDots);
		for (int x = 0; x < track.getTotalTimePoints(); x++) {
			double scalingRatio = getImageScalingRatio();
			TimePoint temp = track.getTimePointAtIndex(x);
//			drawDot(temp.getX()*scalingRatio-5 + sideBarPane.getWidth(), temp.getY()*scalingRatio-5 + topBarPane.getHeight(), Color.DARKGREY);
			drawDot(temp.getX()*scalingRatio-5 + sideBarPane.getWidth(), temp.getY()*scalingRatio-5 + topBarPane.getHeight(), Color.DARKGREY);
		}
		}
	}
	
	/**
	 * this method calculates and return the scaling ratio of the pane and the actual video
	 * @return the scaling ratio for converting between pixels and cm for this specific video
	 */
	private double getImageScalingRatio() {
		double widthRatio = (videoPane.getWidth() - (sideBarPane.getWidth()*.5)) / project.getCurrentProject().getVideo().getFrameWidth();
		double heightRatio = (videoPane.getHeight() - (topBarPane.getHeight()*.5)) / project.getCurrentProject().getVideo().getFrameHeight();
		return Math.min(widthRatio, heightRatio);
		//return heightRatio;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/** this method changes a chick dot color to the user selected color */
	public void handleChickColorChange() {
		if(chickChoice.getSelectionModel().isEmpty()) {
		} else {
			AnimalTrack temp = project.getCurrentProject().getTracks().get(chickChoice.getSelectionModel().getSelectedIndex());
			temp.setColor(chickColor.getValue());
		}
	}

	public void initializeAfterSceneCreated(Rectangle arenaBounds, TimePoint origin, double xPixelsPerCm, double yPixelsPerCm) {
		videoView.fitWidthProperty().bind(videoPane.widthProperty().subtract(sideBarPane.widthProperty()));
		videoView.fitHeightProperty().bind(videoPane.heightProperty().subtract(topBarPane.heightProperty()));
		videoView.fitWidthProperty().bind(videoPane.getScene().widthProperty().subtract(sideBarPane.widthProperty()));
		chickChoice.setOnAction(e -> updateColor());
		loadVideo(filePath);
		
		double x = arenaBounds.x + sideBarPane.getWidth();
		double y = arenaBounds.y + sideBarPane.getHeight();
		arenaBounds.setLocation((int)x, (int)y);
		project.getCurrentProject().getVideo().setArenaBounds(arenaBounds);
		project.getCurrentProject().getVideo().setOriginPoint(origin); 
		project.getCurrentProject().getVideo().setXPixelsPerCm(xPixelsPerCm);
		project.getCurrentProject().getVideo().setYPixelsPerCm(yPixelsPerCm);
		System.out.println("done");
	}
	
	public void repaintCanvas() {
		showFrameAt(project.getCurrentProject().getVideo().getCurFrameNum());
	}
	
	//This method has not work yet
	public void initializeAfterSceneCreated(File chosenFile) throws FileNotFoundException {
		loadProject(chosenFile);
		
//		for (int i =0; i < project.getTracks().size(); i++) {
//			chickChoice.getItems().add(project.getTracks().get(i).getID());
//		}
//		for (AnimalTrack track : project.getUnassignedSegments()) {
//				availAutoChoiceBox.getItems().add(track);
//		}
	}

	private void showFrameAt(int frameNum) {
		if (autotracker == null || !autotracker.isRunning()) {
			project.getCurrentProject().getVideo().setCurrentFrameNum(frameNum);
			Image curFrame = UtilsForOpenCV.matToJavaFXImage(project.getCurrentProject().getVideo().readFrame());
			videoView.setImage(curFrame);
			videoPane.getChildren().removeAll(currentDots);
			double scalingRatio = getImageScalingRatio();
			drawAssignedAnimalTracks(scalingRatio, project.getCurrentProject().getVideo().getCurFrameNum());
			drawUnassignedSegments(scalingRatio, project.getCurrentProject().getVideo().getCurFrameNum());
		}
	}
	
	private void drawAssignedAnimalTracks(double scalingRatio, int frameNum) {
		for (int i = 0; i < project.getCurrentProject().getTracks().size(); i++) {
			AnimalTrack track = project.getCurrentProject().getTracks().get(i);
			Color trackColor = null;
			if(track.getColor() != null) {
				trackColor = track.getColor();
			} else {
			trackColor = chickColors[chickChoice.getSelectionModel().getSelectedIndex()];;
			}
			Color trackPrevColor = trackColor.deriveColor(0, 0.5, 2.5, 1.0); // subtler variant

			// draw chick's recent trail from the last few seconds 
			for (project.TimePoint prevPt : track.getTimePointsWithinInterval(frameNum-90, frameNum)) {
				drawDot(prevPt.getX()*scalingRatio-5, prevPt.getY()*scalingRatio-5, trackPrevColor);
			}
			// draw the current point (if any) as a larger dot
			TimePoint currPt = track.getTimePointAtTime(frameNum);
			if (currPt != null) {
				drawDot(currPt.getX()*scalingRatio-5, currPt.getY()*scalingRatio-5, trackColor);
			}
		}		
	}
	
	private void drawUnassignedSegments(double scalingRatio, int frameNum) {
		for (AnimalTrack segment: project.getCurrentProject().getUnassignedSegments()) {
			// draw this segments recent past & near future locations 
			for (TimePoint prevPt : segment.getTimePointsWithinInterval(frameNum-30, frameNum+30)) {
				drawDot(prevPt.getX()*scalingRatio + sideBarPane.getWidth(), prevPt.getY()*scalingRatio-5 + topBarPane.getHeight(), Color.DARKGREY);
			}
			// draw the current point (if any) as a larger square
			TimePoint currPt = segment.getTimePointAtTime(frameNum);
			if (currPt != null) {
				drawDot(currPt.getX()*scalingRatio + sideBarPane.getWidth(), currPt.getY()*scalingRatio-5 + topBarPane.getHeight(), Color.GREEN);
			}
		}		
	}
	
	
	/**jumpFrame method updated the current video frame, the slider and the time label
	 * by the the selected amount of time 
	 * @param time the selected amount of time changed
	 */
	public void jumpFrame(double time) {
		int frameNum = project.getCurrentProject().getVideo().getCurFrameNum() + ((int) (30 * time));
		if (frameNum <= project.getCurrentProject().getVideo().getTotalNumFrames() && frameNum >= 0) {
			setTimeLabel(frameNum);
			showFrameAt((int) frameNum);
			sliderSeekBar.setValue((int) frameNum);
			project.getCurrentProject().getVideo().setCurFrameNum(frameNum);
		}
	}

	/** this method rewinds the video by the selected amount of time when the user clicks on Backward button*/
	@FXML
	public void handleBackward() {
		videoPane.getChildren().removeAll(currentDots);
		time = -timeStep[timeStepCb.getSelectionModel().getSelectedIndex()];
		jumpFrame(time);
	}

	/** this method fasts forward the video by the selected amount of time when the user clicks on Forward button*/
	@FXML
	public void handleForward() {
		videoPane.getChildren().removeAll(currentDots);
		time = timeStep[timeStepCb.getSelectionModel().getSelectedIndex()];
		jumpFrame(time);
	}
	
	/** this method assigns a chosen auto track to the selected chick*/
	@FXML
	public void handleAutoTrackMerge() {
		if(availAutoChoiceBox.getSelectionModel().getSelectedItem() == null || chickChoice.getItems().isEmpty()) {
			new Alert(AlertType.ERROR, "Unable to add tracks: chick selection or auto track is null").showAndWait();
		} else {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Merging will overwrite any previous track points during the frames automatically tracked. ");
		alert.setContentText("Are you sure you wish to continue?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			int currentChick = chickChoice.getSelectionModel().getSelectedIndex();
			AnimalTrack temp = project.getCurrentProject().getTracks().get(currentChick);
			temp.mergeAutoTracks(availAutoChoiceBox.getSelectionModel().getSelectedItem());
			for (int x = 0; x < project.getCurrentProject().getTracks().size(); x++) {
				System.out.println(temp);
			}
			availAutoChoiceBox.getItems().remove(availAutoChoiceBox.getSelectionModel().getSelectedItem());
			new Alert(AlertType.INFORMATION, "The tracks have been assigned sucesfully");
		} else {
		   new Alert(AlertType.ERROR, "Merge Cancelled By User").showAndWait();
		}
		}
	}
	
	/** this method set the chosen frame as the empty frame*/ 
	@FXML
	public void handleSetEmptyFrame() {
		project.getCurrentProject().getVideo().setEmptyFrameNum(project.getCurrentProject().getVideo().getCurFrameNum());
		new Alert(AlertType.INFORMATION, "Success! The empty frame has been updated.").showAndWait();
	}
	
	/** this method draws and saves the TimePoint of a chick in manual tracking*/ 
	public void mouseClick(MouseEvent event) {
		int selectedChickIndex = chickChoice.getSelectionModel().getSelectedIndex();
		if (selectedChickIndex >= 0) {
			AnimalTrack selectedTrack = project.getCurrentProject().getTracks().get(selectedChickIndex);
			int curFrameNum = (int) sliderSeekBar.getValue();
			double x = event.getX() + videoView.getLayoutX();
			double y = event.getY() + videoView.getLayoutY();
			System.out.println(x + " y: " + y);
			selectedTrack.setTimePointAtTime(x, y, curFrameNum);
			System.out.println(selectedTrack);
			
		} else {
			new Alert(AlertType.WARNING, "You must CHOOSE a chick first!").showAndWait();
		}
		
		//Changed to jump by the currently selected time interval instead of the previous value of .5
		jumpFrame(timeStepCb.getValue());
		chickChoice.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
		});
	}

	
	private void drawDot(double x, double y, Color color) {
		Circle dot = new Circle();
		dot.setCenterX(x);
		dot.setCenterY(y);
		dot.setRadius(5);
		dot.setFill(color);
		currentDots.add(dot);
		videoPane.getChildren().add(dot);
	}
	
	/** this method allows user to add a chick to the project and chick choice box */ 
	@FXML
	public void addChick() {
		TextInputDialog dialog = new TextInputDialog("Enter Chick Name");
		 
		dialog.setTitle("Add new chick");
		dialog.setHeaderText("Enter chick Name");
		dialog.setContentText("Name:");
		 
		Optional<String> result = dialog.showAndWait();
		
		result.ifPresent(name -> {
			String temp = result.get();
			for(int x = 0; x < project.getCurrentProject().getTracks().size(); x++) {
				if(project.getCurrentProject().getTracks().get(x).getID().equals(temp)) {
					new Alert(AlertType.ERROR, "Chick with desired name already exists!").showAndWait();
					return;
				}
			}
		    project.getCurrentProject().getTracks().add(new AnimalTrack(temp));
		    chickChoice.getItems().add(temp);
		});
	}
	
	/** this method export the current working progress to JSon format*/ 
	@FXML
	public void handleSaveProgress() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Saving the project");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", "*.json"));
		File chosenFile = fileChooser.showSaveDialog(stage);
		if (chosenFile != null) {
			project.getCurrentProject().saveToFile(chosenFile); 
		} 
	}
	
	/** this method exports all the tracking data to a CSV file. */
	@FXML
	public void ExportToCSVFile(ActionEvent e) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Exporting to CSV file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			project.getCurrentProject().exportToCSV(file);
		}
	}

	/** this method removes the currently selected chicks */ 
	public void removeChick() {
		if(chickChoice.getSelectionModel().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION, "No chick is selected.");
			Optional<ButtonType> result = alert.showAndWait();
		} else {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Chick Removal");
		alert.setHeaderText("You are about to remove chick: " + 
				chickChoice.getSelectionModel().getSelectedItem() + ". This action cannot be undone.");
		alert.setContentText("Are you sure you wish to continue?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			String temp = chickChoice.getSelectionModel().getSelectedItem();
			project.getCurrentProject().removeChick(temp);
			chickChoice.getItems().remove(temp);
		} else {
		   new Alert(AlertType.ERROR, "Cancelled by user. " + chickChoice.getSelectionModel().getSelectedItem() + " was not removed.").showAndWait();
		}
		}
	}

	
	@FXML
	public void handleSlider() {
		videoPane.getChildren().removeAll(currentDots);
		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int frameNum = (int) (newValue.doubleValue() / sliderSeekBar.getMax()
						* project.getCurrentProject().getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT) - 1);
				showFrameAt(frameNum);
				setTimeLabel(frameNum);
				project.getCurrentProject().getVideo().setCurFrameNum(frameNum);
			}
		});
	}

	/** this method loads the Video and create a new Project */ 
	public void loadVideo(String filePath) {
			project.getCurrentProject().getVideo().setFilePath(filePath);
			Video video = project.getCurrentProject().getVideo();
			sliderSeekBar.setMax(video.getTotalNumFrames() - 1);
			showFrameAt(0);
	}
	
	public void loadProject(File chosenFile) throws FileNotFoundException {
		project = ProjectData.loadFromFile(chosenFile);
		Video video = project.getCurrentProject().getVideo();
		sliderSeekBar.setMax(video.getTotalNumFrames() - 1);
		showFrameAt(0);	
	}

	@FXML
	public void handleAutoTracking() {
		videoPane.getChildren().removeAll(currentDots);
		if (autotracker == null || !autotracker.isRunning()) {
			project.getCurrentProject().getVideo().setXPixelsPerCm(5.5);
			project.getCurrentProject().getVideo().setYPixelsPerCm(5.5);
			autotracker = new AutoTracker();
			// Use Observer Pattern to give autotracker a reference to this object,
			// and call back to methods in this class to update progress.
			autotracker.addAutoTrackListener(this);
			// this method will start a new thread to run AutoTracker in the background
			// so that we don't freeze up the main JavaFX UI thread.
			autotracker.startAnalysis(project.getCurrentProject().getVideo());
			submitButton.setText("CANCEL auto-tracking");
		} else {
			autotracker.cancelAnalysis();
			submitButton.setText("Start auto-tracking");
		}

	}

	// this method will get called repeatedly by the Autotracker after it analyzes
	// each frame
	@Override
	public void handleTrackedFrame(Mat frame, int frameNumber, double fractionComplete) {
		Image imgFrame = UtilsForOpenCV.matToJavaFXImage(frame);
		// this method is being run by the AutoTracker's thread, so we must
		// ask the JavaFX UI thread to update some visual properties
		Platform.runLater(() -> {
			
			videoView.setImage(imgFrame);
			// progressAutoTrack.setProgress(fractionComplete);
			sliderSeekBar.setValue(frameNumber);
			setTimeLabel(frameNumber);
		});
	}

	@Override
	public void trackingComplete(List<AnimalTrack> trackedSegments) {
		project.getCurrentProject().getUnassignedSegments().clear();
		project.getCurrentProject().getUnassignedSegments().addAll(trackedSegments);
		for (AnimalTrack track : trackedSegments) {
			System.out.println(track);
			if(track.getTotalTimePoints() < 15) {
				project.getCurrentProject().getUnassignedSegments().remove(track);
			} else {
				availAutoChoiceBox.getItems().add(track);
			}
		}
		Platform.runLater(() -> {
			// progressAutoTrack.setProgress(1.0);
			submitButton.setText("Start auto-tracking");
		});
	}
	
	
	/** this method updates time label as the current frame change */
	public void setTimeLabel(double curFrameNum) {
		int minute = (int) (curFrameNum / 30) / 60;
		int second = (int) (curFrameNum / 30) - minute * 60;
		String time = "";
		if (second < 10) {
			time = "0" + minute + ":" + "0" + second;
		} else {
			time = "0" + minute + ":" + second;
		}
		timeLabel.setText(time);
	}

	/** this method allows user to set the start and end frame for the auto tracking. */
	@FXML
	public void handleFrame() {
		if (frameBtn.getText().equals("Start Time")) {
			project.getCurrentProject().getVideo().setStartFrameNum(project.getCurrentProject().getVideo().getCurFrameNum());
			frameBtn.setText("End Time");
			instructionLabel.setText("Select your prefered end time:");
		} else {
			project.getCurrentProject().getVideo().setEndFrameNum(project.getCurrentProject().getVideo().getCurFrameNum());
			frameBtn.setText("Start Time");
			instructionLabel.setText("Select your prefered start time:");
		}
	}
	
	/** this method calculates and shows the total distance one chick travels after finish tracking */ 
	@FXML
	public void handleTotalDistance() {
		int selectedChickIndex = chickChoice.getSelectionModel().getSelectedIndex();
		int distance = (int) project.getCurrentProject().getTracks().get(selectedChickIndex).getTotalDistance();
		String message = "Chick " + project.getCurrentProject().getTracks().get(selectedChickIndex).getID() +
				"travels a total distance of "+ distance;
		new Alert(AlertType.INFORMATION, message).showAndWait();
	}
	
	/** this method calculates and shows the average speed of one chick during tracked time */ 
	@FXML
	public void handleAverageVelocity() {
		int selectedChickIndex = chickChoice.getSelectionModel().getSelectedIndex();
		int aveSpeed =  (int) project.getCurrentProject().getAveSpeed(selectedChickIndex);
		String message = "Chick " + project.getCurrentProject().getTracks().get(selectedChickIndex).getID() +
				"travels with an average velocity of "+ aveSpeed;
		new Alert(AlertType.INFORMATION, message).showAndWait();
	}
}