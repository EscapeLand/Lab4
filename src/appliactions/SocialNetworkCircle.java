package appliactions;

import circularOrbit.CircularOrbit;
import circularOrbit.ConcreteCircularOrbit;
import circularOrbit.PhysicalObject;

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

public final class SocialNetworkCircle extends ConcreteCircularOrbit<CentralUser, User> {
	@Override
	public boolean loadFromFile(String path) throws IOException {
		File file = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<User> params = new ArrayList<>();
		Set<String[]> record = new HashSet<>();
		String center = null;
		for(String buffer = reader.readLine(); buffer != null; buffer = reader.readLine()) {
			if(buffer.isEmpty()) continue;
			Matcher m = Pattern.compile("([a-zA-Z]+)\\s?::=\\s?<(.*)>").matcher(buffer);
			if(!m.find() || m.groupCount() != 2){
				System.out.println("warning: regex: group count != 2, continued. ");
				continue;
			}
			List<String> list = new ArrayList<>(Arrays.asList(m.group(2).split("\\s*,\\s*")));
			if(list.size() != 3){
				System.out.println("warning: regex: not 3 args. continued. ");
				continue;
			}
			switch (m.group(1)){
				case "CentralUser":
					center = list.get(0);
					list.add(0, "CentralUser");
					changeCentre((CentralUser) PhysicalObjectFactory.produce(list.toArray(new String[0])));
					break;
				case "Friend":
					params.add(new User(list.get(0), Integer.valueOf(list.get(1)),
							Enum.valueOf(Gender.class, list.get(2))));
					break;
				case "SocialTie":
					record.add(list.toArray(new String[0]));
					break;
				default:
					System.out.println("warning: regex: unexpected key: " + m.group(1));
			}
		}
		reader.close();
		assert center != null;
		
		params.forEach(this::addObject);
		
		for (String[] list : record) {
			PhysicalObject q1 = query(list[0]);
			PhysicalObject q2 = query(list[1]);
			assert q1 != null && q2 != null;
			setRelation(q1, q2, Float.valueOf(list[2]));
		}
		
		updateR();
		
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
		JPanel par = super.test(frame, end);
		JPanel spec = new JPanel();
		spec.setBounds(8, par.getY() + par.getHeight() + 8, 336, 136);
		spec.setLayout(new FlowLayout(FlowLayout.CENTER, 336, 8));
		spec.setBorder(BorderFactory.createLineBorder(Color.decode("#e91e63"), 1, true));
		frame.add(spec);
		
		final String[] operation = new String[]{"Add", "Remove"};
		
		//============================================================
		//============================================================
		//============================================================
		
		JPanel pnlRlt = new JPanel();
		JComboBox<String> cmbRltOP = new JComboBox<>(operation);
		JTextField txtA = new JTextField("TommyWong"), txtB = new JTextField("TomWong"),
				txtFrV = new JTextField("0.99");
		JButton btnRltApply = new JButton("Apply");
		btnRltApply.addActionListener(e->{
			if(Float.valueOf(txtFrV.getText()) > 1) return;
			var a = query(txtA.getText());
			var b = query(txtB.getText());
			if(a == null || b == null) return;
			
			switch (cmbRltOP.getSelectedIndex()){
				case 0: setRelation(a, b, Float.valueOf(txtFrV.getText())); break;
				case 1: setRelation(a, b, 0); break;
			}
			updateR();
			end.accept(this);
		});
		pnlRlt.add(cmbRltOP); pnlRlt.add(txtA); pnlRlt.add(txtB); pnlRlt.add(txtFrV);
		pnlRlt.add(btnRltApply);
		spec.add(pnlRlt);
		
		//============================================================
		//============================================================
		//============================================================
		
		JPanel pnlExt = new JPanel();
		var tmpuser = getObjectsOnTrack(new double[]{1});
		Set<String> tmpstring;
		if(tmpuser.isEmpty()) tmpstring = null;
		else tmpstring = new HashSet<>(tmpuser.size());
		if(tmpstring != null) transform(tmpuser, tmpstring, PhysicalObject::getName);
		JComboBox<String> cmbElm = new JComboBox<>(tmpstring == null ? new String[]{} : tmpstring.toArray(new String[0]));
		if(tmpstring != null) tmpstring.clear();
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
		
		JPanel pnlLgc = new JPanel();
		JLabel lblLgc = new JLabel("LGC DIST between: "), lblrst = new JLabel();
		JButton btnLgc = new JButton("=");
		JTextField txtC = new JTextField("DavidChen"), txtD = new JTextField("TomWong");
		
		btnLgc.addActionListener(e->{
			var a = query(txtC.getText());
			var b = query(txtD.getText());
			if(a instanceof User && b instanceof User){
				lblrst.setText(String.valueOf(getLogicalDistance(this, (User)a, (User)b)));
				lblLgc.setVisible(false);
			}
			else {
				lblrst.setText("");
				lblLgc.setVisible(true);
			}
		});
		
		pnlLgc.add(lblLgc); pnlLgc.add(txtC); pnlLgc.add(txtD); pnlLgc.add(btnLgc); pnlLgc.add(lblrst);
		spec.add(pnlLgc);
		
		frame.setBounds(1000,232,364,360);
		return spec;
	}
	
	/**
	 * update each user's track when {@code relationship} is modified.
	 */
	private void updateR(){
		var graph = getGraph();
		Set<PhysicalObject> cur = new HashSet<>(1);
		cur.add(center());
		var vertex = graph.vertices(); vertex.remove(center());
		int n = vertex.size() + 1;
		
		for(int k = 0; !vertex.isEmpty() && vertex.size() < n; k++) {
			Set<PhysicalObject> rSet = new HashSet<>();
			cur.forEach(p->rSet.addAll(graph.targets(p).keySet()));
			final int tmp = k;
			n = vertex.size();
			rSet.forEach(p->{
				if(vertex.remove(p)){
					moveObject((User) p, new double[]{tmp + 1});
				}
			});
			cur = rSet;
		}
		
		var edges = graph.edges();
		edges.forEach((d, f)->{
			PhysicalObject a = (PhysicalObject) d[0];
			PhysicalObject b = (PhysicalObject) d[1];
			if(a.getR().getRect()[0] > b.getR().getRect()[0]) graph.set(a, b, 0);
		});
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
	protected String[] hintForUser() {
		return PhysicalObjectFactory.hint_User;
	}
	
	@Override
	public void checkRep() {
		forEach(u->{assert u.getR().getRect()[0] == getLogicalDistance(this, center(), u);});
	}
}

enum Gender{
	M, F
}

final class User extends PhysicalObject {
	private final Gender gender;
	private final int age;
	
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

final class CentralUser extends PhysicalObject{
	private final Gender gender;
	private final int age;
	
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