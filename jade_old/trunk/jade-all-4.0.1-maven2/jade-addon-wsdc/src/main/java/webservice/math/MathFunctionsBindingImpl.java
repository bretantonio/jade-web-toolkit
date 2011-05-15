/**
 * MathFunctionsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 26, 2008 (09:27:08 GMT) WSDL2Java emitter.
 */

package webservice.math;

public class MathFunctionsBindingImpl implements webservice.math.MathFunctionsPort{
    public webservice.math.Complex sumComplex(webservice.math.Complex firstComplexElement, webservice.math.Complex secondComplexElement) throws java.rmi.RemoteException {
			Complex result = new Complex();
			result.setReal(firstComplexElement.getReal()+secondComplexElement.getReal());
			result.setImaginary(firstComplexElement.getImaginary()+secondComplexElement.getImaginary());
			System.out.println("sumcomplex("+firstComplexElement+","+secondComplexElement+") -> "+result);
			return result;
    }

    public float sum(float firstElement, float secondElement) throws java.rmi.RemoteException {
    	float result = firstElement + secondElement;
    	System.out.println("sum("+firstElement+","+secondElement+") -> "+result);
      return result;
    }

}
