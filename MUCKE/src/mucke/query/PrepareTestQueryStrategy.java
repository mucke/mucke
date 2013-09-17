package mucke.query;

public class PrepareTestQueryStrategy implements PrepareQueryStrategy{

	@Override
	public Query prepare(String query) {
		System.out.println("Preparing test query");
		return new Query();
	}

}
