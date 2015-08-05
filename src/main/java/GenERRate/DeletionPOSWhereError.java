package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class DeletionPOSWhereError
 *
 * @author Jennifer Foster
 */
public class DeletionPOSWhereError extends DeletionPOSError {


    /**
     * The POS of the word before the word to be deleted.
     */
    private String POSBefore;

    /**
     * The POS of the word after the word to be deleted.
     */
    private String POSAfter;

    public DeletionPOSWhereError(Sentence inputS, String thePOS, String thePOSNear, boolean isBefore) {
        super(inputS, thePOS);
        if (isBefore) {
            POSBefore = thePOSNear;
            errorInfo = "errortype=\"Deletion" + POSBefore + POS + "Error\"";
        } else {
            POSAfter = thePOSNear;
            errorInfo = "errortype=\"Deletion" + POS + POSAfter + "Error\"";
        }


    }

    public DeletionPOSWhereError(Sentence inputS, String thePOSBefore, String thePOS, String thePOSAfter) {
        super(inputS, thePOS);
        POSBefore = thePOSBefore;
        POSAfter = thePOSAfter;
        errorInfo = "errortype=\"Deletion" + POSBefore + POS + POSAfter + "Error\"";
    }

    //For testing purposes
    public static void main(String[] args) {
        try {
            System.out.println("Testing the version with tags");
            Sentence testSentence = new Sentence("This DT man NN walks VBZ and CONJ talks VBZ", true);
            DeletionPOSWhereError deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());

 		/*System.out.println("Testing the version with tags");
          testSentence = new Sentence("This DT man NN walks VBZ and CONJ talks VBZ", true);
  		deletionPOSWhereError = new DeletionPOSWhereError(testSentence,"VBZ", "NN", false);
  		System.out.println(deletionPOSWhereError.insertError());*/

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT man NN walks VBZ and CONJ talks VBZ", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "CONJ", false);
            System.out.println(deletionPOSWhereError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT man NN walks VBZ and CONJ talks VBZ", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", "CONJ");
            System.out.println(deletionPOSWhereError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT woman NN walks VBZ and CONJ this DT woman NN talks VBZ", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT woman NN walks VBZ and CONJ this DT woman NN talks VBZ and CONJ this DT woman NN laughs VBZ", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("He PRP walked VBD and CONJ talked VBD", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("This DT man NN walked VBD and CONJ talked VBD", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());

            System.out.println("Testing the version with tags");
            testSentence = new Sentence("He PRP walks VBZ and CONJ talks VBZ", true);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());


            System.out.println("Testing the version without tags");
            testSentence = new Sentence("This is a test", false);
            deletionPOSWhereError = new DeletionPOSWhereError(testSentence, "VBZ", "NN", true);
            System.out.println(deletionPOSWhereError.insertError());

        } catch (CannotCreateErrorException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Get the value of POSBefore
     * The POS of the word before the word to be deleted.
     *
     * @return the value of POSBefore
     */
    private String getPOSBefore() {
        return POSBefore;
    }

    /**
     * Set the value of POSBefore
     * The POS of the word before the word to be deleted.
     *
     * @param newVar the new value of POSBefore
     */
    private void setPOSBefore(String newVar) {
        POSBefore = newVar;
    }

    /**
     * Get the value of POSBefore
     * The POS of the word before the word to be deleted.
     *
     * @return the value of POSBefore
     */
    private String getPOSAfter() {
        return POSAfter;
    }

    /**
     * Set the value of POSBefore
     * The POS of the word before the word to be deleted.
     *
     * @param newVar the new value of POSBefore
     */
    private void setPOSAfter(String newVar) {
        POSAfter = newVar;
    }

    /**
     * Deletes a word with part-of-speech POS appearing directly after another word
     * with part-of-speech POSBefore and/or appearing directly before another word with part-of-speech POSAfter
     * If POSBefore is null, a CannotCreateErrorException is thrown if the pattern POS POSAfter does not exist
     * If POSAfter is null, a CannotCreateErrorException is thrown if the pattern POSBefore POS does not exist
     * If POSBefore and POSAfter are not null, a CannotCreateErrorException is thrown if the pattern POSBefore POS POSAfter does not exist
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        if (!inputSentence.areTagsIncluded()) {
            throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". The input sentence is not tagged");
        } else {
            //create the new sentence
            Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());
            Word wordToGo, wordBefore, wordAfter, word;

            if (POSAfter == null) {
                //find pair sequences tagged as POSBefore, POS - store position of POS
                List<Integer> wordsForDeletion = new ArrayList<Integer>();
                for (int i = 1; i < newSentence.size(); i++) {
                    wordBefore = newSentence.getWord(i - 1);
                    word = newSentence.getWord(i);
                    if (word.getTag().equals(POS) && wordBefore.getTag().equals(POSBefore)) {
                        wordsForDeletion.add(i);
                    }
                }
                //if there aren't any word pairs tagged as POSBefore, POS in sentence, then we can't do anything
                if (wordsForDeletion.size() == 0) {
                    throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". There aren't any word pairs tagged as: " + POSBefore + "," + POS + " in the sentence.");
                } else {
                    //randomly pick one of these and delete it from the sentence
                    Random rand = new Random(newSentence.hashCode());
                    int randNo = rand.nextInt(wordsForDeletion.size());
                    int randPos = (wordsForDeletion.get(randNo)).intValue();
                    wordToGo = newSentence.getWord(randPos);
                    wordBefore = newSentence.getWord(randPos - 1);
                    newSentence.removeWord(randPos);

                    newSentence.setErrorDescription(errorInfo + " details=\"" + wordToGo.getToken() + " at " + (randPos + 1) + " after " + wordBefore.getToken() + "\"");
                }
            } else if (POSBefore == null) {
                //find pair sequences tagged as POS, POSAfter - store position of POS
                List<Integer> wordsForDeletion = new ArrayList<Integer>();
                for (int i = 0; i < newSentence.size() - 1; i++) {
                    wordAfter = newSentence.getWord(i + 1);
                    word = newSentence.getWord(i);
                    if (word.getTag().equals(POS) && wordAfter.getTag().equals(POSAfter)) {
                        wordsForDeletion.add(i);
                    }
                }
                //if there aren't any word pairs tagged as POSBefore, POS in sentence, then we can't do anything
                if (wordsForDeletion.size() == 0) {
                    throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". There aren't any word pairs tagged as: " + POS + "," + POSAfter + " in the sentence.");
                } else {
                    //randomly pick one of these and delete it from the sentence
                    Random rand = new Random(newSentence.hashCode());
                    int randNo = rand.nextInt(wordsForDeletion.size());
                    int randPos = wordsForDeletion.get(randNo);
                    wordToGo = newSentence.getWord(randPos);
                    wordAfter = newSentence.getWord(randPos + 1);
                    newSentence.removeWord(randPos);

                    newSentence.setErrorDescription(errorInfo + " details=\"" + wordToGo.getToken() + " at " + (randPos + 1) + " before " + wordAfter.getToken() + "\"");
                }
            } else {
                if (POSBefore.equalsIgnoreCase("start") && newSentence.size() < 2) {
                    throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". There are less than two words in the input sentence.");
                } else if (POSAfter.equalsIgnoreCase("end") && newSentence.size() < 2) {
                    throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". There are less than two words in the input sentence.");
                } else if (newSentence.size() < 3) {
                    throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". There are less than three words in the input sentence.");
                }
                List<Integer> wordsForDeletion = new ArrayList<Integer>();
                //	If POSBEfore is "start", see if the first word is tagged as POS and the second as POSAfter
                if (POSBefore.equalsIgnoreCase("start")) {
                    Word firstWord = newSentence.getWord(0);
                    Word secondWord = newSentence.getWord(1);
                    if (firstWord.getTag().equals(POS) && secondWord.getTag().equals(POSAfter)) {
                        wordsForDeletion.add(0);
                    }
                }
                //	If POSBEfore is "end", see if the second last word is tagged as POSBefore and the last as POS
                else if (POSAfter.equalsIgnoreCase("end")) {
                    Word lastWord = newSentence.getWord(newSentence.size() - 1);
                    Word secondLastWord = newSentence.getWord(newSentence.size() - 2);
                    if (lastWord.getTag().equals(POS) && secondLastWord.getTag().equals(POSBefore)) {
                        wordsForDeletion.add(newSentence.size() - 1);
                    }
                }
                //find pair sequences tagged as POSBefore, POS, POSAfter - store position of POS
                else {
                    for (int i = 1; i < newSentence.size() - 1; i++) {
                        wordAfter = newSentence.getWord(i + 1);
                        wordBefore = newSentence.getWord(i - 1);
                        word = newSentence.getWord(i);
                        if (word.getTag().equals(POS) && wordAfter.getTag().equals(POSAfter) && wordBefore.getTag().equals(POSBefore)) {
                            wordsForDeletion.add(i);
                        }
                    }
                }
                //if there aren't any word pairs tagged as POSBefore, POS in sentence, then we can't do anything
                if (wordsForDeletion.size() == 0) {
                    throw new CannotCreateErrorException("Cannot introduce a " + errorInfo + ". There aren't any word pairs tagged as: " + POSBefore + "," + POS + "," + POSAfter + " in the sentence.");
                } else {
                    //randomly pick one of these and delete it from the sentence
                    Random rand = new Random(newSentence.hashCode());
                    int randNo = rand.nextInt(wordsForDeletion.size());
                    int randPos = wordsForDeletion.get(randNo);
                    wordToGo = newSentence.getWord(randPos);
                    if (randPos + 1 < newSentence.size()) {
                        wordAfter = newSentence.getWord(randPos + 1);
                    } else {
                        wordAfter = new Word("end");
                    }
                    if (randPos - 1 >= 0) {
                        wordBefore = newSentence.getWord(randPos - 1);
                    } else {
                        wordBefore = new Word("start");
                    }
                    newSentence.removeWord(randPos);

                    newSentence.setErrorDescription(errorInfo + " details=\"" + wordToGo.getToken() + " at " + (randPos + 1) + " after " + wordBefore.getToken() + " before " + wordAfter.getToken() + "\"");
                }
            }

            return newSentence;
        }
    }


}
