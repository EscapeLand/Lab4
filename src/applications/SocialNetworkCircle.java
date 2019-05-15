package applications;

import APIs.ExceptionGroup;
import circularOrbit.CircularOrbit;
import circularOrbit.ConcreteCircularOrbit;
import circularOrbit.PhysicalObject;
import org.jetbrains.annotations.NotNull;
import track.Track;

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

import static APIs.CircularOrbitAPIs.getLogicalDistance;
import static APIs.CircularOrbitAPIs.transform;
import static APIs.CircularOrbitHelper.generatePanel;
import static applications.PhysicalObjectFactory.insert_copy;

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
		String center = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
				if (buffer.isEmpty()) continue;
				Matcher m = Pattern.compile("([a-zA-Z]+)\\s?::=\\s?<(.*)>").matcher(buffer);
				if (!m.find() || m.groupCount() != 2) {
					exs.join(new IllegalArgumentException("warning: regex: group count != 2, continued. "));
					continue;
				}
				String[] list = (m.group(2).split("\\s*,\\s*"));
				if (list.length != 3) {
					exs.join(new IllegalArgumentException("warning: regex: not 3 args. continued. "));
					continue;
				}
				switch (m.group(1)) {
					case "CentralUser":
						center = list[0];
						list = insert_copy(list, "CentralUser", 0);
						changeCentre((CentralUser) PhysicalObjectFactory.produce(list));
						break;
					case "Friend":
						params.add(new User(list[0], Integer.valueOf(list[1]),
								Enum.valueOf(Gender.class, list[2])));
						break;
					case "SocialTie":
						record.add(list);
						break;
					default:
						exs.join(new IllegalArgumentException("warning: regex: unexpected key: " + m.group(1)));
				}
			}
		} catch (IOException e) {
			exs.join(e);
			throw exs;
		}
		
		
		if (center == null) throw new RuntimeException("warning: center is not set. ");
		
		params.forEach(super::addObject);
		
		for (String[] list : record) {
			if(list[0].equals(list[1])){
				exs.join(new RuntimeException("warning: relationship: " + list[0] + "->" + list[1]));
				continue;
			}
			
			PhysicalObject q1 = query(list[0]);
			PhysicalObject q2 = query(list[1]);
			
			if (q1 == null || q2 == null) {
				exs.join(new RuntimeException("warning: " + (q1 == null ? list[0] + " ": "")
				+ (q2 == null ? list[1] + " ": "") + "not defined. "));
				continue;
			}

			super.setRelation(q1, q2, Float.valueOf(list[2]));
		}
		
		
		if(exs.isEmpty()) {
			updateR();
			return true;
		}
		else throw exs;
	}
	
	@Override
	public void process(Consumer<CircularOrbit> refresh) {
		JFrame frame = new JFrame(getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(null);
		this.test(frame, refresh);
		
		frame.setBounds(1000,232,396,512);
		frame.setVisible(true);
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
	public void setRelation(@NotNull PhysicalObject a, @NotNull PhysicalObject b, float val) {
		super.setRelation(a, b, val);
		updateR();
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
				if(vertex.remove(p)){
					moveObject((User) p, new double[]{tmp + 1});
				}
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