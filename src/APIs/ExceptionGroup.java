package APIs;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ExceptionGroup extends RuntimeException{
	private List<Exception> exs = new ArrayList<>();
	
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
}
