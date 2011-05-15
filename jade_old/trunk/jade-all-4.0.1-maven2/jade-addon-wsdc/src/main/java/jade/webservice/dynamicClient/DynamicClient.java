/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
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
package jade.webservice.dynamicClient;

import jade.content.abs.AbsObject;
import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.util.Logger;
import jade.webservice.utils.CompilerUtils;
import jade.webservice.utils.FileUtils;
import jade.webservice.utils.SSLUtils;
import jade.webservice.utils.WSDLUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.xml.rpc.holders.Holder;

import org.apache.axis.AxisProperties;
import org.apache.axis.Handler;
import org.apache.axis.SimpleChain;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.SimpleSessionHandler;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo.Entry;
import org.apache.ws.axis.security.WSDoAllReceiver;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.axis.security.handler.WSDoAllHandler;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The DynamicClient allows to make calls to web-services without 
 * create stub, SOAP messages or generate java classes 
 * but simply providing the url of the WSDL (Web Service Description Language)
 * and (where necessary) using abstract descriptors (AbsObject) to managing 
 * the complex parameters.<p>
 * See the guide for more details.
 * <p>
 * This an example of use:
 * <code>
 * // Get an instance of DynamicClient<br>
 * DynamicClient dc = new DynamicClient();<br>
 * <br>
 * // Initialize DynamicClient for MathFunctions webservice by file<br>
 * dc.initClient(new URI("file:./MathFunctions.wsdl"));<br>
 * <br>
 * // Example of invocation of an operation (sum) with primitive input/output parameters only<br>
 * WSData input = new WSData();<br>
 * input.setParameter("firstElement", 5);<br>
 * input.setParameter("secondElement", 3);<br>
 * // Invoke the sum operation<br>
 * WSData output = dc.invoke("sum", input);<br>
 * float sum = output.getParameterFloat("sumReturn");<br>
 * </code>
 */
public class DynamicClient {

	/**
	 * W3C-ISO8601 date format used in conversion from String to Date
	 */
	public static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	/**
	 * State of DynamicClient.<br>
	 * Possible values are:
	 * <li> CREATED: DynamicClient created but not already initialized 
	 * <li> INITIALIZED: DynamicClient initialized and ready to use
	 * <li> INIT_FAILED: Some errors occurred during initialization, re-init it
	 */
	public enum State {CREATED, INITIALIZED, INIT_FAILED}

	private static Logger logger = Logger.getMyLogger(DynamicClient.class.getName());;

	private URL defaultEndpoint;
	private String defaultServiceName;
	private String defaultPortName;
	private int defaultTimeout;
	private String defaultHttpUsername;
	private String defaultHttpPassword;
	private String defaultWSSUsername;
	private String defaultWSSPassword;
	private String defaultWSSPasswordType;
	private Boolean defaultWSSMustUnderstand;
	private Integer defaultWSSTimeToLive;

	private DynamicClientProperties properties;
	private ClassLoader classloader;
	private String documentation;
	private State state;
	private DynamicClientException initializationException;
	private BeanOntology typeOnto;
	private Map<String, ServiceInfo> servicesInfo = new HashMap<String, ServiceInfo>();
	
	private StringBuilder sbReport;

	/**
	 * Create a new DynamicClient
	 */
	public DynamicClient() {
		typeOnto = new BeanOntology("WSDL-TYPES", new Ontology[]{XsdPrimitivesOntology.getInstance(), BasicOntology.getInstance()});

		state = State.CREATED;
		classloader = Thread.currentThread().getContextClassLoader();
		defaultTimeout = -1;
		properties = new DynamicClientProperties();
	}

	/**
	 * Get the current DynamicClient configuration properties
	 *  
	 * @return configuration properties
	 * 
	 * @see jade.webservice.dynamicClient.DynamicClientProperties
	 */
	public DynamicClientProperties getProperties() {
		return properties;
	}

	/**
	 * Set the configuration properties of DynamicClient
	 * 
	 * @param properties configuration properties 
	 * 
	 * @see jade.webservice.dynamicClient.DynamicClientProperties
	 */
	public void setProperties(DynamicClientProperties properties) {
		this.properties = properties;
	}

	/**
	 * Get the current default value of wsdl endpoint.
	 * Null if not set.
	 * 
	 * @return default wsdl endpoint
	 */
	public URL getDefaultEndpoint() {
		return defaultEndpoint;
	}

	/**
	 * Set the default url of wsdl endpoint.
	 * 
	 * @param defaultEndpoint url of default endpoint
	 */
	public void setDefaultEndpoint(URL defaultEndpoint) {
		this.defaultEndpoint = defaultEndpoint;
	}

	/**
	 * Get the current default value of wsdl service.
	 * Null if not set.
	 * 
	 * @return default wsdl service
	 */
	public String getDefaultService() {
		return defaultServiceName;
	}

	/**
	 * Set the default name of wsdl service.
	 * 
	 * @param defaultServiceName name of default service
	 */
	public void setDefaultService(String defaultServiceName) {
		this.defaultServiceName = defaultServiceName;
	}

	/**
	 * Get the current default value of wsdl port.
	 * Null if not set.
	 * 
	 * @return default wsdl port
	 */
	public String getDefaultPort() {
		return defaultPortName;
	}
	
	/**
	 * Set the default value of wsdl port.
	 * Null if not set.
	 *  
	 * @param defaultPortName name of default port
	 */
	public void setDefaultPort(String defaultPortName) {
		this.defaultPortName = defaultPortName;
	}

	/**
	 * Get the current default value for timeout call.
	 * Value in millisecond, 0=no timeout, <0=not set.
	 * 
	 * @return default timeout
	 */
	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	/**
	 * Set the default value for timeout call.
	 * Value in millisecond, 0=no timeout
	 * 
	 * @param defaultTimeout value of default timeout
	 */
	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	/**
	 * Get the current default username for HTTP Basic Authentication
	 *  
	 * @return default http username
	 */
	public String getDefaultHttpUsername() {
		return defaultHttpUsername;
	}
	
	/**
	 * Set the current default username for HTTP Basic Authentication
	 * 
	 * @param defaultHttpUsername value of default http username
	 */
	public void setDefaultHttpUsername(String defaultHttpUsername) {
		this.defaultHttpUsername = defaultHttpUsername;
	}

	/**
	 * Get the current default password for HTTP Basic Authentication
	 *  
	 * @return default http password
	 */
	public String getDefaultHttpPassword() {
		return defaultHttpPassword;
	}
	
	/**
	 * Set the current default password for HTTP Basic Authentication
	 * 
	 * @param defaultHttpPassword value of default http password
	 */
	public void setDefaultHttpPassword(String defaultHttpPassword) {
		this.defaultHttpPassword = defaultHttpPassword;
	}
	
	/**
	 * Get the current default username for WS Security specifications - UsernameToken profile
	 *  
	 * @return default wss username
	 */
	public String getDefaultWSSUsername() {
		return defaultWSSUsername;
	}
	
	/**
	 * Set the current default username for WS Security specifications - UsernameToken profile
	 * 
	 * @param defaultHttpUsername value of default wss username
	 */
	public void setDefaultWSSUsername(String defaultWSSUsername) {
		this.defaultWSSUsername = defaultWSSUsername;
	}

	/**
	 * Get the current default password for WS Security specifications - UsernameToken profile
	 *  
	 * @return default wss password
	 */
	public String getDefaultWSSPassword() {
		return defaultWSSPassword;
	}
	
	/**
	 * Set the current default password for WS Security specifications - UsernameToken profile
	 * 
	 * @param defaultWSSPassword value of default wss password
	 */
	public void setDefaultWSSPassword(String defaultWSSPassword) {
		this.defaultWSSPassword = defaultWSSPassword;
	}

	/**
	 * Get the current default password type for WS Security specifications - UsernameToken profile
	 * @see jade.webservice.dynamicClient.SecurityProperties
	 *  
	 * @return default wss password type
	 */
	public String getDefaultWSSPasswordType() {
		return defaultWSSPasswordType;
	}
	
	/**
	 * Set the current default password type for WS Security specifications - UsernameToken profile
	 * @see jade.webservice.dynamicClient.SecurityProperties
	 * 
	 * @param defaultWSSPasswordType value of default wss password
	 */
	public void setDefaultWSSPasswordType(String defaultWSSPasswordType) {
		this.defaultWSSPasswordType = defaultWSSPasswordType;
	}
	
	/**
	 * Get mustUnderstand flag for WS Security specifications - UsernameToken profile
	 * @see jade.webservice.dynamicClient.SecurityProperties
	 * 
	 * @return mustUnderstand flag
	 */
	public Boolean isDefaultWSSMustUnderstand() {
		return defaultWSSMustUnderstand;
	}

	/**
	 * Set mustUnderstand flag for WS Security specifications - UsernameToken profile
	 * @see jade.webservice.dynamicClient.SecurityProperties
	 * 
	 * @param wSSMustUnderstand mustUnderstand flag
	 */
	public void setDefaultWSSMustUnderstand(boolean defaultWSSMustUnderstand) {
		this.defaultWSSMustUnderstand = Boolean.valueOf(defaultWSSMustUnderstand);
	}
		
	/**
	 * Get defaultWSSTimeToLive value in second for WS Security specifications - Timestamp
	 * @see jade.webservice.dynamicClient.SecurityProperties
	 * 
	 * @return defaultWSSTimeToLive value
	 */
	public Integer getDefaultWSSTimeToLive() {
		return defaultWSSTimeToLive;
	}

	/**
	 * Set defaultWSSTimeToLive value in second for WS Security specifications - Timestamp
	 * @see jade.webservice.dynamicClient.SecurityProperties
	 * 
	 * @param defaultWSSTimeToLive defaultWSSTimeToLive value
	 */
	public void setDefaultWSSTimeToLive(int defaultWSSTimeToLive) {
		this.defaultWSSTimeToLive = Integer.valueOf(defaultWSSTimeToLive);
	}
	
	/**
	 * Set the file of the trust-store
	 * 
	 * @param trustStore trust-store file
	 */
	public static void setTrustStore(String trustStore) {
		SSLUtils.setTrustStore(trustStore);
	}

	/**
	 * Set the password of the trust-store
	 * 
	 * @param trustStorePassword password of trust-store
	 */
	public static void setTrustStorePassword(String trustStorePassword) {
		SSLUtils.setTrustStorePassword(trustStorePassword);
	}

	/**
	 * Disable the checking of security certificate 
	 */
	public static void disableCertificateChecking() {
		// Set trust socket used in soap invoke
		AxisProperties.setProperty("axis.socketSecureFactory", "org.apache.axis.components.net.SunFakeTrustSocketFactory");
		
		// Set trust socket used in download wsdl 
		SSLUtils.trustAll();
	}

	/**
	 * Enable the checking of security certificate 
	 */
	public static void enableCertificateChecking() {
		// Reset trust socket used in soap invoke
		AxisProperties.setProperty("axis.socketSecureFactory", "");
		
		// Reset trust socket used in download wsdl
		SSLUtils.resetTrustAll();
	}

	/**
	 * Set the host of proxy
	 * 
	 * @param proxyHost proxy host
	 */
	public static void setProxyHost(String proxyHost) {
		AxisProperties.setProperty("http.proxyHost", proxyHost);
	}

	/**
	 * Set the port of proxy
	 * 
	 * @param proxyPort proxy port
	 */
	public static void setProxyPort(String proxyPort) {
		AxisProperties.setProperty("http.proxyPort", proxyPort);
	}

	/**
	 * Set the list of host excluded from proxy.
	 * Use <code>|</code> to separate hosts.
	 * Permitted <code>*</code> as wildcards. 
	 * 
	 * @param nonProxyHosts list of hosts
	 */
	public static void setNonProxyHosts(String nonProxyHosts) {
		AxisProperties.setProperty("http.nonProxyHosts", nonProxyHosts);
	}
	
	/**
	 * Set proxy authentication credentials 
	 * 
	 * @param proxyUser authentication proxy user
	 * @param proxyPassword authentication proxy password
	 */
	public static void setProxyAuthentication(final String proxyUser, final String proxyPassword) {
	    Authenticator.setDefault(new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	          return new
	             PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
	      }});
	}
	
	/**
	 * Get the current state of DynamicClient
	 * <p>
	 * Possible values are:
	 * <li> CREATED: DynamicClient created but not already initialized 
	 * <li> INITIALIZED: DynamicClient initialized and ready to use
	 * <li> INIT_FAILED: Some errors occurred during initialization, re-init it
	 * 
	 * @return current state of DynamicClient 
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Return the wsdl documentation associated at <code>definition</code> tag.
	 * <p>
	 * <code>
	 *  &lt;wsdl:definition name="WsdlName"&gt;<br>
	 *		&lt;wsdl:documentation&gt;service documentation&lt;/wsdl:documentation&gt;<br>
	 *	&lt;/wsdl:service&gt;<br>
	 * </code>
	 *   
	 * @return wsdl documentation
	 */
	public String getDocumentation() {
		return documentation;
	}
	
	DynamicClientException getInitializationExceptionException() {
		return initializationException;
	}
	
	/**
	 * Initialize the DynamicClient with the specified wsdl.<br>
	 * Only after this operation is possible do web-service call.<br>
	 * This operation may take a long time.
	 * 
	 * @param wsdlUri uri (file or url) of wsdl
	 * @throws DynamicClientException
	 */
	public void initClient(URI wsdlUri) throws DynamicClientException {
		initClient(wsdlUri, defaultHttpUsername, defaultHttpPassword);
	}
	
	/**
	 * Initialize the DynamicClient with the specified wsdl.<br>
	 * Only after this operation is possible do web-service call.<br>
	 * This operation may take a long time.
	 * 
	 * @param wsdlUri uri (file or url) of wsdl
	 * @param username http username authentication to access wsdl  
	 * @param password http password authentication to access wsdl
	 * @throws DynamicClientException
	 */
	public void initClient(URI wsdlUri, String username, String password) throws DynamicClientException {
		boolean localNoWrap = properties.isNoWrap();
		Exception compilerException;
		try {
			compilerException = internalInitClient(wsdlUri, username, password, localNoWrap);
			if (compilerException != null && properties.isSafeMode() && !localNoWrap) {
				localNoWrap = true;
				compilerException = internalInitClient(wsdlUri, username, password, localNoWrap);
			}
		} catch(DynamicClientException dce) {
			state = State.INIT_FAILED;
			initializationException = dce;
			logger.log(Logger.WARNING, "Error discovering "+wsdlUri, dce);
			throw dce;
		}
		if (compilerException != null) {
			state = State.INIT_FAILED;
			initializationException = new DynamicClientException("Error compiling wsdl-java source files", compilerException);
			logger.log(Logger.WARNING, "Error discovering "+wsdlUri, initializationException);
			throw initializationException;
		}
		
		logger.info("Wsdl "+wsdlUri+" discovered, dynamic-client ready to invoke");
		state = State.INITIALIZED;
	}
	
	private Exception internalInitClient(URI wsdlUri, String username, String password, boolean noWrap) throws DynamicClientException {

		File src = null;
		File classes = null;
		try{
			if (wsdlUri == null) {
				throw new DynamicClientException("Wsdl uri not specified");
			}
			
			resetReport();
			addToReport("-- Dynamic Client Report --");
			addToReport("Wsdl:     "+wsdlUri);
			addToReport("Username: "+(username!=null?username:""));
			addToReport("Password: "+(password!=null?password:""));
			addToReport("Wrapped:  "+(!noWrap));
			
			logger.log(Logger.FINE, "Tmp-dir:  "+properties.getTmpDir());
			
			// reset default service/port/endpoint
			defaultServiceName = null;
			defaultPortName = null;
			defaultEndpoint = null;
			
			// Init Axis emitter
			Emitter emitter = new Emitter();
			emitter.setAllWanted(true);
			emitter.setNowrap(noWrap);
			emitter.setPackageName(properties.getPackageName());
			emitter.setBobMode(true);
			emitter.setAllowInvalidURL(true);
			if (username != null) {
				emitter.setUsername(username);
			}
			if (password != null) {
				emitter.setPassword(password);
			}
	
			// Prepare folders 
			String stem = "DynamicClient-" + System.currentTimeMillis();
			src = new File(properties.getTmpDir(), stem + "-src");
			if (!src.mkdir()) {
				throw new DynamicClientException("Unable to create working directory " + src.getAbsolutePath());
			}
			classes = new File(properties.getTmpDir(), stem + "-classes");
			if (!classes.mkdir()) {
				throw new IllegalStateException("Unable to create working directory " + src.getPath());
			}
	
			// Generate webservice classes
			try {
				emitter.setOutputDir(src.getAbsolutePath());
				emitter.run(wsdlUri.toString());
			} catch (SocketException se) {
				throw new DynamicClientException("Wsdl " +wsdlUri.toString()+ " unreachable" ,se);
			} catch (Exception e) {
				throw new DynamicClientException("Error parsing wsdl " +wsdlUri.toString()+ " Cause: " + e.getMessage(), e);
			}
			
			// Prapare classpath
			StringBuilder classPath = properties.getClassPath(); 
			if(classPath == null) {
				classPath = new StringBuilder();
    			try {
    				CompilerUtils.setupClasspath(classPath, classloader);
    			} catch (Exception e) {
    				throw new DynamicClientException("Unable to create compiler classpath", e);
    			}
			}
	
			// Compile files
			List<File> srcFiles = FileUtils.getFilesRecurse(src, ".+\\.java$"); 
			try {
				CompilerUtils.compileJavaSrc(classPath.toString(), srcFiles, classes.toString());
			} catch (Exception e) {
				return e;
			}
			
			// Create new classloader
			URLClassLoader cl;
			try {
				cl = new URLClassLoader(new URL[] {classes.toURI().toURL()}, classloader);
			} catch (MalformedURLException e) {
				throw new DynamicClientException("Error creating classloader, a directory returns a malformed URL: " + e.getMessage(), e);
			}
	
			// Load generated classes and create schemas
			String className = null;
			try {
				logger.log(Logger.FINE, "Classes loaded in classloader");
				Class clazz;
				Entry fileInfo;
				GeneratedFileInfo generatedFileInfo = emitter.getGeneratedFileInfo();
				for (Object entry: generatedFileInfo.getList()) {
					fileInfo = (Entry)entry;
					
					// Load class in classloader
					className = fileInfo.className; 
					logger.log(Logger.FINE, "\t("+fileInfo.type+") "+className);
					clazz = cl.loadClass(className);
					
					// If class is of type "complexType" create the schema
					if ("complexType".equals(fileInfo.type)) {
						if (typeOnto.getSchema(clazz) == null) {
							typeOnto.add(clazz);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				throw new DynamicClientException("Error loading class "+className, e);
			} catch (OntologyException e) {
				throw new DynamicClientException("Error creating schema for class "+className, e);
			}
	
			// Set new classloader as default
			Thread.currentThread().setContextClassLoader(cl);	       
			classloader = Thread.currentThread().getContextClassLoader();
			
			// Parse wsdl and populate internal structure
			try {
				parseWsdl(emitter);
			} catch (Exception e) {
				throw new DynamicClientException("Error parsing wsdl", e);
			}
			
			// Log ontology
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(out);
			typeOnto.dump(ps);
			addToReport(out.toString());
		} 
		catch(DynamicClientException e) {
			logger.log(Logger.SEVERE, "Error discovering wsdl "+wsdlUri, e);
			throw e;
		}		
		finally {
			// Remove classes files
			if (classes != null) {
				FileUtils.removeDir(classes);
			}
			
			// Remove src files
			if (src != null) {
				FileUtils.removeDir(src);
			}
		}
		
		return null;
	}	

	/**
	 * Get the JADE ontology of current wsdl 
	 * 
	 * @return JADE ontology
	 */
	public Ontology getOntology() {
		return typeOnto;
	}
	
	/**
	 * Get the set of service names present in current wsdl
	 *  
	 * @return set of service names
	 */
	public Set<String> getServiceNames() {
		return servicesInfo.keySet();
	}

	/**
	 * Get informations about the service
	 * 
	 * @param serviceName name of service
	 * @return information about service
	 */
	public ServiceInfo getService(String serviceName) {
		if (serviceName == null && servicesInfo.values().iterator().hasNext()) {
			return servicesInfo.values().iterator().next();
		}
		
		return servicesInfo.get(serviceName);
	}
	
	private void parseWsdl(Emitter emitter) throws DynamicClientException, OntologyException, ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// Set wsdl definition documentation
		documentation = getDocumentation(emitter.getSymbolTable().getDefinition());
		
		// Manage services
		servicesInfo.clear();
		List<ServiceEntry> services = WSDLUtils.getServices(emitter);
		for (ServiceEntry serviceEntry : services) {
			String serviceName = serviceEntry.getOriginalServiceName();
			addToReport("Service: "+serviceName);
			Service locator = createLocator(serviceEntry); 
			
			ServiceInfo serviceInfo = new ServiceInfo(serviceName, locator);
			serviceInfo.setDocumentation(getDocumentation(serviceEntry.getService()));
			servicesInfo.put(serviceName, serviceInfo);

			// Manage ports
			Collection<Port> ports = WSDLUtils.getPorts(serviceEntry);
			for (Port port : ports) {
				
				String portName = port.getName();
				addToReport("Port: "+portName, 1);
				Method stubMethod = getStubMethod(emitter, port, locator);
				if (stubMethod != null) {
					PortInfo portInfo = new PortInfo(portName, stubMethod);
					portInfo.setDocumentation(getDocumentation(port));
					serviceInfo.putPort(portName, portInfo);
					
					// Manage operations
					List<BindingOperation> operations = WSDLUtils.getOperations(port);
					for (BindingOperation bindingOperation : operations) {
	
						String operationName = bindingOperation.getName();
						addToReport("Operation: "+operationName, 2);
						OperationInfo operationInfo = new OperationInfo(operationName);
						
						// Get and add operation documentation from portType and binding
						String opDoc1 = getDocumentation(bindingOperation);
						String opDoc2 = getDocumentation(bindingOperation.getOperation());
						if (opDoc1 != null || opDoc2 != null) {
							String opDoc = "";
							if (opDoc1 != null) {
								opDoc += opDoc1;
							}
							if (opDoc2 != null) {
								if (opDoc.length() > 0 && opDoc2.length() > 0) {
									opDoc += ", ";
								}
								opDoc += opDoc2;
							}
							operationInfo.setDocumentation(opDoc);	
						}

						portInfo.putOperation(operationName, operationInfo);
	
						// Manage parameters & headers
						OperationParser opParser;
						try {
							// From axis information -> create ontology types, parameters and headers list  
							opParser = new OperationParser(bindingOperation, port.getBinding(), emitter, typeOnto, classloader);
						} catch (ClassNotFoundException e) {
							throw new DynamicClientException(e);
						} 
						
						// Get parameters
						List<ParameterInfo> parameters = opParser.getParameters();
						for (ParameterInfo parameterInfo : parameters) {
							operationInfo.putParameter(parameterInfo.getName(), parameterInfo);
							
							addToReport("Parameter: "+parameterInfo, 3);
						}
						
						// Get explicit headers
						Collection<HeaderInfo> explicitHeaders = opParser.getExplicitHeaders();
						for (HeaderInfo headerInfo : explicitHeaders) {
							operationInfo.putHeader(headerInfo.getName(), headerInfo);
							
							addToReport("Explicit header: "+headerInfo, 3);
						}
						
						// Get implicit headers
						Collection<HeaderInfo> implicitHeaders = opParser.getImplicitHeaders();
						for (HeaderInfo headerInfo : implicitHeaders) {
							operationInfo.putHeader(headerInfo.getName(), headerInfo);
							
							addToReport("Implicit header: "+headerInfo, 3);
						}
						
						// Retrieve and save in operationInfo the stub method associated to operation
						Class stubClass = stubMethod.getReturnType();
						operationInfo.manageOperationStubMethod(stubClass);
					}
				}
			}
		}
	}

	/**
	 * Invoke a web-service operation using default call values.<br>
	 * This method is very useful in case of web-services with a single service/port.<br>
	 * If you want specify all call values use the complete invoke method:<br>
	 * <code>invoke(String serviceName, String portName, String operation, URL endpoint, int timeout, WSData input)</code> 
	 * 
	 * @param operation name of operation
	 * @param input WSData input parameters/headers
	 * @return WSData output parameters/headers
	 * @throws DynamicClientException client exception 
	 * @throws RemoteException server exception
	 * 
	 * @see jade.webservice.dynamicClient.DynamicClientProperties
	 * @see jade.webservice.dynamicClient.WSData
	 */
	public WSData invoke(String operation, WSData input) throws DynamicClientException, RemoteException {
		return invoke(null, null, operation, null, -1, null, input);
	}

	/**
	 * Invoke a web-service operation.
	 * 
	 * @param serviceName name of service (null to use the default) 
	 * @param portName name of port (null to use the default)
	 * @param operation name of operation
	 * @param endpoint webservice endpoint url
	 * @param timeout call timeout in millisecond (0 no timeout, <0 to use default value)
	 * @param input WSData input parameters/headers
	 * @return WSData output parameters/headers
	 * @throws DynamicClientException client exception 
	 * @throws RemoteException server exception
	 * 
	 * @see jade.webservice.dynamicClient.DynamicClientProperties
	 * @see jade.webservice.dynamicClient.WSData
	 */
	public WSData invoke(String serviceName, String portName, String operation, URL endpoint, int timeout, WSData input) throws DynamicClientException, RemoteException {
		return invoke(serviceName, portName, operation, endpoint, timeout, null, input); 
	}
	
	/**
	 * Invoke a web-service operation.
	 * 
	 * @param serviceName name of service (null to use the default) 
	 * @param portName name of port (null to use the default)
	 * @param operation name of operation
	 * @param endpoint webservice endpoint url
	 * @param timeout call timeout in millisecond (0 no timeout, <0 to use default value)
	 * @param input WSData input parameters/headers
	 * @param securityProperties security configuration (HTTP, WSS,...)
	 * @return WSData output parameters/headers
	 * @throws DynamicClientException client exception 
	 * @throws RemoteException server exception
	 * 
	 * @see jade.webservice.dynamicClient.DynamicClientProperties
	 * @see jade.webservice.dynamicClient.WSData
	 */
	public WSData invoke(String serviceName, String portName, String operation, URL endpoint, int timeout, SecurityProperties securityProperties, WSData input) throws DynamicClientException, RemoteException {
		
		try {
			// Check if is initialized
			if (state != State.INITIALIZED) {
				throw new DynamicClientException("DynamicClient not inited, current state="+state);
			}
			
			// If not specified create a default SecurityProperties 
			if (securityProperties == null) {
				securityProperties = new SecurityProperties(); 
			}

			// Manage default values
			if (serviceName == null) {
				serviceName = defaultServiceName;
			}
			if (portName == null) {
				portName = defaultPortName;
			}
			if (endpoint == null) {
				endpoint = defaultEndpoint;
			}
			if (timeout < 0) {
				timeout = defaultTimeout;
			}
			String httpUsername = securityProperties.getHttpUsername();
			if (httpUsername == null) {
				httpUsername = defaultHttpUsername;
			}
			String httpPassword = securityProperties.getHttpPassword();
			if (httpPassword == null) {
				httpPassword = defaultHttpPassword;
			}
			String wssUsername = securityProperties.getWSSUsername();
			if (wssUsername == null) {
				wssUsername = defaultWSSUsername;
			}
			String wssPassword = securityProperties.getWSSPassword();
			if (wssPassword == null) {
				wssPassword = defaultWSSPassword;
			}
			String wssPasswordType = securityProperties.getWSSPasswordType();
			if (wssPasswordType == null) {
				wssPasswordType = defaultWSSPasswordType;
			}
			
			Boolean wssMustUnderstand = securityProperties.isWSSMustUnderstand();
			if (wssMustUnderstand == null) {
				wssMustUnderstand = defaultWSSMustUnderstand;
			}
			
			Integer wssTimeToLive = securityProperties.getWSSTimeToLive();
			if (wssTimeToLive == null) {
				wssTimeToLive = defaultWSSTimeToLive;
			}
			
			// Get and check service
			ServiceInfo serviceInfo = getService(serviceName);
			if (serviceInfo == null) {
				throw new DynamicClientException("Service "+serviceName+" not present");
			}
			
			// Get and check port
			PortInfo portInfo = serviceInfo.getPort(portName);
			if (portInfo == null) {
				throw new DynamicClientException("Port "+portName+" not present in service "+serviceInfo.getName());
			}
			
			// Get and check operation
			OperationInfo operationInfo = portInfo.getOperation(operation);
			if (operationInfo == null) {
				throw new DynamicClientException("Operation "+operation+" not present in service "+serviceInfo.getName()+", port "+portInfo.getName());
			}
			
			// Create axis stub and handlers
			Method stubMethod = portInfo.getStubMethod();
			WSDoAllSender senderHandler = new WSDoAllSender();
			WSDoAllReceiver receiverHandler = new WSDoAllReceiver();
			Stub stub;
			
			try {
				Service service = serviceInfo.getLocator(); 
				
				// Create custom client configuration
				Handler sessionHandler = (Handler)new SimpleSessionHandler(); 
				SimpleChain reqHandler = new SimpleChain(); 
				SimpleChain respHandler = new SimpleChain(); 
				reqHandler.addHandler(sessionHandler); 
				respHandler.addHandler(sessionHandler); 

				// Only for WSS security add WSS handlers
				if ((wssUsername != null && wssPassword != null) || wssTimeToLive != null) {
					// Sender handler for username-token and/or timestamp
					reqHandler.addHandler(senderHandler);
					
					// Receiver handler only for timestamp
					if (wssTimeToLive != null) {
						respHandler.addHandler(receiverHandler);
					}
				}
				
				Handler pivot = (Handler)new HTTPSender(); 
				Handler transport = new SimpleTargetedChain(reqHandler, pivot, respHandler);
				
				SimpleProvider clientConfig = new SimpleProvider();
				clientConfig.deployTransport(HTTPTransport.DEFAULT_TRANSPORT_NAME, transport); 

				service.setEngineConfiguration(clientConfig); 
				service.setEngine(new AxisClient(clientConfig)); 
				
				stub = createStub(stubMethod, service);
			} catch (Exception e) {
				throw new DynamicClientException("Error creating service stub for service "+serviceInfo.getName()+", port "+portInfo.getName());
			} 
			
			// Set webservice endpoint
			if (endpoint != null) {
				stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, endpoint.toExternalForm());
			}
			
			// Set webservice call timeout
			if (timeout >= 0) {
				stub.setTimeout(timeout);
			}

			// Set HTTP Basic Authentication
			if (httpUsername != null && httpPassword != null) {
				stub.setUsername(httpUsername);
				stub.setPassword(httpPassword);
			}

			// Set global WS-Security  
			if ((wssUsername != null && wssPassword != null) || wssTimeToLive != null) {
	            if (wssMustUnderstand != null) {
	            	stub._setProperty(WSHandlerConstants.MUST_UNDERSTAND, wssMustUnderstand.toString());
	            }
			}

			// Set WS-Security Username Token
			if (wssUsername != null && wssPassword != null) {
			
				// Add Username Token management only in sender handler
				addHandlerAction(senderHandler, WSHandlerConstants.USERNAME_TOKEN);
				stub._setProperty(UsernameToken.PASSWORD_TYPE, wssPasswordType);
				stub._setProperty(WSHandlerConstants.USER, wssUsername);
				
				WSSPasswordCallback passwordCallback = new WSSPasswordCallback(wssPassword);
				stub._setProperty(WSHandlerConstants.PW_CALLBACK_REF, passwordCallback);
			}
			
			// Set WS-Security Timestamp
            if (wssTimeToLive != null) {
            	
            	// Add timestamp management in sender & receiver handlers
            	addHandlerAction(senderHandler, WSHandlerConstants.TIMESTAMP);
            	addHandlerAction(receiverHandler, WSHandlerConstants.TIMESTAMP);
            	
            	stub._setProperty(WSHandlerConstants.TTL_TIMESTAMP, wssTimeToLive.toString());
            }
            
            logger.info("Invoke "+serviceInfo.getName()+"->"+portInfo.getName()+"->"+operationInfo.getName());
            logger.info("Input\n"+input);
			
			// Get axis-stub method parameters (mix of params & headers) 
			Vector<ParameterInfo> methodParams = operationInfo.getStubMethodParameters();
			
			// Create vector of axis-stub parameters object
			Object[] methodValuesObj = new Object[methodParams.size()]; 
			
			// Loop all method parameters to create array of values
			for (int index=0; index<methodParams.size(); index++) {
				ParameterInfo methodParam = methodParams.get(index);
				
				// Get abs values from input
				AbsObject methodParamAbs = null;
				if (input != null) {
					if (methodParam instanceof HeaderInfo) {
						methodParamAbs = input.getHeader(methodParam.getName());
					} else {
						methodParamAbs = input.getParameter(methodParam.getName());
					}
				}
				
				// Convert abs into object
				Object methodParamValue = convertAbsToObj(methodParam, methodParamAbs);
				
				// Add method parameter value to array
				methodValuesObj[index] = methodParamValue;
			}
			
			// Loop for all explicit headers and set it in the call
			if (input != null) {
				java.util.Iterator<String> ith = operationInfo.getInputHeaderNames().iterator();
				while(ith.hasNext()) {
					HeaderInfo hi = operationInfo.getInputHeader(ith.next());
					String headerName = hi.getName();
					int signaturePosition = hi.getSignaturePosition();
		
					// If header explicit
					if (signaturePosition == HeaderInfo.EXPLICIT_HEADER) {
						
						// If exist header value -> set it in call
						AbsObject headerAbs = input.getHeader(headerName);
						if (headerAbs != null) {
							stub.setHeader(hi.getNamespace(), headerName, convertAbsToObj(hi, headerAbs));
						}
					}
				}
			}
			
			// Invoke operation with stub method
			Object returnValue = null;
			Method operationMethod = operationInfo.getOperationMethod();			
			try {
				returnValue = operationMethod.invoke(stub, methodValuesObj);
			} catch (InvocationTargetException ie) {
				if (ie.getCause() instanceof RemoteException) {
					throw (RemoteException)ie.getCause();
				} else {
					throw new DynamicClientException("Error invoking operation "+operation+", service "+serviceInfo.getName()+", port "+portInfo.getName(), ie.getCause());
				}
			} catch (Exception e) {
				throw new DynamicClientException("Error invoking operation "+operation+", service "+serviceInfo.getName()+", port "+portInfo.getName(), e);
			}
	
			// Prepare results
			WSData output = new WSData();
	
			// Read explicit headers from webservice call
			java.util.Iterator<String>ith = operationInfo.getOutputHeaderNames().iterator();
			while(ith.hasNext()) {
				HeaderInfo hi = operationInfo.getOutputHeader(ith.next());
				String headerName = hi.getName();
				int signaturePosition = hi.getSignaturePosition();
				
				// If header is explicit
				if (signaturePosition == HeaderInfo.EXPLICIT_HEADER) {
			
					// Get response header value
					AbsObject headerAbs = getHeaderAbsValue(stub, hi);
					if (headerAbs != null) {
						// Insert output values into headers map
						output.setHeader(headerName, headerAbs);
					}
				}
			}
				
			// Loop all method parameters to read method output values
			for (int index=0; index<methodParams.size(); index++) {
				ParameterInfo methodParam = methodParams.get(index);
				String paramName = methodParam.getName();
				int paramMode = methodParam.getMode();
	
				// Elaborate only output params
				if (paramMode == ParameterInfo.OUT ||
					paramMode == ParameterInfo.INOUT) {
	
					// Get holder value
					Holder paramHolderValue = (Holder)methodValuesObj[index];
	
					// Convert holder value in real value
					Class paramValueClass = JavaUtils.getHolderValueType(paramHolderValue.getClass());
					Object methodParamValue = JavaUtils.convert(paramHolderValue, paramValueClass);
					
					// Convert object in relative abs
					AbsObject absValue = convertObjToAbs(methodParam, methodParamValue);
					
					// Set methodParam in actual headers or params
					if (methodParam instanceof HeaderInfo) {
						output.setHeader(paramName, absValue);
					} else {
						output.setParameter(paramName, absValue);
					}
				}			
			}		
			
			// Set return value if present
			ParameterInfo returnParameter = operationInfo.getStubMethodReturnParameter();
			if (returnParameter != null) {
				AbsObject absValue = convertObjToAbs(returnParameter, returnValue);
				output.setParameter(returnParameter.getName(), absValue);
			}
				
			logger.info("Output\n"+output);
			
			return output;
			
		} catch(DynamicClientException e) {
			logger.log(Logger.WARNING, "Error invoking operation "+operation, e);
			throw e;
		}
	}

	private static void addHandlerAction(WSDoAllHandler handler, String action) {
    	String prevAction = (String)handler.getOption(WSHandlerConstants.ACTION);
    	if (prevAction != null) {
    		action = prevAction + " " + action;
    	}
    	
    	handler.setOption(WSHandlerConstants.ACTION, action);
	}
	
	private String getDocumentation(Element documentationElement) {
		String documentation = null;
		if (documentationElement != null) {
	        Node child = documentationElement.getFirstChild();
	        if (child != null) {
	        	documentation = child.getNodeValue();
	        }
		}
        return documentation;
	}
	
	private String getDocumentation(Definition definition) {
		if (definition == null) {
			return null;
		}
		return getDocumentation(definition.getDocumentationElement());
	}

	private String getDocumentation(Port port) {
		if (port == null) {
			return null;
		}
		return getDocumentation(port.getDocumentationElement());
	}

	private String getDocumentation(BindingOperation bindingOperation) {
		if (bindingOperation == null) {
			return null;
		}
		return getDocumentation(bindingOperation.getDocumentationElement());
	}
	
	private String getDocumentation(Operation operation) {
		if (operation == null) {
			return null;
		}
		return getDocumentation(operation.getDocumentationElement());
	}
	
	private String getDocumentation(javax.wsdl.Service service) {
		if (service == null) {
			return null;
		}
		return getDocumentation(service.getDocumentationElement());
	}
	
	private Service createLocator(ServiceEntry axisService) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String locatorClassName = axisService.getName() + "Locator";
		Class locatorClass = classloader.loadClass(locatorClassName);
		return (Service)locatorClass.newInstance();
	}
	
	private Method getStubMethod(Emitter emitter, Port axisPort, Service locator) {
		Method stubMethod = null;
		String portNameJavaId = WSDLUtils.buildPortNameJavaId(emitter.getSymbolTable(), axisPort);
		try {
			stubMethod = locator.getClass().getMethod("get"+portNameJavaId, new Class[0]);
		} catch(Exception e) {
			logger.log(Logger.WARNING, "Port "+portNameJavaId+" not found in locator");
		}
		return stubMethod;
	}

	private Stub createStub(Method stubMethod, Service locator) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return (Stub)stubMethod.invoke(locator, new Object[0]);
	}
	
	private Object convertAbsToObj(ParameterInfo pi, AbsObject abs) throws DynamicClientException {
		Object value;
		if (pi.getMode() == ParameterInfo.OUT) {
			// Required output value -> create holder object 
			try {
				value = pi.getTypeClass().newInstance();
			} catch (Exception e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error creating instance of "+pi.getTypeClass());
			}
		} else if (pi.getMode() == ParameterInfo.INOUT && pi.getPrimitiveTypeClass() != null) { 
			// Parameter is a INOUT primitive -> create holder object and set value 
			try {
				// Convert abs with ontology
				Object holderValue = typeOnto.toObject(abs);
				holderValue = BasicOntology.adjustPrimitiveValue(holderValue, pi.getPrimitiveTypeClass());
				
				// Create holder object
				value = pi.getTypeClass().newInstance();
				JavaUtils.setHolderValue(value, holderValue);
			} catch (Exception e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error creating instance of "+pi.getTypeClass());
			}
		} else {
			// Assigned input or in-out (not primitive) value
			try {
				// Convert abs with ontology
				value = typeOnto.toObject(abs);
				value = BasicOntology.adjustPrimitiveValue(value, pi.getTypeClass());
				
				// Check if the parameter is a Calendar class
				if (Calendar.class.isAssignableFrom(pi.getTypeClass())) {

					// Convert from String (W3C-ISO8601)to Date
					if (value instanceof String) {
						value = ISO8601_DATE_FORMAT.parse((String)value);
					}
					
					// Convert from Date to Calendar
					if (value instanceof Date) {
						Calendar calendar = new GregorianCalendar();
						calendar.setTime((Date)value);
						value = calendar;
					}
				}
				
				// Check if value is a jade list and parameter a java array
				// (Gli aggregati sono gestiti da Axis come array, mentre il BOB 
				// li gestisce come liste -> conversione implicita)
				else if (value instanceof jade.util.leap.List &&
					pi.getTypeClass().isArray()) {
					
					// Convert the jade list in java Array
					jade.util.leap.List jadeList = (jade.util.leap.ArrayList)value;
					Object javaArray = Array.newInstance(pi.getTypeClass().getComponentType(), jadeList.size());
					for (int i = 0; i < jadeList.size(); i++) {
						Array.set(javaArray, i, jadeList.get(i));
					}
					value = javaArray;
				}

			} catch (Exception e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error converting from abs "+abs, e);
			}
		}
		
		return value;
	}
	
	private AbsObject convertObjToAbs(ParameterInfo pi, Object value) throws DynamicClientException {
		AbsObject absObject = null;
		if (value != null) {
			try {
				
				// Check if value is a Calendar object and the parameter schema a DATE
				if (value instanceof Calendar &&
					pi.getSchema().getTypeName().equals(BasicOntology.DATE)) {
					
					// Convert the Calendar object into Date object
					value = ((Calendar) value).getTime();
				}
				
				// Check if value is a java Array object and the parameter schema a SEQUENCE
				else if (value.getClass().isArray() &&
					pi.getSchema().getTypeName().equals(BasicOntology.SEQUENCE)) {
					
					// Convert the java Array object into jade list object
					jade.util.leap.List jadeList = new jade.util.leap.ArrayList();
					for (int i = 0; i < Array.getLength(value) ; i++) {
						jadeList.add(Array.get(value, i));
					}
					value = jadeList;
				}
				
				// Convert value with ontology
				absObject = typeOnto.fromObject(value);
				
			} catch (OntologyException e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error converting to abs "+value, e);
			}
		}
		return absObject;
	}
	
	private AbsObject getHeaderAbsValue(Stub stub, HeaderInfo hi) throws DynamicClientException {

		AbsObject absObject = null;
		String name = hi.getName();
		
		// Try with namespace
		SOAPHeaderElement header = stub.getResponseHeader(hi.getNamespace(), name);
		if (header == null) {
			// Try without namespace
			header = stub.getResponseHeader(null, name);
		}
		Object headerValue = null;
		if (header != null) {
			// Get value
			try {
				headerValue = header.getObjectValue(hi.getTypeClass());
			} catch (Exception e) {
				throw new DynamicClientException("Header "+name+" error getting value");
			}

			// Convert object in relative abs
			absObject = convertObjToAbs(hi, headerValue);
		}
		return absObject;
	}

	private void resetReport() {
		sbReport = new StringBuilder();
	}
	
	private void addToReport(String line) {
		addToReport(line, 0);
	}
	
	private void addToReport(String line, int tabs) {
		StringBuilder sbLine = new StringBuilder(); 
		for(int i = 0; i < tabs; i++) {
			sbLine.append("\t");
		}
		sbLine.append(line);
		sbReport.append(sbLine);
		sbReport.append("\n");
		
		logger.log(Logger.FINE, sbLine.toString());
	}

	/**
	 * Get the dynamic-client report of discovered wsdl 
	 */
	public String getReport() {
		if (state == State.INITIALIZED) {
			return sbReport.toString();
		} else {
			return "Dynamic client not yet initialized";
		}
	}
	
	
	// Inner class to manage WS-Security Username token
	private class WSSPasswordCallback implements CallbackHandler {
		
		private String password;

		public WSSPasswordCallback(String password) {
			this.password = password;
		}
		
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof WSPasswordCallback) {
					WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
					pc.setPassword(password);
				} else {
					throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
				}
			}
		}
	}
}
