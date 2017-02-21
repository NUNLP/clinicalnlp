package clinicalnlp.dict.automata

import clinicalnlp.dict.DictEntry
import clinicalnlp.dict.DictModel
import clinicalnlp.dict.TokenMatch
import clinicalnlp.dict.stringdist.DynamicStringDist

class LevenshteinAutomatonModel implements DictModel {
    @Override
    Integer getNumEntries() {
        return null
    }

    @Override
    DictEntry get(Collection<CharSequence> tokens) {
        return null
    }

    @Override
    void put(Collection<CharSequence> tokens, DictEntry entry) {

    }

    @Override
    TreeSet<TokenMatch> matches(Collection<CharSequence> tokens) {
        return null
    }

    @Override
    TreeSet<TokenMatch> matches(Collection<CharSequence> tokens, DynamicStringDist dist, Float tolerance, Integer maxRawScore) {
        return null
    }
}
