package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Class SubstWrongFormError
 *
 * @author Jennifer Foster
 */
public class SubstWrongFormError extends SubstError {

    /**
     * The part-of-speech tag set in effect (WSJ, CLAWS).
     */
    private final PartOfSpeech tagSet;

    /**
     * The posTag of the word to be changed.
     */
    private final String posTag;
    /**
     * The nature of the change to the word. What form the change can take will depend on the part-of-speech for the word.
     * For a verb, for example, this might be the tense or number. For an adjective or an adverb, the change will be between comparative,  superlative or normal forms.
     */
    private String form;

    private final List<String> extraWords;

    public SubstWrongFormError(Sentence sentence, PartOfSpeech tagSet, String posTag, String newPosTag, List<String> extraWords) {
        super(sentence);
        this.tagSet = tagSet;
        this.posTag = posTag;
        this.form = newPosTag;
        this.extraWords = extraWords;
        super.errorInfo = "errortype=\"SubstWrongForm" + this.posTag + form + "Error\"";
    }

    /**
     * Selects a word of the given posTag from the sentence and changes the word based on
     * the value of the form attribute.
     * Throws a CannotCreateErrorException if the sentence does not contain a posTag with
     * this form.
     *
     * @return Sentence
     */
    public Sentence insertError() throws CannotCreateErrorException {
        // if the sentence itself is empty, nothing can be added
        // throw an exception
        if (inputSentence.size() < 1) {
            throw new CannotCreateErrorException("The sentence is empty. Cannot substitute one word for another");
        }
        //if the sentence is not tagged, this type of substitution error cannot be substituted
        if (!inputSentence.areTagsIncluded()) {
            throw new CannotCreateErrorException("Cannot substitute a word with posTag tag " + posTag + ". The input sentence is not tagged.");
        }
        Sentence newSentence = new Sentence(inputSentence.toString(), inputSentence.areTagsIncluded());
        //find all words in the sentence tagged as posTag
        ArrayList listPOS = new ArrayList();
        Word word;
        for (int i = 0; i < newSentence.size(); i++) {
            word = (Word) newSentence.getWord(i);
            if (word.getTag().equals(posTag) && posTag.equals(tagSet.INF)) {
                Word nextWord = (Word) newSentence.getWord(i + 1);
                if (nextWord != null && nextWord.getTag().equals(tagSet.VERB_BASE)) {
                    listPOS.add(new Integer(i));
                }
            } else if (word.getTag().equals(posTag)) {
                listPOS.add(new Integer(i));
            }
        }
        //throw an exception if there is no word of this posTag in the sentence
        if (listPOS.size() < 1) {
            throw new CannotCreateErrorException("Cannot substitute a word with posTag " + posTag + " because there is none in the sentence.");
        }
        Random random = new Random(newSentence.toString().hashCode());

        //randomly choose the position in the sentence where the word should be replaced
        int where = ((Integer) listPOS.get(random.nextInt(listPOS.size()))).intValue();

        //delete the word which was at this position in the sentence
        Word oldWord = (Word) newSentence.getWord(where);
        Word newWord = null;
        Word anotherNewWord = null;
        int where2 = -1;

        //examine the posTag and the form to see how to substitute the word
        if ((posTag.equals(tagSet.SINGULAR_NOUN)) && (form.equals(tagSet.PLURAL_NOUN))) {
            newWord = makeNounPlural(oldWord);
        } else if ((posTag.equals(tagSet.PLURAL_NOUN)) && (form.equals(tagSet.SINGULAR_NOUN))) {
            newWord = makeNounSingular(oldWord);
        } else if ((posTag.equals(tagSet.VERB_THIRD_SING)) && (form.equals(tagSet.VERB_NON_THIRD_SING))) {
            newWord = makeVerbPlural(oldWord);
        } else if ((posTag.equals(tagSet.VERB_NON_THIRD_SING)) && (form.equals(tagSet.VERB_THIRD_SING))) {
            newWord = makeVerbSingular(oldWord);
        } else if ((posTag.equals(tagSet.VERB_THIRD_SING)) && (form.equals(tagSet.VERB_PRES_PART))) {
            newWord = thirdSingToPresP(oldWord);
        }
    /*else if ((posTag.equals(tagSet.VERB_NON_THIRD_SING)) && (form.equals(tagSet.VERB_PAST_PART)))
	{
		newWord = nonThirdSingToPastP(oldWord);
	}*/
        else if ((posTag.equals(tagSet.VERB_PRES_PART)) && (form.equals(tagSet.VERB_PAST_PART))) {
            newWord = presPToPastP(oldWord);
        } else if ((posTag.equals(tagSet.VERB_PRES_PART)) && (form.equals(tagSet.VERB_THIRD_SING))) {
            newWord = presPToThirdSing(oldWord);
        } else if ((posTag.equals(tagSet.VERB_PRES_PART)) && (form.equals(tagSet.VERB_NON_THIRD_SING))) {
            newWord = presPToNonThirdSing(oldWord);
        } else if ((posTag.equals(tagSet.VERB_PRES_PART)) && (form.equals(tagSet.INF))) {
            newWord = presPToInf(oldWord);
            anotherNewWord = new Word("to", tagSet.INF);
        } else if ((posTag.equals(tagSet.INF)) && (form.equals(tagSet.VERB_PRES_PART))) {
            oldWord = (Word) newSentence.getWord(where + 1);
            newWord = baseToPresP(oldWord);
            where2 = where + 1;
        } else if ((posTag.equals(tagSet.VERB_BASE)) && (form.equals(tagSet.VERB_PRES_PART))) {
            newWord = baseToPresP(oldWord);
        } else if ((posTag.equals(tagSet.VERB_NON_THIRD_SING)) && (form.equals(tagSet.VERB_PRES_PART))) {
            newWord = nonThirdSingToPresP(oldWord);
        } else if ((posTag.equals(tagSet.VERB_PAST_PART)) && (form.equals(tagSet.VERB_THIRD_SING))) {
            newWord = pastPToThirdSing(oldWord);
        } else if ((posTag.equals(tagSet.VERB_PAST_PART)) && (form.equals(tagSet.VERB_PRES_PART))) {
            newWord = pastPToPresP(oldWord);
        } else if ((posTag.equals(tagSet.VERB_BASE)) && (form.equals(tagSet.VERB_THIRD_SING))) {
            newWord = baseToThirdSing(oldWord);
        } else if ((posTag.equals(tagSet.ADJ)) && (form.equals(tagSet.ADJ_COMP))) {
            newWord = regularAdjToComparative(oldWord);
        } else if ((posTag.equals(tagSet.ADJ)) && (form.equals(tagSet.ADJ_SUP))) {
            newWord = regularAdjToSuperlative(oldWord);
        } else if ((posTag.equals(tagSet.ADJ_COMP)) && (form.equals(tagSet.ADJ_SUP))) {
            newWord = comparativeAdjToSuperlative(oldWord);
        } else if ((posTag.equals(tagSet.ADJ_COMP)) && (form.equals(tagSet.ADJ))) {
            newWord = comparativeAdjToRegular(oldWord);
        } else if ((posTag.equals(tagSet.ADJ_SUP)) && (form.equals(tagSet.ADJ))) {
            newWord = superlativeAdjToRegular(oldWord);
        } else if ((posTag.equals(tagSet.ADJ_SUP)) && (form.equals(tagSet.ADJ_COMP))) {
            newWord = superlativeAdjToComparative(oldWord);
        } else if ((posTag.equals(tagSet.ADV)) && (form.equals(tagSet.ADJ)) && (oldWord.getToken().endsWith("ly"))) {
            newWord = adverbToAdj(oldWord);
        } else {
            //find all the words tagged as form in the extra word list
            ArrayList formList = new ArrayList();
            String tokenTag;
            String pos;
            StringTokenizer tokens;
            for (int i = 0; i < extraWords.size(); i++) {
                tokenTag = (String) extraWords.get(i);
                tokens = new StringTokenizer(tokenTag, " ");
                if (tokens.hasMoreTokens()) {
                    tokens.nextToken();
                }
                if (tokens.hasMoreTokens()) {
                    pos = tokens.nextToken();
                    if (pos.equals(form)) {
                        formList.add(tokenTag);
                    }
                }

            }
            if (formList.size() == 0) {
                throw new CannotCreateErrorException("No word with the posTag " + form + " in the extra word list. Cannot create an " + errorInfo);
            }

            //randomly select the replacing word
            int newWordPos = random.nextInt(formList.size());
            String newWordString = (String) formList.get(newWordPos);
            tokens = new StringTokenizer(newWordString, " ");
            if (tokens.countTokens() == 2) {
                newWord = new Word(tokens.nextToken(), tokens.nextToken());
            }
        }

        if (newWord == null) {
            throw new CannotCreateErrorException("There was a problem inserting a SubstWrongFormError.");
        } else {
            //String oldWord = ((Word) newSentence.getWord(where)).getToken();
            if (where2 == -1) {
                newSentence.removeWord(where);
            } else {
                //oldWord += " " + ((Word) newSentence.getWord(where+1)).getToken();
                newSentence.removeWord(where);
                newSentence.removeWord(where);
            }
            if (anotherNewWord == null) {
                newSentence.insertWord(newWord, where);
            } else {
                newSentence.insertWord(anotherNewWord, where);
                newSentence.insertWord(newWord, where + 1);
            }
            newSentence.setErrorDescription(errorInfo + " details=\"" + oldWord.getToken() + "/" + newWord.getToken() + " at " + (where + 1) + "\"");
        }

        return newSentence;

    }

    public Word makeNounSingular(Word word) {
        if (word.getToken().endsWith("ies")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "y", tagSet.SINGULAR_NOUN);
        } else if (word.getToken().endsWith("men")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "man", tagSet.SINGULAR_NOUN);
        } else if (word.getToken().endsWith("a")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "um", tagSet.SINGULAR_NOUN);
        } else if (word.getToken().endsWith("ches") || word.getToken().endsWith("sses") || word.getToken().endsWith("zes") || word.getToken().endsWith("shes") || word.getToken().endsWith("xes")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2), tagSet.SINGULAR_NOUN);
        } else if (word.getToken().length() > 0) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1), tagSet.SINGULAR_NOUN);
        } else {
            return null;
        }
    }

    public Word makeNounPlural(Word word) {

        if (word.getToken().endsWith("man")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "men", tagSet.PLURAL_NOUN);
        } else if (word.getToken().endsWith("ch") || word.getToken().endsWith("s") || word.getToken().endsWith("z") || word.getToken().endsWith("sh") || word.getToken().endsWith("x")) {
            return new Word(word.getToken() + "es", tagSet.PLURAL_NOUN);
        } else if (word.getToken().endsWith("y") && word.getToken().length() > 1 && !ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ies", tagSet.PLURAL_NOUN);
        } else if (word.getToken().endsWith("um")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "a", tagSet.PLURAL_NOUN);
        } else {
            return new Word(word.getToken() + "s", tagSet.PLURAL_NOUN);
        }
    }

    public Word makeVerbSingular(Word word) {
        if (word.getToken().equalsIgnoreCase("are") || word.getToken().equalsIgnoreCase("'re") || word.getToken().equalsIgnoreCase("'m") || word.getToken().equalsIgnoreCase("am")) {
            return new Word("is", tagSet.VERB_THIRD_SING);
        } else if (word.getToken().equalsIgnoreCase("have")) {
            return new Word("has", tagSet.VERB_THIRD_SING);
        } else if (word.getToken().equalsIgnoreCase("do")) {
            return new Word("does", tagSet.VERB_THIRD_SING);
        } else if (word.getToken().equalsIgnoreCase("go")) {
            return new Word("goes", tagSet.VERB_THIRD_SING);
        } else if (word.getToken().endsWith("y") && word.getToken().length() > 1 && !ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ies", tagSet.VERB_THIRD_SING);
        } else if (word.getToken().endsWith("ch") || word.getToken().endsWith("x") || word.getToken().endsWith("s") || word.getToken().endsWith("z") || word.getToken().endsWith("sh")) {
            return new Word(word.getToken() + "es", tagSet.VERB_THIRD_SING);
        } else {
            return new Word(word.getToken() + "s", tagSet.VERB_THIRD_SING);
        }
    }

    public Word makeVerbPlural(Word word) {
        //System.out.println("In makeVerbPlural");
        if (word.getToken().equalsIgnoreCase("is") || word.getToken().equalsIgnoreCase("'s")) {
            return new Word("are", tagSet.VERB_NON_THIRD_SING);
        } else if (word.getToken().equalsIgnoreCase("has")) {
            return new Word("have", tagSet.VERB_NON_THIRD_SING);
        } else if (word.getToken().equalsIgnoreCase("does")) {
            return new Word("do", tagSet.VERB_NON_THIRD_SING);
        } else if (word.getToken().equalsIgnoreCase("goes")) {
            return new Word("go", tagSet.VERB_NON_THIRD_SING);
        } else if (word.getToken().endsWith("ies")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "y", tagSet.VERB_NON_THIRD_SING);
        } else if (word.getToken().endsWith("sses") || word.getToken().endsWith("ches") || word.getToken().endsWith("xes") || word.getToken().endsWith("zes") || word.getToken().endsWith("shes")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2), tagSet.VERB_NON_THIRD_SING);
        } else if (word.getToken().length() > 0) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1), tagSet.VERB_NON_THIRD_SING);
        } else {
            return null;
        }
    }

    public Word thirdSingToPresP(Word word) {
        String verbTag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("is")) {
            return new Word("being", verbTag);
        } else if (word.getToken().equalsIgnoreCase("has")) {
            return new Word("having", verbTag);
        } else if (word.getToken().endsWith("ies")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ying", verbTag);
        } else if (word.getToken().endsWith("es")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "ing", verbTag);
        } else if (word.getToken().endsWith("ts") && word.getToken().length() > 1
                && (ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 3)))
                && !(word.getToken().substring(word.getToken().length() - 4, word.getToken().length() - 2).equals("ea"))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ting", verbTag);
        } else if (word.getToken().length() > 0) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", verbTag);
        } else {
            return null;
        }
    }


    public Word nonThirdSingToPresP(Word word) {
        String verbTag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("are")) {
            return new Word("being", verbTag);
        } else if (word.getToken().endsWith("e") && !word.getToken().endsWith("ee")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", verbTag);
        } else if (word.getToken().endsWith("t")
                && (!(word.getToken().substring(word.getToken().length() - 3, word.getToken().length() - 1).equals("ea")))
                && word.getToken().length() > 1
                && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken() + "ting", verbTag);
        } else {
            return new Word(word.getToken() + "ing", verbTag);
        }
    }

    public Word pastpToPresp(Word word) {
        String verbTag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("been")) {
            return new Word("being", verbTag);
        } else if (word.getToken().equalsIgnoreCase("had")) {
            return new Word("having", verbTag);
        } else if (word.getToken().equalsIgnoreCase("done")) {
            return new Word("doing", verbTag);
        } else if (word.getToken().equalsIgnoreCase("gone")) {
            return new Word("going", verbTag);
        } else if (word.getToken().equalsIgnoreCase("taken")) {
            return new Word("taking", verbTag);
        } else if (word.getToken().equalsIgnoreCase("left")) {
            return new Word("leaving", verbTag);
        } else if (word.getToken().endsWith("come")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", verbTag);
        } else if (word.getToken().endsWith("ied")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ying", verbTag);
        } else if (word.getToken().length() > 1) {
            //ed
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "ing", verbTag);
        } else {
            return null;
        }
    }

    public Word baseToPresP(Word word) {
        String verbTag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("be")) {
            return new Word("being", verbTag);
        } else if (word.getToken().endsWith("e") && !word.getToken().endsWith("ee")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", verbTag);
        } else if ((word.getToken().endsWith("t") && word.getToken().length() > 1)
                && ((word.getToken().length() > 3) && !(word.getToken().substring(word.getToken().length() - 4, word.getToken().length() - 2).equals("ea")))
                && (ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2)))) {
            return new Word(word.getToken() + "ting", verbTag);
        } else {
            return new Word(word.getToken() + "ing", verbTag);
        }
    }

    public Word presPToPastP(Word word) {
        String verbTag = tagSet.VERB_PAST_PART;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("been", verbTag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("had", verbTag);
        } else if (word.getToken().endsWith("coming")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "e", verbTag);
        } else if (word.getToken().equalsIgnoreCase("going")) {
            return new Word("gone", verbTag);
        } else if (word.getToken().equalsIgnoreCase("doing")) {
            return new Word("done", verbTag);
        } else if (word.getToken().equalsIgnoreCase("leaving")) {
            return new Word("left", verbTag);
        } else if (word.getToken().equalsIgnoreCase("taking")) {
            return new Word("taken", verbTag);
        } else if (word.getToken().equalsIgnoreCase("seeing")) {
            return new Word("seen", verbTag);
        } else if (word.getToken().equalsIgnoreCase("making")) {
            return new Word("made", verbTag);
        } else if (word.getToken().equalsIgnoreCase("bringing")) {
            return new Word("brought", verbTag);
        } else if (word.getToken().equalsIgnoreCase("teaching")) {
            return new Word("taught", verbTag);
        } else if (word.getToken().equalsIgnoreCase("reading")) {
            return new Word("read", verbTag);
        } else if (word.getToken().equalsIgnoreCase("letting")) {
            return new Word("let", verbTag);
        } else if (word.getToken().endsWith("wing") &&
                (word.getToken().charAt(word.getToken().length() - 5) == 'a' ||
                        word.getToken().charAt(word.getToken().length() - 5) == 'o')) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "n", verbTag);
        } else if (word.getToken().endsWith("ying") && !ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 5))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 4) + "ied", verbTag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ed", verbTag);
        } else {
            return null;
        }
    }

    public Word presPToThirdSing(Word word) {
        String verbTag = tagSet.VERB_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("is", verbTag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("has", verbTag);
        } else if (word.getToken().endsWith("ching")
                || word.getToken().endsWith("ssing") || word.getToken().endsWith("oing") || word.getToken().endsWith("dging")
                || word.getToken().endsWith("oting")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "es", verbTag);
        } else if (word.getToken().endsWith("ying")) {

            return new Word(word.getToken().substring(0, word.getToken().length() - 4) + "ies", verbTag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "s", verbTag);
        } else {
            return null;
        }
    }

    public Word presPToNonThirdSing(Word word) {
        String verbTag = tagSet.VERB_NON_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("are", verbTag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("have", verbTag);
        } else if (word.getToken().endsWith("ching")
                || word.getToken().endsWith("ssing") || word.getToken().endsWith("oing") || word.getToken().endsWith("dging")
                || word.getToken().endsWith("oting")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "e", verbTag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), verbTag);
        } else {
            return null;
        }
    }

    public Word presPToInf(Word word) {
        String verbTag = tagSet.VERB_NON_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("be", verbTag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("have", verbTag);
        } else if (word.getToken().endsWith("ching")
                || word.getToken().endsWith("ssing") || word.getToken().endsWith("oing") || word.getToken().endsWith("dging")
                || word.getToken().endsWith("oting")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "e", verbTag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), verbTag);
        } else {
            return null;
        }
    }

    public Word pastPToThirdSing(Word word) {
        String verbTag = tagSet.VERB_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("been")) {
            return new Word("is", verbTag);
        } else if (word.getToken().equalsIgnoreCase("had")) {
            return new Word("has", verbTag);
        } else if (word.getToken().equalsIgnoreCase("done")) {
            return new Word("does", verbTag);
        } else if (word.getToken().equalsIgnoreCase("gone")) {
            return new Word("goes", verbTag);
        } else if (word.getToken().equalsIgnoreCase("taken")) {
            return new Word("takes", verbTag);
        } else if (word.getToken().equalsIgnoreCase("left")) {
            return new Word("leaves", verbTag);
        } else if (word.getToken().length() > 5 && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 4))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "s", verbTag);
        } else if
                (word.getToken().endsWith("ied")
                        || word.getToken().endsWith("ched")
                        || word.getToken().endsWith("sed")
                        || word.getToken().endsWith("ated")
                        || word.getToken().endsWith("lved")
                        || word.getToken().endsWith("nced")
                ) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "s", verbTag);
        } else if (word.getToken().length() > 1) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "s", verbTag);
        } else {
            return null;
        }
    }

    public Word pastPToPresP(Word word) {
        String verbTag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("been")) {
            return new Word("being", verbTag);
        } else if (word.getToken().equalsIgnoreCase("had")) {
            return new Word("having", verbTag);
        } else if (word.getToken().equalsIgnoreCase("done")) {
            return new Word("doing", verbTag);
        } else if (word.getToken().equalsIgnoreCase("gone")) {
            return new Word("going", verbTag);
        } else if (word.getToken().equalsIgnoreCase("taken")) {
            return new Word("taking", verbTag);
        } else if (word.getToken().equalsIgnoreCase("left")) {
            return new Word("leaving", verbTag);
        } else if (word.getToken().endsWith("ied")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ying", verbTag);
        } else if (word.getToken().length() > 2) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "ing", verbTag);
        } else {
            return null;
        }
    }

    public Word baseToThirdSing(Word word) {
        String verbTag = tagSet.VERB_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("be")) {
            return new Word("is", verbTag);
        } else if (word.getToken().equalsIgnoreCase("have")) {
            return new Word("has", verbTag);
        } else if (word.getToken().endsWith("y")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ies", verbTag);
        } else if (word.getToken().endsWith("ch") || word.getToken().endsWith("ss") || word.getToken().endsWith("o")) {
            return new Word(word.getToken() + "es", verbTag);
        } else {
            return new Word(word.getToken() + "s", verbTag);
        }
    }

    public Word regularAdjToComparative(Word word) {
        String newTag = tagSet.ADJ_COMP;
        if (word.getToken().endsWith("y")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ier", newTag);
        } else if (word.getToken().endsWith("t") && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken() + "ter", newTag);
        } else if (word.getToken().endsWith("e")) {
            return new Word(word.getToken() + "r", newTag);
        } else {
            return new Word(word.getToken() + "er", newTag);
        }
    }

    public Word regularAdjToSuperlative(Word word) {
        String newTag = tagSet.ADJ_SUP;
        if (word.getToken().endsWith("y")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "iest", newTag);
        } else if (word.getToken().endsWith("t") && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken() + "test", newTag);
        } else if (word.getToken().endsWith("e")) {
            return new Word(word.getToken() + "st", newTag);
        } else {
            return new Word(word.getToken() + "est", newTag);
        }
    }

    public Word comparativeAdjToSuperlative(Word word) {
        String newTag = tagSet.ADJ_SUP;
        if (word.getToken().length() > 0) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "st", newTag);
        } else {
            return null;
        }
    }

    public Word superlativeAdjToComparative(Word word) {
        String newTag = tagSet.ADJ_COMP;
        if (word.getToken().length() > 1) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "r", newTag);
        } else {
            return null;
        }
    }

    //TODO: deal with cases such as "freer" to "free"
    public Word comparativeAdjToRegular(Word word) {
        String newTag = tagSet.ADJ;
        if (word.getToken().endsWith("ier")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "y", newTag);
        } else if (word.getToken().endsWith("tter")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), newTag);
        } else if (word.getToken().endsWith("er")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2), newTag);
        } else {
            return null;
        }
    }

    public Word superlativeAdjToRegular(Word word) {
        String newTag = tagSet.ADJ;
        if (word.getToken().endsWith("iest")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 4) + "y", newTag);
        } else if (word.getToken().endsWith("ttest")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 4), newTag);
        } else if (word.getToken().length() > 2) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), newTag);
        } else {
            return null;
        }
    }

    public Word adverbToAdj(Word word) {
        final String newTag = tagSet.ADJ;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("angrily")) {
            return new Word(token.substring(0, 1) + "ngry", newTag);
        } else if (token.endsWith("xically")) {
            // e.g. lexically -> lexical
            return new Word(token.substring(0, token.length() - 2), newTag);
            //} else if (token.endsWith("cically") || token.endsWith("stemically")) {
        } else if (token.endsWith("ically")) {
            // e.g. catastrophically -> catastrophic
            // e.g. epistemically -> epistemic
            return new Word(token.substring(0, token.length() - 4), newTag);
        } else if (token.endsWith("ably") || token.endsWith("ibly")) {
            // e.g. unsustainably -> unsustainable
            return new Word(token.substring(0, token.length() - 1) + "e", newTag);
            // } else if (token.endsWith("arily") || token.endsWith("dily")) {
        } else if (token.endsWith("ily")) {
            // e.g. extraordinarily -> extraordinary
            // e.g. Steadily -> Steady
            // e.g. derogatorily -> derogatory
            if (token.equalsIgnoreCase("eerily")) {
                return new Word(token.substring(0, 1) + "erie", newTag);
            } else {
                return new Word(token.substring(0, token.length() - 3) + "y", newTag);
            }
        } else if (token.endsWith("bly") || token.endsWith("btly")) {
            // e.g. feebly -> feeble, subtly -> subtle
            return new Word(token.substring(0, token.length() - 1) + "e", newTag);
        } else if (token.endsWith("uly")) {
            // e.g. unduly -> undue
            return new Word(token.substring(0, token.length() - 2) + "e", newTag);
        } else if (token.endsWith("ully")) {
            // e.g. fully -> full, thankfully -> thankful
            return new Word(token.substring(0, token.length() - 1), newTag);
        } else if (token.endsWith("doubly")) {
            // e.g. doubly -> double (?)
            return new Word(token.substring(0, token.length() - 1) + "e", newTag);
        } else if (token.length() > 1) {
            //strip the "ly" from the end of the word
            return new Word(token.substring(0, token.length() - 2), newTag);
        } else {
            return null;
        }
    }

 /*public static void main(String [] args)
  {
	  try
	  {
	  	System.out.println("Testing the version with tags");
	  	Sentence testSentence = new Sentence("This DT is VBZ a DT test NN", true);
	  	SubstWrongFormError substError = new SubstWrongFormError(testSentence,"NN","NNS");
      		System.out.println(substError.insertError());
      		System.out.println();
	  }
	  catch (CannotCreateErrorException c)
	  {
		System.err.println(c.getMessage());
	  }
  }*/


}
