/**
 * 
 */
package src;

import java.util.Set;

/** 
 *  A Rule specifies an actuator which is to be turned on or off when a number of triggers are (all) satisfied.
 * @uml.annotations
 *     derived_abstraction="platform:/resource/Rules/Blank%20Model.emx#_stq_AGINEd2DevXLZLPWdA"
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class Rule {
	/** 
	 * @uml.annotations for <code>trigger</code>
	 *     collection_type="src.Trigger"
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	private Set<Trigger> trigger;

	/** 
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	private Action action;

	/**
	 * @return
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public boolean isValidRule() {
		// begin-user-code
		// TODO Auto-generated method stub
		return false;
		// end-user-code
	}

	/**
	 * @return
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public boolean triggerSatisfied() {
		// begin-user-code
		// TODO Auto-generated method stub
		return false;
		// end-user-code
	}

	/**
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	public void activateRule() {
		// begin-user-code
		// TODO Auto-generated method stub

		// end-user-code
	}
}