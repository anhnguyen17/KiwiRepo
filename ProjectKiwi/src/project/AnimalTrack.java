package project;

import java.util.*;
/**
 * 
 * @author thomasayele17
 *
 */
public class AnimalTrack {
	private String animalID;

	public AnimalTrack(String animalID, List<TimePoint> positions) {
		super();
		this.animalID = animalID;
		this.positions = positions;
	}
	public String getAnimalID() {
		return animalID;
	}
	public void setAnimalID(String animalID) {
		this.animalID = animalID;
	}
	public List<TimePoint> getPositions() {
		return positions;
	}
	public void setPositions(List<TimePoint> positions) {
		this.positions = positions;
	}
	private List <TimePoint> positions;

}
