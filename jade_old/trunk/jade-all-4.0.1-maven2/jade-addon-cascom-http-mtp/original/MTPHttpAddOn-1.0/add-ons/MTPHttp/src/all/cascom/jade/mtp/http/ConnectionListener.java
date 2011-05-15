package cascom.jade.mtp.http;

import jade.mtp.http2.*;

/**
 * ConnectionListener interface define methods for users of CascomGWConnectionManager to
 * be informed when connection state is changed.
 *
 * @author Ahti Syreeni - TeliaSonera -
 */
public interface ConnectionListener {
    /**
     * Inform the attached object that the connection is closed normally, no errors
     * occured.
     */
    public void connectionClosed();
    /**
     * Inform the attached object that the connection is closed due to error.
     */
    public void connectionClosedByError(String errorMsg);
}
