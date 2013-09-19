package mucke.query;

/** Test implementation of a a PrepareQueryStrategy */
public class TestQueryReader implements QueryReader {

    @Override
    public Query prepare(String query) {
	System.out.println("Preparing test query");
	return new Query();
    }

}