package GenERRate;

import java.util.List;
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


    public InsertionFromFileOrSentenceError(Sentence inputS, List anExtraWordList) {
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
     * @param newIsSameSentence the new value of isSameSentence
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
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (!isSameSentence && extraWordList.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the extra word list is empty.");
        } else if (isSameSentence && sentence.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the sentence itself is empty.");
        }

        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());
        Random random = new Random(newSentence.toString().hashCode());
        //randomly choose the position in the sentence where the extra word should be inserted
        int where = 0;
        if (newSentence.size() > 0) {
            where = random.nextInt(newSentence.size());
        }
        if (!isSameSentence) {
            //choose the extra word from the extra word list
            String extraWord = extraWordList.get(random.nextInt(extraWordList.size()));
            StringTokenizer tokens = new StringTokenizer(extraWord, " ");
            String token = tokens.nextToken();
            String tag = tokens.nextToken();
            newSentence.insertWord(new Word(token, tag), where);
            setErrorInfo(token);
            newSentence.setErrorDescription(errorInfo + " details=\"" + extraWord + " from file at " + (where + 1) + "\"");
        } else {
            //randomly choose the extra word from the sentence itself
            Word extraWord = newSentence.getWord(random.nextInt(newSentence.size()));
            newSentence.insertWord(extraWord, where);
            newSentence.setErrorDescription(errorInfo + "details=\"" + extraWord + " from sentence at " + (where + 1) + "\"");
        }
        return newSentence;
    }
}
