package gov.va.queri.types

import gov.va.vinci.leo.annotationpattern.ae.AnnotationPatternAnnotator
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import gov.va.vinci.leo.regex.ae.RegexAnnotator
import gov.va.vinci.leo.types.TypeLibrarian
import gov.va.vinci.leo.window.ae.WindowAnnotator
import org.apache.uima.resource.metadata.impl.TypeDescription_impl
//-----------------------------------------------------------------------------
// Leo types
//-----------------------------------------------------------------------------
LeoTypeSystemDescription types = new LeoTypeSystemDescription()
types.addType(TypeLibrarian.getCSITypeSystemDescription())
types.addType(TypeLibrarian.getRelationshipAnnotationTypeSystemDescription())
types.addType(TypeLibrarian.getValidationAnnotationTypeSystemDescription())
types.addTypeSystemDescription(new WindowAnnotator().getLeoTypeSystemDescription())
types.addTypeSystemDescription(new RegexAnnotator().getLeoTypeSystemDescription())
types.addTypeSystemDescription(new AnnotationPatternAnnotator().getLeoTypeSystemDescription())
types.addTypeSystemDescription(new LeoTypeSystemDescription('gov.va.vinci.leo.sentence.types.SentenceAnnotatorType',
        true))

//-----------------------------------------------------------------------------
// Token types
//-----------------------------------------------------------------------------
TypeDescription_impl token = new TypeDescription_impl('gov.va.queri.types.Token',
        '', 'uima.tcas.Annotation')
token.addFeature('pos', '', 'uima.cas.String')
token.addFeature('lemma', '', 'uima.cas.String')
token.addFeature('stem', '', 'uima.cas.String')
types.addType(token)

TypeDescription_impl wordToken = new TypeDescription_impl('gov.va.queri.types.WordToken',
        '', 'gov.va.queri.types.Token')
types.addType(wordToken)

TypeDescription_impl textSpan = new TypeDescription_impl('gov.va.queri.types.TextSpan',
    '', 'uima.tcas.Annotation')
types.addType(textSpan)

//-----------------------------------------------------------------------------
// Named entity types
//-----------------------------------------------------------------------------
TypeDescription_impl nem = new TypeDescription_impl('gov.va.queri.types.NamedEntityMention',
        '', 'uima.tcas.Annotation')
nem.addFeature('code', '', 'uima.cas.String')
nem.addFeature('codeSystem', '', 'uima.cas.String')
nem.addFeature('provenance', '', 'uima.cas.String')
nem.addFeature('polarity', '', 'uima.cas.Integer');
types.addType(nem)

//-----------------------------------------------------------------------------
// Context types
//-----------------------------------------------------------------------------
TypeDescription_impl trigger = new TypeDescription_impl('gov.va.queri.types.NegationTrigger', '',
        'uima.tcas.Annotation')
types.addType(trigger)
TypeDescription_impl pretrigger = new TypeDescription_impl('gov.va.queri.types.PreNegationTrigger', '',
        'gov.va.queri.types.NegationTrigger')
types.addType(pretrigger)
TypeDescription_impl posttrigger = new TypeDescription_impl('gov.va.queri.types.PostNegationTrigger', '',
        'gov.va.queri.types.NegationTrigger')
types.addType(posttrigger)
TypeDescription_impl pseudotrigger = new TypeDescription_impl('gov.va.queri.types.PseudoNegationTrigger', '',
        'gov.va.queri.types.NegationTrigger')
types.addType(pseudotrigger)
TypeDescription_impl negscope = new TypeDescription_impl('gov.va.queri.types.NegationScope', '',
        'uima.tcas.Annotation')
types.addType(negscope)
TypeDescription_impl negscopeterminator = new TypeDescription_impl('gov.va.queri.types.NegationScopeTerminator', '',
        'uima.tcas.Annotation')
types.addType(negscopeterminator)

//-----------------------------------------------------------------------------
// Segment types
//-----------------------------------------------------------------------------
TypeDescription_impl segment = new TypeDescription_impl('gov.va.queri.types.Segment', '', 'uima.tcas.Annotation');
segment.addFeature('code', '', 'uima.cas.String');
segment.addFeature('codeSystem', '', 'uima.cas.String');
types.addType(segment)

TypeDescription_impl heading = new TypeDescription_impl('gov.va.queri.types.SegmentHeading', '', 'uima.tcas.Annotation')
heading.addFeature('code', '', 'uima.cas.String');
heading.addFeature('codeSystem', '', 'uima.cas.String');
types.addType(heading)

//-----------------------------------------------------------------------------
// Dictionary types
//-----------------------------------------------------------------------------
TypeDescription_impl dictMatch = new TypeDescription_impl('gov.va.queri.types.DictMatch', '', 'uima.tcas.Annotation')
dictMatch.addFeature('matched', '', 'uima.cas.FSArray')
dictMatch.addFeature('canonical', '', 'uima.cas.String')
dictMatch.addFeature('code', '', 'uima.cas.String')
dictMatch.addFeature('vocabulary', '', 'uima.cas.String')
dictMatch.addFeature('container', '', 'uima.tcas.Annotation')
types.addType(dictMatch)

//-----------------------------------------------------------------------------
// Generate source and descriptors
//-----------------------------------------------------------------------------
types.toXML('src/main/resources/gov/va/queri/types/CoreTypeSystem.xml')
types.jCasGen('src/main/java/', 'build/classes')
