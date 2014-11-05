package at.tuwien.mucke.plugin.bilkentdemo.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.payloads.DelimitedPayloadTokenFilter;
import org.apache.lucene.analysis.payloads.PayloadEncoder;

import java.io.Reader;

public class BilkentDemoPayloadAnalyzer extends Analyzer {

    private PayloadEncoder encoder;
    BilkentDemoPayloadAnalyzer(PayloadEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new WhitespaceTokenizer(reader);
        TokenStream filter = new LowerCaseFilter(source);
        filter = new DelimitedPayloadTokenFilter(filter, '|', encoder);
        return new TokenStreamComponents(source, filter);
    }
}
