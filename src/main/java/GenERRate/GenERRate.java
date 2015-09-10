package GenERRate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.xalan.xsltc.cmdline.getopt.GetOpt;

import java.io.*;
import java.util.*;

/**
 * Class GenERRate
 *
 * @author Jennifer Foster
 */

public class GenERRate {
    public static PartOfSpeech TAG_SET;
    private final Set<String> dictionary = new HashSet<String>();
    /**
     * The name of the file which contains a tag/token word list which can be used when creating an insertion error or a certain kind of substitution error.
     */
    public String extraWordList;
    /**
     * Maps an error type to all sentences in the complete corpus which exhibit this error type
     */
    private HashMap completeErrorMap;
    /**
     * A vector storing the input sentences
     */
    private ArrayList inputSentences;
    /**
     * A Vector of strings
     */
    private ArrayList errorAnalysis;
    /**
     * A Vector of extra words (token + tag)
     */
    private ArrayList extraWords;
    /**
     * The filename of the file used to store the complete error corpus.
     */
    private String completeErrorCorpus;
    /**
     * The filename of the file used to store the realistic error corpus.
     */
    private String realisticErrorCorpus;

    /**
     * Opens the corpus and reads the sentences into sentence vector.
     * Initialises the errorAnalysis vector using the information in the error analysis
     * file.
     * Initialises the extraWordList attribute.
     *
     * @param corpusFile
     * @param isTagged          whether the input sentences in corpusFilename are tagged
     * @param errorAnalysisFile
     * @param extraWordList
     * @param tagSet            the name of the tagset (Penn or CLAWS)
     */
    public GenERRate(String corpusFile, boolean isTagged, String errorAnalysisFile, String extraWordList, String tagSet) {
        TAG_SET = new PartOfSpeech(tagSet);
        this.extraWordList = extraWordList;
        inputSentences = new ArrayList();
        errorAnalysis = new ArrayList();
        this.extraWords = new ArrayList();
        completeErrorMap = new HashMap();
        initializeDictionary();
        try {
            File file = new File(corpusFile);
            FileReader read = new FileReader(file);
            BufferedReader buffer = new BufferedReader(read);
            String line = buffer.readLine();
            Sentence sentence;
            String actualSentence;
            StringBuffer sgml = new StringBuffer("<s");
            while (line != null) {
                actualSentence = extractSgml(line, sgml);
                sentence = new Sentence(actualSentence, isTagged);
                sentence.setSentenceSGML(sgml.toString());
                inputSentences.add(sentence);
                //reset the string buffer to be "<s"
                sgml.delete(2, sgml.length());
                line = buffer.readLine();
            }
            buffer.close();
            file = new File(errorAnalysisFile);
            read = new FileReader(file);
            buffer = new BufferedReader(read);
            line = buffer.readLine();
            while (line != null) {
                errorAnalysis.add(line);
                line = buffer.readLine();
            }
            buffer.close();
            file = new File(extraWordList);
            read = new FileReader(file);
            buffer = new BufferedReader(read);
            line = buffer.readLine();
            while (line != null) {
                this.extraWords.add(line);
                line = buffer.readLine();
            }
            buffer.close();
        } catch (IOException io) {
            System.err.println(io.getMessage());
        }
    }

    /**
     * Opens the corpus and reads the sentences into sentence vector.
     * Initialises the errorAnalysis vector using the information in the error analysis
     * file.
     * Initialises the extraWordList attribute.
     *
     * @param corpusFile
     * @param isTagged whether the input sentences in corpusFilename are tagged
     * @param extraWordList
     * @param errorAnalysis
     */
    public GenERRate(String corpusFile, boolean isTagged, String errorAnalysis, String extraWordList) {
        PartOfSpeech PART_OF_SPEECH = new PartOfSpeech();
        this.extraWordList = extraWordList;
        inputSentences = new ArrayList<String>();
        this.errorAnalysis = new ArrayList();
        this.extraWords = new ArrayList();
        completeErrorMap = new HashMap();
        try {
            File file = new File(corpusFile);
            FileReader read = new FileReader(file);
            BufferedReader buffer = new BufferedReader(read);
            String line = buffer.readLine();
            Sentence sentence;
            String actualSentence;
            StringBuffer sgml = new StringBuffer("<s");
            while (line != null) {
                actualSentence = extractSgml(line, sgml);
                sentence = new Sentence(line, isTagged);
                sentence.setSentenceSGML(sgml.toString());
                inputSentences.add(sentence);
                sgml.delete(2, sgml.length());
                line = buffer.readLine();
            }
            buffer.close();
            file = new File(errorAnalysis);
            read = new FileReader(file);
            buffer = new BufferedReader(read);
            line = buffer.readLine();
            while (line != null) {
                this.errorAnalysis.add(line);
                line = buffer.readLine();
            }
            buffer.close();
            file = new File(extraWordList);
            read = new FileReader(file);
            buffer = new BufferedReader(read);
            line = buffer.readLine();
            while (line != null) {
                this.extraWords.add(line);
                line = buffer.readLine();
            }
            buffer.close();
        } catch (IOException io) {
            System.err.println(io.getMessage());
        }
    }

    /**
     * Prints a help message about the GenERRate input options
     */
    private static void printHelp() {
        System.out.println("Usage: java GenERRate [-options] input-file output-file error-analysis-file word-list");
        System.out.println();
        System.out.println("where options are: ");
        System.out.println();
        System.out.println("-n \t the input file is not POS-tagged");
        System.out.println();
        System.out.println("-o \t the output file will be POS-tagged");
        System.out.println();
        System.out.println("-t <tagset>\t the name of the POS tagset (WSJ or CLAWS, default is WSJ)");
        System.out.println();
        System.out.println("-s <size>\t the desired number of sentences in the output corpus. Only use if frequency information is specified in the error analysis file");
        System.out.println();
        System.out.println("-l <log_file> \t Write failed attempts to the specified log file. If this is not set, writes it to <input-file>.err");
        System.out.println();
    }

    /**
     * Method to extract the SGML markup around a sentence. If there is no markup, simply returns the sentence.
     */
    private static String extractSgml(String sentence, StringBuffer sgml) {
        if (sentence.startsWith("<s") || sentence.startsWith("<S")) {
            //find the first occurrence of ">"
            int closingBracketPos = sentence.indexOf(">");
            if (closingBracketPos > -1) {
                sgml.append(sentence.substring(2, closingBracketPos) + " ");
                if (sentence.endsWith("</s>") || sentence.endsWith("</S>")) {
                    return sentence.substring(closingBracketPos + 1, sentence.length() - 4);
                } else {
                    //missing closing </s> tag - just return everything after the opening tag
                    return sentence.substring(closingBracketPos + 1);
                }
            } else {
                //something wrong with the markup, just return the sentence
                return sentence;
            }
        } else {
            return sentence;
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
        } else {
            GetOpt g = new GetOpt(args, "+:nos:ht:l:");
            int c;
            boolean isTagged = true;
            boolean isOutputTagged = false;
            boolean isErrorFrequency = false;
            String tagset = "WSJ";
            String logFile = null;
            int size = 0;
            try {
                while ((c = g.getNextOption()) != -1) {
                    switch (c) {
                        case 'n':
                            isTagged = false;
                            break;
                        case 'o':
                            isOutputTagged = true;
                            break;
                        case 's':
                            isErrorFrequency = true;
                            try {
                                size = Integer.parseInt(g.getOptionArg());
                            } catch (NumberFormatException n) {
                                System.err.println("Setting size of output corpus to 1000.");
                                size = 1000;
                            }
                            break;
                        case 't':
                            tagset = g.getOptionArg();
                            break;
                        case 'l':
                            logFile = g.getOptionArg();
                            break;
                    }
                }
                String[] obligArgs = g.getCmdArgs();

                String inputSentences = obligArgs[0];
                String outputFile = obligArgs[1];
                String errorAnalysis = obligArgs[2];
                String extraWordList = obligArgs[3];

                GenERRate errorCreation = null;
                if (isTagged) {
                    errorCreation = new GenERRate(inputSentences, isTagged, errorAnalysis, extraWordList, tagset);
                } else {
                    errorCreation = new GenERRate(inputSentences, isTagged, errorAnalysis, extraWordList);
                }

                if (logFile == null) {
                    logFile = inputSentences + ".err";
                }

                if (isErrorFrequency) {
                    errorCreation.createRealisticErrorCorpusOneToOne(size, outputFile, isOutputTagged, logFile);
                } else {
                    errorCreation.createCompleteErrorCorpus(outputFile, isOutputTagged, logFile);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                printHelp();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                printHelp();
            }
        }
    }

    private void initializeDictionary() {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement;

        Map<String, String> env = System.getenv();
        String home = env.get("GENERRATE_HOME");

        if (home == null) {
            home = "";
        } else {
            // Add a trailing slash, since we don't use one below.
            home = home + File.separator;
        }

        try {
            jsonElement = parser.parse(new FileReader(home + "etc/dict.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (JsonElement e : jsonArray) {
            dictionary.add(e.getAsString());
        }
    }

    /**
     * Get the value of extraWordList
     * The name of the file which contains a tag/token word list which can be used when
     * creating an insertion error.
     *
     * @return the value of extraWordList
     */
    public String getExtraWordList() {
        return extraWordList;
    }

    /**
     * Set the value of extraWordList
     * The name of the file which contains a tag/token word list which can be used when
     * creating an insertion error.
     *
     * @param extraWordList the new value of extraWordList
     */
    public void setExtraWordList(String extraWordList) {
        this.extraWordList = extraWordList;
    }

    /**
     * Get the value of inputSentences
     * A vector storing the input sentences
     *
     * @return the value of inputSentences
     */
    private ArrayList getInputSentences() {
        return inputSentences;
    }

    /**
     * Set the value of inputSentences
     * A vector storing the input sentences
     *
     * @param inputSentences the new value of inputSentences
     */
    private void setInputSentences(ArrayList inputSentences) {
        this.inputSentences = inputSentences;
    }

    /**
     * Get the value of errorAnalysis
     * A Vector of strings
     *
     * @return the value of errorAnalysis
     */
    private ArrayList getErrorAnalysis() {
        return errorAnalysis;
    }

    /**
     * Set the value of errorAnalysis
     * A Vector of strings
     *
     * @param errorAnalysis the new value of errorAnalysis
     */
    private void setErrorAnalysis(ArrayList errorAnalysis) {
        this.errorAnalysis = errorAnalysis;
    }

    /**
     * Get the value of completeErrorCorpus
     * The filename of the file used to store the complete error corpus.
     *
     * @return the value of completeErrorCorpus
     */
    private String getCompleteErrorCorpus() {
        return completeErrorCorpus;
    }

    /**
     * Set the value of completeErrorCorpus
     * The filename of the file used to store the complete error corpus.
     *
     * @param completeErrorCorpus the new value of completeErrorCorpus
     */
    private void setCompleteErrorCorpus(String completeErrorCorpus) {
        this.completeErrorCorpus = completeErrorCorpus;
    }

    /**
     * Get the value of realisticErrorCorpus
     * The filename of the file used to store the realistic error corpus.
     *
     * @return the value of realisticErrorCorpus
     */
    private String getRealisticErrorCorpus() {
        return realisticErrorCorpus;
    }

    /**
     * Set the value of realisticErrorCorpus
     * The filename of the file used to store the realistic error corpus.
     *
     * @param realisticErrorCorpus the new value of realisticErrorCorpus
     */
    private void setRealisticErrorCorpus(String realisticErrorCorpus) {
        this.realisticErrorCorpus = realisticErrorCorpus;
    }

    /**
     * For each sentence in inputSentences
     * Write sentence to completeErrorCorpus
     * For each error in errorAnalysis
     * Attempt to insert error into sentence
     * If successful
     * write ungrammatical sentence to completeErrorCorpus together with
     * information about error type (call toString method of the Error class)
     * add ungrammatical sentence to CompleteErrorCorpus hashmap
     */
    public void createCompleteErrorCorpus(String theCompleteErrorCorpusFile, boolean isOutputTagged, String theLogFile) {
        completeErrorCorpus = theCompleteErrorCorpusFile;
        try {
            FileWriter writer = new FileWriter(completeErrorCorpus);
            PrintWriter print = new PrintWriter(writer);
            FileWriter failedW = new FileWriter(theLogFile);
            PrintWriter failedP = new PrintWriter(failedW);

            Sentence sentence;
            String errorInfo;
            Error error;
            ArrayList list = null;
            for (int i = 0; i < inputSentences.size(); i++) {
                sentence = (Sentence) inputSentences.get(i);
                print.println(sentence);
                for (int j = 0; j < errorAnalysis.size(); j++) {
                    try {
                        errorInfo = (String) errorAnalysis.get(j);
                        error = getError(errorInfo, sentence);
                        if (error == null) {
                            throw new CannotCreateErrorException("There is a problem with the error specification in line " + (j + 1) + " of error analysis file.");
                        }
                        Sentence ungrammaticalSentence = error.insertError();

                        if (isOutputTagged) {
                            print.println(sentence.getSentenceSGML() + ungrammaticalSentence.getErrorDescription() + " original=\"" + sentence + "\">" + ungrammaticalSentence + "</s>");
                        } else {
                            print.println(sentence.getSentenceSGML() + ungrammaticalSentence.getErrorDescription() + " original=\"" + sentence + "\">" + ungrammaticalSentence.toStringNoTags() + "</s>");
                        }
                    } catch (CannotCreateErrorException c) {
                        failedP.println(sentence + "\t" + c.getMessage());
                    }
                }
                print.println();
            }
            print.close();
            failedP.close();
        } catch (IOException io) {
            System.err.println(io.getMessage());
        }
    }

    /**
     * Same as above method but no output file is produced
     */
    public void createCompleteErrorCorpus() {
        System.out.println("In createCompleteErrorCorpus");
        Sentence sentence;
        String errorInfo;
        Error error;
        ArrayList list = null;
        for (int i = 0; i < inputSentences.size(); i++) {
            sentence = (Sentence) inputSentences.get(i);
            for (int j = 0; j < errorAnalysis.size(); j++) {
                try {
                    errorInfo = (String) errorAnalysis.get(j);
                    error = getError(errorInfo, sentence);
                    if (error == null) {
                        throw new CannotCreateErrorException("There is a problem with the error specification in line " + (j + 1) + " of error analysis file.");
                    }
                    Sentence ungrammaticalSentence = error.insertError();

                    if (completeErrorMap.containsKey(error.getProbability() + "," + error)) {
                        list = (ArrayList) completeErrorMap.get(error.getProbability() + "," + error);
                    } else {
                        list = new ArrayList();
                    }
                    list.add(ungrammaticalSentence);


                    completeErrorMap.put(error.getProbability() + "," + error, list);
                } catch (CannotCreateErrorException c) {
                    //System.out.println(sentence + "\t" + c.getMessage());
                }
            }
        }
    }

    /**
     * Use the information about error frequency (if there is any) in the errorAnalysis
     * vector and the input sentences in the sentence vector
     * to create an error corpus (realisticErrorCorpus) with a realistic
     * error distribution
     */
    public void createRealisticErrorCorpus(int size, String theRealisticErrorCorpusFile, boolean isOutputTagged) {
        realisticErrorCorpus = theRealisticErrorCorpusFile;
        try {
            FileWriter writer = new FileWriter(realisticErrorCorpus);
            PrintWriter printer = new PrintWriter(writer);
            double probability = 0.0;
            long sentenceNo = 0;
            int totalCount = 0;
            int errorTypeCount = 0;
            Sentence sentence = null;
            Sentence ungrammaticalSentence = null;
            String errorInfo = null;
            Error error = null;
            for (int j = 0; j < errorAnalysis.size(); j++) {
                errorInfo = (String) errorAnalysis.get(j);
                sentenceNo = 1;
                errorTypeCount = 0;
                //shuffle the input sentences
                Collections.shuffle(inputSentences, new Random(errorInfo.hashCode()));
                for (int i = 0; i < inputSentences.size() && errorTypeCount < sentenceNo; i++) {
                    try {
                        sentence = (Sentence) inputSentences.get(i);
                        error = getError(errorInfo, sentence);
                        if (error == null) {
                            throw new CannotCreateErrorException("There is a problem with the error specification in line " + (j + 1) + " of error analysis file.");
                        }
                        //first time we create an Error object for this error type, get the probability associated with this error type
                        if (i == 0) {
                            probability = error.getProbability();
                            sentenceNo = Math.round(size * probability);
                        }
                        if (sentenceNo > 0) {
                            ungrammaticalSentence = error.insertError();
                            errorTypeCount++;
                            totalCount++;
                            if (isOutputTagged) {
                                printer.println(sentence.getSentenceSGML() + ungrammaticalSentence.getErrorDescription() + ">" + ungrammaticalSentence + "</s>");
                            } else {
                                printer.println(sentence.getSentenceSGML() + ungrammaticalSentence.getErrorDescription() + ">" + ungrammaticalSentence.toStringNoTags() + "</s>");
                            }
                        }
                    } catch (CannotCreateErrorException c) {
                        //System.out.println(sentence);
                        //System.out.println(c.getMessage());
                    }
                }
                //System.out.println("The error is " + error + " " + error.getProbability() + " " + errorTypeCount + " " + sentenceNo);
            }
            printer.close();
        } catch (IOException io) {
            System.err.println(io.getMessage());
        }
    }

    /**
     * Use the information about error frequency (if there is any) in the errorAnalysis
     * vector and the input sentences in the sentence vector
     * to create an error corpus (realisticErrorCorpus) with a realistic
     * error distribution
     */
    public void createRealisticErrorCorpusOneToOne(int size, String theRealisticErrorCorpusFile, boolean isOutputTagged, String theLogFile) {
        realisticErrorCorpus = theRealisticErrorCorpusFile;
        try {
            FileWriter writer = new FileWriter(realisticErrorCorpus);
            PrintWriter printer = new PrintWriter(writer);
            FileWriter failedW = new FileWriter(theLogFile);
            PrintWriter failedP = new PrintWriter(failedW);
            double probability = 0.0;
            long sentenceNo = 0;
            int totalCount = 0;
            int errorTypeCount = 0;
            Sentence sentence = null;
            Sentence ungrammaticalSentence = null;
            String errorInfo = null;
            Error error = null;
            int tried = 0;
            Collections.shuffle(inputSentences, new Random(inputSentences.get(0).hashCode()));
            for (int j = 0; j < errorAnalysis.size(); j++) {
                errorInfo = (String) errorAnalysis.get(j);
                sentenceNo = 1;
                errorTypeCount = 0;
                tried = 0;
                for (int i = 0; i < inputSentences.size() && errorTypeCount < sentenceNo && tried < inputSentences.size(); i++) {
                    try {
                        sentence = (Sentence) inputSentences.get(i);
                        error = getError(errorInfo, sentence);
                        if (error == null) {
                            throw new CannotCreateErrorException("There is a problem with the error specification in line " + (j + 1) + " of error analysis file.");
                        }
                        //first time we create an Error object for this error type, get the probability associated with this error type
                        if (i == 0) {
                            probability = error.getProbability();
                            sentenceNo = Math.round(size * probability);
                        }
                        if (sentenceNo > 0) {
                            tried++;
                            ungrammaticalSentence = error.insertError();
                            errorTypeCount++;
                            totalCount++;
                            //if we succeed in creating an error using this sentence, move this sentence to the end of the list
                            inputSentences.remove(sentence);
                            i--;
                            inputSentences.add(sentence);
                            if (isOutputTagged) {
                                printer.println(sentence.getSentenceSGML() + ungrammaticalSentence.getErrorDescription() + " original=\"" + sentence + "\">" + ungrammaticalSentence + "</s>");
                            } else {
                                printer.println(sentence.getSentenceSGML() + ungrammaticalSentence.getErrorDescription() + " original=\"" + sentence + "\">" + ungrammaticalSentence.toStringNoTags() + "</s>");
                            }
                        }
                    } catch (CannotCreateErrorException c) {
                        failedP.println(sentence + "\t" + c.getMessage());
                    }
                }
                System.out.println("The error is " + error + " " + error.getProbability() + " " + errorTypeCount + " " + sentenceNo);
            }
            printer.close();
            failedP.close();
        } catch (IOException io) {
            System.err.println(io.getMessage());
        }
    }

    /**
     * Creates an error of the appropriate type based on the error information supplied as the first parameter
     */
    private Error getError(String errorInfo, Sentence sentence) {
        StringTokenizer tokens = new StringTokenizer(errorInfo, "\t");
        int tokenCount = tokens.countTokens();
        String secondToken = null, thirdToken = null, fourthToken = null, fifthToken = null, sixthToken = null;
        double prob = 0.0;
        if (errorInfo.startsWith("subst")) {
            if (tokenCount == 1) {
                return new SubstError(sentence, extraWords);
            } else if (tokenCount == 2) {
                tokens.nextToken();
                secondToken = tokens.nextToken();
                try {
                    prob = Double.parseDouble(secondToken);
                    SubstError subst = new SubstError(sentence, extraWords);
                    subst.setProbability(prob);
                    return subst;
                } catch (NumberFormatException n) {
                    return new SubstWordConfusionError(sentence, extraWords, secondToken);
                }
            } else if (tokenCount == 3) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    prob = Double.parseDouble(thirdToken);
                    SubstWordConfusionError subst = new SubstWordConfusionError(
                            sentence, extraWords, secondToken);
                    subst.setProbability(prob);
                    return subst;
                } catch (NumberFormatException n) {
                    return new SubstWrongFormError(
                            sentence, TAG_SET, secondToken, thirdToken, extraWords, dictionary);
                }
            } else if (tokenCount == 4) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    fourthToken = tokens.nextToken();
                    prob = Double.parseDouble(fourthToken);
                    SubstWrongFormError subst = new SubstWrongFormError(
                            sentence, TAG_SET, secondToken, thirdToken, extraWords, dictionary);
                    subst.setProbability(prob);
                    return subst;

                } catch (NumberFormatException n) {
                    return new SubstSpecificWordConfusionError(sentence, new Word(thirdToken), new Word(fourthToken));
                }
            } else if (tokenCount == 5) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    fourthToken = tokens.nextToken();
                    fifthToken = tokens.nextToken();
                    prob = Double.parseDouble(fifthToken);
                    SubstSpecificWordConfusionError subst = new SubstSpecificWordConfusionError(sentence, new Word(thirdToken), new Word(fourthToken));
                    subst.setProbability(prob);
                    return subst;
                } catch (NumberFormatException n) {
                    //invalid input, returning null
                    return null;
                }
            } else {
                //invalid input, returning null
                return null;
            }
        } else if (errorInfo.startsWith("move")) {
            if (tokenCount == 1) {
                return new MoveError(sentence);
            } else if (tokenCount == 2) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    prob = Double.parseDouble(secondToken);
                    MoveError move = new MoveError(sentence);
                    move.setProbability(prob);
                    return move;
                } catch (NumberFormatException n) {
                    return new MovePOSError(sentence, secondToken);
                }
            } else if (tokenCount == 3) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    prob = Double.parseDouble(thirdToken);
                    MovePOSError move = new MovePOSError(sentence, secondToken);
                    move.setProbability(prob);
                    return move;
                } catch (NumberFormatException n) {
                    //invalid input, returning null
                    return null;
                }
            } else if (tokenCount == 4) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    fourthToken = tokens.nextToken();
                    return new MovePOSWhereError(sentence, secondToken, Boolean.parseBoolean(thirdToken), Integer.parseInt(fourthToken));
                } catch (NumberFormatException n) {
                    //invalid input, returning null
                    return null;
                }
            } else if (tokenCount == 5) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    fourthToken = tokens.nextToken();
                    fifthToken = tokens.nextToken();
                    prob = Double.parseDouble(fifthToken);
                    MovePOSWhereError move = new MovePOSWhereError(sentence, secondToken, Boolean.parseBoolean(thirdToken), Integer.parseInt(fourthToken));
                    move.setProbability(prob);
                    return move;
                } catch (NumberFormatException n) {
                    //invalid input, returning null
                    return null;
                }
            } else {
                //invalid input, returning null
                return null;
            }
        } else if (errorInfo.startsWith("delete")) {
            if (tokenCount == 1) {
                return new DeletionError(sentence);
            } else if (tokenCount == 2) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    prob = Double.parseDouble(secondToken);
                    DeletionError delete = new DeletionError(sentence);
                    delete.setProbability(prob);
                    return delete;
                } catch (NumberFormatException n) {
                    return new DeletionPOSError(sentence, secondToken);
                }
            } else if (tokenCount == 3) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    Error delete;
                    if (secondToken.equals("word")) {
                        delete = new DeletionWordError(sentence, thirdToken);
                    } else {
                        prob = Double.parseDouble(thirdToken);
                        delete = new DeletionPOSError(sentence, secondToken);
                        delete.setProbability(prob);
                    }
                    return delete;
                } catch (NumberFormatException n) {
                    return null;
                }
            } else if (tokenCount == 4) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    fourthToken = tokens.nextToken();
                    prob = Double.parseDouble(fourthToken);
                    return null;
                } catch (NumberFormatException n) {
                    if (fourthToken.equals("true") || fourthToken.equals("false")) {
                        DeletionPOSWhereError delete = new DeletionPOSWhereError(sentence, secondToken, thirdToken, Boolean.parseBoolean(fourthToken));
                        delete.setProbability(prob);
                        return delete;
                    } else {
                        DeletionPOSWhereError delete = new DeletionPOSWhereError(sentence, secondToken, thirdToken, fourthToken);
                        delete.setProbability(prob);
                        return delete;
                    }
                }
            } else if (tokenCount == 5) {
                try {
                    tokens.nextToken();
                    secondToken = tokens.nextToken();
                    thirdToken = tokens.nextToken();
                    fourthToken = tokens.nextToken();
                    fifthToken = tokens.nextToken();
                    prob = Double.parseDouble(fifthToken);
                    if (fourthToken.equals("true") || fourthToken.equals("false")) {
                        DeletionPOSWhereError delete = new DeletionPOSWhereError(sentence, secondToken, thirdToken, Boolean.parseBoolean(fourthToken));
                        delete.setProbability(prob);
                        return delete;
                    } else {
                        DeletionPOSWhereError delete = new DeletionPOSWhereError(sentence, secondToken, thirdToken, fourthToken);
                        delete.setProbability(prob);
                        return delete;
                    }
                } catch (NumberFormatException n) {
                    //invalid input, returning null
                    return null;
                }
            } else {
                //invalid input, returning null
                return null;
            }
        } else if (errorInfo.startsWith("insert")) {
            if (tokenCount == 1) {
                return new InsertionError(sentence, extraWords);
            } else if (tokenCount == 2) {
                tokens.nextToken();
                //see whether the second token is the token "file" or "same sentence"
                secondToken = tokens.nextToken();
                try {
                    prob = Double.parseDouble(secondToken);
                    InsertionError insert = new InsertionError(sentence, extraWords);
                    insert.setProbability(prob);
                    return insert;
                } catch (NumberFormatException n) {
                    if (secondToken.equalsIgnoreCase("sentence")) {
                        return new InsertionFromFileOrSentenceError(sentence);
                    } else if (secondToken.equalsIgnoreCase("file")) {
                        return new InsertionFromFileOrSentenceError(sentence, extraWords);
                    }
                }
            } else if (tokenCount == 3) {
                tokens.nextToken();
                secondToken = tokens.nextToken();
                thirdToken = tokens.nextToken();
                try {
                    prob = Double.parseDouble(thirdToken);
                    if (secondToken.equalsIgnoreCase("sentence")) {
                        InsertionFromFileOrSentenceError insert = new InsertionFromFileOrSentenceError(sentence);
                        insert.setProbability(prob);
                        return insert;
                    } else if (secondToken.equalsIgnoreCase("file")) {
                        InsertionFromFileOrSentenceError insert = new InsertionFromFileOrSentenceError(sentence, extraWords);
                        insert.setProbability(prob);
                        return insert;
                    }
                } catch (NumberFormatException n) {
                    if (secondToken.equalsIgnoreCase("sentence")) {
                        InsertionPOSError insert = new InsertionPOSError(sentence, thirdToken);
                        return insert;
                    } else {
                        InsertionPOSError insert = new InsertionPOSError(sentence, extraWords, thirdToken);
                        return insert;

                    }
                }
            } else if (tokenCount == 4) {
                tokens.nextToken();
                secondToken = tokens.nextToken();
                thirdToken = tokens.nextToken();
                fourthToken = tokens.nextToken();
                try {
                    prob = Double.parseDouble(fourthToken);
                    if (secondToken.equalsIgnoreCase("sentence")) {
                        InsertionPOSError insert = new InsertionPOSError(sentence, thirdToken);
                        insert.setProbability(prob);
                        return insert;
                    } else if (secondToken.equalsIgnoreCase("file")) {
                        InsertionPOSError insert = new InsertionPOSError(sentence, extraWords, thirdToken);
                        insert.setProbability(prob);
                        return insert;
                    }
                } catch (NumberFormatException n) {
                    return null;
                }
            } else if (tokenCount == 5) {
                tokens.nextToken();
                secondToken = tokens.nextToken();
                thirdToken = tokens.nextToken();
                fourthToken = tokens.nextToken();
                fifthToken = tokens.nextToken();
                try {
                    prob = Double.parseDouble(fifthToken);
                    return null;
                } catch (NumberFormatException n) {
                    if (secondToken.equalsIgnoreCase("sentence") && (fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        return new InsertionPOSWhereError(sentence, thirdToken, fourthToken, Boolean.parseBoolean(fifthToken));
                    } else if (secondToken.equalsIgnoreCase("file") && (fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        return new InsertionPOSWhereError(sentence, extraWords, thirdToken, fourthToken, Boolean.parseBoolean(fifthToken));
                    } else if (secondToken.equalsIgnoreCase("sentence") && !(fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        return new InsertionPOSWhereError(sentence, thirdToken, fourthToken, fifthToken);
                    } else if (secondToken.equalsIgnoreCase("file") && !(fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        return new InsertionPOSWhereError(sentence, extraWords, thirdToken, fourthToken, fifthToken);
                    }
                }
            } else if (tokenCount == 6) {
                tokens.nextToken();
                secondToken = tokens.nextToken();
                thirdToken = tokens.nextToken();
                fourthToken = tokens.nextToken();
                fifthToken = tokens.nextToken();
                sixthToken = tokens.nextToken();
                try {
                    prob = Double.parseDouble(sixthToken);
                    if (secondToken.equalsIgnoreCase("sentence") && (fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        InsertionPOSWhereError insert = new InsertionPOSWhereError(sentence, thirdToken, fourthToken, Boolean.parseBoolean(fifthToken));
                        insert.setProbability(prob);
                        return insert;
                    } else if (secondToken.equalsIgnoreCase("file") && (fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        InsertionPOSWhereError insert = new InsertionPOSWhereError(sentence, extraWords, thirdToken, fourthToken, Boolean.parseBoolean(fifthToken));
                        insert.setProbability(prob);
                        return insert;
                    } else if (secondToken.equalsIgnoreCase("sentence") && !(fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        InsertionPOSWhereError insert = new InsertionPOSWhereError(sentence, thirdToken, fourthToken, fifthToken);
                        insert.setProbability(prob);
                        return insert;
                    } else if (secondToken.equalsIgnoreCase("file") && !(fifthToken.equalsIgnoreCase("true") || fifthToken.equalsIgnoreCase("false"))) {
                        InsertionPOSWhereError insert = new InsertionPOSWhereError(sentence, extraWords, thirdToken, fourthToken, fifthToken);
                        insert.setProbability(prob);
                        return insert;
                    }
                } catch (NumberFormatException n) {
                    return null;
                }
            } else {
                //invalid input, returning null
                return null;
            }
        }
        //something wrong - returning null
        return null;
    }


}
