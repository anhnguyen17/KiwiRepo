package manualtracking;

import java.util.List;

import project.AnimalTrack;
import project.ProjectData;
import project.TimePoint;

public class ManualTrack {
	private List<TimePoint> timepoint;
	private List<AnimalTrack> tracks;
	private ProjectData data;
	
	public void trackPoint(AnimalTrack currentChick, double xCord, double yCord, int frameNum) {
		System.out.println(data.getTracks());
	}
}
