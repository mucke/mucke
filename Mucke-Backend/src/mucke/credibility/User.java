package mucke.credibility;

/**
 * A content-producing user
 * 
 * @author Ralf Bierig
 */
public class User {

    private String id = "";

    /** Constructor */
    public User(String id) {
	this.id = id;
    }

    /**
     * @return the id
     */
    public final String getId() {
	return id;
    }

}
