/*
 * Created on Jan 2, 2006
 */
package net.sf.jade4spring;

/**
 * Exception thrown by information methods on the JadeBean class.
 * @author Jaran Nilsen
 * @since 0.1
 * @version $Revision: 1.1 $
 */
public class JadeBeanException extends Exception {

    /**
     * @see net.sf.jade4spring.JadeBeanException#JadeBeanException(String,
     *      Throwable)
     */
    public JadeBeanException() {

        super();
    }


    /**
     * @see net.sf.jade4spring.JadeBeanException#JadeBeanException(String,
     *      Throwable)
     */
    public JadeBeanException(String message) {

        super(message);
    }


    /**
     * Create a new instance of JadeBeanException.
     * @param message Exception message.
     * @param cause The cause of this exception if any.
     */
    public JadeBeanException(String message, Throwable cause) {

        super(message, cause);
    }

}
