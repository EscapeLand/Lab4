package applications;

import org.junit.Test;

import java.io.IOException;

public class AtomStructureTest {
	AtomStructure a = new AtomStructure();
	AtomStructure am = new AtomStructure();
	
	public AtomStructureTest(){
		a.loadFromFile("input/AtomicStructure.txt");
		am.loadFromFile("input/AtomicStructure_Medium.txt");
	}
	
	@Test
	public void loadFromFile() {
		a.forEach(x->System.out.println(x.getName() + x.getR()));
	}
}