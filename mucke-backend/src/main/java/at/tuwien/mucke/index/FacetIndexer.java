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
     */
    public void index();

    /**
     * Verifies the common parameters for the index which can be inquired by calling <see>explainParams</see>.
     *
     * @return true, if all parameters are correctly defined, false otherwise
     */
    public boolean checkParams();


    /**
     * Provides a detailed description of the parameters and their defaults.
     */
    public String explainParams();
}