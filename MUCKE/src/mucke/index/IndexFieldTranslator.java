package mucke.index;

import mucke.plugin.clef2011.util.clir.BingLanguageTranslator;

import com.memetix.mst.language.Language;

/** Translates content of index fields from one language to another */
public class IndexFieldTranslator {

    private Language language = null;
    
    /** Constructor defines the translator for a certain target language */
    public IndexFieldTranslator(Language to){
	this.language = to;
    }
    
    /** Traverses the list of String texts and translates the first one it finds into the given language, then stops. If the resultText already exists,
     * returns this one and stops.  
     * @param language The destination language the text is translated to
     * @param texts An array of text in descending order of importance
     * @param resultText The result of the translation */
    public String translateFirst(String[] texts, String resultText) {
	// check if translation already exists
	if (resultText.length() > 0){
	    return resultText;
	}
	    
	for (String text : texts){
	    if (text.length() > 0){ // only translate if there is text
		return BingLanguageTranslator.translate(text, null, language);
	    }
	}
	    
	// no result is an empty String
	return ""; 
    }

    /**
     * @return the language
     */
    protected final Language getLanguage() {
        return language;
    }   
    
}