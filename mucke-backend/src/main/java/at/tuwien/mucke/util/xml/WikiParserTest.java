package at.tuwien.mucke.util.xml;

import edu.jhu.nlp.wikipedia.*;
import org.apache.log4j.Logger;


/**
 * Tests the WikiParser provided here: https://code.google.com/p/wikixmlj/
 */
public class WikiParserTest {

    static int counter = 0;

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(WikiParserTest.class);

    public static void main(String[] args){

        WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(
                "C:/Data/enwiki-20120502-pages-articles/enwiki-20120502-pages-articles.xml");

        try {
            wxsp.setPageCallback(new PageCallbackHandler() {
                public void process(WikiPage page) {

                    WikiParserTest.counter++;

                    if (!page.isDisambiguationPage() || !page.isStub() || !page.isSpecialPage() || !page.isRedirect()) {
                        //logger.info("ID: " + page.getID() + " Title: " + page.getTitle());
                        //logger.info(page.getCategories());
                        //InfoBox infoBox = page.getInfoBox();
                        //logger.info(page.getLinks());
                        //logger.info(page.getTranslatedTitle(page.getTitle()));
                        //if (infoBox != null){
                            //logger.info(infoBox.dumpRaw());
                        //}
                        //logger.info("-> " + page.getRedirectPage());

                        // wikitext is the full text with all wiki notation
                        //logger.info("WikiText: " + page.getWikiText());

                        // text is the somehow cleaned text without what comes in square brackets []
                        // does however still contain everything in camels {{}}, bullets *, lists #
                        // and headings ====
                        //logger.info("Text: " + page.getText());

                        //logger.info("" + page.);


                    }

                    // counter
                    if ( (WikiParserTest.counter % 100000) == 0){
                        logger.info("Page counter passed: " + WikiParserTest.counter);
                    }

                }
            });

            wxsp.parse();

            // final page count
            logger.info("Final page counter: " + WikiParserTest.counter);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}