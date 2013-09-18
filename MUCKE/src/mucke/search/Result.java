package mucke.search;

/**
 * Stores the most essential information about search results
 * 
 * @author Ralf Bierig
 */
public class Result {

    private String id = new String();
    private float score;

    public Result(String id, float score) {
	this.id = id;
	this.score = score;
    }

    /**
     * @return the id
     */
    public final String getId() {
	return id;
    }

    /**
     * @return the score
     */
    public final float getScore() {
	return score;
    }

}