package clinicalnlp.dict.stringdist

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.Sortable
import groovy.transform.ToString

@Sortable
@Immutable
@ToString(includeNames=true)
@EqualsAndHashCode
class Match {
    Integer begin
    Integer end
    Double score
    String matchString
}

interface StringDist {
	void set(final Collection<CharSequence> tokens);
	Float add(final Collection<CharSequence> tokens);
	Collection<Match> matches(final Float tolerance);
}
