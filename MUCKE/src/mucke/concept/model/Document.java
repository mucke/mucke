/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract document
 * @author Alexandra
 */
public class Document {

    private List<Field> fields;

    public Document() {
        fields = new ArrayList<Field>();
    }
    
    public Document(List<Field> fields) {
    	this.fields = fields;
    }
    
    public List<Field> getFields() {
        return fields;
    }
}
