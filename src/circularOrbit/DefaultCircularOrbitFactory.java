package circularOrbit;

import appliactions.AtomStructure;
import appliactions.SocialNetworkCircle;
import appliactions.StellarSystem;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class DefaultCircularOrbitFactory implements CircularOrbitFactory {
	@Override @Nullable
	public CircularOrbit CreateAndLoad(String loadFrom) throws IOException {
		CircularOrbit c = Create(loadFrom);
		if(c == null) return null;
		c.loadFromFile(loadFrom);
		assert c instanceof ConcreteCircularOrbit;
		((ConcreteCircularOrbit) c).checkRep();
		return c;
	}
	
	@Override @Nullable
	public CircularOrbit Create(String type) {
		if(type.contains("StellarSystem")) return new StellarSystem();
		else if(type.contains("AtomicStructure")) return new AtomStructure();
		else if(type.contains("SocialNetworkCircle")) return new SocialNetworkCircle();
		else return null;
	}
}
