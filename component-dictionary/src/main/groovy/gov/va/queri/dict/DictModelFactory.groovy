package gov.va.queri.dict

import gov.va.queri.dict.phrase.PhraseDictModel
import gov.va.queri.dict.trie.TrieDictModel
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.util.Span

class DictModelFactory {

    static public CharSequence TOKEN_SEP = ' '

    public static String DICT_MODEL_TYPE_PHRASE = "Phrase"
    public static String DICT_MODEL_TYPE_TRIE = "Trie"

    static public DictModel make(final String dictModelType,
                                 final AbstractionSchema schema,
                                 final Tokenizer tokenizer,
                                 final Boolean caseInsensitive) {

        DictModel model;

        switch (dictModelType) {
            case DICT_MODEL_TYPE_TRIE:
                model = new TrieDictModel<DictEntry>()
                break;

            case DICT_MODEL_TYPE_PHRASE:
                model = new PhraseDictModel<DictEntry>()
                break
        }

        schema.object_values.each { ObjectValue objVal ->
            DictEntry entry = new DictEntry()
            entry.vocab = objVal.vocabulary
            entry.code = objVal.vocabulary_code
            entry.canonical = tokenize(objVal.value, tokenizer, caseInsensitive)
            model.put(entry.canonical, entry)
            objVal.object_value_variants.each { ObjectValueVariant variant ->
                model.put(tokenize(variant.value, tokenizer, caseInsensitive), entry)
            }

        }
        return model
    }

    static public Collection<CharSequence> tokenize(String phrase, TokenizerME tokenizer,
                                                    Boolean caseInsensitive) {
        Collection<Sequence> tokens = new ArrayList<>()
        Span[] tokenSpans = tokenizer.tokenizePos(phrase)
        tokenSpans.each { Span span ->
            tokens << (caseInsensitive ?
                    phrase.substring(span.getStart(), span.getEnd()).toLowerCase() :
                    phrase.substring(span.getStart(), span.getEnd()))
        }
        return tokens
    }

    static public String join(final Collection<CharSequence> tokens, boolean wrap = false) {
        return join(tokens as CharSequence[], wrap)
    }

    static public String join(final CharSequence[] tokens, boolean wrap = false) {
        StringJoiner joiner;
        if (wrap == true) {
            joiner = new StringJoiner(TOKEN_SEP, TOKEN_SEP, TOKEN_SEP)
        }
        else {
            joiner = new StringJoiner(TOKEN_SEP)
        }
        tokens.each { joiner.add(it) }
        return joiner.toString()
    }
}

