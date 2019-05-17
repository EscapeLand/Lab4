package exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

public class GeneralLogger {
	private static Logger warning;
	private static Logger info;
	
	static {
		warning = Logger.getLogger("CircularOrbit.GeneralExceptionLogger");
		info = Logger.getLogger("CircularOrbit.GeneralInfoLogger");
		info.setLevel(Level.INFO);
		try {
			File lp = new File("log/");
			if(!lp.exists() && !lp.mkdir()) throw new IOException("cannot mkdir: log/");
			FileHandler fhI = new FileHandler("log/info.log");
			FileHandler fhW = new FileHandler("log/warning.log");
			
			Formatter fm = new java.util.logging.Formatter(){
				@Override
				public String format(LogRecord record) {
					return record.getInstant() + "\t" + record.getLevel() + "\n" + record.getMessage() + "\n";
				}
			};
			
			fhI.setFormatter(fm);
			fhW.setFormatter(fm);
			info.addHandler(fhI);
			warning.addHandler(fhW);
			
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
		warning.warning(e.getClass().getSimpleName() + ": " + c +  ", " + e.getMessage());
	}
	
	public static void info(String op, String[] args){
		StringBuilder s = new StringBuilder();
		s.append(op).append(" ");
		for (String arg : args) s.append(arg).append(", ");
		var l = s.length();
		s.delete(l - 2, l);
		info.info(s.toString());
	}
	
	public static void info(String msg){
		info.info(msg);
	}
	
	public static void severe(Exception e){
		var c = e.getStackTrace()[0];
		warning.severe(e.getClass().getSimpleName() + ": " + c + "." + c.getMethodName() + ", " + e.getMessage());
	}
	
	public static List<List> loadInfo(String path) throws IOException{
		InfoParser ifp = new InfoParser();
		
		File ifFile = new File(path);
		
		try (BufferedReader read = new BufferedReader(new FileReader(ifFile))) {
			for(String buf = read.readLine(); buf != null; buf = read.readLine()){
				buf += " " + read.readLine();
				ifp.addLogs(buf.trim().split("\\s+"));
			}
		}
		
		return ifp.getLogs();
	}
	
	public static List<List> loadWarning(String path) throws IOException{
		WarningParser wnp = new WarningParser();
		
		File wnFile  = new File(path);
		
		try (BufferedReader read = new BufferedReader(new FileReader(wnFile))) {
			for(String buf = read.readLine(); buf != null; buf = read.readLine()){
				buf += " " + read.readLine();
				wnp.addLogs(buf.trim().split("\\s+"));
			}
		}
		
		return wnp.getLogs();
	}
}

class InfoParser {
	private List<List> logs = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	void addLogs(String[] log){
		List list = new ArrayList();
		list.add(Instant.parse(log[0]));
		list.add(Level.parse(log[1]));
		list.addAll(Arrays.asList(log).subList(2, log.length));
		logs.add(list);
	}
	
	List<List> getLogs() {
		return logs;
	}
}

class WarningParser {
	private List<List> logs = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	void addLogs(String[] log){
		List list = new ArrayList();
		list.add(Instant.parse(log[0]));
		list.add(Level.parse(log[1]));
		list.add(log[2].substring(0, log[2].length() - 1));
		list.add(String.join(" ", Arrays.asList(log).subList(3, log.length)));
		logs.add(list);
	}
	
	List<List> getLogs() {
		return logs;
	}
}