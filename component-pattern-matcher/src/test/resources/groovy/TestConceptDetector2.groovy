import clinicalnlp.types.NamedEntityMention
import org.apache.uima.jcas.JCas
import java.util.regex.Matcher


JCas jcas = (JCas) getProperty('jcas')

Matcher matcher = jcas.documentText =~ /(?i)(Hyperlipidemia|Diabetes mellitus|Hypertension|Benign prostatic hyperplasia)/
matcher.each {
    NamedEntityMention nem = new NamedEntityMention(jcas)
    nem.begin = matcher.start(1)
    nem.end = matcher.end(1)
    nem.addToIndexes()
} 

