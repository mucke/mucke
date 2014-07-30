package at.tuwien.mucke.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Logging facility
     */
    public static  Logger logger = Logger.getLogger(Util.class);



    // TODO check if you can exchange getContents with this code?
    //static String readFile(String path, Charset encoding)
    //        throws IOException {
    //    byte[] encoded = Files.readAllBytes(Paths.get(path));
    //    return new String(encoded, encoding);
    //}


    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     *
     * @param aFile is a file which already exists and can be read.
     */
    public static String getContents(File aFile, boolean lineSpeparated) {

        StringBuilder contents = new StringBuilder();

        try {
            // extract all text from this file
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
            try {

                String line = null; //not declared within while loop
                while ((line = reader.readLine()) != null) {
                    contents.append(line);

                    // return contents line-wise, might disturb number formating (e.g. turning text to integer,
                    // use lineSeparated = false in that case
                    if (lineSpeparated){
                        contents.append(System.getProperty("line.separator"));
                    }

                }
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
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
    public static List<String> getContents(File aFile, boolean lineSeparator, String separator) {

        List<String> contentList = new ArrayList<String>();
        String[] contentStrings = Util.getContents(aFile, lineSeparator).split(separator, -1);
        for (String s : contentStrings) {
            contentList.add(s);
        }

        return contentList;
    }

    /**
     * Write the given contents to the given file either in overwriting mode or in attaching mode.
     *
     * @param aFile A file
     * @param content The content to be written
     * @param append Adds the content to the existing content of the file, if set ot true, overwrites otherwise
     */
    public static void putContents(File aFile, String content, boolean append) {

        // init
        Writer writer = null;

        // make sure file exists
        if (!aFile.exists()) {
            Util.createFile(aFile.getAbsolutePath());
        }

        // write content
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(aFile), "UTF-8"));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            logger.error("Error writing content to file: " + aFile);
        } finally {
            try {
                // Close the writer regardless of what happens
                writer.close();
            } catch (Exception e) {
                logger.error("Error while closing file: " + aFile.getAbsolutePath());
            }
        }

    }

    /**
     * Create an empty new file. Creates the directory structure if it does not yet
     * exist.
     *
     * @param filename Absolute path of the file
     *
     * @throws IOException If file already exists
     */
     public static void createFile(String filename) {

        // protection against accidental overwrite
        if (new File(filename).exists()) {
            logger.warn("File '" + filename + "' already exists. Nothing done.");
            return;
        }

        // create file with directory structure
        File targetFile = new File(filename);
        File parent = targetFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        try {
            targetFile.createNewFile();
        } catch (IOException e){
            logger.error("Error while creating empty file '" + filename + "': " + e.getMessage());
        }

    }

    /** Validates the given URI */
    public static boolean validateHTTP_URI(String uri) {
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e1) {
            return false;
        }
        return "http".equals(url.getProtocol());
    }

    /**
     * For intial testing
     */
    public static void main(String[] args) {

        Util.putContents(new File("D:/Data/TEST.TXT"), "1342122", false);
        String numberString = Util.getContents(new File("D:/Data/TEST.TXT"), false);

        logger.info("String: " + numberString);
        //logger.info("String: " + Integer.parseInt(numberString));
        logger.info("String converted to number with non-numbery stuff removed: " + Integer.parseInt(numberString.replaceAll("\\s", "")));

    }
}


