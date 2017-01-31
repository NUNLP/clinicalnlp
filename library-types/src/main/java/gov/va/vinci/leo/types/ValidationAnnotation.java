

/* First created by JCasGen Tue Jan 31 09:32:09 CST 2017 */
package gov.va.vinci.leo.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Validation Annotations
 * Updated by JCasGen Tue Jan 31 09:32:09 CST 2017
 * XML source: /var/folders/k0/jcxw1d05549c48zgccrbj_q40000gp/T/leoTypeDescription_58ffb175-3b54-4884-8264-ec13641de8dc2638628757084761241.xml
 * @generated */
public class ValidationAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ValidationAnnotation.class);
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
  protected ValidationAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ValidationAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ValidationAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ValidationAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: ReferenceAnnotationGuid

  /** getter for ReferenceAnnotationGuid - gets The GUID of the annotation this validation annotation references.
   * @generated
   * @return value of the feature 
   */
  public String getReferenceAnnotationGuid() {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_ReferenceAnnotationGuid == null)
      jcasType.jcas.throwFeatMissing("ReferenceAnnotationGuid", "gov.va.vinci.leo.types.ValidationAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_ReferenceAnnotationGuid);}
    
  /** setter for ReferenceAnnotationGuid - sets The GUID of the annotation this validation annotation references. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setReferenceAnnotationGuid(String v) {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_ReferenceAnnotationGuid == null)
      jcasType.jcas.throwFeatMissing("ReferenceAnnotationGuid", "gov.va.vinci.leo.types.ValidationAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_ReferenceAnnotationGuid, v);}    
   
    
  //*--------------*
  //* Feature: ValidationValue

  /** getter for ValidationValue - gets The validation value for this annotation.
   * @generated
   * @return value of the feature 
   */
  public String getValidationValue() {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_ValidationValue == null)
      jcasType.jcas.throwFeatMissing("ValidationValue", "gov.va.vinci.leo.types.ValidationAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_ValidationValue);}
    
  /** setter for ValidationValue - sets The validation value for this annotation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValidationValue(String v) {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_ValidationValue == null)
      jcasType.jcas.throwFeatMissing("ValidationValue", "gov.va.vinci.leo.types.ValidationAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_ValidationValue, v);}    
   
    
  //*--------------*
  //* Feature: ValidationComment

  /** getter for ValidationComment - gets The validation comment (if any) for this annotation.
   * @generated
   * @return value of the feature 
   */
  public String getValidationComment() {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_ValidationComment == null)
      jcasType.jcas.throwFeatMissing("ValidationComment", "gov.va.vinci.leo.types.ValidationAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_ValidationComment);}
    
  /** setter for ValidationComment - sets The validation comment (if any) for this annotation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValidationComment(String v) {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_ValidationComment == null)
      jcasType.jcas.throwFeatMissing("ValidationComment", "gov.va.vinci.leo.types.ValidationAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_ValidationComment, v);}    
   
    
  //*--------------*
  //* Feature: CreatedBy

  /** getter for CreatedBy - gets The userId that created this annotation.
   * @generated
   * @return value of the feature 
   */
  public String getCreatedBy() {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_CreatedBy == null)
      jcasType.jcas.throwFeatMissing("CreatedBy", "gov.va.vinci.leo.types.ValidationAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_CreatedBy);}
    
  /** setter for CreatedBy - sets The userId that created this annotation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCreatedBy(String v) {
    if (ValidationAnnotation_Type.featOkTst && ((ValidationAnnotation_Type)jcasType).casFeat_CreatedBy == null)
      jcasType.jcas.throwFeatMissing("CreatedBy", "gov.va.vinci.leo.types.ValidationAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((ValidationAnnotation_Type)jcasType).casFeatCode_CreatedBy, v);}    
  }

    