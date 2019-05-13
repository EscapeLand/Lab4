package circularOrbit;

import graph.Graph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import track.Track;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

import static APIs.CircularOrbitAPIs.*;
import static appliactions.PhysicalObjectFactory.insert_copy;
import static appliactions.PhysicalObjectFactory.produce;
import static circularOrbit.PhysicalObject.getDefaultComparator;

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
	private Set<Track> tracks = new HashSet<>();
	
	/**
	 * @return hint for user.
	 * @apiNote this function will be astonished.
	 */
	protected abstract String[] hintForUser();
	
	/**
	 * check RI.
	 */
	public abstract void checkRep();
	
	@Override
	public boolean addTrack(double[] r) {
		return tracks.add(new Track<>(r));
	}
	
	@Override
	public boolean removeTrack(double[] r){
		Track<E> tmp = new Track<>(r);
		return tracks.remove(tmp) && objects.removeIf(e->e.getR().equals(tmp));
	}
	
	@Override
	public L changeCentre(L newCenter){
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
		return true;
	}
	
	@Override
	public boolean removeObject(@NotNull E obj){
		return objects.remove(obj);
	}
	
	@Override
	public void setRelation(@NotNull PhysicalObject a, @NotNull PhysicalObject b, float val){
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
	public boolean transit(double[] from, double[] to, int number) {
		throw new RuntimeException("only AtomStructure can transit. ");
	}
	
	/**
	 * add User Interface controls on the frame.
	 * @param frame where to add controls.
	 * @param end what to do at the end of each action.
	 * @return a panel that includes the control added.
	 */
	protected JPanel test(JFrame frame, Consumer<CircularOrbit> end){
		JPanel common = new JPanel();
		common.setBounds(8, 8, 336, 160);
		common.setLayout(new FlowLayout(FlowLayout.CENTER, 336, 8));
		common.setBorder(BorderFactory.createLineBorder(Color.decode("#673ab7"), 1, true));
		frame.add(common);
		
		JPanel trackOP = new JPanel();
		JPanel objOP = new JPanel();
		JPanel entropy = new JPanel();
		var ops = new String[]{"Add", "Remove"};
		
		JComboBox<String> cmbOps = new JComboBox<>(ops);
		JTextField tracknum = new JTextField("-1");
		JButton trackExec = new JButton("Execute");
		
		trackExec.addActionListener(e -> {
			switch (cmbOps.getSelectedIndex()){
				case 0:
					addTrack(new double[]{Double.valueOf(tracknum.getText().trim())});
					checkRep();
					break;
				case 1:
					Double d = Double.valueOf(tracknum.getText().trim());
					removeTrack(new double[]{d});
					break;
			}
			end.accept(this);
		});
		
		trackOP.add(cmbOps); trackOP.add(tracknum); trackOP.add(trackExec);
		common.add(trackOP);
		
		JComboBox<String> objops = new JComboBox<>(ops);
		Set<Track> tmp = new TreeSet<>(Track.defaultComparator);
		transform(getTracks(), tmp, Track::new);
		JComboBox<Track> objTidx = new JComboBox<>(tmp.toArray(new Track[0]));
		objTidx.setEditable(true);
		JButton objExec = new JButton("Execute");

		objExec.addActionListener(e -> {
			switch(objops.getSelectedIndex()){
				case 0:
					var form = promptForm(frame, "Add object", hintForUser());
					switch (form.length){
						case 1: form = insert_copy(form, "Electron", 0); break;
						case 4: form = insert_copy(form, "User", 0); break;
						case 8: form = insert_copy(form, "Planet", 0); break;
						default: break;
					}
					var p = produce(form);
					if(p != null) addObject((E) p);
					checkRep();
					break;
				case 1:
					Track r = (Track) objTidx.getSelectedItem();
					if(r != null) {
						String name = prompt(frame, "name of the object",
								"Which object to remove? ", null);
						if(name.equals("")) return;
						E o = find_if(objects, i->i.getR().equals(r) && i.getName().equals(name));
						removeObject(o);
					}
					break;
				default: return;
			}
			end.accept(this);
		});
		
		objOP.add(objops); objOP.add(objTidx); objOP.add(objExec);
		common.add(objOP);
		
		JButton btnent = new JButton("Calculate Entropy");
		JLabel lblrst = new JLabel("");
		btnent.addActionListener(e-> lblrst.setText(String.valueOf(getObjectDistributionEntropy(this))));
		entropy.add(btnent); entropy.add(lblrst);
		common.add(entropy);
		
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
		tracks.add(newObject.getR());
		return objects.add(newObject);
	}
	
	@Override @NotNull
	public Set<E> getObjectsOnTrack(double[] r) {
		final Set<E> ret = new TreeSet<>(E.getDefaultComparator());
		final var tmp = new Track(r);
		forEach(e->{
			if(e.getR().equals(tmp)) ret.add((E) e.clone());
		});
		return ret;
	}
	
	@Override @NotNull
	public Set<E> getObjectsOnTrack(Double[] r) {
		final Set<E> ret = new TreeSet<>(E.getDefaultComparator());
		final var tmp = new Track(r);
		forEach(e->{
			if(e.getR().equals(tmp)) ret.add((E) e.clone());
		});
		return ret;
	}
	
	@Override
	public int size() {
		return objects.size();
	}
}