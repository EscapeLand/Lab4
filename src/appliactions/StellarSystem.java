package appliactions;

import APIs.CircularOrbitAPIs;
import circularOrbit.CircularOrbit;
import circularOrbit.ConcreteCircularOrbit;
import circularOrbit.PhysicalObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StellarSystem extends ConcreteCircularOrbit<FixedStar, Planet> {
	private Thread loop;
	private double time = 0;
	private double timeSpan = 2000;
	private Runnable refresh;
	
	/**
	 * register a refresh function.
	 * it will be called whenever the stellar system is changed.
	 * @param refresh the function.
	 */
	public void register(Runnable refresh) {
		this.refresh = refresh;
	}
	
	/**
	 * start a new thread to refresh the User Interface.
	 */
	public void start(){
		loop = new Thread(refresh);
		loop.start();
	}
	
	@Override
	public boolean loadFromFile(String path) throws IOException {
		File file = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for(String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
			if(buffer.isEmpty()) continue;
			Matcher m = Pattern.compile("([a-zA-Z]+)\\s?::=\\s?<(.*)>").matcher(buffer);
			if(!m.find() || m.groupCount() != 2){
				System.out.println("warning: regex: group count != 2, continued. ");
				continue;
			}
			switch (m.group(1)){
				case "Stellar":
				{
					String[] list = m.group(2).split(",");
					if(list.length != 3){
						System.out.println("warning: regex: Stellar: not 3 args. continued. ");
						continue;
					}
					FixedStar f = new FixedStar(list[0], Float.valueOf(list[1]), Double.valueOf(list[2]));
					changeCentre(f);
					break;
				}
				case "Planet":
				{
					List<String> list = new ArrayList<>(Arrays.asList(m.group(2).split(",")));
					if(list.size() != 8){
						System.out.println("warning: regex: Planet: not 8 args. continued. ");
						continue;
					}
					list.add(0, "Planet");
					PhysicalObject p = PhysicalObjectFactory.produce(list.toArray(new String[0]));
					assert p instanceof Planet;
					if(!addObject((Planet) p))
						System.out.println("warning: failed to add " + list.get(1));
					break;
				}
				default:
					System.out.println("warning: regex: unexpected key: " + m.group(1));
			}
		}
		return true;
	}
	
	@Override
	public void process(Consumer<CircularOrbit> refresh) {
		JFrame frame = new JFrame(getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(null);
		this.test(frame, refresh);
		
		frame.setVisible(true);
	}
	
	@Override
	protected JPanel test(JFrame frame, Consumer<CircularOrbit> end) {
		var par =  super.test(frame, end);
		JPanel spec = new JPanel();
		spec.setBounds(8, par.getY() + par.getHeight() + 8, 336, 136);
		spec.setLayout(new FlowLayout(FlowLayout.CENTER, 336, 8));
		spec.setBorder(BorderFactory.createLineBorder(Color.decode("#e91e63"), 1, true));
		frame.add(spec);
		
		JPanel pnlTimeAt = new JPanel();
		JLabel lblTimeAt = new JLabel("Time at: ");
		JTextField txtTimeAt = new JTextField("18000");
		JButton btnTimeApply = new JButton("Apply");
		btnTimeApply.addActionListener(e-> {
			setTime(Double.valueOf(txtTimeAt.getText()));
			end.accept(this);
		});
		pnlTimeAt.add(lblTimeAt); pnlTimeAt.add(txtTimeAt); pnlTimeAt.add(btnTimeApply);
		spec.add(pnlTimeAt);
		
		JPanel pnlCalc = new JPanel();
		JLabel lblCalc = new JLabel("Distance between "), lblAnd = new JLabel(" and ")
				, lblRes = new JLabel();
		JTextField txtA = new JTextField("Neptune"), txtB = new JTextField("Mercury");
		JButton btnCalc = new JButton("=");
		btnCalc.addActionListener(e->{
			lblCalc.setVisible(false);
			PhysicalObject o1 = query(txtA.getText());
			PhysicalObject o2 = query(txtB.getText());
			if (o1 instanceof Planet && o2 instanceof Planet) {
				lblRes.setText(String.valueOf(CircularOrbitAPIs.getPhysicalDistance(this, o1, o2)));
			} else {
				lblRes.setText("Didn't match. ");
			}
		});
		pnlCalc.add(lblCalc); pnlCalc.add(txtA); pnlCalc.add(lblAnd); pnlCalc.add(txtB); pnlCalc.add(btnCalc);
		pnlCalc.add(lblRes); spec.add(pnlCalc);
		
		JPanel pnlCtrl = new JPanel();
		JButton btnReset = new JButton("Reset"),
				btnPause = new JButton("Pause"),
				btnTimeSpanApply = new JButton("Apply");
		JTextField txtTimeSpan = new JTextField("1600000");
		btnReset.addActionListener(e->{this.reset(); end.accept(this);});
		btnPause.addActionListener(e->{
			switch (btnPause.getText()) {
				case "Resume":
					start();
					btnPause.setText("Pause");
					break;
				case "Pause":
					loop.interrupt();
					btnPause.setText("Resume");
					break;
			}
		});
		btnTimeSpanApply.addActionListener(e->this.setTimeSpan(Double.valueOf(txtTimeSpan.getText())));
		pnlCtrl.add(btnReset); pnlCtrl.add(btnPause); pnlCtrl.add(txtTimeSpan); pnlCtrl.add(btnTimeSpanApply);
		spec.add(pnlCtrl);
		
		frame.setBounds(1000,232,364,360);
		
		return spec;
	}
	
	/**
	 * update the system, as it is at ({@code time += timeSpan}).
	 */
	public void nextTime(){
		this.time += timeSpan;
		forEach(p->p.nextTime(time));
	}
	
	/**
	 * calculate the stellar system at a specific time.
	 * @param time the time to calculate.
	 */
	private void setTime(double time){
		if(loop != null) loop.interrupt();
		this.time = time;
		forEach(p->p.nextTime(time));
	}
	
	/**
	 * reset the system to its initial state.
	 */
	private void reset(){ setTime(0); }
	
	/**
	 * set how much time is covered in 60ms. the greater, the faster the planets runs.
	 * @param timeSpan time span.
	 */
	private void setTimeSpan(double timeSpan) {
		this.timeSpan = timeSpan;
	}
	
	@Override
	protected String[] hintForUser() {
		return PhysicalObjectFactory.hint_Planet;
	}
	
	@Override
	public void checkRep(){
		var tracks = getTracks();
		tracks.forEach(doubles -> {assert getObjectsOnTrack(doubles).size() <= 1; });
		//NOTE: the planet may revolution in the stellar???
		//var center = center();
		//double r = center == null ? 0 : center.r;
		double r = 0;
		for (Planet e : objects) {
			assert e.getR().getRect()[1] > r;
			r = e.getR().getRect()[0] + e.r;
		}
	}
}

final class FixedStar extends PhysicalObject{
	public final double r;
	public final double m;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FixedStar)) return false;
		if (!super.equals(o)) return false;
		FixedStar fixedStar = (FixedStar) o;
		return fixedStar.getR().equals(getR()) &&
				Double.compare(fixedStar.m, m) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getR(), m);
	}
	
	@Override
	public FixedStar clone() {
		return new FixedStar(getName(), r, m);
	}
	
	FixedStar(String name, double r, double m) {
		super(name, new double[]{r}, 0);
		this.r = r;
		this.m = m;
	}
}

class Planet extends PhysicalObject {
	private final String color;
	private final Form form;
	public final double r;              //radius of the planet
	public final double v;
	
	enum Form{
		Solid, Liquid, Gas
	}
	enum Dir{
		CW, CCW
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Planet)) return false;
		if (!super.equals(o)) return false;
		Planet planet = (Planet) o;
		return Objects.equals(planet.getR(), getR()) &&
				Double.compare(planet.v, v) == 0 &&
				getColor().equals(planet.getColor()) &&
				getForm() == planet.getForm();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getColor(), getForm(), getR(), v);
	}
	
	@Override
	public Planet clone() {
		var tmp = new Planet(getName(), getForm(), getColor(), r, getR().getRect(), v, pos_init);
		tmp.setPos(getPos());
		return tmp;
	}
	
	void nextTime(double time) {
		setPos(pos_init + v * time);
	}
	
	Form getForm() {
		return form;
	}
	
	String getColor() { return color; }
	
	/**
	 * @param name name of the planet
	 * @param form form of the planet
	 * @param color color of the planet
	 * @param r radius of the planet itself
	 * @param R radius of the planet orbit radius
	 * @param v radius of its revolution speed
	 * @param dir direction of its revolution
	 * @param pos init pos of the planet.
	 */
	public Planet(String name, Form form, String color, double r,
	       double[] R, double v, Dir dir, double pos) {
		this(name, form, color, r, R, (dir == Dir.CW ? -1 : 1) * Math.abs(v / R[0]), pos);
	}
	
	Planet(String name, Form form, String color, double r,
	       double[] R, double v, double pos){
		super(name, R, pos);
		this.color = color;
		this.form = form;
		this.r = r;
		this.v = v;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}

final class PlanetarySystem extends Planet{
	private Set<Planet> satellites = new TreeSet<>(PhysicalObject.getDefaultComparator());
	
	PlanetarySystem(@NotNull Planet center) {
		super(center.getName(), center.getForm(), center.getColor(), center.r, center.getR().getRect(),
				center.v, center.getPos());
	}
	
	public boolean addSatellite(@NotNull Planet satellite){
		return satellites.add(satellite);
	}
	
	@NotNull
	public Planet[] satellites(){
		return satellites.toArray(new Planet[0]);
	}
	
	@Nullable
	public Planet query(@NotNull String name){
		return CircularOrbitAPIs.find_if(satellites, (Planet p)->p.getName().equals(name));
	}
}