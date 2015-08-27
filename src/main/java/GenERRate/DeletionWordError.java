package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ndronen on 8/26/15.
 */
public class DeletionWordError extends DeletionError {
    private final String token;

    public DeletionWordError(Sentence sentence, String token) {
        super(sentence);
        this.token = token;
    }

    public Sentence insertError() throws CannotCreateErrorException {
        Sentence sentenceWithError = new Sentence(sentence.toString(), sentence.areTagsIncluded());

        List<Integer> candidates = new ArrayList<Integer>();
        for (int i = 0; i < sentenceWithError.size(); i++) {
            Word candidate = sentenceWithError.getWord(i);
            if (candidate.getToken().equals(token)) {
                candidates.add(i);
            }
        }

        if (candidates.size() == 0) {
            throw new CannotCreateErrorException("Cannot delete word.  The word '" + token +
                    "' does not appear in the sentence");
        }

        Random rand = new Random(sentenceWithError.hashCode());
        int i = rand.nextInt(candidates.size());
        int position = candidates.get(i);
        Word removed = sentenceWithError.getWord(position);
        sentenceWithError.removeWord(position);

        sentenceWithError.setErrorDescription(
                errorInfo + " details=\"" + removed.getToken() + " at " + (position + 1) + "\"");

        return sentenceWithError;
    }
}
