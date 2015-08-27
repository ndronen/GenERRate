package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class MovePOSWhereError
 *
 * @author Jennifer Foster
 */
public class MovePOSWhereError extends MovePOSError {
    /**
     * The number of words to the left or right the word should be moved.
     */
    private int movePosition;
    /**
     * Whether to move the word left or right
     */
    private boolean isLeft;


    public MovePOSWhereError(Sentence inputS, String thePOS, boolean isLeftVal, int theMovePosition) {
        super(inputS, thePOS);
        isLeft = isLeftVal;
        movePosition = theMovePosition;
        errorInfo = "errortype=\"Move" + POS + isLeft + movePosition + "Error\"";
    }

    public static void main(String[] args) {
        try {
            System.out.println();
            System.out.println("Testing the version with tags");
            Sentence testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            MovePOSError moveError = new MovePOSWhereError(testSentence, "VBZ", false, 2);
            System.out.println(moveError.insertError());
            System.out.println();

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            moveError = new MovePOSWhereError(testSentence, "DT", true, 1);
            System.out.println(moveError.insertError());
            System.out.println();

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT great JJ test NN", true);
            moveError = new MovePOSWhereError(testSentence, "JJ", true, 2);
            System.out.println(moveError.insertError());
            System.out.println();

            //System.out.println("Testing the version without tags");
            //testSentence = new Sentence("This is a test", false);
            //moveError = new MovePOSWhereError(testSentence,"VBZ",true,1);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version without tags and with an empty sentence");
            //testSentence = new Sentence("", false);
            //moveError = new MovePOSWhereError(testSentence,"VBZ",false,1);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags and with an empty sentence");
            //testSentence = new Sentence("", true);
            //moveError = new MovePOSWhereError(testSentence,"VBZ",true,3);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags and with one word");
            //testSentence = new Sentence("test NN", true);
            //moveError = new MovePOSWhereError(testSentence,"VBZ",false,2);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version without tags and with one word");
            //testSentence = new Sentence("test", false);
            //moveError = new MovePOSWhereError(testSentence,"VBZ",true,1);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags but without tags of desired type");
            //testSentence = new Sentence("This DT is VBZ a DT great JJ test NN", true);
            //moveError = new MovePOSWhereError(testSentence,"VBN",true,2);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags");
            //testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            //moveError = new MovePOSWhereError(testSentence,"DT",true,3);
            //System.out.println(moveError.insertError());
            //System.out.println();

            //System.out.println("Testing the version with tags");
            //testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            //moveError = new MovePOSWhereError(testSentence,"NN",false,1);
            //System.out.println(moveError.insertError());
            //System.out.println();

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            moveError = new MovePOSWhereError(testSentence, "NN", false, 0);
            System.out.println(moveError.insertError());
            System.out.println();
        } catch (CannotCreateErrorException c) {
            System.err.println(c.getMessage());
        }
    }

    /**
     * Get the value of movePosition
     * The number of words to the left or right the word should be moved.
     *
     * @return the value of movePosition
     */
    private int getMovePosition() {
        return movePosition;
    }

    /**
     * Set the value of movePosition
     * The number of words to the left or right the word should be moved.
     *
     * @param newMovePosition the new value of movePosition
     */
    private void setMovePosition(int newMovePosition) {
        movePosition = newMovePosition;
    }

    /**
     * Get the value of isLeft
     * Whether to move the word left or right
     *
     * @return the value of isLeft
     */
    private boolean getIsLeft() {
        return isLeft;
    }

    /**
     * Set the value of isLeft
     * Whether to move the word left or right
     *
     * @param newIsLeft the new value of isLeft
     */
    private void setIsLeft(boolean newIsLeft) {
        isLeft = newIsLeft;
    }

    /**
     * Move a word in the sentence with a particular part-of-speech tag a certain
     * number of places to the left or right.
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
            throw new CannotCreateErrorException("The input sentence is not tagged. Cannot insert a Move Error of this type");
        }
        if (movePosition == 0) {
            throw new CannotCreateErrorException("Cannot create this kind of move error. The move position is zero.");
        }
        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());
        //find all words with the preferred part of speech
        List<Integer> movePOSList = new ArrayList<Integer>();
        for (int i = 0; i < newSentence.size(); i++) {
            if ((newSentence.getWord(i).getTag().equals(POS)) &&
                    ((isLeft && i - movePosition >= 0) || (!isLeft && i + movePosition < newSentence.size()))) {
                movePOSList.add(i);
            }
        }
        if (movePOSList.size() < 1) {
            throw new CannotCreateErrorException("The input sentence does not contain a word tagged as " + POS + " in the correct position . Cannot create a " + errorInfo + ".");
        }
        Random random = new Random(newSentence.toString().hashCode());
        //randomly choose the word to be move
        int moveWordPosition = movePOSList.get(random.nextInt(movePOSList.size()));
        Word moveWord = newSentence.getWord(moveWordPosition);
        //remove the word first
        newSentence.removeWord(moveWordPosition);
        //see whether the word is to be moved to the right or left
        int moveWordNewPosition;
        if (isLeft) {
            moveWordNewPosition = moveWordPosition - movePosition;
        } else {
            moveWordNewPosition = moveWordPosition + movePosition;
        }
        newSentence.insertWord(moveWord, moveWordNewPosition);
        newSentence.setErrorDescription(errorInfo + " details=\"" + moveWord.getToken() + " from " + (moveWordPosition + 1) + " to " + (moveWordNewPosition + 1) + "\"");
        return newSentence;
    }


}
