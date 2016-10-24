package gov.va.queri.dict

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable
import groovy.transform.ToString

@ToString(includeNames=true)
@EqualsAndHashCode
@Sortable(includes = ['vocab', 'code'])
class DictEntry {
	String vocab
	String code
	Collection<CharSequence> canonical
//	Collection<Collection<CharSequence>> variants = new ArrayList<>()
//	Map<String, String> attrs = new HashMap<>()
}
