/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.ui.helpers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexandra
 * 
 * The class is in charge with loading static parameters from properties file.
 */
public class ConfigHelper {

    public static String SCORE;
    public static String TAGS;
    public static String URL;
    public static String TITLE;
    public static String MUCKE_SERVICE_URL;
    public static String PAGE;
    public static String PER_PAGE;
    public static String QUERY;
    public static long PER_PAGE_DEFAULT;
    public static String RESULTS_JSONS_OBJECT;
    public static String RESULTS_IMAGES_JSON_ARRAY;
    public static String RESULTS_TOTAL_IMAGES;
    public static String RESULTS_TOTAL_PAGES;
    public static String RESULTS_CURRENT_PAGE;
   
    static {
        try {
            Properties prop = new Properties();
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));

            SCORE = prop.getProperty("score");
            TAGS = prop.getProperty("tags");
            TITLE = prop.getProperty("title");
            URL = prop.getProperty("url");
            MUCKE_SERVICE_URL = prop.getProperty("muckeServiceURL");
            PAGE = prop.getProperty("page");
            PER_PAGE = prop.getProperty("perpage");
            QUERY = prop.getProperty("query");
            PER_PAGE_DEFAULT = Long.parseLong( prop.getProperty("perpage_default"));
            RESULTS_JSONS_OBJECT = prop.getProperty("resultsJSONObject");
            RESULTS_IMAGES_JSON_ARRAY = prop.getProperty("resultsImagesJSONArray");
            RESULTS_TOTAL_IMAGES = prop.getProperty("resultsTotalNumber");
            RESULTS_TOTAL_PAGES = prop.getProperty("resultsTotalPages");
            RESULTS_CURRENT_PAGE = prop.getProperty("resultsCurrentPage");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
