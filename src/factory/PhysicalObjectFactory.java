package factory;

import circularOrbit.PhysicalObject;
import exceptions.GeneralLogger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class PhysicalObjectFactory {
	
	@NotNull @SuppressWarnings("unchecked")
	public static PhysicalObject produce(Class cls, @NotNull String[] args) throws IllegalArgumentException{
		assert cls.getPackageName().equals("applications");
		
		try{
			var ctor = cls.getDeclaredConstructors()[0];
			ctor.setAccessible(true);
			assert ctor.getParameterTypes().length == args.length;
			switch(cls.getSimpleName()){
				case "Planet":
					var ty = ctor.getParameterTypes();
					return (PhysicalObject) ctor.newInstance(args[0], Enum.valueOf((Class<Enum>)ty[1], args[1]), args[2],
							Double.valueOf(args[3]), new double[]{Double.valueOf(args[4])}, Double.valueOf(args[5]),
							Enum.valueOf((Class<Enum>)ty[6], args[6]), Float.valueOf(args[7]));
				case "Electron":
					return (PhysicalObject) ctor.newInstance(Float.valueOf(args[0]));
				case "User":{
					//unused
					var em = (Class<Enum>) Class.forName("applications.Gender");
					return (PhysicalObject) ctor.newInstance(Double.valueOf(args[0]), args[1], Integer.valueOf(args[2]),
							Enum.valueOf(em, args[3]));
				}
				case "CentralUser":{
					var em = (Class<Enum>) Class.forName("applications.Gender");
					return (PhysicalObject) ctor.newInstance(args[0], Integer.valueOf(args[1]), Enum.valueOf(em, args[2]));
				}
				default: throw new IllegalArgumentException("unknown Class: " + cls.getSimpleName() + ". continued. ");
			}
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
			GeneralLogger.severe(e);
			System.exit(1);
			return null;
		}
	}
}
