package project;

import java.io.File;
import java.io.FileNotFoundException; 

import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		
		tempTrack = new AnimalTrack(names.get(chickNum));
		
		//should be adding the new AnimalTrack to project data tracks.
		tracks.add(tempTrack);
		System.out.println(tracks.get(chickNum).getID());
		//System.out.println(chickNum);
		//System.out.println(tracks.toString());
		//System.out.println("Made it");
	}
	
	public void saveToFile(File saveFile) throws FileNotFoundException {
		String json = toJSON();
		PrintWriter out = new PrintWriter(saveFile);
		out.print(json);
		out.close();
	}
	
	public String toJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();		
		return gson.toJson(this);
	}
	
	public static ProjectData loadFromFile(File loadFile) throws FileNotFoundException {
		String json = new Scanner(loadFile).useDelimiter("\\Z").next();
		return fromJSON(json);
	}
	
	public static ProjectData fromJSON(String jsonText) throws FileNotFoundException {
		Gson gson = new Gson();
		ProjectData data = gson.fromJson(jsonText, ProjectData.class);
		data.getVideo().connectVideoCapture();
		return data;
	}
}
