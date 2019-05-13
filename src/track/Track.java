package track;

import circularOrbit.PhysicalObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * Immutable.
 * represents a track in the circular orbit.
 * @param <E> the type of the objects on the track. (astonished)
 */
public class Track<E extends PhysicalObject> {
	/*
		RI: R[0] >= R[1]; R.length == 2;
		AF: AF(R[0], R[1]) = the orbit, maybe a perfect circle or a ellipse.
	 */
	private final double[] R;
	/**
	 * the default comparator of two tracks.
	 */
	static public final Comparator<Track> defaultComparator = (a, b) -> Arrays.compare(a.R, b.R);
	
	/**
	 * @param R the radius. no larger than 2 elements.
	 */
	public Track(double[] R){
		switch (R.length){
			case 1: this.R = new double[]{R[0], R[0]}; break;
			case 2:
				var max = Math.max(R[0], R[1]);
				var min = Math.min(R[0], R[1]);
				this.R = new double[]{max, min};
				break;
			default: throw new IllegalArgumentException("length of R: " + R.length);
		}
	}
	
	/**
	 * @param R the radius. no larger than 2 elements.
	 */
	public Track(Double[] R){
		switch (R.length){
			case 1: this.R = new double[]{R[0], R[0]}; break;
			case 2:
				var max = Math.max(R[0], R[1]);
				var min = Math.min(R[0], R[1]);
				this.R = new double[]{max, min};
				break;
			default: throw new IllegalArgumentException("length of R: " + R.length);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Track)) return false;
		Track<?> track = (Track<?>) o;
		return Arrays.equals(track.R, R);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(R[0], R[1]);
	}
	
	@Override
	public String toString() {
		if(R[0] == R[1]) return String.valueOf(R[1]);
		else return Arrays.toString(R);
	}
	
	
	/**
	 * @param a one track
	 * @param b another track
	 * @return a negative integer, zero, or a positive integer as the first argument is less than,
	 * equal to, or greater than the second.
	 * @see Comparator#compare(Object, Object)
	 */
	public static int compare(Track a, Track b){
		return defaultComparator.compare(a, b);
	}
	
	/**
	 * @return radius of the track, length == 2. ({long half shaft, short half shaft})
	 */
	public double[] getRect() {
		return R.clone();
	}
	
	/**
	 * @return radius of the track, length == 2. ({long half shaft, short half shaft})
	 */
	public Double[] getRect_alt(){
		return new Double[]{R[0], R[1]};
	}
}