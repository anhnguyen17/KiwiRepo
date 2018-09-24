package project;
import java.awt.*;

/**
 * @author anhnguyen17
 *
 */

public class Video {
	private double xPixelsPerCm;
	private double yPixelsPerCm;
	private String fileName;
	private int startFrameNum;
	private int endFrameNum;
	private Rectangle arenaBounds; 
	private double frameNum;
	
	public Video(String fileName) {
		super();
		this.fileName = fileName;
		
	}
	

	public double getxPixelsPerCm() {
		return xPixelsPerCm;
	}


	public void setxPixelsPerCm(double xPixelsPerCm) {
		this.xPixelsPerCm = xPixelsPerCm;
	}


	public double getyPixelsPerCm() {
		return yPixelsPerCm;
	}


	public void setyPixelsPerCm(double yPixelsPerCm) {
		this.yPixelsPerCm = yPixelsPerCm;
	}

	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
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