package edu.augustana.csc285.kiwi;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import project.AnimalTrack;
import project.ProjectData;
import project.Video;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;
import manualtracking.ManualTrack;

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

	private List<Circle> currentDots = new ArrayList<>();
	// add up to 10 colors
	private Color[] chickColors = new Color[] { Color.PURPLE, Color.AQUA, Color.YELLOW, Color.RED, Color.GREEN,
			Color.PINK, Color.WHITE, Color.GRAY };
	private double[] timeStep = new double[] { 0.5, 1, 2, 3, 5 };
	private double time = 1;
	private String filePath;
	private AutoTracker autotracker;
	private AnimalTrack animalTrack;
	private ManualTrack track;
	private ProjectData project;
	public ArrayList<String> chickNames = new ArrayList<String>();
	private Window stage;

	@FXML
	public void initialize() {
		
		for (int i = 0; i < timeStep.length; i++) {
			timeStepCb.getItems().add(timeStep[i]);
		}
		timeStepCb.getSelectionModel().selectFirst();
		availAutoChoiceBox.setOnAction(e -> drawAutoTracks(availAutoChoiceBox.getSelectionModel().getSelectedItem()));
	}

	public void drawAutoTracks(AnimalTrack tracks) {
		videoPane.getChildren().removeAll(currentDots);
		for (int x = 0; x < tracks.getTotalTimePoints(); x++) {
			double scalingRatio = getImageScalingRatio();
			drawDot(tracks.getTimePointAtIndex(x).getX() + sideBarPane.getWidth() + 15, tracks.getTimePointAtIndex(x).getY()+topBarPane.getHeight() / scalingRatio *2, Color.WHITE);
			//drawDot(tracks.getTimePointAtIndex(x).getX() + videoView.getLayoutX() / (scalingRatio *1.2)  , tracks.getTimePointAtIndex(x).getY() + videoView.getLayoutY() / (scalingRatio), Color.WHITE);
		}
	}
	
	private double getImageScalingRatio() {
		double widthRatio = videoPane.getWidth() / project.getVideo().getFrameWidth();
		double heightRatio = videoPane.getHeight() / project.getVideo().getFrameHeight();
		return Math.min(widthRatio, heightRatio);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void initializeAfterSceneCreated() {
		//videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());
		
		loadVideo(getFilePath());
		for (int x = 0; x < chickNames.size(); x++) {
			String chickName = chickNames.get(x);
			project.getTracks().add(new AnimalTrack(chickName));
		}
		
		System.out.println("done");
	}

	public void showFrameAt(int frameNum) {
		if (autotracker == null || !autotracker.isRunning()) {
			project.getVideo().setCurrentFrameNum(frameNum);
			Image curFrame = UtilsForOpenCV.matToJavaFXImage(project.getVideo().readFrame());
			videoView.setImage(curFrame);
		}
	}
	
	public void jumpFrame(double time) {
		int frameNum = project.getVideo().getCurFrameNum() + ((int) (30 * time));
		if (frameNum <= project.getVideo().getTotalNumFrames() && frameNum >= 0) {
			setTimeLabel(frameNum);
			
			showFrameAt((int) frameNum);
			sliderSeekBar.setValue((int) frameNum);
			project.getVideo().setCurFrameNum(frameNum);
		}
	}

	@FXML
	public void handleBackward() {
		videoPane.getChildren().removeAll(currentDots);
		time = -timeStep[timeStepCb.getSelectionModel().getSelectedIndex()];
		jumpFrame(time);
	}

	@FXML
	public void handleForward() {
		videoPane.getChildren().removeAll(currentDots);
		time = timeStep[timeStepCb.getSelectionModel().getSelectedIndex()];
		jumpFrame(time);
	}
	
	@FXML
	public void handleAutoTrackMerge() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Merging will overwrite any previous track points during the frames automatically tracked. ");
		alert.setContentText("Are you sure you wish to continue?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			int currentChick = chickChoice.getSelectionModel().getSelectedIndex();
			AnimalTrack temp = project.getTracks().get(currentChick);
			temp.mergeAutoTracks(availAutoChoiceBox.getSelectionModel().getSelectedItem());
			for (int x = 0; x < project.getTracks().size(); x++) {
				System.out.println(temp);
			}
			System.out.println("done");
		} else {
		   new Alert(AlertType.ERROR, "Merge Cancelled By User").showAndWait();
		}
	}
	
	@FXML
	public void handleSetEmptyFrame() {
		project.getVideo().setEmptyFrameNum(project.getVideo().getCurFrameNum());
		new Alert(AlertType.INFORMATION, "Success! The empty frame has been updated.").showAndWait();
	}
	
	public void mouseClick(MouseEvent event) {
		int selectedChickIndex = chickChoice.getSelectionModel().getSelectedIndex();
		if (selectedChickIndex >= 0) {
			AnimalTrack selectedTrack = project.getTracks().get(selectedChickIndex);
			int curFrameNum = (int) sliderSeekBar.getValue();
			Color c = chickColors[chickChoice.getSelectionModel().getSelectedIndex()];
			double x = event.getX() + videoView.getLayoutX();
			double y = event.getY() + videoView.getLayoutY();
			System.out.println(x + " y: " + y);
			drawDot(x, y, c);
			selectedTrack.setTimePointAtTime(x, y, curFrameNum);
			System.out.println(selectedTrack);
			
		} else {
			new Alert(AlertType.WARNING, "You must CHOOSE a chick first!").showAndWait();
		}
		
		//Changed to jump by the currently selected time interval instead of the previous value of .5
		jumpFrame(timeStepCb.getValue());
		chickChoice.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
		});
		// ManualTrack.trackPoint(null, time, time, 0);
	}

	public void drawDot(double x, double y, Color color) {
		Circle dot = new Circle();
		dot.setCenterX(x);
		dot.setCenterY(y);
		dot.setRadius(5);
		dot.setFill(color);
		currentDots.add(dot);
		// add circle to scene
		videoPane.getChildren().add(dot);
	}

	public void setChickNames(ArrayList<String> chickName) {
		this.chickNames = chickName;
		for (int i = 0; i < chickNames.size(); i++) {
			chickChoice.getItems().add(chickNames.get(i));
		}
		chickChoice.getSelectionModel().select(0);
	}
	
	@FXML
	public void addChick() {
		TextInputDialog dialog = new TextInputDialog("Enter Chick Name");
		 
		dialog.setTitle("Add new chick");
		dialog.setHeaderText("Enter chick Name");
		dialog.setContentText("Name:");
		 
		Optional<String> result = dialog.showAndWait();
		
		result.ifPresent(name -> {
			String temp = result.get();
			for(int x = 0; x < project.getTracks().size(); x++) {
				if(project.getTracks().get(x).getID().equals(temp)) {
					new Alert(AlertType.ERROR, "Chick with desired name already exists!").showAndWait();
					return;
				}
			}
		    project.getTracks().add(new AnimalTrack(temp));
		    chickChoice.getItems().add(temp);
		});
	}
	
	@FXML
	public void handleExport() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		File chosenFile = fileChooser.showOpenDialog(stage);
		if (chosenFile != null) {
			project.saveToFile(chosenFile); 
		} 
	}

	/*
	 * Removes the currently selected chick\
	 */
	public void removeChick() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Chick Removal");
		alert.setHeaderText("You are about to remove chick: " + 
				chickChoice.getSelectionModel().getSelectedItem() + ". This action cannot be undone. Are you sure you wish to continue?");
		alert.setContentText("Are you sure you wish to continue?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			project.removeChick(chickChoice.getSelectionModel().getSelectedItem());
			chickChoice.getItems().remove(chickChoice.getSelectionModel().getSelectedItem());
		} else {
		   new Alert(AlertType.ERROR, chickChoice.getSelectionModel().getSelectedItem() + " was not removed.").showAndWait();
		}
	}

	@FXML
	public void handleSlider() {
		videoPane.getChildren().removeAll(currentDots);
		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int frameNum = (int) (newValue.doubleValue() / sliderSeekBar.getMax()
						* project.getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT) - 1);
				showFrameAt(frameNum);
				setTimeLabel(frameNum);
				project.getVideo().setCurFrameNum(frameNum);
			}
		});
	}

	public void loadVideo(String filePath) {
		try {
			project = new ProjectData(filePath);
			Video video = project.getVideo();
			sliderSeekBar.setMax(video.getTotalNumFrames() - 1);
			showFrameAt(0);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void handleAutoTracking() {
		videoPane.getChildren().removeAll(currentDots);
		if (autotracker == null || !autotracker.isRunning()) {
			project.getVideo().setXPixelsPerCm(5.5);
			project.getVideo().setYPixelsPerCm(5.5);
			autotracker = new AutoTracker();
			// Use Observer Pattern to give autotracker a reference to this object,
			// and call back to methods in this class to update progress.
			autotracker.addAutoTrackListener(this);
			// this method will start a new thread to run AutoTracker in the background
			// so that we don't freeze up the main JavaFX UI thread.
			autotracker.startAnalysis(project.getVideo());
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
		project.getUnassignedSegments().clear();
		project.getUnassignedSegments().addAll(trackedSegments);
		for (AnimalTrack track : trackedSegments) {
			System.out.println(track);
			availAutoChoiceBox.getItems().add(track);
		}
		Platform.runLater(() -> {
			// progressAutoTrack.setProgress(1.0);
			submitButton.setText("Start auto-tracking");
		});
	}

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

	// this method allows us to set the start and end frame for the auto tracking.
	@FXML
	public void handleFrame() {
		if (frameBtn.getText().equals("Start Time")) {
			project.getVideo().setStartFrameNum(project.getVideo().getCurFrameNum());
			frameBtn.setText("End Time");
			instructionLabel.setText("Select your prefered end time:");
		} else {
			project.getVideo().setEndFrameNum(project.getVideo().getCurFrameNum());
			frameBtn.setText("Start Time");
			instructionLabel.setText("Select your prefered start time:");
		}
	}
}