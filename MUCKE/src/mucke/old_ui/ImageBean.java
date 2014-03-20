/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.ui;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

import mucke.ui.helpers.*;

/**
 *
 * @author Alexandra
 * The class defining an image object
 */
public class ImageBean implements Serializable {

    private double score;
    private String tags;
    private String title;
    private String url;

    /*
     * Constructs an ImageBean object given a JSON object
     */
    public ImageBean(JSONObject jsonImage) {
        try {
            this.score = Double.parseDouble((String) jsonImage.get(ConfigHelper.SCORE));
            this.tags = new String(jsonImage.get(ConfigHelper.TAGS).toString().getBytes(), "UTF-8");
            this.title = new String(jsonImage.get(ConfigHelper.TITLE).toString().getBytes(), "UTF-8");
            this.url = (String) jsonImage.get(ConfigHelper.URL);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ImageBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ImageBean() {
        
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
