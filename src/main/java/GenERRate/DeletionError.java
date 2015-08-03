package GenERRate;

import java.util.Random;

/**
 * Class DeletionError
 *
 * @author Jennifer Foster
 */
public class DeletionError extends Error {

    public DeletionError(Sentence inputS) {
        super(inputS);
        errorInfo = "errortype=\"DeletionError\"";
    }

    /* For testing purposes
     */
    public static void main(String[] args) {
        try {
            System.out.println("Testing the version without tags");
            Sentence testSentence = new Sentence("This is a test", false);
            DeletionError deletionError = new DeletionError(testSentence);
            System.out.println(deletionError.insertError());

            System.out.println();
            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            deletionError.setInputSentence(testSentence);
            System.out.println(deletionError.insertError());

            System.out.println();
            System.out.println("Testing an invalid version");
            testSentence = new Sentence("Test", false);
            deletionError.setInputSentence(testSentence);
            System.out.println(deletionError.insertError());

            System.out.println();
            System.out.println("Testing an invalid version");
            testSentence = new Sentence("Test NN", true);
            deletionError.setInputSentence(testSentence);
            System.out.println(deletionError.insertError());

            System.out.println();
            System.out.println("Testing an invalid version");
            testSentence = new Sentence("", true);
            deletionError.setInputSentence(testSentence);
            System.out.println(deletionError.insertError());

            System.out.println();
            System.out.println("Testing an invalid version");
            testSentence = new Sentence("", false);
            deletionError.setInputSentence(testSentence);
            System.out.println(deletionError.insertError());
        } catch (CannotCreateErrorException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Randomly select a word from the input sentence and delete it.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        if (inputSentence == null || inputSentence.size() < 2) {
            throw new CannotCreateErrorException("Cannot introduce a Deletion error. The input sentence has too few words.");
        } else {
            Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());

            Random random = new Random(newSentence.toString().hashCode());
            int randomNo = random.nextInt(newSentence.size());

            Word wordToGo = (Word) newSentence.getWord(randomNo);

            newSentence.removeWord(randomNo);

            newSentence.setErrorDescription(errorInfo + " details=\"" + wordToGo.getToken() + " at " + (randomNo + 1) + "\"");

            return newSentence;
        }
    }
}
