package GenERRate;

import java.util.Random;

/**
 * Class MoveError
 *
 * @author Jennifer Foster
 */
public class MoveError extends Error {

    public MoveError(Sentence inputS) {
        super(inputS);
        errorInfo = "errortype=\"MoveError\"";
    }

    //for testing purposes
    public static void main(String[] args) {
        try {
            System.out.println("Testing the version without tags");
            Sentence testSentence = new Sentence("This is a test", false);
            MoveError moveError = new MoveError(testSentence);
            System.out.println(moveError.insertError());

            System.out.println();
            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT is VBZ a DT test NN", true);
            moveError.setSentence(testSentence);
            System.out.println(moveError.insertError());

            //System.out.println();
            //System.out.println("Testing the version with tags but only one word in the sentence");
            //testSentence = new Sentence("This DT ", true);
            //moveError.setSentence(testSentence);
            //System.out.println(moveError.insertError());

            //System.out.println();
            //System.out.println("Testing the version without tags but only one word in the sentence");
            //testSentence = new Sentence("This", false);
            //moveError.setSentence(testSentence);
            //System.out.println(moveError.insertError());

            //System.out.println();
            //System.out.println("Testing with no words in the sentence");
            //testSentence = new Sentence("",true);
            //moveError.setSentence(testSentence);
            //System.out.println(moveError.insertError());

            System.out.println();
            System.out.println("Testing with no words in the sentence");
            testSentence = new Sentence("", false);
            moveError.setSentence(testSentence);
            System.out.println(moveError.insertError());
        } catch (CannotCreateErrorException c) {
            System.err.println(c.getMessage());
        }
    }

    /**
     * In the absence of any other information this will select a
     * word randomly from a sentence and move it to a random position within the
     * sentence.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        if (sentence == null || sentence.size() < 2) {
            throw new CannotCreateErrorException("Either the input sentence is empty or it has only one word. Cannot insert a Move Error");
        }
        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());
        Random random = new Random(newSentence.toString().hashCode());
        //randomly choose the  word to be moved
        int moveWordPosition = random.nextInt(newSentence.size());
        Word moveWord = newSentence.getWord(moveWordPosition);
        //randomly choose where the word is to be moved to
        int moveWordNewPosition = random.nextInt(newSentence.size());
        while (moveWordNewPosition == moveWordPosition) {
            moveWordNewPosition = random.nextInt(newSentence.size());
        }
        newSentence.removeWord(moveWordPosition);
        newSentence.insertWord(moveWord, moveWordNewPosition);
        newSentence.setErrorDescription(errorInfo + " details=\"" + moveWord.getToken() + " from " + (moveWordPosition + 1) + " to " + (moveWordNewPosition + 1) + "\"");
        return newSentence;
    }

}
