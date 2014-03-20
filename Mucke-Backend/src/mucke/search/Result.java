package mucke.search;

import java.net.URL;

/**
 * Stores the most essential information about search results.
 * 
 * @author Ralf Bierig
 */
public class Result {

    private URL id;
    private float score;

    public Result(URL id, float score) {
	this.id = id;
	this.score = score;
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

}