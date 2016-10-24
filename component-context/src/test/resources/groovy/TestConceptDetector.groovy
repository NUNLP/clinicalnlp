import gov.va.queri.types.NamedEntityMention
import gov.va.vinci.leo.sentence.types.Sentence
import org.apache.uima.jcas.JCas

import java.util.regex.Matcher

JCas jcas = (JCas) getProperty('jcas')

Matcher matcher = jcas.documentText =~ /([A-Z].+\.)/
matcher.each {
    Sentence sent = new Sentence(jcas)
    sent.begin = matcher.start(1)
    sent.end = matcher.end(1)
    sent.addToIndexes()
}

matcher = jcas.documentText =~ /(?i)(pneumonia|fever|cough|sepsis|weakness|measles)/
matcher.each {
    NamedEntityMention nem = new NamedEntityMention(jcas)
    nem.begin = matcher.start(1)
    nem.end = matcher.end(1)
    nem.polarity = 1
    nem.addToIndexes()
}



