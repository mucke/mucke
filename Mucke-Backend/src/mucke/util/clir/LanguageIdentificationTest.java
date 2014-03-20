package mucke.util.clir;

import java.util.List;

import mucke.util.Logger;

import com.memetix.mst.language.Language;

public class LanguageIdentificationTest {

    // samples
    public static final String sampleTextEN = "Maybe that was on purpose, seeing as you seem to prefer not to write so much about work.";
    public static final String sampleTextDE = "Wenn Sie Student waeren welche Frage wuerden Sie sich selbst stellen und wie wuerden Sie sie beantworten?";
    public static final String sampleTextFR = "Bonjour le monde";
    
    
    public static void displayLanguages(List<String> languages) {
	int rank = 1;
	for (String language : languages) {
	    System.out.println("Probably language #" + (rank++) + ") " + language);
	}
    }

    /** Tests language identification based on test Strings */
    public static void testLanguageIdentification() {

	LanguageIdentificationTest.displayLanguages(LanguageIdentifier.identify(sampleTextFR));
	LanguageIdentificationTest.displayLanguages(LanguageIdentifier.identify(sampleTextEN));
	LanguageIdentificationTest.displayLanguages(LanguageIdentifier.identify(sampleTextDE));

    }

    /** Tests language translation based on test Strings */
    public static void testLanguageTranslation() {
	
	Logger logger = new Logger(new String[]{"type", "text"});
	
	String resultSample = BingLanguageTranslator.translate(sampleTextFR, Language.AUTO_DETECT, Language.ENGLISH);
	System.out.println("Translating text: " + sampleTextFR  + " ---> " + resultSample);
	
	String resultSampleEN = BingLanguageTranslator.translate(sampleTextEN, Language.AUTO_DETECT, Language.ENGLISH);
	System.out.println("Translating text: " + sampleTextEN  + " ---> " + resultSampleEN);
		
	String resultSampleDE = BingLanguageTranslator.translate(sampleTextDE, Language.AUTO_DETECT, Language.ENGLISH);
	System.out.println("Translating text: " + sampleTextDE  + " ---> " + resultSampleDE);
	
    }

    /** For testing only */
    public static void main(String[] args) {

	// test language identification
	testLanguageIdentification();

	// test language translation
	testLanguageTranslation();

    }

}
