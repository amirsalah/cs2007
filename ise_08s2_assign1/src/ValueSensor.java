/**
 * 
 */
package src;

/** 
 * @author charles
 * @uml.annotations
 *     derived_abstraction="platform:/resource/Rules/Blank%20Model.emx#_MW2hUGIQEd2DevXLZLPWdA"
 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class ValueSensor extends Sensor {
	/** 
	 *  A ValueSensor measures a value which is a floating point number.
	 * @generated "UML to Java V5.0 (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
	private double value;
	
	public void setValue(double value){
		this.value = value;
	}
	
	public double getValue(){
		return value;
	}

}