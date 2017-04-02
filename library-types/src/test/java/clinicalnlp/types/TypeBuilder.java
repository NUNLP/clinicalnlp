package clinicalnlp.types;

import gov.va.vinci.leo.annotationpattern.ae.AnnotationPatternAnnotator;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.regex.ae.RegexAnnotator;
import gov.va.vinci.leo.sentence.ae.AnchoredSentenceAnnotator;
import gov.va.vinci.leo.sentence.ae.SentenceAnnotator;
import gov.va.vinci.leo.types.TypeLibrarian;
import gov.va.vinci.leo.window.ae.WindowAnnotator;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;


public class TypeBuilder {
    static void build() throws Exception {
        LeoTypeSystemDescription types = new LeoTypeSystemDescription();

        //-----------------------------------------------------------------------------
        // Leo types
        //-----------------------------------------------------------------------------
        types.addType(TypeLibrarian.getCSITypeSystemDescription());
        types.addType(TypeLibrarian.getRelationshipAnnotationTypeSystemDescription());
        types.addType(TypeLibrarian.getValidationAnnotationTypeSystemDescription());
        types.addTypeSystemDescription(new WindowAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new RegexAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new AnnotationPatternAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new SentenceAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new AnchoredSentenceAnnotator().getLeoTypeSystemDescription());

        //-----------------------------------------------------------------------------
        // Token types
        //-----------------------------------------------------------------------------
        TypeDescription_impl token = new TypeDescription_impl("clinicalnlp.types.Token",
                "", "uima.tcas.Annotation");
        token.addFeature("pos", "", "uima.cas.String");
        token.addFeature("lemma", "", "uima.cas.String");
        token.addFeature("stem", "", "uima.cas.String");
        token.addFeature("normText", "", "uima.cas.String");
        token.addFeature("normDist", "", "uima.cas.Float");

        types.addType(token);

        TypeDescription_impl wordToken = new TypeDescription_impl("clinicalnlp.types.WordToken",
                "", "clinicalnlp.types.Token");
        types.addType(wordToken);

        TypeDescription_impl textSpan = new TypeDescription_impl("clinicalnlp.types.TextSpan",
            "", "uima.tcas.Annotation");
        types.addType(textSpan);

        //-----------------------------------------------------------------------------
        // Named entity types
        //-----------------------------------------------------------------------------
        TypeDescription_impl nem = new TypeDescription_impl("clinicalnlp.types.NamedEntityMention",
            "", "uima.tcas.Annotation");
        nem.addFeature("norm", "", "uima.cas.String");
        nem.addFeature("code", "", "uima.cas.String");
        nem.addFeature("codeSystem", "", "uima.cas.String");
        nem.addFeature("provenance", "", "uima.cas.String");
        nem.addFeature("polarity", "", "uima.cas.Integer");
        types.addType(nem);

        TypeDescription_impl composite = new TypeDescription_impl("clinicalnlp.types.CompositeNamedEntity",
                "", "uima.tcas.Annotation");
        composite.addFeature("mentions", "array of mentions", "uima.cas.FSArray",
                "clinicalnlp.types.NamedEntityMention", false);
        types.addType(composite);

        //-----------------------------------------------------------------------------
        // Context types
        //-----------------------------------------------------------------------------
        TypeDescription_impl trigger = new TypeDescription_impl("clinicalnlp.types.NegationTrigger",
                "","uima.tcas.Annotation");
        types.addType(trigger);
        TypeDescription_impl pretrigger = new TypeDescription_impl("clinicalnlp.types.PreNegationTrigger",
                "", "clinicalnlp.types.NegationTrigger");
        types.addType(pretrigger);
        TypeDescription_impl posttrigger = new TypeDescription_impl("clinicalnlp.types.PostNegationTrigger",
                "", "clinicalnlp.types.NegationTrigger");
        types.addType(posttrigger);
        TypeDescription_impl pseudotrigger = new TypeDescription_impl("clinicalnlp.types.PseudoNegationTrigger",
                "","clinicalnlp.types.NegationTrigger");
        types.addType(pseudotrigger);
        TypeDescription_impl negscope = new TypeDescription_impl("clinicalnlp.types.NegationScope",
                "","uima.tcas.Annotation");
        types.addType(negscope);
        TypeDescription_impl negscopeterminator = new TypeDescription_impl(
                "clinicalnlp.types.NegationScopeTerminator",
                "","uima.tcas.Annotation");
        types.addType(negscopeterminator);

        //-----------------------------------------------------------------------------
        // Segment types
        //-----------------------------------------------------------------------------
        TypeDescription_impl segment = new TypeDescription_impl("clinicalnlp.types.Segment", "",
                "uima.tcas.Annotation");
        segment.addFeature("code", "", "uima.cas.String");
        segment.addFeature("codeSystem", "", "uima.cas.String");
        types.addType(segment);

        TypeDescription_impl heading = new TypeDescription_impl("clinicalnlp.types.SegmentHeading",
                "", "uima.tcas.Annotation");
        heading.addFeature("code", "", "uima.cas.String");
        heading.addFeature("codeSystem", "", "uima.cas.String");
        types.addType(heading);

        types.addType("clinicalnlp.types.LeftWindow", "", "uima.tcas.Annotation");
        types.addType("clinicalnlp.types.RightWindow", "", "uima.tcas.Annotation");

        //-----------------------------------------------------------------------------
        // Dictionary types
        //-----------------------------------------------------------------------------
        TypeDescription_impl dictMatch = new TypeDescription_impl("clinicalnlp.types.DictMatch", "",
                "uima.tcas.Annotation");
        dictMatch.addFeature("matched", "", "uima.cas.FSArray");
        dictMatch.addFeature("canonical", "", "uima.cas.String");
        dictMatch.addFeature("code", "", "uima.cas.String");
        dictMatch.addFeature("vocabulary", "", "uima.cas.String");
        dictMatch.addFeature("score", "", "uima.cas.Float");
        dictMatch.addFeature("container", "", "uima.tcas.Annotation");
        types.addType(dictMatch);

        //-----------------------------------------------------------------------------
        // Generate source and descriptor
        //-----------------------------------------------------------------------------
        types.toXML("src/main/resources/clinicalnlp/types/CoreTypeSystem.xml");
        types.jCasGen("src/main/java/", "build/classes");
    }

    public static void main(String[] args) {
        try {
            TypeBuilder.build();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
