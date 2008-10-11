package tests;

import junit.framework.TestCase;
import src.*;

public class TestTrigger extends TestCase {

	public void testIsValidTrigger() {
		
		Trigger trig = new OnTrigger();
		Sensor sensor = new BooleanSensor();
		trig.sensor = sensor;
		boolean result = trig.isValidTrigger();
		assertTrue (result);
		
		trig = new BelowTrigger();
		sensor = new ValueSensor();
		trig.sensor = sensor;
		assertTrue (trig.isValidTrigger());
	}

	public void testAboveTrigger() {
		Trigger trig = new AboveTrigger();
		ValueSensor sensor = new ValueSensor();
		double sensorValue = 100.5;
		sensor.setValue(sensorValue);
//		((AboveTrigger)trig).setThreshold(50);
		trig.sensor = sensor;
		
		assertTrue(trig.isSatisfied());
	}
	
	public void testBelowTrigger() {
		
	}
	
	public void testValidRule() {
		Rule rule = new Rule();
		
	}

}

