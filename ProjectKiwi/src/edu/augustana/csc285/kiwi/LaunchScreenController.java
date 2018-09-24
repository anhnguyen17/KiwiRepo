package edu.augustana.csc285.kiwi;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgproc.Imgproc;

public class LaunchScreenController {

	private VideoCapture capture = new VideoCapture();
	private double clearFrameNum;
	@FXML private ImageView videoView;
	@FXML private Slider sliderSeekBar;
	@FXML private Button BrowseButton;
	@FXML private Button SubmitButton;

	@FXML
	public void initialize() {
	}
	
	public void handleSubmit() {
		
	}

	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = videoView.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);

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

	public void handleSlider() {

		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (capture.isOpened()) {
					double frameNum = newValue.doubleValue() / sliderSeekBar.getMax()
							* capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT) - 1;
					
					capture.set(Videoio.CAP_PROP_POS_FRAMES, frameNum);
					setClearFrameNum(frameNum);
					System.out.println(frameNum);
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
		this.clearFrameNum = clearFrameNum;
	}

}
