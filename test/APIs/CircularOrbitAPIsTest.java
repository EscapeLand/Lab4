package APIs;

import applications.StellarSystem;
import circularOrbit.CircularOrbit;
import circularOrbit.PhysicalObject;
import factory.CircularOrbitFactory;
import factory.DefaultCircularOrbitFactory;
import factory.PhysicalObjectFactory;
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
	public void getObjectDistributionEntropy() throws ClassNotFoundException {
		CircularOrbit atom = cf.Create("AtomicStructure");
		var eArgs = new String[]{"1"};
		var eCls = Class.forName("applications.Electron");
		assert atom != null;
		atom.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eCls, eArgs)));
		var _1 = CircularOrbitAPIs.getObjectDistributionEntropy(atom);
		atom.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eCls, eArgs)));
		var _2 = CircularOrbitAPIs.getObjectDistributionEntropy(atom);
		eArgs[0] = "2";
		atom.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eCls, eArgs)));
		var _3 = CircularOrbitAPIs.getObjectDistributionEntropy(atom);
		assertEquals(_1, _2, 0.0);
		assertTrue(_2 < _3);
		
		CircularOrbit atom2 = cf.Create("AtomicStructure");
		assert atom2 != null;
		atom2.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eCls, eArgs)));
		atom2.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eCls, eArgs)));
		atom2.addObject(Objects.requireNonNull(PhysicalObjectFactory.produce(eCls, eArgs)));
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
		//System.out.println("test " + c.size() + " objects. ");
	}
	
	@Test @SuppressWarnings("unchecked")
	public void getPhysicalDistance() throws ClassNotFoundException {
		CircularOrbit c = new StellarSystem();
		var pcls = Class.forName("applications.Planet");
		var _3 = PhysicalObjectFactory.produce(pcls,
				new String[]{"_3", "Solid", "color", "0", "3", "0", "CW", "0"});
		var _4 = PhysicalObjectFactory.produce(Class.forName("applications.Planet"),
				new String[]{"_4", "Solid", "color", "0", "4", "0", "CW", "90"});
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