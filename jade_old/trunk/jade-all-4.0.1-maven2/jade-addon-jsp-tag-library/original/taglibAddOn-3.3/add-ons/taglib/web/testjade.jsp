<%@ page import="jade.core.*" %>
<jsp:useBean id="oldsnooper" class="examples.jsp.Snooper" scope="application">
<% try {
    // Does not work for the moment
    // JADE 1.4 String [] args = {"-platform", "buffer:examples.jsp.Buffer"};
    String [] args = {"-container"};
    jade.Boot.main(args);
    System.out.println("Jade Inited()");
    System.out.println("Start");
    oldsnooper.doStart("oldsnooper");
   } catch (Exception ex) {
       out.println(ex);
   }
 %>
</jsp:useBean>

<% oldsnooper.snoop(request.getRemoteHost()+" "+(new java.util.Date())+" "+request.getRequestURI()); %>
<HTML>
<BODY>
It works !!!!
</BODY>
</HTML>