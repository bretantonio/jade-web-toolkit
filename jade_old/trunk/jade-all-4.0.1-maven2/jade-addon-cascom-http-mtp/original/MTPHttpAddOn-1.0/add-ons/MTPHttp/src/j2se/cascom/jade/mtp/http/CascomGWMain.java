package cascom.jade.mtp.http;
import jade.mtp.http2.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.mtp.InChannel;
import jade.mtp.TransportAddress;
import jade.mtp.MTPException;
import java.net.InetAddress;
import jade.util.leap.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import jade.domain.FIPAAgentManagement.Envelope;
import java.io.IOException;

/**
 * Main class for Cascom Messaging Gateway. Commandline user interface to
 * start the messaging gateway. Usage:
 * java -classpath BEFipaMessage.jar;BEEnvelope.jar;JadeLeap.jar -jar cascomgw.jar [configfile.cfg|installaddress|-h]
 *
 * For further instructions use parameter -h to get help.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class CascomGWMain implements InChannel.Dispatcher {
    private static final String DEFAULT_ADDRESS = "http://"+Profile.getDefaultNetworkName()+":56000/acc";
    // use Specifier syntax for MTPS and envelopecodecs: full.class.name(address);full.class.name2(address,address2)
    private static final String MTPS = "jade.mtp.http2.MessageTransportProtocol";
    private static final String ACL_CODECS = "cascom.fipa.acl.BitEffACLCodec";
    // ToDo: ENVCODEC_ID should be in Profile in next versions of JADE-LEAP
    private static final String ENV_CODECS = "cascom.fipa.envelope.BitEfficientEnvelopeCodec";
    private static final String ENVCODEC_ID = jade.mtp.http2.MessageTransportProtocol.ENVCODECS;
    
    /**
     * Creates a new instance of CascomGWMain
     */
    public CascomGWMain(Properties props) {
        ProfileImpl profimpl = new ProfileImpl(props);
        TransportAddress addr = null;
        try {
            if(profimpl.getParameter(Profile.MAIN_HOST,null) != null){
                addr = new HTTPAddress(profimpl.getParameter(Profile.MAIN_HOST,null));
            } else {
                addr = new HTTPAddress("http://+"+InetAddress.getLocalHost().getHostName()+":5675/acc");
            }
        } catch (Exception e){
            System.err.println("Error: installation address not set and/or default address can not be used:"+e.getMessage());
            return;
        }
        
        MessageTransportProtocol mtp = new MessageTransportProtocol();
        
        try {
            mtp.activate(this, addr, profimpl);
            System.out.println("CASCOM Messaging Gateway activated. Type 'q' to quit.");
        } catch (MTPException e){
            System.err.println("Could not start start CASCOM Messaging Gateway:"+e.getMessage());
            return;
        }
        
        InputStream in = System.in;
        try{
            int r = in.read();
            while(r != (int)'q' && r != -1){
                r = in.read();
            }
        } catch (Exception e) { e.printStackTrace();}
        
        try {
            mtp.deactivate();
            System.out.println("CASCOM Messaging Gateway is terminated succesfully.");
        } catch (Exception e){System.err.println("Error while terminating gateway:");
        e.printStackTrace();
        }
        
    }
    
    /**
     * Dispatch the message got from MTP. In this standalone version of gateway
     * this method is dummy as gateway should never receive messages intended for
     * host of this gateway.
     */
    public void dispatchMessage(Envelope env, byte[] payload){
        System.err.println("Warning: standalone version of Gateway received message intended for the host of this gateway");
    }
    
    /**
     * Main method for starting the gateway.
     */
    public static void main(String[] args){
        
        Properties props = new Properties();
        props.setProperty(Profile.MAIN_HOST, DEFAULT_ADDRESS);
        props.setProperty(Profile.PLATFORM_ID, "none");
        
        props.setProperty(Profile.MTPS, MTPS);
        props.setProperty(ENVCODEC_ID, ENV_CODECS);
        props.setProperty(Profile.ACLCODECS, ACL_CODECS);
        props.setProperty("jade_mtp_http_timeout", "10000");
        
        if(args.length > 0){
            if(args[0].trim().startsWith("-h") || args[0].trim().startsWith("/h") ||
                    args[0].trim().startsWith("--h") || args[0].trim().equals("usage")){
                usage();
                return;
            }
            
            if(args[0].indexOf(".cfg") > -1){
                try {
                    //FileInputStream fis = new FileInputStream(args[0]);
                    props.load(args[0]);
                 
                } catch (Exception e){
                    System.err.println("Error occured when reading config file:"+e.getMessage());
                    return;
                }
            } else {
                try {
                    TransportAddress ta = new HTTPAddress(args[0]);
                    props.setProperty(Profile.MAIN_HOST, args[0]);
                } catch (Exception e){
                    System.err.println("Warning: parameter not http-address, using default address");
                }
            }
        }
        
        // use the gateway server module instead of original MTP server
        props.setProperty("jade_mtp_http_server","cascom.jade.mtp.http.CascomGatewayServer");
        props.setProperty("jade_mtp_http_host", Profile.MAIN_HOST);
        props.setProperty("jade_mtp_http_persitentConnections", "true");
        CascomGWMain main = new CascomGWMain(props);
    }
    
    /**
     * Print instructions how to use the gateway
     */
    public static void usage(){
        System.out.println("CASCOM Messaging Gateway (J2SE) usage:");
        System.out.println("java -classpath BEFipaMessage.jar;BEEnvelope.jar;JadeLeap.jar -jar cascomgw.jar [configfile.cfg|installaddress|-h]");
        System.out.println("");
        System.out.println("- configfile.cfg \tProperties file containing properties (JADE MTP)");
        System.out.println("- installaddress \tComplete http-address for gateway to be installed.");
        System.out.println("- h \tprints this usage screen");
        System.out.println("");
        System.out.println("- If no arguments is given, gateway will be started in default address");
        System.out.println("- Example 1: java -jar cascomgw.jar http://foo.bar:80//acc");
        System.out.println("- Example 2: java -jar cascomgw.jar config.cfg");
    }
}