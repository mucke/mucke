package mucke.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mucke.plugin.prototypetest.index.MultiTextFacetIndexer;

import org.apache.log4j.Logger;

public class Util {
    
    /** Logging facility */
    static Logger logger = Logger.getLogger(Util.class);
    
    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     *
     * @param aFile is a file which already exists and can be read.
     */
     static public String getContents(File aFile) {
       
       StringBuilder contents = new StringBuilder();

       try {
	   // extract all text from this file
	   BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
	   try {
	       
	       String line = null; //not declared within while loop
	       while (( line = reader.readLine()) != null){
		   contents.append(line);
		   contents.append(System.getProperty("line.separator"));
	       }
	   }
	   finally {
	       reader.close();
	   }
       }
       catch (IOException ex){
	   ex.printStackTrace();
       }
       
       return contents.toString();
     }
     
     
     /**
      * Fetch the entire contents of a text file, and return it in an List of Strings.
      * A separator is used to define the splitting point that separates the Strings.
      * This style of implementation does not throw Exceptions to the caller.
      *
      * @param aFile is a file which already exists and can be read.
      */
     static public List<String> getContents(File aFile, String separator){
	 
	 List<String> contentList = new ArrayList<String>();
	 String[] contentStrings = Util.getContents(aFile).split(separator, -1);
	 for (String s : contentStrings){
	     contentList.add(s);
	 }
	 
	 return contentList;
     }
     
     
     /** Create a new file with the given text contents. Creates the directory structure if it does not yet
      * exist.
      * @param filename Absolute path of the file
      * @param content Text content to be written
      * @throws IOException If file already exists */
     static public void createFile(String filename, String content){
	 
	 // don't create emptry file
	 if (content.trim().length() == 0){
	     return;
	 }
	 
	 // protection against accidental overwrite
	 if (new File(filename).exists()){
	     //logger.warn("File '" + filename + "' already exists. Nothing done.");
	     return;
	 }
	 
	 // create file with directory structure
	 File targetFile = new File(filename);
	 File parent = targetFile.getParentFile();
	 if(!parent.exists() && !parent.mkdirs()){
	     throw new IllegalStateException("Couldn't create dir: " + parent);
	 }
	 
	 // write file content
	 try {
	 
	     // create new file
	     BufferedWriter tempWriter = new BufferedWriter(new FileWriter(targetFile));
	     tempWriter.write(content.trim());
	     tempWriter.close();	 
	 
	 } catch (Exception e){
	     logger.error("Exception while creating file: " + e.getMessage());
	 }
     }
     
     
     /** For intial testing */
     public static void main(String[] args) {
	 
	 Util.createFile("D:/Data/collections/UserCredibilityImages/imageLists/temp_1/1", "test");
     }
}


