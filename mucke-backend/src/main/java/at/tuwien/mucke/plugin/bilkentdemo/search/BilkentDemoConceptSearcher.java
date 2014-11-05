package at.tuwien.mucke.plugin.bilkentdemo.search;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.plugin.bilkentdemo.ConfigConstants;
import at.tuwien.mucke.plugin.bilkentdemo.index.BilkentDemoPayloadSimilarity;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BilkentDemoConceptSearcher {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(BilkentDemoConceptSearcher.class);

    protected ConfigurationManager configManager = null;

    public BilkentDemoConceptSearcher(ConfigurationManager configManager) {

        // configuration
        this.configManager = configManager;

    }

    public List<Result> search(String queryString) {

        List<Result> results = null;

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(configManager.getProperty(ConfigConstants.CONCEPT_INDEX_FOLDER))));
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new BilkentDemoPayloadSimilarity());
            PayloadTermQuery query = new PayloadTermQuery(new Term("concept", queryString),
                    new AveragePayloadFunction());
            System.out.println("Query: " + query.toString());
            TopDocs topDocs = searcher.search(query, 999);
            ScoreDoc[] hits = topDocs.scoreDocs;
            System.out.println("Number of results:" + hits.length);

            // output
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                logger.info("-> docid: " + doc.get("path") + " score: " + hits[i].score);
            }

            // build result objects
            results = new ArrayList<>();
            // transform into Results objects
            for (int i = 0; i < hits.length; i++) {

                // get document from index
                Document doc = searcher.doc(hits[i].doc);

                // store docIDs (filename) and score
                Result result = new Result(doc.get("path"), doc.get("path"), hits[i].score, doc.get("NO_USER"));
                results.add(result);

            }

            // cleanup
            reader.close();

        } catch (Exception e) {
            System.out.println("Exception while searching: " + e.getMessage());
        }

        return results;
    }
}