package at.tuwien.mucke.util;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.wikipedia.WikipediaTokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Some tokenizing
 **/
public class WikipediaTokenizerTest {

    static Logger logger = Logger.getLogger(WikipediaTokenizerTest.class);

    protected static final String LINK_PHRASES = "click [[link here again]] click [http://lucene.apache.org here again] [[Category:a b c d]]";


    public WikipediaTokenizer testSimple() throws Exception {
        String text = "This is a [[Category:foo]]";
        return new WikipediaTokenizer(new StringReader(text));
    }

    public WikipediaTokenizer testHandwritten() throws Exception {
        // make sure all tokens are in only one type
        /*String test = "[[link]] This is a [[Category:foo]] Category  This is a linked [[:Category:bar none withstanding]] "
                + "Category This is (parens) This is a [[link]]  This is an external URL [http://lucene.apache.org] "
                + "Here is ''italics'' and ''more italics'', '''bold''' and '''''five quotes''''' "
                + " This is a [[link|display info]]  This is a period.  Here is $3.25 and here is 3.50.  Here's Johnny.  "
                + "==heading== ===sub head=== followed by some text  [[Category:blah| ]] "
                + "''[[Category:ital_cat]]''  here is some that is ''italics [[Category:foo]] but is never closed."
                + "'''same [[Category:foo]] goes for this '''''and2 [[Category:foo]] and this"
                + " [http://foo.boo.com/test/test/ Test Test] [http://foo.boo.com/test/test/test.html Test Test]"
                + " [http://foo.boo.com/test/test/test.html?g=b&c=d Test Test] <ref>Citation</ref> <sup>martian</sup> <span class=\"glue\">code</span>";
*/
        String wikiTest = "{{other uses}}\n" +
                "{{Anarchism sidebar |Theory}}\n" +
                "{{Forms of government}}\n" +
                "'''Anarchy''' has more than one definition. Some use the term \"Anarchy\" to refer to a society without a publicly enforced government.<ref>{{cite web|url=http://www.amazon.com/dp/1551642484 |title=Decentralism: Where It Came From-Where Is It Going? |publisher=Amazon.com |date= |accessdate=2012-01-30}}</ref><ref>\"Anarchy.\" Oxford English Dictionary. Oxford University Press. 2004. The first quoted usage is 1667</ref>  When used in this sense, anarchy may<ref>\"Anarchy.\" Oxford English Dictionary. Oxford University Press. 2004. The first quoted usage is 1552</ref> or may not<ref name=\"Anarchy 2004\">\"Anarchy.\" Oxford English Dictionary. [[Oxford University Press]]. 2004. The first quoted usage is 1850.</ref> be intended to imply political disorder or [[Civil disorder|lawlessness]] within a society. Many anarchists complain with [[Anselme Bellegarrigue]] that \"[v]ulgar error has taken 'anarchy' to be synonymous with 'civil war.{{' \"}}<ref>{{Cite web | title = Lexical Investigations: Anarchy | url = http://hotword.dictionary.com/anarchy/ | accessdate = 11 November 2013 | work = The Hot Word | publisher = [[Reference.com|dictionary.com]] }}</ref>\n" +
                "\n" +
                "== Etymology ==\n" +
                "The word ''anarchy'' comes from the [[ancient Greek]] ἀναρχία, ''anarchia'', from [[:wikt:ἀν-|ἀν]] ''an'', \"not, without\" + [[:wikt:ἀρχός|ἀρχός]] ''arkhos'', \"ruler\", meaning \"absence of a ruler\", \"without rulers\").<ref>\"anarchy, n.\". OED Online. December 2012. Oxford University Press. http://www.oed.com/view/Entry/7118?redirectedFrom=anarchy& (accessed January 17, 2013).</ref>";

        return new WikipediaTokenizer(new StringReader(wikiTest));
    }

    public WikipediaTokenizer testLinks() throws Exception {
        String test = "[http://lucene.apache.org/java/docs/index.html#news here] [http://lucene.apache.org/java/docs/index.html?b=c here] [https://lucene.apache.org/java/docs/index.html?b=c here]";
        return new WikipediaTokenizer(new StringReader(test));
    }

    public static void main(String[] args){
        WikipediaTokenizerTest wtt = new WikipediaTokenizerTest();

        try {

            /*
            // first test
            WikipediaTokenizer wikiTokenizer = wtt.testHandwritten();
            wikiTokenizer.reset();

            int i = 0;
            while (wikiTokenizer.incrementToken()) {
                logger.info("seen something");
                i++;

            }

            wikiTokenizer.end();
            wikiTokenizer.close();

            // output how many tokens I have seen
            logger.info("I have seen " + i + " tokens!");
            */


            WikipediaTokenizer wikiTokenizer = wtt.testHandwritten();
            wikiTokenizer.reset();

            CharTermAttribute charTermAttrib = wikiTokenizer.getAttribute(CharTermAttribute.class);
            TypeAttribute typeAtt = wikiTokenizer.getAttribute(TypeAttribute.class);
            OffsetAttribute offset = wikiTokenizer.getAttribute(OffsetAttribute.class);

            List<String> tokens = new ArrayList<String>();
            wikiTokenizer.reset();
            while (wikiTokenizer.incrementToken()) {
                tokens.add(charTermAttrib.toString());
                System.out.println(typeAtt.type() + " (" + offset.startOffset() + "," + offset.endOffset() + "): " + charTermAttrib.toString());
            }
            wikiTokenizer.end();
            wikiTokenizer.close();



        } catch(Exception e){
            logger.error("Exception while tokenizing Wiki Text: " + e.getMessage());
            e.printStackTrace();
        }


    }

}