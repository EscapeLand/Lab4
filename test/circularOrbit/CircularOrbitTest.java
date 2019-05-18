package circularOrbit;

import factory.CircularOrbitFactory;
import factory.DefaultCircularOrbitFactory;
import factory.PhysicalObjectFactory;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class CircularOrbitTest {
	private CircularOrbitFactory cf = new DefaultCircularOrbitFactory();
	
	@Test @SuppressWarnings("unchecked")
	public void testAddAndRemove() throws ClassNotFoundException {
		var eargs = new String[]{"1"};
		CircularOrbit c = cf.Create("AtomicStructure");
		var ecls = Class.forName("applications.Electron");
		var e = PhysicalObjectFactory.produce(ecls, eargs);
		assert c != null;
		c.addObject(e);
		assertEquals(1, c.size());
		eargs[0] = "2";
		assertTrue(c.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(ecls, eargs))));
		assertEquals(2, c.size());
		assertTrue(c.removeObject(e));
		assertEquals(1, c.size());
		assertFalse(c.removeObject(Objects.requireNonNull(PhysicalObjectFactory.produce(ecls, eargs))));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testGetTrackAndObjectOnTrack(){
		var b = cf.CreateAndLoad("input/AtomicStructure.txt");
		assert b != null;
		Set<Double[]> bt = b.getTracks();
		assertEquals(5, bt.size());
		int[] test = new int[]{2, 8, 18, 8, 1};
		for (int i = 0; i < test.length; i++) {
			assertEquals(test[i], b.getObjectsOnTrack(new double[]{i + 1}).size());
		}
		
		var a = cf.CreateAndLoad("input/AtomicStructure_Medium.txt");
		assert a != null;
		bt = a.getTracks();
		assertEquals(6, bt.size());
		test = new int[]{2, 8, 18, 30, 8, 2};
		for (int i = 0; i < test.length; i++) {
			assertEquals(test[i], a.getObjectsOnTrack(new double[]{i + 1}).size());
		}
		
	}
	
	@Test
	public void testQuery(){
		var c = cf.CreateAndLoad("input/StellarSystem.txt");
		assert c != null;
		assertNotNull(c.query("Earth"));
		assertNotNull(c.query("Sun"));
		assertNull(c.query(""));
		
	}
	
	@Test
	public void testRemoveTrack(){
		var c = cf.CreateAndLoad("input/AtomicStructure.txt");
		assert c != null;
		assertEquals(37, c.size());
		assertTrue(c.removeTrack(new double[]{1}));
		assertEquals(35, c.size());
		assertFalse(c.removeTrack(new double[]{8}));
		assertEquals(35, c.size());
		
	}
	
	@Test
	public void testAddTrack(){
		var c = cf.Create("AtomicStructure");
		assert c != null;
		assertEquals(0, c.getTracks().size());
		assertTrue(c.addTrack(new double[]{1}));
		assertEquals(1, c.getTracks().size());
		assertFalse(c.addTrack(new double[]{1}));
		assertEquals(1, c.getTracks().size());
		try{
			c.addTrack(new double[]{-3});
		}catch (IllegalArgumentException ex){
			assertEquals("warning: r cannot be negative while not equal to -1. ", ex.getMessage());
		}
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testMoveObject(){
		var c = cf.CreateAndLoad("input/AtomicStructure.txt");
		assert c != null;
		var s_1 = c.getObjectsOnTrack(new double[]{1});
		assertEquals(2, s_1.size());
		PhysicalObject e = (PhysicalObject) s_1.iterator().next();
		var s_5 = c.getObjectsOnTrack(new double[]{5});
		assertEquals(1, s_5.size());
		c.moveObject(e, new double[]{5});
		s_1 = c.getObjectsOnTrack(new double[]{1});
		s_5 = c.getObjectsOnTrack(new double[]{5});
		assertEquals(1, s_1.size());
		assertEquals(2, s_5.size());
	}
	
	@Test
	public void testSetRelationship(){
		var c = cf.CreateAndLoad("input/SocialNetworkCircle.txt");
		assert c != null;
		var a = c.query("TommyWong");
		var b = c.query("LisaWong");
		var graph = c.getGraph();
		c.setRelation(a, b, 0);
		assert b != null;
		assertEquals(-1, b.getR().getRect()[0], 0);
	}
	
	@Test
	public void testToString(){
		var a = cf.Create("AtomicStructure");
		assert a != null;
		assertEquals("AtomStructure", a.toString());
		var s = cf.Create("StellarSystem");
		assert s != null;
		assertEquals("StellarSystem", s.toString());
		var sn = cf.Create("SocialNetworkCircle");
		assert sn != null;
		assertEquals("SocialNetworkCircle", sn.toString());
	}
	
	@Test
	public void testFindTrackAndClearEmptyTrack()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		var a = cf.Create("AtomicStructure");
		assert a != null;
		var sucls = a.getClass().getSuperclass();
		var _1 = new double[]{1};
		var _2 = new double[]{2};
		a.addTrack(_1);
		a.addTrack(_2);
		assertEquals(2, a.getTracks().size());
		var mtd1 = sucls.getDeclaredMethod("findTrack", double[].class);
		mtd1.setAccessible(true);
		assertTrue((Boolean) mtd1.invoke(a, _1));
		var mtd2 = sucls.getDeclaredMethod("clearEmptyTrack");
		mtd2.setAccessible(true);
		mtd2.invoke(a);
		assertEquals(0, a.getTracks().size());
		assertFalse((Boolean) mtd1.invoke(a, _2));
	}
}