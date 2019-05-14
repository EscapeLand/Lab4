package applications;

import org.junit.Test;

import java.io.IOException;

public class SocialNetworkCircleTest {
	SocialNetworkCircle s = new SocialNetworkCircle();
	public SocialNetworkCircleTest() {
		s.loadFromFile("input/SocialNetworkCircle.txt");
	}
	
	@Test
	public void loadFromFile() {
		System.out.println(s.getGraph());
	}
}