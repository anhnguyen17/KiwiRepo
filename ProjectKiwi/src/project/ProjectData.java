package project;

import java.util.List;

public class ProjectData {
	
	private List<AnimalTrack> tracks;
	private Video video;
	
	public ProjectData() {
		
	}
	
	public List<AnimalTrack> getTracks() {
		return tracks;
	}
	public void setTracks(List<AnimalTrack> tracks) {
		this.tracks = tracks;
	}
	public Video getVideo() {
		return video;
	}
	public void setVideo(Video video) {
		this.video = video;
	}
	

}
