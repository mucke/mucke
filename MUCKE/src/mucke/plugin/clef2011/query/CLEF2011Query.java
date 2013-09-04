package mucke.plugin.clef2011.query;

import java.util.ArrayList;
import java.util.List;


/** 
 * Wraps the relevant parts of a CLEF topic
 * @author Ralf Bierig
 */
public class CLEF2011Query {

	private String topicID = "UNASSIGNED";
	private String en = "UNASSIGNED";
	private String de = "UNASSIGNED";
	private String fr = "UNASSIGNED";
	private List<String> images = new ArrayList<String>();
	private String narrative = "UNASSIGNED";
	
	
	/**
	 * @return the topicID
	 */
	public final String getTopicID() {
		return topicID;
	}
	
	
	/**
	 * @param topicID the topicID to set
	 */
	public final void setTopicID(String topicID) {
		this.topicID = topicID;
	}
	
	
	/**
	 * @return the en
	 */
	public final String getEn() {
		return en;
	}
	
	
	/**
	 * @param en the en to set
	 */
	public final void setEn(String en) {
		this.en = en;
	}
	
	
	/**
	 * @return the de
	 */
	public final String getDe() {
		return de;
	}
	
	
	/**
	 * @param de the de to set
	 */
	public final void setDe(String de) {
		this.de = de;
	}
	
	
	/**
	 * @return the fr
	 */
	public final String getFr() {
		return fr;
	}
	
	
	/**
	 * @param fr the fr to set
	 */
	public final void setFr(String fr) {
		this.fr = fr;
	}
	
	
	/**
	 * @return the images
	 */
	public final List<String> getImages() {
		return images;
	}
	
	
	/**
	 * @param images the images to set
	 */
	public final void setImages(List<String> images) {
		this.images = images;
	}


	/**
	 * @return the narrative
	 */
	public final String getNarrative() {
		return narrative;
	}


	/**
	 * @param narrative the narrative to set
	 */
	public final void setNarrative(String narrative) {
		this.narrative = narrative;
	}
}