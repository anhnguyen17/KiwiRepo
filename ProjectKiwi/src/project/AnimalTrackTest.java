package project;

import static org.junit.jupiter.api.Assertions.*; 
import static project.AnimalTrack.*; 
import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.Test;

class AnimalTrackTest {
	
	AnimalTrack track1 = new AnimalTrack("chick1");
	TimePoint pt1 = new TimePoint(0,0,10);
	TimePoint pt2 = new TimePoint(50,75,50);
	TimePoint pt3 = new TimePoint(100,300,120);
	TimePoint pt4 = new TimePoint(200,200,150);

	@Test
	void test() {
		track1.add(pt1);
		track1.add(pt2);
		track1.add(pt3);
		track1.add(pt4);
		
		assertEquals(pt4, track1.getTimePointAtTime(150));
		assertEquals(pt3, track1.getTimePointAtTime(120));
		assertEquals(null, track1.getTimePointAtTime(110));
		assertEquals(pt2, track1.getTimePointAtTime(50));
		assertEquals(pt1, track1.getTimePointAtTime(10));
		
		
		assertEquals(pt4, track1.getFinalTimePoint());
		
		assertEquals(pt4, track1.getTimePointAtIndex(3)); 
		assertEquals(pt1, track1.getTimePointAtIndex(0));
		
		
	}

}
