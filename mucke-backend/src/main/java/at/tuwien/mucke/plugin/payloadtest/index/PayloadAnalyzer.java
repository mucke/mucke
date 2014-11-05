package at.tuwien.mucke.plugin.payloadtest.index;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.payloads.DelimitedPayloadTokenFilter;
import org.apache.lucene.analysis.payloads.PayloadEncoder;

import java.io.IOException;
import java.io.Reader;


/**
 *
 * */
public class PayloadAnalyzer extends Analyzer {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(PayloadConceptIndexer.class);

    private PayloadEncoder encoder;

    PayloadAnalyzer(PayloadEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

        Tokenizer source = new WhitespaceTokenizer(reader);
        TokenStream filter = new LowerCaseFilter(source);

        // Payload information is expected in the following form:
        // The quick|2.0 red|2.0 fox|10.0 jumped|5.0 over the lazy|2.0 brown|2.0 dogs|10.0
        // 'quick', 'red', 'lazy', and 'brown' are assigned to 2.0, 'fox' and 'dogs' are assigned
        // to 10.0, and 'jumped' are assigned to 5.0. The | is the separator between word and value.
        filter = new DelimitedPayloadTokenFilter(filter, '|', encoder);

        return new TokenStreamComponents(source, filter);
    }

}