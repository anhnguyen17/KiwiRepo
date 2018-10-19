package project;

import java.io.FileNotFoundException; 
import java.util.ArrayList;
import java.util.List;

public class ProjectData {
	private Video video;
	private List<AnimalTrack> tracks;
	private List<AnimalTrack> unassignedSegments;
	
	public ProjectData(String videoFilePath) throws FileNotFoundException {
		video = new Video(videoFilePath);
		tracks = new ArrayList<>();
		unassignedSegments = new ArrayList<>();
	}

	public Video getVideo() {
		return video;
	}
	
	public List<AnimalTrack> getTracks() {
		return tracks;
	}

	public List<AnimalTrack> getUnassignedSegments() {
		return unassignedSegments;
	}
	
	public void addToTracks(int chickNum, ArrayList<String> names) {
		AnimalTrack tempTrack = null;
		
		//see if getting names correctly: Checked
		//System.out.println(names.get(chickNum));
		
		//creates a new temporary track and sets the name to the name of the text field
		tempTrack = new AnimalTrack(names.get(chickNum));
		
		//adds the track to the project data tracks.
		tracks.add(tempTrack);
		System.out.println(tracks.get(chickNum).getID());
		//System.out.println(tracks.toString());

	
}
}
