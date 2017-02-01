
/* First created by JCasGen Tue Jan 31 21:25:28 CST 2017 */
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
 * Updated by JCasGen Tue Jan 31 21:25:28 CST 2017
 * @generated */
public class SegmentHeading_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SegmentHeading_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SegmentHeading_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SegmentHeading(addr, SegmentHeading_Type.this);
  			   SegmentHeading_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SegmentHeading(addr, SegmentHeading_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SegmentHeading.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("clinicalnlp.types.SegmentHeading");
 
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
      jcas.throwFeatMissing("code", "clinicalnlp.types.SegmentHeading");
    return ll_cas.ll_getStringValue(addr, casFeatCode_code);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCode(int addr, String v) {
        if (featOkTst && casFeat_code == null)
      jcas.throwFeatMissing("code", "clinicalnlp.types.SegmentHeading");
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
      jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.SegmentHeading");
    return ll_cas.ll_getStringValue(addr, casFeatCode_codeSystem);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCodeSystem(int addr, String v) {
        if (featOkTst && casFeat_codeSystem == null)
      jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.SegmentHeading");
    ll_cas.ll_setStringValue(addr, casFeatCode_codeSystem, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SegmentHeading_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_code = jcas.getRequiredFeatureDE(casType, "code", "uima.cas.String", featOkTst);
    casFeatCode_code  = (null == casFeat_code) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_code).getCode();

 
    casFeat_codeSystem = jcas.getRequiredFeatureDE(casType, "codeSystem", "uima.cas.String", featOkTst);
    casFeatCode_codeSystem  = (null == casFeat_codeSystem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_codeSystem).getCode();

  }
}



    