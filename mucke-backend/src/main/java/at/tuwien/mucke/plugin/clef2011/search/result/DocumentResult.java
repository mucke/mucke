package at.tuwien.mucke.plugin.clef2011.search.result;

/**
 * Stores the most essential information about document results
 *
 * @author Ralf Bierig
 */
public class DocumentResult {

    private String id = new String();
    private float score;

    public DocumentResult(String id, float score) {
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