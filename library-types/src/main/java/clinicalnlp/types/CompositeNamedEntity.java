

/* First created by JCasGen Mon Apr 03 09:28:01 CDT 2017 */
package clinicalnlp.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Apr 03 09:28:01 CDT 2017
 * XML source: /var/folders/k0/jcxw1d05549c48zgccrbj_q40000gp/T/leoTypeDescription_4c64e559-5736-4c58-afc4-1116786ee212200507943477133410.xml
 * @generated */
public class CompositeNamedEntity extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CompositeNamedEntity.class);
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
  protected CompositeNamedEntity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CompositeNamedEntity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CompositeNamedEntity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CompositeNamedEntity(JCas jcas, int begin, int end) {
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
  //* Feature: semClass

  /** getter for semClass - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSemClass() {
    if (CompositeNamedEntity_Type.featOkTst && ((CompositeNamedEntity_Type)jcasType).casFeat_semClass == null)
      jcasType.jcas.throwFeatMissing("semClass", "clinicalnlp.types.CompositeNamedEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_semClass);}
    
  /** setter for semClass - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSemClass(String v) {
    if (CompositeNamedEntity_Type.featOkTst && ((CompositeNamedEntity_Type)jcasType).casFeat_semClass == null)
      jcasType.jcas.throwFeatMissing("semClass", "clinicalnlp.types.CompositeNamedEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_semClass, v);}    
   
    
  //*--------------*
  //* Feature: mentions

  /** getter for mentions - gets array of mentions
   * @generated
   * @return value of the feature 
   */
  public FSArray getMentions() {
    if (CompositeNamedEntity_Type.featOkTst && ((CompositeNamedEntity_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "clinicalnlp.types.CompositeNamedEntity");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_mentions)));}
    
  /** setter for mentions - sets array of mentions 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMentions(FSArray v) {
    if (CompositeNamedEntity_Type.featOkTst && ((CompositeNamedEntity_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "clinicalnlp.types.CompositeNamedEntity");
    jcasType.ll_cas.ll_setRefValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_mentions, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for mentions - gets an indexed value - array of mentions
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public NamedEntityMention getMentions(int i) {
    if (CompositeNamedEntity_Type.featOkTst && ((CompositeNamedEntity_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "clinicalnlp.types.CompositeNamedEntity");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_mentions), i);
    return (NamedEntityMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_mentions), i)));}

  /** indexed setter for mentions - sets an indexed value - array of mentions
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setMentions(int i, NamedEntityMention v) { 
    if (CompositeNamedEntity_Type.featOkTst && ((CompositeNamedEntity_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "clinicalnlp.types.CompositeNamedEntity");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_mentions), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CompositeNamedEntity_Type)jcasType).casFeatCode_mentions), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    