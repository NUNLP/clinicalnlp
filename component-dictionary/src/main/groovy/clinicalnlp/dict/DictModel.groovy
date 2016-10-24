package clinicalnlp.dict

import clinicalnlp.dict.stringdist.DynamicStringDist

public interface DictModel<Value> {
	
	public Integer getNumEntries()
	
	public Value get (final Collection<CharSequence> tokens);
	
	public void put (final Collection<CharSequence> tokens, final Value entry);
	
	public TreeSet<TokenMatch> matches (final Collection<CharSequence> tokens);
	
	public TreeSet<TokenMatch> matches (final Collection<CharSequence> tokens,
                                        final DynamicStringDist dist,
                                        final Float tolerance,
                                        final Boolean longestMatch);
}
