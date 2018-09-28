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

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import edu.augustana.csc285.kiwi.SecondWindowController.*;

import org.opencv.imgproc.Imgproc;

public class LaunchScreenController implements AutoTrackListener {

	private VideoCapture capture = new VideoCapture();
	private int startFrameNum;
	private String filePath = "";
	@FXML private ImageView videoView;
	@FXML private Slider sliderSeekBar;
	@FXML private Button browseButton;
	@FXML private Button submitButton;
	@FXML private BorderPane videoPane;
	@FXML private ChoiceBox<String> chickChoice;
	@FXML private AnchorPane trackPane;
	
	private List<Circle> currentDots = new ArrayList<>(); 
	
	private AutoTracker autotracker;
	private ProjectData project;
	private Stage stage;
	private int colorChoice =0;

	

	@FXML
	public void initialize() {
		
		chickChoice.getItems().add("Chick 1");
		chickChoice.getItems().add("Chick 2");
		chickChoice.getItems().add("Chick 3");
		
		Color[] color = new Color[] {Color.RED, Color.AQUA, Color.YELLOW};
		

		videoView.setOnMouseClicked(event ->{
			System.out.println("x = " + event.getX());
			System.out.println("y = " + event.getY());
			
			Circle dot = new Circle();
			dot.setCenterX(event.getX() + videoView.getLayoutX());
			dot.setCenterY(event.getY() + videoView.getLayoutY());
			dot.setRadius(5);
			dot.setFill(color[chickChoice.getSelectionModel().getSelectedIndex()]);
			currentDots.add(dot);
			//add circle to scene
			videoPane.getChildren().add(dot);	
		});
		
		chickChoice.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
				System.out.println("dropdown chose: " + newValue.intValue());
			});
	}
	
	public void initializeAfterSceneCreated() {
		videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());
		
	}
	
	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = videoView.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		

		if (chosenFile != null) {
			this.filePath = chosenFile.getAbsolutePath();
			System.out.println(filePath);
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
	}
	
	/*Code to move to second window
	 * @FXML 
	public void handleSubmit(ActionEvent event) throws IOException  {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SecondWindow.fxml"));
		
		BorderPane root = (BorderPane)loader.load();
		SecondWindowController nextController = loader.getController();
		//nextController.loadVideo("/S:/CLASS/CS/285/sample_videos/sample1.mp4"); 
		nextController.setTxtStart("01010101");
		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) submitButton.getScene().getWindow();
		primary.setScene(nextScene);
	}*/

	public void handleSlider() {

		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (capture.isOpened()) {
					int frameNum = (int) (newValue.doubleValue() / sliderSeekBar.getMax()
							* capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT) - 1);
					
					capture.set(Videoio.CAP_PROP_POS_FRAMES, frameNum);
					setClearFrameNum(frameNum);
					Mat frame = grabFrame();
					Image currentImage = mat2Image(frame);

					Platform.runLater(new Runnable() {
						public void run() {
							videoView.setImage(currentImage);
						}

					});
				}
			}

		});
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

	public double getClearFrameNum() {
		return startFrameNum;
	}

	public void setClearFrameNum(double clearFrameNum) {
		this.startFrameNum = (int) clearFrameNum;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	// this method will get called repeatedly by the Autotracker after it analyzes each frame
	@Override
	public void handleTrackedFrame(Mat frame, int frameNumber, double fractionComplete) {
		Image imgFrame = UtilsForOpenCV.matToJavaFXImage(frame);
		// this method is being run by the AutoTracker's thread, so we must
		// ask the JavaFX UI thread to update some visual properties
		Platform.runLater(() -> { 
			videoView.setImage(imgFrame);
			//progressAutoTrack.setProgress(fractionComplete);
			sliderSeekBar.setValue(frameNumber);
			//textFieldCurFrameNum.setText(String.format("%05d",frameNumber));
		});		
	}

	@Override
	public void trackingComplete(List<AnimalTrack> trackedSegments) {
		project.getUnassignedSegments().clear();
		project.getUnassignedSegments().addAll(trackedSegments);

		for (AnimalTrack track: trackedSegments) {
			System.out.println(track);
		}
		Platform.runLater(() -> { 
			//progressAutoTrack.setProgress(1.0);
			submitButton.setText("Start auto-tracking");
		});	
		
	}
	

}
