/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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

package jade.backward;

import java.io.ObjectInputStream;
import java.io.IOException;

import jade.util.leap.HashMap;
import jade.util.leap.List;
import jade.util.leap.ArrayList;
import jade.util.leap.Map;

import jade.domain.FIPAException;

import jade.core.CaseInsensitiveString;

import jade.lang.Codec;
import jade.lang.acl.*;

import jade.onto.Frame;
import jade.onto.Ontology;
import jade.onto.OntologyException;

/**
 */

public class Agent extends jade.core.Agent {

  

  // Individual agent capabilities
  private transient Map languages = new HashMap();
  private transient Map ontologies = new HashMap();

    
  

  /**
     Adds a Content Language codec to the agent capabilities. When an
     agent wants to provide automatic support for a specific content
     language, it must use an implementation of the <code>Codec</code>
     interface for the specific content language, and add it to its
     languages table with this method.
     @param languageName The symbolic name to use for the language.
     @param translator A translator for the specific content language,
     able to translate back and forth between text strings and Frame
     objects.
     @see jade.core.Agent#deregisterLanguage(String languageName)
     @see jade.lang.Codec
  */
  public void registerLanguage(String languageName, Codec translator) {
    languages.put(new CaseInsensitiveString(languageName), translator);
  }


  /**
     Looks a content language up into the supported languages table.
     @param languageName The name of the desired content language.
     @return The translator for the given language, or
     <code>null</code> if no translator was found.
   */
  public Codec lookupLanguage(String languageName) {
    Codec result = (Codec)languages.get(new CaseInsensitiveString(languageName));
    return result;
  }

  /**
     Removes a Content Language from the agent capabilities.
     @param languageName The name of the language to remove.
     @see jade.core.Agent#registerLanguage(String languageName, Codec translator)
   */
  public void deregisterLanguage(String languageName) {
    languages.remove(new CaseInsensitiveString(languageName));
  }

  /**
     Adds an Ontology to the agent capabilities. When an agent wants
     to provide automatic support for a specific ontology, it must use
     an implementation of the <code>Ontology</code> interface for the
     specific ontology and add it to its ontologies table with this
     method.
     @param ontologyName The symbolic name to use for the ontology
     @param o An ontology object, that is able to convert back and
     forth between Frame objects and application specific Java objects
     representing concepts.
     @see jade.core.Agent#deregisterOntology(String ontologyName)
     @see jade.onto.Ontology
   */
  public void registerOntology(String ontologyName, Ontology o) {
    ontologies.put(new CaseInsensitiveString(ontologyName), o);
  }

  /**
     Looks an ontology up into the supported ontologies table.
     @param ontologyName The name of the desired ontology.
     @return The given ontology, or <code>null</code> if no such named
     ontology was found.
   */
  public Ontology lookupOntology(String ontologyName) {
    Ontology result = (Ontology)ontologies.get(new CaseInsensitiveString(ontologyName));
    return result;
  }

  /**
     Removes an Ontology from the agent capabilities.
     @param ontologyName The name of the ontology to remove.
     @see jade.core.Agent#registerOntology(String ontologyName, Ontology o)
   */
  public void deregisterOntology(String ontologyName) {
    ontologies.remove(new CaseInsensitiveString(ontologyName));
  }

  //__JADE_ONLY__BEGIN
  /**
     Builds a Java object out of an ACL message. This method uses the
     <code>:language</code> slot to select a content language and the
     <code>:ontology</code> slot to select an ontology. Then the
     <code>:content</code> slot is interpreted according to the chosen
     language and ontology, to build an object of a user defined class.
     @param msg The ACL message from which a suitable Java object will
     be built.
     @return A new list of Java objects, each object representing an element
     of the t-uple of the the message content in the
     given content language and ontology.
     @exception jade.domain.FIPAException If some problem related to
     the content language or to the ontology is detected.
     @see jade.core.Agent#registerLanguage(String languageName, Codec translator)
     @see jade.core.Agent#registerOntology(String ontologyName, Ontology o)
     @see jade.core.Agent#fillContent(ACLMessage msg, java.util.List content)
		 @deprecated This support to message-content (both <code>fillContent</code> 
		 and <code>extractContent</code>) will not 
		 be ported into the CLDC-J2ME environment. In the long-term, it will be
		 replaced with the new message-content support implemented
		 by jade.content.ContentManager. In the short-term, 
		 <ul>
		 <li> in the J2SE environment this deprecated method can 
		 temporarily continue to be used
		 <li> in the PersonalJava environment the equivalent methods 
		 <code>fillMsgContent</code> and <code>extractMsgContent</code>
		 should be instead used that use
		 jade.util.leap.List instead of java.util.List, the latter being not supported in PersonalJava 
		 </ul>
   */
  public java.util.List extractContent(ACLMessage msg) throws FIPAException {
  	ArrayList l = (ArrayList) extractMsgContent(msg);
  	return l.toList();
  }

  /**
    Fills the <code>:content</code> slot of an ACL message with the string
    representation of a t-uple of user defined ontological objects. Each 
    Java object in the given list
    is first converted into a <code>Frame</code> object according to the
    ontology present in the <code>:ontology</code> message slot, then the
    <code>Frame</code> is translated into a <code>String</code> using the codec
    for the content language indicated by the <code>:language</code> message
    slot.
    <p>
    Notice that this method works properly only if in the Ontology each
    Java class has been registered to play just one role, otherwise
    ambiguity of role playing cannot be solved automatically.
    @param msg The ACL message whose content will be filled.
    @param content A list of Java objects that will be converted into a string and
    written inti the <code>:content</code> slot. This object must be an instance
    of a class registered into the ontology named in the <code>:ontology</code>
    message slot.
    @exception jade.domain.FIPAException This exception is thrown if the
    <code>:language</code> or <code>:ontology</code> message slots contain an
    unknown name, or if some problem occurs during the various translation steps.
    @see jade.core.Agent#extractContent(ACLMessage msg)
    @see jade.core.Agent#registerLanguage(String languageName, Codec translator)
    @see jade.core.Agent#registerOntology(String ontologyName, Ontology o)
		 @deprecated This support to message-content (both <code>fillContent</code> 
		 and <code>extractContent</code>) will not 
		 be ported into the CLDC-J2ME environment. In the long-term, it will be
		 replaced with the new message-content support implemented
		 by jade.content.ContentManager. In the short-term, 
		 <ul>
		 <li> in the J2SE environment this deprecated method can 
		 temporarily continue to be used
		 <li> in the PersonalJava environment the equivalent methods 
		 <code>fillMsgContent</code> and <code>extractMsgContent</code>
		 should be instead used that use
		 jade.util.leap.List instead of java.util.List, the latter being not supported in PersonalJava 
		 </ul>
   */
  public void fillContent(ACLMessage msg, java.util.List content) throws FIPAException {
    ArrayList l = new ArrayList();
    l.fromList(content);
    fillMsgContent(msg, l);
  }
  //__JADE_ONLY__END
  	

  /**
     Builds a Java object out of an ACL message. This method uses the
     <code>:language</code> slot to select a content language and the
     <code>:ontology</code> slot to select an ontology. Then the
     <code>:content</code> slot is interpreted according to the chosen
     language and ontology, to build an object of a user defined class.
     <br>
		 <i>This support to message-content (both <code>fillContent</code> 
		 and <code>extractContent</code>) will not 
		 be ported into the CLDC-J2ME environment. In the long-term, it will be
		 replaced with the new message-content support implemented
		 by jade.content.ContentManager. In the short-term, 
		 <ul>
		 <li> in the J2SE environment this deprecated method can 
		 temporarily continue to be used
		 <li> in the PersonalJava environment the equivalent methods 
		 <code>fillMsgContent</code> and <code>extractMsgContent</code>
		 should be instead used that use
		 jade.util.leap.List instead of java.util.List, the latter being not supported in PersonalJava 
		 </ul>
		 </i>
     @param msg The ACL message from which a suitable Java object will
     be built.
     @return A new list of Java objects, each object representing an element
     of the t-uple of the the message content in the
     given content language and ontology.
     @exception jade.domain.FIPAException If some problem related to
     the content language or to the ontology is detected.
     @see jade.core.Agent#registerLanguage(String languageName, Codec translator)
     @see jade.core.Agent#registerOntology(String ontologyName, Ontology o)
     @see jade.core.Agent#fillMsgContent(ACLMessage msg, List content)
   */
  public List extractMsgContent(ACLMessage msg) throws FIPAException {
    Codec c = lookupLanguage(msg.getLanguage());
    if(c == null)
      throw new FIPAException("Unknown Content Language");
    Ontology o = lookupOntology(msg.getOntology());
    if(o == null)
      throw new FIPAException("Unknown Ontology");
    try {
      List tuple = c.decode(msg.getContent(), o);
      return o.createObject(tuple);
    }
    catch(Codec.CodecException cce) {
      cce.getNested().printStackTrace();
      throw new FIPAException("Codec error: " + cce.getMessage());
    }
    catch(OntologyException oe) {
      oe.printStackTrace();
      throw new FIPAException("Ontology error: " + oe.getMessage());
    }

  }

  /**
    Fills the <code>:content</code> slot of an ACL message with the string
    representation of a t-uple of user defined ontological objects. Each 
    Java object in the given list
    is first converted into a <code>Frame</code> object according to the
    ontology present in the <code>:ontology</code> message slot, then the
    <code>Frame</code> is translated into a <code>String</code> using the codec
    for the content language indicated by the <code>:language</code> message
    slot.
    <p>
    Notice that this method works properly only if in the Ontology each
    Java class has been registered to play just one role, otherwise
    ambiguity of role playing cannot be solved automatically.
     <br>
		 <i>This support to message-content (both <code>fillContent</code> 
		 and <code>extractContent</code>) will not 
		 be ported into the CLDC-J2ME environment. In the long-term, it will be
		 replaced with the new message-content support implemented
		 by jade.content.ContentManager. In the short-term, 
		 <ul>
		 <li> in the J2SE environment this deprecated method can 
		 temporarily continue to be used
		 <li> in the PersonalJava environment the equivalent methods 
		 <code>fillMsgContent</code> and <code>extractMsgContent</code>
		 should be instead used that use
		 jade.util.leap.List instead of java.util.List, the latter being not supported in PersonalJava 
		 </ul>
		 </i>
    @param msg The ACL message whose content will be filled.
    @param content A list of Java objects that will be converted into a string and
    written inti the <code>:content</code> slot. This object must be an instance
    of a class registered into the ontology named in the <code>:ontology</code>
    message slot.
    @exception jade.domain.FIPAException This exception is thrown if the
    <code>:language</code> or <code>:ontology</code> message slots contain an
    unknown name, or if some problem occurs during the various translation steps.
    @see jade.core.Agent#extractMsgContent(ACLMessage msg)
    @see jade.core.Agent#registerLanguage(String languageName, Codec translator)
    @see jade.core.Agent#registerOntology(String ontologyName, Ontology o)
   */
  public void fillMsgContent(ACLMessage msg, List content) throws FIPAException {
    Codec c = lookupLanguage(msg.getLanguage());
    if(c == null)
      throw new FIPAException("Unknown Content Language");
    Ontology o = lookupOntology(msg.getOntology());
    if(o == null)
      throw new FIPAException("Unknown Ontology");
    try {
      List l = new ArrayList();
      Frame f;
      for (int i=0; i<content.size(); i++) {
      	Object obj = content.get(i);
				// PATCH to deal with Location and ContainerID consistently with 
				// the new ontology support
      	/*if (o instanceof jade.domain.MobilityOntology && obj instanceof ContainerID) {
      		obj = jade.domain.MobilityOntology.BCLocation.wrap((ContainerID) obj);
      	}*/	
	f = o.createFrame(obj, o.getRoleName(obj.getClass()));
	l.add(f);
      }
      String s = c.encode(l, o);
      msg.setContent(s);
    }
    catch(OntologyException oe) {
      oe.printStackTrace();
      throw new FIPAException("Ontology error: " + oe.getMessage());
    }

  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	// super.readObject(in);
	// Restore transient fields (apart from myThread, which will be set by doStart())
	languages = new HashMap();
	ontologies = new HashMap();
  }

}
