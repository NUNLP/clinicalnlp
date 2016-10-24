package gov.va.queri.dict.phrase

import com.wcohen.ss.JaroWinkler
import com.wcohen.ss.SoftTFIDF
import com.wcohen.ss.api.Tokenizer
import com.wcohen.ss.tokens.SimpleTokenizer
import gov.va.queri.dict.DictEntry
import gov.va.queri.dict.DictModel
import gov.va.queri.dict.DictModelFactory
import gov.va.queri.dict.TokenMatch
import gov.va.queri.dict.stringdist.DynamicStringDist
import groovy.util.logging.Log4j

@Log4j
public class PhraseDictModel<Value> implements DictModel<Value> {
		
	private Map<Collection<CharSequence>, Value> entries = new HashMap<>()
	private gov.va.queri.dict.phrase.PhraseTree phrases = new gov.va.queri.dict.phrase.PhraseTree()
	private Integer numEntries = 0
	
	@Override
	public Integer getNumEntries() { return numEntries; }
	
	@Override
	public DictEntry get(final Collection<CharSequence> tokens) {
		return this.entries.get(DictModelFactory.join(tokens))
	}
	
	@Override
	public void put (final Collection<CharSequence> tokens, final Value entry) {
		this.phrases.addPhrase(tokens as String[])
		this.entries.put(DictModelFactory.join(tokens), entry)
		this.numEntries++
	}
		
	@Override
	public TreeSet<TokenMatch> matches(final Collection<CharSequence> tokens) {

		// TODO: doesn't appear to be working proerply
		Set<TokenMatch> matches = new TreeSet<>()
		
		for (int i = 0; i < tokens.size(); i++) {
			String[] tokensToEnd = tokens[i, tokens.size() - 1]
			Integer endMatchPosition = phrases.getLongestMatch(tokensToEnd)
			if (endMatchPosition != null) {
				String[] matchedTokens = Arrays.copyOfRange(tokensToEnd, 0, endMatchPosition)
				matches << new TokenMatch(
					begin:i,
					end:(i+endMatchPosition),
					value:entries.get(DictModelFactory.join(matchedTokens))
					)
			}
		}
		
		return matches;
	}
	
	@Override
	public TreeSet<TokenMatch> matches(final Collection<CharSequence> tokens,
								   final DynamicStringDist dist,
								   final Float tolerance,
								   final Boolean longestMatch) {
		Set<TokenMatch> matches = new TreeSet<>()
		
		Tokenizer tokenizer = new SimpleTokenizer(false,true);
		SoftTFIDF distance = new SoftTFIDF(tokenizer, new JaroWinkler(), tolerance);
		
		return matches;
	}	
}
