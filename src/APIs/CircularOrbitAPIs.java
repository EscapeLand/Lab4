package APIs;

import circularOrbit.CircularOrbit;
import circularOrbit.PhysicalObject;
import graph.Graph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import track.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CircularOrbitAPIs {
	public static<L extends PhysicalObject, E extends PhysicalObject> double getObjectDistributionEntropy(CircularOrbit<L, E> c){
		Map<Double, Float> p = new HashMap<>();
		int sum = 0;
		for (E i : c) {
			Float tmp = p.get(i.getR());
			if(tmp == null) tmp = 0.0f;
			p.put(i.getR().getRect()[0], tmp + 1.0f);
			sum++;
		}
		for(Map.Entry<Double, Float> i: p.entrySet()) p.put(i.getKey(), i.getValue() / sum);
		
		float H = 0;
		for(Float i: p.values()) H -= i * Math.log(i);
		return H;
	}
	
	public static int getLogicalDistance (CircularOrbit c, PhysicalObject a, PhysicalObject b){
		Graph<PhysicalObject> graph = c.getGraph();
		if(!graph.vertices().containsAll(Arrays.asList(a, b))) return -1;
		if(a == b) return 0;
		else{
			Set<PhysicalObject> que = new HashSet<>();
			int r = findNext(graph, a, b, que);
			que.clear();
			return r;
		}
	}
	
	private static<E extends PhysicalObject> int findNext(Graph<PhysicalObject> graph, E a, E b, Set<PhysicalObject> que) {
		que.add(a);
		Set<PhysicalObject> next = graph.targets(a).keySet();
		if(next.contains(b)) return que.size();
		else {
			Set<Integer> forcmp = new HashSet<>();
			
			for(PhysicalObject i: next) {
				if(que.contains(i)) continue;
				int r = findNext(graph, i, b, new HashSet<>(que));
				if(r > 0) forcmp.add(r);
			}
			if(forcmp.isEmpty()) return -1;
			else return Collections.min(forcmp);
		}
	}
	
	public static<L extends PhysicalObject, E extends PhysicalObject> double getPhysicalDistance (CircularOrbit<L, E> c, PhysicalObject e1, PhysicalObject e2){
		return oppositeSide(Math.abs(e1.getPos() - e2.getPos()), e1.getR().getRect()[0], e2.getR().getRect()[0]);
	}
	
	public static<L extends PhysicalObject, E extends PhysicalObject> Difference getDifference (CircularOrbit<L, E> c1, CircularOrbit<L, E> c2){
		Set<E> Sc1 = new TreeSet<>(E.getDefaultComparator());
		Set<E> Sc2 = new TreeSet<>(E.getDefaultComparator());
		c1.forEach(Sc1::add);
		c2.forEach(Sc2::add);
		
		Map<Track, Integer> Rc1 = new HashMap<>();
		Map<Track, Integer> Rc2 = new HashMap<>();
		
		Sc1.forEach(x->Rc1.put(x.getR(), Rc1.containsKey(x.getR()) ? Rc1.get(x.getR()) + 1 : 1));
		Sc2.forEach(x->Rc2.put(x.getR(), Rc2.containsKey(x.getR()) ? Rc2.get(x.getR()) + 1 : 1));
		
		int m = Math.max(Sc1.size(), Sc2.size());
		
		int[] trackDif = new int[m];
		Iterator<Integer> Ic1 = Rc1.values().iterator();
		Iterator<Integer> Ic2 = Rc2.values().iterator();
		int i = 0;
		while(Ic1.hasNext() || Ic2.hasNext()){
			trackDif[i++] = Ic1.hasNext() && Ic2.hasNext() ? Ic1.next() - Ic2.next() :
					Ic1.hasNext() ? Ic1.next() : -Ic2.next();
		}
		
		Map<Track, Set<E>> OBJDif1 = new HashMap<>();
		Map<Track, Set<E>> OBJDif2 = new HashMap<>();
		
		for (E e : Sc1) {
			if(!Sc2.contains(e)) {
				Set<E> tmp = OBJDif1.get(e.getR());
				if(tmp == null) tmp = new TreeSet<>(E.getDefaultComparator());
				tmp.add(e);
				OBJDif1.put(e.getR(), tmp);
			}
			else if(!OBJDif1.containsKey(e.getR())) OBJDif1.put(e.getR(), null);
		}
		
		for (E e : Sc2) {
			if(!Sc1.contains(e)) {
				Set<E> tmp = OBJDif2.get(e.getR());
				if(tmp == null) tmp = new TreeSet<>(E.getDefaultComparator());
				tmp.add(e);
				OBJDif2.put(e.getR(), tmp);
			}
			else if(!OBJDif2.containsKey(e.getR())) OBJDif2.put(e.getR(), null);
		}
		
		return new Difference<>(Rc1.size() - Rc2.size(), trackDif,
				new ArrayList<>(OBJDif1.values()), new ArrayList<>(OBJDif2.values()));
	}
	
	private static double oppositeSide(double includeAngle, double l1, double l2){
		return Math.sqrt(l1 * l1 + l2 * l2 - 2 * l1 * l2 * Math.cos(Math.toRadians(includeAngle)));
	}
	
	@Nullable
	public static <E> E find_if(@NotNull Iterable<E> col, Predicate<E> pred){
		for (E e : col) {
			if(pred.test(e)) return e;
		}
		return null;
	}
	
	@NotNull
	public static <O, R> void transform(@NotNull Collection<O> src, @NotNull Collection<R> des, Function<O, R> func){
		des.clear();
		src.forEach(s->des.add(func.apply(s)));
	}
	
	@NotNull
	public static String prompt(@Nullable JFrame owner, String title, String msg, @Nullable String def){
		StringBuffer p = new StringBuffer();
		class promptDialog extends JDialog{
			private promptDialog(){
				super(owner, title);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setBounds(400,200,368,128);
				Container panel = getContentPane();
				panel.setLayout(null);
				JLabel lbl; JTextField txt; JButton btn;
				panel.add(lbl = new JLabel(msg));
				panel.add(txt = new JTextField(def));
				panel.add(btn = new JButton("OK"));
				
				lbl.setBounds(8, 8, 256, 24);
				txt.setBounds(8, 40, 256, 24);
				btn.setBounds(288, 40, 56, 24);
				
				if(def != null) txt.setCaretPosition(def.length());
				
				ActionListener act = e -> {
					p.append(txt.getText());
					this.dispose();
				};
				btn.addActionListener(act);
				
				txt.registerKeyboardAction(act,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
				JComponent.WHEN_FOCUSED);
				
				setModal(true);
			}
		}
		var dialog = new promptDialog();
		dialog.setVisible(true);
		return p.toString();
	}
	
	public static String[] promptForm(@Nullable JFrame owner, String title, @NotNull String[] form){
		JTextField[] formArray = new JTextField[form.length];
		String[] input = new String[form.length];
		
		class promptDialog extends JDialog{
			private promptDialog(){
				super(owner, title);
				int y = 8;
				Container panel = getContentPane();
				panel.setLayout(null);
				
				for (int i = 0; i < form.length; i++) {
					JLabel lbl;
					panel.add(lbl = new JLabel(form[i]));
					panel.add(formArray[i] = new JTextField());
					lbl.setBounds(8, y, 256, 24);
					y += 32;
					formArray[i].setBounds(8, y, 256, 24);
					y += 32;
				}
				
				JButton btn= new JButton("OK");
				panel.add(btn);
				btn.setBounds(200, y, 56, 24);
				setBounds(400,200,292,y + 68);
				
				btn.addActionListener(e -> {
					for (int i = 0; i < formArray.length; i++) {
						input[i] = formArray[i].getText();
					}
					this.dispose();
				});
				setModal(true);
			}
		}
		promptDialog dialog = new promptDialog();
		dialog.setVisible(true);
		return input;
	}
	public static void alert(@Nullable JFrame owner, String title, String msg){
		JOptionPane.showMessageDialog(owner, msg, title, JOptionPane.ERROR_MESSAGE);
	}
}

class Difference<E extends PhysicalObject>{
	public final int trackDif;
	private final int[] trackNumDif;
	private final List<Set<E>> OBJDif1;
	private final List<Set<E>> OBJDif2;
	
	public Difference(int trackDif, int[] trackNumDif, List<Set<E>> OBJDif1, List<Set<E>> OBJDif2) {
		this.trackDif = trackDif;
		this.trackNumDif = trackNumDif;
		this.OBJDif1 = OBJDif1;
		this.OBJDif2 = OBJDif2;
	}
	
	public int[] getTrackNumDif() {
		return trackNumDif.clone();
	}
	
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("轨道数差异: ").append(trackDif);
		int m = Math.max(OBJDif1.size(), OBJDif2.size());
		
		for (int i = 0; i < m; i++) {
			
			s.append("\n轨道").append(i+1).append("的物体数量差异: ").append(trackNumDif[i]);
			s.append("; 物体差异: {");
			if(i < OBJDif1.size()) OBJDif1.get(i).forEach(x->s.append(x.getName()).append(", "));
			if(i < OBJDif1.size() && !OBJDif1.get(i).isEmpty()) s.append("\b\b");
			s.append("} - {");
			if(i < OBJDif2.size()) OBJDif2.get(i).forEach(x->s.append(x.getName()).append(", "));
			if(i < OBJDif2.size() && !OBJDif2.get(i).isEmpty()) s.append("\b\b");
			s.append("}");
		}
		
		return s.toString();
	}
	
	public List<Set<E>> getOBJDif1() {
		return new ArrayList<>(OBJDif1);
	}
	
	public List<Set<E>> getOBJDif2() {
		return new ArrayList<>(OBJDif2);
	}
}