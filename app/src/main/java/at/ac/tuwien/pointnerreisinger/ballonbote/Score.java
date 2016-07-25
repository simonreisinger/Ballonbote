package at.ac.tuwien.pointnerreisinger.ballonbote;

/**
 * Score item
 * @author Michael Pointner
 */
public class Score {

    private long id;
    private String username;
    private int score;

    /**
     * Initializes the score
     * @param username Username
     * @param score Score
     * @author Michael Pointner
     */
    public Score(String username, int score) {
        this.username = username;
        this.score = score;
    }

    /**
     * Returns the id
     * @return Id
     * @author Michael Pointner
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id
     * @param id Id
     * @author Michael Pointner
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the username
     * @return Username
     * @author Michael Pointner
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     * @param username Username
     * @author Michael Pointner
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the score
     * @return Score
     * @author Michael Pointner
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score
     * @param score Score
     * @author Michael Pointner
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns a String represantation of the object
     * @return String represantation
     * @author Michael Pointner
     */
    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", score=" + score +
                '}';
    }
}
