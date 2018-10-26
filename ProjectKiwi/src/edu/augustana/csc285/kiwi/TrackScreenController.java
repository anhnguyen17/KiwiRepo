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
	private Button loadButton;
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
			// drawDot(tracks.get(x).getTimePointAtIndex(x).getX(),tracks.get(x).getTimePointAtIndex(x).getY());
			drawDot(tracks.getTimePointAtIndex(x).getX() + 150, tracks.getTimePointAtIndex(x).getY(), Color.WHITE);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void initializeAfterSceneCreated() {
		videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());
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
				System.out.println(project.getTracks().get(x));
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
			drawDot(x, y, c);
			selectedTrack.setTimePointAtTime(x, y, curFrameNum);
			System.out.println(selectedTrack); 
		} else {
			new Alert(AlertType.WARNING, "You must CHOOSE a chick first!").showAndWait();
		}
		
		jumpFrame(.5);
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
	}

	@FXML
	public void handleLoad() {
		loadVideo(getFilePath());
		for (int x = 0; x < chickNames.size(); x++) {
			String chickName = chickNames.get(x);
			project.getTracks().add(new AnimalTrack(chickName));
			
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
			// for(int x = 0; x< chickNames.size(); x++) {
			// project.addToTracks(x, chickNames);
			// }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void handleAutoTracking() {
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
			// textFieldCurFrameNum.setText(String.format("%05d",frameNumber));
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

		// ignore for now
		// for (int i = 0; i < chickNames.size(); i++) {
		// chickChoice.getItems().add(chickNames.get(i));
		// }
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