package circularOrbit;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface CircularOrbitFactory {
	@Nullable
	public CircularOrbit CreateAndLoad(String loadFrom) throws IOException;
	@Nullable
	public CircularOrbit Create(String type);
}
