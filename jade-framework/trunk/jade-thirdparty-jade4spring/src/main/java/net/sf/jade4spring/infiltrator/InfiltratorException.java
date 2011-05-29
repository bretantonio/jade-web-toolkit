/*
 * Created on May 16, 2005
 */
package net.sf.jade4spring.infiltrator;


/**
 * Exception class used by the PlatformInformationAgent.
 * 
 * @author Jaran Nilsen
 * @since 0.1
 * @version $Revision: 1.1 $
 */
public class InfiltratorException extends Exception {

    /**
     * Create a new instance of InfiltratorException.
     */
    public InfiltratorException() {

        super();
    }


    /**
     * Create a new instance of InfiltratorException.
     * @param message The exception message.
     */
    public InfiltratorException(String message) {

        super(message);
    }


    /**
     * Create a new instance of InfiltratorException.
     * @param cause The exception causing this exception to be thrown.
     */
    public InfiltratorException(Throwable cause) {

        super(cause);
        // TODO Auto-generated constructor stub
    }


    /**
     * Create a new instance of InfiltratorException.
     * 
     * @param message The exception message.
     * @param cause The exception causing this exception to be thrown.
     */
    public InfiltratorException(String message, Throwable cause) {

        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
