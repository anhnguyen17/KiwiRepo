package autotracking;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SingleFrameShapeFinder {
	
	private Mat emptyBackgroundFrame;
	private double brightnessThreshold;
	private double minDetectedShapeArea;
	private double maxDetectedShapeArea;

	private Mat visualizationFrame;

	/**
	 * 
	 * @param emptyBackgroundFrame - the "empty" background frame to compare later frames to
	 * @param brightnessThreshold - how different from the background must the shape's color be? (0-255)
	 * @param minDetectedShapeArea - how large (in pixels squared) must the shape be?
	 */
	public SingleFrameShapeFinder(Mat emptyBackgroundFrame, double brightnessThreshold, double minDetectedShapeArea, double maxDetectedShapeArea) {
		this.emptyBackgroundFrame = emptyBackgroundFrame;
		this.brightnessThreshold = brightnessThreshold;
		this.minDetectedShapeArea = minDetectedShapeArea;
		this.maxDetectedShapeArea = maxDetectedShapeArea;
	}
	
	public List<DetectedShape> findShapes(Mat matFrame) {
		List<DetectedShape> shapes = new ArrayList<>();
		
		Mat diffFrame = new Mat(), grayDiff = new Mat(), bwMask = new Mat(), erodedMask = new Mat();
		Core.absdiff(emptyBackgroundFrame, matFrame, diffFrame);
		
		Imgproc.cvtColor(diffFrame, grayDiff, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(grayDiff, grayDiff, new Size(5,5));
		Imgproc.threshold(grayDiff, bwMask, brightnessThreshold, 255, Imgproc.THRESH_BINARY);
		Imgproc.erode(bwMask, erodedMask, new Mat(), new Point(-1,-1), 5);



		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();		
		Imgproc.findContours(erodedMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


		visualizationFrame = diffFrame;
		for (int i = 0; i < contours.size(); i++) {
			
			DetectedShape shape = new DetectedShape(contours.get(i));
			
			Scalar contourColor = new Scalar(0,255,0); // green
			if (shape.getArea() >= minDetectedShapeArea && shape.getArea() <= maxDetectedShapeArea) {
				shapes.add(shape);

				final Scalar RED = new Scalar(0,0,255);
				Imgproc.circle(visualizationFrame, shape.getCentroidPoint(), 5, RED, -1);
			} else {
				contourColor = new Scalar(255,0,0);
			}
			Imgproc.drawContours( visualizationFrame, contours, i, contourColor, 2, 8, hierarchy, 0, new Point() );				
		}
		
		return shapes;
	}

	public Mat getVisualizationFrame() {
		return visualizationFrame;
	}
		
	

}
