package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class DeletionPOSError
 *
 * @author Jennifer Foster
 */
public class DeletionPOSError extends DeletionError {


    /**
     * The POS of the word to be deleted from the sentence.
     */
    protected String POS;


    public DeletionPOSError(Sentence inputS, String thePOS) {
        super(inputS);
        POS = thePOS;
    }

    //For testing purposes
    public static void main(String[] args) {
        try {
            System.out.println("Testing the version with tags");
            Sentence testSentence = new Sentence("This DT man NN walks VBZ and CONJ talks VBZ", true);
            DeletionPOSError deletionPOSError = new DeletionPOSError(testSentence, "VBZ");
            System.out.println(deletionPOSError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            deletionPOSError = new DeletionPOSError(testSentence, "VBZ");
            System.out.println(deletionPOSError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT test NN", true);
            deletionPOSError = new DeletionPOSError(testSentence, "VBZ");
            System.out.println(deletionPOSError.insertError());

            System.out.println("Testing the version without tags");
            testSentence = new Sentence("This is a test", false);
            deletionPOSError = new DeletionPOSError(testSentence, "VBZ");
            System.out.println(deletionPOSError.insertError());
        } catch (CannotCreateErrorException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Get the value of POS
     * The POS of the word to be deleted from the sentence.
     *
     * @return the value of POS
     */
    public String getPOS() {
        return POS;
    }

    /**
     * Set the value of POS
     * The POS of the word to be deleted from the sentence.
     *
     * @param POS the new value of POS
     */
    public void setPOS(String POS) {
        this.POS = POS;
    }

    /**
     * Deletes a word of a particular POS from the sentence.
     * A CannotCreateErrorException is thrown if the sentence does not contain a word
     * of this POS.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the input sentence isn't tagged, this method won't work
        if (!sentence.areTagsIncluded()) {
            throw new CannotCreateErrorException("Cannot introduce a Deletion Error. The input sentence is not tagged");
        }

        //create the new sentence
        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());

        //find the words in the sentence tagged as POS
        List<Integer> wordsForDeletion = new ArrayList<Integer>();
        Word word;
        for (int i = 0; i < newSentence.size(); i++) {
            word = newSentence.getWord(i);
            if (word.getTag().equals(POS)) {
                wordsForDeletion.add(i);
            }
        }
        //if there aren't any words tagged as POS in sentence, then we can't do anything
        if (wordsForDeletion.size() == 0) {
            throw new CannotCreateErrorException("Cannot introduce a Deletion Error. There aren't any words with POS tag " + POS + " in the sentence.");
        }

        //randomly pick one of these and delete it from the sentence
        Random rand = new Random(newSentence.hashCode());
        int randNo = rand.nextInt(wordsForDeletion.size());
        int randomPosition = wordsForDeletion.get(randNo);
        Word wordToGo = newSentence.getWord(randomPosition);
        newSentence.removeWord(randomPosition);
        errorInfo = "errortype=\"Deletion" + wordToGo.getToken() + "NULL" + "Error\"";
        newSentence.setErrorDescription(errorInfo + " details=\"" + wordToGo.getToken() + " at " + (randomPosition + 1) + "\"");

        return newSentence;
    }


}
