package gov.va.queri.wsd

import com.google.common.io.Resources
import edu.mit.jwi.IDictionary
import edu.mit.jwi.IRAMDictionary
import edu.mit.jwi.RAMDictionary
import edu.mit.jwi.data.ILoadPolicy
import edu.mit.jwi.item.IIndexWord
import edu.mit.jwi.item.ISynset
import edu.mit.jwi.item.ISynsetID
import edu.mit.jwi.item.IWord
import edu.mit.jwi.item.IWordID
import edu.mit.jwi.item.POS
import edu.mit.jwi.item.Pointer
import edu.mit.jwi.morph.WordnetStemmer
import groovy.util.logging.Log4j
import org.junit.Test

@Log4j
class WordnetTest {
    @Test
    public void testDictionary() throws IOException {
        // construct the URL to the Wordnet dictionary directory
        URL url = Resources.getResource('wordnet/dict')

        // construct the dictionary object and open it
        IRAMDictionary dict = new RAMDictionary(url, ILoadPolicy.IMMEDIATE_LOAD)
        dict.open()

        WordnetStemmer stemmer = new WordnetStemmer(dict)
        POS pos = POS.VERB
        List<String> stems = stemmer.findStems('advanced', pos)
        stems.each { String stem ->
            getHypernyms(dict, stem, pos)
        }
    }

    public static void getHypernyms(IDictionary dict, String stem, POS pos) {
        // get the synset
        IIndexWord idxWord = dict.getIndexWord(stem, pos)
        idxWord.wordIDs.each { IWordID wordID ->
            IWord word = dict.getWord(wordID);

            ISynset synset = word.getSynset()
            print "${synset.ID} {"
            synset.words.each { IWord w ->
                print "${w.lemma}, "
            }
            println "}"

            // get the hypernyms
            List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
            // print out each hypernyms id and synonyms
            hypernyms.each { ISynsetID sid ->
                print "$sid {"
                dict.getSynset(sid).words.each { IWord w ->
                    print "${w.lemma}, "
                }
                println "}"
            }
        }
    }
}

