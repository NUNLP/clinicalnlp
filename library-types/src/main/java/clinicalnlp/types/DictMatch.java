

/* First created by JCasGen Sat Feb 11 11:23:44 CST 2017 */
package clinicalnlp.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat Feb 11 11:23:44 CST 2017
 * XML source: /var/folders/k0/jcxw1d05549c48zgccrbj_q40000gp/T/leoTypeDescription_116007de-f8ce-4535-baaa-b13aa71dd24e5550980219044642548.xml
 * @generated */
public class DictMatch extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DictMatch.class);
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
  protected DictMatch() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DictMatch(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DictMatch(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DictMatch(JCas jcas, int begin, int end) {
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
  //* Feature: matched

  /** getter for matched - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getMatched() {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_matched == null)
      jcasType.jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_matched)));}
    
  /** setter for matched - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMatched(FSArray v) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_matched == null)
      jcasType.jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    jcasType.ll_cas.ll_setRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_matched, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for matched - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public TOP getMatched(int i) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_matched == null)
      jcasType.jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_matched), i);
    return (TOP)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_matched), i)));}

  /** indexed setter for matched - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setMatched(int i, TOP v) { 
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_matched == null)
      jcasType.jcas.throwFeatMissing("matched", "clinicalnlp.types.DictMatch");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_matched), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_matched), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: canonical

  /** getter for canonical - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCanonical() {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_canonical == null)
      jcasType.jcas.throwFeatMissing("canonical", "clinicalnlp.types.DictMatch");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DictMatch_Type)jcasType).casFeatCode_canonical);}
    
  /** setter for canonical - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCanonical(String v) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_canonical == null)
      jcasType.jcas.throwFeatMissing("canonical", "clinicalnlp.types.DictMatch");
    jcasType.ll_cas.ll_setStringValue(addr, ((DictMatch_Type)jcasType).casFeatCode_canonical, v);}    
   
    
  //*--------------*
  //* Feature: code

  /** getter for code - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCode() {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "clinicalnlp.types.DictMatch");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DictMatch_Type)jcasType).casFeatCode_code);}
    
  /** setter for code - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCode(String v) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "clinicalnlp.types.DictMatch");
    jcasType.ll_cas.ll_setStringValue(addr, ((DictMatch_Type)jcasType).casFeatCode_code, v);}    
   
    
  //*--------------*
  //* Feature: vocabulary

  /** getter for vocabulary - gets 
   * @generated
   * @return value of the feature 
   */
  public String getVocabulary() {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_vocabulary == null)
      jcasType.jcas.throwFeatMissing("vocabulary", "clinicalnlp.types.DictMatch");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DictMatch_Type)jcasType).casFeatCode_vocabulary);}
    
  /** setter for vocabulary - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setVocabulary(String v) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_vocabulary == null)
      jcasType.jcas.throwFeatMissing("vocabulary", "clinicalnlp.types.DictMatch");
    jcasType.ll_cas.ll_setStringValue(addr, ((DictMatch_Type)jcasType).casFeatCode_vocabulary, v);}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets 
   * @generated
   * @return value of the feature 
   */
  public float getScore() {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "clinicalnlp.types.DictMatch");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((DictMatch_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setScore(float v) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "clinicalnlp.types.DictMatch");
    jcasType.ll_cas.ll_setFloatValue(addr, ((DictMatch_Type)jcasType).casFeatCode_score, v);}    
   
    
  //*--------------*
  //* Feature: container

  /** getter for container - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getContainer() {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_container == null)
      jcasType.jcas.throwFeatMissing("container", "clinicalnlp.types.DictMatch");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_container)));}
    
  /** setter for container - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setContainer(Annotation v) {
    if (DictMatch_Type.featOkTst && ((DictMatch_Type)jcasType).casFeat_container == null)
      jcasType.jcas.throwFeatMissing("container", "clinicalnlp.types.DictMatch");
    jcasType.ll_cas.ll_setRefValue(addr, ((DictMatch_Type)jcasType).casFeatCode_container, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    