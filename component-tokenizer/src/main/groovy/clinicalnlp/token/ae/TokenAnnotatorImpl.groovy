package clinicalnlp.token.ae

import opennlp.tools.lemmatizer.Lemmatizer
import opennlp.tools.postag.POSTagger
import opennlp.tools.stemmer.Stemmer
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.util.Span
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.resource.ResourceInitializationException

import java.util.regex.Matcher
import java.util.regex.Pattern

class TokenAnnotatorImpl {
    Tokenizer tokenizer
    POSTagger posTagger
    Lemmatizer lemmatizer
    Stemmer stemmer
    Pattern splitPattern
    Class<Annotation> containerType
    Class<Annotation> tokenType

    TokenAnnotatorImpl(Tokenizer tokenizer,
                       POSTagger posTagger,
                       Lemmatizer lemmatizer,
                       Stemmer stemmer,
                       String containerTypeName,
                       String tokenTypeName,
                       String splitPatternStr) {
        this.tokenizer = tokenizer
        this.posTagger = posTagger
        this.lemmatizer = lemmatizer
        this.stemmer = stemmer
        try {
            if (splitPatternStr) {
                this.splitPattern = Pattern.compile(splitPatternStr)
            }
            this.containerType = Class.forName(containerTypeName)
            this.tokenType = Class.forName(tokenTypeName)
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e)
        }
    }

    void process(JCas jcas) {
        Class.forName('clinicalnlp.dsl.DSL')
        jcas.select(type: (this.containerType)).each { Annotation ann ->
            List<Span> tokenSpans = []
            if (this.splitPattern) {
                (tokenizer.tokenizePos(ann.coveredText)).each {
                    tokenSpans.addAll(this.splitSpan(it, ann.coveredText, this.splitPattern))
                }
            }
            else {
                tokenSpans = tokenizer.tokenizePos(ann.coveredText)
            }

            if (this.posTagger) {
//                final List<String> tokenStrings = tokenSpans.collect { ann.coveredText.substring(it.start, it.end) }
//                final String[] posTags = this.posTagger.tag(tokenStrings as String[])
//                tokenSpans.eachWithIndex { Span span, int i ->
//                    jcas.create(type:this.tokenType, begin:ann.begin+span.start, end:ann.begin+span.end, pos:posTags.get(i),
//                        lemma:(this.lemmatizer ? lemmatizer.lemmatize(tokenStrings.get(i), posTags.get(i)): null),
//                        stem:(this.stemmer ? stemmer.stem(tokenStrings.get(i)) : null)
//                    )
//                }
            }
            else {
                tokenSpans.each { Span span ->
                    jcas.create(type:this.tokenType, begin:ann.begin+span.start, end:ann.begin+span.end)
                }
            }
        }
    }

    private Collection<Span> splitSpan(Span span, String text, Pattern splitPattern) {

        List<Span> spans = []

        if (splitPattern == null || (span.length() < 2)) {
            spans << new Span(span.start, span.end)
            return spans
        }

        int subSpanBegin = span.start
        String spanText = text.substring(span.start, span.end)
        Matcher matcher = splitPattern.matcher(spanText)
        while(matcher.find()) {
            int matchStart = matcher.start(0)+span.start
            int matchEnd = matcher.end(0)+span.start
            if (matchStart-subSpanBegin > 0) { spans << new Span(subSpanBegin, matchStart) }
            if (matchEnd-matchStart > 0) { spans << new Span(matchStart, matchEnd) }
            subSpanBegin = matchEnd
        }
        if (span.end-subSpanBegin > 0) {
            spans << new Span(subSpanBegin, span.end)
        }

        return spans
    }
}
