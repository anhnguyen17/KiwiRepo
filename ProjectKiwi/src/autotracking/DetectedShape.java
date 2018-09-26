package autotracking;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class DetectedShape {
	private MatOfPoint contour;
	private Moments moments;

	public DetectedShape(MatOfPoint contour) {
		this.contour = contour;
		// calculates some shape-related statistics about the contour
		this.moments = Imgproc.moments(contour);  
	}
	
	public double getCentroidX() {
		return moments.m10 / moments.m00;
	}
	public double getCentroidY() {
		return moments.m01 / moments.m00;
	}
	public Point getCentroidPoint() {
		return new Point(getCentroidX(), getCentroidY());
	}
	
	public double getArea() {
		return moments.m00;
	}

}
