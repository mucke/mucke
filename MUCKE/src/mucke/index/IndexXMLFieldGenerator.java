package mucke.index;

import java.io.File;

import mucke.util.xml.XMLTools;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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
	NodeList nodes = XMLTools.getNodes(file, this.getSignature());
	if (nodes != null && nodes.getLength() > 0) {
	    result = nodes.item(0).getTextContent().trim();
	}
	
	// create field
	Field field = new StringField(this.getFieldname(), result, Field.Store.YES);
	
	return field;
    }

}