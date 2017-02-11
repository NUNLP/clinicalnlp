package clinicalnlp.dict

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable
import groovy.transform.ToString

@ToString(includeNames=true)
@EqualsAndHashCode
@Sortable(includes = ['vocab', 'code'])
class DictEntry {
	String canonical
	String vocab
	String code
}
