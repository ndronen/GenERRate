package GenERRate;

/**
 * Class Word
 *
 * @author Jennifer Foster
 */
public class Word {
    private final String token;
    private String tag;

    public Word(String token, String tag, String original) {
        this.token = preserveCase(token, original);
        this.tag = tag;
    }

    public Word(String token, String tag) {
        this.token = token;
        this.tag = tag;
    }

    public Word(String tokenVal) {
        token = tokenVal;
    }

    private String preserveCase(String token, String original) {
        return original.substring(0, 1) + token.substring(1);
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
     * Get the value of tag
     *
     * @return the value of tag
     */
    public String getTag() {
        return tag;
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
