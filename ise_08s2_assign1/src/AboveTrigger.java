/**
 * 
 */
package src;

/** 
 * @author charles
 * @uml.annotations
 *     derived_abstraction="platform:/resource/Rules/Blank%20Model.emx#_gO6tYGIPEd2DevXLZLPWdA"
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class AboveTrigger extends ValueTrigger {
	/** 
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	private double threshold;
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public boolean isValidTrigger(){
		if(super.sensor instanceof ValueSensor){
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Test if the trigger is fired.
	 * @return true if the corresponding sensor value is above the threshold, false otherwise
	 * @see src.Trigger#isSatisfied()
	 */
	public boolean isSatisfied(){
		if(((ValueSensor)super.sensor).getValue() > threshold){
			return true;
		} else {
			return false;
		}
	}
}