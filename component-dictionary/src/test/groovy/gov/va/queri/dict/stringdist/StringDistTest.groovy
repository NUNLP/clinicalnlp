package gov.va.queri.dict.stringdist

import info.debatty.java.stringsimilarity.Levenshtein;

import static org.junit.Assert.*

import org.junit.Ignore
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
