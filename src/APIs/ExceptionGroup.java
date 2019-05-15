package APIs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.*;

@SuppressWarnings("unused")
public class ExceptionGroup extends RuntimeException implements Iterable<Exception>{
	private List<Exception> exs = new ArrayList<>();
	private static Logger warning;
	private static Logger info;
	
	static {
		warning = Logger.getLogger("CircularOrbit.GeneralExceptionLogger");
		info = Logger.getLogger("CircularOrbit.GeneralInfoLogger");
		info.setLevel(Level.OFF);
		try {
			File lp = new File("log/");
			if(!lp.exists()) lp.mkdir();
			FileHandler fh = new FileHandler("log/temp.log");
			Formatter fm = new java.util.logging.Formatter(){
				@Override
				public String format(LogRecord record) {
					return record.getInstant() + "\t" + record.getLevel() + "\n" + record.getMessage() + "\n";
				}
			};
			fh.setFormatter(fm);
			warning.addHandler(fh);
			info.addHandler(fh);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public boolean join(Exception ex){
		return exs.add(ex);
	}
	
	@Override
	public String getMessage() {
		StringBuilder s = new StringBuilder();
		exs.forEach(e->s.append(e.getMessage()).append('\n'));
		return s.toString();
	}
	
	@Override
	public String getLocalizedMessage() {
		StringBuilder s = new StringBuilder();
		exs.forEach(e->s.append(e.getLocalizedMessage()).append('\n'));
		return s.toString();
	}
	
	@Override
	public void printStackTrace() {
		exs.forEach(Exception::printStackTrace);
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		exs.forEach(e->e.printStackTrace(s));
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		exs.forEach(e->e.printStackTrace(s));
	}
	
	public int size(){
		return exs.size();
	}
	
	public Exception get(int index){
		return exs.get(index);
	}
	
	public void clear(){
		exs.clear();
	}
	
	public boolean isEmpty(){
		return exs.isEmpty();
	}
	
	@NotNull @Override
	public Iterator<Exception> iterator() {
		return exs.iterator();
	}
	
	public static void warning(ExceptionGroup exs){
		exs.forEach(ExceptionGroup::warning);
	}
	
	public static void warning(Exception e){
		var c = e.getStackTrace()[0];
		warning.warning(c + "." + c.getMethodName() + ", " + e.getMessage());
	}
	
	public static void info(String op, String[] args){
		StringBuilder s = new StringBuilder();
		s.append(op).append(" ");
		for (String arg : args) s.append(arg).append(", ");
		s.append("\b\b ");
		info.info(s.toString());
	}
	
	public static void info(String msg){
		info.info(msg);
	}
}
