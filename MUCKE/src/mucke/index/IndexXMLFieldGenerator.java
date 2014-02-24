package mucke.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import mucke.util.xml.XMLTools;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.w3c.dom.NodeList;

/** 
 * XML text IndexFieldGenerator that extracts text from XML files and turns them into Lucene fields
 * 
 * @author Ralf Bierig
 */
public class IndexXMLFieldGenerator extends IndexFieldGenerator {

    public IndexXMLFieldGenerator(String fieldname, String signature) {
	super(fieldname, signature);
    }

    @Override
    public Field generate(File file) {

	String result = "";
	
	// extract nodes
	logger.debug("Before getting nodes with sig: " + this.getSignature());
	NodeList nodes = XMLTools.getNodes(file, this.getSignature());
	logger.debug("After getting nodes: Total nodes # " + nodes.getLength());
	
	if (nodes != null && nodes.getLength() > 0) {
	    result = nodes.item(0).getTextContent().trim();
	}
	
	//
	// TODO: What if we have a null (contents, all content in one field) situation?
	// Currently throws an error. Should really be solved by general code in super-class that handles this case!
	//
	
	Field field = new StringField(this.getFieldname(), result, Field.Store.YES);
	
	return field;
    }

}