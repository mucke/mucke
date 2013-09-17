package mucke.query;

import java.util.List;

public class PrepareTestCollectionStrategy implements PrepareCollectionStrategy{
	
	@Override
	public List<Query> prepare(String listOfQueries) {
		System.out.println("Preparing test collection");
		return null;
	}

}
