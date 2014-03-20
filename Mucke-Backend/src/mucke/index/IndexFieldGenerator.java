package mucke.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import mucke.config.ConfigurationManager;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

/** 
 * Creates Lucene index fields from content files. Signatures define which part of the files content will be used for the field. 
 * The generator also has an optional translator that can be used to translate its content into another language.
 * 
 * @author Ralf Bierig
 */
public class IndexFieldGenerator {

    static Logger logger = Logger.getLogger(ConfigurationManager.class);
    private String fieldname = "UNDEFINED";
    private String signature = "UNDEFINED";
    //private IndexFieldTranslator translator = null; 
    
    
    /** Constructor. Defines the field this IndexFieldGenerator produces. */
    public IndexFieldGenerator(String fieldname,  String signature){
	this.fieldname = fieldname;
	if (signature.equals("null")){
	    this.signature = null;
	} else {
	    this.signature = signature;    
	}
	//this.translator = translator;
	//logger.debug("Building IndexFieldGenerator for field '" + this.fieldname + "' with signature '" + this.signature + "'");
	//if (this.translator != null){
	//    logger.debug("Index field has a translator for " + translator.getLanguage().name());   
	//}

    }
        
    /** Generates a Lucene index field with the given name based on the given file and the given signature. 
     * Translates the field content if a translator is given
     * @param file The file to generate an index field from */
    public Field generate(File file){
	
	// output
	Field field = null;
	
	FileInputStream fis;
	try {
	    fis = new FileInputStream(file);
	} catch (FileNotFoundException e) {
	    // at least on windows, some temporary files raise this exception with an "access denied" message
	    // checking if the file can be read doesn't help
	    return null;
	}
	
	try {
	    
	    if (signature == null){
		// simple case where all content is added to the field 
		//logger.debug("Generating a Lucene index default (TextField containing all content) field: " + fieldname + " for file: " + file.getName());
		field = new TextField(fieldname, new BufferedReader(new InputStreamReader(fis, "UTF-8")));    
	    } else {
		// TODO: Realistic case where content is added based on a regular expression
		logger.info("TODO: parsing content of " + file.getName() + " and adding it to field: " + field);
	    }
	} catch (Exception e){
	    logger.error("Error while generating field '" + fieldname + "' in file '" + file.getName() + "'");
	}
	return field;
	
    }

    /**
     * @return the fieldname
     */
    public final String getFieldname() {
        return fieldname;
    }

    /**
     * @return the signature
     */
    public final String getSignature() {
        return signature;
    }
    
}
