import gov.va.queri.types.Segment
import org.apache.uima.jcas.JCas

import java.util.regex.Pattern

JCas jcas = (JCas) getBinding().getProperty('jcas')

// Match segment headings
matcher = jcas.documentText =~ ~/(?m)(?s)FINAL DIAGNOSIS:(.+)COMMENT:/
matcher.each {
    jcas.create(type:Segment, begin:matcher.start(1), end:matcher.end(1), code:'FINAL_DIAGNOSIS')
}
matcher = jcas.documentText =~ ~/(?m)(?s)COMMENT:(.+)\Z/
matcher.each {
    jcas.create(type:Segment, begin:matcher.start(1), end:matcher.end(1), code:'COMMENT')
}
