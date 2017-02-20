package clinicalnlp.dict.automata

import clinicalnlp.dict.DictModel
import clinicalnlp.dict.TokenMatch
import clinicalnlp.dict.stringdist.DynamicStringDist

class LevenshteinAutomatonModel<Value> implements DictModel<Value> {
    @Override
    Integer getNumEntries() {
        return null
    }

    @Override
    Value get(Collection<CharSequence> tokens) {
        return null
    }

    @Override
    void put(Collection<CharSequence> tokens, Value entry) {

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
