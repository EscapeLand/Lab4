package applications;

import exceptions.ExceptionGroup;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class AtomStructureTest {
	private AtomStructure a = new AtomStructure();
	private AtomStructure am = new AtomStructure();
	private final Class<AtomStructure> cls = AtomStructure.class;
	
	public AtomStructureTest(){
		a.loadFromFile("input/AtomicStructure.txt");
		am.loadFromFile("input/AtomicStructure_Medium.txt");
	}
	
	@Test
	public void testTransit() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		var transit = cls.getDeclaredMethod("transit", double[].class, double[].class, int.class);
		transit.setAccessible(true);
		var _1 = new double[]{1};
		var _2 = new double[]{2};
		assertEquals(2, a.getObjectsOnTrack(_1).size());
		assertTrue((Boolean) transit.invoke(a, _1, _2, 2));
		assertTrue(a.getObjectsOnTrack(_1).isEmpty());
		assertEquals(10, a.getObjectsOnTrack(_2).size());
		assertFalse((Boolean) transit.invoke(a, _1, _2, 1));
		assertTrue((Boolean) transit.invoke(a, _2, _1, 2));
		assertEquals(2, a.getObjectsOnTrack(_1).size());
		assertEquals(8, a.getObjectsOnTrack(_2).size());
		assertFalse((Boolean) transit.invoke(a, _2, _1, 1));
	}
	
	@Test
	public void checkRep() {
		a.checkRep();
	}
	
	@Test
	public void loadFromFile_error() {
		var f = new AtomStructure();
		try{
			f.loadFromFile("input/NotExist.jpg");
		} catch (ExceptionGroup exceptions) {
			assertFalse(exceptions.isEmpty());
		}
	}
	
	@Test
	public void loadFromFile_exception(){
		var f = new AtomStructure();
		try{
			f.loadFromFile("input/AtomicStructure_overload.txt");
		} catch (ExceptionGroup exceptions) {
			assertFalse(exceptions.isEmpty());
		}
		
		var err = new AtomStructure();
		try{
			err.loadFromFile("input/AtomicStructure_error.txt");
		} catch (ExceptionGroup exceptions) {
			assertFalse(exceptions.isEmpty());
			System.out.println(exceptions.size());
		}
	}
}