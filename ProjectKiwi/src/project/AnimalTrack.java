package project;

import java.util.ArrayList; 

import java.util.List;

import project.TimePoint;

public class AnimalTrack {
	private String animalID;
	
	private List<TimePoint> positions;
	
	public AnimalTrack(String id) {
		this.animalID = id;
		positions = new ArrayList<TimePoint>();
	}
	
	public void add(TimePoint pt) {
		positions.add(pt);
	}
	
	public TimePoint getTimePointAtIndex(int index) {
		return positions.get(index);
	}

	/**
	 * Returns the TimePoint at the specified time, or null
	 * @param frameNum
	 * @return
	 */
	
	public TimePoint getTimePointAtTime(int frameNum) {
		//TODO: This method's implementation is inefficient [linear search is O(N)]
		//      Replace this with binary search (O(log n)] or use a Map for fast access
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() == frameNum) {
				return pt;
			}
		}
		return null;
	}
	
	
	/**
	 * Adds auto-tracked segments to the currently selected chick
	 * Will overwrite any existing manual points! *Notify user in pop-up
	 */
	public void mergeAutoTracks(AnimalTrack track) {
		for(int x = 0; x < track.getTotalTimePoints(); x++) {
			double xVal = track.getTimePointAtIndex(x).getX();
			double yVal = track.getTimePointAtIndex(x).getY();
			int frameNum = track.getTimePointAtIndex(x).getFrameNum();
			setTimePointAtTime(xVal,yVal,frameNum);
		}
	}
	/**
	 * Create (or modify, if existing) a timepoint for the specified time & place.
	 */
	public void setTimePointAtTime(double x, double y, int frameNum) {
		TimePoint oldPt = getTimePointAtTime(frameNum);
		if (oldPt != null) {
			oldPt.setX(x);
			oldPt.setY(y);
		} else {
			add(new TimePoint(x, y, frameNum));
		}
	}
	
	/**
	 * 
	 * @param startFrameNum - the starting time (inclusive)
	 * @param endFrameNum   - the ending time (inclusive)
	 * @return all time points in that time interval
	 */
	public List<TimePoint> getTimePointsWithinInterval(int startFrameNum, int endFrameNum) {
		List<TimePoint> pointsInInterval = new ArrayList<>();
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() >= startFrameNum && pt.getFrameNum() <= endFrameNum) {
				pointsInInterval.add(pt);
			}
		}
		return pointsInInterval;
	}
	
	public TimePoint getFinalTimePoint() {
		return positions.get(positions.size()-1);
	}
	
	public String getID() {
		return this.animalID;
	}
	
	public String toString() {
		int startFrame = positions.get(0).getFrameNum();
		int endFrame = getFinalTimePoint().getFrameNum();
		return "AnimalTrack[id="+ animalID + ",numPts=" + positions.size()+" start=" + startFrame + " end=" + endFrame +"]"; 
	}
	
	public int getTotalTimePoints() {
		return positions.size();
		
	}
}