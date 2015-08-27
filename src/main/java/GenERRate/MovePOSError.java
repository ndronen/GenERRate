package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class MovePOSError
 *
 * @author Jennifer Foster
 */
public class MovePOSError extends MoveError {
    /**
     * The preferred part-of-speech to be moved.
     */
    protected String POS;

    public MovePOSError(Sentence inputS, String thePOS) {
        super(inputS);
        POS = thePOS;
        errorInfo = "errortype=\"Move" + POS + "Error\"";
    }

    public static void main(String[] args) {
        try {
            System.out.println();
            System.out.println("Testing the version with tags");
            Sentence testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            MovePOSError moveError = new MovePOSError(testSentence, "VBZ");
            System.out.println(moveError.insertError());
            System.out.println();

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            moveError = new MovePOSError(testSentence, "DT");
            System.out.println(moveError.insertError());
            System.out.println();

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT great JJ test NN", true);
            moveError = new MovePOSError(testSentence, "JJ");
            System.out.println(moveError.insertError());
            System.out.println();

            //System.out.println("Testing the version without tags");
            //testSentence = new Sentence("This is a test", false);
            //moveError = new MovePOSError(testSentence,"VBZ");
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version without tags and with an empty sentence");
            //testSentence = new Sentence("", false);
            //moveError = new MovePOSError(testSentence,"VBZ");
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags and with an empty sentence");
            //testSentence = new Sentence("", true);
            //moveError = new MovePOSError(testSentence,"VBZ");
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags and with one word");
            //testSentence = new Sentence("test NN", true);
            //moveError = new MovePOSError(testSentence,"VBZ");
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version without tags and with one word");
            //testSentence = new Sentence("test", false);
            //moveError = new MovePOSError(testSentence,"VBZ");
            //System.out.println(moveError.insertError());
            //System.out.println();

            System.out.println("Testing the version with tags but tags of desired type");
            testSentence = new Sentence("This DT is VBZ a DT great JJ test NN", true);
            moveError = new MovePOSError(testSentence, "VBN");
            System.out.println(moveError.insertError());
            System.out.println();
        } catch (CannotCreateErrorException c) {
            System.err.println(c.getMessage());
        }

    }

    /**
     * Get the value of POS
     * The preferred part-of-speech to be moved.
     *
     * @return the value of POS
     */
    private String getPOS() {
        return POS;
    }

    /**
     * Set the value of POS
     * The preferred part-of-speech to be moved.
     *
     * @param newPOS the new value of POS
     */
    private void setPOS(String newPOS) {
        POS = newPOS;
    }

    /**
     * Moves a word with the POS tag as specified in the POS attribute to a random
     * position within the sentence.
     * If there isn't a word with this POS tag, then a CannotCreateErrorException is
     * thrown.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        if (sentence == null || sentence.size() < 2) {
            throw new CannotCreateErrorException("Either the input sentence is empty or it has only one word. Cannot insert a Move Error");
        }
        if (!sentence.areTagsIncluded()) {
            throw new CannotCreateErrorException("The input sentence is not tagged. Cannot create a " + errorInfo + ".");
        }
        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());
        //find all words with the preferred part of speech
        List<Integer> movePOSList = new ArrayList<Integer>();
        for (int i = 0; i < newSentence.size(); i++) {
            if (newSentence.getWord(i).getTag().equals(POS)) {
                movePOSList.add(i);
            }
        }
        if (movePOSList.size() < 1) {
            throw new CannotCreateErrorException("The input sentence does not contain a word tagged as " + POS + " . Cannot create a " + errorInfo + ".");
        }
        Random random = new Random(newSentence.toString().hashCode());
        //randomly choose the word to be moved
        int moveWordPosition = movePOSList.get(random.nextInt(movePOSList.size()));
        Word moveWord = newSentence.getWord(moveWordPosition);

        //randomly choose where the word is to be moved to
        int moveWordNewPosition = random.nextInt(newSentence.size());
        while (moveWordNewPosition == moveWordPosition) {
            moveWordNewPosition = random.nextInt(newSentence.size());
        }
        //remove the word first
        newSentence.removeWord(moveWordPosition);
        //add it again
        newSentence.insertWord(moveWord, moveWordNewPosition);
        newSentence.setErrorDescription(errorInfo + " details=\"" + moveWord.getToken() + " from " + (moveWordPosition + 1) + " to " + (moveWordNewPosition + 1) + "\"");
        return newSentence;
    }
}
