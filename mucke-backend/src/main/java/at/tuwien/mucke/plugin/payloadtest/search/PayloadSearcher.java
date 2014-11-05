package at.tuwien.mucke.plugin.payloadtest.search;

import at.tuwien.mucke.config.ConfigurationManager;
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

public class PayloadSearcher {

    static Logger logger = Logger.getLogger(PayloadSearcher.class);

    private ConfigurationManager configManager = null;
    private String searcherName = "UNDEFINED";

    /** Constructor */
    public PayloadSearcher(String searcherName, ConfigurationManager configManager){
        this.configManager = configManager;
        this.searcherName = searcherName;
    }

    /**
     * Searches for documents based on a text query.
     * @param queryString The query that is used to search
     */
    public List<Result> search(String queryString) {

        // for results
        List<Result> results = new ArrayList<>();
        String conceptIndexfolder = configManager.getProperty("conceptindexer.indexfolder");

        try {
            // access the index and create a searcher
            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(conceptIndexfolder)));
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new PayloadSimilarity());

            // create query
            PayloadTermQuery query = new PayloadTermQuery(new Term("concept", queryString),
                    new AveragePayloadFunction());
           logger.info("Query: " + query.toString());

            // searching...
            TopDocs topDocs = searcher.search(query,999);
            ScoreDoc[] hits = topDocs.scoreDocs;

            // transform into Results objects
            for (int i = 0; i < hits.length; i++) {

                // get document from index
                Document doc = searcher.doc(hits[i].doc);

                // store docIDs (filename) and score
                Result result = new Result(doc.get("path"), doc.get("path"), hits[i].score, "NO_USER");
                results.add(result);

            }

            reader.close();

        } catch (Exception e) {
            logger.error("Exception while searching for '" + queryString + "': " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

}