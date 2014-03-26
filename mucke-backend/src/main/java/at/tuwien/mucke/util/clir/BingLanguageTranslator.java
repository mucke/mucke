package at.tuwien.mucke.util.clir;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Connects to the Bing Azure Translation Service via the microsoft-translator-java-api available from Google code. At the time when this
 * class was written, it was possible to register for a free account via a Live ID valid for 2 million characters a month. You will need to
 * substitude the Windows (Bing) Live account ID and the client secret which is provided when closing the contract online. A description how
 * to do that is provided here:
 * <p/>
 * http://anything-digital.com/josetta/learn-more/subscribing-to-the-bing-translator-service.html
 */
public class BingLanguageTranslator {

    /**
     * Please substitude with your Windows Live ID that is connected to the Bing Translate account
     */
    public final static String BING_LIVE_ID = "RalfBierig";

    /**
     * The Bing Azure Client Secret provided when closing the contract with Bing
     */
    public final static String BING_AZURE_CLIENT_SECRET = "IWON7CSSsV4wCbxrw7b1pS721hmnnfpSMvSkmuQnYy0";

    /**
     * Translates the given text from the given fromLanguage to the given toLanguage
     *
     * @param text The text to be translated
     * @param from The source language
     * @param to   The destination language
     * @return The translated text
     */
    public static String translate(String text, Language from, Language to) {

        // capture empty texts
        if (text.trim().length() == 0) {
            return text;
        }

        // Set your Windows Azure Marketplace client info - See http://msdn.microsoft.com/en-us/library/hh454950.aspx
        Translate.setClientId(BING_LIVE_ID);
        Translate.setClientSecret(BING_AZURE_CLIENT_SECRET);

        String translatedText = "";

        try {

            if (from == null) {
                translatedText = Translate.execute(text, to);
            } else {
                translatedText = Translate.execute(text, from, to);
            }

        } catch (Exception e) {
            System.out.println("Exception while translating via Bing: " + e.getMessage() + ". Nothing translated!");
            e.printStackTrace();
        }

        // return translatedText;
        return translatedText;
    }

    /**
     * Translates text conditionally.
     *
     * @param texts      An array of Strings with source texts. The array is prioritized in order of translation quality. This means that the
     *                   first item in the String array is the most fit text for the translation. Only and only if this text is an empty string,
     *                   the method tries to translate the second entry of the array. If this one is also empty, it continues with the third entry,
     *                   and so on until the end of the array is reached or a translation has been successful before. If all texts were empty
     *                   Strings, no translation is performed and the method returns and empty String.
     * @param to         The language to which should be translated (e.g. Language.ENGLISH).
     * @param resultText Result of the translation. The translation will only be performed if the result text is empty. If the result text
     *                   is already available, no translation will be performed and the result text will be returned directly.
     * @return the translated String or an empty String if no translation was possible
     */
    public String translate(String[] texts, Language from, Language to, String resultText) {

        // check if translation already exists.
        if (resultText.length() > 0) {
            return resultText;
        }

        // translate the first text you find in the array
        for (String fromText : texts) {
            if (fromText.length() > 0) {
                return BingLanguageTranslator.translate(fromText, null, to);
            }
        }

        // no result is an empty String
        return "";
    }

    /**
     * For testing only
     */
    public static void main(String[] args) {

        BingLanguageTranslator.translate("Bonjour le monde", Language.FRENCH, Language.ENGLISH);
    }

}
