package clinicalnlp.dsl

import groovy.util.logging.Log4j
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.regex.Matcher
import java.util.regex.Pattern

import static org.apache.uima.fit.util.JCasUtil.selectCovered

@Log4j
class AnnotationPattern {
    // -----------------------------------------------------------------------------------------------------------------
    // AnnotationMatchResult class
    // -----------------------------------------------------------------------------------------------------------------
    class AnnotationMatcher implements Iterator {

        Map<Integer, List<Annotation>> textIdxToAnnMap = [:]
        Queue<Tuple2<Matcher, Map>> matchers = [] as Queue
        Iterator matchIter;
        private Matcher matcher;
        private Map<Integer, Annotation> annMap;

        /**
         * Constructor
         */
        AnnotationMatcher(Annotation annotation, Integer maxCount) {

            // create a map from text indices to annotations
            JCas jcas = annotation.getCAS().getJCas()
            (annClassToCodeMap.keySet()).each {
                selectCovered(jcas, it, annotation.begin, annotation.end).each {
                    List<Annotation> anns = this.textIdxToAnnMap.get(it.begin)
                    if (anns != null) {
                        anns.add(it)
                    }
                    else {
                        this.textIdxToAnnMap.put(it.begin, [it] as List)
                    }
                }
            }

            // generate the annotation matchers spanning the given annotation
            this.matchers.addAll(this.genMatchers(annotation, maxCount))

            // create a match iterator
            def next = this.matchers.remove()
            this.matcher = next[0]
            this.annMap = next[1]
            this.matchIter = StringGroovyMethods.iterator(this.matcher)
        }

        @Override
        boolean hasNext() {
            if (this.matchIter.hasNext()) {
                return true;
            }
            while (this.matchers.peek()) {
                Tuple2 next = this.matchers.remove()
                this.matcher = next[0]
                this.annMap = next[1]
                this.matchIter = StringGroovyMethods.iterator(this.matcher)
                if (this.matchIter.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        Object next() {
            this.matchIter.next()
            Binding binding = new Binding()
            for (String name : AnnotationPattern.this.groupNames) {
                String matchedText = matcher.group(name)
                ////println ("Group ${name}: ${matchedText}")
                if (matchedText) {
                    List<Annotation> matchedAnns = []
                    for (int i = 0; i < matchedText.length(); i++) {
                        Character c = matchedText[i]
                        int j = AnnotationPattern.this.typeCodes.indexOf(c)
                        if (j != -1) {
                            matchedAnns << this.annMap[matcher.start(name)+i]
                        }
                    }
                    binding.setVariable(name, matchedAnns)
                }
            }
            return binding;
        }

        @Override
        void remove() {
            this.matchIter.remove()
        }

        // -----------------------------------------------------------------------------------------------------------------
        // generate a set of match strings from a sequence of annotations covering a spanning annotation
        // -- uses depth-first backtracking algorithm to search for sequences
        // -- also generates a map from annotation positions in match string to corresponding actual annotations
        // -----------------------------------------------------------------------------------------------------------------
        private List<Tuple2<Matcher, Map<Integer, Annotation>>> genMatchers(Annotation spanningAnn, int maxCount) {
            List<Tuple2<Matcher, Map<Integer, Annotation>>> matcherBindingPairs = []

            String text = spanningAnn.coveredText

            Stack<Tuple> choicePoints = []
            choicePoints.push(new Tuple(0,0,'', null))
            int count = 0
            while (!choicePoints.empty() && count < maxCount) {

                // pop off the top choice point
                Tuple choicePoint = choicePoints.pop()
                int choiceTextIdx = choicePoint[0]
                int choiceAnnIdx = choicePoint[1]
                String choiceMappedText = choicePoint[2]
                Annotation choiceMappedAnn = choicePoint[3]

                // check to see if we've gone past the end of the source text;
                // if so, read off the match string and continue looking for more
                // match strings using depth-first search
                if (choiceTextIdx > text.length()) {
                    String matchString = ''
                    Map<Integer, Annotation> matchStringBinding = [:]
                    choicePoints.each { Tuple cp ->
                        // assemble the map to the annotations that generated the match string
                        if(cp[3]) { matchStringBinding[matchString.length()] = cp[3] }
                        // concatenate the match string pieces
                        matchString += cp[2]
                    }
                    // add in the contribution from the last choice point, which has
                    // been popped off the stack and needs to be included here
                    matchStringBinding[matchString.length()] = choiceMappedAnn
                    matchString += choiceMappedText
                    ////println "Match string: ${matchString}"

                    // create a matcher, and add it and its binding map to the collection
                    matcherBindingPairs << new Tuple2(this.pattern.matcher(matchString), matchStringBinding)

                    // increment counter and continue
                    count++
                    continue;
                }

                // Look for annotations at the current text index
                List<Annotation> candidateAnns = this.textIdxToAnnMap.get(choiceTextIdx+spanningAnn.begin)
                // 1. If we found no candidates at this index, increment text index and continue
                if (!candidateAnns) {
                    choicePoints.push(new Tuple(choiceTextIdx+1, 0, choiceMappedText, choiceMappedAnn))
                    continue
                }
                // 2. If we found a candidate at this index, push its contribution onto the stack,
                // and move on to the next open index
                else if (choiceAnnIdx < candidateAnns.size()) {
                    // map the candidate annotation to a text string representation
                    Annotation candidate = candidateAnns[choiceAnnIdx]
                    String mappedText = annClassToCodeMap[candidate.class]
                    mappedText += "${LBRACK}${candidate.coveredText}${RBRACK}"
                    Set<String> feats = annClassToFeatMap[candidate.class]
                    feats.each { String feat ->
                        String featVal = candidate."${feat}"
                        String featConstraint = (featVal ? "${LBRACK}${featVal}${RBRACK}"
                            : "${LBRACK}[^${LBRACK}${RBRACK}]+${RBRACK}"
                        )
                        mappedText += featConstraint
                    }
                    choicePoints.push(new Tuple(choiceTextIdx, choiceAnnIdx+1, choiceMappedText, choiceMappedAnn))
                    choicePoints.push(new Tuple(choiceTextIdx+(candidate.coveredText.length() > 0 ? candidate.coveredText.length() : 1), 0, mappedText, candidate))
                }
            }

            return matcherBindingPairs
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // static fields
    // -----------------------------------------------------------------------------------------------------------------
    static final Integer INIT_TYPE_CODE = 0x2460;
    static final Character LBRACK = (char)0x2039;
    static final Character RBRACK = (char)0x203A;

    // -----------------------------------------------------------------------------------------------------------------
    // instance fields
    // -----------------------------------------------------------------------------------------------------------------
    Integer groupCounter = 0
    Set<String> groupNames = []
    Map<Class<Annotation>, Character> annClassToCodeMap = [:]
    Map<Class<Annotation>, String> annClassToFeatMap = [:]
    List<Character> typeCodes = []
    Pattern pattern;

    // -----------------------------------------------------------------------------------------------------------------
    // public methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     * @param regex
     */
    AnnotationPattern(final Node regex) {
        // -------------------------------------------------------------------------------------------------------------
        // create mapping indices
        // -------------------------------------------------------------------------------------------------------------
        Integer currTypeCode = AnnotationPattern.INIT_TYPE_CODE
        List<Node> includeTypes = regex.include; assert includeTypes
        includeTypes.each { Node n ->
            Character typeCode = (char)currTypeCode++
            typeCodes << typeCode
            this.annClassToCodeMap[n.@type] = typeCode
            this.annClassToFeatMap[n.@type] = n.@feats
        }

        // -------------------------------------------------------------------------------------------------------------
        // generate the pattern from the elements
        // -------------------------------------------------------------------------------------------------------------
        this.pattern = genPattern(regex)
    }

    /**
     * Create an annotation matcher
     * @param coveringAnn
     * @return
     */
    AnnotationMatcher matcher(Annotation annotation, Integer maxCount = 1) {
        return new AnnotationMatcher(annotation, maxCount)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // generate a Pattern object from the logical specification of one
    // -----------------------------------------------------------------------------------------------------------------
    private Pattern genPattern(Node regex) {
        Node body = regex.match[0]; assert body
        Node lookBehind = regex.lookBehind[0]
        Node lookAhead = regex.lookAhead[0]
        Boolean caseInsensitive = (regex.@caseInsensitive ?: false)

        String patternStr = (caseInsensitive ? '(?i)' : '')
        if (lookBehind) {
            patternStr += (lookBehind.@positive ? '(?<=' : '(?<!')
            lookBehind.node.each { Node node ->
                patternStr += this.genPatternChunk(node)
            }
            patternStr += ')'
        }
        body.node.each { Node node ->
            patternStr += this.genPatternChunk(node)
        }
        if (lookAhead) {
            patternStr += (lookAhead.@positive ? '(?=' : '(?!')
            lookAhead.node.each { Node node ->
                patternStr += this.genPatternChunk(node)
            }
            patternStr += ')'
        }
        ////println "Pattern string: ${patternStr}"
        return ~patternStr
    }

    // -----------------------------------------------------------------------------------------------------------------
    // private methods
    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    // generate a pattern chunk; recurses through node tree
    // -----------------------------------------------------------------------------------------------------------------
    private String genPatternChunk(Node node) {
        if (node.@type && node.@type == 'group' && node.node.size() > 0) {
            return this.handleGroup(node)
        }
        else if (node.@type && node.@type == 'union' && node.node.size() > 0) {
            return this.handleUnion(node)
        }
        else {
            return this.handleAtom(node)
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // map group node to text
    // -----------------------------------------------------------------------------------------------------------------
    private String handleGroup(Node node) {
        String patternStr = ''
        node.node.each { Node embedded ->
            patternStr += genPatternChunk(embedded)
        }
        return this.decorate(node, patternStr)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // map union node to text
    // -----------------------------------------------------------------------------------------------------------------
    private String handleUnion(Node node) {
        String patternStr = ''
        node.node.each { Node embedded ->
            patternStr += "${genPatternChunk(embedded)}|"
        }
        patternStr = patternStr.substring(0, patternStr.length()-1)
        return this.decorate(node, patternStr)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // map basic node to text: (annotation class code + text + features)
    // -----------------------------------------------------------------------------------------------------------------
    private String handleAtom(Node node) {
        Class<Annotation> annType = node.@type
        char annCode = annClassToCodeMap[annType]

        // covered text pattern
        String coveredText = (node.@text ?  "${LBRACK}(?:${node.@text})${RBRACK}"
            : "${LBRACK}[^${LBRACK}${RBRACK}]*${RBRACK}"
        )
        // feature constraints pattern
        String featConstraints = ''
        Set<String> feats = annClassToFeatMap[annType]
        feats.each { String feat ->
            String featVal = ((node.@feats && node.@feats[feat]) ? node.@feats[feat] : "[^${LBRACK}${RBRACK}]*")
            String featConstraint ="${LBRACK}${featVal}${RBRACK}"
            featConstraints += featConstraint
        }
        // decorate the combined pattern node with group name and range
        String patternStr = "${annCode}${coveredText}${featConstraints}"
        return this.decorate(node, patternStr)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // decorate pattern string with nodes:
    // - group name
    // - repetition count range (lazy or greedy)
    // -----------------------------------------------------------------------------------------------------------------
    private String decorate(Node node, String patternStr) {
        String name = (node.@name ?: "G${this.groupCounter++}"); this.groupNames << name;
        List range = (node.@range ?: [1,1])
        Boolean greedy = node.@greedy; if (greedy == null) { greedy = true }

        String rangeStr = ((range[0] == 1 && range[1] == 1) ? '' : "{${range[0]},${range[1]}}${greedy ? '' : '?'}")
        patternStr = "(?:${patternStr})${rangeStr}"
        patternStr = "(?<$name>${patternStr})"

        return patternStr
    }
}
