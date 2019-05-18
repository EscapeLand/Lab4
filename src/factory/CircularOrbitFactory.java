package factory;

import circularOrbit.CircularOrbit;
import exceptions.ExceptionGroup;
import org.jetbrains.annotations.Nullable;

public interface CircularOrbitFactory {
	@Nullable
	public CircularOrbit CreateAndLoad(String loadFrom) throws ExceptionGroup;
	
	@Nullable
	public CircularOrbit Create(String type);
}
