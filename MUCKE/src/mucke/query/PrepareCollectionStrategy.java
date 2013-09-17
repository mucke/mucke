package mucke.query;

import java.util.List;

public interface PrepareCollectionStrategy {
	public abstract List<Query> prepare(String listOfQueries);
}
