package circularOrbit;

import APIs.CircularOrbitHelper;
import graph.Graph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import track.Track;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

import static APIs.CircularOrbitAPIs.*;
import static APIs.CircularOrbitHelper.alert;
import static APIs.CircularOrbitHelper.generatePanel;
import static circularOrbit.PhysicalObject.getDefaultComparator;
import static exceptions.GeneralLogger.info;
import static factory.PhysicalObjectFactory.produce;

/**
 * Mutable.
 * an implement of CircularOrbit.
 * @param <L> the type of the center.
 * @param <E> type of the objects on object
 */
public abstract class ConcreteCircularOrbit<L extends PhysicalObject, E extends PhysicalObject>
		implements CircularOrbit<L, E>
{
	/*
		RI: for each object, its track is in the set tracks;
		AF: AF(relationship) = the relationship graph of the objects.
			AF(centre) = the center of the circular orbit.
			AF(objects) = the sum of the objects on the orbit.
	 */
	private Graph<PhysicalObject> relationship = Graph.empty();
	private L centre = null;
	
	protected Set<E> objects = new TreeSet<>(getDefaultComparator());
	protected Set<Track> tracks = new HashSet<>();
	private Class<E> ECLASS;
	@SuppressWarnings({"unused"})
	private Class<L> LCLASS;
	
	protected ConcreteCircularOrbit(Class<L> LCLASS, Class<E> ECLASS) {
		this.LCLASS = LCLASS;
		this.ECLASS = ECLASS;
	}
	
	@Override
	public boolean addTrack(double[] r) throws IllegalArgumentException{
		assert r.length > 0;
		if(r[0] < 0 && r[0] != -1)
			throw new IllegalArgumentException("warning: r cannot be negative while not equal to -1. ");
		info("addTrack", new String[]{Arrays.toString(r)});
		return tracks.add(new Track<>(r));
	}
	
	@Override
	public boolean removeTrack(double[] r){
		Track<E> tmp = new Track<>(r);
		var b = tracks.remove(tmp);
		var it = objects.iterator();
		while(it.hasNext()){
			var e = it.next();
			if(e.getR().equals(tmp)) {
				assert b;
				relationship.remove(e);
				it.remove();
			}
		}
		info("removeTrack", new String[]{Arrays.toString(r)});
		return b;
	}
	
	@Override
	public L changeCentre(L newCenter){
		if(Objects.equals(centre, newCenter)) return centre;
		info("changeCentre", new String[]{newCenter.toString()});
		L prev = centre;
		centre = newCenter;
		return prev;
	}
	
	/**
	 * test if a track is exist in the circular orbit.
	 * @param r the radius of the orbit.
	 * @return true if the track exist.
	 */
	protected boolean findTrack(double[] r){
		var tmp = new Track(r);
		return tracks.contains(tmp);
	}
	
	@Override
	public boolean moveObject(E obj, double[] to) {
		if(!objects.contains(obj)) return false;
		var tmp = new Track<>(to);
		obj.setR(tmp);
		addTrack(to);
		
		info("moveObject", new String[]{obj.toString(), Arrays.toString(to)});
		return true;
	}
	
	@Override
	public boolean removeObject(@NotNull E obj){
		info("removeObject", new String[]{obj.toString()});
		relationship.remove(obj);
		return objects.remove(obj);
	}
	
	@Override
	public void setRelation(@NotNull PhysicalObject a, @NotNull PhysicalObject b, float val){
		info("setRelation", new String[]{a.toString(), b.toString(), String.valueOf(val)});
		assert !a.equals(b);
		relationship.add(a);
		relationship.add(b);
		relationship.set(a, b, val);
	}
	
	@NotNull @Override
	public Graph<PhysicalObject> getGraph(){
		return relationship;
	}
	
	@Override @Nullable
	public PhysicalObject query(String objName){
		info("query", new String[]{objName});
		final String name = objName.trim();
		if(centre.getName().equals(name)) return centre;
		return find_if(objects, e->e.getName().equals(objName));
	}
	
	@Override
	public L center() {
		return centre;
	}
	
	@Override
	public Set<Double[]> getTracks() {
		Set<Double[]> ret = new TreeSet<>(Comparator.comparingDouble((Double[] a) -> a[0]).thenComparingDouble(a -> a[1]));
		transform(tracks, ret, Track::getRect_alt);
		return ret;
	}
	
	@Override
	public JFrame process(Consumer<CircularOrbit> end) {
		JFrame frame = new JFrame(getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(null);
		return frame;
	}
	
	/**
	 * add User Interface controls on the frame.
	 * @param frame where to add controls.
	 * @param end what to do at the end of each action.
	 * @return a panel that includes the control added.
	 */
	protected JPanel test(JFrame frame, Consumer<CircularOrbit> end){
		
		JPanel common = new JPanel();
		common.setLayout(new FlowLayout(FlowLayout.CENTER, 336, 8));
		common.setBorder(BorderFactory.createLineBorder(Color.decode("#673ab7"), 1, true));
		frame.add(common);
		
		JPanel trackOP = generatePanel("Track Operation");
		JPanel objOP = generatePanel("Object Operation");
		JPanel misc = generatePanel("Miscellaneous");
		var ops = new String[]{"Add", "Remove"};
		
		JComboBox<String> cmbOps = new JComboBox<>(ops);
		JTextField trackNum = new JTextField("  1");
		JButton trackExec = new JButton("Execute");
		
		trackExec.addActionListener(e -> {
			double d; try{
				d = Double.valueOf(trackNum.getText().trim());
			} catch (NumberFormatException ex) {
				trackNum.setText("  1");
				return;
			}
			switch (cmbOps.getSelectedIndex()){
				case 0:
					addTrack(new double[]{d});
					checkRep();
					break;
				case 1:
					removeTrack(new double[]{d});
					break;
			}
			end.accept(this);
		});
		
		trackOP.add(cmbOps); trackOP.add(trackNum); trackOP.add(trackExec);
		common.add(trackOP);
		
		
		JComboBox<String> objOps = new JComboBox<>(ops);
		Set<Track> tmp = new TreeSet<>(Track.defaultComparator);
		transform(getTracks(), tmp, Track::new);
		JComboBox<Track> objTidx = new JComboBox<>(tmp.toArray(new Track[0]));
		objTidx.setEditable(true);
		JButton objExec = new JButton("Execute");

		objExec.addActionListener(e -> {
			switch(objOps.getSelectedIndex()){
				case 0:
				{
					var form = CircularOrbitHelper.promptForm(frame, "Add object", E.hintForUser(ECLASS));
					if(form == null) return;
					var p = produce(ECLASS, form);
					addObject(ECLASS.cast(p));
					checkRep();                 //post condition
					break;
				}
				case 1:
					var p = objTidx.getSelectedItem();
					if(!(p instanceof Track)) {
						objTidx.setSelectedIndex(0);
						return;
					}
					Track r = (Track) p;
					String name = CircularOrbitHelper.prompt(frame, "name of the object",
							"Which object to remove? ", null);
					if(name == null) return;
					E o = find_if(objects, i->i.getR().equals(r) && i.getName().equals(name));
					if(o == null) {
						alert(frame, "Delete object", name + "do not exist. ");
						return;
					}
					removeObject(o);
					break;
				default: return;
			}
			end.accept(this);
		});
		
		objOP.add(objOps); objOP.add(objTidx); objOP.add(objExec);
		common.add(objOP);
		
		
		JButton btnent = new JButton("Calculate Entropy");
		JButton btnLog = new JButton("Log Panel");
		JLabel lblrst = new JLabel("");
		btnent.addActionListener(e-> lblrst.setText(String.valueOf(getObjectDistributionEntropy(this))));
		btnLog.addActionListener(e->{
			var logp = CircularOrbitHelper.logPanel(frame);
			CircularOrbitHelper.frame.setVisible(false);
			logp.setVisible(true);
			CircularOrbitHelper.frame.setVisible(true);
		});
		misc.add(btnent); misc.add(lblrst); misc.add(btnLog);
		common.add(misc);
		
		common.setBounds(8, 8, 336, 224);
		return common;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override @NotNull
	public Iterator<E> iterator() {
		return objects.iterator();
	}
	
	@Override
	public boolean addObject(@NotNull E newObject){
		info("addObject", new String[]{newObject.toString()});
		tracks.add(newObject.getR());
		return objects.add(newObject);
	}
	
	/**
	 * @param r the radius of the track
	 * @return copy of the collection in which objects are on the given track.
	 */
	@NotNull
	protected Set<E> getObjectsOnTrack(Track r) {
		final Set<E> ret = new TreeSet<>(E.getDefaultComparator());
		forEach(e->{
			if(e.getR().equals(r)) ret.add(ECLASS.cast(e));
		});
		return ret;
	}
	
	@Override @NotNull
	public Set<E> getObjectsOnTrack(double[] r) {
		return getObjectsOnTrack(new Track(r));
	}
	
	@Override @NotNull
	public Set<E> getObjectsOnTrack(Double[] r) {
		return getObjectsOnTrack(new Track(r));
	}
	
	@Override
	public int size() {
		return objects.size();
	}
	
	protected void clearEmptyTrack(){
		tracks.removeIf(t -> find_if(objects, (E e) -> e.getR().equals(t)) == null);
	}
}