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
     * @param String File path to content directory
     * @param String File path to index directory to be produced
     */
    public void index(String contentDirectory, String indexDirectory, List<IndexFieldGenerator> fieldGenerators);

}