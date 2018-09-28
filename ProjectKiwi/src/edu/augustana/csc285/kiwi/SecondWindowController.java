package edu.augustana.csc285.kiwi;

	import java.io.File; 

	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
	import java.util.List;

	import org.opencv.core.Mat;
	import org.opencv.videoio.VideoCapture;

	import autotracking.AutoTrackListener;
	import autotracking.AutoTracker;
	import project.AnimalTrack;
	import project.ProjectData;
	import project.TimePoint;
	import project.Video;
	import javafx.application.Platform;
	import javafx.beans.value.ChangeListener;
	import javafx.beans.value.ObservableValue;
	import javafx.fxml.FXML;
	import javafx.scene.canvas.Canvas;
	import javafx.scene.control.Alert;
	import javafx.scene.control.Alert.AlertType;
	import javafx.scene.control.Button;
	import javafx.scene.control.Label;
	import javafx.scene.control.Slider;
	import javafx.scene.control.TextField;
	import javafx.scene.image.Image;
	import javafx.scene.image.ImageView;
	import javafx.scene.layout.Pane;
	import javafx.stage.FileChooser;
	import javafx.stage.Stage;
	import javafx.stage.Window;
	import utils.UtilsForOpenCV;

	public class SecondWindowController implements AutoTrackListener {

		@FXML private ImageView videoView;
		@FXML private Slider sliderVideoTime;
		@FXML private TextField textfieldStartFrame;
		@FXML private TextField textfieldEndFrame;
		@FXML private Button btnAutotrack;
		@FXML private Label startFrameLabel;
		private VideoCapture capture = new VideoCapture();
		
		//@FXML private ProgressBar progressAutoTrack;

		
		private AutoTracker autotracker;
		private ProjectData project;
		private Stage stage;
		
		@FXML public void initialize() {
		
	//}
			//FIXME: this quick loading of a specific file and specific settings 
			//       is for debugging purposes only, since there's no way to specify
			//       the settings in the GUI right now...
			//loadVideo("/home/forrest/data/shara_chicks_tracking/sample1.mp4");
			//loadVideo("S:/class/cs/285/sample_videos/sample1.mp4");		
			//project.getVideo().setXPixelsPerCm(6.5); //  these are just rough estimates!
			//project.getVideo().setYPixelsPerCm(6.7);

//			loadVideo("/home/forrest/data/shara_chicks_tracking/lowres/lowres2.avi");
			//loadVideo("S:/class/cs/285/sample_videos/lowres2.mp4");		
//			project.getVideo().setXPixelsPerCm(5.5); //  these are just rough estimates!
//			project.getVideo().setYPixelsPerCm(5.5);
			
			sliderVideoTime.valueProperty().addListener((obs, oldV, newV) -> showFrameAt(newV.intValue())); 
		}
		
		public void initializeWithStage(Stage stage) {
			this.stage = stage;
			
			// bind it so whenever the Scene changes width, the videoView matches it
			// (not perfect though... visual problems if the height gets too large.)
			videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());  
		}
		

		
		public void loadVideo(String filePath) {
			try {
				project = new ProjectData(filePath);
				Video video = project.getVideo();
				sliderVideoTime.setMax(video.getTotalNumFrames()-1);
				showFrameAt(0);
			} catch (FileNotFoundException e) {			
				e.printStackTrace();
			}

		}
		
		public void showFrameAt(int frameNum) {
			if (autotracker == null || !autotracker.isRunning()) {
				project.getVideo().setCurrentFrameNum(frameNum);
				Image curFrame = UtilsForOpenCV.matToJavaFXImage(project.getVideo().readFrame());
				videoView.setImage(curFrame);
				//textFieldCurFrameNum.setText(String.format("%05d",frameNum));
				
			}		
		}
		
		@FXML
		public void handleStartAutotracking() {
			if (autotracker == null || !autotracker.isRunning()) {
				Video video = project.getVideo();
				video.setStartFrameNum(Integer.parseInt(textfieldStartFrame.getText()));
				video.setEndFrameNum(Integer.parseInt(textfieldEndFrame.getText()));
				autotracker = new AutoTracker();
				// Use Observer Pattern to give autotracker a reference to this object, 
				// and call back to methods in this class to update progress.
				autotracker.addAutoTrackListener(this); 
				// this method will start a new thread to run AutoTracker in the background
				// so that we don't freeze up the main JavaFX UI thread.
				autotracker.startAnalysis(video);
				btnAutotrack.setText("CANCEL auto-tracking");
			} else {
				autotracker.cancelAnalysis();			
				btnAutotrack.setText("Start auto-tracking");
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
				sliderVideoTime.setValue(frameNumber);
				//textFieldCurFrameNum.setText(String.format("%05d",frameNumber));
			});		
		}

		@Override
		public void trackingComplete(List<AnimalTrack> trackedSegments) {
			project.getUnassignedSegments().clear();
			project.getUnassignedSegments().addAll(trackedSegments);

			for (AnimalTrack track: trackedSegments) {
				System.out.println(track);
//				System.out.println("  " + track.getPositions());
			}
			Platform.runLater(() -> { 
				//progressAutoTrack.setProgress(1.0);
				btnAutotrack.setText("Start auto-tracking");
			});	
			
		}
		
		public void setTxtStart(String num) {
			textfieldStartFrame.setText(num);
		}
		
	}


