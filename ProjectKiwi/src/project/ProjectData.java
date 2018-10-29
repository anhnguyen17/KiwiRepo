package project;

import java.io.File;
import java.io.FileNotFoundException; 

import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

public class ProjectData {
	private Video video;
	private List<AnimalTrack> tracks;
	private List<AnimalTrack> unassignedSegments;
	private static ProjectData currentProject;

	private ProjectData(String videoFilePath) throws FileNotFoundException {
		video = new Video(videoFilePath);
		tracks = new ArrayList<>();
		unassignedSegments = new ArrayList<>();
	}

	public static void openCurrentProject(String file) throws FileNotFoundException {
		currentProject = new ProjectData(file);
	}

	public static ProjectData getCurrentProject() {
		return currentProject;
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

	/**
	 * @param chickNum
	 * @param names
	 */
	public void addToTracks(int chickNum, ArrayList<String> names) {
		AnimalTrack tempTrack = null;

		//see if getting names correctly: Checked
		//System.out.println(names.get(chickNum));

		tempTrack = new AnimalTrack(names.get(chickNum));

		tracks.add(tempTrack);
	}

	/** This method removes all the information including the tracks of the selected chick
	 * @param chickToRemove represents the ID of the chick to be removed
	 */
	public void removeChick(String chickToRemove) {
		for(int x = 0; x < tracks.size(); x++) {
			if(tracks.get(x).getID().equals(chickToRemove)){
				tracks.remove(x);
				return;
			}
		}
	}

	/** This method give the average speed of the selected chick after tracking process
	 * @param chickNum represents the index number of a chick
	 * @return the average speed of that chick
	 */
	public double getAveSpeed(int chickNum) {
		double distance =  getTracks().get(chickNum).getTotalDistance();
		int numFramesTracked = getTracks().get(chickNum).getTotalNumFrames();
		double timeTracked =  getVideo().convertFrameNumsToSeconds(numFramesTracked);
		return (int) (distance / timeTracked);
	}

	/**
	 * This method returns the unassigned segment that contains a TimePoint (between
	 * startFrame and endFrame) that is closest to the given x,y location
	 * 
	 * @param x          - x coordinate to search near
	 * @param y          - y coordinate to search near
	 * @param startFrame - (inclusive)
	 * @param endFrame   - (inclusive)
	 * @return the unassigned segment (AnimalTrack) that contained the nearest point
	 *         within the given time interval, or *null* if there is NO unassigned
	 *         segment that contains any TimePoints within the given range.
	 */
	public AnimalTrack getNearestUnassignedSegment(double x, double y, int startFrame, int endFrame) {
		double minDistance = Double.POSITIVE_INFINITY;
		AnimalTrack nearest = null;
		for (AnimalTrack segment : unassignedSegments) {
			List<TimePoint> ptsInInterval = segment.getTimePointsWithinInterval(startFrame, endFrame);
			for (TimePoint pt : ptsInInterval) {
				double dist = pt.getDistanceTo(x, y);
				if (dist < minDistance) {
					minDistance = dist;
					nearest = segment;
				}
			}
		}
		return nearest;
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
		@SuppressWarnings("resource")
		String json = new Scanner(loadFile).useDelimiter("\\Z").next();
		return fromJSON(json);
	}

	public static ProjectData fromJSON(String jsonText) throws FileNotFoundException {
		Gson gson = new Gson();
		ProjectData data = gson.fromJson(jsonText, ProjectData.class);
		data.getVideo().connectVideoCapture();
		return data;
	}


	/**
	 * Helper method used when exporting Time Points to CSV file.
	 * @param saveFile
	 * @throws FileNotFoundException
	 */
	public void exportTimePointsToCSV(File saveFile) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(saveFile);
		out.print(" Chick Name, Time (in seconds), X-location, Y-location");
		out.println();
		for (AnimalTrack assignedtracks: tracks) {
			for(TimePoint point: assignedtracks) {
				out.print(assignedtracks.getID()+ ", "+ String.format("%.0f", (video.convertFrameNumsToSeconds(point.getFrameNum()))));
				out.print(", "+ point.getX() + video.getOriginPoint().getX()/video.getXPixelsPerCm());
				out.print(", "+ point.getY() + video.getOriginPoint().getY()/video.getYPixelsPerCm() );
				out.println();
			}
		}
		out.close();
	}
	/**
	 * Helper method used when exporting average velocity to CSV file.
	 * @param saveFile
	 * @throws FileNotFoundException
	 */
	public void exportAverageVelocity(File saveFile) throws FileNotFoundException {
		PrintWriter out  = new PrintWriter(saveFile);
		DecimalFormat df = new DecimalFormat("#.##");
		out.print("Chick Name, Average Velocity");
		out.println();
		if(tracks.size() > 1) {
			for(int i = 0; i < tracks.size(); i ++) {
				out.print(tracks.get(i).getID()); 
				out.print("," + df.format(getAveSpeed(i)));
				out.println();
			}
		}
		out.close();
	}
}
