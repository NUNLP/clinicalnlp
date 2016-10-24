package clinicalnlp.dict.phrase

import clinicalnlp.dict.DictEntry
import clinicalnlp.dict.DictModel
import clinicalnlp.dict.TokenMatch
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@Log4j
class PhraseDictTest {

	DictModel<DictEntry> dict;
	Map<Collection<CharSequence>, DictEntry> entries;
	
	@BeforeClass
	static void setupClass() {
		BasicConfigurator.configure()
	}
	
	@Before
	void setup() {
		log.setLevel(Level.INFO)
		
		this.dict = new PhraseDictModel<DictEntry>()
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
		
		Set<TokenMatch> matches = this.dict.matches(tokens)
		matches.each { log.info it }
		assert matches.size() == 2		
	}
}
