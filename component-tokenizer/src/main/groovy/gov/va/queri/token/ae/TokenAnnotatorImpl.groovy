package gov.va.queri.token.ae

import gov.va.queri.types.Token
import opennlp.tools.lemmatizer.SimpleLemmatizer
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
    Tokenizer tokenizer;
    POSTagger posTagger;
    SimpleLemmatizer lemmatizer;
    Stemmer stemmer;
    Pattern splitPattern;
    Class<Annotation> containerType;

    public TokenAnnotatorImpl(Tokenizer tokenizer,
                              POSTagger posTagger,
                              SimpleLemmatizer lemmatizer,
                              Stemmer stemmer,
                              String containerTypeName,
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
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e)
        }
    }

    public void process(JCas jcas) {
        jcas.select(type: (this.containerType)).each { Annotation ann ->
            List<Span> tokenSpans = []

            if (this.splitPattern) {
                (tokenizer.tokenizePos(ann.coveredText)).each {
                    tokenSpans.addAll(this.splitSpan(it, ann, this.splitPattern))
                }
            }
            else {
                tokenSpans = tokenizer.tokenizePos(ann.coveredText)
            }

            if (this.posTagger) {
                final List<String> tokenStrings = tokenSpans.collect { ann.coveredText.substring(it.start, it.end) }
                final List<String> posTags = this.posTagger.tag(tokenStrings)
                tokenSpans.eachWithIndex { Span span, int i ->
                    jcas.create(type:Token, begin:ann.begin+span.start, end:ann.begin+span.end, pos:posTags.get(i),
                        lemma:(this.lemmatizer ? lemmatizer.lemmatize(tokenStrings.get(i), posTags.get(i)): null),
                        stem:(this.stemmer ? stemmer.stem(tokenStrings.get(i)) : null)
                    )
                }
            }
            else {
                tokenSpans.each { Span span ->
                    jcas.create(type:Token, begin:ann.begin+span.start, end:ann.begin+span.end)
                }
            }
        }
    }

    private Collection<Span> splitSpan(Span span, Annotation ann, Pattern splitPattern) {
        List<Span> spans = []

        String text = ann.coveredText
        int beginOffset = ann.begin + span.start
        int endOffset = ann.begin+span.end

        if (splitPattern == null) {
            spans << new Span(beginOffset, endOffset)
            return spans
        }
        Matcher matcher = splitPattern.matcher(text.substring(span.start, span.end))
        while(matcher.find()) {
            spans << new Span(beginOffset, beginOffset+matcher.start(0))
            spans << new Span(beginOffset+matcher.start(0), beginOffset+matcher.end(0))
            beginOffset = beginOffset+matcher.end(0)
        }
        spans << new Span(beginOffset, ann.begin+span.end)

        return spans;
    }
}
