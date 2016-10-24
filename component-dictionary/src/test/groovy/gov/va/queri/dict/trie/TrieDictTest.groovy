package gov.va.queri.dict.trie

import gov.va.queri.dict.DictEntry
import gov.va.queri.dict.DictModel
import gov.va.queri.dict.TokenMatch
import gov.va.queri.dict.stringdist.DynamicStringDist
import gov.va.queri.dict.stringdist.MinEditDist
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@Log4j
class TrieDictTest {
	DictModel<DictEntry> dict;
	Map<Collection<CharSequence>, DictEntry> entries;
	
	@BeforeClass
	static void setupClass() {
		BasicConfigurator.configure()
	}

	
	@Before
	void setup() {
		log.setLevel(Level.INFO)
		
		this.dict = new TrieDictModel<DictEntry>()
		this.entries = [
			['bee']:(new DictEntry(vocab:'V1', code:'C1', canonical:['bee'])),
			['bees']:(new DictEntry(vocab:'V1', code:'C2', canonical:['bees']))
			]		
		this.entries.each { Collection<CharSequence> k, DictEntry v ->
			dict.put(k, v)
		}
	}

    @Test
    public void smokeTest() {
		assert dict.numEntries == entries.size()
		entries.each { k,v ->
			assert dict.get(k) == v
		}
    }
	
	@Test
	public void findMatches() {
		Collection<CharSequence> tokens = new ArrayList<>()
		tokens << 'bee' << 'bees'
		
		DynamicStringDist dist = new MinEditDist()
		TreeSet<TokenMatch> matches = this.dict.matches(tokens, dist, 0.0, false)
		matches.each { log.info it }
		assert matches.size() == 2

		matches = this.dict.matches(tokens, dist, 1.0, false)
		matches.each { log.info it }
		assert matches.size() == 4
	}
}
