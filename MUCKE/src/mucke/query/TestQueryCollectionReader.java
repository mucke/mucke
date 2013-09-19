package mucke.query;

import java.util.List;

/** Test implementation of a a PrepareCollectionStrategy */
public class TestQueryCollectionReader implements QueryCollectionReader {

    @Override
    public List<Query> prepare(String listOfQueries) {
	System.out.println("Preparing test collection");
	return null;
    }

}
