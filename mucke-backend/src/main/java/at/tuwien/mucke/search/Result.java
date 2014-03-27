package at.tuwien.mucke.search;

import java.net.URL;

/**
 * Stores the most essential information about search results.
 *
 * @author Ralf Bierig
 */
public class Result {

    private String id;
    private float score;
    private String userId;


    public Result(URL id, float score, String userId) {
        this.id = id;
        this.score = score;
        this.userId = userId;
    }

    /**
     * @return the id
     */
    public final URL getId() {
        return id;
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

}