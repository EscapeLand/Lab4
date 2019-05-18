package applications;

import circularOrbit.CircularOrbit;
import circularOrbit.ConcreteCircularOrbit;
import circularOrbit.PhysicalObject;
import exceptions.ExceptionGroup;
import exceptions.LogicErrorException;
import org.jetbrains.annotations.NotNull;
import track.Track;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static APIs.CircularOrbitAPIs.getLogicalDistance;
import static APIs.CircularOrbitAPIs.transform;
import static APIs.CircularOrbitHelper.generatePanel;
import static factory.PhysicalObjectFactory.produce;

public final class SocialNetworkCircle extends ConcreteCircularOrbit<CentralUser, User> {
	public SocialNetworkCircle() {
		super(CentralUser.class, User.class);
	}
	
	@Override
	public boolean loadFromFile(String path) throws ExceptionGroup {
		File file = new File(path);
		ExceptionGroup exs = new ExceptionGroup();
		List<User> params = new ArrayList<>();
		Set<String[]> record = new HashSet<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
				if (buffer.isEmpty()) continue;
				try{
					Matcher m = Pattern.compile("([a-zA-Z]+)\\s?::=\\s?<(.*)>").matcher(buffer);
					if (!m.find() || m.groupCount() != 2)
						throw new IllegalArgumentException("regex: cannot match (" + buffer + "), continued. ");
					String[] list = (m.group(2).split("\\s*,\\s*"));
					if (list.length != 3)
						throw new IllegalArgumentException("regex: (" + buffer + ")not 3 args. continued. ");
					switch (m.group(1)) {
						case "CentralUser":
							changeCentre((CentralUser) produce(CentralUser.class, list));
							break;
						case "Friend":
							params.add(new User(list[0], Integer.valueOf(list[1]),
									Enum.valueOf(Gender.class, list[2])));
							break;
						case "SocialTie":
							record.add(list);
							break;
						default:
							throw new IllegalArgumentException("regex: unexpected label: " + m.group(1) + ". continued. ");
					}
				} catch (IllegalArgumentException e) {
					exs.join(e);
				}
				
			}
		} catch (IOException e) {
			exs.join(e);
			throw exs;
		}
		
		
		if (center() == null) throw new LogicErrorException("center is not set. returned. ");
		
		params.forEach(super::addObject);
		
		for (String[] list : record) {
			if(list[0].equals(list[1])){
				exs.join(new LogicErrorException("relationship: " + list[0] + "->" + list[1] + ". continued. "));
				continue;
			}
			
			PhysicalObject q1 = query(list[0]);
			PhysicalObject q2 = query(list[1]);
			
			if (q1 == null || q2 == null) {
				exs.join(new LogicErrorException("warning: " + (q1 == null ? list[0] + " ": "")
				+ (q2 == null ? list[1] + " ": "") + "not defined. continued. "));
				continue;
			}
			
			try{
				var split = list[2].split(".");
				if(split.length == 2 && split[1].length() > 3){
					split[1] = split[1].substring(0, 3);
					var tmp = list[2];
					list[2] = String.join(".", split);
					exs.join(new IllegalArgumentException(tmp + " more than 3 decimal. truncated. "));
				}
				super.setRelation(q1, q2, Float.valueOf(list[2]));
			} catch (IllegalArgumentException e) {
				exs.join(e);
			}
		}
		
		
		if(exs.isEmpty()) {
			updateR();
			return true;
		}
		else throw exs;
	}
	
	@Override
	public JFrame process(Consumer<CircularOrbit> refresh) {
		JFrame frame = super.process(refresh);
		this.test(frame, refresh);
		
		frame.setBounds(1000,232,396,512);
		frame.setVisible(true);
		return frame;
	}
	
	@Override
	protected JPanel test(JFrame frame, Consumer<CircularOrbit> end) {
		JPanel par = super.test(frame, end);
		JPanel spec = new JPanel();
		par.setBounds(8, 8, 364, par.getHeight());
		spec.setBounds(8, par.getY() + par.getHeight() + 8, 364, 224);
		spec.setLayout(new FlowLayout(FlowLayout.CENTER, 336, 8));
		spec.setBorder(BorderFactory.createLineBorder(Color.decode("#e91e63"), 1, true));
		frame.add(spec);
		
		final String[] operation = new String[]{"Add", "Remove"};
		
		//============================================================
		//============================================================
		//============================================================
		
		JPanel pnlRlt = generatePanel("Relationship Operation");
		JComboBox<String> cmbRltOP = new JComboBox<>(operation);
		JTextField txtA = new JTextField("TommyWong"), txtB = new JTextField("TomWong"),
				txtFrV = new JTextField("0.99");
		JButton btnRltApply = new JButton("Apply");
		btnRltApply.addActionListener(e->{
			var a = query(txtA.getText().trim());
			var b = query(txtB.getText().trim());
			if(a == null || b == null) return;
			
			switch (cmbRltOP.getSelectedIndex()){
				case 0:
				{
					float frV;
					try{
						frV = Float.valueOf(txtFrV.getText().trim());
					} catch (NumberFormatException ex) {
						txtFrV.setText("0.99");
						return;
					}
					if(frV > 1){
						txtFrV.setText("0.99");
						return;
					}
					setRelation(a, b, frV);
					break;
				}
				case 1: setRelation(a, b, 0); break;
			}
			end.accept(this);
		});
		pnlRlt.add(cmbRltOP); pnlRlt.add(txtA); pnlRlt.add(txtB); pnlRlt.add(txtFrV);
		pnlRlt.add(btnRltApply);
		spec.add(pnlRlt);
		
		//============================================================
		//============================================================
		//============================================================
		
		JPanel pnlExt = generatePanel("Extend Degree");
		var tmpUser = getObjectsOnTrack(new double[]{1});
		Set<String> tmpString;
		if(tmpUser.isEmpty()) tmpString = null;
		else tmpString = new HashSet<>(tmpUser.size());
		if(tmpString != null) transform(tmpUser, tmpString, PhysicalObject::getName);
		JComboBox<String> cmbElm = new JComboBox<>(tmpString == null ? new String[]{}
																	: tmpString.toArray(new String[0]));
		if(tmpString != null) tmpString.clear();
		JButton btnExt = new JButton("Calculate");
		JLabel lblExtRst = new JLabel();
		btnExt.addActionListener(e->{
			String item = (String) cmbElm.getSelectedItem();
			if(item == null) return;
			var a = query(item);
			if(a instanceof User) lblExtRst.setText(String.valueOf(extendVal((User) a)));
		});
		pnlExt.add(cmbElm); pnlExt.add(btnExt); pnlExt.add(lblExtRst);
		spec.add(pnlExt);
		
		//============================================================
		//============================================================
		//============================================================
		
		JPanel pnlLgc = generatePanel("Logic Distance");
		JLabel lblrst = new JLabel();
		JButton btnLgc = new JButton("Calculate");
		JTextField txtC = new JTextField("DavidChen"), txtD = new JTextField("TomWong");
		
		btnLgc.addActionListener(e->{
			var a = query(txtC.getText().trim());
			var b = query(txtD.getText().trim());
			if(a instanceof User && b instanceof User){
				lblrst.setText(String.valueOf(getLogicalDistance(this, a, b)));
			}
			else lblrst.setText("");
		});
		
		pnlLgc.add(txtC); pnlLgc.add(txtD); pnlLgc.add(btnLgc); pnlLgc.add(lblrst);
		spec.add(pnlLgc);
		
		return spec;
	}
	
	@Override
	public boolean addObject(@NotNull User newObject) {
		var b = super.addObject(newObject);
		moveObject(newObject, new double[]{-1});
		return b;
	}
	
	@Override
	public boolean removeObject(@NotNull User obj) {
		boolean b;
		if(getObjectsOnTrack(obj.getR()).size() == 1) b = removeTrack(obj.getR().getRect());
		else b = super.removeObject(obj);
		
		if(b) updateR();
		return b;
	}
	
	@Override
	public boolean moveObject(User obj, double[] to) {
		if(obj.getR().equals(new Track(to))) return true;
		var from = obj.getR();
		
		if(getObjectsOnTrack(from).size() == 1) {
			boolean b;
			if (b = super.moveObject(obj, to)) super.removeTrack(from.getRect());
			return b;
		}
		else return super.moveObject(obj, to);
	}
	
	@Override
	public boolean removeTrack(double[] r) {
		var b = super.removeTrack(r);
		if(b) updateR();
		return b;
	}
	
	@Override
	public void setRelation(@NotNull PhysicalObject a, @NotNull PhysicalObject b, float val)
			throws IllegalArgumentException
	{
		DecimalFormat df = new DecimalFormat("#.000");
		var str = df.format(val);
		float v = Float.valueOf(str);
		super.setRelation(a, b, v);
		updateR();
		if(v != val){
			throw new IllegalArgumentException(val + " more than 3 decimal. truncated. ");
		}
	}
	
	/**
	 * update each user's track when {@code relationship} is modified.
	 */
	private void updateR(){
		var relationship = getGraph();
		Set<PhysicalObject> cur = new HashSet<>(1);
		cur.add(center());
		var vertex = relationship.vertices(); vertex.remove(center());
		int n = vertex.size() + 1;
		
		for(int k = 0; !vertex.isEmpty() && vertex.size() < n; k++) {
			Set<PhysicalObject> rSet = new HashSet<>();
			cur.forEach(p->rSet.addAll(relationship.targets(p).keySet()));
			final int tmp = k;
			n = vertex.size();
			rSet.forEach(p->{
				if(vertex.remove(p)) moveObject((User) p, new double[]{tmp + 1});
			});
			cur = rSet;
		}
		
		vertex.forEach(v->v.setR(new double[]{-1}));
		clearEmptyTrack();
		
		var edges = relationship.edges();
		edges.forEach((d, f)->{
			PhysicalObject a = (PhysicalObject) d[0];
			PhysicalObject b = (PhysicalObject) d[1];
			if(a.getR().getRect()[0] > b.getR().getRect()[0]) relationship.set(a, b, 0);
		});
		
		checkRep();
	}
	
	/**
	 * calculate expansion of a user in the first track.
	 * @param first user in the first orbit.
	 * @return expansion degree of the user.
	 */
	private int extendVal(User first){
		Map<PhysicalObject, Float> cur = new HashMap<>(1);
		Set<PhysicalObject> rSet = new HashSet<>();
		cur.put(first, 1.0f);
		var graph = getGraph();
		
		while (!cur.isEmpty()){
			Map<PhysicalObject, Float> rMap = new HashMap<>();
			cur.forEach((u, f)->{
				rMap.putAll(graph.targets(u));
				rMap.entrySet().removeIf(t->t.getKey().getR().getRect()[0] < u.getR().getRect()[0]);
				rMap.values().forEach(i->i *= f);
				rMap.entrySet().removeIf(t->t.getValue() < 0.02);
			});
			rSet.addAll(cur.keySet());
			cur = rMap;
		}
		
		return rSet.size() - 1;
	}
	
	@Override
	public void checkRep() {
		forEach(u->{assert u.getR().getRect()[0] == getLogicalDistance(this, center(), u);});
	}
}

enum Gender{
	M, F
}

@SuppressWarnings("unused")
final class User extends PhysicalObject {
	private final Gender gender;
	private final int age;
	public static String[] hint = new String[]{"Radius", "Name", "Age", "Gender"};
	
	User(Double r, String name, int age, Gender gender) {
		super(name, new double[]{r}, 360 * Math.random());
		this.gender = gender;
		this.age = age;
	}
	
	User(String name, int age, Gender gender) {
		super(name, new double[]{-1});
		this.gender = gender;
		this.age = age;
	}
	
	private Gender getGender() {
		return gender;
	}
	
	private int getAge() {
		return age;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		if (!super.equals(o)) return false;
		User user = (User) o;
		return getAge() == user.getAge() &&
				getGender() == user.getGender();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getGender(), getAge());
	}
	
	@Override
	public User clone() {
		var tmp = new User(R_init.getRect()[0], getName(), getAge(), getGender());
		tmp.setR(getR());
		return tmp;
	}
	
	@Override
	public String toString() {
		return "<" + getName() +
				", " + age +
				", " + gender.toString() +
				'>';
	}
}

@SuppressWarnings("unused")
final class CentralUser extends PhysicalObject{
	private final Gender gender;
	private final int age;
	public static String[] hint = new String[]{"Name", "Age", "Gender"};
	
	CentralUser(String name, int age, Gender gender) {
		super(name, new double[]{0}, 0);
		this.gender = gender;
		this.age = age;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public int getAge() {
		return age;
	}
	
	@Override
	public String toString() {
		return "<" + getName() +
				", " + age +
				", " + gender.toString() +
				'>';
	}
	
	@Override
	public CentralUser clone() {
		return new CentralUser(getName(), getAge(), getGender());
	}
}