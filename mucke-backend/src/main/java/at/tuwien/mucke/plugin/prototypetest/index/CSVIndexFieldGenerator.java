package at.tuwien.mucke.plugin.prototypetest.index;

import at.tuwien.mucke.index.IndexFieldGenerator;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.File;
import java.io.StringReader;
import java.util.List;


/**
 * Creates Lucene index fields from content files. The content file has fields separated by CR, TAB, COMMA or SEMICOLON and content parts
 * are referenced by their position in this array.
 * The generator also has an optional translator that can be used to translate its content into another language.
 *
 * @author Ralf Bierig
 */
public class CSVIndexFieldGenerator extends IndexFieldGenerator {

    static Logger logger = Logger.getLogger(CSVIndexFieldGenerator.class);

    // additional parameter
    protected String separator = "\t";


    /**
     * Constructor. Defines the field this IndexFieldGenerator produces.
     */
    public CSVIndexFieldGenerator(String fieldname, String signature) {
        this.fieldname = fieldname;
        if (signature.equals("null")) {
            logger.error("Signature must not be null but an index number. Please change configuration.");
            return;
        } else {
            this.signature = signature;
        }
    }

    @Override
    public Field generate(File file) {

        // transform signature in position number
        int position = Integer.parseInt(this.signature);

        // output
        Field field = null;

        try {

            // get content of file as CSV array
            List<String> contents = Util.getContents(file, true, this.separator);

            // catch faulty positions
            if ((position + 1) > contents.size()) {
                logger.warn("No position " + position + " in File: '" + file.getName() + "'. Skipped!");
                return null;
            }

            if (contents.get(position) != null) {
                // simple case where all content is added to the field

                // ORG
                //field = new TextField(fieldname, new StringReader(contents.get(position)));

                // NEW not tokenized and stored
                //field = new StringField(fieldname, contents.get(position), Field.Store.YES);

                // NEW tokenized and stored
                field = new TextField(fieldname, contents.get(position), Field.Store.YES);


            } else {
                logger.warn("Null value at position" + position + " in File '" + file.getName() + "'. Nothing done.");
            }

        } catch (Exception e) {
            logger.error("Error while generating field '" + fieldname + "' in file '" + file.getName() + "'");
        }
        return field;

    }

    /**
     * @return the separator
     */
    protected final String getSeparator() {
        return separator;
    }

}