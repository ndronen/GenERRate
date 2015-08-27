package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Class InsertionPOSError
 *
 * @author Jennifer Foster
 */
public class InsertionPOSError extends InsertionFromFileOrSentenceError {

    protected String POS;


    public InsertionPOSError(Sentence inputS, List<String> extraWords, String aPOS) {
        super(inputS, extraWords);
        POS = aPOS;
        errorInfo = "errortype=\"Insertion" + POS + "Error\"";
    }

    public InsertionPOSError(Sentence inputS, String aPOS) {
        super(inputS);
        POS = aPOS;
        errorInfo = "errortype=\"Insertion" + POS + "Error\"";
    }

    /**
     * Get the value of POS
     *
     * @return the value of POS
     */
    private String getPOS() {
        return POS;
    }

    /**
     * Set the value of POS
     *
     * @param newPOS the new value of POS
     */
    private void setPOS(String newPOS) {
        POS = newPOS;
    }

    /**
     * Extends the insertError method of the InsertionError class, by
     * choosing the word to insert based on its POS.
     * If isSameSentence is true and no word of this particular part-of-speech tag can
     * be found, a CannotCreateErrorException is thrown.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (!isSameSentence && extraWordList.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the extra word list is empty.");
        }
        if (isSameSentence && sentence.size() < 1) {
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
            List<String> extraPosWordList = new ArrayList<String>();
            //find all the words tagged as POS in the extra word list
            String extraWord;
            String extraPos;
            StringTokenizer tokens;
            for (int i = 0; i < extraWordList.size(); i++) {
                extraWord = extraWordList.get(i);
                tokens = new StringTokenizer(extraWord, " ");
                tokens.nextToken();
                extraPos = tokens.nextToken();
                if (extraPos.equals(POS)) {
                    extraPosWordList.add(extraWord);
                }
            }
            if (extraPosWordList.size() == 0) {
                throw new CannotCreateErrorException("No word with this POS in the extra word list. Cannot create an " + errorInfo);
            }
            //choose the extra word from the selected extra word list
            extraWord = extraPosWordList.get(random.nextInt(extraPosWordList.size()));
            tokens = new StringTokenizer(extraWord, " ");
            String newToken = tokens.nextToken();
            String newTag = tokens.nextToken();
            newSentence.insertWord(new Word(newToken, newTag), where);
            newSentence.setErrorDescription(errorInfo + " details=\"" + newToken + " from file at " + (where + 1) + "\"");
        } else {
            List<Word> extraPosWordList = new ArrayList<Word>();

            //if the sentence isn't tagged, then we can't determine the POS
            if (!newSentence.areTagsIncluded()) {
                throw new CannotCreateErrorException("The input sentence is not tagged. Cannot create an extra word error of this type.");
            }
            //find all words tagged as POS in the sentence
            Word extraPosWord;
            for (int i = 0; i < newSentence.size(); i++) {
                extraPosWord = newSentence.getWord(i);
                if (extraPosWord.getTag().equals(POS)) {
                    extraPosWordList.add(extraPosWord);
                }
            }
            //randomly choose the extra word from the word
            if (extraPosWordList.size() == 0) {
                throw new CannotCreateErrorException("There is no word with this POS in the sentence. Cannot create an extra word error of this type.");
            }
            Word extraWord = extraPosWordList.get(random.nextInt(extraPosWordList.size()));
            newSentence.insertWord(extraWord, where);
            newSentence.setErrorDescription(errorInfo + "details=\"" + extraWord.getToken() + " from sentence at " + (where + 1) + "\"");
        }
        return newSentence;
    }

//for testing purposes
/*public static void main(String [] args)
{
	try
	{
		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		Sentence testSentence = new Sentence("Is VBZ this DT a DT test NN", true);
		InsertionPOSError insertionError = new InsertionPOSError(testSentence,"testWordList.txt","NN");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		testSentence = new Sentence("This DT is VBZ a DT test NN", true);
		insertionError = new InsertionPOSError(testSentence,"testWordList.txt","VBZ");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from sentence");
		testSentence = new Sentence("This DT is VBZ another DT test NN", true);
		insertionError = new InsertionPOSError(testSentence,"DT");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from sentence");
		testSentence = new Sentence("This DT is VBZ a DT test NN", true);
		insertionError = new InsertionPOSError(testSentence,"NN");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version without tags with extra word coming from extra word list");
		testSentence = new Sentence("This is a test", false);
		insertionError = new InsertionPOSError(testSentence,"testWordList.txt","NN");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version without tags with extra word coming from sentence");
		testSentence = new Sentence("This is a test", false);
		insertionError = new InsertionPOSError(testSentence,"NN");
		System.out.println(insertionError.insertError());
		System.out.println();
	}
	catch (CannotCreateErrorException e)
	{
		System.err.println(e.getMessage());
	}
  }*/
}