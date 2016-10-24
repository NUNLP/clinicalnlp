package clinicalnlp.dict.stringdist

import clinicalnlp.dict.DictModelFactory
import groovy.transform.ToString
import groovy.util.logging.Log4j

@Log4j
public class MinEditDist implements DynamicStringDist {
		
	// ------------------------------------------------------------------------
	// Inner Classes
	// ------------------------------------------------------------------------

	@ToString
	private static class BackPtr implements Comparable {
		Double score
		Integer startIdx
		
		@Override
		public int compareTo(Object other) {
			return Double.compare(score, ((BackPtr)other).score) 
		}
	}

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
		
	CharSequence text
	StringBuilder prefix = new StringBuilder()
	Stack<BackPtr[]> rows = new Stack<>()
	Map<Integer, Integer> str2tok = new TreeMap<>()
	Closure costFunction = { Character c1, Character c2 -> 
		// TODO: function set in API method.
		// TODO: Use methods from java Character API: https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html
		if (c1 == null && c2 == null) { throw new IllegalArgumentException('at least one character must be non-null') }
		if (c1 == null) {
			// insertion
			return 1.0
		}
		else if (c2 == null) {
			// deletion
			return 1.0
		}
		else {
			// substitution
			if (c1 == c2) { return 0.0 }
			else { return 1.0 }
		}
	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	@Override
	public void set(final Collection<CharSequence> tokens) {
		if (tokens == null) { throw new NullPointerException() }
		if (tokens.size() == 0) { throw new IllegalArgumentException("must have at least one token to match") }

		this.text = DictModelFactory.join(tokens, true)

		BackPtr[] bottomRow = new BackPtr[this.text.length()]
		int score = 0
		int tokIdx = -1
		for (int i = 0; i < text.size(); i++) {
			if (text[i] == DictModelFactory.TOKEN_SEP) { score = 0; tokIdx++ }
			else { this.str2tok[i] = tokIdx; }
			bottomRow[i] = new BackPtr(startIdx:i, score:score++)
		}
		rows.push(bottomRow)
	}
	
	/**
	 * 
	 */
	@Override
	public Double push(final char c) {
		prefix.append(c)
		BackPtr[] toprow = rows.peek()
		BackPtr[] newrow = new BackPtr[toprow.length]
		newrow[0] = new BackPtr(score:(toprow[0].score + 1), startIdx:0)
		for (int i = 1; i < newrow.length; i++) {
			def bptrs = [
				// substitution
				new BackPtr(startIdx:toprow[i-1].startIdx, 
					score:toprow[i-1].score + this.costFunction(text.charAt(i), c)),
				// insertion
				new BackPtr(startIdx:toprow[i].startIdx, 
					score:(toprow[i].score + this.costFunction(null, c))),
				// deletion
				new BackPtr(startIdx:newrow[i-1].startIdx, 
					score:(newrow[i-1].score + this.costFunction(text.charAt(i), null)))
				]
			newrow[i] = GroovyCollections.min(bptrs)
		}
		rows.push(newrow)
		return GroovyCollections.min(newrow).score
	}

	/**
	 * 
	 */
	@Override
	public void pop() {
		if (prefix.length() == 0) { return }
		prefix.deleteCharAt(prefix.length()-1)
		this.rows.pop()
	}
	
	/**
	 * 
	 */
	@Override
	public Collection<Match> matches(final Double tolerance) {
		Collection<Integer[]> matches = new ArrayList<>()
		BackPtr[] toprow = rows.peek()
		toprow.eachWithIndex { BackPtr bptr, Integer endIndex ->
			if (bptr.score <= tolerance && (endIndex+1 == text.size() || text.charAt(endIndex+1) == DictModelFactory.TOKEN_SEP)) {
				matches << new Match(
                        begin:(text.charAt(bptr.startIdx) == DictModelFactory.TOKEN_SEP ?
                                this.str2tok[bptr.startIdx+1] : this.str2tok[bptr.startIdx]),
                        end:(text.charAt(endIndex) == DictModelFactory.TOKEN_SEP ?
                                this.str2tok[endIndex-1] : this.str2tok[endIndex]),
                        score:bptr.score
                )
			}
		}
		return matches
	}

	@Override
	public Double add(Collection<CharSequence> tokens) {
		if (tokens == null) { throw new NullPointerException() }
		if (tokens.size() == 0) { throw new IllegalArgumentException("must have at least one token to match") }

		CharSequence text = DictModelFactory.join(tokens, true)

		text.each { char c ->
			this.push(c)
		}
		return GroovyCollections.min(this.rows.peek()).score
	}
}
