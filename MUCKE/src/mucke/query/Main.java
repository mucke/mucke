package mucke.query;

/**
 * Tests the query package
 * 
 * @author Alexandra-Mihaela Siriteanu
 */
public class Main {

    /** Tests the query package */
    public static void main(String[] args) {
	
	String queryText = "hello world";
	QueryManager qProcessor = new QueryManager();

	QueryReader testStrategy = new TestQueryReader();
	Query query = qProcessor.prepare(queryText, testStrategy);

	QueryCollectionReader testCollStrategy = new TestQueryCollectionReader();
	qProcessor.prepareCollection(queryText, testCollStrategy);

	qProcessor.process(query);
    }
}