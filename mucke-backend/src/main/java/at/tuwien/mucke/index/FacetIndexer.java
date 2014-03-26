package at.tuwien.mucke.index;

import java.util.List;

/**
 * Implement this interface if you want to write a new indexer for a particular facet type.
 *
 * @author Ralf Bierig
 */
public interface FacetIndexer {


    /**
     * Indexes all content files of a specific facet stored in the given directory
     *
     * @param contentDirectory File path to content directory
     * @param indexDirectory   File path to index directory to be produced
     * @param fieldGenerators  A list of field generators that create die fields of the index
     */
    public void index(String contentDirectory, String indexDirectory, List<IndexFieldGenerator> fieldGenerators);

}