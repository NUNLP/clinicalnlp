

/* First created by JCasGen Mon Oct 24 09:31:51 CDT 2016 */
package clinicalnlp.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Oct 24 09:31:51 CDT 2016
 * XML source: /var/folders/k0/jcxw1d05549c48zgccrbj_q40000gp/T/leoTypeDescription_987c88e1-5fea-4090-9887-35a648dbe6636873996385252787908.xml
 * @generated */
public class NamedEntityMention extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NamedEntityMention.class);
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
  protected NamedEntityMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NamedEntityMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NamedEntityMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NamedEntityMention(JCas jcas, int begin, int end) {
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
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "clinicalnlp.types.NamedEntityMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_code);}
    
  /** setter for code - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCode(String v) {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "clinicalnlp.types.NamedEntityMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_code, v);}    
   
    
  //*--------------*
  //* Feature: codeSystem

  /** getter for codeSystem - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCodeSystem() {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_codeSystem == null)
      jcasType.jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.NamedEntityMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_codeSystem);}
    
  /** setter for codeSystem - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCodeSystem(String v) {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_codeSystem == null)
      jcasType.jcas.throwFeatMissing("codeSystem", "clinicalnlp.types.NamedEntityMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_codeSystem, v);}    
   
    
  //*--------------*
  //* Feature: provenance

  /** getter for provenance - gets 
   * @generated
   * @return value of the feature 
   */
  public String getProvenance() {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_provenance == null)
      jcasType.jcas.throwFeatMissing("provenance", "clinicalnlp.types.NamedEntityMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_provenance);}
    
  /** setter for provenance - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setProvenance(String v) {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_provenance == null)
      jcasType.jcas.throwFeatMissing("provenance", "clinicalnlp.types.NamedEntityMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_provenance, v);}    
   
    
  //*--------------*
  //* Feature: polarity

  /** getter for polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public int getPolarity() {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "clinicalnlp.types.NamedEntityMention");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_polarity);}
    
  /** setter for polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(int v) {
    if (NamedEntityMention_Type.featOkTst && ((NamedEntityMention_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "clinicalnlp.types.NamedEntityMention");
    jcasType.ll_cas.ll_setIntValue(addr, ((NamedEntityMention_Type)jcasType).casFeatCode_polarity, v);}    
  }

    