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
	QueryManager qManager = new QueryManager();

	QueryReader testStrategy = new TestQueryReader();
	Query query = qManager.prepare(queryText, testStrategy);

	QueryCollectionReader testCollStrategy = new TestQueryCollectionReader();
	qManager.prepareCollection(queryText, testCollStrategy);

	qManager.process(query);
    }
}