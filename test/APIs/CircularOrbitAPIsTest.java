package APIs;

import applications.PhysicalObjectFactory;
import applications.StellarSystem;
import circularOrbit.CircularOrbit;
import circularOrbit.CircularOrbitFactory;
import circularOrbit.DefaultCircularOrbitFactory;
import circularOrbit.PhysicalObject;
import org.junit.Test;

import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;


public class CircularOrbitAPIsTest {
	private CircularOrbitFactory cf = new DefaultCircularOrbitFactory();
	private CircularOrbit s;
	private CircularOrbit a;
	
	public CircularOrbitAPIsTest(){
		s = cf.CreateAndLoad("input/StellarSystem.txt");
		a = cf.CreateAndLoad("input/AtomicStructure.txt");
	}
	
	@Test @SuppressWarnings("unchecked")
	public void getObjectDistributionEntropy() {
		CircularOrbit atom = cf.Create("AtomicStructure");
		var eargs = new String[]{"Electron", "1"};
		assert atom != null;
		atom.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eargs)));
		var _1 = CircularOrbitAPIs.getObjectDistributionEntropy(atom);
		atom.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eargs)));
		var _2 = CircularOrbitAPIs.getObjectDistributionEntropy(atom);
		eargs[1] = "2";
		atom.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eargs)));
		var _3 = CircularOrbitAPIs.getObjectDistributionEntropy(atom);
		assertTrue(_1 < _2);
		assertTrue(_2 < _3);
		
		CircularOrbit atom2 = cf.Create("AtomicStructure");
		assert atom2 != null;
		atom2.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eargs)));
		atom2.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eargs)));
		atom2.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eargs)));
		var _4 = CircularOrbitAPIs.getObjectDistributionEntropy(atom2);
		assertTrue(_4 < _3);
	}
	
	@Test @SuppressWarnings("unchecked")
	public void getLogicalDistance() {
		var c = cf.CreateAndLoad("input/SocialNetworkCircle.txt");
		assert c != null;
		var center = c.center();
		c.forEach(u->{
			assert u instanceof PhysicalObject;
			assertEquals(((PhysicalObject) u).getR().getRect_alt()[0].intValue(),
					CircularOrbitAPIs.getLogicalDistance(c, center, (PhysicalObject) u));
		});
		System.out.println("test " + c.size() + " objects. ");
	}
	
	@Test @SuppressWarnings("unchecked")
	public void getPhysicalDistance() {
		CircularOrbit c = new StellarSystem();
		var _3 = PhysicalObjectFactory.produce(
				new String[]{"Planet", "_3", "Solid", "color", "0", "3", "0", "CW", "0"});
		var _4 = PhysicalObjectFactory.produce(
				new String[]{"Planet", "_4", "Solid", "color", "0", "4", "0", "CW", "90"});
		assert _3 != null && _4 != null;
		c.addObject(_3);
		c.addObject(_4);
		assertEquals(5.0, CircularOrbitAPIs.getPhysicalDistance(c, _3, _4), 1E-4);
	}
	
	@Test @SuppressWarnings("unchecked")
	public void getDifference() {
		var d = CircularOrbitAPIs.getDifference(s, a);
		var dif1 = d.getOBJDif1();
		dif1.forEach(set ->{
			assert set instanceof Set;
			((Set) set).forEach(p->{
				assert p instanceof PhysicalObject;
				assertNotNull(s.query(((PhysicalObject) p).getName()));
			});
		});
		
		var dif2 = d.getOBJDif2();
		dif2.forEach(set ->{
			assert set instanceof Set;
			((Set) set).forEach(p->{
				assert p instanceof PhysicalObject;
				assertNotNull(a.query(((PhysicalObject) p).getName()));
			});
		});
	}
	
}