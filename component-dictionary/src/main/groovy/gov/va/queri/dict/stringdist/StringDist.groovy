package gov.va.queri.dict.stringdist

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

public interface StringDist {
	public void set(final Collection<CharSequence> tokens);
	public Double add(final Collection<CharSequence> tokens);
	public Collection<Match> matches(final Double tolerance);
}
