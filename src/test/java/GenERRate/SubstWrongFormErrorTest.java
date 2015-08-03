package GenERRate;

import com.sun.tools.javac.jvm.Gen;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndronen on 8/3/15.
 */
public class SubstWrongFormErrorTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testInsertError() throws Exception {

    }

    public void testMakeNounSingular() throws Exception {
        fail("not implemented");
    }

    public void testMakeNounPlural() throws Exception {
        fail("not implemented");
    }

    /*
    165 SubstWrongFormVBPVBZError
    746 SubstWrongFormVBVBGError
    */
    public void testMakeVerbSingular() throws Exception {
        fail("not implemented");
    }

    public void testMakeVerbPlural() throws Exception {
        fail("not implemented");
    }

    /*
    359 SubstWrongFormVBZVBGError
    300 SubstWrongFormVBZVBPError
     */
    public void testThirdSingToPresP() throws Exception {
        fail("not implemented");
    }

    public void testNonThirdSingToPresP() throws Exception {
        fail("not implemented");
    }

    public void testPastpToPresp() throws Exception {
        fail("not implemented");
    }

    /*
    343 SubstWrongFormTOVBGError
    */
    public void testBaseToPresP() throws Exception {
        fail("not implemented");
    }

    public void testPresPToPastP() throws Exception {
        fail("not implemented");
    }

    /*
    1102 SubstWrongFormVBGVBZError
     */
    public void testPresPToThirdSing() throws Exception {
        fail("not implemented");
    }

    public void testPresPToNonThirdSing() throws Exception {
        fail("not implemented");
    }

    /*
    1148 SubstWrongFormVBGTOError
     */
    public void testPresPToInf() throws Exception {
        fail("not implemented");
    }

    /*
    1722 SubstWrongFormVBNVBZError
     */
    public void testPastPToThirdSing() throws Exception {
        fail("not implemented");
    }

    public void testPastPToPresP() throws Exception {
        fail("not implemented");
    }

    public void testBaseToThirdSing() throws Exception {
        fail("not implemented");
    }

    public void testRegularAdjToComparative() throws Exception {
        fail("not implemented");
    }

    public void testRegularAdjToSuperlative() throws Exception {
        fail("not implemented");
    }

    public void testComparativeAdjToSuperlative() throws Exception {
        fail("not implemented");
    }

    public void testSuperlativeAdjToComparative() throws Exception {
        fail("not implemented");
    }

    public void testComparativeAdjToRegular() throws Exception {
        fail("not implemented");
    }

    public void testSuperlativeAdjToRegular() throws Exception {
        fail("not implemented");
    }

    /*
    310 SubstWrongFormRBJJError
     */
    public void testAdverbToAdj() throws Exception {
        final Map<String, String> expectations = new HashMap<String, String>();
        expectations.put("Angrily", "Angry");
        // ? expectations.put("Caddisfly", "Caddisf");
        // ? expectations.put("Cleevely", "Cleeve");
        // ? expectations.put("Nally", "Nal");
        // ? expectations.put("Kenneally", "Kenneal");
        // ? expectations.put("Vesely", "Vese");
        // ? expectations.put("anomaly", "anomalous");
        // ? expectations.put("attack-only", "attack-on");
        // ? expectations.put("autocephaly", "autocepha");
        // ? expectations.put("fiddly", "fidd");
        // ? expectations.put("firefly", "firef");
        // ? expectations.put("holly", "hol");
        // ? expectations.put("resupply", "resupple");
        // ? expectations.put("squiggly", "squiggle");
        // ? expectations.put("stylistically", "stylistical");
        // ? expectations.put("hilly", "hil");
        // ? expectations.put("Dolly", "Dol");
        // ? expectations.put("summarily", "summari");
        // ? expectations.put("damselfly", "damself");
        // ? expectations.put("wholly", "whole");
        // ? expectations.put("daily", "dai");

        expectations.put("Comparably", "Comparable");
        expectations.put("Inexplicably", "Inexplicable");
        expectations.put("abominably", "abominable");
        expectations.put("demonstrably", "demonstrable");

        expectations.put("Customarily", "Customary");
        expectations.put("Easily", "Easy");
        expectations.put("Ordinarily", "Ordinary");
        expectations.put("Steadily", "Steady");
        expectations.put("Voluntarily", "Voluntary");
        expectations.put("compulsorily", "compulsory");
        expectations.put("customarily", "customary");
        expectations.put("derogatorily", "derogatory");

        expectations.put("Characteristically", "Characteristic");
        expectations.put("Democratically", "Democratic");
        expectations.put("Diplomatically", "Diplomatic");
        expectations.put("Genetically", "Genetic");
        expectations.put("Simplistically", "Simplistic");
        expectations.put("Sonically", "Sonic");
        expectations.put("Stylistically", "Stylistic");
        expectations.put("combinatorically", "combinatoric");
        expectations.put("Tragically", "Tragic");
        expectations.put("allosterically", "allosteric");
        expectations.put("alpha-numerically", "alpha-numeric");
        expectations.put("amphitheatrically", "amphitheatric");
        expectations.put("anachronistically", "anachronistic");
        expectations.put("analytically", "analytic");
        expectations.put("artistically", "artistic");
        expectations.put("catastrophically", "catastrophic");
        expectations.put("cinematically", "cinematic");
        expectations.put("barotropically", "barotropic");
        expectations.put("catalytically", "catalytic");
        expectations.put("apologetically", "apologetic");
        expectations.put("concentrically", "concentric");
        expectations.put("diegetically", "diegetic");
        expectations.put("elastically", "elastic");
        expectations.put("emphatically", "emphatic");
        expectations.put("enharmonically", "enharmonic");
        expectations.put("enzymatically", "enzymatic");
        expectations.put("episodically", "episodic");
        expectations.put("epistemically", "epistemic");
        expectations.put("erratically", "erratic");
        expectations.put("biweekly", "biweek");
        expectations.put("efficiently", "efficient");
        expectations.put("distinguishably", "distinguishable");
        expectations.put("eerily", "eerie");
        expectations.put("flexibly", "flexible");
        expectations.put("formidably", "formidable");
        expectations.put("geniously", "genious");
        expectations.put("gloomily", "gloomy");
        expectations.put("grittily", "gritty");
        expectations.put("heartily", "hearty");
        expectations.put("hepatomegaly", "hepatomega");
        expectations.put("heroically", "heroic");
        expectations.put("histogenetically", "histogenetic");
        expectations.put("horribly", "horrible");
        expectations.put("hospitably", "hospitable");
        expectations.put("hoverfly", "hoverf");
        expectations.put("hypnotically", "hypnotic");
        expectations.put("iconically", "iconic");
        expectations.put("immeasurably", "immeasurable");
        expectations.put("improbably", "improbable");
        expectations.put("inconceivably", "inconceivable");
        expectations.put("incontestably", "incontestable");
        expectations.put("indefatigably", "indefatigable");
        expectations.put("indescribably", "indescribable");
        expectations.put("inescapably", "inescapable");
        expectations.put("inexcusably", "inexcusable");
        expectations.put("inseparably", "inseparable");
        expectations.put("interferometrically", "interferometric");
        expectations.put("interspecifically", "interspecific");
        expectations.put("intelligently", "intelligent");
        expectations.put("invisibly", "invisible");
        expectations.put("involuntarily", "involuntary");
        expectations.put("irrefutably", "irrefutable");
        expectations.put("jelly", "jel");
        expectations.put("laparoscopically", "laparoscopic");
        expectations.put("light-heartedly", "light-hearted");
        expectations.put("majestically", "majestic");
        expectations.put("matrilineally", "matrilineal");
        expectations.put("mechanistically", "mechanistic");
        expectations.put("memorably", "memorable");
        expectations.put("messily", "messy");
        expectations.put("metabolically", "metabolic");
        expectations.put("miserably", "miserable");
        expectations.put("monotheistically", "monotheistic");
        expectations.put("moralistically", "moralistic");
        expectations.put("noisily", "noisy");
        expectations.put("non-lethally", "non-lethal");
        expectations.put("non-lexically", "non-lexical");
        expectations.put("osmotically", "osmotic");
        expectations.put("parthenogenetically", "parthenogenetic");
        expectations.put("patchily", "patchy");
        expectations.put("perceptibly", "perceptible");
        expectations.put("phenotypically", "phenotypic");
        expectations.put("phonetically", "phonetic");
        expectations.put("photographically", "photographic");
        expectations.put("phylogenetically", "phylogenetic");
        expectations.put("plausibly", "plausible");
        expectations.put("pneumatically", "pneumatic");
        expectations.put("post-translationally", "post-translational");
        expectations.put("pre-emptively", "pre-emptive");
        expectations.put("pre-experimentally", "pre-experimental");
        expectations.put("prehomogeneously", "prehomogeneous");
        expectations.put("publically", "public");
        expectations.put("quadratically", "quadratic");
        expectations.put("quick-wittedly", "quick-witted");
        expectations.put("frequently", "frequent");
        expectations.put("respectably", "respectable");
        expectations.put("responsibly", "responsible");
        expectations.put("sarcastically", "sarcastic");
        expectations.put("sardonically", "sardonic");
        expectations.put("semi-professionally", "semi-professional");
        expectations.put("showily", "showy");
        expectations.put("socio-economically", "socio-economic");
        expectations.put("spectroscopically", "spectroscopic");

        expectations.put("stereospecifically", "stereospecific");
        expectations.put("sterically", "steric");
        expectations.put("stochastically", "stochastic");
        expectations.put("subgingivally", "subgingival");
        expectations.put("subsequestely", "subsequeste");
        expectations.put("sympathetically", "sympathetic");
        expectations.put("synthetically", "synthetic");
        expectations.put("systemically", "systemic");
        expectations.put("tandemly", "tandem");
        expectations.put("taxanomically", "taxanomic");
        expectations.put("temporarily", "temporary");
        expectations.put("thematically", "thematic");
        expectations.put("newly", "new");
        expectations.put("therapeutically", "therapeutic");
        expectations.put("transcriptionally", "transcriptional");
        expectations.put("uncannily", "uncanny");
        expectations.put("uncharacteristically", "uncharacteristic");
        expectations.put("uncomfortably", "uncomfortable");
        expectations.put("uncontrollably", "uncontrollable");
        expectations.put("understandably", "understandable");
        expectations.put("unforgivably", "unforgivable");
        expectations.put("unnecessarily", "unnecessary");
        expectations.put("unpredictably", "unpredictable");
        expectations.put("unreasonably", "unreasonable");
        expectations.put("irrespectively", "irrespective");
        expectations.put("unsustainably", "unsustainable");
        expectations.put("variably", "variable");
        expectations.put("virgously", "virgous");
        expectations.put("volcanically", "volcanic");
        expectations.put("worthily", "worthy");
        expectations.put("zygotically", "zygotic");
        expectations.put("Domestically", "Domestic");
        expectations.put("Heavily", "Heavy");
        expectations.put("Incredibly", "Incredible");
        expectations.put("Merrily", "Merry");
        expectations.put("Presumably", "Presumable");
        expectations.put("Remarkably", "Remarkable");
        expectations.put("Temporarily", "Temporary");
        expectations.put("Truly", "True");
        expectations.put("athletically", "athletic");
        expectations.put("bi-annually", "bi-annual");
        expectations.put("capably", "capable");
        expectations.put("dishonourably", "dishonourable");
        expectations.put("doubly", "double");
        expectations.put("eccentrically", "eccentric");
        expectations.put("energetically", "energetic");
        expectations.put("evolutionarily", "evolutionary");
        expectations.put("extraordinarily", "extraordinary");
        expectations.put("feebly", "feeble");
        expectations.put("frantically", "frantic");
        expectations.put("hydraulically", "hydraulic");
        expectations.put("impeccably", "impeccable");
        expectations.put("inevitably", "inevitable");
        expectations.put("inexorably", "inexorable");
        expectations.put("intracellularly", "intracellular");
        expectations.put("irresponsibly", "irresponsible");
        expectations.put("justifiably", "justifiable");
        expectations.put("linguistically", "linguistic");
        expectations.put("preferably", "preferable");
        expectations.put("profitably", "profitable");
        expectations.put("prolifically", "prolific");
        expectations.put("realistically", "realistic");
        expectations.put("scantily", "scanty");
        expectations.put("schematically", "schematic");
        expectations.put("semantically", "semantic");
        expectations.put("semi-annually", "semi-annual");
        expectations.put("stealthily", "stealthy");
        expectations.put("trivially", "trivial");
        expectations.put("undeniably", "undeniable");
        expectations.put("unhappily", "unhappy");
        expectations.put("unmistakably", "unmistakable");
        expectations.put("Arguably", "Arguable");
        expectations.put("Deeply", "Deep");
        expectations.put("Fully", "Full");
        expectations.put("Luckily", "Lucky");
        expectations.put("cheaply", "cheap");
        expectations.put("democratically", "democratic");
        expectations.put("handily", "handy");
        expectations.put("inexplicably", "inexplicable");
        expectations.put("luckily", "lucky");
        expectations.put("momentarily", "momentary");
        expectations.put("organically", "organic");
        expectations.put("reversibly", "reversible");
        expectations.put("statically", "static");
        expectations.put("telepathically", "telepathic");
        expectations.put("terribly", "terrible");
        expectations.put("tragically", "tragic");
        expectations.put("unduly", "undue");
        expectations.put("unfavorably", "unfavorable");
        expectations.put("unquestionably", "unquestionable");
        expectations.put("Inevitably", "Inevitable");
        expectations.put("chronically", "chronic");
        expectations.put("militarily", "military");
        expectations.put("aesthetically", "aesthetic");
        expectations.put("amicably", "amicable");
        expectations.put("angrily", "angry");
        expectations.put("electronically", "electronic");
        expectations.put("ethnically", "ethnic");
        expectations.put("inextricably", "inextricable");
        expectations.put("interchangeably", "interchangeable");
        expectations.put("noticeably", "noticeable");
        expectations.put("scientifically", "scientific");
        expectations.put("single-handedly", "single-handed");
        expectations.put("subtly", "subtle");
        expectations.put("suitably", "suitable");
        expectations.put("favourably", "favourable");
        expectations.put("genetically", "genetic");
        expectations.put("hastily", "hasty");
        expectations.put("romantically", "romantic");
        expectations.put("sporadically", "sporadic");
        expectations.put("visibly", "visible");
        expectations.put("characteristically", "characteristic");
        expectations.put("happily", "happy");
        expectations.put("honorably", "honorable");
        expectations.put("invariably", "invariable");
        expectations.put("reliably", "reliable");
        expectations.put("Basically", "Basic");
        expectations.put("Notably", "Notable");
        expectations.put("Possibly", "Possible");
        expectations.put("Probably", "Probable");
        expectations.put("duly", "due");
        expectations.put("enthusiastically", "enthusiastic");
        expectations.put("ordinarily", "ordinary");
        expectations.put("forcibly", "forcible");
        expectations.put("Primarily", "Primary");
        expectations.put("domestically", "domestic");
        expectations.put("drastically", "drastic");
        expectations.put("favorably", "favorable");
        expectations.put("reasonably", "reasonable");
        expectations.put("remarkably", "remarkable");
        expectations.put("comfortably", "comfortable");
        expectations.put("incredibly", "incredible");
        expectations.put("ostensibly", "ostensible");
        expectations.put("Specifically", "Specific");
        expectations.put("sharply", "sharp");
        expectations.put("voluntarily", "voluntary");
        expectations.put("basically", "basic");
        expectations.put("arguably", "arguable");
        expectations.put("necessarily", "necessary");
        expectations.put("readily", "ready");
        expectations.put("steadily", "steady");
        expectations.put("truly", "true");
        expectations.put("presumably", "presumable");
        expectations.put("deeply", "deep");
        expectations.put("dramatically", "dramatic");
        expectations.put("automatically", "automatic");

        expectations.put("considerably", "considerable");
        expectations.put("temporarily", "temporary");
        expectations.put("possibly", "possible");
        expectations.put("specifically", "specific");
        expectations.put("easily", "easy");
        expectations.put("heavily", "heavy");
        expectations.put("notably", "notable");
        expectations.put("fully", "full");
        expectations.put("probably", "probable");
        expectations.put("primarily", "primary");

        PartOfSpeech tagSet = new PartOfSpeech();
        SubstWrongFormError obj = new SubstWrongFormError(null, tagSet, null, null, null);
        for (Map.Entry<String, String> entry: expectations.entrySet()) {
            Word word = new Word(entry.getKey(), tagSet.ADV);
            Word replacement = obj.adverbToAdj(word);
            // assertEquals(replacement.getToken(), entry.getValue());
            if (!replacement.getToken().equals(entry.getValue())) {
                System.out.println("Failed " + entry.getValue() + " != " + replacement.getToken());
                System.exit(0);
                assertEquals(entry.getValue(), replacement.getToken());

            }
        }
    }
}