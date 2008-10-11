/**
 * 
 */
package src;

/** 
 * @author charles
 * @uml.annotations
 *     derived_abstraction="platform:/resource/Rules/Blank%20Model.emx#_jVhR8GIPEd2DevXLZLPWdA"
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class BelowTrigger extends ValueTrigger {
	/** 
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	private Object threshold;
	
	public boolean isValidTrigger() {
		if(super.sensor instanceof ValueSensor){
			return true;
		} else {
			return false;
		}
	}
	
}