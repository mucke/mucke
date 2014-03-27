package at.tuwien.mucke.clustering;

import at.tuwien.mucke.search.Result;

import java.util.List;

/**
 * Merges results based on some Criteria
 *
 * @author Ralf Bierig
 */
public interface ResultMerger {

    public List<Result> merge(List<Result> results);
}
