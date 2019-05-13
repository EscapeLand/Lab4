package APIs;

import appliactions.StellarSystem;
import circularOrbit.CircularOrbit;
import circularOrbit.CircularOrbitFactory;
import circularOrbit.DefaultCircularOrbitFactory;
import circularOrbit.PhysicalObject;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jetbrains.annotations.NotNull;
import track.Track;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import static APIs.CircularOrbitAPIs.alert;
import static java.lang.Thread.interrupted;


public class CircularOrbitHelper<L extends PhysicalObject, E extends PhysicalObject> extends JFrame {
	private mxGraph graph;
	private Object parent;
	private double scale;
	private double length;
	private Map<Double[], Double[]> Rs = new TreeMap<>(
			Comparator.comparingDouble((Double[] a) -> a[0]).thenComparingDouble(a -> a[1]));
	private Map<PhysicalObject, Object> cells = new TreeMap<>(PhysicalObject.getDefaultComparator());
	private Set<Object> circles = new HashSet<>();
	
	private CircularOrbitHelper(@NotNull CircularOrbit<L, E> c, int length)
	{
		super(c.toString());
		graph = new mxGraph();
		parent = graph.getDefaultParent();
		this.length = length;
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		
		refresh(c, false);
	}
	
	static<L extends PhysicalObject, E extends PhysicalObject> void visualize(CircularOrbit<L, E> c){
		CircularOrbitHelper<L, E> frame = new CircularOrbitHelper<>(c, 800);
		if(c instanceof StellarSystem) {
			((StellarSystem) c).register(() -> frame.run((StellarSystem) c));
			((StellarSystem) c).start();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		c.process(c1 -> frame.refresh(c1, true));
	}
	
	void refresh(CircularOrbit<L, E> c, boolean refreshCircle){
		Set<Double[]> tracks = c.getTracks();
		Consumer<PhysicalObject> addCell = x -> {if(x != null) cells.put(x, label(x));};
		
		scale = length / 2 / tracks.size();
		Rs.clear();
		tracks.forEach(f->{
			if(f[0] > 0) {
				var tmp = (Rs.size() + 1) * scale;
				Rs.put(f, new Double[]{tmp, tmp * f[1] / f[0]});
			}
			
		});
		assert Rs.size() <= tracks.size();
		
		tracks.clear();
		
		setSize((int) (length * 1.1), (int) (length * 1.1));
		
		graph.getModel().beginUpdate();
		
		graph.removeCells(cells.values().toArray(), true);
		cells.clear();
		
		if(refreshCircle){
			graph.removeCells(circles.toArray());
			circles.clear();
		}
		
		Rs.values().forEach(d->circles.add(circle(d)));
		
		try
		{
			addCell.accept(c.center());
			c.forEach(addCell);
			final var pg = c.getGraph().edges();
			pg.forEach((e, f)->{
				PhysicalObject a = (PhysicalObject) e[0];
				PhysicalObject b = (PhysicalObject) e[1];
				if(Track.compare(a.getR(), b.getR()) <= 0)
					line(cells.get(a), cells.get(b), f.toString());
			});
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	private void run(@NotNull StellarSystem s) {
		Map <PhysicalObject, double[]>current = new HashMap<>();
		cells.forEach((p, o)->current.put(p, xy(p)));
		
		while(!interrupted()){
			s.nextTime();
			cells.forEach((p, c)->{
				var now = xy(p);
				double x = now[0], y = now[1];
				var tmp = current.get(p);
				x -= tmp[0]; y -= tmp[1];
				tmp[0] = now[0]; tmp[1] = now[1];
				graph.moveCells(new Object[]{c}, x, y);
			});
			try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	private Object circle(Double[] r){
		if(r[0] < 0) return null;
		final var style = "shape=ellipse;fillColor=none;movable=0;resizable=0;editable=0;connectable=0;";
		double basex = length / 2 - r[0] + 16;
		double basey = length / 2 - r[1] + 16;
		return graph.insertVertex(parent, null, "", basex, basey, 2*r[0], 2*r[1], style);
	}
	
	private double[] xy(PhysicalObject p){
		final double[] ret = new double[2];
		Double[] r;
		if(p.getR().getRect()[0] == -1)
			r = new Double[]{scale * (Rs.size() + 1), scale * (Rs.size() + 1)};
		else r = Rs.get(p.getR().getRect_alt());
		if(r == null) r = new Double[]{0.0, 0.0};
		ret[0] = length / 2.0 + r[0] * Math.cos(Math.toRadians(p.getPos())) + 16;
		ret[1] = length / 2.0 + r[1] * Math.sin(Math.toRadians(p.getPos())) + 16;
		return ret;
	}
	
	private Object label(@NotNull PhysicalObject p){
		final var style = "shape=ellipse;movable=0;resizable=0;editable=0;connectable=0;";
		var tmp = xy(p);
		String name  = p.getName();
		Font font = getContentPane().getComponent(0).getFont();
		FontMetrics fm = getContentPane().getComponent(0).getFontMetrics(font);
		double x = tmp[0], y = tmp[1];
		double w = fm.stringWidth(name) * 1.4;
		return graph.insertVertex(parent, p.toString(), p.getName(), x - w / 2, y - w / 2, w, w, style);
	}
	
	private Object line(Object a, Object b, String label){
		String style = "edgeStyle=none;rounded=0;orthogonalLoop=1;jettySize=auto;dashed=1;endArrow=none;endFill=0;" +
				"editable=0;bendable=0;movable=0;jumpStyle=none;";
		return graph.insertEdge(parent, null, label, a, b, style);
	}
	
	public static void main(String[] args){
		CircularOrbitFactory factory = new DefaultCircularOrbitFactory();
		CircularOrbit s;
		Properties prop = new Properties();
		String last = null;
		
		try{
			InputStream in = new BufferedInputStream(new FileInputStream("history.properties"));
			prop.load(in);
			last = prop.getProperty("lastLoad");
			in.close();
		} catch (IOException e) {
			System.out.println("INFO: property not load. continued. ");
		}
		finally {
			if(last == null) last = "input/";
			last = CircularOrbitAPIs.prompt(null,
					"Load From", "input the path of the config file. ", last);
		}
		
		try {
			s = factory.CreateAndLoad(last);
		} catch (IOException e) {
			alert(null, "Error", e.getMessage());
			return;
		}
		
		if(s == null) {
			if(!last.equals(""))
				alert(null, "Error", "failed to create circular orbit. ");
			return;
		}
		
		try {
			FileOutputStream oFile = new FileOutputStream("history.properties");
			prop.setProperty("lastLoad", last);
			prop.store(oFile, "History");
			oFile.close();
		} catch (IOException e) {
			alert(null, "Error", e.getMessage());
			return;
		}
		
		visualize(s);
	}
}