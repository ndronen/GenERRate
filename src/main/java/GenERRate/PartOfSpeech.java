package GenERRate;

class PartOfSpeech {
    public String SINGULAR_NOUN;
    public String PLURAL_NOUN;
    public String VERB_THIRD_SING;
    public String VERB_NON_THIRD_SING;
    public String VERB_PRES_PART;
    public String VERB_PAST_PART;
    public String VERB_PAST;
    public String VERB_BASE;
    public String ADJ;
    public String ADJ_COMP;
    public String ADJ_SUP;
    public String ADV;
    public String VERB_PARTICLE;
    public String PREP;
    public String INF;

    public PartOfSpeech() {
        SINGULAR_NOUN = "NN";
        PLURAL_NOUN = "NNS";
        VERB_THIRD_SING = "VBZ";
        VERB_NON_THIRD_SING = "VBP";
        VERB_PRES_PART = "VBG";
        VERB_PAST_PART = "VBN";
        VERB_PAST = "VBD";
        VERB_BASE = "VB";
        ADJ = "JJ";
        ADJ_COMP = "JJR";
        ADJ_SUP = "JJS";
        ADV = "RB";
        VERB_PARTICLE = "RP";
        PREP = "IN";
        INF = "TO";
    }

    public PartOfSpeech(String tagset) {
        if (tagset.equalsIgnoreCase("CLAWS")) {
            SINGULAR_NOUN = "NN1";
            PLURAL_NOUN = "NN2";
            VERB_THIRD_SING = "VVZ";
            VERB_NON_THIRD_SING = "VV0";
            VERB_PRES_PART = "VVG";
            VERB_PAST_PART = "VVN";
            VERB_PAST = "VVD";
            VERB_BASE = "VV0";
            ADJ = "JJ";
            ADJ_COMP = "JJR";
            ADJ_SUP = "JJT";
            ADV = "RR";
            VERB_PARTICLE = "RP";
            PREP = "II";
            INF = "TO";
        } else if (tagset.equalsIgnoreCase("WSJ")) {
            SINGULAR_NOUN = "NN";
            PLURAL_NOUN = "NNS";
            VERB_THIRD_SING = "VBZ";
            VERB_NON_THIRD_SING = "VBP";
            VERB_PRES_PART = "VBG";
            VERB_PAST_PART = "VBN";
            VERB_PAST = "VBD";
            VERB_BASE = "VB";
            ADJ = "JJ";
            ADJ_COMP = "JJR";
            ADJ_SUP = "JJS";
            ADV = "RB";
            VERB_PARTICLE = "RP";
            PREP = "IN";
            INF = "TO";
        }
        //no other tagset supported at the moment - defaulting to WSJ
        else {
            SINGULAR_NOUN = "NN";
            PLURAL_NOUN = "NNS";
            VERB_THIRD_SING = "VBZ";
            VERB_NON_THIRD_SING = "VBP";
            VERB_PRES_PART = "VBG";
            VERB_PAST_PART = "VBN";
            VERB_PAST = "VBD";
            VERB_BASE = "VB";
            ADJ = "JJ";
            ADJ_COMP = "JJR";
            ADJ_SUP = "JJS";
            ADV = "RB";
            VERB_PARTICLE = "RP";
            PREP = "IN";
            INF = "TO";
        }

    }
}