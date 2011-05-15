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

import org.apache.ws.security.WSConstants;

/**
 * Security configuration class of a DynamicClient. For details see the WSDC user guide.
 *  
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class SecurityProperties {

	public final static String PASSWORD_TEXT = WSConstants.PASSWORD_TEXT; 
	public final static String PASSWORD_DIGEST = WSConstants.PASSWORD_DIGEST;
	public final static String PW_TEXT = WSConstants.PW_TEXT;
	public final static String PW_DIGEST = WSConstants.PW_DIGEST;
	
	private String httpUsername;
	private String httpPassword;
	private String WSSUsername;
	private String WSSPassword;
	private String WSSPasswordType;
	private Boolean WSSMustUnderstand;
	private Integer WSSTimeToLive;

	/**
	 * Create a SecurityProperties
	 */
	public SecurityProperties() {
		WSSPasswordType = PW_TEXT;
	}
	
	/**
	 * Get the current username for HTTP Basic Authentication
	 *  
	 * @return http username
	 */
	public String getHttpUsername() {
		return httpUsername;
	}
	
	/**
	 * Set the current username for HTTP Basic Authentication
	 * 
	 * @param httpUsername value of http username
	 */
	public void setHttpUsername(String httpUsername) {
		this.httpUsername = httpUsername;
	}

	/**
	 * Get the current password for HTTP Basic Authentication
	 *  
	 * @return http password
	 */
	public String getHttpPassword() {
		return httpPassword;
	}
	
	/**
	 * Set the current password for HTTP Basic Authentication
	 * 
	 * @param httpPassword value of http password
	 */
	public void setHttpPassword(String httpPassword) {
		this.httpPassword = httpPassword;
	}
	
	/**
	 * Get the current username for WS Security specifications - UsernameToken profile
	 *  
	 * @return wss username
	 */
	public String getWSSUsername() {
		return WSSUsername;
	}
	
	/**
	 * Set the current username for WS Security specifications - UsernameToken profile
	 * 
	 * @param WSSUsername value of wss username
	 */
	public void setWSSUsername(String WSSUsername) {
		this.WSSUsername = WSSUsername;
	}

	/**
	 * Get the current password for WS Security specifications - UsernameToken profile
	 *  
	 * @return wss password
	 */
	public String getWSSPassword() {
		return WSSPassword;
	}
	
	/**
	 * Set the current password for WS Security specifications - UsernameToken profile
	 * 
	 * @param WSSPassword value of wss password
	 */
	public void setWSSPassword(String WSSPassword) {
		this.WSSPassword = WSSPassword;
	}

	/**
	 * Get the current password type for WS Security specifications - UsernameToken profile
	 *  
	 * @return wss password type
	 */
	public String getWSSPasswordType() {
		return WSSPasswordType;
	}
	
	/**
	 * Set the current password type for WS Security specifications - UsernameToken profile
	 * 
	 * @param WSSPassword type value of wss password
	 */
	public void setWSSPasswordType(String WSSPasswordType) {
		this.WSSPasswordType = WSSPasswordType;
	}

	/**
	 * Get mustUnderstand flag for WS Security specifications - UsernameToken profile
	 * 
	 * @return mustUnderstand flag
	 */
	public Boolean isWSSMustUnderstand() {
		return WSSMustUnderstand;
	}

	/**
	 * Set mustUnderstand flag for WS Security specifications - UsernameToken profile
	 * 
	 * @param wSSMustUnderstand mustUnderstand flag
	 */
	public void setWSSMustUnderstand(boolean WSSMustUnderstand) {
		this.WSSMustUnderstand = Boolean.valueOf(WSSMustUnderstand);
	}

	/**
	 * Get LifeTime in seconds for WS Security specifications - Timestamp
	 * 
	 * @return LifeTime in seconds
	 */
	public Integer getWSSTimeToLive() {
		return WSSTimeToLive;
	}

	/**
	 * Set LifeTime in seconds for WS Security specifications - Timestamp
	 * 
	 * @param wSSLifeTime in seconds
	 */
	public void setWSSTimeToLive(int WSSTimeToLive) {
		this.WSSTimeToLive = Integer.valueOf(WSSTimeToLive);
	}
}
