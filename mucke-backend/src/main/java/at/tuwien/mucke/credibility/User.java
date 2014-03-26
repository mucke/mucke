package at.tuwien.mucke.credibility;

import org.apache.log4j.Logger;

/**
 * A user must provide the minimal information of a unique id and a credibility score. Optional
 * user properties can be added to build more complex user profiles.
 *
 * @author Ralf Bierig
 */
public class User {

    static Logger logger = Logger.getLogger(User.class);

    private String id = "";
    private double credibilityScore = 0.0;    // per default, a user is not credible

    /**
     * Constructor
     */
    public User(String id, double crediblityScore) {
        this.id = id;
        if (crediblityScore < 0.0 || crediblityScore > 1.0) {
            logger.warn("User created with illegal crediblity score. Value must be between 0.0 and 1.0. Value set to 0.0!");
        } else {
            this.credibilityScore = crediblityScore;
        }
    }


    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }


    /**
     * @return the credibilityScore
     */
    public final double getCredibilityScore() {
        return credibilityScore;
    }

}