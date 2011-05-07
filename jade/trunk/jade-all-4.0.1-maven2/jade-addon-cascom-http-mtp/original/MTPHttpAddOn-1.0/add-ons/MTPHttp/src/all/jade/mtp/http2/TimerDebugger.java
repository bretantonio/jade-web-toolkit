/*
 * TimerDebugger.java
 *
 * Created on 25. huhtikuuta 2007, 9:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jade.mtp.http2;


/**
 *
 * @author syreeah1
 */
public class TimerDebugger extends java.util.TimerTask {
    FipaHttpObject ro;
    String message;
    public TimerDebugger(FipaHttpObject fho, String message){
        this.message = message;
    }
    public void run() {
        if(message != null){
            System.err.println(message+" [end "+System.currentTimeMillis()+"]");
        }
        if(ro != null){
            java.util.Enumeration em = ro.getHeaderNames();
            while(em.hasMoreElements()){
                System.err.println((String)em.nextElement());
            }
        }
    }
    
    
    public static void printError(FipaHttpObject fho, Exception e, String message){
        if(message != null){
            System.err.println(message+" [end "+System.currentTimeMillis()+"]");
        }
        if(e != null){
            e.printStackTrace();
        }
        if(fho != null){
            java.util.Enumeration em = fho.getHeaderNames();
            while(em.hasMoreElements()){
                System.err.println((String)em.nextElement());
            }
        }
    }
    
}

