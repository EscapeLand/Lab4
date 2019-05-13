package appliactions;

import org.junit.Test;

import java.io.IOException;

public class SocialNetworkCircleTest {
	SocialNetworkCircle s = new SocialNetworkCircle();
	public SocialNetworkCircleTest() {
		try {
			s.loadFromFile("input/SocialNetworkCircle.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void loadFromFile() {
		System.out.println(s.getGraph());
	}
}