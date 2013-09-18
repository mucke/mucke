/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract document consisting of a list of fields.
 * @author Alexandra
 */
public class Document {

    private List<Field> fields;

    /** Standard Constructor */
    public Document() {
        fields = new ArrayList<Field>();
    }
    
    /** Constructor
     * @param List<Field> fields
     */
    public Document(List<Field> fields) {
    	this.fields = fields;
    }
    
    /** 
     * Return fields of the document
     * @return fields */
    public List<Field> getFields() {
        return fields;
    }
}
