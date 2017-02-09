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
}

interface StringDist {
	void set(final Collection<CharSequence> tokens);
	Double add(final Collection<CharSequence> tokens);
	Collection<Match> matches(final Double tolerance);
}
