package factory;

import APIs.ExceptionGroup;
import circularOrbit.CircularOrbit;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface CircularOrbitFactory {
	@Nullable
	public CircularOrbit CreateAndLoad(String loadFrom) throws ExceptionGroup;
	
	@Nullable
	public CircularOrbit Create(String type);
}
