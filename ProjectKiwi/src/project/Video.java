package project;

import java.awt.*;

/**
 * @author anhnguyen17
 *
 */

public class Video {
	private double frameRate;
	private double xPixelsPerCm;
	private double yPixelsPerCM;
	private int totalNumFrames;
	private String filePath;
	private int startFrameNum;
	private int endFrameNum;
	private Rectangle arenaBounds;
	
	public Video() {
		
	}
	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}

	public double getxPixelsPerCm() {
		return xPixelsPerCm;
	}

	public void setxPixelsPerCm(double xPixelsPerCm) {
		this.xPixelsPerCm = xPixelsPerCm;
	}

	public double getyPixelsPerCM() {
		return yPixelsPerCM;
	}

	public void setyPixelsPerCM(double yPixelsPerCM) {
		this.yPixelsPerCM = yPixelsPerCM;
	}

	public int getTotalNumFrames() {
		return totalNumFrames;
	}

	public void setTotalNumFrames(int totalNumFrames) {
		this.totalNumFrames = totalNumFrames;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getStartFrameNum() {
		return startFrameNum;
	}

	public void setStartFrameNum(int startFrameNum) {
		this.startFrameNum = startFrameNum;
	}

	public int getEndFrameNum() {
		return endFrameNum;
	}

	public void setEndFrameNum(int endFrameNum) {
		this.endFrameNum = endFrameNum;
	}

	public Rectangle getArenaBounds() {
		return arenaBounds;
	}

	public void setArenaBounds(Rectangle arenaBounds) {
		this.arenaBounds = arenaBounds;
	}

	

}
