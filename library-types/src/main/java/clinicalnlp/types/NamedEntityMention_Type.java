
/* First created by JCasGen Mon Apr 03 09:28:01 CDT 2017 */
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
 * Updated by JCasGen Mon Apr 03 09:28:01 CDT 2017
 * @generated */
public class NamedEntityMention_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (NamedEntityMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = NamedEntityMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new NamedEntityMention(addr, NamedEntityMention_Type.this);
  			   NamedEntityMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new NamedEntityMention(addr, NamedEntityMention_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NamedEntityMention.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("clinicalnlp.types.NamedEntityMention");
 
  /** @generated */
  final Feature casFeat_norm;
  /** @generated */
  final int     casFeatCode_norm;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNorm(int addr) {
        if (featOkTst && casFeat_norm == null)
      jcas.throwFeatMissing("norm", "clinicalnlp.types.NamedEntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_norm);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNorm(int addr, String v) {
        if (featOkTst && casFeat_norm == null)
      jcas.throwFeatMissing("norm", "clinicalnlp.types.NamedEntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_norm, v);}
    
  
 
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
      jcas.throwFeatMissing("code", "clinicalnlp.types.NamedEntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_code);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCode(int addr, String v) {
        if (featOkTst && casFeat_code == null)
      jcas.throwFeatMissing("code", "clinicalnlp.types.NamedEntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_code, v);}
    
  
 
  /** @generated */
  final Feature casFeat_codeSystem;
  /** @generated */
  final int     casFeatCode_codeSystem;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCodeSystem(int addr) {
        if (featOkTst && casFeat_codeSystem == null)
      jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.NamedEntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_codeSystem);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCodeSystem(int addr, String v) {
        if (featOkTst && casFeat_codeSystem == null)
      jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.NamedEntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_codeSystem, v);}
    
  
 
  /** @generated */
  final Feature casFeat_semClass;
  /** @generated */
  final int     casFeatCode_semClass;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSemClass(int addr) {
        if (featOkTst && casFeat_semClass == null)
      jcas.throwFeatMissing("semClass", "clinicalnlp.types.NamedEntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_semClass);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSemClass(int addr, String v) {
        if (featOkTst && casFeat_semClass == null)
      jcas.throwFeatMissing("semClass", "clinicalnlp.types.NamedEntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_semClass, v);}
    
  
 
  /** @generated */
  final Feature casFeat_provenance;
  /** @generated */
  final int     casFeatCode_provenance;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getProvenance(int addr) {
        if (featOkTst && casFeat_provenance == null)
      jcas.throwFeatMissing("provenance", "clinicalnlp.types.NamedEntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_provenance);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setProvenance(int addr, String v) {
        if (featOkTst && casFeat_provenance == null)
      jcas.throwFeatMissing("provenance", "clinicalnlp.types.NamedEntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_provenance, v);}
    
  
 
  /** @generated */
  final Feature casFeat_polarity;
  /** @generated */
  final int     casFeatCode_polarity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getPolarity(int addr) {
        if (featOkTst && casFeat_polarity == null)
      jcas.throwFeatMissing("polarity", "clinicalnlp.types.NamedEntityMention");
    return ll_cas.ll_getIntValue(addr, casFeatCode_polarity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPolarity(int addr, int v) {
        if (featOkTst && casFeat_polarity == null)
      jcas.throwFeatMissing("polarity", "clinicalnlp.types.NamedEntityMention");
    ll_cas.ll_setIntValue(addr, casFeatCode_polarity, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NamedEntityMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_norm = jcas.getRequiredFeatureDE(casType, "norm", "uima.cas.String", featOkTst);
    casFeatCode_norm  = (null == casFeat_norm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_norm).getCode();

 
    casFeat_code = jcas.getRequiredFeatureDE(casType, "code", "uima.cas.String", featOkTst);
    casFeatCode_code  = (null == casFeat_code) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_code).getCode();

 
    casFeat_codeSystem = jcas.getRequiredFeatureDE(casType, "codeSystem", "uima.cas.String", featOkTst);
    casFeatCode_codeSystem  = (null == casFeat_codeSystem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_codeSystem).getCode();

 
    casFeat_semClass = jcas.getRequiredFeatureDE(casType, "semClass", "uima.cas.String", featOkTst);
    casFeatCode_semClass  = (null == casFeat_semClass) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_semClass).getCode();

 
    casFeat_provenance = jcas.getRequiredFeatureDE(casType, "provenance", "uima.cas.String", featOkTst);
    casFeatCode_provenance  = (null == casFeat_provenance) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_provenance).getCode();

 
    casFeat_polarity = jcas.getRequiredFeatureDE(casType, "polarity", "uima.cas.Integer", featOkTst);
    casFeatCode_polarity  = (null == casFeat_polarity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_polarity).getCode();

  }
}



    