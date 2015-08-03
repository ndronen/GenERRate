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
    protected Sentence inputSentence;


    /**
     * Information about the particular error type
     */
    protected String errorInfo;


    /**
     * The probability of this particular error type. This is obtained from the frequency counts in the errorAnalysisFile (see ErrorCreation file).
     */
    protected double probability;

    public Error(Sentence anInputSentence) {
        inputSentence = anInputSentence;
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
     * @param inputSentence The input sentence.
     */
    public void setInputSentence(Sentence inputSentence) {
        this.inputSentence = inputSentence;
    }

    public Sentence insertError() throws CannotCreateErrorException {
        if (inputSentence.size() < 1) {
            throw new CannotCreateErrorException("The input sentence is empty.");
        }
        return inputSentence;
    }

    /**
     * Return information about this error type
     */
    public String toString() {
        return errorInfo;
    }
}
