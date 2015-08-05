package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
/**
 * Class SubstError
 *
 * @author Jennifer Foster
 */
public class SubstError extends Error {

    protected List<String> extraWordList;

    public SubstError(Sentence sentence, List<String> extraWords) {
        super(sentence);
        errorInfo = "errortype=\"SubstError\"";
        extraWordList = extraWords;
    }

    public SubstError(Sentence inputS) {
        super(inputS);
        errorInfo = "errortype=\"SubstError\"";
    }

    /**
     * Randomly selects a word from the sentence and replaces it with a word randomly
     * selected from the extraWordList.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (inputSentence.size() < 1) {
            throw new CannotCreateErrorException("The sentence is empty. Cannot substitute one word for another");
        }
        if (extraWordList == null || extraWordList.size() < 1) {
            throw new CannotCreateErrorException("Cannot substitute a word: the extra word list is empty.");
        }
        Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());
        Random random = new Random(newSentence.toString().hashCode());

        //randomly choose the position in the sentence where the word should be replaced
        int where = random.nextInt(newSentence.size());

        //delete the word which was at this position in the sentence
        Word oldWord = newSentence.getWord(where);
        newSentence.removeWord(where);

        //choose the new word from the extra word list and add it to the sentence
        String newWord = extraWordList.get(random.nextInt(extraWordList.size()));
        StringTokenizer tokens = new StringTokenizer(newWord, " ");
        String newToken = tokens.nextToken();
        String newTag = tokens.nextToken();
        newSentence.insertWord(new Word(newToken, newTag), where);
        newSentence.setErrorDescription(errorInfo + " details=\"" + oldWord.getToken() + "/" + newToken + " at " + (where + 1) + "\"");

        return newSentence;
    }
}
