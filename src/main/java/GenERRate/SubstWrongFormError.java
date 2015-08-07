package GenERRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class SubstWrongFormError
 *
 * @author Jennifer Foster
 */
public class SubstWrongFormError extends SubstError {
    private static final String VOWEL = "aeiou";
    private static final String CONSONANT = "bcdfghjklmnpqrstvwxyz";
    private static final String PUNCTUATION = "!\"#$%&'()*+,./:;<=>?@[]^_`{|}~";
    private static final Pattern CONSONANT_CONSONANT_ING = Pattern.compile(".*([" + CONSONANT + "])\\1ing");
    private static final Pattern VOWEL_VOWEL_M = Pattern.compile(".*[aeiou][aeuio]m$");
    private static final Pattern CONSONANT_CONSONANT_ED = Pattern.compile(".*([" + CONSONANT + "])\\1ed");
    private static final Pattern VOWEL_CONSONANT = Pattern.compile("(.*[" + VOWEL + "])([" + CONSONANT + "])");
    private static final Pattern CONSONANT_E = Pattern.compile(".*[" + CONSONANT + "]e$");

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
    private final String form;

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
            newWord = thirdSingularToNonThirdSingular(oldWord);
        } else if ((posTag.equals(tagSet.VERB_NON_THIRD_SING)) && (form.equals(tagSet.VERB_THIRD_SING))) {
            newWord = nonThirdSingularToThirdSingular(oldWord);
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
        final String singular_noun = tagSet.SINGULAR_NOUN;
        final String token = word.getToken();

        if (token.endsWith("ies")) {
            return new Word(token.substring(0, token.length() - 3) + "y", singular_noun);
        } else if (token.endsWith("men")) {
            return new Word(token.substring(0, token.length() - 3) + "man", singular_noun);
        } else if (token.endsWith("a")) {
            return new Word(token.substring(0, token.length() - 1) + "um", singular_noun);
        } else if (token.endsWith("ches") || token.endsWith("sses") || token.endsWith("zes") || token.endsWith("shes") || token.endsWith("xes")) {
            return new Word(token.substring(0, token.length() - 2), singular_noun);
        } else if (token.length() > 0) {
            return new Word(token.substring(0, token.length() - 1), singular_noun);
        } else {
            return null;
        }
    }

    public Word makeNounPlural(Word word) {
        final String plural_noun = tagSet.PLURAL_NOUN;
        final String token = word.getToken();

        if (token.endsWith("man")) {
            return new Word(token.substring(0, token.length() - 3) + "men", plural_noun);
        } else if (token.endsWith("ch") || token.endsWith("s") || token.endsWith("z") || token.endsWith("sh") || token.endsWith("x")) {
            return new Word(token + "es", plural_noun);
        } else if (token.endsWith("y") && token.length() > 1 && !ErrorUtilities.isVowel(token.charAt(token.length() - 2))) {
            return new Word(token.substring(0, token.length() - 1) + "ies", plural_noun);
        } else if (token.endsWith("um")) {
            return new Word(token.substring(0, token.length() - 2) + "a", plural_noun);
        } else {
            return new Word(token + "s", plural_noun);
        }
    }

    public Word nonThirdSingularToThirdSingular(Word word) {
        final String tag = tagSet.VERB_THIRD_SING;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("are") || token.equalsIgnoreCase("'re") || token.equalsIgnoreCase("'m") || token.equalsIgnoreCase("am")) {
            return new Word("is", tag);
        } else if (token.equalsIgnoreCase("have")) {
            return new Word("has", tag);
        } else if (token.equalsIgnoreCase("do")) {
            return new Word("does", tag);
        } else if (token.equalsIgnoreCase("go")) {
            return new Word("goes", tag);
        } else if (token.equalsIgnoreCase("shall")) {
            return new Word(token, tag, token);
        } else if (token.endsWith("y") && token.length() > 1 && !ErrorUtilities.isVowel(token.charAt(token.length() - 2))) {
            return new Word(token.substring(0, token.length() - 1) + "ies", tag);
        } else if (token.endsWith("ch") || token.endsWith("x") || token.endsWith("s") || token.endsWith("z") || token.endsWith("sh")) {
            return new Word(token + "es", tag);
        } else {
            return new Word(token + "s", tag);
        }
    }

    public Word thirdSingularToNonThirdSingular(Word word) {
        final String tag = tagSet.VERB_NON_THIRD_SING;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("is") || token.equalsIgnoreCase("'s")) {
            return new Word("are", tag);
        } else if (token.equalsIgnoreCase("has")) {
            return new Word("have", tag);
        } else if (token.equalsIgnoreCase("does")) {
            return new Word("do", tag);
        } else if (token.equalsIgnoreCase("goes")) {
            return new Word("go", tag);
        } else if (token.equalsIgnoreCase("shall")) {
            return new Word(token, tag, token);
        } else if (token.endsWith("ies")) {
            return new Word(token.substring(0, token.length() - 2) + "y", tag);
        } else if (token.endsWith("sses") || token.endsWith("ches") || token.endsWith("xes") || token.endsWith("zzes") || token.endsWith("shes")) {
            return new Word(token.substring(0, token.length() - 2), tag);
        } else if (token.length() > 0) {
            return new Word(token.substring(0, token.length() - 1), tag);
        } else {
            return null;
        }
    }

    protected boolean baseConvertCToCk(String token) {
        token = token.toLowerCase();
        return token.endsWith("anic") || token.endsWith("imic");
    }

    public Word thirdSingToBase(Word word) {
        final String tag = tagSet.VERB_BASE;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("is")) {
            return new Word("be", tag);
        } else if (token.equalsIgnoreCase("has")) {
            return new Word("have", tag);
        } else if (token.endsWith("ies")) {
            return new Word(token.substring(0, token.length() - 3) + "y", tag);
        } else if (token.equalsIgnoreCase("begat")) {
            return new Word(token.substring(0, token.length() - 2) + "et", tag, token);
        } else if (token.endsWith("yes") || token.endsWith("ys")) {
            // e.g. eyes -> eye, relays -> relay
            return new Word(token.substring(0, token.length() - 1), tag);
        } else if (token.endsWith("es")) {
            return new Word(token.substring(0, token.length() - 1), tag);
            //} else if (thirdSingToPresPAddTING(token)) {
            //    return new Word(token.substring(0, token.length() - 1) + "ting", tag);
            //} else if (thirdSingToPresPDuplicateFinalConsonant(token, matcher)) {
//            MatchResult result = matcher.toMatchResult();
//            String newToken = result.group(1) + result.group(2) + result.group(2) + "ing";
//            return new Word(newToken, tag, token);
        } else if (token.length() > 0) {
            return new Word(token.substring(0, token.length() - 1), tag);
        } else {
            return null;
        }

    }

    protected boolean thirdSingToPresPDuplicateFinalConsonant(String token, Matcher matcher) {
        return matcher.matches() &&
                !token.endsWith("in") && // e.g. obtain -> obtaining

                !token.endsWith("eep") && // e.g. keep -> keeping
                !token.endsWith("oop") && // e.g. stoop -> stooping
                !token.endsWith("lop") && // e.g. develop -> developing

                !token.endsWith("ear") && // e.g. appear -> appearing
                !token.endsWith("air") && // e.g. chair -> chairing
                !token.endsWith("er") && // e.g. offer -> offering
                !token.endsWith("bor") && // e.g. harbor -> harboring
                !token.endsWith("our") && // e.g. harbour -> harbouring

                !token.endsWith("at") && // e.g. eat -> eating
                !token.endsWith("ret") && // e.g. interpret -> interpreting
                !token.endsWith("et") && // e.g. target -> targeting, market -> marketing
                !token.endsWith("ait") && // e.g. await -> awaiting
                !token.endsWith("bit") && // e.g. orbit -> orbiting
                !token.endsWith("cit") && // e.g. elicit -> eliciting
                !token.endsWith("dit") && // e.g. edit -> editing
                !token.endsWith("eit") && // e.g. forfeit -> forfeiting
                !token.endsWith("fit") && // e.g. benefit -> benefiting
                !token.endsWith("imit") && // e.g. limit -> limiting
                !token.endsWith("oit") && // e.g. exploit -> exploiting
                !token.endsWith("rit") && // e.g. inherit -> inheriting
                !token.endsWith("isit") && // e.g. revisit -> revisiting
                !token.endsWith("osit") && // e.g. posit -> positing
                !token.endsWith("uit") && // e.g. recruit -> recruiting
                !token.endsWith("ot") && // e.g. pivot -> pivoting
                !token.endsWith("ut"); // e.g. shout -> shouting
    }

    public Word thirdSingToPresP(Word word) {
        final String tag = tagSet.VERB_PRES_PART;
        final Word base = thirdSingToBase(word);
        final String token = base.getToken();
        final Matcher vcMatcher = VOWEL_CONSONANT.matcher(token);
        final Matcher ceMatcher = CONSONANT_E.matcher(token);

        System.out.println(word.getToken() + " => " + token);

        if (token.equalsIgnoreCase("be")) {
            return new Word("being", tag);
        } else if (token.equalsIgnoreCase("have")) {
            return new Word("having", tag);
        } else if (token.equalsIgnoreCase("beget")) {
            return new Word("begetting", tag, token);
        } else if (token.endsWith("ye") || token.endsWith("y")) {
            System.out.println("append ing");
            return new Word(token.substring(0, token.length()) + "ing", tag);
        } else if (baseConvertCToCk(token)) {
            return new Word(token + "king", tag, token);
        } else if (ceMatcher.matches()) {
            return new Word(token.substring(0, token.length() - 1) + "ing", tag);
//        } else if (thirdSingToPresPAddTING(token)) {
//            System.out.println("append ting");
//            return new Word(token.substring(0, token.length()) + "ting", tag);
        } else if (thirdSingToPresPDuplicateFinalConsonant(token, vcMatcher)) {
            System.out.println("append TRAILING CONSONANT ing");
            MatchResult result = vcMatcher.toMatchResult();
            String newToken = result.group(1) + result.group(2) + result.group(2) + "ing";
            return new Word(newToken, tag, token);
        } else if (token.length() > 0) {
            System.out.println("DEFAULT: append ing");
            return new Word(token.substring(0, token.length()) + "ing", tag);
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
            if (VOWEL_VOWEL_M.matcher(token).matches()) {
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
        final String tag = tagSet.VERB_THIRD_SING;
        final Word infinitive = presPToInf(word);

        if (infinitive == null) {
            return infinitive;
        }

        return baseToThirdSing(infinitive);
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
        return token.endsWith("ribing") || // e.g. proscribing -> proscribe
                token.endsWith("robing") || // e.g. probing -> probe
                token.endsWith("ubing") || // e.g. tubing -> tube

                token.endsWith("ancing") || // e.g. freelancing -> freelance
                token.endsWith("eecing") || // e.g. fleecing -> fleece
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
                token.endsWith("ceding") || // e.g. conceding -> concede
                token.endsWith("peding") || // e.g. impeding -> impede
                token.endsWith("seding") || // e.g. superseding -> supersede
                token.endsWith("iding")     || // e.g. deciding -> decide
                token.endsWith("oding")     || // e.g. eroding -> erode
                token.endsWith("luding") || // e.g. including -> include

                token.endsWith("caching") || // e.g. caching -> cache

                token.endsWith("biling") || // e.g. snowmobiling -> snowmobile
                token.equalsIgnoreCase("taling") || // e.g. taling -> tale
                token.endsWith("mbling")    || // e.g. rumbling -> rumble
                token.endsWith("abling")    || // e.g. disabling -> disable
                token.endsWith("ipling") || // e.g. tripling -> triple
                token.endsWith("mpling") || // e.g. trampling -> trample
                token.endsWith("upling")    || // e.g. coupling -> couple
                token.endsWith("ppling")    || // e.g. rippling -> ripple
                token.endsWith("haling")    || // e.g. whaling -> whale
                token.endsWith("piling") || // e.g. compiling -> compile
                token.endsWith("cling")     || // e.g. circling -> circle
                token.endsWith("ggling")    || // e.g. wiggling -> wiggle
                token.endsWith("ngling")    || // e.g. singling -> single
                token.endsWith("ogling")    || // e.g. ogling -> ogle
                token.endsWith("fling")     || // e.g. stifling -> stifle
                token.endsWith("ckling")    || // e.g. tickling -> tickle
                token.endsWith("inkling")   || // e.g. wrinkling -> wrinkle
                token.endsWith("ntling")    || // e.g. dismantling -> dismantle
                token.endsWith("rtling") || // e.g. wrestling -> wrestle
                token.endsWith("stling") || // e.g. hurtling -> hurtle
                token.endsWith("ttling")    || // e.g. settling -> settle
                token.endsWith("zzling")    || // e.g. embezzling -> embezzle
                token.endsWith("bling")     || // e.g. troubling -> trouble (overrides next rule)
                token.endsWith("bbling")    || // e.g. babbling -> babble
                token.endsWith("dling")     || // e.g. waddling -> waddle
                token.endsWith("paling") || // e.g. impaling -> impale
                token.endsWith("ciling") || // e.g. reconciling -> reconcile
                token.endsWith("filing") || // e.g. filing -> file
                token.endsWith("miling") || // e.g. smiling -> smile
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
                token.endsWith("orging") || // e.g. forging -> forge
                token.endsWith("ulging")    || // e.g. indulging -> indulge
                token.endsWith("urging")    || // e.g. puring -> purge
                token.endsWith("uging")     || // e.g. gouging -> gouge

                token.endsWith("rsing") || // e.g. parsing -> parse
                token.endsWith("casing")    || // e.g. truecasing -> truecase
                token.endsWith("chasing")   || // e.g. purchasing -> purchase
                token.endsWith("ising")     || // e.g. reorganising -> reorganise
                token.endsWith("eansing") || // e.g. cleansing -> cleanse
                token.endsWith("ensing")    || // e.g. condensing -> condense
                token.endsWith("earsing") || // e.g. rehearsing -> rehearse
                token.endsWith("easing") || // e.g. releasing -> release
                token.endsWith("ersing")    || // e.g. traversing -> traverse
                token.endsWith("ursing")    || // e.g. coursing -> course
                token.endsWith("ulsing") || // e.g. pulsing -> pulse
                token.endsWith("oosing")    || // e.g. choosing -> choose
                token.endsWith("orsing") || // e.g. endoring -> endorse
                token.endsWith("using")     || // e.g. reusing -> reuse
                token.endsWith("posing")    || // e.g. supposing -> suppose
                token.endsWith("osing")     || // e.g. closing -> close
                token.endsWith("psing")     || // e.g. collapsing -> collapse
                token.endsWith("ysing")     || // e.g. catalysing -> catalyse

                token.endsWith("cating")    || // e.g. reciprocating -> reciprocate
                token.endsWith("dating") || // e.g. predating -> predate
                token.endsWith("rating") || // e.g. rating -> rate
                token.endsWith("creating") || // e.g. creating -> create
                token.endsWith("kating") || // e.g. skating -> skate
                token.endsWith("nciting") || // e.g. inciting -> incite
                token.endsWith("xciting") || // e.g. exciting -> excite
                token.endsWith("ctating")   || // e.g. nictating -> nictate
                token.endsWith("aseating")  || // e.g. caseating -> caseate
                token.endsWith("leting")    || // e.g. completing -> complete
                token.endsWith("peting")    || // e.g. competing -> compete
                token.endsWith("niting") || // e.g. uniting -> unite
                token.endsWith("writing")   || // e.g. writing -> write
                token.endsWith("buting")    || // e.g. attributing -> attribute
                token.endsWith("iluting")   || // e.g. diluting -> dilute
                token.endsWith("tuting")    || // e.g. substituting -> substitute
                token.endsWith("iating")    || // e.g. obviating -> obviate
                token.endsWith("nating")    || // e.g. designating -> designate
                token.endsWith("erating")   || // e.g. operating -> operate
                token.endsWith("gating") || // e.g. segregating -> segregate
                token.endsWith("wasting") || // e.g. wasting -> waste
                token.endsWith("uating")    || // e.g. evaluating -> evaluate
                token.endsWith("ulating") || // e.g. articulating -> articulate
                token.endsWith("uting")     || // e.g. diluting -> dilute

                token.endsWith("afing")     || // e.g. strafing -> strafe
                token.endsWith("iking")     || // e.g. hiking -> hike
                token.endsWith("oking")     || // e.g. invoking -> invoke
                token.endsWith("uking")     || // e.g. rebuking -> rebuke
                token.endsWith("making")    || // e.g. making -> make
                token.endsWith("taking")    || // e.g. taking -> take

                token.endsWith("uming") || // e.g. assuming -> assume
                token.endsWith("coming") || // e.g. unbecoming -> unbecome
                token.endsWith("aming")     || // e.g. flaming -> flame
                token.endsWith("iming") || // e.g. chiming -> chime

                token.endsWith("azing")     || // e.g. gazing -> gaze
                token.endsWith("uiring") || // e.g. enquiring -> enquire
                token.endsWith("faring") || // e.g. seafaring -> seafare
                token.endsWith("fering")    || // e.g. interfering -> interfere
                token.endsWith("rsevering") || // e.g. persevering -> persevere
                token.endsWith("juring")    || // e.g. injuring -> injure
                token.endsWith("suring") || // e.g. reinsuring -> reinsure
                token.endsWith("turing")    || // e.g. manufacturing -> manufacture
                token.endsWith("ntring")    || // e.g. centring -> centre
                token.endsWith("arving")    || // e.g. carving -> carve

                token.endsWith("bining")    || // e.g. combining -> combine
                token.endsWith("lining")    || // e.g. lining -> line
                token.endsWith("gining")    || // e.g. imagining -> imagine
                token.endsWith("hining") || // e.g. shining -> shine
                token.endsWith("pining")    || // e.g. opining -> opine
                token.endsWith("fining")    || // e.g. defining -> define
                token.endsWith("mining")    || // e.g. examining -> examine
                token.endsWith("twining")   || // e.g. intertwining -> intertwine
                token.endsWith("vining") || // e.g. divining -> diving
                token.endsWith("aning")     || // e.g. laning -> lane
                token.endsWith("vening")    || // e.g. intervening -> intervene
                token.endsWith("boning") || // e.g. boning -> bones
                token.endsWith("doning")    || // e.g. condoning -> condone
                token.endsWith("honing")    || // e.g. phoning -> phone
                token.endsWith("loning") || // e.g. cloning -> clone
                token.endsWith("poning") || // e.g. postponing -> postpone
                token.endsWith("roning")    || // e.g. dethroning -> dethrone
                token.endsWith("toning") || // e.g. intoning -> intone
                token.endsWith("zoning")    || // e.g. zoning -> zone

                token.endsWith("aving")     || // e.g. saving -> save
                token.endsWith("hiding")    || // e.g. chiding -> chide

                token.endsWith("lving")     || // e.g. halving -> halve

                token.endsWith("iving")     || // e.g. receiving -> receive
                token.endsWith("izing")     || // e.g. dualizing -> dualize
                token.endsWith("siding")    || // e.g. subsiding -> subside
                token.endsWith("uiding")    || // e.g. guiding -> guide

                token.endsWith("iping")     || // e.g. wiping -> wipe
                token.endsWith("aping") || // e.g. taping -> tape
                token.endsWith("oping")     || // e.g. eloping -> elope
                token.endsWith("yping")     || // e.g. genotyping -> genotype
                token.endsWith("caping")    || // e.g. escaping -> escape
                token.endsWith("haping") || // e.g. reshaping -> reshapes

                token.endsWith("buing") || // e.g. imbuing -> imbue
                token.endsWith("cuing")     || // e.g. rescuing -> rescue
                token.endsWith("duing")     || // e.g. subduing -> subdue
                token.endsWith("euing") || // e.g. queueing -> queue
                token.endsWith("guing")     || // e.g. arguing -> argue
                token.endsWith("aluing")    || // e.g. valuing -> value
                token.endsWith("inuing")    || // e.g. continuing -> continue
                token.endsWith("quing")     || // e.g. critiquing -> critique
                token.endsWith("suing") || // e.g. ensuing -> ensue

                token.endsWith("owsing") || // e.g. browsing -> browse
                token.equalsIgnoreCase("owing"); //|| // e.g. owing -> owe
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
        // token.endsWith("Xing"); //|| // e.g. Xing -> X
    }

    protected boolean presPToInfConvertCkToC(String token) {
        token = token.toLowerCase();
        return token.endsWith("icking");  // e.g. panicking -> panic
    }

    public Word presPToInf(Word word) {
        final String tag = tagSet.VERB_NON_THIRD_SING;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("being")) {
            return new Word("be", tag);
        } else if (presPToInfConvertCkToC(token)) {
            return new Word(token.substring(0, token.length() - 4), tag);
        } else if (token.equalsIgnoreCase("lying")) {
            return new Word(token.substring(0, token.length() - 4) + "ie", tag);
        } else if (infEndsWithE(token)) {
            System.out.println(token + " matches[1] infEndsWithE");
            return new Word(token.substring(0, token.length() - 3) + "e", tag);
        } else if (CONSONANT_CONSONANT_ING.matcher(token).matches()) {
            System.out.println(token + " matches[2] + " + CONSONANT_CONSONANT_ING);
            if (token.endsWith("ssing") || token.endsWith("zzing") || token.endsWith("spelling") ||
                    token.endsWith("stalling") || token.endsWith("selling") || token.endsWith("welling") ||
                    token.endsWith("cotting") || token.endsWith("affing")) {
                return new Word(token.substring(0, token.length() - 3), tag);
            } else {
                return new Word(token.substring(0, token.length() - 4), tag);
            }
        } else if (token.endsWith("xying")) {
            return new Word(token.substring(0, token.length() - 4) + "i", tag);
        } else if (token.endsWith("ing")) {
            return new Word(token.substring(0, token.length() - 3), tag);
        } else {
            return null;
        }
    }

    protected boolean removeD(String token) {
        token = token.toLowerCase();

        return token.endsWith("aced") || // e.g. faced -> face
                token.endsWith("cenced") || // e.g. licenced -> licence
                token.endsWith("rced") || // e.g. sourced -> source
                token.endsWith("uced") || // e.g. introduced -> introduce
                token.endsWith("llided") || // e.g. collided -> collide
                token.endsWith("brided") || // e.g. debrided -> debride
                token.endsWith("graded") || // e.g. down-graded -> down-grade
                token.equals("ceded") ||
                token.endsWith("jaded") || // e.g. jaded -> jade
                token.endsWith("coded") || // e.g. coded -> code
                token.endsWith("emceed") || // e.g. emceed -> emcee
                token.endsWith("reed") || // e.g. freed -> free
                token.endsWith("teed") || // e.g. guaranteed -> guarantee
                token.equals("aged") ||
                token.endsWith("riaged") || // e.g. triaged -> triage
                token.endsWith("naged") || // e.g. managed -> manage
                token.endsWith("paged") || // e.g. paged -> page
                token.endsWith("dged") || // e.g. bridged -> bridge
                token.endsWith("lged") || // e.g. indulged -> indulge
                token.endsWith("nged") || // e.g. changed -> change
                token.endsWith("rged") || // e.g. emerged -> emerge
                token.endsWith("athed") || // e.g. breathed -> breathe
                token.endsWith("ythed") || // e.g. scythed -> scythe
                token.endsWith("faked") || // e.g. faked -> fake
                token.endsWith("raked") || // e.g. raked -> rake
                token.endsWith("iked") || // e.g. liked -> like
                token.endsWith("caled") || // e.g. down-scales -> down-scale
                token.endsWith("bled") || // e.g. assembled -> assemble, enabled -> enable
                token.endsWith("ogled") || // e.g. ogled -> ogle, googled -> google
                token.endsWith("aroled") || // e.g. paroled -> parole
                token.endsWith("tled") || // e.g. titled -> title
                token.endsWith("duled") || // e.g. scheduled -> schedule
                token.endsWith("timed") || // e.g. timed -> time
                token.endsWith("named") || // e.g. codenamed -> codename
                token.endsWith("fined") || // e.g. defined -> define
                token.endsWith("lined") || // e.g. lined -> line
                token.endsWith("mined") || // e.g. examined -> examine
                token.endsWith("boned") || // e.g. boned -> bone
                token.endsWith("poned") || // e.g. postponed -> postpone
                token.equals("zoned") || // e.g. zoned -> zone
                token.equals("re-zoned") || // e.g. zoned -> zone
                token.endsWith("gined") || // e.g. imagined -> imagine
                token.endsWith("tuned") || // e.g. tuned -> tune
                token.endsWith("iped") || // e.g. wiped -> wipe
                token.endsWith("yped") || // e.g. typed -> type
                token.endsWith("roped") || // e.g. roped -> rope
                token.endsWith("dared") || // e.g. dared -> dare
                token.endsWith("cred") || // e.g. massacred -> massacre
                token.endsWith("bored") || // e.g. bored -> bore
                token.endsWith("hired") || // e.g. hired -> hire
                token.endsWith("uired") || // e.g. enquired -> enquire
                token.endsWith("tred") || // e.g. centred -> centre
                token.endsWith("cured") || // e.g. cured -> cure
                token.endsWith("gured") || // e.g. reconfigured -> reconfigure
                token.endsWith("jured") || // e.g. injured -> injure
                token.equals("lured") || // e.g. lured -> lure
                token.endsWith("leased") || // e.g. released -> release
                token.endsWith("ised") || // e.g. criminalised -> criminalise
                token.endsWith("nsed") || // e.g. licensed -> license
                token.endsWith("osed") || // e.g. opposed -> oppose
                token.equals("owed") || // e.g. owed -> owe
                token.endsWith("ursed") || // e.g. accursed -> accurse
                token.endsWith("ysed") || // e.g. catalysed -> catalyse
                token.endsWith("rrotted") || // e.g. garrotted -> garrottes
                token.endsWith("cated") || // e.g. located -> locate
                token.endsWith("dated") || // e.g. consolidated -> consolidate
                token.endsWith("created") || // e.g. created -> create
                token.endsWith("gated") || // e.g. relegated -> relegate
                token.endsWith("iated") || // e.g. affiliated -> affiliate
                token.endsWith("ulated") || // e.g. regulated -> regulate
                token.endsWith("ylated") || // e.g. methylated -> methylate
                token.endsWith("nated") || // e.g. hyphenated -> hyphenate
                token.endsWith("mated") || // e.g. decimated -> decimate, desquamated -> desquamate
                token.endsWith("rated") || // e.g. frustrated -> frustrate
                token.endsWith("tated") || // e.g. reinstated -> reinstate
                token.endsWith("vated") || // e.g. activated -> activate
                token.equals("meted") || // e.g. meted -> mete
                token.equals("cited") || // e.g. cited -> cite
                token.endsWith("uetted") || // e.g. silhouetted -> silhouette
                token.endsWith("zetted") || // e.g. gazetted -> gazette
                token.endsWith("ibuted") || // e.g. misattributed -> misattribute
                token.endsWith("ocuted") || // e.g. electrocuted -> electrocute
                token.endsWith("muted") || // e.g. commuted -> commute
                token.endsWith("moted") || // e.g. promoted -> promote
                token.endsWith("noted") || // e.g. denoted -> denote
                token.endsWith("voted") || // e.g. voted -> vote
                token.equals("routed") || // e.g. routed -> route
                token.equals("re-routed") || // e.g. routed -> route
                token.endsWith("sputed") || // e.g. disputed -> dispute
                token.endsWith("bued") || // e.g. imbued -> imbue
                token.endsWith("cued") || // e.g. rescued -> rescue
                token.endsWith("dued") || // e.g. subdued -> subdue
                token.endsWith("ueued") || // e.g. queued -> queue
                token.endsWith("gued") || // e.g. prologued -> prologue
                token.endsWith("lued") || // e.g. glued -> glue
                token.endsWith("nued") || // e.g. discontinued -> discontinue
                token.endsWith("qued") || // e.g. piqued -> pique
                token.endsWith("crued") || // e.g. accrued -> accrue
                token.endsWith("strued") || // e.g. construed -> construe
                token.endsWith("sued") || // e.g. sued -> sue, issued -> issue
                token.endsWith("aved") || // e.g. saved -> save
                token.endsWith("ived") || // e.g. outlived -> outlive
                token.endsWith("oved") || // e.g. loved -> love
                token.endsWith("rved") || // e.g. reserved -> reserve
                token.endsWith("xed") || // e.g. axed -> axe
                token.endsWith("dyed") || // e.g. dyed -> dye
                token.endsWith("dazed") || // e.g. dazed -> daze
                token.endsWith("razed") || // e.g. crazed -> craze
                token.endsWith("ized") || // e.g. criminalized -> criminalize
                token.endsWith("tzed") || // e.g. waltzed -> waltz
                token.endsWith("yzed"); // || // e.g. criminalized -> criminalize
    }

    protected boolean removeEDAndConsonant(String token) {
        return CONSONANT_CONSONANT_ED.matcher(token).matches() &&
                !token.toLowerCase().endsWith("balled") && // e.g. blackballed
                !token.toLowerCase().endsWith("called") && // e.g. called
                !token.toLowerCase().endsWith("dalled") && // e.g. medalled
                !token.toLowerCase().endsWith("talled") && // e.g. installed
                !token.toLowerCase().endsWith("walled") && // e.g. walled
                !token.toLowerCase().endsWith("felled") && // e.g. felled
                !token.toLowerCase().endsWith("helled") && // e.g. shelled
                !token.toLowerCase().endsWith("melled") && // e.g. smelled
                !token.toLowerCase().endsWith("spelled") && // e.g. spelled
                !token.toLowerCase().endsWith("swelled") && // e.g. spelled
                !token.toLowerCase().endsWith("quelled") && // e.g. quelled
                !token.toLowerCase().endsWith("yelled") && // e.g. yelled
                !token.toLowerCase().endsWith("billed") && // e.g. billed
                !token.toLowerCase().endsWith("filled") && // e.g. filled
                !token.toLowerCase().endsWith("chilled") && // e.g. chilled
                !token.toLowerCase().endsWith("drilled") && // e.g. drilled
                !token.toLowerCase().endsWith("killed") && // e.g. killed
                !token.toLowerCase().endsWith("milled") && // e.g. milled
                !token.toLowerCase().endsWith("stilled") && // e.g. stilled
                !token.toLowerCase().endsWith("spilled") && // e.g. stilled
                !token.toLowerCase().endsWith("tilled") && // e.g. tilled
                !token.toLowerCase().endsWith("thrilled") && // e.g. thrilled
                !token.toLowerCase().endsWith("willed") && // e.g. willed
                !token.toLowerCase().endsWith("polled") && // e.g. polled
                !token.toLowerCase().equals("rolled") && // e.g. rolled
                !token.toLowerCase().equals("tolled") && // e.g. rolled
                !token.toLowerCase().equals("trolled") && // e.g. trolled
                !token.toLowerCase().endsWith("ulled") && // e.g. culled
                !token.toLowerCase().endsWith("ossed") && // e.g. crossed
                !token.toLowerCase().endsWith("uzzed"); // e.g. buzzed
    }

    protected boolean replaceIEDWithY(String token) {
        return token.endsWith("ied");
    }

    protected boolean pastPToBaseNoChange(String token) {
        token = token.toLowerCase();
        return token.endsWith("cast") ||
                token.endsWith("spread") ||
                token.endsWith("become") ||
                token.endsWith("overcome") ||
                token.equals("set") ||
                token.equals("upset") ||
                token.equals("wed") ||
                token.equals("shed") ||
                token.equals("split") ||
                token.equals("rerun") ||
                token.equals("fit") ||
                token.equals("clad") ||
                token.equals("ironclad") ||
                token.equals("read") ||
                token.equals("misread") ||
                token.equals("offset") ||
                token.equals("hit") ||
                token.equals("quit") ||
                token.equals("bet") ||
                token.equals("bid") ||
                token.equals("rebid") ||
                token.equals("beset") ||
                token.equals("thrust") ||
                token.equals("inset") ||
                token.equals("beat") ||
                token.equals("overrun") ||
                token.equals("hurt") ||
                token.equals("knit") ||
                token.equals("shut");
    }

    protected boolean containsPunctuation(String token) {
        for (int i = 0; i < PUNCTUATION.length(); i++) {
            if (token.contains(PUNCTUATION.substring(i, i + 1))) {
                return true;
            }
        }
        return false;
    }

    protected boolean isBlackListedPastPToBase(String token) {
        token = token.toLowerCase();
        return token.equals("opinionated") ||
                token.endsWith("wrought") ||
                token.equals("sled") ||
                token.equals("bore") || // bore is past tense, not past participle
                token.equals("bed") ||
                token.equals("coalbed") ||
                token.equals("deathbed") ||
                token.equals("trackbed") ||
                token.equals("need") ||
                token.equals("seabed") ||
                token.equals("testbed") ||
                token.equals("riverbed") ||
                token.equals("linseed") ||
                token.equals("sinced") ||
                containsPunctuation(token);
    }

    public Word pastPToBase(Word word) {
        final String tag = tagSet.VERB_BASE;
        final String token = word.getToken();

        if (isBlackListedPastPToBase(token)) {
            return null;
        } else if (pastPToBaseNoChange(token)) {
            return new Word(token, tag);
        } else if (token.equalsIgnoreCase("been")) {
            return new Word("be", tag, token);
        } else if (token.equalsIgnoreCase("had")) {
            return new Word("have", tag, token);
        } else if (token.endsWith("done")) {
            return new Word(token.substring(0, token.length() - 2), tag, token);
        } else if (token.endsWith("gone")) {
            return new Word(token.substring(0, token.length() - 2), tag, token);
        } else if (token.equalsIgnoreCase("taken")) {
            return new Word("take", tag, token);
        } else if (token.equalsIgnoreCase("left")) {
            return new Word("leave", tag, token);
        } else if (token.toLowerCase().equals("got") || token.toLowerCase().endsWith("forgot")) {
            return new Word(token.substring(0, token.length() - 3) + "get", tag, token);
        } else if (token.toLowerCase().endsWith("gotten")) {
            // e.g. gotten -> get, forgotten -> forget
            return new Word(token.substring(0, token.length() - 5) + "et", tag, token);
        } else if (token.toLowerCase().endsWith("told") || token.toLowerCase().endsWith("sold")) {
            return new Word(token.substring(0, token.length() - 3) + "ell", tag, token);
        } else if (token.equalsIgnoreCase("took")) {
            return new Word("take", tag, token);
        } else if (token.endsWith("sat")) {
            return new Word(token.substring(0, token.length() - 2) + "it", tag, token);
        } else if (token.endsWith("saw")) {
            return new Word(token.substring(0, token.length() - 2) + "ee", tag, token);
        } else if (token.toLowerCase().endsWith("sewn") || token.toLowerCase().endsWith("hewn") ||
                token.toLowerCase().endsWith("strewn")) {
            return new Word(token.substring(0, token.length() - 1), tag);
        } else if (token.equalsIgnoreCase("fed") || token.equalsIgnoreCase("overfed") ||
                token.equalsIgnoreCase("bred") || token.equalsIgnoreCase("overbred")) {
            return new Word(token.substring(0, token.length() - 1) + "ed", tag, token);
        } else if (token.endsWith("torn")) {
            return new Word("tear", tag, token);
        } else if (token.equalsIgnoreCase("led") || token.equalsIgnoreCase("misled") || token.equalsIgnoreCase("co-led")) {
            return new Word(token.substring(0, token.length() - 1) + "ad", tag, token);
        } else if (token.endsWith("lit")) {
            return new Word(token.substring(0, token.length() - 2) + "ight", tag, token);
        } else if (token.endsWith("paid")) {
            return new Word(token.substring(0, token.length() - 2) + "y", tag, token);
        } else if (token.toLowerCase().endsWith("felt")) {
            return new Word(token.substring(0, token.length() - 3) + "eel", tag);
        } else if (token.toLowerCase().endsWith("dealt")) {
            return new Word(token.substring(0, token.length() - 4) + "eal", tag);
        } else if (token.toLowerCase().endsWith("built")) {
            return new Word(token.substring(0, token.length() - 1) + "d", tag);
        } else if (token.toLowerCase().endsWith("spelt") || token.toLowerCase().endsWith("spilt")) {
            return new Word(token.substring(0, token.length() - 1) + "l", tag);
        } else if (token.toLowerCase().endsWith("held")) {
            return new Word(token.substring(0, token.length() - 3) + "old", tag);
        } else if (token.toLowerCase().endsWith("rose")) {
            return new Word(token.substring(0, token.length() - 3) + "ise", tag);
        } else if (token.toLowerCase().endsWith("shrunk")) {
            return new Word(token.substring(0, token.length() - 3) + "ink", tag, token);
        } else if (token.toLowerCase().endsWith("stood")) {
            return new Word(token.substring(0, token.length() - 3) + "and", tag, token);
        } else if (token.toLowerCase().endsWith("slid")) {
            return new Word(token + "e", tag, token);
        } else if (token.toLowerCase().endsWith("broken")) {
            return new Word(token.substring(0, token.length() - 4) + "eak", tag, token);
        } else if (token.toLowerCase().endsWith("frozen")) {
            return new Word(token.substring(0, token.length() - 4) + "eeze", tag, token);
        } else if (token.toLowerCase().endsWith("chosen")) {
            return new Word(token.substring(0, token.length() - 4) + "oose", tag, token);
        } else if (token.toLowerCase().endsWith("woven")) {
            return new Word(token.substring(0, token.length() - 4) + "eave", tag, token);
        } else if (token.toLowerCase().endsWith("hidden")) {
            return new Word(token.substring(0, token.length() - 3) + "e", tag, token);
        } else if (token.toLowerCase().endsWith("bidden")) {
            // e.g. forbidden, bidden
            return new Word(token.substring(0, token.length() - 3), tag, token);
        } else if (token.toLowerCase().endsWith("ridden")) {
            return new Word(token.substring(0, token.length() - 3) + "e", tag, token);
        } else if (token.equalsIgnoreCase("risen") || token.equalsIgnoreCase("arisen")) {
            return new Word(token.substring(0, token.length() - 1), tag, token);
        } else if (token.toLowerCase().endsWith("given") || token.toLowerCase().endsWith("riven")) {
            return new Word(token.substring(0, token.length() - 1), tag, token);
        } else if (token.toLowerCase().endsWith("written") || token.toLowerCase().endsWith("bitten") ||
                token.toLowerCase().endsWith("smitten")) {
            return new Word(token.substring(0, token.length() - 3) + "e", tag, token);
        } else if (token.toLowerCase().endsWith("beaten")) {
            return new Word(token.substring(0, token.length() - 2), tag, token);
        } else if (token.toLowerCase().endsWith("shot")) {
            return new Word(token.substring(0, token.length() - 2) + "oot", tag, token);
        } else if (token.toLowerCase().endsWith("thought")) {
            return new Word(token.substring(0, token.length() - 5) + "ink", tag, token);
        } else if (token.toLowerCase().endsWith("fought")) {
            return new Word(token.substring(0, token.length() - 5) + "ight", tag, token);
        } else if (token.toLowerCase().endsWith("taught")) {
            return new Word(token.substring(0, token.length() - 5) + "each", tag, token);
        } else if (token.toLowerCase().endsWith("caught")) {
            return new Word(token.substring(0, token.length() - 5) + "atch", tag, token);
        } else if (token.toLowerCase().endsWith("flung") || token.toLowerCase().endsWith("stung")) {
            return new Word(token.substring(0, token.length() - 3) + "ing", tag, token);
        } else if (token.toLowerCase().endsWith("sent") || token.toLowerCase().endsWith("lent") ||
                token.toLowerCase().endsWith("spent")) {
            return new Word(token.substring(0, token.length() - 3) + "end", tag, token);
        } else if (token.toLowerCase().endsWith("panicked") || token.toLowerCase().endsWith("mimicked") ||
                token.toLowerCase().endsWith("afficked")) {
            return new Word(token.substring(0, token.length() - 3), tag, token);
        } else if (token.toLowerCase().endsWith("lain") || token.toLowerCase().endsWith("laid") || token.toLowerCase().endsWith("said")) {
            return new Word(token.substring(0, token.length() - 3) + "ay", tag, token);
        } else if (token.toLowerCase().endsWith("rung")) {
            return new Word(token.substring(0, token.length() - 3) + "ing", tag, token);
        } else if (token.toLowerCase().endsWith("sang") || token.toLowerCase().endsWith("sung")) {
            return new Word(token.substring(0, token.length() - 3) + "ing", tag, token);
        } else if (token.toLowerCase().endsWith("hung")) {
            return new Word(token.substring(0, token.length() - 3) + "ang", tag, token);
        } else if (token.toLowerCase().endsWith("sunk")) {
            return new Word(token.substring(0, token.length() - 3) + "ink", tag, token);
        } else if (token.toLowerCase().endsWith("borne")) {
            return new Word(token.substring(0, token.length() - 4) + "ear", tag, token);
        } else if (token.toLowerCase().endsWith("drawn") || token.endsWith("grown") ||
                token.toLowerCase().endsWith("seen") ||
                token.toLowerCase().endsWith("thrown") || token.toLowerCase().endsWith("blown")) {
            return new Word(token.substring(0, token.length() - 1), tag, token);
        } else if (token.toLowerCase().endsWith("flown")) {
            return new Word(token.substring(0, token.length() - 3) + "y", tag);
        } else if (token.toLowerCase().endsWith("sworn")) {
            return new Word(token.substring(0, token.length() - 3) + "ear", tag, token);
        } else if (token.toLowerCase().endsWith("heard")) {
            return new Word(token.substring(0, token.length() - 4) + "ear", tag, token);
        } else if (token.toLowerCase().endsWith("lost")) {
            return new Word(token.substring(0, token.length() - 1) + "e", tag, token);
        } else if (token.toLowerCase().endsWith("slept") || token.toLowerCase().endsWith("swept") ||
                token.toLowerCase().endsWith("kept")) {
            return new Word(token.substring(0, token.length() - 2) + "ep", tag, token);
        } else if (token.toLowerCase().endsWith("bound") || token.equalsIgnoreCase("wound")) {
            return new Word(token.substring(0, token.length() - 4) + "ind", tag, token);
        } else if (token.toLowerCase().endsWith("stuck")) {
            return new Word(token.substring(0, token.length() - 4) + "tick", tag, token);
        } else if (token.toLowerCase().endsWith("struck")) {
            return new Word(token.substring(0, token.length() - 3) + "ike", tag, token);
        } else if (token.toLowerCase().endsWith("stricken")) {
            return new Word(token.substring(0, token.length() - 5) + "ike", tag, token);
        } else if (token.toLowerCase().endsWith("spun")) {
            return new Word(token.substring(0, token.length() - 4) + "spin", tag, token);
        } else if (token.equalsIgnoreCase("dug")) {
            return new Word(token.substring(0, token.length() - 2) + "ig", tag, token);
        } else if (token.toLowerCase().endsWith("woken")) {
            return new Word(token.substring(0, token.length() - 4) + "ake", tag, token);
        } else if (token.equalsIgnoreCase("fled")) {
            return new Word(token.substring(0, token.length() - 1) + "e", tag, token);
        } else if (removeD(token)) {
            return new Word(token.substring(0, token.length() - 1), tag);
        } else if (removeEDAndConsonant(token)) {
            return new Word(token.substring(0, token.length() - 3), tag);
        } else if (replaceIEDWithY(token)) {
            return new Word(token.substring(0, token.length() - 3) + "y", tag);
        } else if (token.endsWith("ed")) {
            return new Word(token.substring(0, token.length() - 2), tag);
        } else {
            System.out.println("Probably not a past particple " + token);
            return null;
        }
    }

    protected boolean replaceYWithIES(String token) {
        return token.endsWith("y") &&
                !token.endsWith("oy") &&
                !token.endsWith("ay") &&
                !token.endsWith("ey");
    }

    protected boolean appendSES(String token) {
        token = token.toLowerCase();
        return token.equals("bus");
    }

    protected boolean appendZES(String token) {
        token = token.toLowerCase();
        return token.equals("quiz");
    }

    public Word pastPToThirdSing(Word word) {
        final String tag = tagSet.VERB_THIRD_SING;
        final Word replacement = pastPToBase(word);

        if (replacement == null) {
            return null;
        }

        final String token = replacement.getToken();

        System.out.println(word.getToken() + " => " + token);

        if (token.equalsIgnoreCase("be")) {
            return new Word("is", tag, token);
        } else if (token.equalsIgnoreCase("have")) {
            return new Word("has", tag, token);
        } else if (token.endsWith("do")) {
            return new Word(token + "es", tag);
        } else if (token.endsWith("go")) {
            return new Word(token + "es", tag);
        } else if (token.equalsIgnoreCase("take")) {
            return new Word("takes", tag, token);
        } else if (token.equalsIgnoreCase("leave")) {
            return new Word("leaves", tag, token);
        } else if (replaceYWithIES(token)) {
            return new Word(token.substring(0, token.length() - 1) + "ies", tag, token);
        } else if (token.endsWith("ch") || token.endsWith("sh") || token.endsWith("ss") ||
                token.endsWith("es") || token.endsWith("cus") ||
                token.endsWith("zz") ||
                token.endsWith("ucco") /* e.g. stucco */) {
            return new Word(token + "es", tag, token);
        } else if (appendSES(token)) {
            return new Word(token + "ses", tag, token);
        } else if (appendZES(token)) {
            return new Word(token + "zes", tag, token);
        } else {
            return new Word(token + "s", tag);
        }
        /*
        }
        else if (token.length() > 5 && ErrorUtilities.isVowel(token.charAt(word.getToken().length() - 4))) {
            return new Word(token.substring(0, token.length() - 1) + "s", tag);
        } else if
                (token.endsWith("ied")
                        || token.endsWith("ched")
                        || token.endsWith("sed")
                        || token.endsWith("ated")
                        || token.endsWith("lved")
                        || token.endsWith("nced")
                ) {
            return new Word(token.substring(0, token.length() - 1) + "s", tag);

        } else if (token.length() > 1) {
            return new Word(token.substring(0, token.length() - 2) + "s", tag);

        } else {
            return null;
        }
                */
    }

    public Word pastPToPresP(Word word) {
        final String tag = tagSet.VERB_PRES_PART;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("been")) {
            return new Word("being", tag);
        } else if (token.equalsIgnoreCase("had")) {
            return new Word("having", tag);
        } else if (token.equalsIgnoreCase("done")) {
            return new Word("doing", tag);
        } else if (token.equalsIgnoreCase("gone")) {
            return new Word("going", tag);
        } else if (token.equalsIgnoreCase("taken")) {
            return new Word("taking", tag);
        } else if (token.equalsIgnoreCase("left")) {
            return new Word("leaving", tag);
        } else if (token.endsWith("ied")) {
            return new Word(token.substring(0, token.length() - 3) + "ying", tag);
        } else if (token.length() > 2) {
            return new Word(token.substring(0, token.length() - 2) + "ing", tag);
        } else {
            return null;
        }
    }

    public Word baseToThirdSing(Word word) {
        final String tag = tagSet.VERB_THIRD_SING;
        final String token = word.getToken();

        if (token.equalsIgnoreCase("be")) {
            return new Word("is", tag);
        } else if (token.equalsIgnoreCase("have")) {
            return new Word("has", tag);
        } else if (token.endsWith("sh") || token.endsWith("ch") || token.endsWith("ss") ||
                token.endsWith("sso") ||
                token.endsWith("xi") || token.endsWith("x")) {
            return new Word(token + "es", tag);
        } else {
            return new Word(token + "s", tag);
        }
    }

    public Word regularAdjToComparative(Word word) {
        final String tag = tagSet.ADJ_COMP;
        final String token = word.getToken();

        if (token.endsWith("y")) {
            return new Word(token.substring(0, token.length() - 1) + "ier", tag);
        } else if (token.endsWith("t") && ErrorUtilities.isVowel(token.charAt(token.length() - 2))) {
            return new Word(token + "ter", tag);
        } else if (token.endsWith("e")) {
            return new Word(token + "r", tag);
        } else {
            return new Word(token + "er", tag);
        }
    }

    public Word regularAdjToSuperlative(Word word) {
        final String tag = tagSet.ADJ_SUP;
        final String token = word.getToken();

        if (token.endsWith("y")) {
            return new Word(token.substring(0, token.length() - 1) + "iest", tag);
        } else if (token.endsWith("t") && ErrorUtilities.isVowel(token.charAt(token.length() - 2))) {
            return new Word(token + "test", tag);
        } else if (token.endsWith("e")) {
            return new Word(token + "st", tag);
        } else {
            return new Word(token + "est", tag);
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
