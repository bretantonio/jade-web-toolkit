package jade.webservice.dynamicClient;

import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.content.schema.AggregateSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.TermSchema;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DynamicClientShell {

	private static DynamicClient dc;
	private static ServiceInfo service;
	private static PortInfo port;
	private static OperationInfo operation;
	private static String wsdlHttpUsername;
	private static String wsdlHttpPassword;
	private static String proxyHost;
	private static String proxyPort;
	private static String proxyUsername;
	private static String proxyPassword;
	private static String trustStore;
	private static String trustStorePassword;
	private static boolean disableCertificateChecking = false;
	private static URI wsdl;
	private static String httpUsername;
	private static String httpPassword;
	private static String wssUsername;
	private static String wssPassword;
	private static String wssPasswordType;
	private static String wssTimeToLive;
	private static String wssMustUnderstand;
	private static String endpoint;
	private static String timeout;
	private static int varIndex;

	// Main 
	public static void main(String[] args) {
		
		// Create dynamic client
		dc = new DynamicClient();

		try {
			// Parse command line arguments 
			URI wsdl = parseArguments(args);
			if (wsdl != null) {
				// Init dynamic client
				dc.initClient(wsdl);

				// Show console
				writeLine();
				writeLine();
				writeLine("---------------------------");
				writeLine("-- DynamicClient Shell --");
				writeLine("---------------------------");
				
				mainMenu();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void mainMenu() {
		while(true) {
			writeLine();
			writeLine("Main menu'");
			writeLine("1) Show report");
			writeLine("2) Navigate services");
			writeLine("x) Exit");
			
			String choice = readLine("Choice? ");
			writeLine();
			if ("1".equalsIgnoreCase(choice)) {
				writeLine(dc.getReport());
			} else if ("2".equalsIgnoreCase(choice)) {
				servicesMenu();				
			} else if ("x".equalsIgnoreCase(choice)) {
				exit();
			}
		}
	}
	
	private static void servicesMenu() {
		boolean back = false;
		while(!back) {
			writeLine();
			writeLine("Services list");
			int i = 1;
			Map<Integer,String> services = new HashMap<Integer,String>();
			for (String serviceName : dc.getServiceNames()) {
				writeLine(i+") -> "+serviceName);
				services.put(i, serviceName);
				i++;
			}
			writeLine("b) Go back");
			writeLine("x) Exit");
			String choice = readLine("Choice? ");
			writeLine();
			if ("x".equalsIgnoreCase(choice)) {
				exit();
			} else if ("b".equalsIgnoreCase(choice)) {
				back = true;
			} else {
				int pos = getInt(choice);
				if (pos > 0 && pos < i) {
					service = dc.getService(services.get(pos));
					portsMenu();
				}
			}
		}
	}

	private static void portsMenu() {
		boolean back = false;
		while(!back) {
			writeLine();
			writeLine("Ports list");
			int i = 1;
			Map<Integer,String> ports = new HashMap<Integer,String>();
			for (String portName : service.getPortNames()) {
				writeLine(i+") -> "+portName);
				ports.put(i, portName);
				i++;
			}
			writeLine("b) Go back");
			writeLine("x) Exit");
			String choice = readLine("Choice? ");
			writeLine();
			if ("x".equalsIgnoreCase(choice)) {
				exit();
			} else if ("b".equalsIgnoreCase(choice)) {
				back = true;
			} else {
				int pos = getInt(choice);
				if (pos > 0 && pos < i) {
					port = service.getPort(ports.get(pos));
					operationsMenu();
				}
			}
		}
	}

	private static void operationsMenu() {
		boolean back = false;
		while(!back) {
			writeLine();
			writeLine("Operations list");
			int i = 1;
			Map<Integer,String> ops = new HashMap<Integer,String>();
			for (String opName : port.getOperationNames()) {
				writeLine(i+") -> "+opName);
				ops.put(i, opName);
				i++;
			}
			writeLine("b) Go back");
			writeLine("x) Exit");
			String choice = readLine("Choice? ");
			writeLine();
			if ("x".equalsIgnoreCase(choice)) {
				exit();
			} else if ("b".equalsIgnoreCase(choice)) {
				back = true;
			} else {
				int pos = getInt(choice);
				if (pos > 0 && pos < i) {
					operation = port.getOperation(ops.get(pos));
					operationMenu();
				}
			}
		}
	}
	
	private static void operationMenu() {
		boolean back = false;
		while(!back) {
			writeLine();
			writeLine("Operation ("+operation.getName()+") menu'");
			writeLine("1) Show input parameters");
			writeLine("2) Show output parameters");
			writeLine("3) Show java-code");
			writeLine("4) Write java-code");
			writeLine("b) Go back");
			writeLine("x) Exit");
			String choice = readLine("Choice? ");
			writeLine();
			if ("1".equalsIgnoreCase(choice)) {
				showInput();
			} else if ("2".equalsIgnoreCase(choice)) {
				showOutput();
			} else if ("3".equalsIgnoreCase(choice)) {
				try {
					showCode();
				} catch (OntologyException e) {
					e.printStackTrace();
				}
			} else if ("4".equalsIgnoreCase(choice)) {
				try {
					writeCode();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("b".equalsIgnoreCase(choice)) {
				back = true;
			} else if ("x".equalsIgnoreCase(choice)) {
				exit();
			}
		}
	}
	
	private static void showInput() {
		writeLine("Parameters:");
		for (String parameterName : operation.getInputParameterNames()) {
			ParameterInfo parameter = operation.getInputParameter(parameterName);
			writeLine("- "+parameter.toString());
		}
		if (!operation.getInputHeaderNames().isEmpty()) {
			writeLine();
			writeLine("Headers:");
			for (String headerName : operation.getInputHeaderNames()) {
				HeaderInfo header = operation.getInputHeader(headerName);
				writeLine("- "+header.toString());
			}
		}
		writeLine();
	}

	private static void showOutput() {
		writeLine("Parameters:");
		for (String parameterName : operation.getOutputParameterNames()) {
			ParameterInfo parameter = operation.getOutputParameter(parameterName);
			writeLine("- "+parameter.toString());
		}
		if (!operation.getOutputHeaderNames().isEmpty()) {
			writeLine();
			writeLine("Headers:");
			for (String headerName : operation.getOutputHeaderNames()) {
				HeaderInfo header = operation.getOutputHeader(headerName);
				writeLine("- "+header.toString());
			}
		}
		writeLine();
	}

	private static String getCode() throws OntologyException {

		varIndex = 1;
		StringBuilder sb = new StringBuilder();
		
		sb.append("import jade.content.abs.AbsConcept;\n");
		sb.append("import jade.content.abs.AbsPrimitive;\n");
		sb.append("import jade.content.abs.AbsAggregate;\n");
		sb.append("import jade.webservice.dynamicClient.DynamicClient;\n");
		sb.append("import jade.webservice.dynamicClient.SecurityProperties;\n");
		sb.append("import jade.webservice.dynamicClient.WSData;\n");
		sb.append("import java.net.URI;\n");
		sb.append("import java.net.URL;\n");
		sb.append("import java.util.Date;\n");
		sb.append("\n");
		sb.append("public class Test {\n");
		sb.append("\tpublic static void main(String[] args) {\n");
		sb.append("\t\ttry {\n");
		
		// Init dc
		sb.append("\t\t\tDynamicClient dc = new DynamicClient();\n");
		if (proxyHost != null) {
			sb.append("\t\t\tdc.setProxyHost(\""+proxyHost+"\");\n");
		}
		if (proxyPort != null) {
			sb.append("\t\t\tdc.setProxyPort(\""+proxyPort+"\");\n");
		}
		if (proxyUsername != null && proxyPassword != null) {
			sb.append("\t\t\tdc.setProxyAuthentication(\""+proxyUsername+"\", \""+proxyPassword+"\");\n");
		}
		if (trustStore != null) {
			sb.append("\t\t\tdc.setTrustStore(\""+trustStore+"\");\n");
		}
		if (trustStorePassword != null) {
			sb.append("\t\t\tdc.setTrustStorePassword(\""+trustStorePassword+"\");\n");
		}
		if (disableCertificateChecking) {
			sb.append("\t\t\tdc.disableCertificateChecking();\n");
		}
		sb.append("\n");
		
		if (wsdlHttpUsername != null && wsdlHttpPassword != null) {
			sb.append("\t\t\tdc.initClient(new URI(\""+wsdl+"\"), \""+wsdlHttpUsername+"\", \""+wsdlHttpPassword+"\");\n");
		} else {
			sb.append("\t\t\tdc.initClient(new URI(\""+wsdl+"\"));\n");
		}
		sb.append("\n");
		
		// Security
		boolean spPresent = false;
		if (httpUsername != null || httpPassword != null || wssUsername != null || wssPassword != null || 
			wssPasswordType != null || wssTimeToLive != null || wssMustUnderstand != null) {
			sb.append("\t\t\tSecurityProperties sp = new SecurityProperties();\n");
			if (httpUsername != null) {
				sb.append("\t\t\tsp.setHttpUsername(\""+httpUsername+"\");\n");
			}
			if (httpPassword != null) {
				sb.append("\t\t\tsp.setHttpPassword(\""+httpPassword+"\");\n");
			}
			if (wssUsername != null) {
				sb.append("\t\t\tsp.setWSSUsername(\""+wssUsername+"\");\n");
			}
			if (wssPassword != null) {
				sb.append("\t\t\tsp.setWSSPassword(\""+wssPassword+"\");\n");
			}
			if (wssPasswordType != null) {
				sb.append("\t\t\tsp.setWSSPasswordType(\""+wssPasswordType+"\");\n");
			}
			if (wssTimeToLive != null) {
				sb.append("\t\t\tsp.setWSSTimeToLive("+Integer.parseInt(wssTimeToLive)+");\n");
			}
			if (wssMustUnderstand != null) {
				sb.append("\t\t\tsp.setWSSMustUnderstand("+Boolean.parseBoolean(wssMustUnderstand)+");\n");
			}	
			sb.append("\n");
			spPresent = true;
		}

		sb.append("\t\t\tWSData input = new WSData();\n");
		sb.append("\n");

		// Parameters
		StringBuilder sbInput = new StringBuilder(); 
		for (String parameterName : operation.getInputParameterNames()) {
			ParameterInfo parameter = operation.getInputParameter(parameterName);

			StringBuilder sbAbs = new StringBuilder(); 
			String paramValue = getCode4Schema(parameter.getSchema(), sbAbs);
			
			if (sbAbs.length() > 0) {
				sb.append(sbAbs);
			}
			
			String opt = "";
			if (!parameter.isMandatory()) {
				opt = "  // Optional";
			}
			sbInput.append("\t\t\tinput.setParameter(\""+parameter.getName()+"\", "+paramValue+");"+opt+"\n");
		}
		if (sbInput.length() > 0) {
			sb.append(sbInput);
			sb.append("\n");
		}
		
		// Headers
		sbInput = new StringBuilder(); 
		for (String headerName : operation.getInputHeaderNames()) {
			HeaderInfo header = operation.getInputHeader(headerName);

			StringBuilder sbAbs = new StringBuilder(); 
			String headerValue = getCode4Schema(header.getSchema(), sbAbs);
			
			if (sbAbs.length() > 0) {
				sb.append(sbAbs);
			}
			sbInput.append("\t\t\tinput.setHeader(\""+header.getName()+"\", "+headerValue+");\n");
		}
		if (sbInput.length() > 0) {
			sb.append(sbInput);
			sb.append("\n");
		}
		
		// Invoke
		if (dc.getServiceNames().size() > 1 || service.getPortNames().size() >1 || spPresent || 
			endpoint != null || timeout != null) {

			String serviceName = "null";
			if (dc.getServiceNames().size() > 1) {
				serviceName = "\""+service.getName()+"\"";
			}
			String portName = "null";
			if (service.getPortNames().size() > 1) {
				portName = "\""+port.getName()+"\"";
			}
			String opName = "\""+operation.getName()+"\"";
			String urlEndpoint = "null";
			if (endpoint != null) {
				urlEndpoint = "new URL(\""+endpoint+"\")";
			}
			String timeoutValue = "0";
			if (timeout != null) {
				timeoutValue = timeout; 
			}
			String sp = "null";
			if (spPresent) {
				sp = "sp";
			}
			sb.append("\t\t\tWSData output = dc.invoke("+serviceName+", "+portName+", "+opName+", "+urlEndpoint+", "+timeoutValue+", "+sp+", input);\n");
		} else {
			sb.append("\t\t\tWSData output = dc.invoke(\""+operation.getName()+"\", input);\n");
		}
		
		sb.append("\n");
		
		sb.append("\t\t} catch(Exception e) {\n");
		sb.append("\t\t\te.printStackTrace();\n");
		sb.append("\t\t}\n");
		sb.append("\t}\n");
		sb.append("}\n");

		return sb.toString();
	}

	private static void showCode() throws OntologyException {
		writeLine();
		writeLine(getCode());
	}

	private static void writeCode() throws OntologyException, IOException {
		FileWriter outFile = new FileWriter("Test.java");
		PrintWriter out = new PrintWriter(outFile);
		out.println(getCode());
		out.close();
		
		writeLine();
		writeLine("<Test.java> successfully written");
	}
	
	private static String getCode4Schema(TermSchema schema, StringBuilder sbAbs) throws OntologyException {
		
		// Primitive
		if (schema instanceof PrimitiveSchema) {
			String value = null;
			if (schema.getTypeName() == BasicOntology.STRING) {
				value = "\"aaa\"";
			} else if (schema.getTypeName() == BasicOntology.INTEGER) {
				value = "999";
			} else if (schema.getTypeName() == BasicOntology.FLOAT) {
				value = "999.9f";
			} else if (schema.getTypeName() == BasicOntology.BOOLEAN) {
				value = "true";
			} else if (schema.getTypeName() == BasicOntology.DATE) {
				value = "new Date()";
			} else if (schema.getTypeName() == BasicOntology.BYTE_SEQUENCE) {
				value = "new byte[] {0,1}";
			}
			return value;
		}
		
		String varName = generateAbsVariable();

		// Aggregate
		if (schema instanceof AggregateSchema) {
			StringBuilder sbAgg = new StringBuilder();
			TermSchema elementsSchema = ((AggregateSchema)schema).getElementsSchema();
			String aggElem = getCode4Schema(elementsSchema, sbAgg);
			if (elementsSchema instanceof PrimitiveSchema) {
				aggElem = "AbsPrimitive.wrap("+aggElem+")";
			}
			
			if (sbAgg.length() > 0) {
				sbAbs.append(sbAgg);
			}
			sbAbs.append("\t\t\tAbsAggregate "+varName+" = new AbsAggregate(\""+schema.getTypeName()+"\");\n");
			sbAbs.append("\t\t\t"+varName+".add("+aggElem+");\n");
			sbAbs.append("\n");
			
			return varName;
		}
		
		// Concept
		StringBuilder sbCon = new StringBuilder(); 
		sbCon.append("\t\t\tAbsConcept "+varName+" = new AbsConcept(\""+schema.getTypeName()+"\");\n");
		
		for (String slotName : schema.getNames()) {
			StringBuilder sbSlot = new StringBuilder(); 
			String slotValue = getCode4Schema((TermSchema)schema.getSchema(slotName), sbSlot);
			
			String opt = "";
			if (schema.isMandatory(slotName)) {
				opt = "  // Optional";
			}
			sbCon.append("\t\t\t"+varName+".set(\""+slotName+"\", "+slotValue+");"+opt+"\n");
			if (sbSlot.length() > 0) {
				sbAbs.append(sbSlot);
			}
		}
		
		sbAbs.append(sbCon);
		sbAbs.append("\n");
		return varName;
	}
	
	private static String generateAbsVariable() {
		return "abs"+(varIndex ++); 
	}
	
	private static void exit() {
		writeLine("Goodbye!");
		writeLine();
		System.exit(0);
	}
	
	private static URI parseArguments(String[] args) throws Exception {
		// Check arguments
		if (args == null || args.length < 1) {
			printUsage();
			return null;
		}

		// Parse arguments 
		int i = 0;
		while (i < args.length) {
			if (args[i].startsWith("-")) {
				// Parse options

				if (args[i].equalsIgnoreCase("-help")) {
					printUsage();
					return null;
				}
				
				// -http-username
				if (args[i].equalsIgnoreCase("-wsdl-http-username")) {
					if (++i < args.length) {
						wsdlHttpUsername = args[i];
						dc.setDefaultHttpUsername(wsdlHttpUsername);
					}
					else {
						throw new IllegalArgumentException("No username specified after \"-wsdl-http-username\" option");
					}
				}

				// -http-password
				else if (args[i].equalsIgnoreCase("-wsdl-http-password")) {
					if (++i < args.length) {
						wsdlHttpPassword = args[i];
						dc.setDefaultHttpPassword(wsdlHttpPassword);
					}
					else {
						throw new IllegalArgumentException("No username specified after \"-wsdl-http-password\" option");
					}
				}
				
				// -proxy-host
				else if (args[i].equalsIgnoreCase("-proxy-host")) {
					if (++i < args.length) {
						proxyHost = args[i];
						dc.setProxyHost(proxyHost);
					}
					else {
						throw new IllegalArgumentException("No host specified after \"-proxy-host\" option");
					}
				}

				// -proxy-port
				else if (args[i].equalsIgnoreCase("-proxy-port")) {
					if (++i < args.length) {
						proxyPort = args[i];
						dc.setProxyPort(proxyPort);
					}
					else {
						throw new IllegalArgumentException("No port specified after \"-proxy-port\" option");
					}
				}

				// -proxy-username
				else if (args[i].equalsIgnoreCase("-proxy-username")) {
					if (++i < args.length) {
						proxyUsername = args[i];
						if (proxyUsername != null && proxyPassword != null) {
							dc.setProxyAuthentication(proxyUsername, proxyPassword);
						}
					}
					else {
						throw new IllegalArgumentException("No username specified after \"-proxy-username\" option");
					}
				}

				// -proxy-password
				else if (args[i].equalsIgnoreCase("-proxy-password")) {
					if (++i < args.length) {
						proxyPassword = args[i];
						if (proxyUsername != null && proxyPassword != null) {
							dc.setProxyAuthentication(proxyUsername, proxyPassword);
						}
					}
					else {
						throw new IllegalArgumentException("No password specified after \"-proxy-password\" option");
					}
				}
				
				// -ssl-trust-store
				else if (args[i].equalsIgnoreCase("-ssl-trust-store")) {
					if (++i < args.length) {
						trustStore = args[i];
						dc.setTrustStore(trustStore);
					}
					else {
						throw new IllegalArgumentException("No trust store specified after \"-ssl-trust-store\" option");
					}
				}
				
				// -ssl-trust-store-password
				else if (args[i].equalsIgnoreCase("-ssl-trust-store-password")) {
					if (++i < args.length) {
						trustStorePassword = args[i];
						dc.setTrustStorePassword(trustStorePassword);
					}
					else {
						throw new IllegalArgumentException("No trust store password specified after \"-ssl-trust-store-password\" option");
					}
				}

				// -ssl-disable
				else if (args[i].equalsIgnoreCase("-ssl-disable")) {
					disableCertificateChecking = true;
					dc.disableCertificateChecking();
				}
				
				// -http-username
				else if (args[i].equalsIgnoreCase("-http-username")) {
					if (++i < args.length) {
						httpUsername = args[i];
					}
					else {
						throw new IllegalArgumentException("No username specified after \"-http-username\" option");
					}
				}
				
				// -http-password
				else if (args[i].equalsIgnoreCase("-http-password")) {
					if (++i < args.length) {
						httpPassword = args[i];
					}
					else {
						throw new IllegalArgumentException("No password specified after \"-http-password\" option");
					}
				}
				
				// -wss-username
				else if (args[i].equalsIgnoreCase("-wss-username")) {
					if (++i < args.length) {
						wssUsername = args[i];
					}
					else {
						throw new IllegalArgumentException("No username specified after \"-wss-username\" option");
					}
				}
				
				// -wss-password
				else if (args[i].equalsIgnoreCase("-wss-password")) {
					if (++i < args.length) {
						wssPassword = args[i];
					}
					else {
						throw new IllegalArgumentException("No password specified after \"-wss-password\" option");
					}
				}
				
				// -wss-password-type
				else if (args[i].equalsIgnoreCase("-wss-password-type")) {
					if (++i < args.length) {
						wssPasswordType = args[i];
					}
					else {
						throw new IllegalArgumentException("No password type specified after \"-wss-password-type\" option");
					}
				}

				// -wss-time-to-live
				else if (args[i].equalsIgnoreCase("-wss-time-to-live")) {
					if (++i < args.length) {
						wssTimeToLive = args[i];
					}
					else {
						throw new IllegalArgumentException("No time to live specified after \"-wss-time-to-live\" option");
					}
				}

				// -wss-must-understand
				else if (args[i].equalsIgnoreCase("-wss-must-understand")) {
					if (++i < args.length) {
						wssMustUnderstand = args[i];
					}
					else {
						throw new IllegalArgumentException("No must understand flag specified after \"-wss-must-understand\" option");
					}
				}
			
				// -endpoint
				else if (args[i].equalsIgnoreCase("-endpoint")) {
					if (++i < args.length) {
						endpoint = args[i];
					}
					else {
						throw new IllegalArgumentException("No url of endpoint specified after \"-endpoint\" option");
					}
				}
			
				// -timeout
				else if (args[i].equalsIgnoreCase("-timeout")) {
					if (++i < args.length) {
						timeout = args[i];
					}
					else {
						throw new IllegalArgumentException("No timeout value specified after \"-timeout\" option");
					}
				}
			}
			else {
				// wsdl
				wsdl = new URI(args[i]);
			}
			
			++i;
		}
		
		return wsdl;
	}
	
	private static void printUsage() {
		System.out.println("Usage: DynamicClientShell wsdl [options]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("    -wsdl-http-username <http basic authentication username to discover wsdl>");
		System.out.println("    -wsdl-http-password <http basic authentication password to discover wsdl>");
		System.out.println("    -http-username <http basic authentication username to invoke service>");
		System.out.println("    -http-password <http basic authentication password to invoke service>");
		System.out.println("    -wss-username <WS-Security username token profile>");
		System.out.println("    -wss-password <WS-Security password token profile>");
		System.out.println("    -wss-password-type <WS-Security password type token profile> (default: PasswordText)");
		System.out.println("    -wss-must-understand <WS-Security must understand flag> (true/false)");
		System.out.println("    -wss-time-to-live <WS-Security-Timestamp, LifeTime in seconds>");
		System.out.println("    -proxy-host <proxy host>");
		System.out.println("    -proxy-port <proxy port>");
		System.out.println("    -proxy-username <proxy username>");
		System.out.println("    -proxy-password <proxy password>");
		System.out.println("    -ssl-trust-store <SSL trust store file>");
		System.out.println("    -ssl-trust-store-password <SSL trust store password>");
		System.out.println("    -ssl-disable");
		System.out.println("    -endpoint <endpoint url>");
		System.out.println("    -timeout <invokation timeout in ms> (0 = no timeout)");
		System.out.println();
	}
	
	private static String readLine(String text) {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			write(text);
			line = input.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		if (line == null || line.equals("")) {
			line = null;
		}
		return line;
	}
	
	private static int getInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (Exception e) {
			return 0;
		}
	}
	
	private static void writeLine() {
		writeLine("", 0);
	}
	
	private static void writeLine(String text) {
		writeLine(text, 0);
	}

	private static void writeLine(String text, int indentLevel) {
		if (text != null) {
			write(text, indentLevel);
			System.out.println();
		}
	}

	private static void write(String text) {
		write(text, 0);
	}
	
	private static void write(String text, int indentLevel) {
		if (text != null) {
			StringBuilder sb = new StringBuilder(); 
			for(int i = 0; i < indentLevel; i++) {
				sb.append("\t");
			}
			sb.append(text);
			System.out.print(sb.toString());
		}
	}
	
}
