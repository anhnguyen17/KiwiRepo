package project;

import java.util.*;

import project.TimePoint;

/**
 * 
 * @author thomasayele17
 *
 */
public class AnimalTrack {
	public static final String UNNAMED_ID = "<<unassigned>>";
	private String animalID = UNNAMED_ID;
	
	private List<TimePoint> positions;

	public AnimalTrack() {
		positions = new ArrayList<TimePoint>();
	}
	
	public boolean hasIDAssigned() {
		return !animalID.equals(UNNAMED_ID);
	}
	
	public List<TimePoint> getPositions() {
		return positions;
	}
	
	public String toString() {
		return "AnimalTrack[id="+ animalID + ",len=" + positions.size()+"]"; 
	}
}
