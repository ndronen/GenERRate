package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Class SubstWordConfusionError
 *
 * @author Jennifer Foster
 */
public class SubstWordConfusionError extends SubstError {

    /**
     * The posTag of the word to be replaced
     */
    private String posTag;

    public SubstWordConfusionError(Sentence sentence, List<String> extraWords, String posTag) {
        super(sentence, extraWords);
        this.posTag = posTag;
        errorInfo = "errortype=\"Subst" + this.posTag + "Error\"";
    }

    /**
     * Get the value of posTag
     * The posTag of the word to be replaced
     *
     * @return the value of posTag
     */
    private String getPosTag() {
        return posTag;
    }

    /**
     * Set the value of posTag
     * The posTag of the word to be replaced
     *
     * @param posTag the new value of posTag
     */
    private void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    /**
     * Selects a word with the given posTag from the input sentence and replaces it with
     * another word with the same posTag from the extraWordList (see ErrorCreation
     * class).
     * Throws a CannotCreateErrorException if the sentence does not contain a word of
     * this posTag.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (sentence.size() < 1) {
            throw new CannotCreateErrorException("The sentence is empty. Cannot substitute one word for another");
        }
        if (extraWordList == null || extraWordList.size() < 1) {
            throw new CannotCreateErrorException("Cannot substitute a word: the extra word list is empty.");
        }
        //if the sentence is not tagged, this type of substitution error cannot be substituted
        if (!sentence.areTagsIncluded()) {
            throw new CannotCreateErrorException("Cannot substitute a word with posTag tag " + posTag + ". The input sentence is not tagged.");
        }
        Sentence newSentence = new Sentence(sentence.toString(), sentence.areTagsIncluded());
        //find all words in the sentence tagged as posTag
        List<Integer> listPOS = new ArrayList<Integer>();
        Word word;
        for (int i = 0; i < newSentence.size(); i++) {
            word = newSentence.getWord(i);
            if (word.getTag().equals(posTag)) {
                listPOS.add(i);
            }
        }
        //throw an exception if there is no word of this posTag in the sentence
        if (listPOS.size() < 1) {
            throw new CannotCreateErrorException("Cannot substitute a word with posTag " + posTag + " because there is none in the sentence.");
        }
        Random random = new Random(newSentence.toString().hashCode());

        //randomly choose the position in the sentence where the word should be replaced
        int where = listPOS.get(random.nextInt(listPOS.size()));
        Word substitutedWord = newSentence.getWord(where);

        //delete the word which was at this position in the sentence
        newSentence.removeWord(where);

        //build up a list of words tagged as posTag from the extra word list
        listPOS = new ArrayList<Integer>();
        String tokenTagPair;
        StringTokenizer tokens;
        String token;
        for (int i = 0; i < extraWordList.size(); i++) {
            tokenTagPair = extraWordList.get(i);
            tokens = new StringTokenizer(tokenTagPair);
            token = tokens.nextToken();
            //make sure not to include the same word as the word just removed, i.e. subst a word for itself
            if (tokens.nextToken().equals(posTag) && !token.equalsIgnoreCase(substitutedWord.getToken())) {
                listPOS.add(i);
            }
        }
        //throw an exception if there are no words of this posTag in the extra word list
        if (listPOS.size() < 1) {
            throw new CannotCreateErrorException("Cannot substitute a word with this posTag " + posTag + " because there is none in the sentence.");
        }

        //choose the new word from the extra word list and add it to the sentence
        String newWord = extraWordList.get(listPOS.get(random.nextInt(listPOS.size())));
        tokens = new StringTokenizer(newWord, " ");
        String newToken = tokens.nextToken();
        String newTag = tokens.nextToken();
        newSentence.insertWord(new Word(newToken, newTag), where);
        newSentence.setErrorDescription(errorInfo + " details=\"" + substitutedWord.getToken() + "/" + newToken + " at " + (where + 1) + "\"");

        return newSentence;
    }

    //for testing purposes
  /*public static void main(String [] args)
  {
	  try
	  {
	  	System.out.println("Testing the version with tags");
	  	Sentence testSentence = new Sentence("This DT is VBZ a DT test NN", true);
	  	SubstWordConfusionError substError = new SubstWordConfusionError(testSentence,"testWordList.txt","NN");
      		System.out.println(substError.insertError());
      		System.out.println();

		//System.out.println("Testing the version without tags");
	  	//testSentence = new Sentence("This is a test", false);
	  	//substError = new SubstWordConfusionError(testSentence,"testWordList.txt","VBZ");
      		//System.out.println(substError.insertError());
      		//System.out.println();

	  	//System.out.println("Testing the version with tags and with an invalid extra word list file");
	  	//testSentence = new Sentence("This DT is VBZ a DT test NN", true);
	  	//substError = new SubstWordConfusionError(testSentence,"doesNotExist.txt","NN");
      		//System.out.println(substError.insertError());
      		//System.out.println();

	  	//System.out.println("Testing the version with tags and with an empty sentence");
	  	//testSentence = new Sentence("", true);
	  	//substError = new SubstWordConfusionError(testSentence,"testWordList.txt","NN");
      		//System.out.println(substError.insertError());
      		//System.out.println();

	  	System.out.println("Testing the version with tags and with a sentence that doesn't contain a word with relevant tag");
	  	testSentence = new Sentence("This DT is VBZ a DT test NN", true);
	  	substError = new SubstWordConfusionError(testSentence,"testWordList.txt","NNS");
      		System.out.println(substError.insertError());
      		System.out.println();

	  }
	  catch (CannotCreateErrorException c)
	  {
		  System.err.println(c.getMessage());
	  }
  }*/


}
