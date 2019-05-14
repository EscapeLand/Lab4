package applications;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class StellarSystemTest {
	
	@Test
	public void loadFromFile() {
		StellarSystem s = new StellarSystem();
		assertTrue(s.loadFromFile("input/StellarSystem.txt"));
		for(Planet i: s){
			System.out.println(i.getName());
		}
	}
}