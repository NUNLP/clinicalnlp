package clinicalnlp.pattern

import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation

import static org.apache.uima.fit.util.JCasUtil.selectCovered


class AnnotationSequenceGenerator implements Iterable<List<? extends Annotation>> {
    Annotation span
    Collection<Class<? extends  Annotation>> types

    AnnotationSequenceGenerator(Annotation span, Collection<Class<? extends  Annotation>> types) {
        this.span = span
        this.types = types
    }

    @Override
    Iterator<List<? extends Annotation>> iterator() {
        return new AnnotationSequenceIterator()
    }

    /**
     * Iterator class
     */
    private class AnnotationSequenceIterator implements Iterator<List<? extends Annotation>> {
        Map<Integer, List<? extends Annotation>> textIdxToAnnMap = [:]
        Stack<Tuple> choicePoints = []
        List<? extends Annotation> sequence

        AnnotationSequenceIterator() {
            // create a map from text indices to annotations
            Annotation span = AnnotationSequenceGenerator.this.span
            Collection<Class<? extends  Annotation>> types = AnnotationSequenceGenerator.this.types
            JCas jcas = span.getCAS().getJCas()
            types.each { Class<? extends Annotation> type ->
                selectCovered(jcas, type, span).each { Annotation ann ->
                    List<Annotation> anns = this.textIdxToAnnMap.get(ann.begin)
                    if (anns != null) {
                        anns.add(ann)
                    }
                    else {
                        this.textIdxToAnnMap.put(ann.begin, [ann] as List)
                    }
                }
            }

            // initialize depth-first search
            this.choicePoints.push(new Tuple(0, 0, []))
            this.genNextSequence()
        }

        @Override
        boolean hasNext() {
            return this.sequence != null
        }

        @Override
        List<? extends Annotation> next() {
            List<? extends Annotation> seq = this.sequence
            this.genNextSequence()
            return seq
        }

        private List<? extends Annotation> genNextSequence() {
            JCas jcas = span.getCAS().getJCas()
            while (!this.choicePoints.empty()) {
                Tuple choicePoint = choicePoints.pop()
                int textIdx = choicePoint[0]
                int candIdx = choicePoint[1]
                List currSeq = choicePoint[2]

                // check for completion
                if (textIdx > jcas.documentText.length()) {
                    this.sequence = currSeq
                    return
                }
                // look for annotations at the current text index
                List<? extends Annotation> candidates = this.textIdxToAnnMap.get(textIdx + span.begin)
                // if there are no candidates at this index, increment text index and continue
                if (!candidates) {
                    choicePoints.push(new Tuple(textIdx + 1, 0, currSeq))
                    continue
                }
                // if there is a candidate at this index, push its contribution onto the stack,
                // and move on to the next open index
                else if (candIdx < candidates.size()) {
                    Annotation candidate = candidates[candIdx]
                    choicePoints.push(new Tuple(textIdx, candIdx + 1, currSeq.collect()))
                    choicePoints.push(new Tuple(textIdx + (candidate.coveredText.length() ?: 1), 0, (currSeq << candidate)))
                }
            }

            // no more choices to explore
            this.sequence = null
        }
    }
}
