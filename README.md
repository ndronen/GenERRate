# GenERRate
Updated version of Jennifer Foster's [GenERRate](http://www.computing.dcu.ie/~jfoster/resources/genERRate.html).

===========================================================================

GenERRate expects the following input:


1. An input file containing one SGML-encoded sentence per line. If the sentences are POS-tagged, the format is:

<s> word1 TAG1 word2 TAG2 ... </s>

If the sentences are not POS-tagged, the format is:

<s> word1 word2 ... </s>

Use the -n option to specify that the input is not tagged.

Attributes can be included within the <s> tag. These will be included in the output.



2. An output file. This will be where the error annotated corpus will be written. 

Each sentence in the output file will contain a grammatical error. 

The original sentence will be encoded as an attribute in the SGML mark-up. 

Details of the actual error will also be encoded as an attribute.




3. An error analysis file, specifying one error per line. 

Frequency information is optional and is specified as a ratio at the end of each error specification, e.g. 

subst	word	an	a	0.1

means that an error involving an an/a confusion should constitute 10% of the final error corpus.

See below for the full list of errors supported by GenERRate.


4. A word list to be used by GenERRate when substituting a word or adding one to a sentence. The format of this is one word per line:

token TAG
token TAG



To see the GenERRate usage options, type:

java GenERRate


GenERRate can be run with the sample input files provided:

java GenERRate testInput.txt testOutput.txt testErrorAnalysis.txt testWordList.txt


Please make sure that your classpath includes the xalan jar file which is in the lib folder of GenERRate.jar.



GenERRate Error Options

delete	
Delete a word at random

delete	POS
Delete word tagged as POS.

delete	POS1	POS2	true
Delete a word tagged as POS1 occurring *after* a word tagged as POS2

delete	POS1	POS2	false
Delete a word tagged as POS1 occurring *before* a word tagged as POS2

delete	POS1	POS2	POS3
Delete a word tagged as POS2 occurring between a word tagged as POS1 and a word tagged as POS3

delete	start	POS1	POS2
Delete a word tagged as POS1 occurring at the start of the sentence and before a word tagged as POS2

delete	POS1	POS2	end
Delete a word tagged as POS2 occurring after a word tagged as POS1 and at the end of the sentence

delete	word	word1
Delete an occurrence of word1 in the sentence.

insert	
Insert a word at random into the sentence. Randomly choose a word from the sentence or from the extra word list.

insert	file
Insert a word at random into the sentence. The word is chosen at random from the extra word list

insert	sentence
Insert a word at random into the sentence. The word is chosen at random from the sentence.

insert	file	POS
Insert a word tagged as POS into the sentence at random. The word is chosen at random from the extra word list.

insert	sentence POS
Same as above except that the word is chosen at random from the sentence.

insert	file	POS1	POS2	true
Insert a word tagged as POS1 into the sentence after a word tagged as POS2. The word is chosen from the extra word list.

insert	sentence	POS1	POS2	true
Same as above except that the word is chosen at random from the sentence.

insert	file	POS1	POS2	false
Insert a word tagged as POS1 into the sentence before a word tagged as POS2. The word is chosen from the extra word list.

insert	sentence	POS1	POS2	false
Same as above except that the word is chosen at random from the sentence.

insert	file	POS1	POS2	POS3
Insert a word tagged as POS2 after a word tagged as POS1 and before a word tagged as POS3. The word is chosen from the extra word list.

insert	sentence	POS1	POS2	POS3
Same as above except that the word is chosen at random from the sentence.

insert	file	start	POS1	POS2
Insert a word tagged as POS1 at the start of the sentence before a word tagged as POS2. The word is chosen from the extra word list.

insert	sentence	start	POS1	POS2
Same as above except that the word is chosen at random from the sentence.

insert	file	POS1	POS2	end
Insert a word tagged as POS2 at the end of the sentence after a word tagged as POS1. The word is chosen from the extra word list.

insert	sentence	POS1	POS2	end
Same as above except that the word is chosen at random from the sentence.

move
Move a word chosen at random from the sentence to another position (also randomly chosen) within the sentence.

move	POS
Move a word tagged as POS to another position within the sentence.

move	POS	true	n
Move a word tagged as POS n places to the left.

move	POS	false	n
Move a word tagged as POS n places to the right.

subst
Substitute a word chosen at random in the sentence for a word chosen at random from the extra word list.

subst	POS
Substitute a word tagged as POS for another word from the extra word list tagged as POS

subst	word	word1	word2
Substitute an occurence of word1 in the sentence for an occurence of word2

subst	POS1	POS2
Substitute a word tagged as POS1 for a word tagged as POS2 from the extra word list UNLESS POS1 and POS2 are one of the pairs below. 
If they are, the inflection of the word is changed.

singular noun, plural noun (WSJ: NN -> NNS)
plural noun, singular noun (WSJ: NNS -> NN)

third singular verb, non third singular verb (WSJ: VBZ -> VBP)
non third singular verb, third singular verb (WSJ: VBP -> VBZ)

third singular verb, present participle verb (WSJ: VBZ -> VBG)
present participle verb, third singular verb (WSJ: VBG -> VBZ)

present participle verb, past participle verb (WSJ: VBG -> VBN)
past participle verb, present participle verb (WSJ: VBN -> VBG)

present participle verb, non third singular verb (WSJ: VBG -> VBP)
non third singular verb, present participle verb (WSJ: VBP -> VBG)

present participle verb, inf marker (WSJ: VBG -> TO)
inf marker, present participle verb (WSJ: TO -> VBG)

base verb, present participle verb (WSJ: VB -> VBG)
past participle verb, third singular verb (WSJ: VBN -> VBZ)
base verb, third singular verb (WSJ: VB -> VBZ)

base adjective, comparative adjective (WSJ: JJ -> JJR)
base adjective, superlative adjective (WSJ: JJ -> JJS)

comparative adjective, superlative adjective (WSJ: JJR -> JJS)
comparative adjective, base adjective (WSJ: JJR -> JJ)

superlative adjective, comparative adjective (WSJ: JJS -> JJR)
superlative adjective, base adjective (WSJ: JJS -> JJ)

adverb, adjective (WSJ: RB -> JJ)
