package at.tuwien.mucke.plugin.clef2011.evaluation;

/** Contains the relation between a topic and its relevance. */

import java.util.ArrayList;
import java.util.List;

public class TopicRelevancyObj {

    String topicId;
    List<Boolean> relevancy = new ArrayList<Boolean>();

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public List<Boolean> getRelevancy() {
        return relevancy;
    }

    public void setRelevancy(List<Boolean> relevancy) {
        this.relevancy = relevancy;
    }

}
