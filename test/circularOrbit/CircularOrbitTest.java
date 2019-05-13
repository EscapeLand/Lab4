package circularOrbit;

import appliactions.PhysicalObjectFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

public class CircularOrbitTest {
	private CircularOrbitFactory cf = new DefaultCircularOrbitFactory();
	
	@Test
	public void testAddAndRemove(){
		var eargs = new String[]{"Electron", "1"};
		CircularOrbit c = cf.Create("AtomicStructure");
		var e = PhysicalObjectFactory.produce(eargs);
		c.addObject(e);
		assertEquals(1, c.size());
		eargs[1] = "2";
		assertTrue(c.addObject(PhysicalObjectFactory.produce(eargs)));
		assertEquals(2, c.size());
		assertTrue(c.removeObject(e));
		assertEquals(1, c.size());
		assertFalse(c.removeObject(PhysicalObjectFactory.produce(eargs)));
	}
	
	@Test
	public void testGetTrackAndObjectOnTrack(){
		try {
			var b = cf.CreateAndLoad("input/AtomicStructure.txt");
			Set<Double[]> bt = b.getTracks();
			assertEquals(5, bt.size());
			int[] test = new int[]{2, 8, 18, 8, 1};
			for (int i = 0; i < test.length; i++) {
				assertEquals(test[i], b.getObjectsOnTrack(new double[]{i + 1}).size());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			var a = cf.CreateAndLoad("input/AtomicStructure_Medium.txt");
			Set<Double[]> bt = a.getTracks();
			assertEquals(6, bt.size());
			int[] test = new int[]{2, 8, 18, 30, 8, 2};
			for (int i = 0; i < test.length; i++) {
				assertEquals(test[i], a.getObjectsOnTrack(new double[]{i + 1}).size());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Test
	public void testQuery(){
		try {
			var c = cf.CreateAndLoad("input/StellarSystem.txt");
			assertNotNull(c.query("Earth"));
			assertNotNull(c.query("Sun"));
			assertNull(c.query(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testRemoveTrack(){
		try {
			var c = cf.CreateAndLoad("input/AtomicStructure.txt");
			assertEquals(37, c.size());
			assertTrue(c.removeTrack(new double[]{1}));
			assertEquals(35, c.size());
			assertFalse(c.removeTrack(new double[]{8}));
			assertEquals(35, c.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAddTrack(){
		var c = cf.Create("AtomicStructure");
		assertEquals(0, c.getTracks().size());
		assertTrue(c.addTrack(new double[]{1}));
		assertEquals(1, c.getTracks().size());
		assertFalse(c.addTrack(new double[]{1}));
		assertEquals(1, c.getTracks().size());
	}
}