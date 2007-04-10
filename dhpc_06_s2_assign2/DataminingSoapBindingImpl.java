/**
 * DataminingSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package datamining.ws;
import datamining.DataminingImpl;

public class DataminingSoapBindingImpl implements datamining.ws.Datamining{
		DataminingImpl dm = new DataminingImpl();
		
    public double mean(java.lang.String in0) throws java.rmi.RemoteException {
        return dm.mean(in0);
    }

    public double[] histogram(java.lang.String in0) throws java.rmi.RemoteException {
        return dm.histogram(in0);
    }

    public double correlations(java.lang.String in0, int in1) throws java.rmi.RemoteException {
        return dm.correlations(in0, in1);
    }

}
