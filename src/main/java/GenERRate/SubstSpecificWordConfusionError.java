package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class SubstSpecificWordConfusionError
 *
 * @author Jennifer Foster
 */
public class SubstSpecificWordConfusionError extends SubstError {

    /**
     * The actual word to be replaced.
     */
    private Word wordToBeReplaced;

    /**
     * The word which will replace wordToBeReplaced.
     */
    private Word replacementWord;

    public SubstSpecificWordConfusionError(Sentence inputS, Word wordToBeReplaced, Word replacement) {
        super(inputS);
        this.wordToBeReplaced = wordToBeReplaced;
        replacementWord = replacement;
        errorInfo = "errortype=\"Subst" + this.wordToBeReplaced + replacementWord + "Error\"";
    }

    /**
     * Get the value of wordToBeReplaced
     * The actual word to be replaced.
     *
     * @return the value of wordToBeReplaced
     */
    private Word getWordToBeReplaced() {
        return wordToBeReplaced;
    }

    /**
     * Set the value of wordToBeReplaced
     * The actual word to be replaced.
     *
     * @param wordToBeReplaced the new value of wordToBeReplaced
     */
    private void setWordToBeReplaced(Word wordToBeReplaced) {
        this.wordToBeReplaced = wordToBeReplaced;
    }

    /**
     * Get the value of replacementWord
     * The word which will replace wordToBeReplaced.
     *
     * @return the value of replacementWord
     */
    private Word getReplacementWord() {
        return replacementWord;
    }

    /**
     * Set the value of replacementWord
     * The word which will replace wordToBeReplaced.
     *
     * @param replacementWord the new value of replacementWord
     */
    private void setReplacementWord(Word replacementWord) {
        this.replacementWord = replacementWord;
    }

    /**
     * Searches the sentence for wordToBeReplaced. If it is found, it is replaced
     * by replacementWord. Otherwise a CannotCreateErrorException is thrown.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (sentence.size() < 1) {
            throw new CannotCreateErrorException("The sentence is empty. Cannot substitute one word for another");
        }
        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());
        //see if the word to be replaced is in the input sentence, if not throw an exception
        boolean isWordThere = false;
        Word word;
        List<Integer> whereList = new ArrayList<Integer>();
        for (int i = 0; i < newSentence.size() && !isWordThere; i++) {
            word = newSentence.getWord(i);
            if (word.getToken().equals(wordToBeReplaced.getToken())) {
                isWordThere = true;
                whereList.add(i);
            }
        }
        if (!isWordThere) {
            throw new CannotCreateErrorException("Cannot substitute one word for another. " +
                    "The word to be replaced '" + wordToBeReplaced.getToken() + "' is not in the input sentence");
        }

        Random random = new Random(newSentence.toString().hashCode());

        //if there is more than one instance of the word to be replaced in the sentence, randomly choose one of them
        int where = whereList.get(random.nextInt(whereList.size()));

        //delete the word which was at this position in the sentence
        newSentence.removeWord(where);
        newSentence.insertWord(replacementWord, where);

        newSentence.setErrorDescription(errorInfo + " details=\"" + wordToBeReplaced.getToken() + "/" + replacementWord.getToken() + " at " + (where + 1) + "\"");

        return newSentence;

    }

}
