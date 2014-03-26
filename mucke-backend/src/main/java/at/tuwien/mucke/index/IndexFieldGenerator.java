package at.tuwien.mucke.index;

import org.apache.lucene.document.Field;

import java.io.File;

/**
 * Defines how content (e.g. from Files) it transformed into Lucene / Lire index fields.
 *
 * @author Ralf Bierig
 */

public abstract class IndexFieldGenerator {

    protected String fieldname = "UNDEFINED";
    protected String signature = "UNDEFINED";
    //protected IndexFieldTranslator translator = null; 

    /**
     * Generates a Lucene/Lire index field for the given file. Field name and field signature are provided via the constructor.
     *
     * @param file The file to generate an index field from
     */
    public abstract Field generate(File file);

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