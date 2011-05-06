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

/**
 * Descriptor of a header exposed by the web-service.
 * 
 * @see jade.webservice.dynamicClient.ParameterInfo
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class HeaderInfo extends ParameterInfo {

	static final int EXPLICIT_HEADER = -1;
	
	private int signaturePosition = EXPLICIT_HEADER;
	private String namespace;

	HeaderInfo(String headerName) {
		super(headerName);
	}

	int getSignaturePosition() {
		return signaturePosition;
	}

	void setSignaturePosition(int signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	String getNamespace() {
		return namespace;
	}

	void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
