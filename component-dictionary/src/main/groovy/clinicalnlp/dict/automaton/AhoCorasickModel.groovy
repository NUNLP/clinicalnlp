package clinicalnlp.dict.automaton

import clinicalnlp.dict.DictEntry
import clinicalnlp.dict.DictModel
import clinicalnlp.dict.DictModelFactory
import clinicalnlp.dict.TokenMatch
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie
import org.apache.commons.collections.map.HashedMap

class AhoCorasickModel implements DictModel {

    AhoCorasickDoubleArrayTrie<String> ahoCorasickTrie = new AhoCorasickDoubleArrayTrie<String>()
    final Map<String, Set<DictEntry>> entries = new HashMap<>()

    @Override
    Integer getNumEntries() {
        return entries.size()
    }

    @Override
    Set<DictEntry> get(Collection<CharSequence> tokens) {
        return entries[tokens]
    }

    @Override
    void put(Collection<CharSequence> tokens, DictEntry entry) {
        String term = DictModelFactory.join(tokens)
        if (this.entries[term] == null) {
            this.entries[term] = []
        }
        this.entries[term] << entry    }

    @Override
    void complete() {
        ahoCorasickTrie.build(entries)
    }

    @Override
    TreeSet<TokenMatch> matches(List<CharSequence> tokens,
                                Float tolerance,
                                Integer maxDistance) {
        Set<TokenMatch> matches = new TreeSet<>()
        Map<Integer, Integer> tokenOffsets = new HashedMap<>()
        String text = ''
        Integer offset = 0
        Integer tokenCount = 0
        for (CharSequence token : tokens) {
            tokenOffsets[offset] = tokenCount
            offset += token.length()+1 ; tokenCount++
            text += token; text += DictModelFactory.TOKEN_SEP
        }
        text.stripMargin(DictModelFactory.TOKEN_SEP)
        List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> hits = ahoCorasickTrie.parseText(text)
        hits.each{ AhoCorasickDoubleArrayTrie.Hit hit ->
            hit.value.each { DictEntry dictEntry ->
                println "Hit: ${hit}, matches string: ${text.substring(hit.begin, hit.end)}"
//                matches.add(new TokenMatch(begin:tokenOffsets[hit.begin],
//                    end:(tokenOffsets[hit.end+1] ? tokenOffsets[hit.end+1]-1 : tokens.size()-1),
//                    score:0,
//                    value:dictEntry))
            }
        }
        return matches
    }
}