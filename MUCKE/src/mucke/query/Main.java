package mucke.query;

public class Main {

	
	public static void main(String[] args) {
		String testQuery = "hello world";
		QueryProcessor qProcessor = new QueryProcessor();
		
		
		PrepareQueryStrategy testStrategy = new PrepareTestQueryStrategy();
		Query query = qProcessor.prepare(testQuery, testStrategy);
		
		PrepareCollectionStrategy testCollStrategy = new PrepareTestCollectionStrategy();
		qProcessor.prepareCollection(testQuery, testCollStrategy);
	
		qProcessor.process(query);
	}
}
