package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Class SubstWrongFormError
 *
 * @author Jennifer Foster
 */
public class SubstWrongFormError extends SubstError {
    private static final String VOWEL = "aeiou";
    private static final String CONSONANT = "bcdfghjklmnpqrstvwxyz";


    private static final Pattern PRES_P_TO_INF_CONSONANT_CONSONANT_ING = Pattern.compile(".*([" + CONSONANT + "])\\1ing");
        /*
    private static final Pattern PRES_P_TO_INF_VOWEL_VOWEL_CONSONANT_ING = Pattern.compile(
            ".*[" + VOWEL + "][" + VOWEL + "][" + CONSONANT + "]ing");
    private static final Pattern PRES_P_TO_INF_MIGHT_END_WITH_E = Pattern.compile(
            ".*(a[cfgklz]|aseat|uat|bl|but|dur|fer|i[knsz]|kl|nc|gl|om|r[sv]|tat|tl||uis|u[st])ing");
    */

    private static final Pattern BASE_TO_PRES_P_VOWEL_VOWEL_M = Pattern.compile("[aeiou][aeuio]m$");

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
        List<Integer> listPOS = new ArrayList<Integer>();
        Word word;
        for (int i = 0; i < newSentence.size(); i++) {
            word = newSentence.getWord(i);
            if (word.getTag().equals(posTag) && posTag.equals(tagSet.INF)) {
                Word nextWord = newSentence.getWord(i + 1);
                if (nextWord != null && nextWord.getTag().equals(tagSet.VERB_BASE)) {
                    listPOS.add(i);
                }
            } else if (word.getTag().equals(posTag)) {
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

        //delete the word which was at this position in the sentence
        Word oldWord = newSentence.getWord(where);
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
            oldWord = newSentence.getWord(where + 1);
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
            List<String> formList = new ArrayList<String>();
            String tokenTag;
            String pos;
            StringTokenizer tokens;
            for (int i = 0; i < extraWords.size(); i++) {
                tokenTag = extraWords.get(i);
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
            String newWordString = formList.get(newWordPos);
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
        String tag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("is")) {
            return new Word("being", tag);
        } else if (word.getToken().equalsIgnoreCase("has")) {
            return new Word("having", tag);
        } else if (word.getToken().endsWith("ies")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ying", tag);
        } else if (word.getToken().endsWith("es")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "ing", tag);
        } else if (word.getToken().endsWith("ts") && word.getToken().length() > 1
                && (ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 3)))
                && !(word.getToken().substring(word.getToken().length() - 4, word.getToken().length() - 2).equals("ea"))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ting", tag);
        } else if (word.getToken().length() > 0) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", tag);
        } else {
            return null;
        }
    }


    public Word nonThirdSingToPresP(Word word) {
        String tag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("are")) {
            return new Word("being", tag);
        } else if (word.getToken().endsWith("e") && !word.getToken().endsWith("ee")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", tag);
        } else if (word.getToken().endsWith("t")
                && (!(word.getToken().substring(word.getToken().length() - 3, word.getToken().length() - 1).equals("ea")))
                && word.getToken().length() > 1
                && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken() + "ting", tag);
        } else {
            return new Word(word.getToken() + "ing", tag);
        }
    }

    public Word pastpToPresp(Word word) {
        String tag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("been")) {
            return new Word("being", tag);
        } else if (word.getToken().equalsIgnoreCase("had")) {
            return new Word("having", tag);
        } else if (word.getToken().equalsIgnoreCase("done")) {
            return new Word("doing", tag);
        } else if (word.getToken().equalsIgnoreCase("gone")) {
            return new Word("going", tag);
        } else if (word.getToken().equalsIgnoreCase("taken")) {
            return new Word("taking", tag);
        } else if (word.getToken().equalsIgnoreCase("left")) {
            return new Word("leaving", tag);
        } else if (word.getToken().endsWith("come")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ing", tag);
        } else if (word.getToken().endsWith("ied")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ying", tag);
        } else if (word.getToken().length() > 1) {
            //ed
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "ing", tag);
        } else {
            return null;
        }
    }

    public Word baseToPresP(Word word) {
        String tag = tagSet.VERB_PRES_PART;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("be")) {
            return new Word("being", tag);
        } else if (token.endsWith("e") && !token.endsWith("ee")) {
            if (token.endsWith("noe")) {
                // e.g. canoe -> canoeing
                return new Word(token + "ing", tag);
            } else if (token.endsWith("ie") && token.length() == 3) {
                // e.g. tie -> tying, die -> dying
                return new Word(token.substring(0, token.length() - 2) + "ying", tag);
            } else {
                // e.g. hope -> hoping
                return new Word(token.substring(0, token.length() - 1) + "ing", tag);
            }
        } else if (token.endsWith("am") || token.endsWith("um")) {
            if (BASE_TO_PRES_P_VOWEL_VOWEL_M.matcher(token).matches()) {
                // e.g. foam -> foaming
                return new Word(token + "ing", tag);
            } else {
                // e.g. dam -> damming, ram -> ramming,
                return new Word(token + "ming", tag);
            }
        } else if (token.endsWith("ise")) {
            // e.g. subsidise -> subsidising
            return new Word(token.substring(0, token.length() - 1) + "ing", tag);
        } else if (token.endsWith("er")) {
            if (token.endsWith("der") || token.endsWith("eer") || token.endsWith("iter") || token.endsWith("ester")) {
                // e.g. engender -> engendering, engineer -> engineering,
                // reconnoiter -> reconnoitering, pester -> pestering
                return new Word(token + "ing", tag);
            } else if (token.endsWith("ier") || token.endsWith("euver")) {
                // e.g. tier -> tiering, manoeuver -> manoeuvering
                return new Word(token + "ing", tag);
            } else {
                // e.g. refer -> referring
                return new Word(token + "ring", tag);
            }
        } else if (token.endsWith("id") || token.endsWith("ed") || token.endsWith("ud")) {
            // e.g. forbid -> forbidding, stud -> studding
            return new Word(token + "ding", tag);
        } else if (token.endsWith("ab") || token.endsWith("ob") || token.endsWith("ub")) {
            // e.g. log -> lobbing, rub -> rubbing, grab -> grabbing
            return new Word(token + "bing", tag);
        } else if (token.endsWith("og") || token.endsWith("ag")) {
            // e.g. log -> logging, lag -> lagging
            return new Word(token + "ging", tag);
        } else if (token.endsWith("ek") || token.endsWith("ic")) {
            // e.g. trek -> trekking, mimic -> mimicking
            return new Word(token + "king", tag);
        } else if (token.endsWith("el")) {
            // e.g. excel -> excelling
            return new Word(token + "ling", tag);
        } else if (token.endsWith("ag") || token.endsWith("eg") || token.endsWith("ig") || token.endsWith("og") || token.endsWith("ug")) {
            // e.g. rig -> rigging, hug -> hugging
            return new Word(token + "ging", tag);
        } else if (token.endsWith("em") || token.endsWith("im")) {
            // e.g. swim -> swimming
            return new Word(token + "ming", tag);
        } else if (token.endsWith("in") && token.length() == 3) {
            // e.g. win -> winning, pin -> pinning
            return new Word(token + "ning", tag);
        } else if (token.endsWith("ap") || token.endsWith("ip") || token.endsWith("up")) {
            // e.g. kidnap -> kidnapping
            return new Word(token + "ping", tag);
        } else if (token.endsWith("ol")) {
            // e.g. control -> controlling
            return new Word(token + "ling", tag);
        } else if (token.endsWith("un")) {
            // e.g. run -> running
            return new Word(token + "ning", tag);
        } else if (token.endsWith("ir") || token.endsWith("ur")) {
            if (token.endsWith("our")) {
                // e.g. favour -> favouring
                return new Word(token + "ing", tag);
            } else {
                // e.g. incur -> incurring, spur -> spurring, stir -> stirring
                return new Word(token + "ring", tag);
            }
        } else if (token.toLowerCase().endsWith("set") || token.toLowerCase().endsWith("fit") ||
                token.toLowerCase().endsWith("cut") || token.toLowerCase().endsWith("hit") ||
                token.toLowerCase().endsWith("put") || token.toLowerCase().endsWith("let")) {
            if (token.endsWith("nefit") || token.endsWith("rofit")) {
                // e.g. benefit -> benefiting, profit -> profiting
                return new Word(token + "ing", tag);
            } else {

                // e.g. set -> setting
                return new Word(token + "ting", tag);
            }
// I'm not going to take the time to understand this block. Five conditions?
//        } else if ((word.getToken().endsWith("t") && word.getToken().length() > 1)
//                && ((word.getToken().length() > 3) && !(word.getToken().substring(word.getToken().length() - 4, word.getToken().length() - 2).equals("ea")))
//                && (ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2)))) {
//            return new Word(word.getToken() + "ting", tag);
        } else {
            return new Word(token + "ing", tag);
        }
    }

    public Word presPToPastP(Word word) {
        String tag = tagSet.VERB_PAST_PART;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("been", tag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("had", tag);
        } else if (word.getToken().endsWith("coming")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "e", tag);
        } else if (word.getToken().equalsIgnoreCase("going")) {
            return new Word("gone", tag);
        } else if (word.getToken().equalsIgnoreCase("doing")) {
            return new Word("done", tag);
        } else if (word.getToken().equalsIgnoreCase("leaving")) {
            return new Word("left", tag);
        } else if (word.getToken().equalsIgnoreCase("taking")) {
            return new Word("taken", tag);
        } else if (word.getToken().equalsIgnoreCase("seeing")) {
            return new Word("seen", tag);
        } else if (word.getToken().equalsIgnoreCase("making")) {
            return new Word("made", tag);
        } else if (word.getToken().equalsIgnoreCase("bringing")) {
            return new Word("brought", tag);
        } else if (word.getToken().equalsIgnoreCase("teaching")) {
            return new Word("taught", tag);
        } else if (word.getToken().equalsIgnoreCase("reading")) {
            return new Word("read", tag);
        } else if (word.getToken().equalsIgnoreCase("letting")) {
            return new Word("let", tag);
        } else if (word.getToken().endsWith("wing") &&
                (word.getToken().charAt(word.getToken().length() - 5) == 'a' ||
                        word.getToken().charAt(word.getToken().length() - 5) == 'o')) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "n", tag);
        } else if (word.getToken().endsWith("ying") && !ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 5))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 4) + "ied", tag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ed", tag);
        } else {
            return null;
        }
    }

    public Word presPToThirdSing(Word word) {
        String tag = tagSet.VERB_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("is", tag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("has", tag);
        } else if (word.getToken().endsWith("ching")
                || word.getToken().endsWith("ssing") || word.getToken().endsWith("oing") || word.getToken().endsWith("dging")
                || word.getToken().endsWith("oting")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "es", tag);
        } else if (word.getToken().endsWith("ying")) {

            return new Word(word.getToken().substring(0, word.getToken().length() - 4) + "ies", tag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "s", tag);
        } else {
            return null;
        }
    }

    public Word presPToNonThirdSing(Word word) {
        String tag = tagSet.VERB_NON_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("being")) {
            return new Word("are", tag);
        } else if (word.getToken().equalsIgnoreCase("having")) {
            return new Word("have", tag);
        } else if (word.getToken().endsWith("ching")
                || word.getToken().endsWith("ssing") || word.getToken().endsWith("oing") || word.getToken().endsWith("dging")
                || word.getToken().endsWith("oting")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "e", tag);
        } else if (word.getToken().endsWith("ing")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), tag);
        } else {
            return null;
        }
    }

    protected boolean infEndsWithE(String token) {
        token = token.toLowerCase();
        return token.endsWith("ancing")    || // e.g. freelancing -> freelance
                token.endsWith("encing")    || // e.g. referencing -> reference
                token.endsWith("incing")    || // e.g. convincing -> convince
                token.endsWith("ouncing")   || // e.g. announcing -> announce
                token.endsWith("acing")     || // e.g. placing -> place
                token.endsWith("licing")    || // e.g. slicing -> slice
                token.endsWith("ourcing")   || // e.g. sourcing -> source
                token.endsWith("ercing")    || // e.g. piercing -> pierce
                token.endsWith("orcing")    || // e.g. divorcing -> divorce
                token.endsWith("scing")     || // e.g. producing -> produce
                token.endsWith("ucing")     || // e.g. convalescing -> convalesce
                token.endsWith("icing")     || // e.g. sacrificing -> sacrifice

                token.endsWith("arauding")  || // e.g. marauding -> maraude
                token.endsWith("cading")    || // e.g. cascading -> cascade
                token.endsWith("rading")    || // e.g. degrading -> degrade
                token.endsWith("uading")    || // e.g. persuading -> persuade
                token.endsWith("vading")    || // e.g. pervading -> pervade
                token.endsWith("eding")     || // e.g. conceding -> concede
                token.endsWith("iding")     || // e.g. deciding -> decide
                token.endsWith("oding")     || // e.g. eroding -> erode

                token.endsWith("caching")   || // e.g. caching -> cache

                token.endsWith("mbling")    || // e.g. rumbling -> rumble
                token.endsWith("abling")    || // e.g. disabling -> disable
                token.endsWith("ipling")    || // e.g. triple -> tripling
                token.endsWith("upling")    || // e.g. coupling -> couple
                token.endsWith("ppling")    || // e.g. rippling -> ripple
                token.endsWith("haling")    || // e.g. whaling -> whale
                token.endsWith("cling")     || // e.g. circling -> circle
                token.endsWith("ggling")    || // e.g. wiggling -> wiggle
                token.endsWith("ngling")    || // e.g. singling -> single
                token.endsWith("ogling")    || // e.g. ogling -> ogle
                token.endsWith("fling")     || // e.g. stifling -> stifle
                token.endsWith("ckling")    || // e.g. tickling -> tickle
                token.endsWith("inkling")   || // e.g. wrinkling -> wrinkle
                token.endsWith("ntling")    || // e.g. dismantling -> dismantle
                token.endsWith("stling")    || // e.g. wrestling -> wrestle
                token.endsWith("ttling")    || // e.g. settling -> settle
                token.endsWith("zzling")    || // e.g. embezzling -> embezzle
                token.endsWith("bling")     || // e.g. troubling -> trouble (overrides next rule)
                token.endsWith("bbling")    || // e.g. babbling -> babble
                token.endsWith("dling")     || // e.g. waddling -> waddle
                token.endsWith("aling")     || // e.g. impaling -> impale
                token.endsWith("iling")     || // e.g. filing -> file
                token.endsWith("soling")    || // e.g. consoling -> console
                token.endsWith("itling")    || // e.g. titling -> title
                token.endsWith("istling")   || // e.g. whistling -> whistle
                token.endsWith("ycling")    || // e.g. cycling -> cycle
                token.endsWith("caling")    || // e.g. scaling -> scale
                token.endsWith("culing")    || // e.g. ridiculing -> ridicule
                token.endsWith("duling")    || // e.g. scheduling -> schedule
                token.endsWith("ruling")    || // e.g. ruling -> rule
                token.endsWith("yling")     || // e.g. styling -> style

                token.endsWith("aging")     || // e.g. managing -> manage
                token.endsWith("ieging")     || // e.g. besieging -> besiege
                token.endsWith("leging")     || // e.g. alleging -> allege
                token.endsWith("arging")    || // e.g. charging -> charge
                token.endsWith("erging")    || // e.g. emerging -> emerge
                token.endsWith("ulging")    || // e.g. indulging -> indulge
                token.endsWith("urging")    || // e.g. puring -> purge
                token.endsWith("uging")     || // e.g. gouging -> gouge

                token.endsWith("casing")    || // e.g. truecasing -> truecase
                token.endsWith("chasing")   || // e.g. purchasing -> purchase
                token.endsWith("ising")     || // e.g. reorganising -> reorganise
                token.endsWith("ensing")    || // e.g. condensing -> condense
                token.endsWith("ersing")    || // e.g. traversing -> traverse
                token.endsWith("ursing")    || // e.g. coursing -> course
                token.endsWith("oosing")    || // e.g. choosing -> choose
                token.endsWith("using")     || // e.g. reusing -> reuse
                token.endsWith("posing")    || // e.g. supposing -> suppose
                token.endsWith("osing")     || // e.g. closing -> close
                token.endsWith("psing")     || // e.g. collapsing -> collapse
                token.endsWith("ysing")     || // e.g. catalysing -> catalyse

                token.endsWith("cating")    || // e.g. reciprocating -> reciprocate
                token.endsWith("nciting")    || // e.g. inciting -> incite
                token.endsWith("xciting")    || // e.g. exciting -> excite
                token.endsWith("ctating")   || // e.g. nictating -> nictate
                token.endsWith("aseating")  || // e.g. caseating -> caseate
                token.endsWith("leting")    || // e.g. completing -> complete
                token.endsWith("peting")    || // e.g. competing -> compete
                token.endsWith("writing")   || // e.g. writing -> write
                token.endsWith("buting")    || // e.g. attributing -> attribute
                token.endsWith("iluting")   || // e.g. diluting -> dilute
                token.endsWith("tuting")    || // e.g. substituting -> substitute
                token.endsWith("iating")    || // e.g. obviating -> obviate
                token.endsWith("nating")    || // e.g. designating -> designate
                token.endsWith("erating")   || // e.g. operating -> operate
                token.endsWith("uating")    || // e.g. evaluating -> evaluate
                token.endsWith("uting")     || // e.g. diluting -> dilute

                token.endsWith("coming")    || // e.g. unbecoming -> unbecome
                token.endsWith("afing")     || // e.g. strafing -> strafe
                token.endsWith("iking")     || // e.g. hiking -> hike
                token.endsWith("oking")     || // e.g. invoking -> invoke
                token.endsWith("uking")     || // e.g. rebuking -> rebuke
                token.endsWith("making")    || // e.g. making -> make
                token.endsWith("taking")    || // e.g. taking -> take

                token.endsWith("aming")     || // e.g. flaming -> flame

                token.endsWith("azing")     || // e.g. gazing -> gaze
                token.endsWith("fering")    || // e.g. interfering -> interfere
                token.endsWith("juring")    || // e.g. injuring -> injure
                token.endsWith("turing")    || // e.g. manufacturing -> manufacture
                token.endsWith("ntring")    || // e.g. centring -> centre
                token.endsWith("arving")    || // e.g. carving -> carve

                token.endsWith("bining")    || // e.g. combining -> combine
                token.endsWith("lining")    || // e.g. lining -> line
                token.endsWith("gining")    || // e.g. imagining -> imagine
                token.endsWith("pining")    || // e.g. opining -> opine
                token.endsWith("fining")    || // e.g. defining -> define
                token.endsWith("mining")    || // e.g. examining -> examine
                token.endsWith("twining")   || // e.g. intertwining -> intertwine
                token.endsWith("aning")     || // e.g. laning -> lane
                token.endsWith("vening")    || // e.g. intervening -> intervene
                token.endsWith("doning")    || // e.g. condoning -> condone
                token.endsWith("honing")    || // e.g. phoning -> phone
                token.endsWith("roning")    || // e.g. dethroning -> dethrone
                token.endsWith("zoning")    || // e.g. zoning -> zone

                token.endsWith("aving")     || // e.g. saving -> save
                token.endsWith("hiding")    || // e.g. chiding -> chide
                token.endsWith("uming")     || // e.g. assuming -> assume
                token.endsWith("lving")     || // e.g. halving -> halve

                token.endsWith("iving")     || // e.g. receiving -> receive
                token.endsWith("izing")     || // e.g. dualizing -> dualize
                token.endsWith("siding")    || // e.g. subsiding -> subside
                token.endsWith("uiding")    || // e.g. guiding -> guide
                token.endsWith("ribing")    || // e.g. proscribing -> proscribe

                token.endsWith("iping")     || // e.g. wiping -> wipe
                token.endsWith("oping")     || // e.g. eloping -> elope
                token.endsWith("yping")     || // e.g. genotyping -> genotype
                token.endsWith("caping")    || // e.g. escaping -> escape

                token.endsWith("cuing")     || // e.g. rescuing -> rescue
                token.endsWith("duing")     || // e.g. subduing -> subdue
                token.endsWith("guing")     || // e.g. arguing -> argue
                token.endsWith("aluing")    || // e.g. valuing -> value
                token.endsWith("inuing")    || // e.g. continuing -> continue
                token.endsWith("quing")     || // e.g. critiquing -> critique
                token.endsWith("suing"); //    || // e.g. ensuing -> ensue

        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
    }

    protected boolean convertCkToC(String token) {
        token = token.toLowerCase();
        return token.endsWith("icking");  // e.g. panicking -> panic
    }

    public Word presPToInf(Word word) {
        final String tag = tagSet.VERB_NON_THIRD_SING;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("being")) {
            return new Word("be", tag);
        } else if (convertCkToC(token)) {
            return new Word(token.substring(0, token.length() - 4), tag);
        } else if (token.equalsIgnoreCase("lying")) {
            return new Word(token.substring(0, token.length() - 4) + "ie", tag);
        } else if (infEndsWithE(token)) {
            //System.out.println(token + " matches[1] infEndsWithE");
            return new Word(token.substring(0, token.length() - 3) + "e", tag);
        } else if (PRES_P_TO_INF_CONSONANT_CONSONANT_ING.matcher(token).matches()) {
            //System.out.println(token + " matches[2] + " + PRES_P_TO_INF_CONSONANT_CONSONANT_ING);
            if (token.endsWith("ssing") || token.endsWith("zzing") || token.endsWith("spelling") ||
                    token.endsWith("stalling") || token.endsWith("selling") || token.endsWith("welling") ||
                    token.endsWith("cotting") || token.endsWith("affing")) {
                return new Word(token.substring(0, token.length() - 3), tag);
            } else {
                return new Word(token.substring(0, token.length() - 4), tag);
            }
        } else if (token.endsWith("ing")) {
            return new Word(token.substring(0, token.length() - 3), tag);
        } else {
            return null;
        }
    }

    public Word pastPToThirdSing(Word word) {
        String tag = tagSet.VERB_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("been")) {
            return new Word("is", tag);
        } else if (word.getToken().equalsIgnoreCase("had")) {
            return new Word("has", tag);
        } else if (word.getToken().equalsIgnoreCase("done")) {
            return new Word("does", tag);
        } else if (word.getToken().equalsIgnoreCase("gone")) {
            return new Word("goes", tag);
        } else if (word.getToken().equalsIgnoreCase("taken")) {
            return new Word("takes", tag);
        } else if (word.getToken().equalsIgnoreCase("left")) {
            return new Word("leaves", tag);
        } else if (word.getToken().length() > 5 && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 4))) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "s", tag);
        } else if
                (word.getToken().endsWith("ied")
                        || word.getToken().endsWith("ched")
                        || word.getToken().endsWith("sed")
                        || word.getToken().endsWith("ated")
                        || word.getToken().endsWith("lved")
                        || word.getToken().endsWith("nced")
                ) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "s", tag);
        } else if (word.getToken().length() > 1) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "s", tag);
        } else {
            return null;
        }
    }

    public Word pastPToPresP(Word word) {
        String tag = tagSet.VERB_PRES_PART;
        if (word.getToken().equalsIgnoreCase("been")) {
            return new Word("being", tag);
        } else if (word.getToken().equalsIgnoreCase("had")) {
            return new Word("having", tag);
        } else if (word.getToken().equalsIgnoreCase("done")) {
            return new Word("doing", tag);
        } else if (word.getToken().equalsIgnoreCase("gone")) {
            return new Word("going", tag);
        } else if (word.getToken().equalsIgnoreCase("taken")) {
            return new Word("taking", tag);
        } else if (word.getToken().equalsIgnoreCase("left")) {
            return new Word("leaving", tag);
        } else if (word.getToken().endsWith("ied")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "ying", tag);
        } else if (word.getToken().length() > 2) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "ing", tag);
        } else {
            return null;
        }
    }

    public Word baseToThirdSing(Word word) {
        String tag = tagSet.VERB_THIRD_SING;
        if (word.getToken().equalsIgnoreCase("be")) {
            return new Word("is", tag);
        } else if (word.getToken().equalsIgnoreCase("have")) {
            return new Word("has", tag);
        } else if (word.getToken().endsWith("y")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ies", tag);
        } else if (word.getToken().endsWith("ch") || word.getToken().endsWith("ss") || word.getToken().endsWith("o")) {
            return new Word(word.getToken() + "es", tag);
        } else {
            return new Word(word.getToken() + "s", tag);
        }
    }

    public Word regularAdjToComparative(Word word) {
        String tag = tagSet.ADJ_COMP;
        if (word.getToken().endsWith("y")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "ier", tag);
        } else if (word.getToken().endsWith("t") && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken() + "ter", tag);
        } else if (word.getToken().endsWith("e")) {
            return new Word(word.getToken() + "r", tag);
        } else {
            return new Word(word.getToken() + "er", tag);
        }
    }

    public Word regularAdjToSuperlative(Word word) {
        String tag = tagSet.ADJ_SUP;
        if (word.getToken().endsWith("y")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "iest", tag);
        } else if (word.getToken().endsWith("t") && ErrorUtilities.isVowel(word.getToken().charAt(word.getToken().length() - 2))) {
            return new Word(word.getToken() + "test", tag);
        } else if (word.getToken().endsWith("e")) {
            return new Word(word.getToken() + "st", tag);
        } else {
            return new Word(word.getToken() + "est", tag);
        }
    }

    public Word comparativeAdjToSuperlative(Word word) {
        String tag = tagSet.ADJ_SUP;
        if (word.getToken().length() > 0) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 1) + "st", tag);
        } else {
            return null;
        }
    }

    public Word superlativeAdjToComparative(Word word) {
        String tag = tagSet.ADJ_COMP;
        if (word.getToken().length() > 1) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2) + "r", tag);
        } else {
            return null;
        }
    }

    //TODO: deal with cases such as "freer" to "free"
    public Word comparativeAdjToRegular(Word word) {
        String tag = tagSet.ADJ;
        if (word.getToken().endsWith("ier")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3) + "y", tag);
        } else if (word.getToken().endsWith("tter")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), tag);
        } else if (word.getToken().endsWith("er")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 2), tag);
        } else {
            return null;
        }
    }

    public Word superlativeAdjToRegular(Word word) {
        String tag = tagSet.ADJ;
        if (word.getToken().endsWith("iest")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 4) + "y", tag);
        } else if (word.getToken().endsWith("ttest")) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 4), tag);
        } else if (word.getToken().length() > 2) {
            return new Word(word.getToken().substring(0, word.getToken().length() - 3), tag);
        } else {
            return null;
        }
    }

    public Word adverbToAdj(Word word) {
        final String tag = tagSet.ADJ;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("angrily")) {
            return new Word(token.substring(0, 1) + "ngry", tag);
        } else if (token.endsWith("xically")) {
            // e.g. lexically -> lexical
            return new Word(token.substring(0, token.length() - 2), tag);
            //} else if (token.endsWith("cically") || token.endsWith("stemically")) {
        } else if (token.endsWith("ically")) {
            // e.g. catastrophically -> catastrophic
            // e.g. epistemically -> epistemic
            return new Word(token.substring(0, token.length() - 4), tag);
        } else if (token.endsWith("ably") || token.endsWith("ibly")) {
            // e.g. unsustainably -> unsustainable
            return new Word(token.substring(0, token.length() - 1) + "e", tag);
            // } else if (token.endsWith("arily") || token.endsWith("dily")) {
        } else if (token.endsWith("ily")) {
            // e.g. extraordinarily -> extraordinary
            // e.g. Steadily -> Steady
            // e.g. derogatorily -> derogatory
            if (token.equalsIgnoreCase("eerily")) {
                return new Word(token.substring(0, 1) + "erie", tag);
            } else {
                return new Word(token.substring(0, token.length() - 3) + "y", tag);
            }
        } else if (token.endsWith("bly") || token.endsWith("btly")) {
            // e.g. feebly -> feeble, subtly -> subtle
            return new Word(token.substring(0, token.length() - 1) + "e", tag);
        } else if (token.endsWith("uly")) {
            // e.g. unduly -> undue
            return new Word(token.substring(0, token.length() - 2) + "e", tag);
        } else if (token.endsWith("ully")) {
            // e.g. fully -> full, thankfully -> thankful
            return new Word(token.substring(0, token.length() - 1), tag);
        } else if (token.endsWith("doubly")) {
            // e.g. doubly -> double (?)
            return new Word(token.substring(0, token.length() - 1) + "e", tag);
        } else if (token.length() > 1) {
            //strip the "ly" from the end of the word
            return new Word(token.substring(0, token.length() - 2), tag);
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
