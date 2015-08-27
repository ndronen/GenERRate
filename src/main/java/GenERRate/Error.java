package GenERRate;
/**
 * Class Error
 *
 * @author Jennifer Foster
 */
public class Error {

    /**
     * The sentence into which an error will be inserted.
     */
    protected Sentence sentence;


    /**
     * Information about the particular error type
     */
    protected String errorInfo;


    /**
     * The probability of this particular error type. This is obtained from the frequency counts in the errorAnalysisFile (see ErrorCreation file).
     */
    protected double probability;

    public Error(Sentence sentence) {
        this.sentence = sentence;
        errorInfo = "";
    }

    /**
     * Get the value of probability
     * The probability of this particular error type. This is obtained from the
     * frequency counts in the errorAnalysisFile (see ErrorCreation file).
     *
     * @return the value of probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Set the value of probability
     * The probability of this particular error type. This is obtained from the
     * frequency counts in the errorAnalysisFile (see ErrorCreation file).
     *
     * @param probability the new value of probability
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * Set the value of the input sentence
     *
     * @param sentence The input sentence.
     */
    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public Sentence insertError() throws CannotCreateErrorException {
        if (sentence.size() < 1) {
            throw new CannotCreateErrorException("The input sentence is empty.");
        }
        return sentence;
    }

    /**
     * Return information about this error type
     */
    public String toString() {
        return errorInfo;
    }
}
