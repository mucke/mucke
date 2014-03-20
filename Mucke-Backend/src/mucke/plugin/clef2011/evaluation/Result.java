package mucke.plugin.clef2011.evaluation;
/** 
 * Represents a document result with all individual ASTERA scores and the final score. 
 * 
 * @author Serwah Sabetghadam and Ralf Bierig
 */
public class Result {
	
	private String topicId;
	private String rank;
	private String docNo;
	private String docScore;
	private String imageId;
	private String imageScore;
	private String metadataScore;
	private String finalScore;
	private boolean relevant = false;

	
	/** Constructor that does not define the metadata score */
	public Result(String topicId, String rank, String docNo, String docScore, String imageId, String imageScore, String finalScore) {
	    this(topicId, rank, docNo, docScore, imageId, imageScore, null, finalScore);
	}
	
	/** Constructor */
	public Result(String topicId, String rank, String docNo, String docScore, String imageId, String imgScore, String metadataScore, String combinedScore) {
		this.topicId = topicId;
		this.rank = rank;
		this.docNo = docNo;
		this.docScore = docScore;
		this.imageId = imageId;
		this.imageScore = imgScore;
		this.finalScore = combinedScore;
		this.metadataScore = metadataScore;
	}

	/**
	 * @return the topicId
	 */
	public final String getTopicId() {
	    return topicId;
	}

	/**
	 * @param topicId the topicId to set
	 */
	public final void setTopicId(String topicId) {
	    this.topicId = topicId;
	}

	/**
	 * @return the rank
	 */
	public final String getRank() {
	    return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public final void setRank(String rank) {
	    this.rank = rank;
	}

	/**
	 * @return the docNo
	 */
	public final String getDocNo() {
	    return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public final void setDocNo(String docNo) {
	    this.docNo = docNo;
	}

	/**
	 * @return the docScore
	 */
	public final String getDocScore() {
	    return docScore;
	}

	/**
	 * @param docScore the docScore to set
	 */
	public final void setDocScore(String docScore) {
	    this.docScore = docScore;
	}

	/**
	 * @return the imageId
	 */
	public final String getImageId() {
	    return imageId;
	}

	/**
	 * @param imageId the imageId to set
	 */
	public final void setImageId(String imageId) {
	    this.imageId = imageId;
	}

	/**
	 * @return the imageScore
	 */
	public final String getImageScore() {
	    return imageScore;
	}

	/**
	 * @param imageScore the imageScore to set
	 */
	public final void setImageScore(String imageScore) {
	    this.imageScore = imageScore;
	}

	/**
	 * @return the metadataScore
	 */
	public final String getMetadataScore() {
	    return metadataScore;
	}

	/**
	 * @param metadataScore the metadataScore to set
	 */
	public final void setMetadataScore(String metadataScore) {
	    this.metadataScore = metadataScore;
	}

	/**
	 * @return the finalScore
	 */
	public final String getFinalScore() {
	    return finalScore;
	}

	/**
	 * @param finalScore the finalScore to set
	 */
	public final void setFinalScore(String finalScore) {
	    this.finalScore = finalScore;
	}

	/**
	 * @return the relevant
	 */
	public final boolean isRelevant() {
	    return relevant;
	}

	/**
	 * @param relevant the relevant to set
	 */
	public final void setRelevant(boolean relevant) {
	    this.relevant = relevant;
	}
	
}