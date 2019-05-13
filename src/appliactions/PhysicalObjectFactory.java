package appliactions;

import circularOrbit.PhysicalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhysicalObjectFactory {
	static String[] hint_Electron = new String[]{"Radius"};
	static String[] hint_Planet = new String[]{"Name", "Form", "Color", "Planet radius",
			"Revolution radius", "Revolution speed", "Direction", "Position"};
	static String[] hint_User = new String[]{"Radius", "Name", "Age", "Gender"};
	
	@Nullable
	public static PhysicalObject produce(@NotNull String[] args){
		assert args.length > 0;
		try{
			switch(args[0]){
				case "Planet":
					assert args.length == 9;
					return new Planet(args[1], Enum.valueOf(Planet.Form.class, args[2]), args[3],
							Double.valueOf(args[4]), new double[]{Double.valueOf(args[5])}, Double.valueOf(args[6]),
							Enum.valueOf(Planet.Dir.class, args[7]), Float.valueOf(args[8]));
				case "Electron":
					assert args.length == 2;
					return new Electron(Float.valueOf(args[1]));
				case "User":
					assert args.length == 5;
					return new User(Double.valueOf(args[1]), args[2], Integer.valueOf(args[3]),
							Enum.valueOf(Gender.class, args[4]));
				case "CentralUser":
					assert args.length == 4;
					return new CentralUser(args[1], Integer.valueOf(args[2]), Enum.valueOf(Gender.class, args[3]));
				default: return null;
			}
		} catch (IllegalArgumentException e) {
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
