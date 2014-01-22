package mucke.util.clir;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import net.olivo.lc4j.LanguageCategorization;


/** Identifies a long list of languages based on pre-existing language models stored at LC4J_LANGUAGE_MODELS_DIR. 
 * These models have been trained with ML using categorized Newsgroups. The libary lc4j library is based on the 
 * theoretical work by Cavnar and Trankle: N-Gram-Based Text Categorization (1994). 
 * 
 * @author Ralf Bierig
 */
public class LanguageIdentifier {

    public static final String LC4J_LANGUAGE_MODELS_DIR = "D:/IR/LC4J/lc4j/models";

    
    /** 
     * Identifies the probable languages for the given sample using N-Gram-based
     * language models located at LC4J_LANGUAGE_MODELS_DIR. 
     * 
     * @param String text sample
     * @return List of languages
     */
    public static List<String> identify(String sample) {

	System.out.println("Identifying language of sample: " + sample);

	LanguageCategorization lc = new LanguageCategorization();
	lc.setLanguageModelsDir(LC4J_LANGUAGE_MODELS_DIR);
	List<String> languages = lc.findLanguage(new BufferedReader(new StringReader(sample)));

	return languages;
    }

}
