package mucke.plugin.clef2011.search;

import org.apache.lucene.search.similarities.DefaultSimilarity;

public class TFSimilarity extends DefaultSimilarity {

    public float idf(long docFreq, long numDocs) {
	return 1;
    }

}