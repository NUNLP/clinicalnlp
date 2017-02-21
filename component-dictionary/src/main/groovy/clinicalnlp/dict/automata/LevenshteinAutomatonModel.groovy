package clinicalnlp.dict.automata

import clinicalnlp.dict.DictEntry
import clinicalnlp.dict.DictModel
import clinicalnlp.dict.DictModelFactory
import clinicalnlp.dict.TokenMatch
import com.github.liblevenshtein.transducer.Candidate
import com.github.liblevenshtein.transducer.ITransducer
import com.github.liblevenshtein.transducer.factory.TransducerBuilder

class LevenshteinAutomatonModel implements DictModel {
    Integer maxEntryLength = 0
    final Map<String, Set<DictEntry>> entries = new TreeMap<>()
//    final SortedDawg sortedDawg = new SortedDawg()
    ITransducer transducer

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
        maxEntryLength = (tokens.size() > maxEntryLength ? tokens.size() : maxEntryLength)
        String term = DictModelFactory.join(tokens)
        if (this.entries[term] == null) {
            this.entries[term] = []
        }
        this.entries[term] << entry
    }

    @Override
    void complete() {
//        sortedDawg.addAll(this.entries.keySet())
        TransducerBuilder builder = new TransducerBuilder()
        builder.dictionary(this.entries.keySet())
        transducer = builder.build()
    }

    @Override
    TreeSet<TokenMatch> matches(List<CharSequence> tokens,
                                Float tolerance,
                                Integer maxDistance) {
        Set<TokenMatch> matches = new TreeSet<>()
        for (int i = 0; i < tokens.size(); i++) {
            for (int j = 1; j <= maxEntryLength; j++) {
                if (i + j > tokens.size()) {
                    break
                }
                String token$string = DictModelFactory.join(tokens.subList(i, i+j))
                this.transducer.transduce(token$string, maxDistance).each { Candidate candidate ->
                    Float normScore = candidate.distance()/token$string.length()
                    if (normScore <= tolerance) {
                        this.entries[candidate.term()].each { DictEntry dictEntry ->
                            matches.add(new TokenMatch(begin:i, end:i+j-1, score:normScore, value:dictEntry))
                        }
                    }
                }
            }
        }
        return matches
    }
}