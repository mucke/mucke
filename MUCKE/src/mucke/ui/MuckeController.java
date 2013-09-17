/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import mucke.ui.helpers.ConfigHelper;

/**
 *
 * @author Alexandra
 * 
 * This is the bean class in charge with images displaying from showImages.xhtml
 */
public class MuckeController {

    //retrived images
    private ArrayList<ImageBean> images = new ArrayList<>();
    //user input query
    private String query;

    /**
     * Creates a new instance of MuckeController
     */
    public MuckeController() {
    }

    /*
     * Searches for images matching the user input query, by calling the Mucke
     * Search REST Service. The images corresponding to the first page are 
     * retrived as an JSON object which is parsed and turned into ImageBean 
     * objects.
     */
    public String searchImages() {

        images.clear();

        if (query.length() == 0) {
            return "empty";
        }
        try {

            URL url = new URL(buildRestQuery(query, "1"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder jsonArrayString = new StringBuilder();
            while ((output = br.readLine()) != null) {
                jsonArrayString.append(output);
            }

            conn.disconnect();

            JSONParser parser = new JSONParser();
            try {

                JSONObject jsonObject = (JSONObject) parser.parse(jsonArrayString.toString());
                JSONObject photosObject = (JSONObject) jsonObject.get(ConfigHelper.RESULTS_JSONS_OBJECT);


                long pages = (long) photosObject.get(ConfigHelper.RESULTS_TOTAL_PAGES);

                if (pages > 0) {
                    JSONArray array = (JSONArray) photosObject.get(ConfigHelper.RESULTS_IMAGES_JSON_ARRAY);

                    Iterator<JSONObject> iterator = array.iterator();

                    while (iterator.hasNext()) {
                        JSONObject jsonImage = iterator.next();
                        ImageBean image = new ImageBean(jsonImage);
                        images.add(image);
                    }
                }


            } catch (ParseException ex) {
                Logger.getLogger(MuckeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }



        return "success";
    }

    /*
     * Creates the search url for interrogating the MUCKE Search Rest Service
     * using the following fields:
     * query - input string from user
     * page - current results page
     * perpage - the maximum number of results displayed on page
     */
    public static String buildRestQuery(String query, String page) {
        StringBuilder url = new StringBuilder(ConfigHelper.MUCKE_SERVICE_URL);
        url.append(ConfigHelper.QUERY).append("=").append(getEncodedString(query)).append("&");
        url.append(ConfigHelper.PAGE).append("=").append(page).append("&");
        url.append(ConfigHelper.PER_PAGE).append("=").append(ConfigHelper.PER_PAGE_DEFAULT);
        return url.toString();

    }

    /*
     * Retrives the html encoded query
     */
    public String getEncodedQuery() {
        return getEncodedString(query);
    }

    /*
     * Retrieves the encoded html form of the given parameter
     */
    public static String getEncodedString(String query) {
        try {
            if (query != null) {
                return URLEncoder.encode(query, "UTF-8");
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MuckeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<ImageBean> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageBean> images) {
        this.images = images;
    }
}
