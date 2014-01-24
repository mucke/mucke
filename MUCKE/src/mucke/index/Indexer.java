package mucke.index;

import java.util.List;

/**
 * Indexes collection content.
 * 
 * @author Ralf Bierig
 */
public interface Indexer {

    /**
     * Indexes all content files stored in the given directory
     * 
     * @param contentDirectory File path to content directory
     * @param indexDirectory File path to index directory to be produced
     * @param fieldGenerators A list of field generators that create die fields of the index
     */
    public void index(String contentDirectory, String indexDirectory, List<IndexFieldGenerator> fieldGenerators);

}