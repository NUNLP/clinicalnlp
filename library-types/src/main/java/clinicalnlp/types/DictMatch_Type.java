
/* First created by JCasGen Sun Apr 02 14:59:24 CDT 2017 */
package clinicalnlp.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sun Apr 02 14:59:24 CDT 2017
 * @generated */
public class DictMatch_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DictMatch_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DictMatch_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DictMatch(addr, DictMatch_Type.this);
  			   DictMatch_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DictMatch(addr, DictMatch_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DictMatch.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("clinicalnlp.types.DictMatch");
 
  /** @generated */
  final Feature casFeat_matched;
  /** @generated */
  final int     casFeatCode_matched;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getMatched(int addr) {
        if (featOkTst && casFeat_matched == null)
      jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    return ll_cas.ll_getRefValue(addr, casFeatCode_matched);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMatched(int addr, int v) {
        if (featOkTst && casFeat_matched == null)
      jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    ll_cas.ll_setRefValue(addr, casFeatCode_matched, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getMatched(int addr, int i) {
        if (featOkTst && casFeat_matched == null)
      jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matched), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_matched), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matched), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setMatched(int addr, int i, int v) {
        if (featOkTst && casFeat_matched == null)
      jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matched), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_matched), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matched), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_canonical;
  /** @generated */
  final int     casFeatCode_canonical;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCanonical(int addr) {
        if (featOkTst && casFeat_canonical == null)
      jcas.throwFeatMissing("canonical", "clinicalnlp.types.DictMatch");
    return ll_cas.ll_getStringValue(addr, casFeatCode_canonical);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCanonical(int addr, String v) {
        if (featOkTst && casFeat_canonical == null)
      jcas.throwFeatMissing("canonical", "clinicalnlp.types.DictMatch");
    ll_cas.ll_setStringValue(addr, casFeatCode_canonical, v);}
    
  
 
  /** @generated */
  final Feature casFeat_code;
  /** @generated */
  final int     casFeatCode_code;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCode(int addr) {
        if (featOkTst && casFeat_code == null)
      jcas.throwFeatMissing("code", "clinicalnlp.types.DictMatch");
    return ll_cas.ll_getStringValue(addr, casFeatCode_code);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCode(int addr, String v) {
        if (featOkTst && casFeat_code == null)
      jcas.throwFeatMissing("code", "clinicalnlp.types.DictMatch");
    ll_cas.ll_setStringValue(addr, casFeatCode_code, v);}
    
  
 
  /** @generated */
  final Feature casFeat_vocabulary;
  /** @generated */
  final int     casFeatCode_vocabulary;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getVocabulary(int addr) {
        if (featOkTst && casFeat_vocabulary == null)
      jcas.throwFeatMissing("vocabulary", "clinicalnlp.types.DictMatch");
    return ll_cas.ll_getStringValue(addr, casFeatCode_vocabulary);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setVocabulary(int addr, String v) {
        if (featOkTst && casFeat_vocabulary == null)
      jcas.throwFeatMissing("vocabulary", "clinicalnlp.types.DictMatch");
    ll_cas.ll_setStringValue(addr, casFeatCode_vocabulary, v);}
    
  
 
  /** @generated */
  final Feature casFeat_score;
  /** @generated */
  final int     casFeatCode_score;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public float getScore(int addr) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "clinicalnlp.types.DictMatch");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_score);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setScore(int addr, float v) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "clinicalnlp.types.DictMatch");
    ll_cas.ll_setFloatValue(addr, casFeatCode_score, v);}
    
  
 
  /** @generated */
  final Feature casFeat_container;
  /** @generated */
  final int     casFeatCode_container;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getContainer(int addr) {
        if (featOkTst && casFeat_container == null)
      jcas.throwFeatMissing("container", "clinicalnlp.types.DictMatch");
    return ll_cas.ll_getRefValue(addr, casFeatCode_container);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setContainer(int addr, int v) {
        if (featOkTst && casFeat_container == null)
      jcas.throwFeatMissing("container", "clinicalnlp.types.DictMatch");
    ll_cas.ll_setRefValue(addr, casFeatCode_container, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DictMatch_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_matched = jcas.getRequiredFeatureDE(casType, "matched", "uima.cas.FSArray", featOkTst);
    casFeatCode_matched  = (null == casFeat_matched) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_matched).getCode();

 
    casFeat_canonical = jcas.getRequiredFeatureDE(casType, "canonical", "uima.cas.String", featOkTst);
    casFeatCode_canonical  = (null == casFeat_canonical) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_canonical).getCode();

 
    casFeat_code = jcas.getRequiredFeatureDE(casType, "code", "uima.cas.String", featOkTst);
    casFeatCode_code  = (null == casFeat_code) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_code).getCode();

 
    casFeat_vocabulary = jcas.getRequiredFeatureDE(casType, "vocabulary", "uima.cas.String", featOkTst);
    casFeatCode_vocabulary  = (null == casFeat_vocabulary) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_vocabulary).getCode();

 
    casFeat_score = jcas.getRequiredFeatureDE(casType, "score", "uima.cas.Float", featOkTst);
    casFeatCode_score  = (null == casFeat_score) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_score).getCode();

 
    casFeat_container = jcas.getRequiredFeatureDE(casType, "container", "uima.tcas.Annotation", featOkTst);
    casFeatCode_container  = (null == casFeat_container) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_container).getCode();

  }
}



    