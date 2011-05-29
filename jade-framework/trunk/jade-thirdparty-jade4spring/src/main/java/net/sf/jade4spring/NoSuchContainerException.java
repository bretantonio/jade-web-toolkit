/*
 * Created on Jan 12, 2006
 */
package net.sf.jade4spring;

/**
 * Exception class for use when an invalid container is being referenced.
 * @author Jaran Nilsen
 * @since 0.2
 * @version $Revision: 1.1 $
 */
public class NoSuchContainerException extends Exception {

    private String containerName;


    public NoSuchContainerException(String containerName) {

        super();

        this.containerName = containerName;
    }


    /**
     * @return Returns the containerName.
     */
    public String getContainerName() {

        return containerName;
    }

}
