package at.tuwien.mucke.search;

import java.net.URL;

/**
 * Stores the most essential information about search results.
 *
 * @author Ralf Bierig
 */
public class Result {

    private String id;
    private String title;
    private float score;
    private String userId;


    public Result(String id, String title, float score, String userId) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.userId = userId;
    }

    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the score
     */
    public final float getScore() {
        return score;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Re-set the score
     */
    public void setScore(float score) {
        this.score = score;
    }

}