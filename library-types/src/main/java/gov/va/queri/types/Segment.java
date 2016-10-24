

/* First created by JCasGen Tue Sep 27 22:40:32 CDT 2016 */
package gov.va.queri.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Sep 27 22:40:32 CDT 2016
 * XML source: /var/folders/0x/5f5fx8fn4q71df_5ch8ck_2c0000gn/T/leoTypeDescription_a511a43b-a719-4658-9379-2688cac7dc617141626629190966671.xml
 * @generated */
public class Segment extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Segment.class);
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
  protected Segment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Segment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Segment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Segment(JCas jcas, int begin, int end) {
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
  //* Feature: code

  /** getter for code - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCode() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "gov.va.queri.types.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_code);}
    
  /** setter for code - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCode(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "gov.va.queri.types.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_code, v);}    
   
    
  //*--------------*
  //* Feature: codeSystem

  /** getter for codeSystem - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCodeSystem() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_codeSystem == null)
      jcasType.jcas.throwFeatMissing("codeSystem", "gov.va.queri.types.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_codeSystem);}
    
  /** setter for codeSystem - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCodeSystem(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_codeSystem == null)
      jcasType.jcas.throwFeatMissing("codeSystem", "gov.va.queri.types.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_codeSystem, v);}    
  }

    