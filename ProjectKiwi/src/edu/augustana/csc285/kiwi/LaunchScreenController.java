package edu.augustana.csc285.kiwi;

import javafx.application.Platform; 

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.Group;

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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.augustana.csc285.kiwi.SecondWindowController.*;

import org.opencv.imgproc.Imgproc;

public class LaunchScreenController {

	private VideoCapture capture = new VideoCapture();
	private int clearFrameNum;
	private String filePath;
	@FXML private ImageView videoView;
	@FXML private Slider sliderSeekBar;
	@FXML private Button browseButton;
	@FXML private Button submitButton;
	@FXML private BorderPane videoPane;
	
	
	
	@FXML
	public void initialize() {

		videoView.setOnMouseClicked(event ->{
			System.out.println("x = " + event.getX());
			System.out.println("y = " + event.getY());
			
			
						
			Circle dot = new Circle();
			dot.setCenterX(event.getX() + videoView.getLayoutX());
			dot.setCenterY(event.getY() + videoView.getLayoutY());
			dot.setRadius(5);
			dot.setFill(Color.RED);
			//add circle to scene
			videoPane.getChildren().add(dot);
				
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
		setFilePath(chosenFile.getAbsolutePath());

		if (chosenFile != null) {
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
	
	@FXML
	public void handleSubmit(ActionEvent event) throws IOException  {
		FXMLLoader FXMLloader = new FXMLLoader(getClass().getResource("SecondWindow.fxml"));
		Parent root1 = (Parent) FXMLloader.load();

		Stage stage = new Stage();
		stage.setScene(new Scene(root1));
		stage.show();
		
		
		
	}

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
		return clearFrameNum;
	}

	public void setClearFrameNum(double clearFrameNum) {
		this.clearFrameNum = (int) clearFrameNum;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

}
