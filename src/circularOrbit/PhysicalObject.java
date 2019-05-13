package circularOrbit;

import track.Track;

import java.util.Comparator;
import java.util.Objects;

/**
 * Mutable.
 * represents the objects on an orbit (or as a center).
 */
public abstract class PhysicalObject {
	/*
		RI: none.
		AF: AF(name, R, pos) = an object at now time .
			AF(name, R_init, pos_init) = an object when its initialed.
	 */
	private final String name;
	protected final Track R_init;
	protected final double pos_init;
	private Track R;
	private double pos;
	/**
	 * how many objects with random pos have been created.
	 */
	private static int num = 0;
	
	/**
	 * @param name name of the object
	 * @param r the radius of its track.
	 * @param pos the pos of which the object take on the orbit.
	 */
	public PhysicalObject(String name, double[] r, double pos) {
		this.name = name;
		this.R_init = this.R = new Track(r);
		this.pos_init = this.pos = pos;
	}
	
	/**
	 * generate a object with random pos.
	 * @param name name of the object
	 * @param r the radius of its track.
	 */
	public PhysicalObject(String name, double[] r) {
		this(name, r, num < 9 ? 40 * num + 40 * Math.random()
				: 360 * Math.random());
		num++;
	}
	
	public final Track getR() {
		return R;
	}
	
	/**
	 * @param r radius of the new track.
	 */
	public void setR(double[] r) {
		setR(new Track(r));
	}
	
	/**
	 * @param r radius of the new track.
	 */
	public void setR(Double[] r) {
		setR(new Track(r));
	}
	
	/**
	 * @param r radius of the new track.
	 */
	public void setR(Track r) { R = r; }
	
	public final double getPos() {
		return pos;
	}
	
	public void setPos(double pos) { this.pos = pos; }
	
	public String getName(){
		return name;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + '{' + R +
				", " + pos + '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PhysicalObject)) return false;
		PhysicalObject that = (PhysicalObject) o;
		return Double.compare(that.pos_init, pos_init) == 0 &&
				getName().equals(that.getName()) &&
				R_init.equals(that.R_init);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getName(), R_init, pos_init);
	}
	
	@Override
	public abstract PhysicalObject clone();
	
	/**
	 * @return a comparator of two physical objects.
	 * @see Double#compare(double, double)
	 * @see Track#compare(Track, Track)
	 */
	public static Comparator<PhysicalObject> getDefaultComparator(){
		return (o1, o2) -> {
			var r = Track.compare(o1.R_init, o2.R_init);
			if(r == 0) return Double.compare(o1.pos_init, o2.pos_init);
			else return r;
		};
	}
}

