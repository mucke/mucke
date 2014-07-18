package at.tuwien.mucke.search;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextFacetSearcher implements FacetSearcher {

    static Logger logger = Logger.getLogger(TextFacetSearcher.class);

    private ConfigurationManager configManager = null;
    private String searcherName = "UNDEFINED";

    /** Constructor */
    public TextFacetSearcher(String searcherName, ConfigurationManager configManager){
        this.configManager = configManager;
        this.searcherName = searcherName;
    }

    /**
     * Searches for documents based on a text query.
     *
     * @param queryString         The query that is used to search the text index
     * @param filterResults List of already existing results as a filter for the results found by
     *                      the search. Results outside that list will be ignored. If the result list is null, a normal
     *                      search will be performed with all results considered.
     */
    @Override
    public List<Result> search(String queryString, List<Result> filterResults) {

        List<Result> results = new ArrayList<>();

        logger.info("Extracting the index facet folder to be searched... ");
        String facet = configManager.getProperty(searcherName + ".facet");
        logger.info("facet: " + facet);
        String facetIndexfolder = configManager.getProperty(facet + ".indexfolder");
        logger.info("facetIndexFolder: " + facetIndexfolder);

        // logging
        logger.debug("TODO: Searching for text facets!");

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(facetIndexfolder)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);

            // extract search fields from configuration
            List<String> searchDocFields = configManager.getProperties(searcherName + ".facet.fields", true);
            String[] sFields = new String[searchDocFields.size()];
            for (int i = 0; i < searchDocFields.size(); i++) {
                sFields[i] = searchDocFields.get(i);
            }

            MultiFieldQueryParser multi = new MultiFieldQueryParser(Version.LUCENE_42, sFields, analyzer);
            multi.setDefaultOperator(QueryParser.Operator.OR);

            org.apache.lucene.search.Query query = multi.parse(queryString);
            System.out.println("		document query: " + query.toString());
            System.out.println("		number of results considered: " + System.getProperty(ConfigConstants.SEARCH_RESULTLIST_LENGTH));

            TopDocs topDocs = searcher.search(query, Integer.valueOf(System.getProperty(ConfigConstants.SEARCH_RESULTLIST_LENGTH)));
            ScoreDoc[] hits = topDocs.scoreDocs;

            // transform into Results objects
            for (int i = 0; i < hits.length; i++) {

                // get document from index
                Document doc = searcher.doc(hits[i].doc);

                // store docIDs (filename) and score
                Result result = new Result(doc.get("location"), doc.get("caption"), hits[i].score, doc.get("userid"));
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