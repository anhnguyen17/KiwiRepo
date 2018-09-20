package project;

import java.awt.*;
/**
 * 
 * @author thomasayele17
 *
 */
public class TimePoint {
	private Point point;

	public TimePoint(Point point, int frameNumber) {
		super();
		this.point = point;
		this.frameNumber = frameNumber;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public int getFrameNumber() {
		return frameNumber;
	}
	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}
	private int frameNumber; 


}
