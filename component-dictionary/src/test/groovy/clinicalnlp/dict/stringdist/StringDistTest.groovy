package clinicalnlp.dict.stringdist

import info.debatty.java.stringsimilarity.Levenshtein
import org.junit.Test

class StringDistTest {

	@Test
	public void test() {
		Levenshtein l = new Levenshtein();
		println l.distance('My string', 'My %tring')
		println l.distance('My string', 'My %%tring')
		println l.distance('My string', 'My %%%tring')
	}
}
