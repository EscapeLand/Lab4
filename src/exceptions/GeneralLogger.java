package exceptions;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class GeneralLogger {
	private static Logger warning;
	private static Logger info;
	
	static {
		warning = Logger.getLogger("CircularOrbit.GeneralExceptionLogger");
		info = Logger.getLogger("CircularOrbit.GeneralInfoLogger");
		info.setLevel(Level.OFF);
		try {
			File lp = new File("log/");
			if(!lp.exists() && !lp.mkdir()) throw new IOException("cannot mkdir: log/");
			FileHandler fh = new FileHandler("log/temp.log");
			Formatter fm = new java.util.logging.Formatter(){
				@Override
				public String format(LogRecord record) {
					return record.getInstant() + "\t" + record.getLevel() + "\n" + record.getMessage() + "\n";
				}
			};
			fh.setFormatter(fm);
			GeneralLogger.warning.addHandler(fh);
			GeneralLogger.info.addHandler(fh);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void warning(ExceptionGroup exs){
		exs.forEach(GeneralLogger::warning);
	}
	
	public static void warning(Exception e){
		var c = e.getStackTrace()[0];
		warning.warning(e.getClass().getSimpleName() + ": " + c + "." + c.getMethodName() + ", " + e.getMessage());
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
	
	public static void severe(Exception e){
		var c = e.getStackTrace()[0];
		warning.severe(e.getClass().getSimpleName() + ": " + c + "." + c.getMethodName() + ", " + e.getMessage());
	}
}
