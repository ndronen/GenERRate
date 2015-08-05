package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Class InsertionPOSWhereError
 *
 * @author Jennifer Foster
 */
public class InsertionPOSWhereError extends InsertionPOSError {

    private String POSBefore;
    private String POSAfter;


    public InsertionPOSWhereError(Sentence inputS, ArrayList anExtraWordList, String aPOS, String aPOSNear, boolean isBefore) {
        super(inputS, anExtraWordList, aPOS);
        if (isBefore) {
            POSBefore = aPOSNear;
            errorInfo = "errortype=\"Insertion" + POSBefore + POS + "Error\"";
        } else {
            POSAfter = aPOSNear;
            errorInfo = "errortype=\"Insertion" + POS + POSAfter + "Error\"";
        }
    }

    public InsertionPOSWhereError(Sentence inputS, String aPOS, String aPOSNear, boolean isBefore) {
        super(inputS, aPOS);
        if (isBefore) {
            POSBefore = aPOSNear;
            errorInfo = "errortype=\"Insertion" + POSBefore + POS + "Error\"";
        } else {
            POSAfter = aPOSNear;
            errorInfo = "errortype=\"Insertion" + POS + POSAfter + "Error\"";
        }

    }


    public InsertionPOSWhereError(Sentence inputS, ArrayList anExtraWordList, String aPOSBefore, String aPOS, String aPOSAfter) {
        super(inputS, anExtraWordList, aPOS);
        POSBefore = aPOSBefore;
        POSAfter = aPOSAfter;
        errorInfo = "errortype=\"Insertion" + POSBefore + POS + POSAfter + "Error\"";
    }

    public InsertionPOSWhereError(Sentence inputS, String aPOSBefore, String aPOS, String aPOSAfter) {
        super(inputS, aPOS);
        POSBefore = aPOSBefore;
        POSAfter = aPOSAfter;
        errorInfo = "errortype=\"Insertion" + POSBefore + POS + POSAfter + "Error\"";
    }

    /**
     * Get the value of POSBefore
     *
     * @return the value of POSBefore
     */
    private String getPOSBefore() {
        return POSBefore;
    }

    /**
     * Set the value of POSBefore
     *
     * @param POSBefore the new value of POSBefore
     */
    private void setPOSBefore(String POSBefore) {
        this.POSBefore = POSBefore;
    }

    /**
     * Extends the insertError method of the InsertionPOSError class, by inserting a
     * word after another word in the sentence with a particular POS (specified in
     * POSbefore) and/or before another word with tagged as POSAfter.
     * If isSameSentence is true and no word of this particular part-of-speech tag can
     * be found, a CannotCreateErrorException is thrown. If no word in the sentence
     * with the tag POSBefore or POSAfter (for non-null values of these) are found, a CannotCreateErrorException is also thrown.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        //if the extra word list is empty and the sentence itself is empty, nothing can be added
        //throw an exception
        if (!isSameSentence && extraWordList.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the extra word list is empty.");
        }
        if (isSameSentence && inputSentence.size() < 1) {
            throw new CannotCreateErrorException("Cannot insert an extra word: the sentence itself is empty.");
        }
        //if the sentence isn't tagged, then we can't determine the POS and create this kind of error
        if (!inputSentence.areTagsIncluded()) {
            throw new CannotCreateErrorException("The input sentence is not tagged. Cannot create an " + errorInfo);
        }
        List<Integer> listPOSBefore = null;ArrayList<Integer> listPOSAfter = null;
        List<Integer> listPOSBeforeAfter = null;
        if (POSAfter == null) {
            //find all words in the input sentence tagged as POSBefore - if there are none, throw an exception
            listPOSBefore = new ArrayList<Integer>();
            Word word;
            for (int i = 0; i < inputSentence.size(); i++) {
                word = inputSentence.getWord(i);
                if (word.getTag().equals(POSBefore)) {
                    //add the position of the word to the sentence
                    listPOSBefore.add(i);
                }
            }
            if (listPOSBefore.size() < 1) {
                throw new CannotCreateErrorException("There is no word with POS " + POSBefore + " in the sentence. Cannot create an " + errorInfo);
            }
        } else if (POSBefore == null) {
            //find all words in the input sentence tagged as POSAfter - if there are none, throw an exception
            listPOSAfter = new ArrayList<Integer>();
            Word word;
            for (int i = 0; i < inputSentence.size(); i++) {
                word = inputSentence.getWord(i);
                if (word.getTag().equals(POSAfter)) {
                    //add the position of the word to the sentence
                    listPOSAfter.add(i);
                }
            }
            if (listPOSAfter.size() < 1) {
                throw new CannotCreateErrorException("There is no word with POS " + POSAfter + " in the sentence. Cannot create an " + errorInfo);
            }
        } else {
            listPOSBeforeAfter = new ArrayList<Integer>();
            if (POSBefore.equalsIgnoreCase("start")) {
                Word secondWord = inputSentence.getWord(0);

                if (secondWord.getTag().equals(POSAfter)) {
                    listPOSBeforeAfter.add(0);
                }
            } else if (POSAfter.equalsIgnoreCase("end")) {
                Word secondLastWord = inputSentence.getWord(inputSentence.size() - 1);

                if (secondLastWord.getTag().equals(POSBefore)) {
                    listPOSBeforeAfter.add(inputSentence.size() - 1);
                }
            } else {
                //find all word pairs in the input sentence tagged as POSBefore, POSAfter - if there are none, throw an exception

                Word word, nextWord;
                for (int i = 0; i < inputSentence.size() - 1; i++) {
                    word = inputSentence.getWord(i);
                    nextWord = inputSentence.getWord(i + 1);
                    if (word.getTag().equals(POSBefore) && nextWord.getTag().equals(POSAfter)) {
                        //add the position of the word to the sentence
                        listPOSBeforeAfter.add(i);
                    }
                }
            }
            if (listPOSBeforeAfter.size() < 1) {
                throw new CannotCreateErrorException("There is no word pairs with POSs " + POSBefore + "," + POSAfter + " in the sentence. Cannot create an " + errorInfo);
            }
        }
        Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());
        Random random = new Random(newSentence.toString().hashCode());

        if (!isSameSentence) {
            List<String> extraPosWordList = new ArrayList<String>();
            //find all the words tagged as POS in the extra word list
            String extraWord;
            String extraPos;
            StringTokenizer tokens;
            for (int i = 0; i < extraWordList.size(); i++) {
                extraWord = extraWordList.get(i);
                tokens = new StringTokenizer(extraWord, " ");
                if (tokens.hasMoreTokens()) {
                    tokens.nextToken();
                    if (tokens.hasMoreTokens()) {
                        extraPos = tokens.nextToken();
                        if (extraPos.equals(POS)) {
                            extraPosWordList.add(extraWord);
                        }
                    }
                }
            }
            if (extraPosWordList.size() == 0) {
                throw new CannotCreateErrorException("No word with this POS in the extra word list. Cannot create an." + errorInfo);
            }
            //choose the extra word from the selected extra word list
            extraWord = extraPosWordList.get(random.nextInt(extraPosWordList.size()));
            tokens = new StringTokenizer(extraWord, " ");
            //decide where to insert the extra word
            int where = -1;
            if (POSAfter == null) {
                where = listPOSBefore.get(random.nextInt(listPOSBefore.size())) + 1;
            } else if (POSBefore == null) {
                where = listPOSAfter.get(random.nextInt(listPOSAfter.size()));
            } else {
                if (POSBefore.equalsIgnoreCase("start")) {
                    where = listPOSBeforeAfter.get(random.nextInt(listPOSBeforeAfter.size()));
                } else {
                    where = listPOSBeforeAfter.get(random.nextInt(listPOSBeforeAfter.size()));
                }
            }
            String newToken = tokens.nextToken();
            String newTag = tokens.nextToken();
            newSentence.insertWord(new Word(newToken, newTag), where);
            newSentence.setErrorDescription(errorInfo + " details=\"" + newToken + " from file at " + (where + 1) + "\"");
        } else {
            List<Word> extraPosWordList = new ArrayList<Word>();
            //find all words tagged as POS in the sentence
            Word extraPosWord;
            for (int i = 0; i < newSentence.size(); i++) {
                extraPosWord = newSentence.getWord(i);
                if (extraPosWord.getTag().equals(POS)) {
                    extraPosWordList.add(extraPosWord);
                }
            }
            //randomly choose the extra word from the list
            if (extraPosWordList.size() == 0) {
                throw new CannotCreateErrorException("There is no word with this POS in the sentence. Cannot create an " + errorInfo);
            }
            Word extraWord = (Word) extraPosWordList.get(random.nextInt(extraPosWordList.size()));
            int where = -1;
            if (POSAfter == null) {
                where = listPOSBefore.get(random.nextInt(listPOSBefore.size())) + 1;
            } else if (POSBefore == null) {
                where = listPOSAfter.get(random.nextInt(listPOSAfter.size()));
            } else {
                if (POSBefore.equalsIgnoreCase("start")) {
                    where = listPOSBeforeAfter.get(random.nextInt(listPOSBeforeAfter.size()));
                } else {
                    where = listPOSBeforeAfter.get(random.nextInt(listPOSBeforeAfter.size()));
                }
            }
            newSentence.insertWord(extraWord, where);
            newSentence.setErrorDescription(errorInfo + " details=\"" + extraWord.getToken() + " from sentence at " + (where + 1) + "\"");
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
		InsertionPOSWhereError insertionError = new InsertionPOSWhereError(testSentence,"testWordList.txt","NN","DT",true);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		testSentence = new Sentence("Is VBZ this DT a DT test NN", true);
		insertionError = new InsertionPOSWhereError(testSentence,"testWordList.txt","NN","DT",false);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		testSentence = new Sentence("Is VBZ this DT a DT test NN", true);
		insertionError = new InsertionPOSWhereError(testSentence,"testWordList.txt","VBZ","DT",false);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		testSentence = new Sentence("Is VBZ this DT a DT test NN", true);
		insertionError = new InsertionPOSWhereError(testSentence,"testWordList.txt","VBZ","DT","NN");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from extra word list");
		testSentence = new Sentence("This DT is VBZ a DT test NN", true);
		insertionError = new InsertionPOSWhereError(testSentence,"testWordList.txt","DT","VBZ");
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from the same sentence");
		testSentence = new Sentence("Is VBZ this DT a DT test NN", true);
		insertionError = new InsertionPOSWhereError(testSentence,"NN","DT",true);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from the same sentence");
		testSentence = new Sentence("This DT is VBZ a DT test NN", true);
		insertionError = new InsertionPOSWhereError(testSentence,"DT","VBZ",true);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version without tags and with extra word coming from extra word list");
		testSentence = new Sentence("Is this a test", false);
		insertionError = new InsertionPOSWhereError(testSentence,"testWordList.txt","NN","DT",true);
		System.out.println(insertionError.insertError());
		System.out.println();

		System.out.println("Testing the version with tags and with extra word coming from same sentence");
		testSentence = new Sentence("This is another test", false);
		insertionError = new InsertionPOSWhereError(testSentence,"VBZ","VBZ",true);
		System.out.println(insertionError.insertError());
		System.out.println();
	}
	catch (CannotCreateErrorException c)
	{
		System.err.println(c.getMessage());
	}
  }*/


}
