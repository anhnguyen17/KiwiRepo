package project;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;

class ProjectDataTest {

	@BeforeAll
	static void initialize() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	ProjectData makeFakeData() throws FileNotFoundException {
		ProjectData project = new ProjectData("S:\\CLASS\\CS\\285\\sample_videos\\CircleTest1_no_overlap.mp4");
		AnimalTrack track1 = new AnimalTrack("chicken1");
		AnimalTrack track2 = new AnimalTrack("chicken2");
		project.getTracks().add(track1);
		project.getTracks().add(track2);

		track1.add(new TimePoint(100, 200, 0));
		track1.add(new TimePoint(105, 225, 30));
		track1.add(new TimePoint(105, 225, 30));
		track1.add(new TimePoint(115, 200, 90));

		track2.add(new TimePoint(300, 400, 90));
		track2.add(new TimePoint(290, 410, 110));
		return project;
	}
	
	@Test
	void testGetAveSpeed() throws FileNotFoundException {
		ProjectData proj = makeFakeData();
		
		assertEquals(17, (int) proj.getAveSpeed(0));
		assertEquals(21, (int) proj.getAveSpeed(1));
	}
	
	
	

	

}