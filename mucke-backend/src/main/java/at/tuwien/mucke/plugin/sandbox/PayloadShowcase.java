package at.tuwien.mucke.plugin.sandbox;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.payloads.DelimitedPayloadTokenFilter;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class PayloadShowcase {

    public static void main(String s[]) {
        PayloadShowcase p = new PayloadShowcase();
        p.run();
    }

    public void run () {
        // Step 1: indexing
        MyPayloadIndexer indexer = new MyPayloadIndexer();
        indexer.index();
        // Step 2: searching
        MyPayloadSearcher searcher = new MyPayloadSearcher();
        searcher.search("red");
    }

    public class MyPayloadAnalyzer extends Analyzer {

        private PayloadEncoder encoder;
        MyPayloadAnalyzer(PayloadEncoder encoder) {
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

    public class MyPayloadIndexer {

        public MyPayloadIndexer() {}

        public void index() {
            try {
                Directory dir = FSDirectory.open(new File("D:/data/indices/sandbox"));
                Analyzer analyzer = new MyPayloadAnalyzer(new FloatEncoder());
                IndexWriterConfig iwconfig = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
                iwconfig.setSimilarity(new MyPayloadSimilarity());
                iwconfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

                // load mappings and classifiers
                HashMap<String, String> mappings = this.loadDataMappings();
                HashMap<String, HashMap> cMaps = this.loadData();

                IndexWriter writer = new IndexWriter(dir, iwconfig);
                indexDocuments(writer, mappings, cMaps);
                writer.close();

            } catch (IOException e) {
                System.out.println("Exception while indexing: " + e.getMessage());
            }
        }

        private void indexDocuments(IndexWriter writer, HashMap<String, String> fileMappings, HashMap<String, HashMap> concepts) throws IOException {

            Set fileSet = fileMappings.keySet();
            Iterator<String> iterator = fileSet.iterator();
            while (iterator.hasNext()){
                // unique file information
                String fileID = iterator.next();
                String filePath = fileMappings.get(fileID);
                // create a new, empty document
                Document doc = new Document();
                // path of the indexed file
                Field pathField = new StringField("path", filePath, Field.Store.YES);
                doc.add(pathField);
                // lookup all concept probabilities for this fileID
                Iterator<String> conceptIterator = concepts.keySet().iterator();
                while (conceptIterator.hasNext()){
                    String conceptName = conceptIterator.next();
                    HashMap conceptMap = concepts.get(conceptName);
                    doc.add(new TextField("concept", ("" + conceptName + "|").trim() + (conceptMap.get(fileID) + "").trim(), Field.Store.YES));
                }
                writer.addDocument(doc);
            }
        }

        public HashMap<String, String> loadDataMappings(){
            HashMap<String, String> h = new HashMap<>();
            h.put("1", "1.jpg");
            h.put("2", "2.jpg");
            h.put("3", "3.jpg");
            return h;
        }

        public HashMap<String, HashMap> loadData(){
            HashMap<String, HashMap> h = new HashMap<>();
            HashMap<String, String> green = new HashMap<>();
            green.put("1", "50.0");
            green.put("2", "1.0");
            green.put("3", "100.0");
            HashMap<String, String> red = new HashMap<>();
            red.put("1", "100.0");
            red.put("2", "50.0");
            red.put("3", "1.0");
            HashMap<String, String> blue = new HashMap<>();
            blue.put("1", "1.0");
            blue.put("2", "50.0");
            blue.put("3", "100.0");
            h.put("green", green);
            h.put("red", red);
            h.put("blue", blue);
            return h;
        }
    }

    class MyPayloadSimilarity extends DefaultSimilarity {

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

    public class MyPayloadSearcher {

        public MyPayloadSearcher() {}

        public void search(String queryString) {
            try {
                IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("D:/data/indices/sandbox")));
                IndexSearcher searcher = new IndexSearcher(reader);
                searcher.setSimilarity(new MyPayloadSimilarity());
                PayloadTermQuery query = new PayloadTermQuery(new Term("concept", queryString),
                        new AveragePayloadFunction());
                System.out.println("Query: " + query.toString());
                TopDocs topDocs = searcher.search(query, 999);
                ScoreDoc[] hits = topDocs.scoreDocs;
                System.out.println("Number of results:" + hits.length);


                // output
                for (int i = 0; i < hits.length; i++) {
                    Document doc = searcher.doc(hits[i].doc);
                    System.out.println("-> docid: " + doc.get("path") + " score: " + hits[i].score);
                }
                reader.close();

            } catch (Exception e) {
                System.out.println("Exception while searching: " + e.getMessage());
            }
        }
    }
}