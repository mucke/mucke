package at.tuwien.mucke.index;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.util.Util;
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

import java.io.*;
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
    private static int counter = 0;
    protected File statusFile = null;
    protected String lastStatus = null;
    private String finishStatusMarker = "FINISHED";

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

        // define status file
        statusFile = new File(indexFolder + "/" + "WIKIPEDIA_INDEX_STATUS.LOG");

        // read last status, stop if already finished
        this.lastStatus = readStatus();
        logger.info("last status: '" + this.lastStatus + "' log file: '" + statusFile.getAbsolutePath() + "'");

    }

    /**
     * Indexes the Wikipedia dump
     */
    public void index() {

        // stop if index has already been built
        if (this.lastStatus.endsWith(finishStatusMarker)){
            logger.info("Wikipedia Index has already been build. Index folder: " + this.indexFolder);
            return;
        }

        // true creates a new index / false updates the existing index
        boolean create = false;

        // check if data directory exists
        final File wikipediaDumpFile = new File(contentFolder);
        if (!wikipediaDumpFile.exists() || !wikipediaDumpFile.canRead()) {
            logger.error("Wikipedia dump file '" + wikipediaDumpFile.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path. ");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {

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
            writer.commit();
            writer.close();

            // time stamping
            Date end = new Date();
            logger.debug("Indexing time: " + (end.getTime() - start.getTime()) + " total milliseconds for " + WikipediaTextFacetIndexer.counter + " articles.");

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

        // reset the file counter
        WikipediaTextFacetIndexer.counter = 0;

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

                    // access wikipedia dump file
                    WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(file.getAbsolutePath());

                    try {
                        wxsp.setPageCallback(new PageCallbackHandler() {
                            public void process(WikiPage page) {

                                // skip pages already indexed
                                //logger.info("reading status and converting: " + Integer.parseInt(readStatus()));
                                //logger.info("id converting: " +Integer.parseInt(page.getID().trim()));

                                if (Integer.parseInt(page.getID().trim()) <= Integer.parseInt(readStatus())){
                                    logger.info("O Wikipage id " + page.getID() + " skipped!");
                                    return;
                                }

                                // create a new, empty document
                                Document doc = new Document();

                                // field storage trigger
                                Field.Store storePolicy = Field.Store.YES;

                                // default is to filter out stubs, special pages, redirection and disambiguation pages
                                // TODO makes this dependent on a filter that the user can configure
                                // TODO ALLOW_REDIRECT_PAGE = true
                                // TODO ALLOW_DISAMBIGUATION_PAGE = true
                                // TODO ALLOW_SPECIAL_PAGE = true
                                // TODO ALLOW_STUB_PAGE = true
                                if (page.isRedirect() || page.isDisambiguationPage() || page.isSpecialPage() || page.isStub()) {
                                    //logger.info("- Excluding Redirection / Disambiguation / Special / Stub Wikipedia page id " + page.getID() + " about '"  + page.getTitle().trim() + "'");
                                    return;
                                }

                                // facetid is wikipedia ID
                                // check if docId was read successfully, stop if not
                                if (page.getID() == null || page.getID().length() == 0) {
                                    logger.error("Facet Id unknown for wikipedia article '" + page.getTitle() + "'. Nothing done.");
                                    return;
                                }

                                // id
                                String id = page.getID();
                                doc.add(new IntField("id", new Integer(id), storePolicy));
                                //logger.info("id ");
                                //logger.info("id: " + page.getID());

                                // title
                                String title = page.getTitle();
                                doc.add(new TextField("title", title, storePolicy));
                                //logger.info("title ");
                                //logger.info("title: " + page.getTitle());

                                // text
                                String text = page.getText();
                                if (text != null){
                                    text = text.trim();
                                    doc.add(new TextField("text", text, storePolicy));
                                } else {
                                    logger.info("-> No text found");
                                }
                                //logger.info("text ");
                                //logger.info("TEXT: " + page.getText());


                                // original wikipedia text --- excludes some Wiki markup
                                String wikiText = page.getWikiText();
                                if (wikiText != null){
                                    wikiText = wikiText.trim();
                                    doc.add(new TextField("text", text, storePolicy));
                                } else {
                                    logger.info("-> No wikitext found");
                                }

                                // infobox
                                // NOTE: try-block fixes a current bug with this library when extracting certain faulty infoboxes
                                // NOTE: faulty infoboxes are excluded from the index to allow the indexer to continue
                                try {
                                    InfoBox ib = page.getInfoBox();
                                    if (ib != null) {
                                        String infoBox = page.getInfoBox().toString();
                                        doc.add(new TextField("infobox", infoBox, storePolicy));
                                    }
                                } catch (Exception e) {
                                    logger.error("Exception while extracting infobox from document " + page.getID() + " with title '" + page.getTitle() + "': " + e.getMessage());
                                    logger.error("Therefore, no infobox included for Wikipedia page " + page.getID());
                                }

                                // links

                                // NEW
                                Vector<String> links = page.getLinks();
                                for (String link : links){
                                    if (link != null & link.trim().length() > 0){
                                        doc.add(new TextField("link", link.trim(), storePolicy));
                                        //logger.info("-> link: " + link.trim());
                                    }
                                }

                                // categories
                                Vector<String> categories = page.getCategories();
                                for (String category : categories){
                                    if (category != null & category.trim().length() > 0){
                                        doc.add(new TextField("category", category.trim(), storePolicy));
                                        //logger.info("-> category: " + category.trim());
                                    }
                                }

                                // redirect page
                                String redirectPage = page.getRedirectPage();
                                if (page.getRedirectPage() != null && page.getRedirectPage().length() > 0){
                                    doc.add(new TextField("redirectPage", redirectPage, storePolicy));
                                    //logger.info("redirectpage ");
                                    //logger.info("redirect: " + page.getRedirectPage());
                                }

                                // translated titles for French, German and Spanish
                                String translatedTitleFR = page.getTranslatedTitle(Language.FRENCH);
                                if (page.getTranslatedTitle(Language.FRENCH) != null && page.getTranslatedTitle(Language.FRENCH).length() > 0){
                                    doc.add(new TextField("translatedTitleFR", page.getTranslatedTitle(Language.FRENCH), storePolicy));
                                    //logger.info("translatedTitleFR ");
                                    //logger.info("translate: " + page.getTranslatedTitle(Language.FRENCH));
                                }
                                String translatedTitleDE = page.getTranslatedTitle(Language.GERMAN);
                                if (page.getTranslatedTitle(Language.GERMAN) != null && page.getTranslatedTitle(Language.GERMAN).length() > 0){
                                    doc.add(new TextField("translatedTitleDE", translatedTitleDE, storePolicy));
                                    //logger.info("translatedTitleDE ");
                                    //logger.info("translate: " + page.getTranslatedTitle(Language.GERMAN));
                                }
                                String translatedTitleES = page.getTranslatedTitle(Language.SPANISH);
                                if (page.getTranslatedTitle(Language.SPANISH) != null && page.getTranslatedTitle(Language.SPANISH).length() > 0){
                                    doc.add(new TextField("translatedTitleES", translatedTitleES, storePolicy));
                                    //logger.info("translatedTitleES ");
                                    //logger.info("translate: " + page.getTranslatedTitle(Language.SPANISH));
                                }

                                // write document to index
                                try {

                                    logger.debug("[" + WikipediaTextFacetIndexer.counter + "] + Adding Wikipedia page id " + page.getID() + " about '" + page.getTitle().trim() + "'");
                                    writer.addDocument(doc);
                                    WikipediaTextFacetIndexer.counter++;
                                    // keep marker
                                    writeStatus(page.getID().trim());

                                } catch (Exception e) {
                                    logger.error("Exception while writing index: " + e.getMessage());
                                }

                            }

                        });

                        wxsp.parse();
                        logger.info("AFTER wxsp.parse()");
                        writeStatus(readStatus() + "," + finishStatusMarker);
                        logger.info("Status FINISH written: " + finishStatusMarker);

                    } catch (Exception e) {
                        logger.error("Exception while writing index: " + e.getMessage());
                        e.printStackTrace();
                    }


                } finally {
                    fis.close();
                }
            }
        }

        return;
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

    /** Writes the status (i.e. the current position) of the Wikipedia indexing process. This allows
     * incremental indexing.
     * @param status A marker that identifies the position in the Wikipedia indexing process */
    public void writeStatus(String status){
        Util.putContents(statusFile, status, false);
    }

    /** Reads the status (i.e. the current position) of the Wikipedia indexing process. This allows
     * incremental indexing. Creates the marker file if it does not yet exists.
     * @return marker A marker that identifies the position in the Wikipedia indexing process */
    public String readStatus(){

        // creates the file if it does not yet exist
        if (!statusFile.exists()){
            Util.putContents(statusFile, "0", false);
        }

        return Util.getContents(statusFile ,false);
    }
}