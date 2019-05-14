package applications;

import APIs.CircularOrbitAPIs;
import APIs.ExceptionGroup;
import circularOrbit.CircularOrbit;
import circularOrbit.ConcreteCircularOrbit;
import circularOrbit.PhysicalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import track.Track;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static APIs.CircularOrbitHelper.generatePanel;

public final class AtomStructure extends ConcreteCircularOrbit<Kernel, Electron> {
	private Caretaker caretaker = new Caretaker();
	
	public AtomStructure() {
		super(Kernel.class, Electron.class);
	}
	
	@Override
	public boolean loadFromFile(String path) throws ExceptionGroup {
		Pattern[] patterns = {
				Pattern.compile("ElementName\\s?::= ([A-Z][a-z]{0,2})"),
				Pattern.compile("NumberOfElectron\\s?::= ((?:\\d+[/;]?)+)"),
				Pattern.compile("NumberOfTracks\\s?::= (\\d+)")
		};
		ExceptionGroup exs = new ExceptionGroup();
		File file = new File(path);
		String[] txt = new String[3];
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (int i = 0; i < 3; i++) txt[i] = reader.readLine();
		} catch (Exception e) {
			exs.join(e);
			throw exs;
		}
		
		Arrays.sort(txt);
		Matcher m = patterns[0].matcher(txt[0]);
		if (!m.find() || m.groupCount() != 1) {
			exs.join(new IllegalArgumentException("warning: regex: ElementName != 1. "));
		}
		
		changeCentre(new Kernel(m.group(1)));
		
		m = patterns[2].matcher(txt[2]);
		if (!m.find() || m.groupCount() != 1) {
			exs.join(new IllegalArgumentException("warning: regex: NumberOfTracks != 1. "));
		}
		int n = Integer.valueOf(m.group(1));
		
		m = patterns[1].matcher(txt[1]);
		if (!m.find() || m.groupCount() != 1) {
			exs.join(new IllegalArgumentException("warning: regex: NumberOfElectron != 1. "));
		}
		
		int[] num = new int[n];
		String[] tmp = m.group(1).split("[/;]");
		if(tmp.length != 2 * n) {
			exs.join(new IllegalArgumentException("warning: track number != sizeof(ObjectGroup)"));
		}
		
		for (int i = 0; i < n; i++) {
			num[i] = Integer.valueOf(tmp[2 * i + 1]);
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < num[i]; j++) addObject(new Electron(i + 1));
		}
		
		if(!exs.isEmpty()) throw exs;
		else return true;
	}
	
	@Override
	public void checkRep() {}
	
	/**
	 * @param from transit from.
	 * @param to transit to.
	 * @param number how many electrons to transit.
	 * @apiNote only AtomStructure can transit.
	 * @return true if success.
	 */
	private boolean transit(double[] from, double[] to, int number) {
		if(from == to) return false;
		if(!findTrack(from) || !findTrack(to)) return false;
		//TODO boolean up = to[1] > from[0];
		boolean up = to[0] > from[0];
		Track tfrom = new Track(from);
		//if(n > sfrom.size()) return false;
		
		caretaker.setMementos(from, to, saveMemento(from, to));
		
		for (int i = 0; i < number; i++) {
			Electron e = CircularOrbitAPIs.find_if(this,
					t->t.getR().equals(tfrom) && t.isGround() == up);
			if(e == null || !moveObject(e, to)){
				recover(from, to);
				return false;
			}
			e.switchState(!up);
		}

		return true;
	}
	
	@Override
	public void process(Consumer<CircularOrbit> refresh) {
		JFrame frame = new JFrame(getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(null);
		var spc = this.test(frame, refresh);
		
		frame.setBounds(1000,232,364,spc.getY() + spc.getHeight() + 48);
		frame.setVisible(true);
	}
	
	@Override
	protected JPanel test(JFrame frame, Consumer<CircularOrbit> refresh) {
		var par = super.test(frame, refresh);
		JPanel spec = new JPanel();
		spec.setBounds(8, par.getY() + par.getHeight() + 8, 336, 84);
		spec.setLayout(new FlowLayout(FlowLayout.CENTER, 336, 8));
		spec.setBorder(BorderFactory.createLineBorder(Color.decode("#e91e63"), 1, true));
		frame.add(spec);
		
		JPanel panel = generatePanel("Transit");
		spec.add(panel);
		panel.setBounds(8, 176, 336, 32);
		
		Set<Track> tmp = new TreeSet<>(Track.defaultComparator);
		CircularOrbitAPIs.transform(getTracks(), tmp, Track::new);
		JComboBox<Track> cmbS1 = new JComboBox<>(tmp.toArray(new Track[0]));
		JComboBox<Track> cmbS2 = new JComboBox<>(tmp.toArray(new Track[0]));
		cmbS2.setSelectedIndex(1);
		JButton btnTrsit = new JButton("Transit");
		JTextField txtNum = new JTextField("  1");
		
		panel.add(cmbS1); panel.add(btnTrsit); panel.add(cmbS2); panel.add(txtNum);
		
		btnTrsit.addActionListener(e -> {
			Track from = (Track) cmbS1.getSelectedItem();
			Track to = (Track) cmbS2.getSelectedItem();
			assert from != null && to != null;
			try{
				if(transit(from.getRect(), to.getRect(), Integer.valueOf(txtNum.getText().trim())))
					refresh.accept(this);
			} catch (NumberFormatException ex) {
				txtNum.setText("  1");
			}
			
		});
		
		return spec;
	}
	
	/**
	 * recover the atom system when transit failed.
	 * @param from transit from
	 * @param to transit to
	 * @see AtomStructure#transit(double[], double[], int)
	 */
	private void recover(double[] from, double[] to){
		var rec = caretaker.getMementos(from, to);
		if(rec == null) throw new RuntimeException("cannot recover: " + Arrays.toString(from) + "->" + Arrays.toString(to));
		removeTrack(from); removeTrack(to);
		addTrack(from); addTrack(to);
		objects.addAll(rec.getFrom());
		objects.addAll(rec.getTo());
		caretaker.destroyMementos(from, to);
	}
	/**
	 * Originator.
	 * @param from transit from
	 * @param to transit to.
	 * @return Memento of the origin state.
	 */
	private Memento<Electron> saveMemento(double[] from, double[] to){
		return new Memento<>(getObjectsOnTrack(from), getObjectsOnTrack(to));
	}
}

final class Memento<E extends PhysicalObject>{
	private Set<E> from;
	private Set<E> to;
	
	Set<E> getFrom() { return from; }
	
	public Set<E> getTo() { return to; }
	
	Memento(Set<E> from, Set<E> to) {
		this.from = from;
		this.to = to;
	}
}

final class Caretaker{
	final class pair{
		double[] first;
		double[] second;
		
		pair(double[] first, double[] second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof pair)) return false;
			pair pair = (pair) o;
			return Arrays.equals(first, pair.first) &&
					Arrays.equals(second, pair.second);
		}
		
		@Override
		public int hashCode() {
			int result = Arrays.hashCode(first);
			result = 31 * result + Arrays.hashCode(second);
			return result;
		}
	}
	private Map<pair, Memento<Electron>> mementos = new HashMap<>();
	
	@Nullable
	Memento<Electron> getMementos(double[] from, double[] to) {
		return mementos.get(new pair(from, to));
	}
	
	void setMementos(double[] from, double[] to, Memento<Electron> mementos) {
		this.mementos.put(new pair(from, to), mementos);
	}
	
	void destroyMementos(double[] from, double[] to){
		mementos.remove(new pair(from, to));
	}
}

final class Electron extends PhysicalObject{
	private ElectronState state = new Ground();
	@SuppressWarnings("unused")
	public static String[] hint = new String[]{"Radius"};
	
	Electron(double r) {
		super("e", new double[]{r}, 360 * Math.random());
	}
	
	void switchState(boolean ground){
		if(ground) state = new Ground();
		else state = new Excited();
	}
	
	boolean isGround(){
		return state.isGround();
	}
	
	@NotNull @Override
	public String toString() {
		return "Electron{" + getR().toString()
				+ ", " + state.toString()
				+ "}";
	}
	
	@Override
	public Electron clone() {
		Electron e = new Electron(R_init.getRect()[0]);
		e.setR(getR());
		e.state = state.isGround() ? new Ground() : new Excited();
		return e;
	}
}

@SuppressWarnings("unused")
final class Kernel extends PhysicalObject{
	private int proton;
	private int neutron;
	public String[] hint = new String[]{"Name"};
	
	Kernel(String name) {
		super(name, new double[]{0}, 0);
	}
	
	@Override
	public PhysicalObject clone() {
		return new Kernel(getName());
	}
	
	public int getProton() {
		return proton;
	}
	
	public void setProton(int proton) {
		this.proton = proton;
	}
	
	public int getNeutron() {
		return neutron;
	}
	
	public void setNeutron(int neutron) {
		this.neutron = neutron;
	}
}