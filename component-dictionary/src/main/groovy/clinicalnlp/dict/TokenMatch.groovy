package clinicalnlp.dict

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class TokenMatch implements Comparable {
    Integer begin
	Integer end
	Float score
	DictEntry value

    @Override
    int compareTo(Object o) {
        if (!o instanceof TokenMatch) {
            throw new IllegalArgumentException()
        }
        TokenMatch other = (TokenMatch)o
        if (this.begin < other.begin) return -1
            if (this.begin > other.begin) return 1
                if (this.end < other.end) return -1
                    if (this.end > other.end) return 1
                        if (this.value < other.value) return -1
                            if (this.value > other.value) return 1
        return 0
    }
}