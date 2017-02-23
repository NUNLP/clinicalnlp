package clinicalnlp.dict

import com.mifmif.common.regex.Generex
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.util.Span

class DictModelFactory {

    static public CharSequence TOKEN_SEP = ' '

    static DictModel make(final String dictModelType,
                          final AbstractionSchema schema,
                          final Tokenizer tokenizer,
                          final Boolean caseInsensitive) {
        DictModel model = Class.forName(dictModelType).newInstance()
        schema.object_values.each { ObjectValue objVal ->
            DictEntry entry = new DictEntry()
            entry.vocab = objVal.vocabulary
            entry.code = objVal.vocabulary_code
            entry.canonical = objVal.value
            model.put(tokenize(entry.canonical, tokenizer, caseInsensitive), entry)
            objVal.object_value_variants.each { ObjectValueVariant variant ->
                if (variant.regex == true) {
                    Generex generex = new Generex(variant.value)
                    generex.getAllMatchedStrings().each {
                        model.put(tokenize(it, tokenizer, caseInsensitive), entry)
                    }
                }
                else {
                    model.put(tokenize(variant.value, tokenizer, caseInsensitive), entry)
                }
            }

        }
        model.complete()
        return model
    }

    static Collection<CharSequence> tokenize(String phrase, TokenizerME tokenizer,
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

    static String join(final Collection<CharSequence> tokens, boolean wrap = false) {
        return join(tokens as CharSequence[], wrap)
    }

    static String join(final CharSequence[] tokens, boolean wrap = false) {
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

