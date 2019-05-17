package applications;

import exceptions.ExceptionGroup;
import exceptions.GeneralLogger;
import exceptions.LogicErrorException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static applications.Gender.M;
import static org.junit.Assert.assertEquals;

public class SocialNetworkCircleTest {
	SocialNetworkCircle s = new SocialNetworkCircle();
	public SocialNetworkCircleTest() {
		s.loadFromFile("input/SocialNetworkCircle.txt");
	}
	
	@Test
	public void loadFromFile_error() {
		var ss = new SocialNetworkCircle();
		try{
			ss.loadFromFile("NotExist.jpg");
		} catch (ExceptionGroup exceptions) {
			assertEquals(1, exceptions.size());
		}
	}
	
	@Test
	public void addObject() {
		var u = new User(2.0, "zzs", 20, M);
		s.addObject(u);
		assertEquals(-1.0, u.getR().getRect()[0], 0);
		User Frank = (User) s.query("FrankLee");
		assert Frank != null;
		s.setRelation(Frank, u, 1);
		assertEquals(3.0, u.getR().getRect()[0], 0);
		s.removeObject(u);
	}
	
	@Test
	public void testRemoveObject() {
		SocialNetworkCircle c = new SocialNetworkCircle();
		c.loadFromFile("input/SocialNetworkCircle.txt");
		
		User Tom = (User) c.query("TomWong");
		User Frank = (User) c.query("FrankLee");
		assert Tom != null;
		assert Frank != null;
		c.removeObject(Tom);
		assertEquals(-1.0, Frank.getR().getRect()[0], 0);
	}
	
	@Test
	public void removeTrack() {
		SocialNetworkCircle c = new SocialNetworkCircle();
		c.loadFromFile("input/SocialNetworkCircle.txt");
		
		c.removeTrack(new double[]{1});
		User Frank = (User) c.query("FrankLee");
		assertEquals(-1.0, Frank.getR().getRect()[0], 0);
	}
	
	@Test
	public void testExtendVal(){
		var cls = s.getClass();
		var Tom = s.query("TomWong");
		var Lisa = s.query("LisaWong");
		assert Tom != null;
		assert Lisa != null;
		try {
			var mtd = cls.getDeclaredMethod("extendVal", User.class);
			mtd.setAccessible(true);
			var i = mtd.invoke(s, Tom);
			assertEquals(1, i);
			i = mtd.invoke(s, Lisa);
			assertEquals(0, i);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void temp(){
//		GeneralLogger.warning(new LogicErrorException("emmmm"));
//	}
}