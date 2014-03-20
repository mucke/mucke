package mucke.plugin.clef2011.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mucke.config.ConfigurationManager;
import mucke.plugin.clef2011.evaluation.Result;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import mucke.plugin.clef2011.ConfigConstants;

import mucke.util.Logger;
import mucke.plugin.clef2011.search.result.DocumentImageResult;
import mucke.plugin.clef2011.search.result.DocumentImageResultList;
import mucke.plugin.clef2011.search.result.DocumentResult;
import mucke.plugin.clef2011.search.result.ImageResult;

/**
 * Standard Lucene Searcher. Uses a basic unstructured index to search files.
 * 
 * @author Ralf Bierig
 */
public class CLEF2011WikiMetaSearcher {

    private ConfigurationManager configManager = null;

    /** Constructor */
    public CLEF2011WikiMetaSearcher(ConfigurationManager configManager) {
	this.configManager = configManager;
    }

    /**
     * Performs a single search on the CLEF2011 Wiki Doc index based on the given queryText and filters based on the given documentID
     * 
     * @param String queryText
     * @param String documentID
     * @return List<String> imageIDs
     */
    public List<ImageResult> searchMetadata(String queryText, String documentID) {

	// search results
	List<ImageResult> imgResults = new ArrayList<ImageResult>();

	try {
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(System.getProperty(ConfigConstants.WIKI_META_INDEXDIR))));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);

	    searcher.setSimilarity(new TFSimilarity());

	    // extract image search fields from configuration
	    List<String> searchDocFields = configManager.getProperties(ConfigConstants.SEARCH_IMG_FIELDS, true);
	    String[] iFields = new String[searchDocFields.size()];
	    for (int i = 0; i < searchDocFields.size(); i++) {
		iFields[i] = searchDocFields.get(i);
	    }
	    
	    MultiFieldQueryParser multi = new MultiFieldQueryParser(Version.LUCENE_42, iFields, analyzer);
	    multi.setDefaultOperator(Operator.OR);

	    Query query = multi.parse(queryText);
	    System.out.println("		metadata query: " + query.toString());

	    // filtering
	    //Filter docIDFilter = null;
	    //if (System.getProperty(ConfigConstants.FILTER_META_FIELD) != null) {
	    //	docIDFilter = new PrefixFilter(new Term(System.getProperty(ConfigConstants.FILTER_META_FIELD), documentID));
	    //	System.out.println("	Filtering based on field: " + docIDFilter.toString());
	    //}
	   
	    TopDocs topDocs = searcher.search(query, //docIDFilter,
		    Integer.valueOf(System.getProperty(ConfigConstants.RESULTLIST_META_LENGTH)));
	    ScoreDoc[] hits = topDocs.scoreDocs;

	    for (int i = 0; i < hits.length; i++) {

		// get document from index
		Document doc = searcher.doc(hits[i].doc);
		// store docIDs (filename) and score
		ImageResult imgResult = new ImageResult(doc.get(System.getProperty(ConfigConstants.FIELD_WIKIMETA_IMAGEID)), hits[i].score);
		imgResults.add(imgResult);
	    }
	    reader.close();

	} catch (Exception e) {
	    System.out.println("IOException while searching for documentID: " + documentID + ": " + e.getMessage());
	    e.printStackTrace();
	}
	return imgResults;
    }

}