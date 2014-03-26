package at.tuwien.mucke.plugin.clef2011.search.result;

/**
 * Stores the most essential search result information
 * which includes information about the image and the
 * document it relates to (including the scores for both).
 *
 * @author Ralf Bierig
 */
public class DocumentImageResult {

    private String docId;
    private float docScore;
    private String imageId;
    private float imageScore;

    public DocumentImageResult(String docId, float docScore, String imageId, float imageScore) {
        this.docId = docId;
        this.docScore = docScore;
        this.imageId = imageId;
        this.imageScore = imageScore;
    }

    /**
     * @return the docId
     */
    public final String getDocId() {
        return docId;
    }

    /**
     * @return the docScore
     */
    public final float getDocScore() {
        return docScore;
    }

    /**
     * @return the imageId
     */
    public final String getImageId() {
        return imageId;
    }

    /**
     * @return the imageScore
     */
    public final float getImageScore() {
        return imageScore;
    }


}