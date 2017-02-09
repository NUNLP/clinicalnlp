package clinicalnlp.dict

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class TokenMatch<Value extends Comparable<Value>> implements Comparable {
	Integer begin
	Integer end
	Float score
	Value value

    @Override
    int compareTo(Object o) {
        if (this.begin < o.begin) return -1
            if (this.begin > o.begin) return 1
                if (this.end < o.end) return -1
                    if (this.end > o.end) return 1
                        if (this.value < o.value) return -1
                            if (this.value > o.value) return 1
        return 0
    }
}