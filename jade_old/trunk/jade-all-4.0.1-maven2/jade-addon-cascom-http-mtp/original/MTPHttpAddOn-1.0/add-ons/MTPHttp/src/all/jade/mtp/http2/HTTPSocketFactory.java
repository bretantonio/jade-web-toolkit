/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

The updating of this file to JADE 2.0 has been partially supported by the
IST-1999-10211 LEAP Project

This file refers to parts of the FIPA 99/00 Agent Message Transport
Implementation Copyright (C) 2000, Laboratoire d'Intelligence
Artificielle, Ecole Polytechnique Federale de Lausanne

GNU Lesser General Public License

This library is free software; you can redistribute it sand/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.mtp.http2;

import jade.core.Profile;
import jade.mtp.MTPException;
import jade.util.Logger;

import java.io.IOException;
import cascom.net.InetAddress;
import cascom.net.URL;


import cascom.net.ServerSocket;
import cascom.net.Socket;
import cascom.net.ServerSocketFactory;
import cascom.net.SocketFactory;

//import javax.net.ssl.KeyManager;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLServerSocket;
//import javax.net.ssl.TrustManager;

//import jade.mtp.http.https.*;

/**
 * Singleton class for obtaining sockets. HTTP MTP calls methods use this class
 * every time that a socket is needed. 
 * 
 * This version uses cascom.net.Socket instead of java.net.Socket in order to be 
 * compatible also with J2ME
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Ahti Syreeni - TeliaSonera
 * 
 */
public class HTTPSocketFactory {
  private static Logger logger = Logger.getMyLogger("HTTPSocketFactory");


  public static HTTPSocketFactory getInstance() {
    if (_instance == null)
      _instance = new HTTPSocketFactory();
    return _instance;
  }

  public void configure(Profile profile, HTTPAddress hta) throws Exception {
    if (hta.getProto().equals("https")) {
        if(logger.isLoggable(Logger.WARNING))
            logger.log(Logger.WARNING,"https not yet supported! Now using raw http connection");
        /*
      _usingHttps = true;
      try {
        String trustManagerClass =
          profile.getParameter(
            PREFIX + "trustManagerClass",
            "jade.mtp.http.https.NoAuthentication");

        String keyManagerClass =
          profile.getParameter(
            PREFIX + "keyManagerClass",
            "jade.mtp.http.https.KeyStoreKeyManager");

        HTTPSTrustManager tm =
          (HTTPSTrustManager)Class.forName(trustManagerClass).newInstance();
        tm.init(profile);

        HTTPSKeyManager km =
          (HTTPSKeyManager)Class.forName(keyManagerClass).newInstance();
        km.init(profile);

        if (profile
          .getParameter(PREFIX + "needClientAuth", "no")
          .equals("yes"))
          _needClientAuth = true;

        SSLContext sctx = SSLContext.getInstance("TLS");
        sctx.init(new KeyManager[] { km }, new TrustManager[] { tm }, null);

        _socketFactory = sctx.getSocketFactory();
        _serverSocketFactory = sctx.getServerSocketFactory();
      } catch (Exception e) {
        throw new MTPException("Error initializing secure conection", e);
      }
         */        
    } 
    //else {
      _socketFactory = SocketFactory.getDefault();
      _serverSocketFactory = ServerSocketFactory.getDefault();
    //}
  }

  public Socket createSocket(String host, int port, boolean timeouts) throws IOException {
    return _socketFactory.createSocket(host, port, timeouts);
  }

  public Socket createSocket(
    String host,
    int port,
    String dest,
    int outport,
    boolean timeouts)
    throws IOException {
    return _socketFactory.createSocket(host, port, dest, outport, timeouts);
  }

  public ServerSocket createServerSocket(int port) throws IOException {
    // No timeouts for server sockets
    ServerSocket ss = _serverSocketFactory.createServerSocket(port, false);
    /*
    if (_usingHttps)
       ((SSLServerSocket)ss).setNeedClientAuth(_needClientAuth);
     */
    return ss;
  }

  private HTTPSocketFactory() {
  }

  private static HTTPSocketFactory _instance;
  private static final String PREFIX = "jade_mtp_http_https_";
  private SocketFactory _socketFactory;
  private ServerSocketFactory _serverSocketFactory;
  private boolean _needClientAuth = false;
  private boolean _usingHttps = false;
}
