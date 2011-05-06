/**
 * MathFunctionsService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 26, 2008 (09:27:08 GMT) WSDL2Java emitter.
 */

package webservice.math;

public interface MathFunctionsService extends javax.xml.rpc.Service {

/**
 * MathFunctions service documentation
 */
    public java.lang.String getMathFunctionsPortAddress();

    public webservice.math.MathFunctionsPort getMathFunctionsPort() throws javax.xml.rpc.ServiceException;

    public webservice.math.MathFunctionsPort getMathFunctionsPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
