/**
 * 
 */
package src;

/** 
 * @author charles
 * @uml.annotations
 *     derived_abstraction="platform:/resource/Rules/Blank%20Model.emx#_5-lJgGIPEd2DevXLZLPWdA"
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class OnTrigger extends BooleanTrigger {
	/**
	 * @return
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public boolean isValidTrigger() {
		// begin-user-code
		if (super.sensor instanceof BooleanSensor) {
			return true;
		} else {
			return false;
		}
		// end-user-code
	}
}