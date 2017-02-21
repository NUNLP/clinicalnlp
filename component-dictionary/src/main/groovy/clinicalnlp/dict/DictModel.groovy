package clinicalnlp.dict

interface DictModel {
	
	Integer getNumEntries()
	
	Set<DictEntry> get (final Collection<CharSequence> tokens)
	
	void put (final Collection<CharSequence> tokens, final DictEntry entry)

	void complete()

	TreeSet<TokenMatch> matches (final Collection<CharSequence> tokens,
								 final Float tolerance,
								 final Integer maxRawScore
								 )
}
