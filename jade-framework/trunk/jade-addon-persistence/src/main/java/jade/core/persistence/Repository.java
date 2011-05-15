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


package jade.core.persistence;


import java.io.IOException;
import java.net.URL;
import java.util.Collections;


/**
 This class describes a data repository of the JADE Persistence Service.
 Instances of this class are persisted to create the meta-database of the
 service, and are used during the service bootstrap process.

 @author Giovanni Rimassa - FRAMeTech s.r.l.

*/
public class Repository {


    /**
      This interface models the configuration properties of a specific
      repository (such as the kind of specific DBMS and its actual location).
    */
    public static abstract class Properties {

        public abstract java.util.Properties getProperties() throws IOException;

        public void setPersistentID(Long id) {
            persistentID = id;
        }

        public Long getPersistentID() {
            return persistentID;
        }
        
        private Long persistentID;

    } // End of Properties class


    public static class StoredProperties extends Properties {

        public StoredProperties() {
        }

        public java.util.Properties getProperties() throws IOException {
            java.util.Properties result = new java.util.Properties();
            
            java.util.Set entries = myValues.entrySet();
            java.util.Iterator it = entries.iterator();
            while(it.hasNext()) {
                java.util.Map.Entry e = (java.util.Map.Entry)it.next();
                result.setProperty((String)e.getKey(), (String)e.getValue());
            }

            return result;
        }

        public void setValues(java.util.Map v) {
            myValues = v;
        }

        public java.util.Map getValues() {
            return myValues;
        }

        private java.util.Map myValues = new java.util.HashMap();

    } // End of StoredProperties class

    public static class ExternalProperties extends Properties {

        public ExternalProperties() {
        }

        public java.util.Properties getProperties() throws IOException {
            java.util.Properties result = new java.util.Properties();
            result.load(new URL(myURL).openStream());

            return result;
        }

        public void setURL(String u) {
            myURL = u;
        }

        public String getURL() {
            return myURL;
        }

        private String myURL;

    } // End of ExternalProperties class

    /** Creates a new instance of Repository */
    public Repository() {
        myMappings = Collections.EMPTY_LIST;
    }

    public void setName(String n) {
        myName = n;
    }

    public String getName() {
        return myName;
    }

    public void setProperties(Properties p) {
        myProperties = p;
    }

    public Properties getProperties() {
        return myProperties;
    }

    public void setMappings(java.util.List m) {
        myMappings = m;
    }

    public java.util.List getMappings() {
        return myMappings;
    }

    private String myName;
    private Properties myProperties;
    private java.util.List myMappings;

    private Long persistentID;


    public void setPersistentID(Long id) {
        persistentID = id;
    }

    public Long getPersistentID() {
        return persistentID;
    }

}
