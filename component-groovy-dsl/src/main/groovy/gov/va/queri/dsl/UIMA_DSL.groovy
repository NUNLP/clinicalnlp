package gov.va.queri.dsl

import gov.va.queri.types.DictMatch
import gov.va.queri.types.NamedEntityMention
import gov.va.queri.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import gov.va.vinci.leo.tools.AnnotationComparator
import gov.va.vinci.leo.window.types.Window
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.fit.component.JCasConsumer_ImplBase
import org.apache.uima.fit.util.JCasUtil
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.cas.FSArray
import org.apache.uima.jcas.cas.TOP
import org.apache.uima.jcas.impl.JCasImpl
import org.apache.uima.jcas.tcas.Annotation

import java.util.regex.Matcher
import java.util.regex.Pattern

import static gov.va.queri.dsl.UIMA_DSL.getBetween
import static gov.va.queri.dsl.UIMA_DSL.getBetween
import static gov.va.queri.dsl.UIMA_DSL.getCoveredBy
import static org.apache.uima.fit.util.JCasUtil.selectCovered

class UIMA_DSL extends Script {

    @Override
    Object run() {
        super.run()
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Static initialization
    // -----------------------------------------------------------------------------------------------------------------

    static {
        // -------------------------------------------------------------------------------------------------------------
        // Return an AnnotationMatcher when passed a Map argument
        // -------------------------------------------------------------------------------------------------------------
        Pattern.metaClass.matcher = { Map args ->
            JCas jCas = args.JCas
            Annotation coveringAnn = (args.coveringAnn ?: jcas.documentAnnotationFs)
            Boolean includeText = (args.includeText == false ? false : true)
            java.util.List<Class<Annotation>> types = (args.types ?: [])
            return new AnnotationMatcher(jCas, coveringAnn, types, delegate, includeText)
        }

        // -------------------------------------------------------------------------------------------------------------
        // Extend JCas class with create function
        // -------------------------------------------------------------------------------------------------------------
        JCas.metaClass.create = { Map attrs ->
            TOP a = attrs.type.newInstance(getDelegate())
            attrs.each { k, v ->
                if (a.metaClass.hasProperty(a, k)) {
                    if (k != 'type') {
                        a."${k}" = v
                    }
                }
            }
            a.addToIndexes()
            return a
        }

        // -------------------------------------------------------------------------------------------------------------
        // Extend JCas class with select function
        // -------------------------------------------------------------------------------------------------------------
        JCas.metaClass.select = { Map args ->
            Class type = args.type
            Closure filter = args.filter
            Collection<AnnotationFS> annotations = (type != null ? JCasUtil.select(getDelegate(), type) :
                    JCasUtil.selectAll(getDelegate()))
            if (filter) {
                Collection<Annotation> filtered = []
                annotations.each {
                    if (filter.call(it) == true) { filtered << it }
                }
                annotations = filtered
            }
            return annotations
        }

        // -------------------------------------------------------------------------------------------------------------
        // Extend JCas class with remove function
        // -------------------------------------------------------------------------------------------------------------
        JCas.metaClass.removeCovered = { Map args ->
            JCas jcas = delegate
            Collection<Annotation> anns = args.anns
            Collection<Class<? extends Annotation>> removeTypes = args.types
            Comparator<Annotation> comparator = args.comparator

            // first remove duplicate annotations, picking one (at random) to keep
            if (comparator == null) { comparator = new AnnotationComparator() }
            TreeSet<Annotation> uniques = new TreeSet<Annotation>(comparator)
            anns.each { Annotation ann ->
                uniques.add(ann)
            }

            // next, remove annotations that are embedded inside other annotations
            Collection<Annotation> embedded = []
            uniques.each { Annotation ann ->
                removeTypes.each { Class type ->
                    embedded.addAll(JCasUtil.selectCovered(jcas, type, ann))
                }
            }
            embedded.each {
                it.removeFromIndexes()
            }
        }

        // -------------------------------------------------------------------------------------------------------------
        // Extend JCas class with window function
        // -------------------------------------------------------------------------------------------------------------
        JCas.metaClass.createTokenWindow = { Map args ->
            JCas jcas = delegate
            Annotation ann = args.ann
            Class<? extends Annotation> AnchorType = args.anchorType
            Class<? extends Annotation> TokenType = args.tokenType
            Class<? extends Annotation> WindowType = args.windowType
            Class<? extends Annotation> RightWindowType = args.rightWindowType
            Class<? extends Annotation> LeftWindowType = args.leftWindowType

            int leftTokenCount = args.leftTokenCount
            int rightTokenCount = args.rightTokenCount

            Collection<Annotation> anchors = jcas.select type:AnchorType, filter:coveredBy(ann)
            anchors.each { Annotation anchor ->
                Collection<Annotation> beforeTokens = jcas.select type:TokenType,
                        filter:between(ann.begin, anchor.begin)
                int beforeCount = [beforeTokens.size(), leftTokenCount].min()
                Collection<Annotation> afterTokens = jcas.select type:TokenType,
                        filter:between(anchor.end, ann.end)
                int afterCount = [afterTokens.size(), rightTokenCount].min()

                int beginIdx = (beforeCount ? beforeTokens[-beforeCount].begin : anchor.begin)
                int endIdx = (afterCount ? afterTokens[afterCount-1].end : anchor.end)

                jcas.create(type:WindowType, begin:beginIdx, end:endIdx)
                if (RightWindowType) {
                    jcas.create(type:RightWindowType, begin:anchor.begin, end:endIdx)
                }
                if (LeftWindowType) {
                    jcas.create(type:LeftWindowType, begin:beginIdx, end:anchor.end)
                }
            }
        }

        // -------------------------------------------------------------------------------------------------------------
        // Extend DictMatch annotation class with convenience functions
        // -------------------------------------------------------------------------------------------------------------
        DictMatch.metaClass.getMatchedTokens = {
            return (delegate.matched == null ? []:
                    org.apache.uima.fit.util.JCasUtil.select(delegate.matched, Annotation))
        }
        DictMatch.metaClass.setMatchedTokens = { anns ->
            if (anns == null) {
                return;
            }
            FSArray array = new FSArray(delegate.getCAS().getJCas(), anns.size())
            int i = 0
            anns.each {
                array.set(i, it)
                i += 1
            }
            delegate.matched = array
        }
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Utility methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Apply a set of regex patterns to a collection of annotations. For each match, apply
     * the specified closure action.
     */
    static applyPatterns = { Map args ->
        Collection<Annotation> anns = args.anns
        Collection<Pattern> patterns = args.patterns
        Closure action = args.action
        List<Class<Annotation>> types = args.types
        Boolean includeText = args.includeText

        anns.each { ann ->
            patterns.each { p ->
                AnnotationMatcher m = p.matcher(
                        JCas:ann.getCAS().getJCas(),
                        coveringAnn:ann,
                        types:types,
                        includeText:includeText
                )
                m.each { action.call(m) }
            }
        }
    }

    /**
     * Apply a set of regex patterns to a collection of annotations. For each match, create
     * a NamedEntityMention instance.
     * @param args
     * @return
     */
    static createMentions = { Map args ->
        JCas jcas = args.jcas
        Map patterns = args.patterns
        Collection searchSet = args.searchSet
        Class type = args.type
        Boolean longestMatch = args.longestMatch

        List<NamedEntityMention> mentions = []
        searchSet.each { Annotation ann ->
            patterns.each { Pattern pattern, Map vals ->
                Matcher matcher = ann.coveredText =~ pattern
                matcher.each {
                    // create an annotation for each match
                    assert jcas != null
                    NamedEntityMention mention = jcas.create(
                            type:type,
                            begin:(matcher.start(vals.group) + ann.begin),
                            end:(matcher.end(vals.group) + ann.begin),
                            code:vals.code,
                            codeSystem:vals.codeSystem
                    )
                    mentions << mention
                }
            }
        }

        if (longestMatch) {
            jcas.removeCovered(
                    anns:jcas.select(type:type),
                    types:[type]
            )
        }

        return mentions
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Filter predicates
    // -----------------------------------------------------------------------------------------------------------------

    static not = { Closure pred ->
        { TOP ann ->
            !pred.call(ann)
        }
    }

    static and = { Closure... preds ->
        { TOP ann ->
            for (Closure pred : preds) {
                if (pred.call(ann) == false) { return false }
            }
            true
        }
    }

    static or = { Closure... preds ->
        { TOP ann ->
            for (Closure pred : preds) {
                if (pred.call(ann) == true) { return true }
            }
            false
        }
    }

    static contains = { Class<? extends Annotation> type ->
        { TOP ann ->
            selectCovered(ann.CAS.getJCas(), type, ann).size() > 0;
        }
    }

    static coveredBy = { TOP coveringAnn ->
        { TOP ann ->
            (ann != coveringAnn &&
                    coveringAnn.begin <= ann.begin &&
                    coveringAnn.end >= ann.end)
        }
    }

    static between = { Integer begin, Integer end ->
        { TOP ann ->
            (begin <= end && begin <= ann.begin && end >= ann.end)
        }
    }

    static before = { Integer index ->
        { TOP ann ->
            ann.end < index
        }
    }

    static after = { Integer index ->
        { TOP ann ->
            ann.begin > index
        }
    }
}