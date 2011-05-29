/*
 * Created on Dec 28, 2005
 */
package net.sf.jade4spring;

/**
 * Exception class thrown by the JadeBean in case of errors.
 * @author Jaran Nilsen
 * @since 0.1
 * @version ${Revision}
 */
public class JadeRuntimeException extends RuntimeException {

    /**
     * Create a new instance of JadeRuntimeException.
     */
    public JadeRuntimeException() {

        super();
    }


    /**
     * Create a new instance of JadeRuntimeException.
     * @param message Exception message.
     */
    public JadeRuntimeException(String message) {

        super(message);
    }


    /**
     * Create a new instance of JadeRuntimeException.
     * @param message Exception message.
     * @param cause A Throwable causing this exception to be thrown.
     */
    public JadeRuntimeException(String message, Throwable cause) {

        super(message, cause);
    }
}
