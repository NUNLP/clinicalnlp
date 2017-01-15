
/* First created by JCasGen Thu Dec 01 10:19:00 CST 2016 */
package gov.va.vinci.leo.window.types;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Window Type
 * Updated by JCasGen Thu Dec 01 10:19:00 CST 2016
 * @generated */
public class Window_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Window_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Window_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Window(addr, Window_Type.this);
  			   Window_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Window(addr, Window_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Window.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.leo.window.types.Window");
 
  /** @generated */
  final Feature casFeat_Anchor;
  /** @generated */
  final int     casFeatCode_Anchor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAnchor(int addr) {
        if (featOkTst && casFeat_Anchor == null)
      jcas.throwFeatMissing("Anchor", "gov.va.vinci.leo.window.types.Window");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Anchor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnchor(int addr, int v) {
        if (featOkTst && casFeat_Anchor == null)
      jcas.throwFeatMissing("Anchor", "gov.va.vinci.leo.window.types.Window");
    ll_cas.ll_setRefValue(addr, casFeatCode_Anchor, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Window_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Anchor = jcas.getRequiredFeatureDE(casType, "Anchor", "uima.tcas.Annotation", featOkTst);
    casFeatCode_Anchor  = (null == casFeat_Anchor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Anchor).getCode();

  }
}



    