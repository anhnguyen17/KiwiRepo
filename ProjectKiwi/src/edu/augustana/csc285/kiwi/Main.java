package edu.augustana.csc285.kiwi;
	
import org.opencv.core.Core; 
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
<<<<<<< HEAD
			
=======
>>>>>>> 9d7408a2f55b6f859a4d8ad66951de6be91681a6
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LaunchScreen.fxml"));
			BorderPane root = (BorderPane)loader.load();
			
			Scene scene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			LaunchScreenController controller = loader.getController();
			controller.initializeAfterSceneCreated();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}
