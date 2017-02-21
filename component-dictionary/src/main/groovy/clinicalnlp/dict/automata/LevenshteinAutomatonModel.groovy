package clinicalnlp.dict.automata

import clinicalnlp.dict.DictEntry
import clinicalnlp.dict.DictModel
import clinicalnlp.dict.DictModelFactory
import clinicalnlp.dict.TokenMatch
import clinicalnlp.dict.stringdist.DynamicStringDist
import com.github.liblevenshtein.collection.dictionary.SortedDawg

class LevenshteinAutomatonModel implements DictModel {
    final Map<String, Set<DictEntry>> entries = new TreeMap<>()
    final SortedDawg sortedDawg = new SortedDawg()

    @Override
    Integer getNumEntries() {
        return this.entries.size()
    }

    @Override
    Set<DictEntry> get(Collection<CharSequence> tokens) {
        return this.entries[DictModelFactory.join(tokens)]
    }

    @Override
    void put(Collection<CharSequence> tokens, DictEntry entry) {
        String term = DictModelFactory.join(tokens)
        if (this.entries[term] == null) {
            this.entries[term] = []
        }
        this.entries[term] << entry
    }

    @Override
    void complete() {
        sortedDawg.addAll(this.entries.keySet())
    }

    @Override
    TreeSet<TokenMatch> matches(Collection<CharSequence> tokens,
                                Float tolerance,
                                Integer maxRawScore) {
        return null
    }
}
