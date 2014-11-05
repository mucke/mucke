package at.tuwien.mucke.plugin.payloadtest.search;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

/**
 * Adds the payload, if existing, to adjust the scoring
 */
public class PayloadSimilarity extends DefaultSimilarity {

    static Logger logger = Logger.getLogger(PayloadSimilarity.class);

    @Override
    public float scorePayload(int docID, int start, int end, BytesRef payload) {

        float pload = 1.0f;
        if (payload != null) {
            pload = PayloadHelper.decodeFloat(payload.bytes);
        }

        logger.info("===>  docid: " + docID + " payload: " + pload);
        return pload;
    }
}
