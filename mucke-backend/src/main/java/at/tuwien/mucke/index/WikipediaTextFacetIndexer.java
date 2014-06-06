package at.tuwien.mucke.index;

import at.tuwien.mucke.config.ConfigurationManager;
import edu.jhu.nlp.wikipedia.*;
import edu.jhu.nlp.language.Language;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

/**
 * Indexes the Wikipedia dump.
 *
 * @author Ralf Bierig
 */
public class WikipediaTextFacetIndexer implements FacetIndexer {

    protected String facetName = null;
    protected ConfigurationManager configManager = null;
    protected String contentFolder = null;
    protected String indexFolder = null;

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(WikipediaTextFacetIndexer.class);

    /**
     * Constructor
     */
    public WikipediaTextFacetIndexer(String facetName, ConfigurationManager configManager) {

        this.facetName = facetName;
        this.configManager = configManager;

        // check index parameters against errors
        this.checkParams();

        // extract content folder
        this.contentFolder = configManager.getProperty(this.facetName + ".contentfolder");
        logger.debug("contentFolder: " + contentFolder);

        // extract index folder
        this.indexFolder = configManager.getProperty(this.facetName + ".indexfolder");
        logger.debug("indexFolder: " + indexFolder);

    }

    /**
     * Indexes the Wikipedia dump
     */
    public void index() {

        // true creates a new index / false updates the existing index
        boolean create = false;

        // check if data directory exists
        logger.debug("wikipedia dump file = " + contentFolder);
        final File wikipediaDumpFile = new File(contentFolder);
        if (!wikipediaDumpFile.exists() || !wikipediaDumpFile.canRead()) {
            logger.error("Wikipedia dump file '" + wikipediaDumpFile.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path. ");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {
            logger.debug("Indexing to directory '" + this.indexFolder + "'...");

            Directory dir = FSDirectory.open(new File(this.indexFolder));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42, analyzer);

            if (create) {
                // Create new index, remove previous index
                logger.debug("Creating a new index in directory: '" + this.indexFolder + "'...");
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to existing index
                logger.debug("Updating the index in directory: '" + this.indexFolder + "'...");
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // index
            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocuments(writer, wikipediaDumpFile);
            writer.close();

            // time stamping
            Date end = new Date();
            logger.debug("Indexing time: " + (end.getTime() - start.getTime()) + " total milliseconds");

        } catch (IOException e) {
            logger.error("Exception: " + e.getMessage());
        }
    }

    /**
     * Indexes individual pages from the wikipedia dump with a set of configured IndexFieldGenerators.
     *
     * @param writer               A writing handle to the index
     * @param file                      The file to be indexed
     * @throws IOException
     */
    private void indexDocuments(final IndexWriter writer, File file) throws IOException {

        // do not try to index files that cannot be read
        if (file.canRead()) {

            if (file.isDirectory()) {
                String[] files = file.list();

                // an IO error could occur
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocuments(writer, new File(file, files[i]));
                    }
                }

            } else {

                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    // at least on windows, some temporary files raise this exception with an "access denied" message
                    // checking if the file can be read doesn't help
                    return;
                }

                try {

                    // create a new, empty document
                    final Document doc = new Document();

                    // access wikipedia dump file
                    WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(file.getAbsolutePath());

                    try {
                        wxsp.setPageCallback(new PageCallbackHandler() {
                            public void process(WikiPage page) {

                                // default is to filter out stubs, special pages, redirection and disambiguation pages
                                // TODO makes this dependent on a filter that the user can configure
                                // TODO ALLOW_REDIRECT_PAGE = true
                                // TODO ALLOW_DISAMBIGUATION_PAGE = true
                                // TODO ALLOW_SPECIAL_PAGE = true
                                // TODO ALLOW_STUB_PAGE = true
                                if (!page.isRedirect() && !page.isDisambiguationPage() && !page.isSpecialPage() && !page.isStub()) {
                                    logger.info("Redirection Page '" + page.getTitle().trim() + "' excluded!");
                                    return;
                                }

                                // facetid is wikipedia ID
                                // check if docId was read successfully, stop if not
                                if (page.getID() == null || page.getID().length() == 0) {
                                    logger.error("Facet Id unknown for wikipedia article '" + page.getTitle() + "'. Nothing done.");
                                    return;
                                }

                                // id
                                doc.add(new IntField("id", new Integer(page.getID()), Field.Store.YES));

                                // title
                                doc.add(new TextField("title", page.getTitle(), Field.Store.YES));

                                // cleaned wikipedia text --- excludes some Wiki markup
                                doc.add(new TextField("text", page.getText(), Field.Store.YES));

                                // original wikipedia text --- excludes some Wiki markup
                                doc.add(new TextField("wikitext", page.getWikiText(), Field.Store.YES));

                                // infobox
                                InfoBox infoBox = page.getInfoBox();
                                if (infoBox != null) {
                                    doc.add(new TextField("infobox", page.getInfoBox().dumpRaw(), Field.Store.YES));
                                }

                                // links
                                Vector links = page.getLinks();
                                String linksString = "";
                                for (int i = 0; i < links.size(); i++) {
                                    linksString = linksString + links.get(i);
                                    if (i < (links.size() - 1)) {
                                        linksString = linksString + ";";
                                    }
                                }
                                doc.add(new TextField("links", linksString.trim(), Field.Store.YES));

                                // categories
                                Vector categories = page.getCategories();
                                String categoriesString = "";
                                for (int i = 0; i < categories.size(); i++) {
                                    categoriesString = categoriesString + categories.get(i);
                                    if (i < (categories.size() - 1)) {
                                        categoriesString = categoriesString + ";";
                                    }
                                }
                                doc.add(new TextField("categories", categoriesString.trim(), Field.Store.YES));

                                // redirect page
                                if (page.getRedirectPage() != null){
                                    doc.add(new TextField("redirectPage", page.getRedirectPage(), Field.Store.YES));
                                }

                                // translated titles for French, German and Spanish
                                if (page.getTranslatedTitle(Language.FRENCH) != null){
                                    doc.add(new TextField("translatedTitleFR", page.getTranslatedTitle(Language.FRENCH), Field.Store.YES));
                                }
                                if (page.getTranslatedTitle(Language.GERMAN) != null){
                                    doc.add(new TextField("translatedTitleFR", page.getTranslatedTitle(Language.GERMAN), Field.Store.YES));
                                }
                                if (page.getTranslatedTitle(Language.SPANISH) != null){
                                    doc.add(new TextField("translatedTitleFR", page.getTranslatedTitle(Language.SPANISH), Field.Store.YES));
                                }

                                // write document to index
                                try {
                                    logger.debug("Adding wikipedia page id " + page.getID() + " about '" + page.getTitle().trim() + "'");
                                    writer.addDocument(doc);
                                } catch (Exception e) {
                                    logger.error("Exception while writing index: " + e.getMessage());
                                }
                            }

                        });

                        wxsp.parse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } finally {
                    fis.close();
                }
            }
        }

}

    @Override
    public boolean checkParams() {

        // checks content folder
        String indexContentFolderProperty = this.facetName + ".contentfolder";
        if (!configManager.isProperty(indexContentFolderProperty)) {
            logger.error("No content folder provided for facet index " + this.facetName +
                    ". You must declare a content folder in the properties.");
            return false;
        }

        // checks index folder
        String indexFolderProperty = this.facetName + ".indexfolder";
        if (!configManager.isProperty(indexFolderProperty)) {
            logger.error("No index folder provided for facet index " + this.facetName +
                    ". You must declare an index folder in the properties.");
            return false;
        }
        return true;
    }

    @Override
    public String explainParams() {
        return null;
    }
}