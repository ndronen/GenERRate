package GenERRate;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Class Sentence
 *
 * @author Jennifer Foster
 */
public class Sentence extends Object {


    /**
     * The list of words in the sentence
     */
    private ArrayList words;

    /**
     * Is the sentence POS-tagged?
     */
    private boolean areTagsIncluded;


    /**
     * If the sentence is ungrammatical, a string representing what is wrong with the sentence
     */
    private String errorDescription;


    /**
     * The SGML mark-up surrounding an input sentence
     */
    private String sentenceSGML = "<s ";


    public Sentence(String theSentence, boolean includeTags) {
        if (includeTags) {
            //tokenise the sentence and place each token/tag into the vector of words
            StringTokenizer tokens = new StringTokenizer(theSentence, " ");
            words = new ArrayList();
            String tag = null;
            String word = null;
            if (tokens.countTokens() % 2 == 1) {
                System.out.println("Problem creating sentence: \t" + theSentence + " uneven number of tags and tokens");
            }
            while (tokens.hasMoreTokens()) {
                word = tokens.nextToken();
                if (tokens.hasMoreTokens()) {
                    tag = tokens.nextToken();
                } else {
                    tag = "emptyTag";
                }
                words.add(new Word(word, tag));
            }
        } else {
            //tokenise the sentence and place each token into the vector of words
            StringTokenizer tokens = new StringTokenizer(theSentence, " ");
            words = new ArrayList();
            while (tokens.hasMoreTokens()) {
                words.add(new Word(tokens.nextToken()));
            }
        }
        areTagsIncluded = includeTags;
    }

    ;

    /**
     * Get the value of words
     *
     * @return the value of words
     */
    public ArrayList getWords() {
        return words;
    }

    /**
     * Set the value of words
     *
     * @param newVar the new value of words
     */
    public void setWords(ArrayList newVar) {
        words = newVar;
    }

    /**
     * Are tags included in the sentence
     */
    public boolean areTagsIncluded() {
        return areTagsIncluded;
    }

    /**
     * Return the SGML markup associated with the sentence
     */
    public String getSentenceSGML() {
        return sentenceSGML;
    }

    /**
     * Return the SGML markup associated with the sentence
     */
    public void setSentenceSGML(String newSGML) {
        sentenceSGML = newSGML;
    }

    /**
     * Return the sentence as a string
     *
     * @return String
     */
    public String toString() {
        String theSentence = "";
        String theWord = "";
        for (int i = 0; i < words.size(); i++) {
            if (areTagsIncluded) {
                theWord = ((Word) words.get(i)).toString();
            } else {
                theWord = ((Word) words.get(i)).getToken();
            }
            if (i < words.size() - 1) {
                theSentence += theWord + " ";
            } else {
                theSentence += theWord;
            }
        }
        return theSentence;
    }


    /**
     * Return the sentence as a string
     *
     * @return String
     */
    public String toStringNoTags() {
        String theSentence = "";
        String theWord = "";
        for (int i = 0; i < words.size(); i++) {
            theWord = ((Word) words.get(i)).getToken();

            if (i < words.size() - 1) {
                theSentence += theWord + " ";
            } else {
                theSentence += theWord;
            }
        }
        return theSentence;
    }

    /**
     * Return a particular word in the sentence
     *
     * @param int the position of the word to be returned
     * @return String
     */
    public Word getWord(int index) {
        Word theWord = null;
        try {
            theWord = (Word) words.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
        } finally {
            return theWord;
        }
    }

    /**
     * Return the number of tokens in the sentence
     *
     * @return int the number of words in the sentence
     */
    public int size() {
        return words.size();
    }

    /**
     * Remove a word at the specified index from the sentence
     */
    public void removeWord(int index) {
        words.remove(index);
    }

    /**
     * Add a word to the sentence at the specified index
     */
    public void insertWord(Word word, int index) {
        words.add(index, word);
    }

    /**
     * Return the error description for this sentence
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Set the error description for this sentence
     */
    public void setErrorDescription(String anErrorDesc) {
        errorDescription = anErrorDesc;
    }
}
