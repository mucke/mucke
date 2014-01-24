package mucke.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.Ostermiller.util.ExcelCSVPrinter;

/**
 * Simple, flexible logging facility for comma separated log files
 * 
 * @author Ralf Bierig
 */
public class Logger {

    private ExcelCSVPrinter logPrinter = null;
    private String logFileDir = "logdata";
    private String logFilePath = "./logdata/logging";
    private int columnCount = 0;
    private long rank = 1; // first ranking position
    private String noDataSymbol = "0";
    
    private Map<String, String> currentLog = null;

    /** Logging facility */
    public Logger(String[] columnHeaders) {
	try {

	    // init ensures that keys in LinkedHashMap always have same order
	    currentLog = new LinkedHashMap<String, String>();
	    for (String column : columnHeaders){
		currentLog.put(column, noDataSymbol);	// per default has no value
	    }
	    
	    // store the number of columns for validation when logging data
	    columnCount = columnHeaders.length;

	    // create directory for logs
	    File dir = new File(logFileDir);
	    if (!dir.exists()) {
		dir.mkdir();
	    }

	    // define log file
	    File logFile = new File(this.logFilePath + "_" + Logger.currentTime() + ".log");

	    // Create the log CSV printer pointed at log file
	    // Logging file can only be created at this point
	    logPrinter = new ExcelCSVPrinter(new FileOutputStream(logFile));
	    
	    // always write results immediately
	    logPrinter.setAutoFlush(true);
	    
	    // header
	    logPrinter.writeln(columnHeaders);
	    
	} catch (Exception e) {
	    System.out.println("Logger: Logger() Could'nt write log status - Exception.");
	}
    }

    /**
     * Writes a log of columns, represented in a String array, directly to the logging file. Checks the number of columns with the number of
     * column headers when the Logger was created and throws an error if they differ.
     * 
     * @param columns An array of logging columns
     * @return true if the log was successfully written, false otherwise
     */
    public boolean log(String[] columns) {

	// check column count, every column needs a header and every cell needs data.
	if (columns.length != columnCount) {
	    System.err.println("Logger: logging of data has mismatching columns based on header. Please check your datastructure!");
	}

	try {
	    // Write to the printer
	    //logPrinter.changeDelimiter(',');
	    logPrinter.writeln(columns);
	    // adjust current ranking position
	    this.rank++;

	    return true;

	} catch (IOException e) {
	    System.out.println("Logger: writeLog() IOException: " + e.getMessage());
	}
	return false;
    }
    
    
    /** Resets current columns of the current log entry */
    public void resetColumns(){
	Set<String> keys = currentLog.keySet();
	for (String key : keys){
	    currentLog.put(key, noDataSymbol);
	}
    }
    
    
    /** 
     * Sets the given column of the log file format
     * 
     * @param name Name of the column as defined during instantiation
     * @param value String value of the column
     */
    public void setColumn(String name, String value){
	currentLog.put(name, value);
    }
    
    
    /** Logs current log with its values as set with setColumnValue */
    public void log(){
	Collection<String> values = currentLog.values();
	String[] columns = new String[values.size()];
	values.toArray(columns);
	log(columns);
    }
    

    /** Provides a String representation from the current date and time. */
    private static String currentTime() {
	Calendar calendar = new GregorianCalendar();
	// Translate into a string representation
	// Use of StringBuffer to save memory

	String.format("%03d", 1); // => "001"

	StringBuffer now = new StringBuffer().append(Integer.toString(calendar.get(Calendar.YEAR))).append("-")
		.append(Integer.toString(calendar.get(Calendar.MONTH) + 1)).append("-")
		.append(Integer.toString(calendar.get(Calendar.DATE))).append("_")
		.append(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY))).append("h")
		.append(Integer.toString(calendar.get(Calendar.MINUTE))).append("m")
		.append(Integer.toString(calendar.get(Calendar.SECOND))).append("s")
		.append(Integer.toString(calendar.get(Calendar.MILLISECOND))).append("ms");
	return now.toString();
    }

    /** Provides a String representation from the current date and time as a stringyfied long value. */
    private static String currentTimeAsNumber() {
	Calendar calendar = new GregorianCalendar();
	return Long.toString(calendar.getTime().getTime());
    }

    /** Resets the rank to its inital position */
    public void resetRank() {
	this.rank = 1;
    }
    
    /** Flushes the logger so that all text is written to the logfile */
    public void flush(){
	try {
	    logPrinter.flush();    
	} catch (Exception e){
	    System.out.println("Exception while flushing the logger: " + e.getMessage());
	    e.printStackTrace();
	}
	
    }

    /** Closes the log */
    public boolean close() {
	try {
	    logPrinter.flush();
	    logPrinter.close();
	    // report back
	    return true;
	} catch (IOException exception) {
	    System.out.println("Logger: close() log file could not be closed!");
	}
	return false;
    }

    /** For testing only */
    public static void main(String[] args) {
	
	System.out.println("Testing logger...");
	
	// header
	Logger logger = new Logger( new String[]{"header1", "header2"});
	// data
	for (int i = 1; i < 100; i++) {
	    logger.log(new String[]{"" + i, "" + (30000 + i)});
	}
	logger.close();
	
	// header
	Logger loggerIterative = new Logger( new String[]{"header1", "header2", "header3"});
	// data
	for (int i = 1; i < 100; i++) {
	    loggerIterative.resetColumns();
	    loggerIterative.setColumn("header1", "1");
	    loggerIterative.setColumn("header2", "1");
	    loggerIterative.log();
	    loggerIterative.resetColumns();
	    loggerIterative.setColumn("header3", "1");
	    loggerIterative.log();
	}
	loggerIterative.close();
	
	System.out.println("Finished testing logger!");
    }
}