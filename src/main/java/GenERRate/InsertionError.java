package GenERRate;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Class InsertionError
 *
 * @author Jennifer Foster
 */
public class InsertionError extends Error {


    protected ArrayList extraWordList;


    public InsertionError(Sentence inputS, ArrayList anExtraWordList) {
        super(inputS);
        errorInfo = "errortype=\"InsertionError\"";
        extraWordList = anExtraWordList;
    }

    public InsertionError(Sentence inputS) {
        super(inputS);
        errorInfo = "errortype=\"InsertionError\"";
    }


    /**
     * Inserts a word into the sentence at a random position.
     * Randomly chooses whether the word to be inserted will be a word in the sentence
     * or a word in the extraWordList
     *
     * @param inputSentence
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if ((extraWordList == null || extraWordList.size() < 1) && inputSentence.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the extra word list and the sentence are both empty.");
        } else {
            Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());
            Random random = new Random(newSentence.toString().hashCode());
            //randomly choose the position in the sentence where the extra word should be inserted
            int where = 0;
            if (newSentence.size() > 0) {
                where = random.nextInt(newSentence.size());
            }
            //randomly choose whether the extra word comes from the file or from the sentence itself
            int whereFrom = random.nextInt(2);
            if ((whereFrom == 0 && extraWordList != null && extraWordList.size() > 0) || (newSentence.size() < 1)) {
                //choose the extra word from the extra word list
                String extraWord = (String) extraWordList.get(random.nextInt(extraWordList.size()));
                StringTokenizer tokens = new StringTokenizer(extraWord, " ");
                String newToken = tokens.nextToken();
                String newTag = tokens.nextToken();
                newSentence.insertWord(new Word(newToken, newTag), where);
                newSentence.setErrorDescription(errorInfo + " details=\"" + newToken + " from file at " + (where + 1) + "\"");
            } else {
                //randomly choose the extra word from the sentence itself
                Word extraWord = (Word) newSentence.getWord(random.nextInt(newSentence.size()));
                newSentence.insertWord(extraWord, where);
                newSentence.setErrorDescription(errorInfo + " details=\"" + extraWord.getToken() + " from sentence at " + (where + 1) + "\"");
            }
            return newSentence;
        }
    }


    //for testing purposes
  /*public static void main(String [] args)
  {
	  try
	  {
	  	System.out.println("Testing the version without tags and with invalid extra word list file");
	  	Sentence testSentence = new Sentence("This is a test", false);
	  	InsertionError insertionError = new InsertionError(testSentence,"doesNotExist.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();

	  	System.out.println("Testing the version with tags and with invalid extra word list file");
	  	testSentence = new Sentence("This DT is VBZ a DT test NN", true);
	  	insertionError = new InsertionError(testSentence,"doesNotExist.txt");
      		System.out.println(insertionError.insertError());
		System.out.println();

	  	System.out.println("Testing the version without tags and with valid extra word list file");
	  	testSentence = new Sentence("This is a test", false);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();

	  	System.out.println("Testing the version with tags and with valid extra word list file");
	  	testSentence = new Sentence("This DT is VBZ a DT test NN", true);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
		System.out.println();

	  	System.out.println("Testing the version without tags and with valid extra word list file");
	  	testSentence = new Sentence("This is another test", false);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();

	  	System.out.println("Testing the version with tags and with valid extra word list file");
	  	testSentence = new Sentence("This DT is VBZ another DT test NN", true);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
		System.out.println();

	  	System.out.println("Testing the version without tags and with valid extra word list file");
	  	testSentence = new Sentence("This is yet another test", false);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();

	  	System.out.println("Testing the version with tags and with valid extra word list file");
	  	testSentence = new Sentence("This DT is VBZ yet RB another DT test NN", true);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
		System.out.println();

	  	System.out.println("Testing the version without tags and with valid extra word list file");
	  	testSentence = new Sentence("This is another silly test", false);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();

	  	System.out.println("Testing the version with tags and with valid extra word list file");
	  	testSentence = new Sentence("This DT is VBZ another DT silly JJ test NN", true);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
		System.out.println();

	  	System.out.println("Testing the version with an empty sentence and valid extra word list file");
	  	testSentence = new Sentence("", false);
	  	insertionError = new InsertionError(testSentence,"testWordList.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();

	  	System.out.println("Testing the version with an empty sentence and valid extra word list file");
	  	testSentence = new Sentence("", false);
	  	insertionError = new InsertionError(testSentence,"doesNotExist.txt");
      		System.out.println(insertionError.insertError());
      		System.out.println();
	  }
	catch (CannotCreateErrorException e)
	{
		System.err.println(e.getMessage());
	}
  }*/

}
