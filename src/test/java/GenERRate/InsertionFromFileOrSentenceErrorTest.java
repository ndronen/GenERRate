package GenERRate;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndronen on 9/1/15.
 */
public class InsertionFromFileOrSentenceErrorTest extends TestCase {

    public void testInsertError() throws Exception {
        List<String> extraWordList = new ArrayList<String>();
        extraWordList.add("about IN");
        extraWordList.add("at IN");
        extraWordList.add("by IN");
        extraWordList.add("for IN");
        extraWordList.add("from IN");
        extraWordList.add("in IN");
        extraWordList.add("of IN");
        extraWordList.add("on IN");
        extraWordList.add("to IN");
        extraWordList.add("with IN");
        Sentence sentence = new Sentence(
                "Before IN his PRP$ death NN in IN DIGITDIGITDIGITDIGIT NNP , , Mahesh NNP " +
                        "went VBD on IN to TO work VB in IN the DT sound NN department NN in IN " +
                        "Kamal NNP Haasan NNP 's POS Kuruthipunal NNP and CC Aalavandhan NNP . .", true);
        InsertionFromFileOrSentenceError error = new InsertionFromFileOrSentenceError(
                sentence, extraWordList);
        Sentence corruptSentence = error.insertError();
        System.out.println(sentence);
        System.out.println(corruptSentence);
        System.out.println(corruptSentence.getErrorDescription());
    }
}