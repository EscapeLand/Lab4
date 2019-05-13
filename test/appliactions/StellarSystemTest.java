package appliactions;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class StellarSystemTest {
	
	@Test
	public void loadFromFile() {
		StellarSystem s = new StellarSystem();
		try {
			assertTrue(s.loadFromFile("input/StellarSystem.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Planet i: s){
			System.out.println(i.getName());
		}
	}
}