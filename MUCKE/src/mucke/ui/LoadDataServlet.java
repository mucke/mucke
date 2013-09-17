/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import mucke.ui.helpers.*;

/**
 *
 * @author Alexandra 
 * The class is used by infinite scroll library. Every results page corresponds 
 * to a html page and because we can't have static html pages for every query,
 * we generate them automatically here. When a url matching the following format
 * /MuckeClient/pages/query=white/2.html is entered, the LoadDataServlet is 
 * called.
 */
public class LoadDataServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     * Parses the requested url and extracts the values corresponding to the
     * query and current page fields. It calls the loadImages method (which
     * calls the Mucke Search Service and retrieves the image results) and
     * displays the retrieved image results on output.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String[] urlTokens = request.getRequestURL().toString().split("/");
        String queryInfo = urlTokens[urlTokens.length - 2];
        String query = URLDecoder.decode(queryInfo.split("=")[1], "UTF-8");
        String page = urlTokens[urlTokens.length - 1].split("\\.")[0];

        ArrayList<ImageBean> images = loadImages(query, page);

        if (images == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet LoadDataServlet</title>");
                out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"/MuckeClient/faces/javax.faces.resource/style.css?ln=css\" />");
                out.println("</head>");
                out.println("<body>");


                out.println("<div id=\"content\" class=\"container clearfix\">");
                out.println("<ul>");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < images.size(); i++) {
                    ImageBean image = images.get(i);
                    builder.append("<li class=\"item\">\n");
                    builder.append("<a href=\"").append(image.getUrl()).
                            append("\" title=\"").append(image.getTitle()).
                            append("\"").append(" data-lightbox=\"roundtrip\"").
                            append(">\n<img src=\"").
                            append(image.getUrl()).append("\" alt=\"").
                            append(image.getTitle()).append("\"/>");
                    builder.append("</a>");
                    builder.append("<div class=\"box\">");
                    builder.append(image.getTitle());
                    builder.append("</div>");
                    builder.append("</li>\n");
                }
                out.println(builder.toString());
                out.println("</ul>");
                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    
    /*
     * Loads images matching the user input query, by calling the Mucke
     * Search REST Service. The images corresponding to the current page 
     * parameter are retrived as an JSON object which is parsed and turned into 
     * ImageBean objects.
     */
    public ArrayList<ImageBean> loadImages(String query, String currentPage) {
        try {
            URL url = new URL(MuckeController.buildRestQuery(query, currentPage));
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
                JSONObject photosJsonObject = (JSONObject) jsonObject.get(ConfigHelper.RESULTS_JSONS_OBJECT);

                long pagesNumber = (long) photosJsonObject.get(ConfigHelper.RESULTS_TOTAL_PAGES);

                if (Long.parseLong(currentPage) <= pagesNumber) {

                    JSONArray jsonPhotoArray = (JSONArray) photosJsonObject.get(ConfigHelper.RESULTS_IMAGES_JSON_ARRAY);

                    Iterator<JSONObject> iterator = jsonPhotoArray.iterator();


                    ArrayList<ImageBean> images = new ArrayList<ImageBean>();

                    while (iterator.hasNext()) {
                        JSONObject jsonImage = iterator.next();
                        ImageBean image = new ImageBean(jsonImage);
                        images.add(image);
                    }
                    return images;
                }

            } catch (ParseException ex) {
                Logger.getLogger(MuckeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return null;

    }
}
