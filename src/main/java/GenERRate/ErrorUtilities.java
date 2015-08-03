package GenERRate;

/**
 * Class ErrorUtilities
 *
 * @author Jennifer Foster
 */
class ErrorUtilities {
    /**
     * Checks whether a character is a vowel (upper or lower case)
     */
    public static boolean isVowel(char letter) {
        return letter == 'a' || letter == 'A' || letter == 'e' || letter == 'E' || letter == 'i' || letter == 'I' || letter == 'o' || letter == 'O' || letter == 'u' || letter == 'u';
    }

    /**
     * Returns true if the string passed in as a parameter is a part of speech tag
     * At the moment, these are only the POS tags used in SubstWrongFormErrors
     * Also, at the moment the Penn Treebank tagset is used.
     */
    public static boolean isPOS(String letters) {
        if (letters == null || GenERRate.TAG_SET == null) {
            return false;
        }

        return (letters.equals(GenERRate.TAG_SET.SINGULAR_NOUN) ||
                letters.equals(GenERRate.TAG_SET.PLURAL_NOUN) ||
                letters.equals(GenERRate.TAG_SET.VERB_BASE) ||
                letters.equals(GenERRate.TAG_SET.VERB_THIRD_SING) ||
                letters.equals(GenERRate.TAG_SET.VERB_PRES_PART) ||
                letters.equals(GenERRate.TAG_SET.VERB_PAST_PART) ||
                letters.equals(GenERRate.TAG_SET.VERB_NON_THIRD_SING) ||
                letters.equals(GenERRate.TAG_SET.VERB_PAST) ||
                letters.equals(GenERRate.TAG_SET.ADJ) ||
                letters.equals(GenERRate.TAG_SET.ADJ_COMP) ||
                letters.equals(GenERRate.TAG_SET.ADJ_SUP) ||
                letters.equals(GenERRate.TAG_SET.ADV) ||
                letters.equals(GenERRate.TAG_SET.VERB_PARTICLE) ||
                letters.equals(GenERRate.TAG_SET.INF) ||
                letters.equals(GenERRate.TAG_SET.PREP));
    }

}

