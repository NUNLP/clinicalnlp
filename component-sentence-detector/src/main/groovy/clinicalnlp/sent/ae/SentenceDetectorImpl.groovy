package clinicalnlp.sent.ae

import com.google.common.io.Resources
import clinicalnlp.types.Segment
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.util.Span
import org.apache.commons.lang.math.IntRange
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.regex.Matcher
import java.util.regex.Pattern

import static clinicalnlp.dsl.UIMA_DSL.between

@Log4j
class SentenceDetectorImpl {

    private SentenceDetectorME sentDetect;
    private Pattern splitPattern;
    private int spanSize;
    private String[] anchorTypes;
    private Script segSelectScript;

    public SentenceDetectorImpl(SentenceDetectorME sentDetect, String splitPatternStr, String segScriptFile,
                                Integer spanSize, String[] anchorTypes) {
        this.sentDetect = sentDetect
        this.spanSize = spanSize
        this.anchorTypes = anchorTypes
        if (splitPatternStr) { this.splitPattern = Pattern.compile(splitPatternStr) }

        if (segScriptFile) {
            CompilerConfiguration config = new CompilerConfiguration()
            config.setScriptBaseClass('clinicalnlp.dsl.UIMA_DSL')
            GroovyShell shell = new GroovyShell(config)
            def scriptContents = Resources.toString(Resources.getResource(segScriptFile),
                    org.apache.commons.io.Charsets.UTF_8)
            this.segSelectScript = shell.parse(scriptContents)
        }
    }

    public void process(JCas jcas) {
        Collection<Segment> segs = null;
        if (this.segSelectScript) {
            this.segSelectScript.setProperty('jcas', jcas)
            segs = (Collection<Segment>)this.segSelectScript.run()
        }
        if (segs == null) {
            segs = jcas.select(type:Segment)
        }

        segs.each { Segment seg ->
            if (this.anchorTypes != null) {
                this.anchorTypes.each { String type ->
                    jcas.select(type:Class.forName(type)).each { Annotation ann ->
                        Span span = this.anchoredSentence(jcas.documentText, ann.begin, ann.end)
                        if (span) {
                            this.splitAndAdjustSpan(span, seg, splitPattern).each { Span subspan ->
                                if (subspan.start <= ann.begin && subspan.end >= ann.end) {
                                    if (jcas.select(type:Sentence, filter:between(subspan.start, subspan.end)).size() == 0) {
                                        jcas.create(type:Sentence, begin:subspan.start, end:subspan.end)
                                    }
                                }
                            }
                        }
                    }
                }
                // filter out embedded sentences
                jcas.removeCovered(
                        anns:jcas.select(type:Sentence),
                        types:[Sentence]
                )
            }
            else {
                Span[] spans = sentDetect.sentPosDetect(seg.coveredText)
                spans.each { Span span ->
                    this.splitAndAdjustSpan(span, seg, splitPattern).each { Span subspan ->
                        jcas.create(type:Sentence, begin:subspan.start, end:subspan.end)
                    }
                }
            }
        }
    }

    private Collection<Span> splitAndAdjustSpan(Span span, Annotation cover, Pattern splitPattern) {
        List<Span> spans = []

        String coveredText = cover.coveredText
        if (splitPattern == null) {
            Tuple trimOffsets = trimOffsets(coveredText.substring(span.start, span.end))
            addSpan(spans, new Span(cover.begin+span.start+trimOffsets[0], cover.begin+span.end-trimOffsets[1]))
            return spans
        }
        int offset = cover.begin + span.start
        Matcher matcher = splitPattern.matcher(coveredText.substring(span.start, span.end))
        while(matcher.find()) {
            int newOffset = cover.begin + span.start + matcher.end(0)
            Tuple trimOffsets = trimOffsets(coveredText.substring(offset-cover.begin, newOffset-cover.begin))
            addSpan(spans, new Span(offset+trimOffsets[0], newOffset-trimOffsets[1]))
            offset = newOffset
        }
        Tuple trimOffsets = trimOffsets(coveredText.substring(offset-cover.begin, span.end))
        addSpan(spans, new Span(offset+trimOffsets[0], cover.begin+span.end-trimOffsets[1]))

        return spans;
    }

    private void addSpan(Collection<Span> spans, Span span) {
        if (span.start != span.end) {
            spans.add(span)
        }
    }

    private Tuple trimOffsets(String text) {
        int beginTrimOffset, endTrimOffset = 0
        if (text.isAllWhitespace()) {
            return [0, text.length()]
        }
        for (c in text) {
            if (c.isAllWhitespace()) {
                beginTrimOffset += 1
            }
            else break
        }
        for (c in text.reverse()) {
            if (c.isAllWhitespace()) {
                endTrimOffset += 1
            }
            else break
        }
        return [beginTrimOffset, endTrimOffset]
    }

    private Span anchoredSentence(String text, int anchorStart, int anchorEnd) {
        IntRange validRange = new IntRange(0, text.length());
        if(!validRange.containsInteger(anchorStart) ||
                !validRange.containsInteger(anchorEnd) ||
                (anchorStart > anchorEnd)) {
            throw new IllegalArgumentException('Invalid anchor indices,' +
                    'start: ' + anchorStart +
                    ' end: ' + anchorEnd +
                    ' size: ' + text.length());
        }
        //Get the spans in the text
        int start = (anchorStart >= spanSize)? anchorStart - spanSize : 0;
        int end   = (anchorEnd + spanSize < text.length())?
                anchorEnd + spanSize : text.length();
        Span[] spans = this.sentDetect.sentPosDetect(text.substring(start, end))
        Span span = null, anchorSpan = new Span(anchorStart - start, anchorEnd - start);
        for(Span s : spans) {
            if(s.contains(anchorSpan)) {
                span = s;
                break;
            }
        }
        //If there is a span then adjust the start and end based on reference to text.
        if(span != null) {
            span = new Span(start + span.getStart(), start + span.getEnd());
        }
        return span;
    }
}
