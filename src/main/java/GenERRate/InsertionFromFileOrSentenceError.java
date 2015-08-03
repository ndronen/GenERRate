package GenERRate;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Class InsertionFromFileOrSentenceError
 *
 * @author Jennifer Foster
 */
public class InsertionFromFileOrSentenceError extends InsertionError {


    /**
     * Whether or not the word to be inserted comes from the same sentence or from the extra word list file.
     */
    protected boolean isSameSentence;


    public InsertionFromFileOrSentenceError(Sentence inputS) {
        super(inputS);
        errorInfo = "errortype=\"InsertionFromFileOrSentenceError\"";
        isSameSentence = true;
    }


    public InsertionFromFileOrSentenceError(Sentence inputS, ArrayList anExtraWordList) {
        super(inputS, anExtraWordList);
        errorInfo = "errortype=\"InsertionFromFileOrSentenceError\"";
        isSameSentence = false;
    }

    /**
     * Get the value of isSameSentence
     * Whether or not the word to be inserted comes from the same sentence or from the
     * extra word list file (see ErrorCreation class).
     *
     * @return the value of isSameSentence
     */
    private boolean getIsSameSentence() {
        return isSameSentence;
    }

    /**
     * Set the value of isSameSentence
     * Whether or not the word to be inserted comes from the same sentence or from the
     * extra word list file (see ErrorCreation class).
     *
     * @param newVar the new value of isSameSentence
     */
    private void setIsSameSentence(boolean newIsSameSentence) {
        isSameSentence = newIsSameSentence;
    }

    /**
     * Inserts a word into the sentence. If the isSameSentence attribute is true, a
     * word from the same sentence is randomly chosen and inserted at a random point in
     * the sentence.
     * Otherwise, a word from the extraWordList is
     * randomly chosen and inserted at a random position.
     *
     * @param inputSentence
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (!isSameSentence && extraWordList.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the extra word list is empty.");
        } else if (isSameSentence && inputSentence.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the sentence itself is empty.");
        }

        Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());
        Random random = new Random(newSentence.toString().hashCode());
        //randomly choose the position in the sentence where the extra word should be inserted
        int where = 0;
        if (newSentence.size() > 0) {
            where = random.nextInt(newSentence.size());
        }
        if (!isSameSentence) {
            //choose the extra word from the extra word list
            String extraWord = (String) extraWordList.get(random.nextInt(extraWordList.size()));
            StringTokenizer tokens = new StringTokenizer(extraWord, " ");
            newSentence.insertWord(new Word(tokens.nextToken(), tokens.nextToken()), where);
            newSentence.setErrorDescription(errorInfo + " details=\"" + extraWord + " from file at " + (where + 1) + "\"");
        } else {
            //randomly choose the extra word from the sentence itself
            Word extraWord = (Word) newSentence.getWord(random.nextInt(newSentence.size()));
            newSentence.insertWord(extraWord, where);
            newSentence.setErrorDescription(errorInfo + "details=\"" + extraWord + " from sentence at " + (where + 1) + "\"");
        }
        return newSentence;
    }


//for testing purposes
/*public static void main(String [] args)
{
	try
	{
		System.out.println("Testing the version without tags with extra word coming from sentence");
		Sentence testSentence = new Sentence("This is a test", false);
		InsertionFromFileOrSentenceError insertionError = new InsertionFromFileOrSentenceError(testSentence);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from sentence");
		testSentence = new Sentence("This DT is VBZ a DT test NN", true);
		insertionError = new InsertionFromFileOrSentenceError(testSentence);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version without tags with extra word coming from extra word list");
		testSentence = new Sentence("This is a test", false);
		insertionError = new InsertionFromFileOrSentenceError(testSentence,"testWordList.txt");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		testSentence = new Sentence("This DT is VBZ a DT test NN", true);
		insertionError = new InsertionFromFileOrSentenceError(testSentence,"testWordList.txt");
		System.out.println(insertionError.insertError());
		System.out.println();
	}
	catch (CannotCreateErrorException err)
	{
		System.err.println(err.getMessage());
	}
}*/


}
