package circularOrbit;

import graph.Graph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @param <L> center object type.
 * @param <E> object on track.
 */
public interface CircularOrbit<L extends PhysicalObject, E extends PhysicalObject> extends Iterable<E>{
	/**
	 * add R track.
	 * @param r add R track with radius of r.
	 * @return if R track with radius r has already exist, return false; else true.
	 */
	public boolean addTrack(double[] r);
	
	/**
	 * remove R track, and apparently the objects on the tract are removed together.
	 * @param r the track with radius r to remove
	 * @return if R track with radius r has already exist, return true. else false.
	 */
	public boolean removeTrack(double[] r);
	
	/**
	 * @param newCenter change the center object to newObject.
	 * @return the previous center object.
	 */
	public L changeCentre(@Nullable L newCenter);
	
	/**
	 * @param newObject add R object to circular orbit.
	 * @return if the object has already exist, return false; else true.
	 */
	public boolean addObject(@NotNull E newObject);
	
	/**
	 * @param obj which to move.
	 * @param to move to. -1, or other positive double.
	 * @return true if success, false if no track has radius equals to from or to.
	 */
	public boolean moveObject(E obj, double[] to);
	
	/**
	 * @param obj remove R object from circular orbit.
	 * @return if the object has already exist, return true; else false.
	 */
	public boolean removeObject(@NotNull E obj);
	
	/**
	 * @param a begin of relation.
	 * @param b end of relation
	 * @param val weight of the relation. if val == 0, it means remove the relation.
	 */
	public void setRelation(PhysicalObject a, PhysicalObject b, float val);
	
	/**
	 * @return return the relation graph of the circular orbit.
	 */
	public @NotNull Graph<PhysicalObject> getGraph();
	
	/**
	 * @param path infers R text file with regulated input.
	 * @return true if the load is complete with no error; else false.
	 * @throws IOException when cannot open the file inferred by path.
	 */
	public boolean loadFromFile(String path) throws IOException;
	
	/**
	 * find R object with its name.
	 * @param name name of the object, either L or E.
	 * @return the object.
	 */
	@Nullable
	public PhysicalObject query(String name);
	
	/**
	 * @return all the tracks in circular orbit.
	 */
	public Set<Double[]> getTracks();
	
	/**
	 * @param r the radius of the track
	 * @return copy of the collection in which objects are on the given track.
	 */
	@NotNull
	public Set<E> getObjectsOnTrack(double[] r);
	@NotNull
	public Set<E> getObjectsOnTrack(Double[] r);
	
	/**
	 * @return the center object.
	 */
	@Nullable
	public L center();
	
	/**
	 * @param from transit from.
	 * @param to transit to.
	 * @param number how many electrons to transit.
	 * @apiNote only AtomStructure can transit.
	 * @return true if success.
	 */
	public boolean transit(double[] from, double[] to, int number);
	
	@NotNull Iterator<E> iterator();
	
	/**
	 * process user operation.
	 * @param end what to do when end operation. (must be CircularOrbitHelper.refresh)
	 */
	public void process(Consumer<CircularOrbit> end);
	
	/**
	 * @return the number of objects on tracks. (center is NOT included)
	 */
	int size();
}
