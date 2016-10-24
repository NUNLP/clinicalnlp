

/* First created by JCasGen Tue Sep 27 22:40:32 CDT 2016 */
package gov.va.vinci.leo.sentence.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Sentence Type
 * Updated by JCasGen Tue Sep 27 22:40:32 CDT 2016
 * XML source: /var/folders/0x/5f5fx8fn4q71df_5ch8ck_2c0000gn/T/leoTypeDescription_a511a43b-a719-4658-9379-2688cac7dc617141626629190966671.xml
 * @generated */
public class Sentence extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentence.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Sentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentence(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Anchor

  /** getter for Anchor - gets Anchor Annotation around which the sentence was created
   * @generated
   * @return value of the feature 
   */
  public Annotation getAnchor() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_Anchor == null)
      jcasType.jcas.throwFeatMissing("Anchor", "gov.va.vinci.leo.sentence.types.Sentence");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_Anchor)));}
    
  /** setter for Anchor - sets Anchor Annotation around which the sentence was created 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnchor(Annotation v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_Anchor == null)
      jcasType.jcas.throwFeatMissing("Anchor", "gov.va.vinci.leo.sentence.types.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_Anchor, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    