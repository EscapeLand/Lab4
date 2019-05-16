package factory;

import circularOrbit.PhysicalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhysicalObjectFactory {
	@Nullable @SuppressWarnings("unchecked")
	public static PhysicalObject produce(@NotNull String[] args){
		assert args.length > 0;
		try{
			var cls = Class.forName("applications." + args[0]);
			var ctor = cls.getDeclaredConstructors()[0];
			ctor.setAccessible(true);
			assert ctor.getParameterTypes().length == args.length - 1;
			switch(args[0]){
				case "Planet":
					var ty = ctor.getParameterTypes();
					return (PhysicalObject) ctor.newInstance(args[1], Enum.valueOf((Class<Enum>)ty[1], args[2]), args[3],
							Double.valueOf(args[4]), new double[]{Double.valueOf(args[5])}, Double.valueOf(args[6]),
							Enum.valueOf((Class<Enum>)ty[6], args[7]), Float.valueOf(args[8]));
				case "Electron":
					return (PhysicalObject) ctor.newInstance(Float.valueOf(args[1]));
				case "User":{
					//unused
					var em = (Class<Enum>) Class.forName("applications.Gender");
					return (PhysicalObject) ctor.newInstance(Double.valueOf(args[1]), args[2], Integer.valueOf(args[3]),
							Enum.valueOf(em, args[4]));
				}
				case "CentralUser":{
					var em = (Class<Enum>) Class.forName("applications.Gender");
					return (PhysicalObject) ctor.newInstance(args[1], Integer.valueOf(args[2]), Enum.valueOf(em, args[3]));
				}
				default: return null;
			}
		} catch (Exception e) {
			System.out.println("warning: " + e.getMessage());
			System.exit(1);
			return null;
		}
	}
	
	public static String[] insert_copy(String[] arr, String elm, int pos){
		assert pos <= arr.length + 1;
		String[] r = new String[arr.length + 1];
		int i = 0;
		for(; i < pos; i++) r[i] = arr[i];
		r[i++] = elm;
		for(;i < r.length; i++) r[i] = arr[i - 1];
		return r;
	}
	
}
