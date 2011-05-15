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
import jade.content.abs.AbsPrimitive;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Data container for input/output parameters/headers of DynamicCliet invocation.<br>
 * The values are expressed with <code>AbsObject</code>. For JADE primitive value 
 * (eg. String, float,...) special methods wrap and dewrap automatically the value 
 * from and to <code>AbsPrimitive</code>
 * 
 * @see jade.webservice.dynamicClient.DynamicClient
 * @see jade.content.abs.AbsObject
 */
public class WSData implements Serializable {

	private Map<String, AbsObject> parameters = new HashMap<String, AbsObject>();
	private Map<String, AbsObject> headers = new HashMap<String, AbsObject>();

	/**
	 * Tests if specific parameter is not present in this data container
	 * 
	 * @param name name of parameter
	 * @return true if specific parameter is not present in this data container, false otherwise
	 */
	public boolean isParameterEmpty(String name) {
		return !parameters.containsKey(name);
	}
	
	/**
	 * Return abstract descriptor of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return abstract descriptor of specific parameter 
	 * 
	 * @see jade.content.abs.AbsObject
	 */
	public AbsObject getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * Return primitive string of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return string value of specific parameter 
	 */
	public String getParameterString(String name) {
		return ((AbsPrimitive)getParameter(name)).getString();
	}

	/**
	 * Return primitive boolean of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return boolean value of specific parameter 
	 */
	public boolean getParameterBoolean(String name) {
		return ((AbsPrimitive)getParameter(name)).getBoolean();
	}
	
	/**
	 * Return primitive int of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return int value of specific parameter 
	 */
	public int getParameterInteger(String name) {
		return ((AbsPrimitive)getParameter(name)).getInteger();
	}
	
	/**
	 * Return primitive long of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return long value of specific parameter 
	 */
	public long getParameterLong(String name) {
		return ((AbsPrimitive)getParameter(name)).getLong();
	}
	
	/**
	 * Return primitive float of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return float value of specific parameter 
	 */
	public float getParameterFloat(String name) {
		return ((AbsPrimitive)getParameter(name)).getFloat();
	}
	
	/**
	 * Return primitive double of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return double value of specific parameter 
	 */
	public double getParameterDouble(String name) {
		return ((AbsPrimitive)getParameter(name)).getDouble();
	}
	
	/**
	 * Return primitive Data of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return Data value of specific parameter 
	 */
	public Date getParameterDate(String name) {
		return ((AbsPrimitive)getParameter(name)).getDate();
	}
	
	/**
	 * Return primitive byte-sequence of specific parameter, null if not present
	 * 
	 * @param name name of parameter
	 * @return byte[] value of specific parameter 
	 */
	public byte[] getParameterByteSequence(String name) {
		return ((AbsPrimitive)getParameter(name)).getByteSequence();
	}
	
	/**
	 * Set the abstract value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param abs abstract descriptor value
	 * 
	 * @see jade.content.abs.AbsObject
	 */
	public void setParameter(String name, AbsObject abs) {
		this.parameters.put(name, abs);
	}

	/**
	 * Set a primitive string value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value string value
	 */
	public void setParameter(String name, String value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}

	/**
	 * Set a primitive boolean value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value boolean value
	 */
	public void setParameter(String name, boolean value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive int value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value int value
	 */
	public void setParameter(String name, int value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive long value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value long value
	 */
	public void setParameter(String name, long value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive float value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value float value
	 */
	public void setParameter(String name, float value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive double value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value double value
	 */
	public void setParameter(String name, double value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive Date value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value Date value
	 */
	public void setParameter(String name, Date value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive byte-sequence value for specific parameter
	 * 
	 * @param name name of parameter
	 * @param value byte[] value
	 */
	public void setParameter(String name, byte[] value) {
		setParameter(name, AbsPrimitive.wrap(value));
	}

	/**
	 * Tests if specific header is not present in this data container
	 * 
	 * @param name name of header
	 * @return true if specific header is not present in this data container, false otherwise
	 */
	public boolean isHeaderEmpty(String name) {
		return !headers.containsKey(name);
	}

	/**
	 * Return abstract descriptor of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return abstract descriptor of specific header
	 * 
	 * @see jade.content.abs.AbsObject
	 */
	public AbsObject getHeader(String name) {
		return headers.get(name);
	}
	
	/**
	 * Return primitive string of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return string value of specific header 
	 */
	public String getHeaderString(String name) {
		return ((AbsPrimitive)getHeader(name)).getString();
	}

	/**
	 * Return primitive boolean of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return boolean value of specific header 
	 */
	public boolean getHeaderBoolean(String name) {
		return ((AbsPrimitive)getHeader(name)).getBoolean();
	}

	/**
	 * Return primitive int of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return int value of specific header 
	 */
	public int getHeaderInteger(String name) {
		return ((AbsPrimitive)getHeader(name)).getInteger();
	}
	
	/**
	 * Return primitive long of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return long value of specific header 
	 */
	public long getHeaderLong(String name) {
		return ((AbsPrimitive)getHeader(name)).getLong();
	}
	
	/**
	 * Return primitive float of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return float value of specific header 
	 */
	public float getHeaderFloat(String name) {
		return ((AbsPrimitive)getHeader(name)).getFloat();
	}
	
	/**
	 * Return primitive double of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return double value of specific header 
	 */
	public double getHeaderDouble(String name) {
		return ((AbsPrimitive)getHeader(name)).getDouble();
	}
	
	/**
	 * Return primitive Date of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return Date value of specific header 
	 */
	public Date getHeaderDate(String name) {
		return ((AbsPrimitive)getHeader(name)).getDate();
	}
	
	/**
	 * Return primitive byte-sequence of specific header, null if not present
	 * 
	 * @param name name of header
	 * @return byte[] value of specific header 
	 */
	public byte[] getHeaderByteSequence(String name) {
		return ((AbsPrimitive)getHeader(name)).getByteSequence();
	}
	
	/**
	 * Set the abstract value for specific header
	 * 
	 * @param name name of header
	 * @param abs abstract descriptor value
	 * 
	 * @see jade.content.abs.AbsObject
	 */
	public void setHeader(String name, AbsObject abs) {
		this.headers.put(name, abs);
	}
	
	/**
	 * Set a primitive string value for specific header
	 * 
	 * @param name name of header
	 * @param value string value
	 */
	public void setHeader(String name, String value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}

	/**
	 * Set a primitive boolean value for specific header
	 * 
	 * @param name name of header
	 * @param value boolean value
	 */
	public void setHeader(String name, boolean value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive int value for specific header
	 * 
	 * @param name name of header
	 * @param value int value
	 */
	public void setHeader(String name, int value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive long value for specific header
	 * 
	 * @param name name of header
	 * @param value long value
	 */
	public void setHeader(String name, long value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive float value for specific header
	 * 
	 * @param name name of header
	 * @param value float value
	 */
	public void setHeader(String name, float value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive double value for specific header
	 * 
	 * @param name name of header
	 * @param value double value
	 */
	public void setHeader(String name, double value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	/**
	 * Set a primitive Date value for specific header
	 * 
	 * @param name name of header
	 * @param value Date value
	 */
	public void setHeader(String name, Date value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}

	/**
	 * Set a primitive byte-sequence value for specific header
	 * 
	 * @param name name of header
	 * @param value byte[] value
	 */
	public void setHeader(String name, byte[] value) {
		setHeader(name, AbsPrimitive.wrap(value));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Parameters\n");
		Iterator<Entry<String, AbsObject>> itp = parameters.entrySet().iterator();
		while(itp.hasNext()){
			Entry<String, AbsObject> entry = itp.next();
			sb.append("\t"+entry.getKey()+"="+entry.getValue()+"\n");
		}

		sb.append("Headers\n");
		Iterator<Entry<String, AbsObject>> ith = headers.entrySet().iterator();
		while(ith.hasNext()){
			Entry<String, AbsObject> entry = ith.next();
			sb.append("\t"+entry.getKey()+"="+entry.getValue()+"\n");
		}
		
		return sb.toString();
	}
}
