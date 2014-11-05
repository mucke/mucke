package at.tuwien.mucke.plugin.bilkentdemo.index;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

public class BilkentDemoPayloadSimilarity extends DefaultSimilarity {

    @Override
    public float scorePayload(int docID, int start, int end, BytesRef payload) {
        float pload = 1.0f;
        if (payload != null) {
            //pload = PayloadHelper.decodeFloat(payload.bytes);
            pload = PayloadHelper.decodeFloat(payload.bytes, payload.offset);
        }
        System.out.println("===>  docid: " + docID + " payload: " + pload);
        return pload;
    }
}