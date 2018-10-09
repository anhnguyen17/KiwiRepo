package project;

import project.TimePoint;

public class TimePoint implements Comparable<TimePoint> {
	private double x;     // location
	private double y;      
	private int frameNum; // time (measured in frames)
	
	public TimePoint(double x, double y, int frameNum) {
		this.x = x;
		this.y = y;
		this.frameNum = frameNum;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
		
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public org.opencv.core.Point getPointOpenCV() {
		return new org.opencv.core.Point(x,y);
	}

	public java.awt.Point getPointAWT() {
		return new java.awt.Point((int)x,(int)y);
	}

	public int getFrameNum() {
		return frameNum;
	}

	@Override
	public String toString() {
		return String.format("(%.1f,%.1f@T=%d)",x,y,frameNum);
	}

	public double getDistanceTo(TimePoint other) {
		double dx = other.x-x;
		double dy = other.y-y;
		return Math.sqrt(dx*dx+dy*dy);
	}

	/**
	 * How many frames have passed since another TimePoint
	 * @param other - the otherTimePoint to compare with
	 * @return the difference (negative if the other TimePoint is later)
	 */
	public int getTimeDiffAfter(TimePoint other) {
		return this.frameNum - other.frameNum;
	}

	/**
	 * Comparison based on the time (frame number).
	 */
	@Override
	public int compareTo(TimePoint other) {		
		return this.getTimeDiffAfter(other);
	}
}

