package edu.augustana.csc285.kiwi;

import javafx.application.Platform; 

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import project.AnimalTrack;
import project.ProjectData;
import project.TimePoint;
import project.Video;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import project.AnimalTrack;
import project.Video;
import utils.UtilsForOpenCV;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import autotracking.AutoTrackListener;
import autotracking.AutoTracker;

import org.opencv.imgproc.Imgproc;

public class TrackScreenController implements AutoTrackListener {

	private VideoCapture capture = new VideoCapture();
	private int startFrameNum;
	private String filePath = "";
	@FXML
	private ImageView videoView;
	@FXML
	private Slider sliderSeekBar;
	@FXML
	private Button browseButton;
	@FXML
	private Button submitButton;
	@FXML
	private Button backwardBtn;
	@FXML
	private Button forwardBtn;
	@FXML
	private Button playVideoButton;
	@FXML
	private BorderPane videoPane;
	@FXML
	private ChoiceBox<String> chickChoice;
	@FXML
	private AnchorPane trackPane;
	@FXML
	private Label choiceBoxLabel;
	@FXML
	private Label timeLabel;

	private List<Circle> currentDots = new ArrayList<>();
	private Color[] color = new Color[] { Color.PURPLE, Color.AQUA, Color.YELLOW };

	private AutoTracker autotracker;
	private ProjectData project;
	private Stage stage;
	private int colorChoice = 0;
	private ScheduledExecutorService timer;
	public ArrayList<String> chickNames = new ArrayList<String>();

	@FXML
	public void initialize() {
	}

	public void handleBackward() {
		
		videoPane.getChildren().removeAll(currentDots);
		double curFrameNum = getClearFrameNum() - 30;
		capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		setFrameNum(getClearFrameNum() - 30);

		int minute = (int) (curFrameNum / 30) / 60;
		int second = (int) (curFrameNum / 30) - minute * 60;
		String time = "";
		if (second < 10) {
			time = "0" + minute + ":" + "0" + second;
		} else {
			time = "0" + minute + ":" + second;
		}
		timeLabel.setText(time);
		Mat frame = grabFrame();
		Image currentImage = mat2Image(frame);

		Platform.runLater(new Runnable() {
			public void run() {
				videoView.setImage(currentImage);
			}

		});

	}

	public void handleForward() {
		videoPane.getChildren().removeAll(currentDots);
		double curFrameNum = getClearFrameNum() + 30;

		capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		setFrameNum(getClearFrameNum() + 30);

		int minute = (int) (curFrameNum / 30) / 60;
		int second = (int) (curFrameNum / 30) - minute * 60;
		String time = "";
		if (second < 10) {
			time = "0" + minute + ":" + "0" + second;
		} else {
			time = "0" + minute + ":" + second;
		}
		timeLabel.setText(time);
		Mat frame = grabFrame();
		Image currentImage = mat2Image(frame);

		Platform.runLater(new Runnable() {
			public void run() {
				videoView.setImage(currentImage);
			}

		});
		handleSlider();
	}

	public void drawDot(MouseEvent event) {
		Color[] color = new Color[] { Color.BLACK ,Color.PURPLE, Color.AQUA, Color.YELLOW };
		System.out.println("x = " + event.getX());
		System.out.println("y = " + event.getY());

		// TimePoint pt = new TimePoint(event.getX(), event.getY(),
		// project.getVideo().getCurrentFrameNum());

		try {
			Circle dot = new Circle();
			dot.setCenterX(event.getX() + videoView.getLayoutX());
			dot.setCenterY(event.getY() + videoView.getLayoutY());
			dot.setRadius(5);
			dot.setFill(color[chickChoice.getSelectionModel().getSelectedIndex()+1]);
			currentDots.add(dot);
			// add circle to scene
			videoPane.getChildren().add(dot);
		} catch (Exception e) {
			System.err.println("Choose a chick");

		}

		chickChoice.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {

		});

		// project.getTracks().add(chick1);
		// project.getTracks().add(chick2);
		// project.getTracks().add(chick3);
	}

	public void setChickNames(ArrayList<String> chickName) {
		this.chickNames = chickName;
		for (int i = 0; i < chickNames.size(); i++) {
			chickChoice.getItems().add(chickNames.get(i));
		}

	}

	public void initializeAfterSceneCreated() {
		videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());

	}

	// Code to calibrate
	/*public void handleCalibration(MouseEvent event) {
	
		Circle dot1 = new Circle();
		dot1.setCenterX(event.getX() + videoView.getLayoutX());
		dot1.setCenterY(event.getY() + videoView.getLayoutY());
		dot1.setRadius(5);
		dot1.setFill(Color.WHITE);
		// add circle to scene
		videoPane.getChildren().add(dot1);
		
		Circle dot2 = new Circle();
		dot2.setCenterX(event.getX() + videoView.getLayoutX());
		dot2.setCenterY(event.getY() + videoView.getLayoutY());
		dot2.setRadius(5);
		dot2.setFill(Color.WHITE);
		// add circle to scene
		videoPane.getChildren().add(dot1);
		
	}*/

	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = videoView.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);

		if (chosenFile != null) {
			this.filePath = chosenFile.getAbsolutePath();
			project = new ProjectData(filePath);
			capture.open(chosenFile.getAbsolutePath());
			Mat frame = grabFrame();
			if (frame.width() != 0) {

				Image imageToShow = mat2Image(frame);
				videoView.setImage(imageToShow);

			} else {
				capture.release();
			}
		}

		AnimalTrack chick1 = new AnimalTrack(chickNames.get(0));
		AnimalTrack chick2 = new AnimalTrack(chickNames.get(1));
		AnimalTrack chick3 = new AnimalTrack(chickNames.get(2));

		project.getTracks().add(0, chick1);
		project.getTracks().add(1, chick2);
		project.getTracks().add(2, chick3);
	}

	public void handleSlider() {

		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (capture.isOpened()) {
					int frameNum = (int) (newValue.doubleValue() / sliderSeekBar.getMax()
							* capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT) - 1);

					capture.set(Videoio.CAP_PROP_POS_FRAMES, frameNum);

					int minute = (int) (frameNum / 30) / 60;
					int second = (int) (frameNum / 30) - minute * 60;
					String time = "";
					if (second < 10) {
						time = "0" + minute + ":" + "0" + second;
					} else {
						time = "0" + minute + ":" + second;
					}
					timeLabel.setText(time);

					setFrameNum(frameNum);
					Mat frame = grabFrame();
					Image currentImage = mat2Image(frame);

					videoPane.getChildren().removeAll(currentDots);

					Platform.runLater(new Runnable() {
						public void run() {
							videoView.setImage(currentImage);
						}

					});
				}
			}

		});
	}

	@FXML
	public void handleSubmit() {
		if (autotracker == null || !autotracker.isRunning()) {
			project.getVideo().setXPixelsPerCm(5.5);
			project.getVideo().setYPixelsPerCm(5.5);
			project.getVideo().setStartFrameNum(startFrameNum);
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
		}
		Platform.runLater(() -> {
			// progressAutoTrack.setProgress(1.0);
			submitButton.setText("Start auto-tracking");
		});

	}

	public double getClearFrameNum() {
		return startFrameNum;
	}

	public void setFrameNum(double clearFrameNum) {
		this.startFrameNum = (int) clearFrameNum;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private Mat grabFrame() {
		// init everything
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
				// read the current frame
				this.capture.read(frame);

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return frame;
	}

	public static Image mat2Image(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		} catch (Exception e) {
			System.err.println("Cannot convert the Mat obejct: ");
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage matToBufferedImage(Mat original) {
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

}
