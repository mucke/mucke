package mucke.credibility;


/** A user producing content in a collection */
public class User {

    private String id = "";

    public User(String id){
	this.id = id;
    }
    
    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }
    
    
    
}
