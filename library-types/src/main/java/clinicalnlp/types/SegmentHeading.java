

/* First created by JCasGen Sun Apr 02 14:59:24 CDT 2017 */
package clinicalnlp.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Apr 02 14:59:24 CDT 2017
 * XML source: /var/folders/k0/jcxw1d05549c48zgccrbj_q40000gp/T/leoTypeDescription_22e5a908-e296-40b9-9a7f-1901296f63715280762021609735894.xml
 * @generated */
public class SegmentHeading extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SegmentHeading.class);
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
  protected SegmentHeading() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SegmentHeading(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SegmentHeading(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SegmentHeading(JCas jcas, int begin, int end) {
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
    if (SegmentHeading_Type.featOkTst && ((SegmentHeading_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "clinicalnlp.types.SegmentHeading");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SegmentHeading_Type)jcasType).casFeatCode_code);}
    
  /** setter for code - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCode(String v) {
    if (SegmentHeading_Type.featOkTst && ((SegmentHeading_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "clinicalnlp.types.SegmentHeading");
    jcasType.ll_cas.ll_setStringValue(addr, ((SegmentHeading_Type)jcasType).casFeatCode_code, v);}    
   
    
  //*--------------*
  //* Feature: codeSystem

  /** getter for codeSystem - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCodeSystem() {
    if (SegmentHeading_Type.featOkTst && ((SegmentHeading_Type)jcasType).casFeat_codeSystem == null)
      jcasType.jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.SegmentHeading");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SegmentHeading_Type)jcasType).casFeatCode_codeSystem);}
    
  /** setter for codeSystem - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCodeSystem(String v) {
    if (SegmentHeading_Type.featOkTst && ((SegmentHeading_Type)jcasType).casFeat_codeSystem == null)
      jcasType.jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.SegmentHeading");
    jcasType.ll_cas.ll_setStringValue(addr, ((SegmentHeading_Type)jcasType).casFeatCode_codeSystem, v);}    
  }

    