package clinicalnlp.dict

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames=true)
@EqualsAndHashCode
class ObjectValueVariant {
	String value;
}

@ToString(includeNames=true)
@EqualsAndHashCode
class ObjectValue {
	String value;
	Map<String, String> properties;
	String vocabulary_code;
	String vocabulary;
	String vocabulary_version
	Collection<ObjectValueVariant> object_value_variants;
}

@ToString(includeNames=true)
@EqualsAndHashCode
class AbstractionSchema {
	String predicate;
	String display_name;
	String abstractor_object_type;
	String preferred_name;
	Collection<String> predicate_variants;
	Collection<ObjectValue> object_values;
}
