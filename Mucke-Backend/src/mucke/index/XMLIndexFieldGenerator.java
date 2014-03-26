package mucke.index;

import java.io.File;

import mucke.util.xml.XMLTools;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.w3c.dom.NodeList;

/** 
 * Extracts text from XML files and turns them into Lucene/Lire index fields.
 * 
 * @author Ralf Bierig
 */
public class XMLIndexFieldGenerator extends IndexFieldGenerator {

    static Logger logger = Logger.getLogger(XMLIndexFieldGenerator.class);
    
    /** Constructor expects an XPath as a signature. 'null' is not permitted. */
    public XMLIndexFieldGenerator(String fieldname, String signature) {
	this.fieldname = fieldname;
	if (signature.equals("null")){
	    logger.error("Signature must not be null. Please change configuration.");
	    return;
	} else {
	    this.signature = signature;    
	}
    }

    @Override
    public Field generate(File file) {

	// extract nodes
	String result = "";
	NodeList nodes = XMLTools.getNodes(file, this.getSignature());
	if (nodes != null && nodes.getLength() > 0) {
	    result = nodes.item(0).getTextContent().trim();
	}
	
	// create field
	Field field = new StringField(this.getFieldname(), result, Field.Store.YES);
	
	return field;
    }

}