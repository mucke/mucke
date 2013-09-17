package mucke.query;

import java.util.List;





import mucke.concept.Visitor;
import mucke.concept.model.Concept;
import mucke.concept.model.Field;
import mucke.concept.FieldProcessor;

public class QueryProcessor {
	
	private Visitor visitor;
	
	public QueryProcessor() {
		visitor = new FieldProcessor();
	}
	
	public Query prepare (String query, PrepareQueryStrategy prepareStrategy) {
		return prepareStrategy.prepare(query);
	}
	
	public List<Query> prepareCollection(String listOfQueries, PrepareCollectionStrategy prepareStrategy) {
		return prepareStrategy.prepare(listOfQueries);
	}
	
	public List<Concept> process(Query query) {
		List<Field> fields = query.getFields();
        if (fields != null) {
            for (Field field : fields) {
                field.accept(visitor);
            }
        }
		return null;
	}
}
