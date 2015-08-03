package GenERRate;

/**
 * Class Word
 *
 * @author Jennifer Foster
 */
public class Word {
    private String token;
    private String tag;

    public Word(String tokenVal, String tagVal) {
        token = tokenVal;
        tag = tagVal;
    }

    public Word(String tokenVal) {
        token = tokenVal;
    }

    /**
     * Get the value of token
     *
     * @return the value of token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the value of token
     *
     * @param newVar the new value of token
     */
    public void setToken(String newVar) {
        token = newVar;
    }

    /**
     * Get the value of tag
     *
     * @return the value of tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Set the value of tag
     *
     * @param newVar the new value of tag
     */
    public void setTag(String newVar) {
        tag = newVar;
    }

    /**
     * @return String a string representation of the word
     */
    public String toString() {
        if (tag == null) {
            return token;
        } else {
            return token + " " + tag;
        }
    }

}
