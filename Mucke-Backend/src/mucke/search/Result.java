package mucke.search;

import java.net.URI;

/**
 * Stores the most essential information about search results.
 * 
 * @author Ralf Bierig
 */
public class Result {

    private URI id;
    private float score;

    public Result(URI id, float score) {
	this.id = id;
	this.score = score;
    }

    /**
     * @return the id
     */
    public final URI getId() {
	return id;
    }

    /**
     * @return the score
     */
    public final float getScore() {
	return score;
    }

}