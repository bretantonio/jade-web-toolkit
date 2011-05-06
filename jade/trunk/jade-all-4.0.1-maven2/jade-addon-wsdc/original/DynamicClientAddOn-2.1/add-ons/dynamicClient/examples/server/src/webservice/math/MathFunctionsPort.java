/**
 * MathFunctionsPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 26, 2008 (09:27:08 GMT) WSDL2Java emitter.
 */

package webservice.math;

public interface MathFunctionsPort extends java.rmi.Remote {

    /**
     * Operation sumComplex documentation
     */
    public webservice.math.Complex sumComplex(webservice.math.Complex firstComplexElement, webservice.math.Complex secondComplexElement) throws java.rmi.RemoteException;
    public float sum(float firstElement, float secondElement) throws java.rmi.RemoteException;
}
