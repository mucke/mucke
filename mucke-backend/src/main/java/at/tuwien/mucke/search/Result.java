package at.tuwien.mucke.search;


/**
 * Stores the most essential information about search results.
 *
 * @author Ralf Bierig
 */
public class Result implements Comparable<Result>{

    private String id;
    private String title;
    private float score;
    private String userId;


    public Result(String id, String title, float score, String userId) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.userId = userId;
    }

    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the score
     */
    public final float getScore() {
        return score;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Re-set the score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /** Sort Result objects always in descending order based on their score
     * @param compareObject The Result object to compare this Result to
     * @return  -1 if object < compared object, +1 if object > compared object and 0 if they are the same.
     */
    public int compareTo(Result compareObject) {
        if (getScore() < compareObject.getScore()){
            return 1;
        } else if (getScore() == compareObject.getScore()) {
            return 0;
        } else {
            return -1;
        }
    }

}