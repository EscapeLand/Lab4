package applications;

import exceptions.ExceptionGroup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StellarSystemTest {
	
	@Test
	public void loadFromFile_error() {
		StellarSystem s = new StellarSystem();
		try{
			s.loadFromFile("NotExist.jpg");
		} catch (ExceptionGroup exceptions) {
			assertEquals(1, exceptions.size());
		}
	}
	
	@Test
	public void testRemoveObject(){
		StellarSystem s = new StellarSystem();
		s.loadFromFile("input/StellarSystem.txt");
		var e = (Planet) s.query("Earth");
		assert e != null;
		int i = s.getTracks().size();
		assertTrue(s.removeObject(e));
		assertEquals(i-1, s.getTracks().size());
	}
	
	@Test
	public void testNextTime(){
		StellarSystem s = new StellarSystem();
		Planet planet = new Planet("p", Planet.Form.Solid, "red", 1, new double[]{180}, Math.PI, Planet.Dir.CCW, 0);
		s.addObject(planet);
		s.setTimeSpan(1);
		s.nextTime();
		assertEquals(1.0, Math.toDegrees(planet.getPos()), 1e-5);
		s.reset();
		assertEquals(0, planet.getPos(), 0);
	}
}