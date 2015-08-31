package GenERRate;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ndronen on 8/31/15.
 */
public class DeletionWordErrorTest extends TestCase {
    public void testInsertError() throws CannotCreateErrorException {
        boolean isTagged = true;
        Sentence sentence = new Sentence("I PRP grew VBD up RP with IN a DT " +
                "mentally RB ill JJ single JJ mother NN raising VBG me PRP " +
                "and CC no DT father NN figure NN in IN my PRP$ life NN . .",
                isTagged);
        Map<String, Integer> expectations = new HashMap<String, Integer>();
        expectations.put("with", 4);
        expectations.put("in", 16);

        for (Map.Entry<String, Integer> entry : expectations.entrySet()) {
            DeletionWordError error = new DeletionWordError(sentence, entry.getKey());
            Sentence corruptSentence = error.insertError();
            // Expecting the error to be position 4.
            DeletionDescription description = new DeletionDescription(corruptSentence.getErrorDescription());
            assertEquals((int) entry.getValue(), description.getPosition());
            assertEquals(entry.getKey(), description.getToken());
        }
    }

    private class DeletionDescription {
        private final String token;
        private final int position;

        private final String regex = "errortype=\"DeletionError\" details=\"(.*) at (.*)\"";
        private final Pattern pattern = Pattern.compile(regex);

        public DeletionDescription(String errorDescription) {
            System.out.println(errorDescription);
            Matcher matcher = pattern.matcher(errorDescription);
            if (!matcher.matches()) {
                throw new RuntimeException("Not a DeletionError description : " + errorDescription);
            }
            token = matcher.group(1);
            position = Integer.valueOf(matcher.group(2));
        }

        public String getToken() {
            return token;
        }

        public int getPosition() {
            return position;
        }
    }
}