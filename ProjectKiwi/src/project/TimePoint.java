package project;

import java.awt.*;

/**
 * 
 * @author thomasayele17
 *
 */
public class TimePoint {
	private Point point;
	private int frameNum; // time (measured in frames)

	public TimePoint(int x, int y, int frameNum) {
		point = new Point(x, y);
		this.frameNum = frameNum;
	}

	public int getX() {
		return point.x;
	}
	
	public int getY() {
		return point.y;
	}

	public int getFrameNum() {
		return frameNum;
	}

	public String toString() {
		return "("+point.x+","+point.y+"@T="+frameNum +")";
	}

	public double getDistanceTo(TimePoint other) {
		return point.distance(other.point);
	}
	
}
