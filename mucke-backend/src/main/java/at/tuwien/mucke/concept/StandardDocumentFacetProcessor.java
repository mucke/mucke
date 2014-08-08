package at.tuwien.mucke.concept;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.documentmodel.ImageFacet;
import at.tuwien.mucke.documentmodel.TagFacet;
import at.tuwien.mucke.documentmodel.TextFacet;
import at.tuwien.mucke.search.Result;
import at.tuwien.mucke.search.SearchManager;
import at.tuwien.mucke.util.Util;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard processor of transforming each of the three document facets (text, tag and image) into concepts.
 * Use inheritance to selectively overwrite these methods with more specific functionality if needed.
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public class StandardDocumentFacetProcessor implements DocumentFacetProcessor {

    /**
     * Logging facility
     */
    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(StandardDocumentFacetProcessor.class);

    protected ConfigurationManager configurationManager = null;


    /** Constructor */
    public StandardDocumentFacetProcessor(ConfigurationManager configurationManager){
        this.configurationManager = configurationManager;
    }


    /** TextFacets are per default translated into Wikipedia concepts. It searches the Wikipedia index that is created
     * from the Wikipedia dump index (as created by the WikipediaTextFacetIndexer). This means, that this implementation
     * has a dependency against WikipediaTextFacetIndexer which requires its index to be in place before using this
     * FacetProcessor.
     *
     * @param textFacet The TextFacet that is to be processed
     * @return List of concepts as detected from the Wikipedia index
     * @see at.tuwien.mucke.index.WikipediaTextFacetIndexer
     */
    public List<Concept> process(TextFacet textFacet) {

        System.out.println(" Processing text facet to Wikipedia...");

        List<Concept> concepts = new ArrayList<>();

        // searches for Wikipedia concepts
        SearchManager searchManager = new SearchManager(this.configurationManager);

        List<Result> results = new ArrayList<>();

        String facetIndexfolder = configurationManager.getProperty(ConfigConstants.WIKIPEDIA_INDEXFOLDER);
        logger.info("facetIndexFolder: " + facetIndexfolder);

        try {

            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(facetIndexfolder)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);

            // define all fiels are shall be searched
            String[] sFields = {"title", "text"};

            MultiFieldQueryParser multi = new MultiFieldQueryParser(Version.LUCENE_42, sFields, analyzer);
            multi.setDefaultOperator(QueryParser.Operator.OR);

            org.apache.lucene.search.Query query = multi.parse(textFacet.getContent());
            TopDocs topDocs = searcher.search(query, 100); // unlimited concepts are considered
            ScoreDoc[] hits = topDocs.scoreDocs;

            // transform into Results objects
            for (int i = 0; i < hits.length; i++) {

                // get document from index
                Document doc = searcher.doc(hits[i].doc);

                // TODO generate URl from title, see: http://en.wikipedia.org/wiki/Wikipedia:Page_name for how it is done here
                // TODO: Does this always work? Needs testing!
                //URI conceptURI = new URI("http://en.wikipedia.org/wiki/" + doc.get("title").replaceAll(" ", "_"));
                Concept concept = new Concept(doc.get("title"), Concept.CONCEPT_TYPE_TEXT);
                concepts.add(concept);

            }

            reader.close();

        } catch (Exception e) {
            logger.error("Exception while trying to extract concepts for TextFacet '" + textFacet.getId() + "': " + e.getMessage());
            e.printStackTrace();
        }

        return concepts;
    }

    @Override
    public List<Concept> process(ImageFacet imageFacet) {
        System.out.println("Processing image facet");
        return null;
    }

    @Override
    public List<Concept> process(TagFacet tagFacet) {
        System.out.println("Processing tag facet");
        return null;
    }
}