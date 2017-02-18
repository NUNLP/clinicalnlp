package clinicalnlp.dict

import clinicalnlp.dict.stringdist.DynamicStringDist

interface DictModel<Value> {
	
	Integer getNumEntries()
	
	Value get (final Collection<CharSequence> tokens)
	
	void put (final Collection<CharSequence> tokens, final Value entry)
	
	TreeSet<TokenMatch> matches (final Collection<CharSequence> tokens)
	
	TreeSet<TokenMatch> matches (final Collection<CharSequence> tokens,
								 final DynamicStringDist dist,
								 final Float tolerance,
								 final Integer maxRawScore
								 )
}
