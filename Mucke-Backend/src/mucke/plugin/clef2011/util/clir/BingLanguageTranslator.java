package mucke.plugin.clef2011.util.clir;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;


/** 
 * Connects to the Bing Azure Translation Service via the microsoft-translator-java-api available from Google code. 
 * At the time when this class was written, it was possible to register for a free account via a Live ID valid for 
 * 2 million characters a month. You will need to substitude the Windows (Bing) Live account ID and the client 
 * secret which is provided when closing the contract online. A description how to do that is provided here:
 * 
 * http://anything-digital.com/josetta/learn-more/subscribing-to-the-bing-translator-service.html
 */
public class BingLanguageTranslator {
    
    /** Please substitude with your Windows Live ID that is connected to the Bing Translate account*/
    public final static String BING_LIVE_ID = "RalfBierig";
    
    /** The Bing Azure Client Secret provided when closing the contract with Bing */
    public final static String BING_AZURE_CLIENT_SECRET = "IWON7CSSsV4wCbxrw7b1pS721hmnnfpSMvSkmuQnYy0";

    /**
     * Translates the given text from the given fromLanguage to the given toLanguage
     * 
     * @param String text The text to be translated
     * @param String from
     * @param String to
     * @return String translated text
     */
    public static String translate(String text, Language from, Language to) {

	// capture empty texts
	if (text.trim().length() == 0){
	    return text;
	}
	
	// Set your Windows Azure Marketplace client info - See http://msdn.microsoft.com/en-us/library/hh454950.aspx
	Translate.setClientId(BING_LIVE_ID);
	Translate.setClientSecret(BING_AZURE_CLIENT_SECRET);

	String translatedText = "";
	
	try {
	    
	    if (from == null){
		translatedText = Translate.execute(text, to);    
	    } else {
		translatedText = Translate.execute(text, from, to);    
	    }
	    
	} catch (Exception e){
	    System.out.println("Exception while translating via Bing: " + e.getMessage() + ". Nothing translated!");
	    e.printStackTrace();
	}
	
	// return translatedText;
	return translatedText;
    }
    
    
    /** For testing only */
    public static void main(String[] args) {
	
	BingLanguageTranslator.translate("Bonjour le monde", Language.FRENCH, Language.ENGLISH);
    }
    
}
