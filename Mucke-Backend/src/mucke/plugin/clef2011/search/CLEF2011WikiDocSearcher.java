package mucke.plugin.clef2011.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mucke.config.ConfigurationManager;
import mucke.plugin.clef2011.ConfigConstants;
import mucke.plugin.clef2011.search.result.DocumentResult;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Searches Wiki documents stored in the document index.
 * 
 * @author Ralf Bierig
 */
public class CLEF2011WikiDocSearcher {

    private ConfigurationManager configManager = null;
    static Logger logger = Logger.getLogger(ConfigurationManager.class);

    /** Constructor */
    public CLEF2011WikiDocSearcher(ConfigurationManager configManager) {
	this.configManager = configManager;
    }

    /**
     * Performs a single search on the CLEF2011 Wiki Doc index based on the given query
     * 
     * @param String queryText
     * @return TopDocs list of Lucene hits
     */
    public List<DocumentResult> searchDocuments(String queryText, String indexDirectory) {

	// search results
	List<DocumentResult> docResults = new ArrayList<DocumentResult>();
	
	try {
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDirectory)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);

	    // extract search fields from configuration
	    List<String> searchDocFields = configManager.getProperties(ConfigConstants.SEARCH_DOC_FIELDS, true);
	    String[] sFields = new String[searchDocFields.size()];
	    for (int i = 0; i < searchDocFields.size(); i++) {
		sFields[i] = searchDocFields.get(i);
	    }
	    
	    MultiFieldQueryParser multi = new MultiFieldQueryParser(Version.LUCENE_42, sFields, analyzer);
	    Query query = multi.parse(queryText);
	    System.out.println("		document query: " + query.toString());
	    System.out.println("		number of results considered: " + System.getProperty(ConfigConstants.RESULTLIST_DOC_LENGTH));
	    
	    TopDocs topDocs = searcher.search(query, Integer.valueOf(System.getProperty(ConfigConstants.RESULTLIST_DOC_LENGTH)));
	    ScoreDoc[] hits = topDocs.scoreDocs;

	    for (int i = 0; i < hits.length; i++) {

		// get document from index
		Document doc = searcher.doc(hits[i].doc);
		// store docIDs (filename) and score
		DocumentResult docResult = new DocumentResult(doc.get(System.getProperty(ConfigConstants.FIELD_WIKIDOC_FILENAME)),
			hits[i].score);
		docResults.add(docResult);
	    }

	    // searcher.doc(hits[i].doc)
	    reader.close();

	} catch (Exception e) {
	    System.out.println("Exception while searching for topic: '" + queryText + "': " + e.getMessage());
	    e.printStackTrace();
	}	
	
	return docResults;
    }

}